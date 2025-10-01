package org.opencdmp.filetransformer.raid.service.raidfiletransformer;

import com.fasterxml.jackson.core.JsonProcessingException;
import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.logging.LoggerService;
import org.opencdmp.commonmodels.enums.*;
import org.opencdmp.commonmodels.models.*;
import org.opencdmp.commonmodels.models.description.*;
import org.opencdmp.commonmodels.models.descriptiotemplate.FieldModel;
import org.opencdmp.commonmodels.models.descriptiotemplate.FieldSetModel;
import org.opencdmp.commonmodels.models.descriptiotemplate.fielddata.RadioBoxDataModel;
import org.opencdmp.commonmodels.models.descriptiotemplate.fielddata.SelectDataModel;
import org.opencdmp.commonmodels.models.plan.PlanBlueprintValueModel;
import org.opencdmp.commonmodels.models.plan.PlanModel;
import org.opencdmp.commonmodels.models.plan.PlanPropertiesModel;
import org.opencdmp.commonmodels.models.planblueprint.ExtraFieldModel;
import org.opencdmp.commonmodels.models.planblueprint.PlanBlueprintModel;
import org.opencdmp.commonmodels.models.planblueprint.SectionModel;
import org.opencdmp.commonmodels.models.planreference.PlanReferenceModel;
import org.opencdmp.commonmodels.models.reference.ReferenceModel;
import org.opencdmp.filetransformer.raid.enums.*;
import org.opencdmp.filetransformer.raid.model.raid.*;
import org.opencdmp.filetransformer.raid.model.raid.Date;
import org.opencdmp.filetransformer.raid.model.raid.Language;
import org.opencdmp.filetransformer.raid.service.descriptiontemplatesearcher.TemplateFieldSearcherService;
import org.opencdmp.filetransformerbase.interfaces.FileTransformerClient;
import org.opencdmp.filetransformerbase.interfaces.FileTransformerConfiguration;
import org.opencdmp.filetransformer.raid.service.json.JsonHandlingService;
import org.opencdmp.filetransformer.raid.service.storage.FileStorageService;
import org.opencdmp.filetransformerbase.models.misc.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RaidFileTransformerService implements FileTransformerClient {
    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(RaidFileTransformerService.class));

    private final FileStorageService storageService;
    private final JsonHandlingService jsonHandlingService;
    private final RaidFileTransformerServiceConfiguration configuration;
    private final TemplateFieldSearcherService templateFieldSearcherService;
    
    private final static List<PluginEntityType> FILE_TRANSFORMER_ENTITY_TYPES = List.of(PluginEntityType.Plan);

    private static final String SEMANTIC_IDENTIFIER_ID = "raid.identifier.id";
    private static final String SEMANTIC_IDENTIFIER_SCHEMA_URI = "raid.identifier.schemaUri";
    private static final String SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_ID = "raid.identifier.registrationAgency.id";
    private static final String SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_SCHEMA_URI = "raid.identifier.registrationAgency.schemaUri";
    private static final String SEMANTIC_IDENTIFIER_OWNER_ID = "raid.identifier.owner.id";
    private static final String SEMANTIC_IDENTIFIER_OWNER_SCHEMA_URI = "raid.identifier.owner.schemaUri";
    private static final String SEMANTIC_IDENTIFIER_OWNER_SERVICE_POINT = "raid.identifier.owner.servicePoint";
    private static final String SEMANTIC_IDENTIFIER_LICENSE = "raid.identifier.license";
    private static final String SEMANTIC_IDENTIFIER_VERSION = "raid.identifier.version";
    private static final String SEMANTIC_DATE_START_DATE = "raid.date.startDate";
    private static final String SEMANTIC_DATE_END_DATE = "raid.date.endDate";
    private static final String SEMANTIC_TITLE_TEXT = "raid.title.text";
    private static final String SEMANTIC_TITLE_TYPE_ID = "raid.title.type.id";
    private static final String SEMANTIC_TITLE_TYPE_SCHEMA_URI = "raid.title.type.schemaUri";
    private static final String SEMANTIC_TITLE_LANGUAGE_ID = "raid.title.language.id";
    private static final String SEMANTIC_TITLE_LANGUAGE_SCHEMA_URI = "raid.title.language.schemaUri";
    private static final String SEMANTIC_TITLE_START_DATE = "raid.title.startDate";
    private static final String SEMANTIC_TITLE_END_DATE = "raid.title.endDate";
    private static final String SEMANTIC_DESCRIPTION_TEXT = "raid.description.text";
    private static final String SEMANTIC_DESCRIPTION_TYPE_ID = "raid.description.type.id";
    private static final String SEMANTIC_DESCRIPTION_TYPE_SCHEMA_URI = "raid.description.type.schemaUri";
    private static final String SEMANTIC_DESCRIPTION_LANGUAGE_ID = "raid.description.language.id";
    private static final String SEMANTIC_DESCRIPTION_LANGUAGE_SCHEMA_URI = "raid.description.language.schemaUri";
    private static final String SEMANTIC_CONTRIBUTOR_ID = "raid.contributor.id";
    private static final String SEMANTIC_CONTRIBUTOR_SCHEMA_URI = "raid.contributor.schemaUri";
    private static final String SEMANTIC_CONTRIBUTOR_POSITION_ID = "raid.contributor.position.id";
    private static final String SEMANTIC_CONTRIBUTOR_POSITION_SCHEMA_URI = "raid.contributor.position.schemaUri";
    private static final String SEMANTIC_CONTRIBUTOR_POSITION_START_DATE = "raid.contributor.position.startDate";
    private static final String SEMANTIC_CONTRIBUTOR_POSITION_END_DATE = "raid.contributor.position.endDate";
    private static final String SEMANTIC_CONTRIBUTOR_LEADER = "raid.contributor.leader";
    private static final String SEMANTIC_CONTRIBUTOR_CONTACT = "raid.contributor.contact";
    private static final String SEMANTIC_CONTRIBUTOR_ROLE_ID = "raid.contributor.role.id";
    private static final String SEMANTIC_CONTRIBUTOR_ROLE_SCHEMA_URI = "raid.contributor.role.schemaUri";
    private static final String SEMANTIC_ORGANISATION_ID = "raid.organisation.id";
    private static final String SEMANTIC_ORGANISATION_SCHEMA_URI = "raid.organisation.schemaUri";
    private static final String SEMANTIC_ORGANISATION_ROLE_ID = "raid.organisation.role.id";
    private static final String SEMANTIC_ORGANISATION_ROLE_SCHEMA_URI = "raid.organisation.role.schemaUri";
    private static final String SEMANTIC_ORGANISATION_ROLE_START_DATE = "raid.organisation.role.startDate";
    private static final String SEMANTIC_ORGANISATION_ROLE_END_DATE = "raid.organisation.role.endDate";
    private static final String SEMANTIC_RELATED_OBJECT_ID = "raid.relatedObject.id";
    private static final String SEMANTIC_RELATED_OBJECT_SCHEMA_URI = "raid.relatedObject.schemaUri";
    private static final String SEMANTIC_RELATED_OBJECT_TYPE_ID = "raid.relatedObject.type.id";
    private static final String SEMANTIC_RELATED_OBJECT_TYPE_SCHEMA_URI = "raid.relatedObject.type.schemaUri";
    private static final String SEMANTIC_RELATED_OBJECT_CATEGORY_ID = "raid.relatedObject.category.id";
    private static final String SEMANTIC_RELATED_OBJECT_CATEGORY_SCHEMA_URI = "raid.relatedObject.category.schemaUri";
    private static final String SEMANTIC_ALTERNATE_IDENTIFIER_ID = "raid.alternateIdentifier.id";
    private static final String SEMANTIC_ALTERNATE_IDENTIFIER_TYPE = "raid.alternateIdentifier.type";
    private static final String SEMANTIC_ALTERNATE_URL_URL = "raid.alternateUrl.url";
    private static final String SEMANTIC_RELATED_RAID_ID = "raid.relatedRaid.id";
    private static final String SEMANTIC_RELATED_RAID_TYPE_ID = "raid.relatedRaid.type.id";
    private static final String SEMANTIC_RELATED_RAID_TYPE_SCHEMA_URI = "raid.relatedRaid.type.schemaUri";
    private static final String SEMANTIC_ACCESS_TYPE_ID = "raid.access.type.id";
    private static final String SEMANTIC_ACCESS_TYPE_SCHEMA_URI = "raid.access.type.schemaUri";
    private static final String SEMANTIC_ACCESS_EMBARGO_EXPIRY = "raid.access.embargoExpiry";
    private static final String SEMANTIC_ACCESS_STATEMENT_TEXT = "raid.access.statement.text";
    private static final String SEMANTIC_ACCESS_STATEMENT_LANGUAGE_ID = "raid.access.statement.language.id";
    private static final String SEMANTIC_ACCESS_STATEMENT_LANGUAGE_SCHEMA_URI = "raid.access.statement.language.schemaUri";


    @Autowired
    public RaidFileTransformerService(FileStorageService storageService, JsonHandlingService jsonHandlingService, RaidFileTransformerServiceConfiguration configuration, TemplateFieldSearcherService templateFieldSearcherService) {
        this.storageService = storageService;
	    this.configuration = configuration;
	    this.jsonHandlingService = jsonHandlingService;
	    this.templateFieldSearcherService = templateFieldSearcherService;
    }
    @Override
    public FileEnvelopeModel exportPlan(PlanModel planFileTransformerModel, String variant)  {
        Raid raid = this.buildRaid(planFileTransformerModel);
        String raidJson = jsonHandlingService.toJsonSafe(raid);
        
        byte[] bytes = raidJson.getBytes(StandardCharsets.UTF_8);
        FileEnvelopeModel wordFile = new FileEnvelopeModel();
        if (this.getConfiguration().isUseSharedStorage()) {
            String fileRef = this.storageService.storeFile(bytes);
            wordFile.setFileRef(fileRef);
        } else {
            wordFile.setFile(bytes);
        }
        wordFile.setFilename(planFileTransformerModel.getLabel() + ".json");
        return wordFile;
    }

    @Override
    public FileEnvelopeModel exportDescription(DescriptionModel descriptionFileTransformerModel, String format) {
        throw new MyApplicationException("Not implemented");
    }
    
    //region export
    
    //region dmp

    private Raid buildRaid(PlanModel plan){
        if (plan == null) throw new MyApplicationException("Plan is missing");
        if (plan.getCreatedAt() == null) throw new MyApplicationException("Plan Created is missing");
        if (plan.getUpdatedAt() == null) throw new MyApplicationException("Plan Modified is missing");
        if (plan.getLabel() == null) throw new MyApplicationException("Plan Label is missing");
        if (plan.getProperties() == null)  throw new MyApplicationException("Plan is missing language and contact properties");

        Raid raid = new Raid();

        //default title
        Title title = new Title();
        title.setText(plan.getLabel());

        Title.Type type = new Title.Type();
        type.setId(TitleTypeId.Primary);
        type.setSchemaUri(TitleTypeSchemaUri.SchemaUri);

        title.setType(type);
        title.setStartDate((DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault()).format(plan.getCreatedAt())));
        if (plan.getFinalizedAt() != null) title.setEndDate(DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault()).format(plan.getFinalizedAt()));

        raid.setTitles(new ArrayList<>());
        raid.getTitles().add(title);

        raid.setIdentifier(this.buildRaidIdentifier(plan));
        raid.setDate(this.buildRaidDate(plan));
        raid.setAccess(this.buildRaidAccess(plan));

        if (plan.getDescriptions() != null){
            for (DescriptionModel descriptionModel : plan.getDescriptions()) {
                List<Title> titles = this.buildRaidTitle(descriptionModel);
                if (titles != null && !titles.isEmpty()) raid.getTitles().addAll(titles);
                raid.setDescriptions(this.buildRaidDescriptions(descriptionModel));
                raid.setContributors(this.buildRaidContributors(descriptionModel));
                raid.setOrganisations(this.buildRaidOrganisations(descriptionModel));
                raid.setRelatedObjects(this.buildRaidRelatedObjects(descriptionModel));
                raid.setAlternateIdentifiers(this.buildRaidAlternateIdentifiers(descriptionModel));
                raid.setAlternateUrls(this.buildRaidAlternateUrls(descriptionModel));
                raid.setRelatedRaids(this.buildRaidRelatedRaids(descriptionModel));
                raid.setDate(this.buildRaidDate(descriptionModel, raid.getDate()));
                raid.setIdentifier(this.buildRaidIdentifier(descriptionModel, raid.getIdentifier()));
                raid.setAccess(this.buildRaidAccess(descriptionModel, raid.getAccess()));
            }
        }

        return raid;
    }

    private boolean isValidDateField(String input) {
        // Check format first
        if (!input.matches("^\\d{4}(-\\d{2})?(-\\d{2})?$")) return false;

        try {
            if (input.matches("^\\d{4}$")) {
                // Just a year is fine
                return true;
            } else if (input.matches("^\\d{4}-\\d{2}$")) {
                // Validate year-month
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
                formatter.parse(input);  // will throw if invalid month
                return true;
            } else if (input.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                // Validate full date
                LocalDate.parse(input);  // validates full date including leap year
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.warn("invalid date value: " + input);
            return false;
        }
    }

    public Identifier buildRaidIdentifier(PlanModel model) {
        if (model == null) throw new MyApplicationException("Plan is missing");
        Identifier identifier = new Identifier();

        List<org.opencdmp.commonmodels.models.planblueprint.FieldModel> valueFields = this.getFieldsOfSemantic(model.getPlanBlueprint(), SEMANTIC_IDENTIFIER_ID);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : valueFields) {
            PlanBlueprintValueModel valueModel = field != null ? this.getPLanBlueprintValue(model, field.getId()) : null;
            if (valueModel != null && valueModel.getValue() != null) identifier.setId(valueModel.getValue());
        }

        valueFields = this.getFieldsOfSemantic(model.getPlanBlueprint(), SEMANTIC_IDENTIFIER_SCHEMA_URI);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : valueFields) {
            PlanBlueprintValueModel valueModel = field != null ? this.getPLanBlueprintValue(model, field.getId()) : null;
            if (valueModel != null && valueModel.getValue() != null && !valueModel.getValue().isBlank()) {
                try {
                    identifier.setSchemaUri(IdentifierSchemaUri.fromValue(valueModel.getValue()));
                } catch (Exception e) {
                    logger.warn("invalid identifier schema uri value: " + valueModel.getValue());
                }
            }
        }

        Identifier.RegistrationAgency agency = new Identifier.RegistrationAgency();

        valueFields = this.getFieldsOfSemantic(model.getPlanBlueprint(), SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_ID);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : valueFields) {
            PlanBlueprintValueModel valueModel = field != null ? this.getPLanBlueprintValue(model, field.getId()) : null;
            if (valueModel != null && valueModel.getValue() != null && !valueModel.getValue().isBlank()) agency.setId(valueModel.getValue());
        }

        valueFields = this.getFieldsOfSemantic(model.getPlanBlueprint(), SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_SCHEMA_URI);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : valueFields) {
            PlanBlueprintValueModel valueModel = field != null ? this.getPLanBlueprintValue(model, field.getId()) : null;
            if (valueModel != null && valueModel.getValue() != null && !valueModel.getValue().isBlank()) {
                try {
                    agency.setSchemaUri(IdentifierRegistrationAgencySchemaUri.fromValue(valueModel.getValue()));
                } catch (Exception e) {
                    logger.warn("invalid agency schema uri value: " + valueModel.getValue());
                }
            }
        }

        identifier.setRegistrationAgency(agency);

        Identifier.Owner owner = new Identifier.Owner();

        valueFields = this.getFieldsOfSemantic(model.getPlanBlueprint(), SEMANTIC_IDENTIFIER_OWNER_ID);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : valueFields) {
            PlanBlueprintValueModel valueModel = field != null ? this.getPLanBlueprintValue(model, field.getId()) : null;
            if (valueModel != null && valueModel.getValue() != null && !valueModel.getValue().isBlank()) owner.setId(valueModel.getValue());
        }

        valueFields = this.getFieldsOfSemantic(model.getPlanBlueprint(), SEMANTIC_IDENTIFIER_OWNER_SCHEMA_URI);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : valueFields) {
            PlanBlueprintValueModel valueModel = field != null ? this.getPLanBlueprintValue(model, field.getId()) : null;
            if (valueModel != null && valueModel.getValue() != null && !valueModel.getValue().isBlank()) {
                try {
                    owner.setSchemaUri(IdentifierRegistrationAgencySchemaUri.fromValue(valueModel.getValue()));
                } catch (Exception e) {
                    logger.warn("invalid owner schema uri value: " + valueModel.getValue());
                }
            }
        }

        valueFields = this.getFieldsOfSemantic(model.getPlanBlueprint(), SEMANTIC_IDENTIFIER_OWNER_SERVICE_POINT);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : valueFields) {
            PlanBlueprintValueModel valueModel = field != null ? this.getPLanBlueprintValue(model, field.getId()) : null;
            if (valueModel != null && valueModel.getValue() != null && !valueModel.getValue().isBlank()) owner.setServicePoint(valueModel.getValue());
        }

        identifier.setOwner(owner);

        valueFields = this.getFieldsOfSemantic(model.getPlanBlueprint(), SEMANTIC_IDENTIFIER_LICENSE);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : valueFields) {
            PlanBlueprintValueModel valueModel = field != null ? this.getPLanBlueprintValue(model, field.getId()) : null;
            if (valueModel != null && valueModel.getValue() != null && !valueModel.getValue().isBlank()) {
                try {
                    identifier.setLicense(IdentifierLicense.fromValue(valueModel.getValue()));
                } catch (Exception e) {
                    logger.warn("invalid identifier license uri value: " + valueModel.getValue());
                }
            }
        }

        valueFields = this.getFieldsOfSemantic(model.getPlanBlueprint(), SEMANTIC_IDENTIFIER_VERSION);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : valueFields) {
            PlanBlueprintValueModel valueModel = field != null ? this.getPLanBlueprintValue(model, field.getId()) : null;
            if (valueModel != null && valueModel.getValue() != null && !valueModel.getValue().isBlank()) identifier.setVersion(valueModel.getValue());
        }

        return identifier;
    }

    public Date buildRaidDate(PlanModel model) {
        if (model == null) throw new MyApplicationException("Plan is missing");
        Date date = new Date();

        date.setStartDate(LocalDateTime.ofInstant(model.getUpdatedAt(), ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE));
        date.setEndDate(LocalDateTime.ofInstant(model.getUpdatedAt(), ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE));

        List<org.opencdmp.commonmodels.models.planblueprint.FieldModel> startDateFields = this.getFieldsOfSemantic(model.getPlanBlueprint(), SEMANTIC_DATE_START_DATE);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : startDateFields) {
            PlanBlueprintValueModel valueModel = field != null ? this.getPLanBlueprintValue(model, field.getId()) : null;

            if (valueModel != null && valueModel.getDateValue() != null) {
                LocalDateTime ldt = LocalDateTime.ofInstant(valueModel.getDateValue(), ZoneOffset.UTC);
                date.setStartDate(ldt.format(DateTimeFormatter.ISO_LOCAL_DATE));
            } else if (valueModel != null && valueModel.getValue() != null && !valueModel.getValue().isBlank()) {
                if (this.isValidDateField(valueModel.getValue())) date.setStartDate(valueModel.getValue());
            }
        }

        List<org.opencdmp.commonmodels.models.planblueprint.FieldModel> endDateFields = this.getFieldsOfSemantic(model.getPlanBlueprint(), SEMANTIC_DATE_END_DATE);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : endDateFields) {
            PlanBlueprintValueModel valueModel = field != null ? this.getPLanBlueprintValue(model, field.getId()) : null;

            if (valueModel != null && valueModel.getDateValue() != null) {
                LocalDateTime ldt = LocalDateTime.ofInstant(valueModel.getDateValue(), ZoneOffset.UTC);
                date.setEndDate(ldt.format(DateTimeFormatter.ISO_LOCAL_DATE));
            } else if (valueModel != null && valueModel.getValue() != null && !valueModel.getValue().isBlank()) {
                if (this.isValidDateField(valueModel.getValue())) date.setEndDate(valueModel.getValue());
            }
        }

        return date;
    }

    public Access buildRaidAccess(PlanModel model) {
        if (model == null) throw new MyApplicationException("Plan is missing");
        Access access = new Access();

        Access.Type type = new Access.Type();

        List<org.opencdmp.commonmodels.models.planblueprint.FieldModel> valueFields = this.getFieldsOfSemantic(model.getPlanBlueprint(), SEMANTIC_ACCESS_TYPE_ID);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : valueFields) {
            PlanBlueprintValueModel valueModel = field != null ? this.getPLanBlueprintValue(model, field.getId()) : null;
            if (valueModel != null && valueModel.getValue() != null && !valueModel.getValue().isBlank()) {
                try {
                    type.setId(AccessTypeId.fromValue(valueModel.getValue()));
                } catch (Exception e) {
                    logger.warn("invalid access type id value: " + valueModel.getValue());
                }
            }
        }

        valueFields = this.getFieldsOfSemantic(model.getPlanBlueprint(), SEMANTIC_ACCESS_TYPE_SCHEMA_URI);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : valueFields) {
            PlanBlueprintValueModel valueModel = field != null ? this.getPLanBlueprintValue(model, field.getId()) : null;
            if (valueModel != null && valueModel.getValue() != null && !valueModel.getValue().isBlank()) {
                try {
                    type.setSchemaUri(AccessTypeSchemaUri.fromValue(valueModel.getValue()));
                } catch (Exception e) {
                    logger.warn("invalid access type schema uri: " + valueModel.getValue());
                }
            }
        }

        access.setType(type);

        valueFields = this.getFieldsOfSemantic(model.getPlanBlueprint(), SEMANTIC_ACCESS_EMBARGO_EXPIRY);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : valueFields) {
            PlanBlueprintValueModel valueModel = field != null ? this.getPLanBlueprintValue(model, field.getId()) : null;

            if (valueModel != null && valueModel.getDateValue() != null) {
                LocalDateTime ldt = LocalDateTime.ofInstant(valueModel.getDateValue(), ZoneOffset.UTC);
                access.setEmbargoExpiry(ldt.format(DateTimeFormatter.ISO_LOCAL_DATE));
            } else if (valueModel != null && valueModel.getValue() != null && !valueModel.getValue().isBlank()) {
                try {
                    if (valueModel.getValue().matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                        // Validate full date
                        LocalDate.parse(valueModel.getValue());
                        access.setEmbargoExpiry(valueModel.getValue());
                    }
                } catch (Exception e) {
                    logger.warn("invalid access embargoExpiry value: " + valueModel.getValue());
                }
            }
        }

        Access.Statement statement = new Access.Statement();

        valueFields = this.getFieldsOfSemantic(model.getPlanBlueprint(), SEMANTIC_ACCESS_STATEMENT_TEXT);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : valueFields) {
            PlanBlueprintValueModel valueModel = field != null ? this.getPLanBlueprintValue(model, field.getId()) : null;
            if (valueModel != null && valueModel.getValue() != null && !valueModel.getValue().isBlank()) statement.setText(valueModel.getValue());
        }

        Language language = new Language();

        valueFields = this.getFieldsOfSemantic(model.getPlanBlueprint(), SEMANTIC_ACCESS_STATEMENT_LANGUAGE_ID);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : valueFields) {
            PlanBlueprintValueModel valueModel = field != null ? this.getPLanBlueprintValue(model, field.getId()) : null;
            if (valueModel != null && valueModel.getValue() != null && !valueModel.getValue().isBlank()) {
                try {
                    language.setId(LanguageId.fromValue(valueModel.getValue()));
                } catch (Exception e) {
                    logger.warn("invalid access statement language id value: " + valueModel.getValue());
                }
            }
        }

        valueFields = this.getFieldsOfSemantic(model.getPlanBlueprint(), SEMANTIC_ACCESS_STATEMENT_LANGUAGE_SCHEMA_URI);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : valueFields) {
            PlanBlueprintValueModel valueModel = field != null ? this.getPLanBlueprintValue(model, field.getId()) : null;
            if (valueModel != null && valueModel.getValue() != null && !valueModel.getValue().isBlank()) {
                try {
                    language.setSchemaUri(LanguageSchemaUri.fromValue(valueModel.getValue()));
                } catch (Exception e) {
                    logger.warn("invalid access statement language schema uri  value: " + valueModel.getValue());
                }
            }
        }

        statement.setLanguage(language);

        access.setStatement(statement);

        return access;
    }

    private List<Title> buildRaidTitle(DescriptionModel model){
        List<Title> values = new ArrayList<>();
        for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(model.getDescriptionTemplate(), List.of(SEMANTIC_TITLE_TEXT, SEMANTIC_TITLE_TYPE_ID,
                SEMANTIC_TITLE_TYPE_SCHEMA_URI, SEMANTIC_TITLE_LANGUAGE_ID, SEMANTIC_TITLE_LANGUAGE_SCHEMA_URI, SEMANTIC_TITLE_START_DATE, SEMANTIC_TITLE_END_DATE))) {
            List<org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties());
            for (org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                boolean valueFound = false;
                Title item = new Title();

                Title.Type type = new Title.Type();
                String fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_TITLE_TYPE_ID);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        type.setId(TitleTypeId.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid title type value: " + fieldValue);
                    }
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_TITLE_TYPE_SCHEMA_URI);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        type.setSchemaUri(TitleTypeSchemaUri.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid title schema uri value: " + fieldValue);
                    }
                }

                item.setType(type);
                Language language = new Language();

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_TITLE_LANGUAGE_ID);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        language.setId(LanguageId.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid language id value: " + fieldValue);
                    }
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_TITLE_LANGUAGE_SCHEMA_URI);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        language.setSchemaUri(LanguageSchemaUri.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid language schema uri value: " + fieldValue);
                    }
                }
                item.setLanguage(language);

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_TITLE_TEXT);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    item.setText(fieldValue);
                    valueFound = true;
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_TITLE_START_DATE);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    if (this.isValidDateField(fieldValue)) {
                        item.setStartDate(fieldValue);
                        valueFound = true;
                    }
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_TITLE_END_DATE);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    if (this.isValidDateField(fieldValue)) {
                        item.setEndDate(fieldValue);
                        valueFound = true;
                    }
                }

                if (valueFound) values.add(item);
            }
        }
        return values.isEmpty() ? null : values;
    }

    private List<Description> buildRaidDescriptions(DescriptionModel model){

//        Description mainDescription = new Description();
//        mainDescription.setText(model.getLabel());
//
//        Description.Type mainDescriptionType = new Description.Type();
//        mainDescriptionType.setId(DescriptionTypeId.Primary);
//        mainDescriptionType.setSchemaUri(DescriptionTypeSchemaUri.SchemaUri);
//        mainDescription.setType(mainDescriptionType);
//
//        values.add(mainDescription);
        List<Description> values = new ArrayList<>();
        for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(model.getDescriptionTemplate(), List.of(SEMANTIC_DESCRIPTION_TEXT, SEMANTIC_DESCRIPTION_TYPE_ID, SEMANTIC_DESCRIPTION_TYPE_SCHEMA_URI
                , SEMANTIC_DESCRIPTION_LANGUAGE_ID, SEMANTIC_DESCRIPTION_LANGUAGE_SCHEMA_URI))) {
            List<org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties());
            for (org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                boolean valueFound = false;
                Description item = new Description();

                Description.Type type = new Description.Type();
                String fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_DESCRIPTION_TYPE_ID);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        type.setId(DescriptionTypeId.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid description type value: " + fieldValue);
                    }
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_DESCRIPTION_TYPE_SCHEMA_URI);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        type.setSchemaUri(DescriptionTypeSchemaUri.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid description schema uri value: " + fieldValue);
                    }
                }

                item.setType(type);

                Language language = new Language();

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_DESCRIPTION_LANGUAGE_ID);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        language.setId(LanguageId.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid language id value: " + fieldValue);
                    }
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_DESCRIPTION_LANGUAGE_SCHEMA_URI);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        language.setSchemaUri(LanguageSchemaUri.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid language schema uri value: " + fieldValue);
                    }
                }
                item.setLanguage(language);

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_DESCRIPTION_TEXT);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    item.setText(fieldValue);
                    valueFound = true;
                }

                if (valueFound) values.add(item);
            }
        }
        return values.isEmpty() ? null : values;
    }

    private List<Contributor> buildRaidContributors(DescriptionModel model){
        List<Contributor> values = new ArrayList<>();
        for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(model.getDescriptionTemplate(), List.of(SEMANTIC_CONTRIBUTOR_ID, SEMANTIC_CONTRIBUTOR_SCHEMA_URI, SEMANTIC_CONTRIBUTOR_POSITION_ID
                , SEMANTIC_CONTRIBUTOR_POSITION_SCHEMA_URI, SEMANTIC_CONTRIBUTOR_POSITION_START_DATE, SEMANTIC_CONTRIBUTOR_POSITION_END_DATE, SEMANTIC_CONTRIBUTOR_LEADER, SEMANTIC_CONTRIBUTOR_CONTACT, SEMANTIC_CONTRIBUTOR_ROLE_ID, SEMANTIC_CONTRIBUTOR_ROLE_SCHEMA_URI))) {
            List<org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties());
            for (org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                boolean valueFound = false;
                Contributor item = new Contributor();

                Contributor.Position position = new Contributor.Position();
                String fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_CONTRIBUTOR_POSITION_ID);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        position.setId(ContributorPositionId.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid contributor position id value: " + fieldValue);
                    }
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_CONTRIBUTOR_POSITION_SCHEMA_URI);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        position.setSchemaUri(ContributorPositionSchemaUri.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid contributor schema uri value: " + fieldValue);
                    }
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_CONTRIBUTOR_POSITION_START_DATE);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    if (this.isValidDateField(fieldValue)) {
                        position.setStartDate(fieldValue);
                        valueFound = true;
                    }
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_CONTRIBUTOR_POSITION_END_DATE);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    if (this.isValidDateField(fieldValue)) {
                        position.setEndDate(fieldValue);
                        valueFound = true;
                    }
                }

                item.setPosition(position);

                Contributor.Role role = new Contributor.Role();
                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_CONTRIBUTOR_ROLE_ID);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        role.setId(ContributorRoleId.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid contributor role id value: " + fieldValue);
                    }
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_CONTRIBUTOR_ROLE_SCHEMA_URI);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        role.setSchemaUri(ContributorRoleSchemaUri.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid contributor role uri value: " + fieldValue);
                    }
                }

                item.setRole(role);

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_CONTRIBUTOR_ID);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    item.setId(fieldValue);
                    valueFound = true;
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_CONTRIBUTOR_LEADER);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        if (fieldValue.equalsIgnoreCase(ContributorLeader.Yes.value()) || fieldValue.equalsIgnoreCase("true")) {
                            item.setLeader(ContributorLeader.Yes);
                            valueFound = true;
                        }
                    } catch (Exception e) {
                        logger.warn("invalid contributor leader value: " + fieldValue);
                    }
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_CONTRIBUTOR_CONTACT);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        if (fieldValue.equalsIgnoreCase(ContributorLeader.Yes.value()) || fieldValue.equalsIgnoreCase("true")) {
                            item.setContact(ContributorLeader.Yes);
                            valueFound = true;
                        }
                    } catch (Exception e) {
                        logger.warn("invalid contributor contact value: " + fieldValue);
                    }
                }

                if (valueFound) values.add(item);
            }
        }
        return values.isEmpty() ? null : values;
    }

    private Date buildRaidDate(DescriptionModel model, Date item){

        if (item == null) item = new Date();

        for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(model.getDescriptionTemplate(), List.of(SEMANTIC_DATE_START_DATE, SEMANTIC_DATE_END_DATE))) {
            List<org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties());
            for (org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {

                String fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_DATE_START_DATE);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    if (this.isValidDateField(fieldValue)) item.setStartDate(fieldValue);
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_DATE_END_DATE);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    if (this.isValidDateField(fieldValue)) item.setEndDate(fieldValue);
                }

            }
        }

        return item;
    }

    private Identifier buildRaidIdentifier(DescriptionModel model, Identifier identifier){

        if (identifier == null) identifier = new Identifier();

        for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(model.getDescriptionTemplate(), List.of(SEMANTIC_IDENTIFIER_ID, SEMANTIC_IDENTIFIER_SCHEMA_URI, SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_ID,
                SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_SCHEMA_URI, SEMANTIC_IDENTIFIER_OWNER_ID, SEMANTIC_IDENTIFIER_OWNER_SCHEMA_URI, SEMANTIC_IDENTIFIER_OWNER_SERVICE_POINT, SEMANTIC_IDENTIFIER_LICENSE, SEMANTIC_IDENTIFIER_VERSION))) {
            List<org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties());
            for (org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {

                String fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_IDENTIFIER_ID);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    identifier.setId(fieldValue);
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_IDENTIFIER_SCHEMA_URI);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        identifier.setSchemaUri(IdentifierSchemaUri.fromValue(fieldValue));
                    } catch (Exception e) {
                        logger.warn("invalid identifier schema uri value: " + fieldValue);
                    }
                }

                if (identifier.getRegistrationAgency() == null) identifier.setRegistrationAgency(new Identifier.RegistrationAgency());

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_ID);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    identifier.getRegistrationAgency().setId(fieldValue);
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_SCHEMA_URI);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        identifier.getRegistrationAgency().setSchemaUri(IdentifierRegistrationAgencySchemaUri.fromValue(fieldValue));
                    } catch (Exception e) {
                        logger.warn("invalid agency schema uri value: " + fieldValue);
                    }
                }

                if (identifier.getOwner() == null) identifier.setOwner(new Identifier.Owner());

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_IDENTIFIER_OWNER_ID);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    identifier.getOwner().setId(fieldValue);
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_IDENTIFIER_OWNER_SCHEMA_URI);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        identifier.getOwner().setSchemaUri(IdentifierRegistrationAgencySchemaUri.fromValue(fieldValue));
                    } catch (Exception e) {
                        logger.warn("invalid owner schema uri value: " + fieldValue);
                    }
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_IDENTIFIER_OWNER_SERVICE_POINT);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    identifier.getOwner().setServicePoint(fieldValue);
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_IDENTIFIER_LICENSE);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        identifier.setLicense(IdentifierLicense.fromValue(fieldValue));
                    } catch (Exception e) {
                        logger.warn("invalid identifier license uri value: " + fieldValue);
                    }
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_IDENTIFIER_VERSION);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    identifier.setVersion(fieldValue);
                }
            }
        }

        return identifier;
    }

    private Access buildRaidAccess(DescriptionModel model, Access access){

        if (access == null) access = new Access();

        for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(model.getDescriptionTemplate(), List.of(SEMANTIC_ACCESS_TYPE_ID, SEMANTIC_ACCESS_TYPE_SCHEMA_URI, SEMANTIC_ACCESS_EMBARGO_EXPIRY,
                SEMANTIC_ACCESS_STATEMENT_TEXT, SEMANTIC_ACCESS_STATEMENT_LANGUAGE_ID, SEMANTIC_ACCESS_STATEMENT_LANGUAGE_SCHEMA_URI))) {
            List<org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties());
            for (org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {

                if (access.getType() == null) access.setType(new Access.Type());

                String fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_ACCESS_TYPE_ID);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        access.getType().setId(AccessTypeId.fromValue(fieldValue));
                    } catch (Exception e) {
                        logger.warn("invalid access type id value: " + fieldValue);
                    }
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_ACCESS_TYPE_SCHEMA_URI);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        access.getType().setSchemaUri(AccessTypeSchemaUri.fromValue(fieldValue));
                    } catch (Exception e) {
                        logger.warn("invalid access type schema uri: " + fieldValue);
                    }
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_ACCESS_EMBARGO_EXPIRY);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    if (this.isValidDateField(fieldValue)) access.setEmbargoExpiry(fieldValue);
                }

                if (access.getStatement() == null) access.setStatement(new Access.Statement());

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_ACCESS_STATEMENT_TEXT);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    access.getStatement().setText(fieldValue);
                }

                if (access.getStatement().getLanguage() == null) access.getStatement().setLanguage(new Language());

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_ACCESS_STATEMENT_LANGUAGE_ID);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        access.getStatement().getLanguage().setId(LanguageId.fromValue(fieldValue));
                    } catch (Exception e) {
                        logger.warn("invalid access statement language id value: " + fieldValue);
                    }
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_ACCESS_STATEMENT_LANGUAGE_SCHEMA_URI);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        access.getStatement().getLanguage().setSchemaUri(LanguageSchemaUri.fromValue(fieldValue));
                    } catch (Exception e) {
                        logger.warn("invalid access statement language schema uri  value: " + fieldValue);
                    }
                }

            }
        }

        return access;
    }

    private List<Organisation> buildRaidOrganisations(DescriptionModel model){
        List<Organisation> values = new ArrayList<>();
        for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(model.getDescriptionTemplate(), List.of(SEMANTIC_ORGANISATION_ID, SEMANTIC_ORGANISATION_SCHEMA_URI, SEMANTIC_ORGANISATION_ROLE_ID
                , SEMANTIC_ORGANISATION_ROLE_SCHEMA_URI, SEMANTIC_ORGANISATION_ROLE_START_DATE, SEMANTIC_ORGANISATION_ROLE_END_DATE))) {
            List<org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties());
            for (org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                boolean valueFound = false;
                Organisation item = new Organisation();

                Organisation.Role role = new Organisation.Role();
                String fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_ORGANISATION_ROLE_ID);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        role.setId(OrganisationRoleId.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid organisation role id value: " + fieldValue);
                    }
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_ORGANISATION_ROLE_SCHEMA_URI);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        role.setSchemaUri(OrganisationRoleSchemaUri.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid organisation role schema uri value: " + fieldValue);
                    }
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_ORGANISATION_ROLE_START_DATE);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    if (this.isValidDateField(fieldValue)) {
                        role.setStartDate(fieldValue);
                        valueFound = true;
                    }
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_ORGANISATION_ROLE_END_DATE);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    if (this.isValidDateField(fieldValue)) {
                        role.setEndDate(fieldValue);
                        valueFound = true;
                    }
                }

                item.setRole(role);

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_ORGANISATION_ID);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    item.setId(fieldValue);
                    valueFound = true;
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_ORGANISATION_SCHEMA_URI);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        item.setSchemaUri(OrganisationSchemaUri.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid organisation schema uri value: " + fieldValue);
                    }
                }

                if (valueFound) values.add(item);
            }
        }
        return values.isEmpty() ? null : values;
    }

    private List<RelatedObject> buildRaidRelatedObjects(DescriptionModel model){
        List<RelatedObject> values = new ArrayList<>();
        for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(model.getDescriptionTemplate(), List.of(SEMANTIC_RELATED_OBJECT_ID, SEMANTIC_RELATED_OBJECT_SCHEMA_URI, SEMANTIC_RELATED_OBJECT_TYPE_ID
                , SEMANTIC_RELATED_OBJECT_TYPE_SCHEMA_URI, SEMANTIC_RELATED_OBJECT_CATEGORY_ID, SEMANTIC_RELATED_OBJECT_CATEGORY_SCHEMA_URI))) {
            List<org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties());
            for (org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                boolean valueFound = false;
                RelatedObject item = new RelatedObject();

                RelatedObject.Type type = new RelatedObject.Type();
                String fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_RELATED_OBJECT_TYPE_ID);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        type.setId(RelatedObjectTypeId.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid related object type id value: " + fieldValue);
                    }
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_RELATED_OBJECT_TYPE_SCHEMA_URI);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        type.setSchemaUri(RelatedObjectTypeSchemaUri.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid related object schema uri value: " + fieldValue);
                    }
                }

                item.setType(type);

                RelatedObject.Category category = new RelatedObject.Category();
                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_RELATED_OBJECT_CATEGORY_ID);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        category.setId(RelatedObjectCategoryId.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid related object category id value: " + fieldValue);
                    }
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_RELATED_OBJECT_CATEGORY_SCHEMA_URI);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        category.setSchemaUri(RelatedObjectCategorySchemaUri.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid related object category schema uri value: " + fieldValue);
                    }
                }

                item.setCategory(category);

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_RELATED_OBJECT_ID);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    item.setId(fieldValue);
                    valueFound = true;
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_RELATED_OBJECT_SCHEMA_URI);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        item.setSchemaUri(RelatedObjectSchemaUri.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid related object schema uri value: " + fieldValue);
                    }
                }

                if (valueFound) values.add(item);
            }
        }
        return values.isEmpty() ? null : values;
    }

    private List<AlternateIdentifier> buildRaidAlternateIdentifiers(DescriptionModel model){
        List<AlternateIdentifier> values = new ArrayList<>();
        for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(model.getDescriptionTemplate(), List.of(SEMANTIC_ALTERNATE_IDENTIFIER_ID, SEMANTIC_ALTERNATE_IDENTIFIER_TYPE))) {
            List<org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties());
            for (org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                boolean valueFound = false;
                AlternateIdentifier item = new AlternateIdentifier();

                String fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_ALTERNATE_IDENTIFIER_ID);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    item.setId(fieldValue);
                    valueFound = true;
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_ALTERNATE_IDENTIFIER_TYPE);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    item.setType(fieldValue);
                    valueFound = true;
                }

                if (valueFound) values.add(item);
            }
        }
        return values.isEmpty() ? null : values;
    }

    private List<AlternativeUrl> buildRaidAlternateUrls(DescriptionModel model){
        List<AlternativeUrl> values = new ArrayList<>();
        for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(model.getDescriptionTemplate(), List.of(SEMANTIC_ALTERNATE_URL_URL))) {
            List<org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties());
            for (org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                boolean valueFound = false;
                AlternativeUrl item = new AlternativeUrl();

                String fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_ALTERNATE_URL_URL);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        item.setUrl(URI.create(fieldValue));
                        valueFound = true;
                    } catch (Exception ex) {
                        logger.warn("invalid Alternative Url value: " + fieldValue);
                    }

                }

                if (valueFound) values.add(item);
            }
        }
        return values.isEmpty() ? null : values;
    }

    private List<RelatedRaid> buildRaidRelatedRaids(DescriptionModel model){
        List<RelatedRaid> values = new ArrayList<>();
        for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(model.getDescriptionTemplate(), List.of(SEMANTIC_RELATED_RAID_ID, SEMANTIC_RELATED_RAID_TYPE_ID, SEMANTIC_RELATED_RAID_TYPE_SCHEMA_URI))) {
            List<org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties());
            for (org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                boolean valueFound = false;
                RelatedRaid item = new RelatedRaid();

                RelatedRaid.Type type = new RelatedRaid.Type();
                String fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_RELATED_RAID_TYPE_ID);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        type.setId(RelatedRaidTypeId.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid related raid type id value: " + fieldValue);
                    }
                }

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_RELATED_RAID_TYPE_SCHEMA_URI);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    try {
                        type.setSchemaUri(RelatedRaidTypeSchemaUri.fromValue(fieldValue));
                        valueFound = true;
                    } catch (Exception e) {
                        logger.warn("invalid related raid type schema uri value: " + fieldValue);
                    }
                }

                item.setType(type);

                fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_RELATED_RAID_ID);
                if (fieldValue != null && !fieldValue.isBlank()) {
                    item.setId(fieldValue);
                    valueFound = true;
                }

                if (valueFound) values.add(item);
            }
        }
        return values.isEmpty() ? null : values;
    }


    private List<ReferenceModel> getReferenceModelOfTypeCode(PlanModel plan, String code){
        List<ReferenceModel> response = new ArrayList<>();
        if (plan.getReferences() == null) return response;
        for (PlanReferenceModel planReferenceModel : plan.getReferences()){
            if (planReferenceModel.getReference() != null && planReferenceModel.getReference().getType() != null && planReferenceModel.getReference().getType().getCode() != null  && planReferenceModel.getReference().getType().getCode().equals(code)){
                response.add(planReferenceModel.getReference());
            }
        }

        return response;
    }

    private List<org.opencdmp.commonmodels.models.planblueprint.FieldModel> getFieldsOfSemantic(PlanBlueprintModel blueprintModel, String semanticKey){
        if (blueprintModel == null || blueprintModel.getDefinition() == null || blueprintModel.getDefinition().getSections() == null) return null;
        List<org.opencdmp.commonmodels.models.planblueprint.FieldModel> fieldModels = new ArrayList<>();
        for (SectionModel sectionModel : blueprintModel.getDefinition().getSections()){
            if (sectionModel.getFields() != null){
	            sectionModel.getFields().stream().filter(x -> x.getSemantics() != null && x.getSemantics().contains(semanticKey)).findFirst().ifPresent(fieldModels::add);
            }
        }
        return fieldModels;
    }

    private PlanBlueprintValueModel getPLanBlueprintValue(PlanModel plan, UUID id){
        if (plan == null || plan.getProperties() == null || plan.getProperties().getPlanBlueprintValues() == null) return null;
        return plan.getProperties().getPlanBlueprintValues().stream().filter(x-> x.getFieldId().equals(id)).findFirst().orElse(null);
    }
    
    //endregion


    private List<org.opencdmp.commonmodels.models.description.FieldModel> findValueField(FieldModel fieldModel, PropertyDefinitionModel descriptionTemplateModel){
        List<org.opencdmp.commonmodels.models.description.FieldModel> items = new ArrayList<>();
        if (fieldModel == null || descriptionTemplateModel == null || descriptionTemplateModel.getFieldSets() == null) return items;
        for (PropertyDefinitionFieldSetModel propertyDefinitionFieldSetModel : descriptionTemplateModel.getFieldSets().values()){
            if (propertyDefinitionFieldSetModel.getItems() == null) continue;
            for (PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetModel.getItems()){
                if (propertyDefinitionFieldSetItemModel.getFields() == null) continue;
                org.opencdmp.commonmodels.models.description.FieldModel valueField = propertyDefinitionFieldSetItemModel.getFields().getOrDefault(fieldModel.getId(), null);
                if (valueField != null) items.add(valueField);
            }
        }
        return items;
    }

    private List<org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel> findFieldSetValue(FieldSetModel fieldSetModel, PropertyDefinitionModel descriptionTemplateModel){
        List<org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel> items = new ArrayList<>();
        if (fieldSetModel == null || descriptionTemplateModel == null || descriptionTemplateModel.getFieldSets() == null) return items;
        PropertyDefinitionFieldSetModel propertyDefinitionFieldSetModel =  descriptionTemplateModel.getFieldSets().getOrDefault(fieldSetModel.getId(), null);
        if (propertyDefinitionFieldSetModel != null && propertyDefinitionFieldSetModel.getItems() != null) return propertyDefinitionFieldSetModel.getItems();
        return items;
    }
    
    private String findValueFieldBySemantic(FieldSetModel fieldSet, org.opencdmp.commonmodels.models.description.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel, String semantic){
        FieldModel field = this.templateFieldSearcherService.findFieldBySemantic(fieldSet, semantic);
        if (field != null) {
            org.opencdmp.commonmodels.models.description.FieldModel valueField = propertyDefinitionFieldSetItemModel.getFields().getOrDefault(field.getId(), null);
            if (valueField != null) {
                switch (field.getData().getFieldType()) {
                    case FREE_TEXT, TEXT_AREA, RICH_TEXT_AREA -> {
                        if (valueField.getTextValue() != null && !valueField.getTextValue().isBlank()) return valueField.getTextValue();
                    }
                    case BOOLEAN_DECISION, CHECK_BOX -> {
                        if (valueField.getBooleanValue() != null) return valueField.getBooleanValue().toString();
                    }
                    case DATE_PICKER -> {
                        if (valueField.getDateValue() != null) return (DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault()).format(valueField.getDateValue()));
                    }
                    case DATASET_IDENTIFIER, VALIDATION -> {
                        if (valueField.getExternalIdentifier() != null && valueField.getExternalIdentifier().getIdentifier() != null && !valueField.getExternalIdentifier().getIdentifier().isBlank()) {
                            return (valueField.getExternalIdentifier().getIdentifier());
                        }
                    }
                    case TAGS -> {
                        if (valueField.getTextListValue() != null && !valueField.getTextListValue().isEmpty()) {
                            return (valueField.getTextListValue().getFirst());
                        }
                    }
                    case SELECT -> {
                        if (valueField.getTextListValue() != null && !valueField.getTextListValue().isEmpty()) {
                            SelectDataModel selectDataModel = (SelectDataModel)field.getData();
                            if (selectDataModel != null && selectDataModel.getOptions() != null && !selectDataModel.getOptions().isEmpty()){
                                for (SelectDataModel.OptionModel option : selectDataModel.getOptions()){
                                    if (valueField.getTextListValue().contains(option.getValue()) || valueField.getTextListValue().contains(option.getLabel())) return (option.getValue());
                                }
                            }
                        }
                    }
                    case RADIO_BOX -> {
                        if (valueField.getTextListValue() != null && !valueField.getTextListValue().isEmpty()) {
                            RadioBoxDataModel radioBoxModel = (RadioBoxDataModel)field.getData();
                            if (radioBoxModel != null && radioBoxModel.getOptions() != null && !radioBoxModel.getOptions().isEmpty()){
                                for (RadioBoxDataModel.RadioBoxOptionModel option : radioBoxModel.getOptions()){
                                    if (valueField.getTextListValue().contains(option.getValue()) || valueField.getTextListValue().contains(option.getLabel())) return option.getValue();
                                }
                            }
                        }
                    }
                    case REFERENCE_TYPES -> {
                        if (valueField.getReferences() != null && !valueField.getReferences().isEmpty()) {
                            for (ReferenceModel referenceModel : valueField.getReferences()) {
                                if (referenceModel == null
                                        || referenceModel.getType() == null || referenceModel.getType().getCode() == null || referenceModel.getType().getCode().isBlank()
                                        || referenceModel.getDefinition() == null || referenceModel.getDefinition().getFields() == null || referenceModel.getDefinition().getFields().isEmpty()) continue;
                                if (referenceModel.getReference() != null && !referenceModel.getReference().isBlank()) {
                                    return referenceModel.getReference();
                                }
                            }
                        }
                    }
                }
            }
        }
       return null;
    }
    //endregion


    //region import dmp
    
    public PlanModel importPlan(PlanImportModel planImportModel) {
        if (planImportModel.getFile() == null) throw new MyApplicationException("Not file defined");

        byte[] bytes = null;
        if (this.getConfiguration().isUseSharedStorage()) {
            bytes = this.storageService.readFile(planImportModel.getFile().getFileRef());
        } else {
            bytes = planImportModel.getFile().getFile();
        }
        PlanModel model = new PlanModel();
        try {
            Raid raid = this.jsonHandlingService.fromJson(Raid.class, new String(bytes, StandardCharsets.UTF_8));
            if (raid == null) throw new MyApplicationException("RAID is missing");


            if (raid.getAccess() != null && raid.getAccess().getType() != null && raid.getAccess().getType().getId() != null && raid.getAccess().getType().getId().equals(AccessTypeId.OpenAccess)) model.setAccessType(PlanAccessType.Public);
            else if (raid.getAccess() != null && raid.getAccess().getType() != null && raid.getAccess().getType().getId() != null && raid.getAccess().getType().getId().equals(AccessTypeId.EmbargoedAccess)) model.setAccessType(PlanAccessType.Restricted);
            else model.setAccessType(PlanAccessType.Restricted);

            model.setProperties(buildPlanPropertiesModel(planImportModel.getBlueprintModel(), raid));
            if (planImportModel.getBlueprintModel() != null) model.setPlanBlueprint(planImportModel.getBlueprintModel());

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return model;
    }

    private PlanPropertiesModel buildPlanPropertiesModel(PlanBlueprintModel planBlueprintModel, Raid raid) {
        PlanPropertiesModel planPropertiesModel = new PlanPropertiesModel();
        if (raid == null) return planPropertiesModel;

        planPropertiesModel.setPlanBlueprintValues(new ArrayList<>());

        if (raid.getIdentifier() != null) planPropertiesModel.getPlanBlueprintValues().addAll(buildPlanBlueprintValuesByRaidIdentifier(planBlueprintModel, raid.getIdentifier()));
        if (raid.getDate() != null) planPropertiesModel.getPlanBlueprintValues().addAll(buildPlanBlueprintValuesByRaidDate(planBlueprintModel, raid.getDate()));
        if (raid.getAccess() != null) planPropertiesModel.getPlanBlueprintValues().addAll(buildPlanBlueprintValuesByRaidAccess(planBlueprintModel, raid.getAccess()));

        return planPropertiesModel;
    }

    private List<PlanBlueprintValueModel> buildPlanBlueprintValuesByRaidIdentifier(PlanBlueprintModel planBlueprintModel, Identifier identifier) {
        List<PlanBlueprintValueModel> planBlueprintValueModels = new ArrayList<>();

        if (planBlueprintModel == null || identifier == null) return planBlueprintValueModels;

        List<org.opencdmp.commonmodels.models.planblueprint.FieldModel> fields = this.getFieldsOfSemantic(planBlueprintModel, SEMANTIC_IDENTIFIER_ID);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : fields) {
            if (field != null) planBlueprintValueModels.add(this.buildPlanBlueprintValueModel(field, identifier.getId()));
        }

        fields = this.getFieldsOfSemantic(planBlueprintModel, SEMANTIC_IDENTIFIER_SCHEMA_URI);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : fields) {
            if (field != null) planBlueprintValueModels.add(this.buildPlanBlueprintValueModel(field, identifier.getSchemaUri() != null ? identifier.getSchemaUri().value(): null));
        }

        if (identifier.getRegistrationAgency() != null) {
            fields = this.getFieldsOfSemantic(planBlueprintModel, SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_ID);
            for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : fields) {
                if (field != null) planBlueprintValueModels.add(this.buildPlanBlueprintValueModel(field, identifier.getRegistrationAgency().getId()));
            }

            fields = this.getFieldsOfSemantic(planBlueprintModel, SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_SCHEMA_URI);
            for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : fields) {
                if (field != null) planBlueprintValueModels.add(this.buildPlanBlueprintValueModel(field, identifier.getRegistrationAgency().getSchemaUri() != null ? identifier.getRegistrationAgency().getSchemaUri().value(): null));
            }
        }

        if (identifier.getOwner() != null) {
            fields = this.getFieldsOfSemantic(planBlueprintModel, SEMANTIC_IDENTIFIER_OWNER_ID);
            for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : fields) {
                if (field != null) planBlueprintValueModels.add(this.buildPlanBlueprintValueModel(field, identifier.getOwner().getId()));
            }

            fields = this.getFieldsOfSemantic(planBlueprintModel, SEMANTIC_IDENTIFIER_OWNER_SCHEMA_URI);
            for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : fields) {
                if (field != null) planBlueprintValueModels.add(this.buildPlanBlueprintValueModel(field, identifier.getOwner().getSchemaUri() != null ? identifier.getOwner().getSchemaUri().value(): null));
            }

            fields = this.getFieldsOfSemantic(planBlueprintModel, SEMANTIC_IDENTIFIER_OWNER_SERVICE_POINT);
            for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : fields) {
                if (field != null) planBlueprintValueModels.add(this.buildPlanBlueprintValueModel(field, identifier.getOwner().getServicePoint()));
            }
        }

        fields = this.getFieldsOfSemantic(planBlueprintModel, SEMANTIC_IDENTIFIER_LICENSE);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : fields) {
            if (field != null) planBlueprintValueModels.add(this.buildPlanBlueprintValueModel(field, identifier.getLicense() != null ? identifier.getLicense().value(): null));
        }

        fields = this.getFieldsOfSemantic(planBlueprintModel, SEMANTIC_IDENTIFIER_VERSION);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : fields) {
            if (field != null) planBlueprintValueModels.add(this.buildPlanBlueprintValueModel(field, identifier.getVersion()));
        }


        return planBlueprintValueModels;
    }

    private List<PlanBlueprintValueModel> buildPlanBlueprintValuesByRaidDate(PlanBlueprintModel planBlueprintModel, Date date) {
        List<PlanBlueprintValueModel> planBlueprintValueModels = new ArrayList<>();

        if (planBlueprintModel == null || date == null) return planBlueprintValueModels;

        List<org.opencdmp.commonmodels.models.planblueprint.FieldModel> fields = this.getFieldsOfSemantic(planBlueprintModel, SEMANTIC_DATE_START_DATE);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : fields) {
            if (field != null) planBlueprintValueModels.add(this.buildPlanBlueprintValueModel(field, date.getStartDate()));
        }

        fields = this.getFieldsOfSemantic(planBlueprintModel, SEMANTIC_DATE_END_DATE);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : fields) {
            if (field != null) planBlueprintValueModels.add(this.buildPlanBlueprintValueModel(field, date.getEndDate()));
        }

        return planBlueprintValueModels;
    }

    private List<PlanBlueprintValueModel> buildPlanBlueprintValuesByRaidAccess(PlanBlueprintModel planBlueprintModel, Access access) {
        List<PlanBlueprintValueModel> planBlueprintValueModels = new ArrayList<>();

        if (planBlueprintModel == null || access == null) return planBlueprintValueModels;

        List<org.opencdmp.commonmodels.models.planblueprint.FieldModel> fields = this.getFieldsOfSemantic(planBlueprintModel, SEMANTIC_ACCESS_EMBARGO_EXPIRY);
        for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : fields) {
            if (field != null) planBlueprintValueModels.add(this.buildPlanBlueprintValueModel(field, access.getEmbargoExpiry()));
        }

        if (access.getType() != null) {
            fields = this.getFieldsOfSemantic(planBlueprintModel, SEMANTIC_ACCESS_TYPE_ID);
            for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : fields) {
                if (field != null) planBlueprintValueModels.add(this.buildPlanBlueprintValueModel(field, access.getType().getId() != null ? access.getType().getId().value(): null));
            }

            fields = this.getFieldsOfSemantic(planBlueprintModel, SEMANTIC_ACCESS_TYPE_SCHEMA_URI);
            for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : fields) {
                if (field != null) planBlueprintValueModels.add(this.buildPlanBlueprintValueModel(field, access.getType().getSchemaUri() != null ? access.getType().getSchemaUri().value(): null));
            }
        }

        if (access.getStatement() != null) {
            fields = this.getFieldsOfSemantic(planBlueprintModel, SEMANTIC_ACCESS_STATEMENT_TEXT);
            for(org.opencdmp.commonmodels.models.planblueprint.FieldModel field : fields) {
                if (field != null) planBlueprintValueModels.add(this.buildPlanBlueprintValueModel(field, access.getStatement().getText()));
            }

            if (access.getStatement().getLanguage() != null) {
                fields = this.getFieldsOfSemantic(planBlueprintModel, SEMANTIC_ACCESS_STATEMENT_LANGUAGE_ID);
                for (org.opencdmp.commonmodels.models.planblueprint.FieldModel field : fields) {
                    if (field != null)
                        planBlueprintValueModels.add(this.buildPlanBlueprintValueModel(field, access.getStatement().getLanguage().getId() != null ? access.getStatement().getLanguage().getId().value(): null));
                }

                fields = this.getFieldsOfSemantic(planBlueprintModel, SEMANTIC_ACCESS_STATEMENT_LANGUAGE_SCHEMA_URI);
                for (org.opencdmp.commonmodels.models.planblueprint.FieldModel field : fields) {
                    if (field != null)
                        planBlueprintValueModels.add(this.buildPlanBlueprintValueModel(field, access.getStatement().getLanguage().getSchemaUri() != null ? access.getStatement().getLanguage().getSchemaUri().value(): null));
                }
            }
        }


        return planBlueprintValueModels;
    }

    private PlanBlueprintValueModel buildPlanBlueprintValueModel(org.opencdmp.commonmodels.models.planblueprint.FieldModel field, String value ){
        PlanBlueprintValueModel valueModel = new PlanBlueprintValueModel();

        if (field == null) return null;

        valueModel.setFieldId(field.getId());
        if (field.getCategory().equals(PlanBlueprintFieldCategory.Extra)) {
            ExtraFieldModel extraFieldModel = (ExtraFieldModel) field;
            if (value != null && !value.isBlank()) {
                switch (extraFieldModel.getDataType()) {
                    case Text, RichTex -> {
                        valueModel.setValue(value);
                    }
                    case Date -> {
                        try {
                            LocalDate localDate = LocalDate.parse(value);
                            valueModel.setDateValue(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                        } catch (Exception e) {
                            logger.warn("invalid instant parse for value: " + value);
                        }
                    }
                    case Number -> {
                        try {
                            valueModel.setNumberValue(Double.parseDouble(value));;
                        } catch (Exception e) {
                            logger.warn("invalid double parse for value: " + value);
                        }
                    }

                }
            }
        }
        return valueModel;
    }

    //endregion

    //region import description

    @Override
    public DescriptionModel importDescription(DescriptionImportModel descriptionImportModel) {
        throw new UnsupportedOperationException("import not supported");
    }


    @Override
    public FileTransformerConfiguration getConfiguration() {
        List<FileFormat> supportedFormats = List.of(new FileFormat("json", false, null));
        FileTransformerConfiguration configuration = new FileTransformerConfiguration();
        configuration.setFileTransformerId(this.configuration.getRaidFileTransformerServiceProperties().getTransformerId());
        configuration.setExportVariants(supportedFormats);
        configuration.setExportEntityTypes(FILE_TRANSFORMER_ENTITY_TYPES);
        configuration.setImportVariants(supportedFormats);
        configuration.setImportEntityTypes(FILE_TRANSFORMER_ENTITY_TYPES);
        configuration.setUseSharedStorage(this.configuration.getRaidFileTransformerServiceProperties().isUseSharedStorage());
        configuration.setConfigurationFields(this.configuration.getRaidFileTransformerServiceProperties().getConfigurationFields());
        configuration.setUserConfigurationFields(this.configuration.getRaidFileTransformerServiceProperties().getUserConfigurationFields());
        return configuration;
    }

    //endregion

    //region preprocessing
    @Override
    public PreprocessingPlanModel preprocessingPlan(FileEnvelopeModel fileEnvelopeModel) {

        PreprocessingPlanModel preprocessingPlanModel = new PreprocessingPlanModel();
        byte[] bytes = null;
        if (this.getConfiguration().isUseSharedStorage()) {
            bytes = this.storageService.readFile(fileEnvelopeModel.getFileRef());
        } else {
            bytes = fileEnvelopeModel.getFile();
        }
        try {
            Raid raid = this.jsonHandlingService.fromJson(Raid.class, new String(bytes, StandardCharsets.UTF_8));
            if (raid == null) throw new MyApplicationException("RaidModel is missing");

            preprocessingPlanModel.setLabel(fileEnvelopeModel.getFilename());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return preprocessingPlanModel;
    }

    @Override
    public PreprocessingDescriptionModel preprocessingDescription(FileEnvelopeModel fileEnvelopeModel) {
        throw new UnsupportedOperationException("preprocessing not supported");
    }

    //endregion
}
