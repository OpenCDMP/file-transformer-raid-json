package org.opencdmp.filetransformer.raid.service.raidfiletransformer;

import org.opencdmp.commonmodels.models.planblueprintdescriptiontemplatefields.FieldModel;
import org.opencdmp.commonmodels.models.planblueprintdescriptiontemplatefields.fielddata.ReferenceTypeDataModel;
import org.opencdmp.commonmodels.models.reference.ReferenceDefinitionModel;
import org.opencdmp.commonmodels.models.reference.ReferenceFieldModel;
import org.opencdmp.commonmodels.models.reference.ReferenceModel;
import org.opencdmp.commonmodels.models.referencetype.ReferenceTypeDefinitionModel;
import org.opencdmp.commonmodels.models.referencetype.ReferenceTypeFieldModel;
import org.opencdmp.commonmodels.models.referencetype.ReferenceTypeModel;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The keys a reference type gives the references picked in an input: their id, name and description (the type's
 * system fields) and the type's own fields can each carry keys, so an input without keys of its own still answers them.
 */
public final class ReferenceSemantics {

    private ReferenceSemantics() {
    }

    /** Does the input's reference type give the key to a system field or to one of its own fields? */
    public static boolean carries(FieldModel field, String key) {
        return field != null && field.getData() instanceof ReferenceTypeDataModel data && keys(data.getReferenceType()).contains(key);
    }

    /** The keys the reference type gives its id, name, description and fields. */
    public static Set<String> keys(ReferenceTypeModel type) {
        Set<String> keys = new HashSet<>();
        if (type == null || type.getDefinition() == null) return keys;
        ReferenceTypeDefinitionModel definition = type.getDefinition();
        if (definition.getReferenceIdSemantics() != null) keys.addAll(definition.getReferenceIdSemantics());
        if (definition.getLabelSemantics() != null) keys.addAll(definition.getLabelSemantics());
        if (definition.getDescriptionSemantics() != null) keys.addAll(definition.getDescriptionSemantics());
        if (definition.getFields() != null) definition.getFields().stream().filter(x -> x.getSemantics() != null).forEach(x -> keys.addAll(x.getSemantics()));
        return keys;
    }

    /**
     * The value a picked reference gives the key through its type: its id, name or description when the type gives
     * that system field the key, else the value of the reference's own field carrying it. Null when none does.
     */
    public static String value(ReferenceModel reference, ReferenceTypeModel type, String key) {
        if (reference == null || type == null || type.getDefinition() == null || key == null) return null;
        ReferenceTypeDefinitionModel definition = type.getDefinition();
        String value = null;
        if (contains(definition.getReferenceIdSemantics(), key)) value = reference.getReference();
        else if (contains(definition.getLabelSemantics(), key)) value = reference.getLabel();
        else if (contains(definition.getDescriptionSemantics(), key)) value = reference.getDescription();
        else if (definition.getFields() != null && reference.getDefinition() != null && reference.getDefinition().getFields() != null) {
            ReferenceTypeFieldModel typeField = definition.getFields().stream().filter(x -> x.getCode() != null && contains(x.getSemantics(), key)).findFirst().orElse(null);
            if (typeField != null) value = reference.getDefinition().getFields().stream().filter(x -> typeField.getCode().equals(x.getCode())).map(ReferenceFieldModel::getValue).findFirst().orElse(null);
        }
        return value != null && !value.isBlank() ? value : null;
    }

    /** Is the key one the import writes back (identifier, date and access: the objects a blueprint can hold)? */
    public static boolean imported(String key) {
        return key != null && IMPORTED_OBJECTS.stream().anyMatch(key::startsWith);
    }

    private static final List<String> IMPORTED_OBJECTS = List.of("raid.identifier.", "raid.date.", "raid.access.");

    /**
     * The reference the type makes of the imported RAiD record: the code from its id key, the name, description and
     * fields from its other keys, the name falling back to the code. Null when the record gives neither.
     */
    public static ReferenceModel reference(ReferenceTypeModel type, JsonNode raid) {
        if (type == null || type.getDefinition() == null || raid == null) return null;
        ReferenceTypeDefinitionModel definition = type.getDefinition();
        String code = firstValue(definition.getReferenceIdSemantics(), raid);
        String name = firstValue(definition.getLabelSemantics(), raid);
        if (code == null && name == null) return null;
        ReferenceModel reference = new ReferenceModel();
        reference.setType(type);
        reference.setReference(code);
        reference.setLabel(name != null ? name : code);
        reference.setDescription(firstValue(definition.getDescriptionSemantics(), raid));
        List<ReferenceFieldModel> fields = new ArrayList<>();
        if (definition.getFields() != null) {
            for (ReferenceTypeFieldModel typeField : definition.getFields()) {
                String value = firstValue(typeField.getSemantics(), raid);
                if (value == null || typeField.getCode() == null) continue;
                ReferenceFieldModel field = new ReferenceFieldModel();
                field.setCode(typeField.getCode());
                field.setDataType(typeField.getDataType());
                field.setValue(value);
                fields.add(field);
            }
        }
        if (!fields.isEmpty()) {
            ReferenceDefinitionModel referenceDefinition = new ReferenceDefinitionModel();
            referenceDefinition.setFields(fields);
            reference.setDefinition(referenceDefinition);
        }
        return reference;
    }

    /** The first of the imported keys the record answers; a key names its path in the record ("raid.identifier.owner.id"). */
    private static String firstValue(List<String> keys, JsonNode raid) {
        if (keys == null) return null;
        for (String key : keys) {
            if (!imported(key)) continue;
            JsonNode node = raid;
            for (String segment : key.substring(RaidSemanticsManifest.PREFIX.length() + 1).split("\\.")) node = node.path(segment);
            if (node.isValueNode() && !node.asText().isBlank()) return node.asText();
        }
        return null;
    }

    private static boolean contains(List<String> semantics, String key) {
        return semantics != null && semantics.contains(key);
    }
}
