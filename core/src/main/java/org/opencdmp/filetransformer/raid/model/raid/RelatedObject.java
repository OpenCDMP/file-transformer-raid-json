package org.opencdmp.filetransformer.raid.model.raid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.opencdmp.filetransformer.raid.enums.*;

import java.io.Serializable;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelatedObject implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("schemaUri")
    private RelatedObjectSchemaUri schemaUri;

    @JsonProperty("type")
    private Type type;

    @JsonProperty("category")
    private List<Category> category;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Type {
        private RelatedObjectTypeId id;

        private RelatedObjectTypeSchemaUri schemaUri;

        public RelatedObjectTypeId getId() {
            return id;
        }

        public void setId(RelatedObjectTypeId id) {
            this.id = id;
        }

        public RelatedObjectTypeSchemaUri getSchemaUri() {
            return schemaUri;
        }

        public void setSchemaUri(RelatedObjectTypeSchemaUri schemaUri) {
            this.schemaUri = schemaUri;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Category {
        private RelatedObjectCategoryId id;

        private RelatedObjectCategorySchemaUri schemaUri;

        public RelatedObjectCategoryId getId() {
            return id;
        }

        public void setId(RelatedObjectCategoryId id) {
            this.id = id;
        }

        public RelatedObjectCategorySchemaUri getSchemaUri() {
            return schemaUri;
        }

        public void setSchemaUri(RelatedObjectCategorySchemaUri schemaUri) {
            this.schemaUri = schemaUri;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RelatedObjectSchemaUri getSchemaUri() {
        return schemaUri;
    }

    public void setSchemaUri(RelatedObjectSchemaUri schemaUri) {
        this.schemaUri = schemaUri;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }
}
