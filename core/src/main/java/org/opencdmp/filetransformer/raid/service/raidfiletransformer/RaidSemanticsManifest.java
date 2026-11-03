package org.opencdmp.filetransformer.raid.service.raidfiletransformer;

import org.opencdmp.commonmodels.enums.PluginEntityType;
import org.opencdmp.commonmodels.enums.SemanticKeyLevel;
import org.opencdmp.commonmodels.enums.SemanticValueFormat;
import org.opencdmp.commonmodels.models.semantics.SemanticKeyUsage;
import org.opencdmp.commonmodels.models.semantics.SemanticObject;
import org.opencdmp.commonmodels.models.semantics.SemanticVocabulary;
import org.opencdmp.commonmodels.models.semantics.SemanticsManifest;
import org.opencdmp.filetransformer.raid.enums.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.opencdmp.filetransformer.raid.service.raidfiletransformer.RaidFileTransformerService.*;

/**
 * The semantics manifest the RAiD transformer publishes with its configuration: one usage per key it reads.
 * <p>
 * The export searches both the plan's blueprint fields and the templates of the plan's descriptions for every key, so
 * every usage and object applies to plans and descriptions alike. A key is read once per answer row of the question
 * (or blueprint field set) carrying it, and each row that answers any of an object's keys becomes one object in the
 * export, so every value is single inside its object while the list objects hold one entry per row.
 * <p>
 * A value is "needs" when RAiD requires it and the export has nothing to put in its place; everything else it reads is
 * "can use".
 */
public final class RaidSemanticsManifest {

    public static final String PREFIX = "raid";

    private static final List<PluginEntityType> ENTITY_TYPES = List.of(PluginEntityType.Plan, PluginEntityType.Description);

    private static final String OBJECT_IDENTIFIER = "raid.identifier";
    private static final String OBJECT_DATE = "raid.date";
    private static final String OBJECT_ACCESS = "raid.access";
    private static final String OBJECT_TITLE = "raid.title";
    private static final String OBJECT_DESCRIPTION = "raid.description";
    private static final String OBJECT_CONTRIBUTOR = "raid.contributor";
    private static final String OBJECT_ORGANISATION = "raid.organisation";
    private static final String OBJECT_RELATED_OBJECT = "raid.relatedObject";
    private static final String OBJECT_ALTERNATE_IDENTIFIER = "raid.alternateIdentifier";
    private static final String OBJECT_ALTERNATE_URL = "raid.alternateUrl";
    private static final String OBJECT_RELATED_RAID = "raid.relatedRaid";

    private static final String NEEDS = "Required by RAiD; unanswered, the export leaves it out and the record fails the schema.";
    private static final String NEEDS_DATE = "Required by RAiD; unanswered, the export leaves it out and the record fails the schema. Given as YYYY, YYYY-MM or YYYY-MM-DD.";
    private static final String WRITTEN = "Written to the RAiD export when answered.";
    private static final String WRITTEN_CHOICE = "Written to the RAiD export when answered with one of the accepted values; other answers are dropped.";
    private static final String WRITTEN_DATE = "Written to the RAiD export when answered as YYYY, YYYY-MM or YYYY-MM-DD; other answers are dropped.";
    private static final String TITLE = "Written as an additional title when answered; the plan title is always written as the primary title.";
    private static final String TITLE_CHOICE = "Written as part of an additional title when answered with one of the accepted values; the plan title is always written as the primary title.";
    private static final String TITLE_DATE = "Written as part of an additional title when answered as YYYY, YYYY-MM or YYYY-MM-DD; the plan title is always written as the primary title.";
    private static final String FLAG = "Sets the flag when answered \"true\" or \"Yes\"; any other answer leaves it out.";

    private static final SemanticsManifest MANIFEST = build();

    private RaidSemanticsManifest() {
    }

    public static SemanticsManifest get() {
        return MANIFEST;
    }

