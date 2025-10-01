package org.opencdmp.filetransformer.raid.model.raid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.opencdmp.filetransformer.raid.enums.*;

import java.io.Serializable;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelatedRaid implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("type")
    private Type type;


    public static class Type {
        private RelatedRaidTypeId id;

        private RelatedRaidTypeSchemaUri schemaUri;

        public RelatedRaidTypeId getId() {
            return id;
        }

        public void setId(RelatedRaidTypeId id) {
            this.id = id;
        }

        public RelatedRaidTypeSchemaUri getSchemaUri() {
            return schemaUri;
        }

        public void setSchemaUri(RelatedRaidTypeSchemaUri schemaUri) {
            this.schemaUri = schemaUri;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
