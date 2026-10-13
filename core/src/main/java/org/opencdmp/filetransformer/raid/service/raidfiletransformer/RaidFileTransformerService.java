package org.opencdmp.filetransformer.raid.service.raidfiletransformer;

import gr.cite.tools.exception.MyApplicationException;
import gr.cite.tools.logging.LoggerService;
import org.opencdmp.commonmodels.enums.*;
import org.opencdmp.commonmodels.models.*;
import org.opencdmp.commonmodels.models.description.*;
import org.opencdmp.commonmodels.models.plan.PlanModel;
import org.opencdmp.commonmodels.models.plan.PlanPropertiesModel;
import org.opencdmp.commonmodels.models.planblueprint.PlanBlueprintModel;
import org.opencdmp.commonmodels.models.planblueprintdescriptiontemplatefields.FieldModel;
import org.opencdmp.commonmodels.models.planblueprintdescriptiontemplatefields.FieldSetModel;
import org.opencdmp.commonmodels.models.planblueprintdescriptiontemplatefields.fielddata.RadioBoxDataModel;
import org.opencdmp.commonmodels.models.planblueprintdescriptiontemplatefields.fielddata.ReferenceTypeDataModel;
import org.opencdmp.commonmodels.models.planblueprintdescriptiontemplatefields.fielddata.SelectDataModel;
import org.opencdmp.commonmodels.models.plandescriptionfields.ExternalIdentifierModel;
import org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel;
import org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetModel;
import org.opencdmp.commonmodels.models.reference.ReferenceModel;
import org.opencdmp.filetransformer.raid.enums.*;
import org.opencdmp.filetransformer.raid.model.raid.*;
import org.opencdmp.filetransformer.raid.model.raid.Date;
import org.opencdmp.filetransformer.raid.model.raid.Language;
import org.opencdmp.filetransformer.raid.service.descriptiontemplatesearcher.TemplateFieldSearcherService;
import org.opencdmp.filetransformer.raid.service.planblueprintsearcher.PlanBlueprintFieldSearcherService;
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
import tools.jackson.core.JacksonException;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RaidFileTransformerService implements FileTransformerClient {
    private static final LoggerService logger = new LoggerService(LoggerFactory.getLogger(RaidFileTransformerService.class));

    private final FileStorageService storageService;
    private final JsonHandlingService jsonHandlingService;
    private final RaidFileTransformerServiceConfiguration configuration;
    private final PlanBlueprintFieldSearcherService blueprintFieldSearcherService;
    private final TemplateFieldSearcherService templateFieldSearcherService;
    
    private final static List<PluginEntityType> FILE_TRANSFORMER_ENTITY_TYPES = List.of(PluginEntityType.Plan);

    static final String SEMANTIC_IDENTIFIER_ID = "raid.identifier.id";
    static final String SEMANTIC_IDENTIFIER_SCHEMA_URI = "raid.identifier.schemaUri";
    static final String SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_ID = "raid.identifier.registrationAgency.id";
    static final String SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_SCHEMA_URI = "raid.identifier.registrationAgency.schemaUri";
    static final String SEMANTIC_IDENTIFIER_OWNER_ID = "raid.identifier.owner.id";
    static final String SEMANTIC_IDENTIFIER_OWNER_SCHEMA_URI = "raid.identifier.owner.schemaUri";
    static final String SEMANTIC_IDENTIFIER_OWNER_SERVICE_POINT = "raid.identifier.owner.servicePoint";
    static final String SEMANTIC_IDENTIFIER_LICENSE = "raid.identifier.license";
    static final String SEMANTIC_IDENTIFIER_VERSION = "raid.identifier.version";
    static final String SEMANTIC_DATE_START_DATE = "raid.date.startDate";
    static final String SEMANTIC_DATE_END_DATE = "raid.date.endDate";
    static final String SEMANTIC_TITLE_TEXT = "raid.title.text";
    static final String SEMANTIC_TITLE_TYPE_ID = "raid.title.type.id";
    static final String SEMANTIC_TITLE_TYPE_SCHEMA_URI = "raid.title.type.schemaUri";
    static final String SEMANTIC_TITLE_LANGUAGE_ID = "raid.title.language.id";
    static final String SEMANTIC_TITLE_LANGUAGE_SCHEMA_URI = "raid.title.language.schemaUri";
    static final String SEMANTIC_TITLE_START_DATE = "raid.title.startDate";
    static final String SEMANTIC_TITLE_END_DATE = "raid.title.endDate";
    static final String SEMANTIC_DESCRIPTION_TEXT = "raid.description.text";
    static final String SEMANTIC_DESCRIPTION_TYPE_ID = "raid.description.type.id";
    static final String SEMANTIC_DESCRIPTION_TYPE_SCHEMA_URI = "raid.description.type.schemaUri";
    static final String SEMANTIC_DESCRIPTION_LANGUAGE_ID = "raid.description.language.id";
    static final String SEMANTIC_DESCRIPTION_LANGUAGE_SCHEMA_URI = "raid.description.language.schemaUri";
    static final String SEMANTIC_CONTRIBUTOR_ID = "raid.contributor.id";
    static final String SEMANTIC_CONTRIBUTOR_SCHEMA_URI = "raid.contributor.schemaUri";
    static final String SEMANTIC_CONTRIBUTOR_POSITION_ID = "raid.contributor.position.id";
    static final String SEMANTIC_CONTRIBUTOR_POSITION_SCHEMA_URI = "raid.contributor.position.schemaUri";
    static final String SEMANTIC_CONTRIBUTOR_POSITION_START_DATE = "raid.contributor.position.startDate";
    static final String SEMANTIC_CONTRIBUTOR_POSITION_END_DATE = "raid.contributor.position.endDate";
    static final String SEMANTIC_CONTRIBUTOR_LEADER = "raid.contributor.leader";
    static final String SEMANTIC_CONTRIBUTOR_CONTACT = "raid.contributor.contact";
    static final String SEMANTIC_CONTRIBUTOR_ROLE_ID = "raid.contributor.role.id";
    static final String SEMANTIC_CONTRIBUTOR_ROLE_SCHEMA_URI = "raid.contributor.role.schemaUri";
    static final String SEMANTIC_ORGANISATION_ID = "raid.organisation.id";
    static final String SEMANTIC_ORGANISATION_SCHEMA_URI = "raid.organisation.schemaUri";
    static final String SEMANTIC_ORGANISATION_ROLE_ID = "raid.organisation.role.id";
    static final String SEMANTIC_ORGANISATION_ROLE_SCHEMA_URI = "raid.organisation.role.schemaUri";
    static final String SEMANTIC_ORGANISATION_ROLE_START_DATE = "raid.organisation.role.startDate";
    static final String SEMANTIC_ORGANISATION_ROLE_END_DATE = "raid.organisation.role.endDate";
    static final String SEMANTIC_RELATED_OBJECT_ID = "raid.relatedObject.id";
    static final String SEMANTIC_RELATED_OBJECT_SCHEMA_URI = "raid.relatedObject.schemaUri";
    static final String SEMANTIC_RELATED_OBJECT_TYPE_ID = "raid.relatedObject.type.id";
    static final String SEMANTIC_RELATED_OBJECT_TYPE_SCHEMA_URI = "raid.relatedObject.type.schemaUri";
    static final String SEMANTIC_RELATED_OBJECT_CATEGORY_ID = "raid.relatedObject.category.id";
    static final String SEMANTIC_RELATED_OBJECT_CATEGORY_SCHEMA_URI = "raid.relatedObject.category.schemaUri";
    static final String SEMANTIC_ALTERNATE_IDENTIFIER_ID = "raid.alternateIdentifier.id";
    static final String SEMANTIC_ALTERNATE_IDENTIFIER_TYPE = "raid.alternateIdentifier.type";
    static final String SEMANTIC_ALTERNATE_URL_URL = "raid.alternateUrl.url";
    static final String SEMANTIC_RELATED_RAID_ID = "raid.relatedRaid.id";
    static final String SEMANTIC_RELATED_RAID_TYPE_ID = "raid.relatedRaid.type.id";
    static final String SEMANTIC_RELATED_RAID_TYPE_SCHEMA_URI = "raid.relatedRaid.type.schemaUri";
    static final String SEMANTIC_ACCESS_TYPE_ID = "raid.access.type.id";
    static final String SEMANTIC_ACCESS_TYPE_SCHEMA_URI = "raid.access.type.schemaUri";
    static final String SEMANTIC_ACCESS_EMBARGO_EXPIRY = "raid.access.embargoExpiry";
    static final String SEMANTIC_ACCESS_STATEMENT_TEXT = "raid.access.statement.text";
    static final String SEMANTIC_ACCESS_STATEMENT_LANGUAGE_ID = "raid.access.statement.language.id";
    static final String SEMANTIC_ACCESS_STATEMENT_LANGUAGE_SCHEMA_URI = "raid.access.statement.language.schemaUri";


    @Autowired
    public RaidFileTransformerService(FileStorageService storageService, JsonHandlingService jsonHandlingService, RaidFileTransformerServiceConfiguration configuration, PlanBlueprintFieldSearcherService blueprintFieldSearcherService, TemplateFieldSearcherService templateFieldSearcherService) {
        this.storageService = storageService;
	    this.configuration = configuration;
	    this.jsonHandlingService = jsonHandlingService;
        this.blueprintFieldSearcherService = blueprintFieldSearcherService;
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
        wordFile.setFilename(this.safeFileName(planFileTransformerModel.getLabel(), "plan") + ".json");
        return wordFile;
    }

    private String safeFileName(String name, String fallback) {
        String safe = name == null ? "" : name.replaceAll("[\\\\/:*?\"<>|\\p{Cntrl}]", "_").replaceAll("[\\s.]+$", "").strip();
        return safe.isBlank() ? fallback : safe;
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
        title.setStartDate((DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC).format(plan.getCreatedAt())));
        if (plan.getFinalizedAt() != null) title.setEndDate(DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC).format(plan.getFinalizedAt()));

        raid.setTitles(new ArrayList<>());
        raid.getTitles().add(title);

        List<Title> titles = this.buildRaidTitle(plan);
        if (titles != null && !titles.isEmpty()) raid.getTitles().addAll(titles);

        raid.setDescriptions(this.buildRaidDescriptions(plan));
        raid.setIdentifier(this.buildRaidIdentifier(plan));
        raid.setDate(this.buildRaidDate(plan));
        raid.setAccess(this.buildRaidAccess(plan));
        raid.setContributors(this.buildRaidContributors(plan));
        raid.setOrganisations(this.buildRaidOrganisations(plan));
        raid.setRelatedObjects(this.buildRaidRelatedObjects(plan));
        raid.setAlternateIdentifiers(this.buildRaidAlternateIdentifiers(plan));
        raid.setAlternateUrls(this.buildRaidAlternateUrls(plan));
        raid.setRelatedRaids(this.buildRaidRelatedRaids(plan));

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

    private List<Title> buildRaidTitle(PlanModel model){
        List<Title> values = new ArrayList<>();
        for (FieldSetModel fieldSet : this.blueprintFieldSearcherService.searchFieldSetsBySemantics(model.getPlanBlueprint(), List.of(SEMANTIC_TITLE_TEXT, SEMANTIC_TITLE_TYPE_ID,
                SEMANTIC_TITLE_TYPE_SCHEMA_URI, SEMANTIC_TITLE_LANGUAGE_ID, SEMANTIC_TITLE_LANGUAGE_SCHEMA_URI, SEMANTIC_TITLE_START_DATE, SEMANTIC_TITLE_END_DATE))) {
            List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties().getFieldSets());
            for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
               this.buildTitle(values, fieldSet, propertyDefinitionFieldSetItemModel);
            }
        }

        if (model.getDescriptions() != null) {
            for (DescriptionModel descriptionModel : model.getDescriptions()) {
                for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(descriptionModel.getDescriptionTemplate(), List.of(SEMANTIC_TITLE_TEXT, SEMANTIC_TITLE_TYPE_ID,
                        SEMANTIC_TITLE_TYPE_SCHEMA_URI, SEMANTIC_TITLE_LANGUAGE_ID, SEMANTIC_TITLE_LANGUAGE_SCHEMA_URI, SEMANTIC_TITLE_START_DATE, SEMANTIC_TITLE_END_DATE))) {
                    List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, descriptionModel.getProperties().getFieldSets());
                    for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                        this.buildTitle(values, fieldSet, propertyDefinitionFieldSetItemModel);
                    }
                }
            }
        }

        return values.isEmpty() ? null : values;
    }

    private void buildTitle (List<Title> values, FieldSetModel fieldSet, org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel) {
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

        if (type.getId() != null || type.getSchemaUri() != null) item.setType(type);
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
        if (language.getId() != null || language.getSchemaUri() != null) item.setLanguage(language);

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

    private List<Description> buildRaidDescriptions(PlanModel model){

        List<Description> values = new ArrayList<>();
        for (FieldSetModel fieldSet : this.blueprintFieldSearcherService.searchFieldSetsBySemantics(model.getPlanBlueprint(), List.of(SEMANTIC_DESCRIPTION_TEXT, SEMANTIC_DESCRIPTION_TYPE_ID, SEMANTIC_DESCRIPTION_TYPE_SCHEMA_URI
                , SEMANTIC_DESCRIPTION_LANGUAGE_ID, SEMANTIC_DESCRIPTION_LANGUAGE_SCHEMA_URI))) {
            List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties().getFieldSets());
            for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                this.buildDescription(values, fieldSet, propertyDefinitionFieldSetItemModel);
            }
        }

        if (model.getDescriptions() != null) {
            for (DescriptionModel descriptionModel : model.getDescriptions()) {
                for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(descriptionModel.getDescriptionTemplate(), List.of(SEMANTIC_DESCRIPTION_TEXT, SEMANTIC_DESCRIPTION_TYPE_ID, SEMANTIC_DESCRIPTION_TYPE_SCHEMA_URI
                        , SEMANTIC_DESCRIPTION_LANGUAGE_ID, SEMANTIC_DESCRIPTION_LANGUAGE_SCHEMA_URI))) {
                    List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, descriptionModel.getProperties().getFieldSets());
                    for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                        this.buildDescription(values, fieldSet, propertyDefinitionFieldSetItemModel);
                    }
                }
            }
        }
        return values.isEmpty() ? null : values;
    }

    private void buildDescription (List<Description> values, FieldSetModel fieldSet, org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel) {
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

        if (type.getId() != null || type.getSchemaUri() != null) item.setType(type);

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
        if (language.getId() != null || language.getSchemaUri() != null) item.setLanguage(language);

        fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_DESCRIPTION_TEXT);
        if (fieldValue != null && !fieldValue.isBlank()) {
            item.setText(fieldValue);
            valueFound = true;
        }

        if (valueFound) values.add(item);
    }

    private List<Contributor> buildRaidContributors(PlanModel model){
        List<Contributor> values = new ArrayList<>();
        for (FieldSetModel fieldSet : this.blueprintFieldSearcherService.searchFieldSetsBySemantics(model.getPlanBlueprint(), List.of(SEMANTIC_CONTRIBUTOR_ID, SEMANTIC_CONTRIBUTOR_SCHEMA_URI, SEMANTIC_CONTRIBUTOR_POSITION_ID
                , SEMANTIC_CONTRIBUTOR_POSITION_SCHEMA_URI, SEMANTIC_CONTRIBUTOR_POSITION_START_DATE, SEMANTIC_CONTRIBUTOR_POSITION_END_DATE, SEMANTIC_CONTRIBUTOR_LEADER, SEMANTIC_CONTRIBUTOR_CONTACT, SEMANTIC_CONTRIBUTOR_ROLE_ID, SEMANTIC_CONTRIBUTOR_ROLE_SCHEMA_URI))) {
            List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties().getFieldSets());
            for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
               this.buildContributor(values, fieldSet, propertyDefinitionFieldSetItemModel);
            }
        }

        if (model.getDescriptions() != null) {
            for (DescriptionModel descriptionModel : model.getDescriptions()) {
                for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(descriptionModel.getDescriptionTemplate(), List.of(SEMANTIC_CONTRIBUTOR_ID, SEMANTIC_CONTRIBUTOR_SCHEMA_URI, SEMANTIC_CONTRIBUTOR_POSITION_ID
                        , SEMANTIC_CONTRIBUTOR_POSITION_SCHEMA_URI, SEMANTIC_CONTRIBUTOR_POSITION_START_DATE, SEMANTIC_CONTRIBUTOR_POSITION_END_DATE, SEMANTIC_CONTRIBUTOR_LEADER, SEMANTIC_CONTRIBUTOR_CONTACT, SEMANTIC_CONTRIBUTOR_ROLE_ID, SEMANTIC_CONTRIBUTOR_ROLE_SCHEMA_URI))) {
                    List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, descriptionModel.getProperties().getFieldSets());
                    for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                        this.buildContributor(values, fieldSet, propertyDefinitionFieldSetItemModel);
                    }
                }
            }
        }
        return values.isEmpty() ? null : values;
    }

    private void buildContributor(List<Contributor> values, FieldSetModel fieldSet, org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel) {
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

        if (position.getId() != null || position.getSchemaUri() != null || position.getStartDate() != null || position.getEndDate() != null) item.setPosition(List.of(position));

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

        if (role.getId() != null || role.getSchemaUri() != null) item.setRole(List.of(role));

        fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_CONTRIBUTOR_ID);
        if (fieldValue != null && !fieldValue.isBlank()) {
            item.setId(fieldValue);
            valueFound = true;
        }

        fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_CONTRIBUTOR_SCHEMA_URI);
        if (fieldValue != null && !fieldValue.isBlank()) {
            try {
                item.setSchemaUri(ContributorSchemaUri.fromValue(fieldValue));
                valueFound = true;
            } catch (Exception e) {
                logger.warn("invalid contributor schema uri value: " + fieldValue);
            }
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

    private Date buildRaidDate(PlanModel model){

        Date item = new Date();

        for (FieldSetModel fieldSet : this.blueprintFieldSearcherService.searchFieldSetsBySemantics(model.getPlanBlueprint(), List.of(SEMANTIC_DATE_START_DATE, SEMANTIC_DATE_END_DATE))) {
            List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties().getFieldSets());
            for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                if (this.buildRaidDate(item, fieldSet, propertyDefinitionFieldSetItemModel)) break;
            }
        }

        if (model.getDescriptions() != null) {
            for (DescriptionModel descriptionModel : model.getDescriptions()) {
                for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(descriptionModel.getDescriptionTemplate(), List.of(SEMANTIC_DATE_START_DATE, SEMANTIC_DATE_END_DATE))) {
                    List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, descriptionModel.getProperties().getFieldSets());
                    for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                        if (this.buildRaidDate(item, fieldSet, propertyDefinitionFieldSetItemModel)) break;
                    }
                }
            }
        }

        return item.getStartDate() == null && item.getEndDate() == null ? null : item;
    }

    // RAiD allows one date, so only the first answered row of a field set is used and earlier values are kept
    private boolean buildRaidDate(Date item, FieldSetModel fieldSet, org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel) {
        boolean valueFound = false;

        String fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_DATE_START_DATE);
        if (fieldValue != null && !fieldValue.isBlank()) {
            if (this.isValidDateField(fieldValue)) {
                if (item.getStartDate() == null) item.setStartDate(fieldValue);
                valueFound = true;
            }
        }

        fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_DATE_END_DATE);
        if (fieldValue != null && !fieldValue.isBlank()) {
            if (this.isValidDateField(fieldValue)) {
                if (item.getEndDate() == null) item.setEndDate(fieldValue);
                valueFound = true;
            }
        }

        return valueFound;
    }

    private Identifier buildRaidIdentifier(PlanModel model){

        Identifier identifier = new Identifier();

        for (FieldSetModel fieldSet : this.blueprintFieldSearcherService.searchFieldSetsBySemantics(model.getPlanBlueprint(), List.of(SEMANTIC_IDENTIFIER_ID, SEMANTIC_IDENTIFIER_SCHEMA_URI, SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_ID,
                SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_SCHEMA_URI, SEMANTIC_IDENTIFIER_OWNER_ID, SEMANTIC_IDENTIFIER_OWNER_SCHEMA_URI, SEMANTIC_IDENTIFIER_OWNER_SERVICE_POINT, SEMANTIC_IDENTIFIER_LICENSE, SEMANTIC_IDENTIFIER_VERSION))) {
            List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties().getFieldSets());
            for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                if (this.buildIdentifier(identifier, fieldSet, propertyDefinitionFieldSetItemModel)) break;
            }
        }
        if (model.getDescriptions() != null) {
            for (DescriptionModel descriptionModel : model.getDescriptions()) {
                for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(descriptionModel.getDescriptionTemplate(), List.of(SEMANTIC_IDENTIFIER_ID, SEMANTIC_IDENTIFIER_SCHEMA_URI, SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_ID,
                        SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_SCHEMA_URI, SEMANTIC_IDENTIFIER_OWNER_ID, SEMANTIC_IDENTIFIER_OWNER_SCHEMA_URI, SEMANTIC_IDENTIFIER_OWNER_SERVICE_POINT, SEMANTIC_IDENTIFIER_LICENSE, SEMANTIC_IDENTIFIER_VERSION))) {
                    List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, descriptionModel.getProperties().getFieldSets());
                    for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                        if (this.buildIdentifier(identifier, fieldSet, propertyDefinitionFieldSetItemModel)) break;
                    }
                }
            }
        }

        if (identifier.getRegistrationAgency() != null && identifier.getRegistrationAgency().getId() == null && identifier.getRegistrationAgency().getSchemaUri() == null) identifier.setRegistrationAgency(null);
        if (identifier.getOwner() != null && identifier.getOwner().getId() == null && identifier.getOwner().getSchemaUri() == null && identifier.getOwner().getServicePoint() == null) identifier.setOwner(null);
        if (identifier.getId() == null && identifier.getSchemaUri() == null && identifier.getRegistrationAgency() == null && identifier.getOwner() == null && identifier.getLicense() == null && identifier.getVersion() == null) return null;

        return identifier;
    }

    // RAiD allows one identifier, so only the first answered row of a field set is used and earlier values are kept
    private boolean buildIdentifier(Identifier identifier, FieldSetModel fieldSet, org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel) {
        boolean valueFound = false;

        String fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_IDENTIFIER_ID);
        if (fieldValue != null && !fieldValue.isBlank()) {
            if (identifier.getId() == null) identifier.setId(fieldValue);
            valueFound = true;
        }

        fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_IDENTIFIER_SCHEMA_URI);
        if (fieldValue != null && !fieldValue.isBlank()) {
            try {
                IdentifierSchemaUri value = IdentifierSchemaUri.fromValue(fieldValue);
                if (identifier.getSchemaUri() == null) identifier.setSchemaUri(value);
                valueFound = true;
            } catch (Exception e) {
                logger.warn("invalid identifier schema uri value: " + fieldValue);
            }
        }

        if (identifier.getRegistrationAgency() == null) identifier.setRegistrationAgency(new Identifier.RegistrationAgency());

        fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_ID);
        if (fieldValue != null && !fieldValue.isBlank()) {
            if (identifier.getRegistrationAgency().getId() == null) identifier.getRegistrationAgency().setId(fieldValue);
            valueFound = true;
        }

        fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_SCHEMA_URI);
        if (fieldValue != null && !fieldValue.isBlank()) {
            try {
                IdentifierRegistrationAgencySchemaUri value = IdentifierRegistrationAgencySchemaUri.fromValue(fieldValue);
                if (identifier.getRegistrationAgency().getSchemaUri() == null) identifier.getRegistrationAgency().setSchemaUri(value);
                valueFound = true;
            } catch (Exception e) {
                logger.warn("invalid agency schema uri value: " + fieldValue);
            }
        }

        if (identifier.getOwner() == null) identifier.setOwner(new Identifier.Owner());

        fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_IDENTIFIER_OWNER_ID);
        if (fieldValue != null && !fieldValue.isBlank()) {
            if (identifier.getOwner().getId() == null) identifier.getOwner().setId(fieldValue);
            valueFound = true;
        }

        fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_IDENTIFIER_OWNER_SCHEMA_URI);
        if (fieldValue != null && !fieldValue.isBlank()) {
            try {
                // RAiD allows only ROR (https://ror.org/) for both the owner and the registration agency, so they share one vocabulary
                IdentifierRegistrationAgencySchemaUri value = IdentifierRegistrationAgencySchemaUri.fromValue(fieldValue);
                if (identifier.getOwner().getSchemaUri() == null) identifier.getOwner().setSchemaUri(value);
                valueFound = true;
            } catch (Exception e) {
                logger.warn("invalid owner schema uri value: " + fieldValue);
            }
        }

        fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_IDENTIFIER_OWNER_SERVICE_POINT);
        if (fieldValue != null && !fieldValue.isBlank()) {
            try {
                Long value = new BigDecimal(fieldValue.strip()).longValueExact();
                if (identifier.getOwner().getServicePoint() == null) identifier.getOwner().setServicePoint(value);
                valueFound = true;
            } catch (Exception e) {
                logger.warn("invalid owner service point value: " + fieldValue);
            }
        }

        fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_IDENTIFIER_LICENSE);
        if (fieldValue != null && !fieldValue.isBlank()) {
            try {
                IdentifierLicense value = IdentifierLicense.fromValue(fieldValue);
                if (identifier.getLicense() == null) identifier.setLicense(value);
                valueFound = true;
            } catch (Exception e) {
                logger.warn("invalid identifier license uri value: " + fieldValue);
            }
        }

        fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_IDENTIFIER_VERSION);
        if (fieldValue != null && !fieldValue.isBlank()) {
            if (identifier.getVersion() == null) identifier.setVersion(fieldValue);
            valueFound = true;
        }

        return valueFound;
    }

    private Access buildRaidAccess(PlanModel model){

        Access access = new Access();

        for (FieldSetModel fieldSet : this.blueprintFieldSearcherService.searchFieldSetsBySemantics(model.getPlanBlueprint(), List.of(SEMANTIC_ACCESS_TYPE_ID, SEMANTIC_ACCESS_TYPE_SCHEMA_URI, SEMANTIC_ACCESS_EMBARGO_EXPIRY,
                SEMANTIC_ACCESS_STATEMENT_TEXT, SEMANTIC_ACCESS_STATEMENT_LANGUAGE_ID, SEMANTIC_ACCESS_STATEMENT_LANGUAGE_SCHEMA_URI))) {
            List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties().getFieldSets());
            for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                if (this.buildAccess(access, fieldSet, propertyDefinitionFieldSetItemModel)) break;
            }
        }

        if (model.getDescriptions() != null) {
            for (DescriptionModel descriptionModel : model.getDescriptions()) {
                for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(descriptionModel.getDescriptionTemplate(), List.of(SEMANTIC_ACCESS_TYPE_ID, SEMANTIC_ACCESS_TYPE_SCHEMA_URI, SEMANTIC_ACCESS_EMBARGO_EXPIRY,
                        SEMANTIC_ACCESS_STATEMENT_TEXT, SEMANTIC_ACCESS_STATEMENT_LANGUAGE_ID, SEMANTIC_ACCESS_STATEMENT_LANGUAGE_SCHEMA_URI))) {
                    List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, descriptionModel.getProperties().getFieldSets());
                    for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                        if (this.buildAccess(access, fieldSet, propertyDefinitionFieldSetItemModel)) break;
                    }
                }
            }
        }

        if (access.getType() != null && access.getType().getId() == null && access.getType().getSchemaUri() == null) access.setType(null);
        if (access.getStatement() != null && access.getStatement().getLanguage() != null && access.getStatement().getLanguage().getId() == null && access.getStatement().getLanguage().getSchemaUri() == null) access.getStatement().setLanguage(null);
        if (access.getStatement() != null && access.getStatement().getText() == null && access.getStatement().getLanguage() == null) access.setStatement(null);
        if (access.getType() == null && access.getEmbargoExpiry() == null && access.getStatement() == null) return null;

        return access;
    }

    // RAiD allows one access block, so only the first answered row of a field set is used and earlier values are kept
    private boolean buildAccess(Access access, FieldSetModel fieldSet, org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel) {
        boolean valueFound = false;

        if (access.getType() == null) access.setType(new Access.Type());

        String fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_ACCESS_TYPE_ID);
        if (fieldValue != null && !fieldValue.isBlank()) {
            try {
                AccessTypeId value = AccessTypeId.fromValue(fieldValue);
                if (access.getType().getId() == null) access.getType().setId(value);
                valueFound = true;
            } catch (Exception e) {
                logger.warn("invalid access type id value: " + fieldValue);
            }
        }

        fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_ACCESS_TYPE_SCHEMA_URI);
        if (fieldValue != null && !fieldValue.isBlank()) {
            try {
                AccessTypeSchemaUri value = AccessTypeSchemaUri.fromValue(fieldValue);
                if (access.getType().getSchemaUri() == null) access.getType().setSchemaUri(value);
                valueFound = true;
            } catch (Exception e) {
                logger.warn("invalid access type schema uri: " + fieldValue);
            }
        }

        fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_ACCESS_EMBARGO_EXPIRY);
        if (fieldValue != null && !fieldValue.isBlank()) {
            if (this.isValidDateField(fieldValue)) {
                if (access.getEmbargoExpiry() == null) access.setEmbargoExpiry(fieldValue);
                valueFound = true;
            }
        }

        if (access.getStatement() == null) access.setStatement(new Access.Statement());

        fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_ACCESS_STATEMENT_TEXT);
        if (fieldValue != null && !fieldValue.isBlank()) {
            if (access.getStatement().getText() == null) access.getStatement().setText(fieldValue);
            valueFound = true;
        }

        if (access.getStatement().getLanguage() == null) access.getStatement().setLanguage(new Language());

        fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_ACCESS_STATEMENT_LANGUAGE_ID);
        if (fieldValue != null && !fieldValue.isBlank()) {
            try {
                LanguageId value = LanguageId.fromValue(fieldValue);
                if (access.getStatement().getLanguage().getId() == null) access.getStatement().getLanguage().setId(value);
                valueFound = true;
            } catch (Exception e) {
                logger.warn("invalid access statement language id value: " + fieldValue);
            }
        }

        fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_ACCESS_STATEMENT_LANGUAGE_SCHEMA_URI);
        if (fieldValue != null && !fieldValue.isBlank()) {
            try {
                LanguageSchemaUri value = LanguageSchemaUri.fromValue(fieldValue);
                if (access.getStatement().getLanguage().getSchemaUri() == null) access.getStatement().getLanguage().setSchemaUri(value);
                valueFound = true;
            } catch (Exception e) {
                logger.warn("invalid access statement language schema uri  value: " + fieldValue);
            }
        }

        return valueFound;
    }

    private List<Organisation> buildRaidOrganisations(PlanModel model){
        List<Organisation> values = new ArrayList<>();
        for (FieldSetModel fieldSet : this.blueprintFieldSearcherService.searchFieldSetsBySemantics(model.getPlanBlueprint(), List.of(SEMANTIC_ORGANISATION_ID, SEMANTIC_ORGANISATION_SCHEMA_URI, SEMANTIC_ORGANISATION_ROLE_ID
                , SEMANTIC_ORGANISATION_ROLE_SCHEMA_URI, SEMANTIC_ORGANISATION_ROLE_START_DATE, SEMANTIC_ORGANISATION_ROLE_END_DATE))) {
            List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties().getFieldSets());
            for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                this.buildRaidOrganisation(values, fieldSet, propertyDefinitionFieldSetItemModel);
            }
        }

        if (model.getDescriptions() != null) {
            for (DescriptionModel descriptionModel : model.getDescriptions()) {
                for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(descriptionModel.getDescriptionTemplate(), List.of(SEMANTIC_ORGANISATION_ID, SEMANTIC_ORGANISATION_SCHEMA_URI, SEMANTIC_ORGANISATION_ROLE_ID
                        , SEMANTIC_ORGANISATION_ROLE_SCHEMA_URI, SEMANTIC_ORGANISATION_ROLE_START_DATE, SEMANTIC_ORGANISATION_ROLE_END_DATE))) {
                    List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, descriptionModel.getProperties().getFieldSets());
                    for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                        this.buildRaidOrganisation(values, fieldSet, propertyDefinitionFieldSetItemModel);
                    }
                }
            }
        }
        return values.isEmpty() ? null : values;
    }

    private void buildRaidOrganisation(List<Organisation> values, FieldSetModel fieldSet, org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel) {
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

        if (role.getId() != null || role.getSchemaUri() != null || role.getStartDate() != null || role.getEndDate() != null) item.setRole(List.of(role));

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

    private List<RelatedObject> buildRaidRelatedObjects(PlanModel model){
        List<RelatedObject> values = new ArrayList<>();
        for (FieldSetModel fieldSet : this.blueprintFieldSearcherService.searchFieldSetsBySemantics(model.getPlanBlueprint(), List.of(SEMANTIC_RELATED_OBJECT_ID, SEMANTIC_RELATED_OBJECT_SCHEMA_URI, SEMANTIC_RELATED_OBJECT_TYPE_ID
                , SEMANTIC_RELATED_OBJECT_TYPE_SCHEMA_URI, SEMANTIC_RELATED_OBJECT_CATEGORY_ID, SEMANTIC_RELATED_OBJECT_CATEGORY_SCHEMA_URI))) {
            List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties().getFieldSets());
            for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                this.buildRelatedObject(values, fieldSet, propertyDefinitionFieldSetItemModel);
            }
        }

        if (model.getDescriptions() != null) {
            for (DescriptionModel descriptionModel : model.getDescriptions()) {
                for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(descriptionModel.getDescriptionTemplate(), List.of(SEMANTIC_RELATED_OBJECT_ID, SEMANTIC_RELATED_OBJECT_SCHEMA_URI, SEMANTIC_RELATED_OBJECT_TYPE_ID
                        , SEMANTIC_RELATED_OBJECT_TYPE_SCHEMA_URI, SEMANTIC_RELATED_OBJECT_CATEGORY_ID, SEMANTIC_RELATED_OBJECT_CATEGORY_SCHEMA_URI))) {
                    List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, descriptionModel.getProperties().getFieldSets());
                    for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                        this.buildRelatedObject(values, fieldSet, propertyDefinitionFieldSetItemModel);
                    }
                }
            }
        }

        return values.isEmpty() ? null : values;
    }

    private void buildRelatedObject(List<RelatedObject> values, FieldSetModel fieldSet, org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel) {
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

        if (type.getId() != null || type.getSchemaUri() != null) item.setType(type);

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

        if (category.getId() != null || category.getSchemaUri() != null) item.setCategory(List.of(category));

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

    private List<AlternateIdentifier> buildRaidAlternateIdentifiers(PlanModel model){
        List<AlternateIdentifier> values = new ArrayList<>();
        for (FieldSetModel fieldSet : this.blueprintFieldSearcherService.searchFieldSetsBySemantics(model.getPlanBlueprint(), List.of(SEMANTIC_ALTERNATE_IDENTIFIER_ID, SEMANTIC_ALTERNATE_IDENTIFIER_TYPE))) {
            List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties().getFieldSets());
            for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
               this.buildAlternateIdentifier(values, fieldSet, propertyDefinitionFieldSetItemModel);
            }
        }

        if (model.getDescriptions() != null) {
            for (DescriptionModel descriptionModel : model.getDescriptions()) {
                for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(descriptionModel.getDescriptionTemplate(), List.of(SEMANTIC_ALTERNATE_IDENTIFIER_ID, SEMANTIC_ALTERNATE_IDENTIFIER_TYPE))) {
                    List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, descriptionModel.getProperties().getFieldSets());
                    for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                        this.buildAlternateIdentifier(values, fieldSet, propertyDefinitionFieldSetItemModel);
                    }
                }
            }
        }
        return values.isEmpty() ? null : values;
    }

    private void buildAlternateIdentifier(List<AlternateIdentifier> values, FieldSetModel fieldSet, org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel) {
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

    private List<AlternativeUrl> buildRaidAlternateUrls(PlanModel model){
        List<AlternativeUrl> values = new ArrayList<>();
        for (FieldSetModel fieldSet : this.blueprintFieldSearcherService.searchFieldSetsBySemantics(model.getPlanBlueprint(), List.of(SEMANTIC_ALTERNATE_URL_URL))) {
            List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties().getFieldSets());
            for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                this.buildRaidAlternateUrl(values, fieldSet, propertyDefinitionFieldSetItemModel);
            }
        }

        if (model.getDescriptions() != null) {
            for (DescriptionModel descriptionModel : model.getDescriptions()) {
                for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(descriptionModel.getDescriptionTemplate(), List.of(SEMANTIC_ALTERNATE_URL_URL))) {
                    List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, descriptionModel.getProperties().getFieldSets());
                    for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                        this.buildRaidAlternateUrl(values, fieldSet, propertyDefinitionFieldSetItemModel);
                    }
                }
            }
        }
        return values.isEmpty() ? null : values;
    }

    private void buildRaidAlternateUrl(List<AlternativeUrl> values, FieldSetModel fieldSet, org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel) {
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

    private List<RelatedRaid> buildRaidRelatedRaids(PlanModel model){
        List<RelatedRaid> values = new ArrayList<>();
        for (FieldSetModel fieldSet : this.blueprintFieldSearcherService.searchFieldSetsBySemantics(model.getPlanBlueprint(), List.of(SEMANTIC_RELATED_RAID_ID, SEMANTIC_RELATED_RAID_TYPE_ID, SEMANTIC_RELATED_RAID_TYPE_SCHEMA_URI))) {
            List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, model.getProperties().getFieldSets());
            for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                this.buildRaidRelatedRaid(values, fieldSet, propertyDefinitionFieldSetItemModel);
            }
        }

        if (model.getDescriptions() != null) {
            for (DescriptionModel descriptionModel : model.getDescriptions()) {
                for (FieldSetModel fieldSet : this.templateFieldSearcherService.searchFieldSetsBySemantics(descriptionModel.getDescriptionTemplate(), List.of(SEMANTIC_RELATED_RAID_ID, SEMANTIC_RELATED_RAID_TYPE_ID, SEMANTIC_RELATED_RAID_TYPE_SCHEMA_URI))) {
                    List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> propertyDefinitionFieldSetItemModels = this.findFieldSetValue(fieldSet, descriptionModel.getProperties().getFieldSets());
                    for (org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel : propertyDefinitionFieldSetItemModels) {
                        this.buildRaidRelatedRaid(values, fieldSet, propertyDefinitionFieldSetItemModel);
                    }
                }
            }
        }
        return values.isEmpty() ? null : values;
    }

    private void buildRaidRelatedRaid(List<RelatedRaid> values, FieldSetModel fieldSet, org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel) {
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

        if (type.getId() != null || type.getSchemaUri() != null) item.setType(type);

        fieldValue = this.findValueFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, SEMANTIC_RELATED_RAID_ID);
        if (fieldValue != null && !fieldValue.isBlank()) {
            item.setId(fieldValue);
            valueFound = true;
        }

        if (valueFound) values.add(item);
    }


    private List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> findFieldSetValue(FieldSetModel fieldSetModel, Map<String, PropertyDefinitionFieldSetModel> fieldSets){
        List<org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel> items = new ArrayList<>();
        if (fieldSetModel == null || fieldSets == null) return items;
        PropertyDefinitionFieldSetModel propertyDefinitionFieldSetModel =  fieldSets.getOrDefault(fieldSetModel.getId(), null);
        if (propertyDefinitionFieldSetModel != null && propertyDefinitionFieldSetModel.getItems() != null) return propertyDefinitionFieldSetModel.getItems();
        return items;
    }

    private String findValueFieldBySemantic(FieldSetModel fieldSet, org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel, String semantic) {
        List<String> findValueFieldBySemantic = this.findValuesFieldBySemantic(fieldSet, propertyDefinitionFieldSetItemModel, semantic);
        return findValueFieldBySemantic != null && !findValueFieldBySemantic.isEmpty() ? findValueFieldBySemantic.getFirst(): null;
    }

    private List<String> findValuesFieldBySemantic(FieldSetModel fieldSet, org.opencdmp.commonmodels.models.plandescriptionfields.PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel, String semantic){
        FieldModel field = this.templateFieldSearcherService.findFieldBySemantic(fieldSet, semantic);
        List<String> values = new ArrayList<>();
        if (field != null) {
            org.opencdmp.commonmodels.models.plandescriptionfields.FieldModel valueField = propertyDefinitionFieldSetItemModel.getFields().getOrDefault(field.getId(), null);
            if (valueField != null) {
                switch (field.getData().getFieldType()) {
                    case FREE_TEXT, TEXT_AREA, RICH_TEXT_AREA -> {
                        if (valueField.getTextValue() != null && !valueField.getTextValue().isBlank()) values.add(valueField.getTextValue());
                    }
                    case BOOLEAN_DECISION, CHECK_BOX -> {
                        if (valueField.getBooleanValue() != null) values.add(valueField.getBooleanValue().toString());
                    }
                    case NUMBER -> {
                        if (valueField.getNumberValue() != null) values.add(String.valueOf(valueField.getNumberValue()));
                    }
                    case DATE_PICKER -> {
                        try {
                            if (valueField.getDateValue() != null) values.add((DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC).format(valueField.getDateValue())));
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                    case DATASET_IDENTIFIER, VALIDATION -> {
                        if (valueField.getExternalIdentifier() != null && valueField.getExternalIdentifier().getIdentifier() != null && !valueField.getExternalIdentifier().getIdentifier().isBlank()) {
                            values.add(valueField.getExternalIdentifier().getIdentifier());
                        }
                    }
                    case TAGS -> {
                        if (valueField.getTextListValue() != null && !valueField.getTextListValue().isEmpty()) {
                            values.add((valueField.getTextListValue().getFirst()));
                        }
                    }
                    case SELECT -> {
                        if (valueField.getTextListValue() != null && !valueField.getTextListValue().isEmpty()) {
                            SelectDataModel selectDataModel = (SelectDataModel)field.getData();
                            if (selectDataModel != null && selectDataModel.getOptions() != null && !selectDataModel.getOptions().isEmpty()){
                                for (SelectDataModel.OptionModel option : selectDataModel.getOptions()){
                                    if (valueField.getTextListValue().contains(option.getValue()) || valueField.getTextListValue().contains(option.getLabel())) values.add(option.getValue());
                                }
                            }
                        }
                    }
                    case RADIO_BOX -> {
                        if (valueField.getTextListValue() != null && !valueField.getTextListValue().isEmpty()) {
                            RadioBoxDataModel radioBoxModel = (RadioBoxDataModel)field.getData();
                            if (radioBoxModel != null && radioBoxModel.getOptions() != null && !radioBoxModel.getOptions().isEmpty()){
                                for (RadioBoxDataModel.RadioBoxOptionModel option : radioBoxModel.getOptions()){
                                    if (valueField.getTextListValue().contains(option.getValue()) || valueField.getTextListValue().contains(option.getLabel())) values.add(option.getValue());
                                }
                            }
                        }
                        if (valueField.getTextValue() != null && !valueField.getTextValue().isEmpty()) {
                            RadioBoxDataModel radioBoxModel = (RadioBoxDataModel)field.getData();
                            if (radioBoxModel != null && radioBoxModel.getOptions() != null && !radioBoxModel.getOptions().isEmpty()){
                                for (RadioBoxDataModel.RadioBoxOptionModel option : radioBoxModel.getOptions()){
                                    if (valueField.getTextValue().equalsIgnoreCase(option.getValue()) || valueField.getTextValue().equalsIgnoreCase(option.getLabel())) values.add(option.getValue());
                                }
                            }
                        }
                    }
                    case REFERENCE_TYPES -> {
                        if (valueField.getReferences() != null && !valueField.getReferences().isEmpty()) {
                            for (ReferenceModel referenceModel : valueField.getReferences()) {
                                if (referenceModel == null) continue;
                                if (referenceModel.getReference() != null && !referenceModel.getReference().isBlank()) {
                                    values.add(referenceModel.getReference());
                                }
                            }
                        }
                    }
                }
            }
        }
        return !values.isEmpty() ? values: null;
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

            if (raid.getTitles() != null) {
                Title title = raid.getTitles().stream().filter(x -> x != null && x.getText() != null && !x.getText().isBlank() && x.getType() != null && TitleTypeId.Primary.equals(x.getType().getId())).findFirst()
                        .orElse(raid.getTitles().stream().filter(x -> x != null && x.getText() != null && !x.getText().isBlank()).findFirst().orElse(null));
                if (title != null) model.setLabel(title.getText());
            }

            if (raid.getAccess() != null && raid.getAccess().getType() != null && raid.getAccess().getType().getId() != null && raid.getAccess().getType().getId().equals(AccessTypeId.OpenAccess)) model.setAccessType(PlanAccessType.Public);
            else if (raid.getAccess() != null && raid.getAccess().getType() != null && raid.getAccess().getType().getId() != null && raid.getAccess().getType().getId().equals(AccessTypeId.EmbargoedAccess)) model.setAccessType(PlanAccessType.Restricted);
            else model.setAccessType(PlanAccessType.Restricted);

            if (planImportModel.getBlueprintModel() != null) {
                model.setProperties(buildPlanPropertiesModel(planImportModel.getBlueprintModel(), raid));
                model.setPlanBlueprint(planImportModel.getBlueprintModel());
            }

        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }

        return model;
    }

    private PlanPropertiesModel buildPlanPropertiesModel(PlanBlueprintModel planBlueprintModel, Raid raid) {
        PlanPropertiesModel planPropertiesModel = new PlanPropertiesModel();
        if (raid == null) return planPropertiesModel;

        if (planBlueprintModel == null) throw new MyApplicationException("planBlueprintModel is missing");
        if (planBlueprintModel.getDefinition() == null) return planPropertiesModel;

        Map<String, PropertyDefinitionFieldSetModel> fieldSets = new HashMap<>();

        if (raid.getIdentifier() != null) fieldSets.putAll(this.buildPlanBlueprintValuesByRaidIdentifier(planBlueprintModel, raid.getIdentifier()));
        if (raid.getDate() != null) fieldSets.putAll(buildPlanBlueprintValuesByRaidDate(planBlueprintModel, raid.getDate()));
        if (raid.getAccess() != null) fieldSets.putAll(buildPlanBlueprintValuesByRaidAccess(planBlueprintModel, raid.getAccess()));

        for (FieldSetModel fieldSetModel: planBlueprintModel.getDefinition().getAllFieldSets()){
            if (!fieldSets.containsKey(fieldSetModel.getId())){
                Map<String, org.opencdmp.commonmodels.models.plandescriptionfields.FieldModel> fieldsMap = new HashMap<>();
                for (org.opencdmp.commonmodels.models.planblueprintdescriptiontemplatefields.FieldModel templateFieldModel: fieldSetModel.getFields()){
                    org.opencdmp.commonmodels.models.plandescriptionfields.FieldModel fieldModel = new org.opencdmp.commonmodels.models.plandescriptionfields.FieldModel();
                    fieldModel.setId(templateFieldModel.getId());
                    fieldsMap.put(templateFieldModel.getId(), fieldModel);
                }

                PropertyDefinitionFieldSetModel propertyDefinitionFieldSetModel = new PropertyDefinitionFieldSetModel();
                propertyDefinitionFieldSetModel.setItems(new ArrayList<>());

                PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel = new PropertyDefinitionFieldSetItemModel();
                propertyDefinitionFieldSetItemModel.setFields(fieldsMap);
                propertyDefinitionFieldSetItemModel.setOrdinal(propertyDefinitionFieldSetModel.getItems().size());

                propertyDefinitionFieldSetModel.getItems().add(propertyDefinitionFieldSetItemModel);

                fieldSets.put(fieldSetModel.getId(), propertyDefinitionFieldSetModel);
            }
        }

        planPropertiesModel.setFieldSets(fieldSets);

        return planPropertiesModel;
    }

    private Map<String, PropertyDefinitionFieldSetModel> buildPlanBlueprintValuesByRaidIdentifier(PlanBlueprintModel planBlueprintModel, Identifier identifier){
        Map<String, PropertyDefinitionFieldSetModel> fieldSetModelMap = new HashMap<>();

        if (planBlueprintModel == null) throw new MyApplicationException("planBlueprintModel is missing");

        if (identifier == null) return fieldSetModelMap;

        List<FieldSetModel> fieldSetsWithSemantics = this.blueprintFieldSearcherService.searchFieldSetsBySemantics(planBlueprintModel, List.of(SEMANTIC_IDENTIFIER_ID, SEMANTIC_IDENTIFIER_SCHEMA_URI, SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_ID, SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_SCHEMA_URI,
                SEMANTIC_IDENTIFIER_OWNER_ID, SEMANTIC_IDENTIFIER_OWNER_SCHEMA_URI, SEMANTIC_IDENTIFIER_OWNER_SERVICE_POINT, SEMANTIC_IDENTIFIER_LICENSE, SEMANTIC_IDENTIFIER_VERSION));
        if (!fieldSetsWithSemantics.isEmpty()){

            for (FieldSetModel templateFieldSetModel: fieldSetsWithSemantics) {
                PropertyDefinitionFieldSetModel propertyDefinitionFieldSetModel = fieldSetModelMap.getOrDefault(templateFieldSetModel.getId(), null);
                if(propertyDefinitionFieldSetModel == null) {
                    propertyDefinitionFieldSetModel = new PropertyDefinitionFieldSetModel();
                    propertyDefinitionFieldSetModel.setItems(new ArrayList<>());
                    fieldSetModelMap.put(templateFieldSetModel.getId(), propertyDefinitionFieldSetModel);
                }

                PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel = new PropertyDefinitionFieldSetItemModel();
                Map<String, org.opencdmp.commonmodels.models.plandescriptionfields.FieldModel> fieldsMap = new HashMap<>();
                for (FieldModel templateFieldModel: templateFieldSetModel.getFields()) {
                    if (templateFieldModel.getSemantics() != null && !templateFieldModel.getSemantics().isEmpty()) {
                        boolean valueFound = false;
                        org.opencdmp.commonmodels.models.plandescriptionfields.FieldModel fieldModel = new org.opencdmp.commonmodels.models.plandescriptionfields.FieldModel();
                        fieldModel.setId(templateFieldModel.getId());
                        if (identifier.getId() != null && templateFieldModel.getSemantics().contains(SEMANTIC_IDENTIFIER_ID)){
                            fieldModel = this.buildFieldModel(fieldModel, templateFieldModel, identifier.getId(), null, null, null);
                            valueFound = true;
                        }
                        if (identifier.getSchemaUri() != null && templateFieldModel.getSemantics().contains(SEMANTIC_IDENTIFIER_SCHEMA_URI)){
                            fieldModel = this.buildFieldModel(fieldModel, templateFieldModel, identifier.getSchemaUri().value(), null, null, null);
                            valueFound = true;
                        }

                        if (identifier.getRegistrationAgency() != null) {
                            if (identifier.getRegistrationAgency().getId() != null && templateFieldModel.getSemantics().contains(SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_ID)){
                                fieldModel = this.buildFieldModel(fieldModel, templateFieldModel, identifier.getRegistrationAgency().getId(), null, null, null);
                                valueFound = true;
                            }
                            if (identifier.getRegistrationAgency().getSchemaUri() != null && templateFieldModel.getSemantics().contains(SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_SCHEMA_URI)){
                                fieldModel = this.buildFieldModel(fieldModel, templateFieldModel, identifier.getRegistrationAgency().getSchemaUri().value(), null, null, null);
                                valueFound = true;
                            }
                        }

                        if (identifier.getOwner() != null) {
                            if (identifier.getOwner().getId() != null && templateFieldModel.getSemantics().contains(SEMANTIC_IDENTIFIER_OWNER_ID)){
                                fieldModel = this.buildFieldModel(fieldModel, templateFieldModel, identifier.getOwner().getId(), null, null, null);
                                valueFound = true;
                            }
                            if (identifier.getOwner().getSchemaUri() != null && templateFieldModel.getSemantics().contains(SEMANTIC_IDENTIFIER_OWNER_SCHEMA_URI)){
                                fieldModel = this.buildFieldModel(fieldModel, templateFieldModel, identifier.getOwner().getSchemaUri().value(), null, null, null);
                                valueFound = true;
                            }
                            if (identifier.getOwner().getServicePoint() != null && templateFieldModel.getSemantics().contains(SEMANTIC_IDENTIFIER_OWNER_SERVICE_POINT)){
                                fieldModel = this.buildFieldModel(fieldModel, templateFieldModel, String.valueOf(identifier.getOwner().getServicePoint()), null, null, null);
                                valueFound = true;
                            }
                        }

                        if (identifier.getLicense() != null && templateFieldModel.getSemantics().contains(SEMANTIC_IDENTIFIER_LICENSE)){
                            fieldModel = this.buildFieldModel(fieldModel, templateFieldModel, identifier.getLicense().value(), null, null, null);
                            valueFound = true;
                        }
                        if (identifier.getVersion() != null && templateFieldModel.getSemantics().contains(SEMANTIC_IDENTIFIER_VERSION)){
                            fieldModel = this.buildFieldModel(fieldModel, templateFieldModel, identifier.getVersion(), null, null, null);
                            valueFound = true;
                        }

                        if (valueFound) fieldsMap.put(templateFieldModel.getId(), fieldModel);
                    }
                }
                if (propertyDefinitionFieldSetModel.getItems() == null){
                    propertyDefinitionFieldSetModel.setItems(new ArrayList<>());
                }
                propertyDefinitionFieldSetItemModel.setFields(fieldsMap);
                propertyDefinitionFieldSetItemModel.setOrdinal(propertyDefinitionFieldSetModel.getItems().size());
                propertyDefinitionFieldSetModel.getItems().add(propertyDefinitionFieldSetItemModel);

                fieldSetModelMap.put(templateFieldSetModel.getId(), propertyDefinitionFieldSetModel);
            }


        }

        return fieldSetModelMap;
    }

    private Map<String, PropertyDefinitionFieldSetModel> buildPlanBlueprintValuesByRaidDate(PlanBlueprintModel planBlueprintModel, Date date) {
        Map<String, PropertyDefinitionFieldSetModel> fieldSetModelMap = new HashMap<>();

        if (planBlueprintModel == null) throw new MyApplicationException("planBlueprintModel is missing");

        if (date == null) return fieldSetModelMap;

        List<FieldSetModel> fieldSetsWithSemantics = this.blueprintFieldSearcherService.searchFieldSetsBySemantics(planBlueprintModel, List.of(SEMANTIC_DATE_START_DATE, SEMANTIC_DATE_END_DATE));
        if (!fieldSetsWithSemantics.isEmpty()){

            for (FieldSetModel templateFieldSetModel: fieldSetsWithSemantics) {
                PropertyDefinitionFieldSetModel propertyDefinitionFieldSetModel = fieldSetModelMap.getOrDefault(templateFieldSetModel.getId(), null);
                if(propertyDefinitionFieldSetModel == null) {
                    propertyDefinitionFieldSetModel = new PropertyDefinitionFieldSetModel();
                    propertyDefinitionFieldSetModel.setItems(new ArrayList<>());
                    fieldSetModelMap.put(templateFieldSetModel.getId(), propertyDefinitionFieldSetModel);
                }

                PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel = new PropertyDefinitionFieldSetItemModel();
                Map<String, org.opencdmp.commonmodels.models.plandescriptionfields.FieldModel> fieldsMap = new HashMap<>();
                for (FieldModel templateFieldModel: templateFieldSetModel.getFields()) {
                    if (templateFieldModel.getSemantics() != null && !templateFieldModel.getSemantics().isEmpty()) {
                        boolean valueFound = false;
                        org.opencdmp.commonmodels.models.plandescriptionfields.FieldModel fieldModel = new org.opencdmp.commonmodels.models.plandescriptionfields.FieldModel();
                        fieldModel.setId(templateFieldModel.getId());
                        if (date.getStartDate() != null && templateFieldModel.getSemantics().contains(SEMANTIC_DATE_START_DATE)){
                            fieldModel = this.buildFieldModel(fieldModel, templateFieldModel, date.getStartDate(), null, null, null);
                            valueFound = true;
                        }
                        if (date.getEndDate() != null && templateFieldModel.getSemantics().contains(SEMANTIC_DATE_END_DATE)){
                            fieldModel = this.buildFieldModel(fieldModel, templateFieldModel, date.getEndDate(), null, null, null);
                            valueFound = true;
                        }

                        if (valueFound) fieldsMap.put(templateFieldModel.getId(), fieldModel);
                    }
                }
                if (propertyDefinitionFieldSetModel.getItems() == null){
                    propertyDefinitionFieldSetModel.setItems(new ArrayList<>());
                }
                propertyDefinitionFieldSetItemModel.setFields(fieldsMap);
                propertyDefinitionFieldSetItemModel.setOrdinal(propertyDefinitionFieldSetModel.getItems().size());
                propertyDefinitionFieldSetModel.getItems().add(propertyDefinitionFieldSetItemModel);

                fieldSetModelMap.put(templateFieldSetModel.getId(), propertyDefinitionFieldSetModel);
            }


        }

        return fieldSetModelMap;
    }

    private Map<String, PropertyDefinitionFieldSetModel> buildPlanBlueprintValuesByRaidAccess(PlanBlueprintModel planBlueprintModel, Access access){
        Map<String, PropertyDefinitionFieldSetModel> fieldSetModelMap = new HashMap<>();

        if (planBlueprintModel == null) throw new MyApplicationException("planBlueprintModel is missing");

        if (access == null) return fieldSetModelMap;

        List<FieldSetModel> fieldSetsWithSemantics = this.blueprintFieldSearcherService.searchFieldSetsBySemantics(planBlueprintModel, List.of(SEMANTIC_ACCESS_EMBARGO_EXPIRY, SEMANTIC_ACCESS_TYPE_ID, SEMANTIC_ACCESS_TYPE_SCHEMA_URI, SEMANTIC_ACCESS_STATEMENT_TEXT,
                SEMANTIC_ACCESS_STATEMENT_LANGUAGE_ID, SEMANTIC_ACCESS_STATEMENT_LANGUAGE_SCHEMA_URI));
        if (!fieldSetsWithSemantics.isEmpty()){

            for (FieldSetModel templateFieldSetModel: fieldSetsWithSemantics) {
                PropertyDefinitionFieldSetModel propertyDefinitionFieldSetModel = fieldSetModelMap.getOrDefault(templateFieldSetModel.getId(), null);
                if(propertyDefinitionFieldSetModel == null) {
                    propertyDefinitionFieldSetModel = new PropertyDefinitionFieldSetModel();
                    propertyDefinitionFieldSetModel.setItems(new ArrayList<>());
                    fieldSetModelMap.put(templateFieldSetModel.getId(), propertyDefinitionFieldSetModel);
                }

                PropertyDefinitionFieldSetItemModel propertyDefinitionFieldSetItemModel = new PropertyDefinitionFieldSetItemModel();
                Map<String, org.opencdmp.commonmodels.models.plandescriptionfields.FieldModel> fieldsMap = new HashMap<>();
                for (FieldModel templateFieldModel: templateFieldSetModel.getFields()) {
                    if (templateFieldModel.getSemantics() != null && !templateFieldModel.getSemantics().isEmpty()) {
                        boolean valueFound = false;
                        org.opencdmp.commonmodels.models.plandescriptionfields.FieldModel fieldModel = new org.opencdmp.commonmodels.models.plandescriptionfields.FieldModel();
                        fieldModel.setId(templateFieldModel.getId());
                        if (access.getEmbargoExpiry() != null && templateFieldModel.getSemantics().contains(SEMANTIC_ACCESS_EMBARGO_EXPIRY)){
                            fieldModel = this.buildFieldModel(fieldModel, templateFieldModel, access.getEmbargoExpiry(), null, null, null);
                            valueFound = true;
                        }
                        if (access.getType() != null) {
                            if (access.getType().getId() != null && templateFieldModel.getSemantics().contains(SEMANTIC_ACCESS_TYPE_ID)){
                                fieldModel = this.buildFieldModel(fieldModel, templateFieldModel, access.getType().getId().value(), null, null, null);
                                valueFound = true;
                            }
                            if (access.getType().getSchemaUri() != null && templateFieldModel.getSemantics().contains(SEMANTIC_ACCESS_TYPE_SCHEMA_URI)){
                                fieldModel = this.buildFieldModel(fieldModel, templateFieldModel, access.getType().getSchemaUri().value(), null, null, null);
                                valueFound = true;
                            }
                        }
                        if (access.getStatement() != null) {
                            if (access.getStatement().getText() != null && templateFieldModel.getSemantics().contains(SEMANTIC_ACCESS_STATEMENT_TEXT)){
                                fieldModel = this.buildFieldModel(fieldModel, templateFieldModel, access.getStatement().getText(), null, null, null);
                                valueFound = true;
                            }
                            if (access.getStatement().getLanguage() != null) {
                                if (access.getStatement().getLanguage().getId() != null && templateFieldModel.getSemantics().contains(SEMANTIC_ACCESS_STATEMENT_LANGUAGE_ID)){
                                    fieldModel = this.buildFieldModel(fieldModel, templateFieldModel, access.getStatement().getLanguage().getId().value(), null, null, null);
                                    valueFound = true;
                                }
                                if (access.getStatement().getLanguage().getSchemaUri() != null && templateFieldModel.getSemantics().contains(SEMANTIC_ACCESS_STATEMENT_LANGUAGE_SCHEMA_URI)){
                                    fieldModel = this.buildFieldModel(fieldModel, templateFieldModel, access.getStatement().getLanguage().getSchemaUri().value(), null, null, null);
                                    valueFound = true;
                                }
                            }
                        }

                        if (valueFound) fieldsMap.put(templateFieldModel.getId(), fieldModel);
                    }
                }
                if (propertyDefinitionFieldSetModel.getItems() == null){
                    propertyDefinitionFieldSetModel.setItems(new ArrayList<>());
                }
                propertyDefinitionFieldSetItemModel.setFields(fieldsMap);
                propertyDefinitionFieldSetItemModel.setOrdinal(propertyDefinitionFieldSetModel.getItems().size());
                propertyDefinitionFieldSetModel.getItems().add(propertyDefinitionFieldSetItemModel);

                fieldSetModelMap.put(templateFieldSetModel.getId(), propertyDefinitionFieldSetModel);
            }


        }

        return fieldSetModelMap;
    }


    //endregion

    private org.opencdmp.commonmodels.models.plandescriptionfields.FieldModel buildFieldModel(org.opencdmp.commonmodels.models.plandescriptionfields.FieldModel fieldModel, FieldModel templateFieldModel, String textValue, java.util.Date dateValue, Boolean booleanValue, List<String> textListValue){
        return this.buildFieldModel(fieldModel, templateFieldModel, textValue, dateValue, booleanValue, textListValue, null);
    }

    private org.opencdmp.commonmodels.models.plandescriptionfields.FieldModel buildFieldModel(org.opencdmp.commonmodels.models.plandescriptionfields.FieldModel fieldModel, FieldModel templateFieldModel, String textValue, java.util.Date dateValue, Boolean booleanValue, List<String> textListValue, Double numberValue){
        if (templateFieldModel == null || templateFieldModel.getData() == null) return fieldModel;

        switch (templateFieldModel.getData().getFieldType()) {
            case FREE_TEXT, TEXT_AREA, RICH_TEXT_AREA -> {
                if (textValue != null && !textValue.isBlank()) fieldModel.setTextValue(textValue);
                else if (booleanValue != null) fieldModel.setTextValue(booleanValue.toString());
                else if (dateValue != null) fieldModel.setTextValue(dateValue.toString());
                else if (textListValue != null && !textListValue.isEmpty()) fieldModel.setTextValue(textListValue.getFirst());
                else if (numberValue != null) fieldModel.setTextValue(String.valueOf(numberValue));
            }
            case BOOLEAN_DECISION, CHECK_BOX -> {
                if (booleanValue != null) fieldModel.setBooleanValue(booleanValue);
                if (textValue != null && !textValue.isBlank()) {
                    if (textValue.equalsIgnoreCase("true")) fieldModel.setBooleanValue(true);
                    else if (textValue.equalsIgnoreCase("false")) fieldModel.setBooleanValue(false);
                }
            }
            case NUMBER -> {
                if (numberValue != null) fieldModel.setNumberValue(numberValue);
                if (textValue != null && !textValue.isBlank()) {
                    try {
                        fieldModel.setNumberValue(Double.valueOf(textValue));
                    } catch (Exception e) {
                        logger.warn("invalid double parse from value: " + textValue);
                    }
                }
                if (textListValue != null && !textListValue.isEmpty()) {
                    try {
                        fieldModel.setNumberValue(Double.valueOf(textListValue.getFirst()));
                    } catch (Exception e) {
                        logger.warn("invalid double parse from value: " + textListValue.getFirst());
                    }
                }
            }
            case DATE_PICKER -> {
                if (dateValue != null) fieldModel.setDateValue(dateValue.toInstant());
                else if (textValue != null && !textValue.isBlank()) {
                    try {
                        fieldModel.setDateValue(Instant.parse(textValue));
                    } catch (Exception e) {
                        try {
                            fieldModel.setDateValue(LocalDate.parse(textValue, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
                        } catch (Exception ex) {
                            logger.warn("invalid instant parse from value: " + textValue);
                        }
                    }

                }
            }
            case DATASET_IDENTIFIER, VALIDATION -> {
                if (textValue != null && !textValue.isBlank()) {
                    ExternalIdentifierModel externalIdentifierModel = new ExternalIdentifierModel();
                    externalIdentifierModel.setIdentifier(textValue);
                    if (templateFieldModel.getData().getFieldType() == FieldType.DATASET_IDENTIFIER) externalIdentifierModel.setType("other");
                    fieldModel.setExternalIdentifier(externalIdentifierModel);
                }
            }
            case TAGS -> {
                if (textListValue != null && !textListValue.isEmpty()) {
                    fieldModel.setTextListValue(textListValue);
                } else if (textValue != null && !textValue.isBlank()) {
                    fieldModel.setTextListValue(List.of(textValue));
                }
            }
            case SELECT -> {
                SelectDataModel selectDataModel = (SelectDataModel) templateFieldModel.getData();
                if (selectDataModel != null && selectDataModel.getOptions() != null && !selectDataModel.getOptions().isEmpty()){
                    if (textListValue != null && !textListValue.isEmpty()) {
                        fieldModel.setTextListValue(textListValue);
                    } else if (textValue != null && !textValue.isBlank()) {
                        fieldModel.setTextListValue(List.of(textValue));
                    }
                }

            }
            case RADIO_BOX -> {
                if (textValue != null && !textValue.isBlank()) {
                    RadioBoxDataModel radioBoxModel = (RadioBoxDataModel)templateFieldModel.getData();
                    if (radioBoxModel != null && radioBoxModel.getOptions() != null && !radioBoxModel.getOptions().isEmpty()){
                        for (RadioBoxDataModel.RadioBoxOptionModel option : radioBoxModel.getOptions()){
                            if (option.getValue() != null && option.getValue().equalsIgnoreCase(textValue)) {
                                fieldModel.setTextValue(option.getValue());
                                break;
                            } else if (option.getLabel() != null && option.getLabel().equalsIgnoreCase(textValue)) {
                                fieldModel.setTextValue(option.getValue());
                                break;
                            }
                        }
                    }
                }
            }
            case REFERENCE_TYPES -> {
                if (textValue != null && !textValue.isBlank()) {
                    ReferenceModel referenceModel = new ReferenceModel();
                    ReferenceTypeDataModel referenceTypeDataModel = (ReferenceTypeDataModel) templateFieldModel.getData();
                    if (referenceTypeDataModel != null) referenceModel.setType(referenceTypeDataModel.getReferenceType());
                    referenceModel.setReference(textValue);
                    referenceModel.setLabel(textValue);

                    fieldModel.setReferences(List.of(referenceModel));
                }
            }
        }


        return fieldModel;
    }

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
        configuration.setSemantics(RaidSemanticsManifest.get());
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
        } catch (JacksonException e) {
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