    private static SemanticsManifest build() {
        List<SemanticKeyUsage> usages = new ArrayList<>();

        // identifier, date and access are written back to blueprint fields on import
        usages.add(imported(usage(SEMANTIC_IDENTIFIER_ID, OBJECT_IDENTIFIER, SemanticKeyLevel.CanUse, "Identifier", WRITTEN, "identifier.id", SemanticValueFormat.Uri)));
        usages.add(imported(choice(SEMANTIC_IDENTIFIER_SCHEMA_URI, OBJECT_IDENTIFIER, SemanticKeyLevel.CanUse, "Identifier › Schema URI", WRITTEN_CHOICE, "identifier.schemaUri", values(IdentifierSchemaUri.values(), IdentifierSchemaUri::value))));
        usages.add(imported(usage(SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_ID, OBJECT_IDENTIFIER, SemanticKeyLevel.CanUse, "Identifier › Registration agency", WRITTEN, "identifier.registrationAgency.id", SemanticValueFormat.Uri)));
        usages.add(imported(choice(SEMANTIC_IDENTIFIER_REGISTRATION_AGENCY_SCHEMA_URI, OBJECT_IDENTIFIER, SemanticKeyLevel.CanUse, "Identifier › Registration agency › Schema URI", WRITTEN_CHOICE, "identifier.registrationAgency.schemaUri", values(IdentifierRegistrationAgencySchemaUri.values(), IdentifierRegistrationAgencySchemaUri::value))));
        usages.add(imported(usage(SEMANTIC_IDENTIFIER_OWNER_ID, OBJECT_IDENTIFIER, SemanticKeyLevel.CanUse, "Identifier › Owner", WRITTEN, "identifier.owner.id", SemanticValueFormat.Uri)));
        // RAiD allows only ROR for the owner, the same vocabulary as the registration agency
        usages.add(imported(choice(SEMANTIC_IDENTIFIER_OWNER_SCHEMA_URI, OBJECT_IDENTIFIER, SemanticKeyLevel.CanUse, "Identifier › Owner › Schema URI", WRITTEN_CHOICE, "identifier.owner.schemaUri", values(IdentifierRegistrationAgencySchemaUri.values(), IdentifierRegistrationAgencySchemaUri::value))));
        usages.add(imported(usage(SEMANTIC_IDENTIFIER_OWNER_SERVICE_POINT, OBJECT_IDENTIFIER, SemanticKeyLevel.CanUse, "Identifier › Owner › Service point", WRITTEN, "identifier.owner.servicePoint", SemanticValueFormat.Integer)));
        usages.add(imported(choice(SEMANTIC_IDENTIFIER_LICENSE, OBJECT_IDENTIFIER, SemanticKeyLevel.CanUse, "Identifier › License", WRITTEN_CHOICE, "identifier.license", values(IdentifierLicense.values(), IdentifierLicense::value))));
        usages.add(imported(usage(SEMANTIC_IDENTIFIER_VERSION, OBJECT_IDENTIFIER, SemanticKeyLevel.CanUse, "Identifier › Version", WRITTEN, "identifier.version", SemanticValueFormat.Text)));

        usages.add(imported(usage(SEMANTIC_DATE_START_DATE, OBJECT_DATE, SemanticKeyLevel.Needs, "Date › Start date", NEEDS_DATE, "date.startDate", SemanticValueFormat.Date)));
        usages.add(imported(usage(SEMANTIC_DATE_END_DATE, OBJECT_DATE, SemanticKeyLevel.CanUse, "Date › End date", WRITTEN_DATE, "date.endDate", SemanticValueFormat.Date)));

        usages.add(imported(choice(SEMANTIC_ACCESS_TYPE_ID, OBJECT_ACCESS, SemanticKeyLevel.Needs, "Access › Type", NEEDS, "access.type.id", values(AccessTypeId.values(), AccessTypeId::value))));
        usages.add(imported(choice(SEMANTIC_ACCESS_TYPE_SCHEMA_URI, OBJECT_ACCESS, SemanticKeyLevel.Needs, "Access › Type › Schema URI", NEEDS, "access.type.schemaUri", values(AccessTypeSchemaUri.values(), AccessTypeSchemaUri::value))));
        usages.add(imported(usage(SEMANTIC_ACCESS_EMBARGO_EXPIRY, OBJECT_ACCESS, SemanticKeyLevel.CanUse, "Access › Embargo expiry", "Required by RAiD when access is embargoed; written when answered as YYYY, YYYY-MM or YYYY-MM-DD.", "access.embargoExpiry", SemanticValueFormat.Date)));
        usages.add(imported(usage(SEMANTIC_ACCESS_STATEMENT_TEXT, OBJECT_ACCESS, SemanticKeyLevel.CanUse, "Access › Statement", "Required by RAiD when access is embargoed; written when answered.", "access.statement.text", SemanticValueFormat.Text)));
        usages.add(imported(choice(SEMANTIC_ACCESS_STATEMENT_LANGUAGE_ID, OBJECT_ACCESS, SemanticKeyLevel.CanUse, "Access › Statement › Language", WRITTEN_CHOICE, "access.statement.language.id", values(LanguageId.values(), LanguageId::value))));
        usages.add(imported(choice(SEMANTIC_ACCESS_STATEMENT_LANGUAGE_SCHEMA_URI, OBJECT_ACCESS, SemanticKeyLevel.CanUse, "Access › Statement › Language › Schema URI", WRITTEN_CHOICE, "access.statement.language.schemaUri", values(LanguageSchemaUri.values(), LanguageSchemaUri::value))));

        usages.add(usage(SEMANTIC_TITLE_TEXT, OBJECT_TITLE, SemanticKeyLevel.CanUse, "Title", TITLE, "titles[].text", SemanticValueFormat.Text));
        usages.add(choice(SEMANTIC_TITLE_TYPE_ID, OBJECT_TITLE, SemanticKeyLevel.CanUse, "Title › Type", TITLE_CHOICE, "titles[].type.id", values(TitleTypeId.values(), TitleTypeId::value)));
        usages.add(choice(SEMANTIC_TITLE_TYPE_SCHEMA_URI, OBJECT_TITLE, SemanticKeyLevel.CanUse, "Title › Type › Schema URI", TITLE_CHOICE, "titles[].type.schemaUri", values(TitleTypeSchemaUri.values(), TitleTypeSchemaUri::value)));
        usages.add(choice(SEMANTIC_TITLE_LANGUAGE_ID, OBJECT_TITLE, SemanticKeyLevel.CanUse, "Title › Language", TITLE_CHOICE, "titles[].language.id", values(LanguageId.values(), LanguageId::value)));
        usages.add(choice(SEMANTIC_TITLE_LANGUAGE_SCHEMA_URI, OBJECT_TITLE, SemanticKeyLevel.CanUse, "Title › Language › Schema URI", TITLE_CHOICE, "titles[].language.schemaUri", values(LanguageSchemaUri.values(), LanguageSchemaUri::value)));
        usages.add(usage(SEMANTIC_TITLE_START_DATE, OBJECT_TITLE, SemanticKeyLevel.CanUse, "Title › Start date", TITLE_DATE, "titles[].startDate", SemanticValueFormat.Date));
        usages.add(usage(SEMANTIC_TITLE_END_DATE, OBJECT_TITLE, SemanticKeyLevel.CanUse, "Title › End date", TITLE_DATE, "titles[].endDate", SemanticValueFormat.Date));

        usages.add(usage(SEMANTIC_DESCRIPTION_TEXT, OBJECT_DESCRIPTION, SemanticKeyLevel.CanUse, "Description", WRITTEN, "descriptions[].text", SemanticValueFormat.Text));
        usages.add(choice(SEMANTIC_DESCRIPTION_TYPE_ID, OBJECT_DESCRIPTION, SemanticKeyLevel.CanUse, "Description › Type", WRITTEN_CHOICE, "descriptions[].type.id", values(DescriptionTypeId.values(), DescriptionTypeId::value)));
        usages.add(choice(SEMANTIC_DESCRIPTION_TYPE_SCHEMA_URI, OBJECT_DESCRIPTION, SemanticKeyLevel.CanUse, "Description › Type › Schema URI", WRITTEN_CHOICE, "descriptions[].type.schemaUri", values(DescriptionTypeSchemaUri.values(), DescriptionTypeSchemaUri::value)));
        usages.add(choice(SEMANTIC_DESCRIPTION_LANGUAGE_ID, OBJECT_DESCRIPTION, SemanticKeyLevel.CanUse, "Description › Language", WRITTEN_CHOICE, "descriptions[].language.id", values(LanguageId.values(), LanguageId::value)));
        usages.add(choice(SEMANTIC_DESCRIPTION_LANGUAGE_SCHEMA_URI, OBJECT_DESCRIPTION, SemanticKeyLevel.CanUse, "Description › Language › Schema URI", WRITTEN_CHOICE, "descriptions[].language.schemaUri", values(LanguageSchemaUri.values(), LanguageSchemaUri::value)));

        usages.add(usage(SEMANTIC_CONTRIBUTOR_ID, OBJECT_CONTRIBUTOR, SemanticKeyLevel.Needs, "Contributor", NEEDS, "contributors[].id", SemanticValueFormat.Uri));
        usages.add(choice(SEMANTIC_CONTRIBUTOR_SCHEMA_URI, OBJECT_CONTRIBUTOR, SemanticKeyLevel.Needs, "Contributor › Schema URI", NEEDS, "contributors[].schemaUri", values(ContributorSchemaUri.values(), ContributorSchemaUri::value)));
        usages.add(choice(SEMANTIC_CONTRIBUTOR_POSITION_ID, OBJECT_CONTRIBUTOR, SemanticKeyLevel.Needs, "Contributor › Position", NEEDS, "contributors[].position[].id", values(ContributorPositionId.values(), ContributorPositionId::value)));
        usages.add(choice(SEMANTIC_CONTRIBUTOR_POSITION_SCHEMA_URI, OBJECT_CONTRIBUTOR, SemanticKeyLevel.Needs, "Contributor › Position › Schema URI", NEEDS, "contributors[].position[].schemaUri", values(ContributorPositionSchemaUri.values(), ContributorPositionSchemaUri::value)));
        usages.add(usage(SEMANTIC_CONTRIBUTOR_POSITION_START_DATE, OBJECT_CONTRIBUTOR, SemanticKeyLevel.Needs, "Contributor › Position › Start date", NEEDS_DATE, "contributors[].position[].startDate", SemanticValueFormat.Date));
        usages.add(usage(SEMANTIC_CONTRIBUTOR_POSITION_END_DATE, OBJECT_CONTRIBUTOR, SemanticKeyLevel.CanUse, "Contributor › Position › End date", WRITTEN_DATE, "contributors[].position[].endDate", SemanticValueFormat.Date));
        usages.add(usage(SEMANTIC_CONTRIBUTOR_LEADER, OBJECT_CONTRIBUTOR, SemanticKeyLevel.CanUse, "Contributor › Leader", FLAG, "contributors[].leader", SemanticValueFormat.Boolean));
        usages.add(usage(SEMANTIC_CONTRIBUTOR_CONTACT, OBJECT_CONTRIBUTOR, SemanticKeyLevel.CanUse, "Contributor › Contact", FLAG, "contributors[].contact", SemanticValueFormat.Boolean));
        usages.add(choice(SEMANTIC_CONTRIBUTOR_ROLE_ID, OBJECT_CONTRIBUTOR, SemanticKeyLevel.CanUse, "Contributor › Role", WRITTEN_CHOICE, "contributors[].role[].id", values(ContributorRoleId.values(), ContributorRoleId::value)));
        usages.add(choice(SEMANTIC_CONTRIBUTOR_ROLE_SCHEMA_URI, OBJECT_CONTRIBUTOR, SemanticKeyLevel.CanUse, "Contributor › Role › Schema URI", WRITTEN_CHOICE, "contributors[].role[].schemaUri", values(ContributorRoleSchemaUri.values(), ContributorRoleSchemaUri::value)));

        usages.add(usage(SEMANTIC_ORGANISATION_ID, OBJECT_ORGANISATION, SemanticKeyLevel.CanUse, "Organisation", WRITTEN, "organisations[].id", SemanticValueFormat.Uri));
        usages.add(choice(SEMANTIC_ORGANISATION_SCHEMA_URI, OBJECT_ORGANISATION, SemanticKeyLevel.CanUse, "Organisation › Schema URI", WRITTEN_CHOICE, "organisations[].schemaUri", values(OrganisationSchemaUri.values(), OrganisationSchemaUri::value)));
        usages.add(choice(SEMANTIC_ORGANISATION_ROLE_ID, OBJECT_ORGANISATION, SemanticKeyLevel.CanUse, "Organisation › Role", WRITTEN_CHOICE, "organisations[].role[].id", values(OrganisationRoleId.values(), OrganisationRoleId::value)));
        usages.add(choice(SEMANTIC_ORGANISATION_ROLE_SCHEMA_URI, OBJECT_ORGANISATION, SemanticKeyLevel.CanUse, "Organisation › Role › Schema URI", WRITTEN_CHOICE, "organisations[].role[].schemaUri", values(OrganisationRoleSchemaUri.values(), OrganisationRoleSchemaUri::value)));
        usages.add(usage(SEMANTIC_ORGANISATION_ROLE_START_DATE, OBJECT_ORGANISATION, SemanticKeyLevel.CanUse, "Organisation › Role › Start date", WRITTEN_DATE, "organisations[].role[].startDate", SemanticValueFormat.Date));
        usages.add(usage(SEMANTIC_ORGANISATION_ROLE_END_DATE, OBJECT_ORGANISATION, SemanticKeyLevel.CanUse, "Organisation › Role › End date", WRITTEN_DATE, "organisations[].role[].endDate", SemanticValueFormat.Date));

        usages.add(usage(SEMANTIC_RELATED_OBJECT_ID, OBJECT_RELATED_OBJECT, SemanticKeyLevel.CanUse, "Related object", WRITTEN, "relatedObjects[].id", SemanticValueFormat.Uri));
        usages.add(choice(SEMANTIC_RELATED_OBJECT_SCHEMA_URI, OBJECT_RELATED_OBJECT, SemanticKeyLevel.CanUse, "Related object › Schema URI", WRITTEN_CHOICE, "relatedObjects[].schemaUri", values(RelatedObjectSchemaUri.values(), RelatedObjectSchemaUri::value)));
        usages.add(choice(SEMANTIC_RELATED_OBJECT_TYPE_ID, OBJECT_RELATED_OBJECT, SemanticKeyLevel.CanUse, "Related object › Type", WRITTEN_CHOICE, "relatedObjects[].type.id", values(RelatedObjectTypeId.values(), RelatedObjectTypeId::value)));
        usages.add(choice(SEMANTIC_RELATED_OBJECT_TYPE_SCHEMA_URI, OBJECT_RELATED_OBJECT, SemanticKeyLevel.CanUse, "Related object › Type › Schema URI", WRITTEN_CHOICE, "relatedObjects[].type.schemaUri", values(RelatedObjectTypeSchemaUri.values(), RelatedObjectTypeSchemaUri::value)));
        usages.add(choice(SEMANTIC_RELATED_OBJECT_CATEGORY_ID, OBJECT_RELATED_OBJECT, SemanticKeyLevel.CanUse, "Related object › Category", WRITTEN_CHOICE, "relatedObjects[].category[].id", values(RelatedObjectCategoryId.values(), RelatedObjectCategoryId::value)));
        usages.add(choice(SEMANTIC_RELATED_OBJECT_CATEGORY_SCHEMA_URI, OBJECT_RELATED_OBJECT, SemanticKeyLevel.CanUse, "Related object › Category › Schema URI", WRITTEN_CHOICE, "relatedObjects[].category[].schemaUri", values(RelatedObjectCategorySchemaUri.values(), RelatedObjectCategorySchemaUri::value)));

        usages.add(usage(SEMANTIC_ALTERNATE_IDENTIFIER_ID, OBJECT_ALTERNATE_IDENTIFIER, SemanticKeyLevel.CanUse, "Alternate identifier", WRITTEN, "alternateIdentifiers[].id", SemanticValueFormat.Text));
        usages.add(usage(SEMANTIC_ALTERNATE_IDENTIFIER_TYPE, OBJECT_ALTERNATE_IDENTIFIER, SemanticKeyLevel.CanUse, "Alternate identifier › Type", WRITTEN, "alternateIdentifiers[].type", SemanticValueFormat.Text));

        usages.add(usage(SEMANTIC_ALTERNATE_URL_URL, OBJECT_ALTERNATE_URL, SemanticKeyLevel.CanUse, "Alternate URL", "Written to the RAiD export when answered with a valid URL; other answers are dropped.", "alternateUrls[].url", SemanticValueFormat.Uri));

        usages.add(usage(SEMANTIC_RELATED_RAID_ID, OBJECT_RELATED_RAID, SemanticKeyLevel.CanUse, "Related RAiD", WRITTEN, "relatedRaids[].id", SemanticValueFormat.Uri));
        usages.add(choice(SEMANTIC_RELATED_RAID_TYPE_ID, OBJECT_RELATED_RAID, SemanticKeyLevel.CanUse, "Related RAiD › Type", WRITTEN_CHOICE, "relatedRaids[].type.id", values(RelatedRaidTypeId.values(), RelatedRaidTypeId::value)));
        usages.add(choice(SEMANTIC_RELATED_RAID_TYPE_SCHEMA_URI, OBJECT_RELATED_RAID, SemanticKeyLevel.CanUse, "Related RAiD › Type › Schema URI", WRITTEN_CHOICE, "relatedRaids[].type.schemaUri", values(RelatedRaidTypeSchemaUri.values(), RelatedRaidTypeSchemaUri::value)));

        List<SemanticObject> objects = new ArrayList<>();
        // identifier, date and access are written on every export, answered or not
        objects.add(object(OBJECT_IDENTIFIER, "Identifier", false, true, "identifier"));
        objects.add(object(OBJECT_DATE, "Date", false, true, "date"));
        objects.add(object(OBJECT_ACCESS, "Access", false, true, "access"));
        objects.add(object(OBJECT_TITLE, "Title", true, false, "titles[]"));
        objects.add(object(OBJECT_DESCRIPTION, "Description", true, false, "descriptions[]"));
        // the export leaves contributors out when none is answered, but RAiD requires at least one, so a structure
        // without a contributor falls short either way: marking it always present makes its needed values count
        objects.add(object(OBJECT_CONTRIBUTOR, "Contributor", true, true, "contributors[]"));
        objects.add(object(OBJECT_ORGANISATION, "Organisation", true, false, "organisations[]"));
        objects.add(object(OBJECT_RELATED_OBJECT, "Related object", true, false, "relatedObjects[]"));
        objects.add(object(OBJECT_ALTERNATE_IDENTIFIER, "Alternate identifier", true, false, "alternateIdentifiers[]"));
        objects.add(object(OBJECT_ALTERNATE_URL, "Alternate URL", true, false, "alternateUrls[]"));
        objects.add(object(OBJECT_RELATED_RAID, "Related RAiD", true, false, "relatedRaids[]"));

        SemanticsManifest manifest = new SemanticsManifest(List.of(new SemanticVocabulary(PREFIX, "RAiD", true)), usages);
        manifest.setObjects(objects);
        return manifest;
    }

    private static SemanticKeyUsage usage(String key, String object, SemanticKeyLevel level, String label, String purpose, String target, SemanticValueFormat format) {
        SemanticKeyUsage usage = new SemanticKeyUsage(List.of(key), false, level, ENTITY_TYPES, label, purpose, target);
        usage.setFormat(format);
        usage.setMultiple(false);
        usage.setObject(object);
        // every key is read through the reference type of a reference input too
        usage.setFromReferenceFields(true);
        return usage;
    }

    private static SemanticKeyUsage choice(String key, String object, SemanticKeyLevel level, String label, String purpose, String target, List<String> values) {
        SemanticKeyUsage usage = usage(key, object, level, label, purpose, target, SemanticValueFormat.Choice);
        usage.setValues(values);
        return usage;
    }

    private static SemanticKeyUsage imported(SemanticKeyUsage usage) {
        usage.setImported(true);
        return usage;
    }

    private static <T extends Enum<T>> List<String> values(T[] constants, Function<T, String> value) {
        return Arrays.stream(constants).map(value).toList();
    }

    private static SemanticObject object(String key, String label, boolean multiple, boolean alwaysPresent, String target) {
        SemanticObject object = new SemanticObject();
        object.setKey(key);
        object.setLabel(label);
        object.setParent(null);
        object.setMultiple(multiple);
        object.setFromReference(false);
        object.setAlwaysPresent(alwaysPresent);
        object.setEntityTypes(ENTITY_TYPES);
        object.setTarget(target);
        return object;
    }
}
