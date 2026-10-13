package org.opencdmp.filetransformer.raid.model.raid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.opencdmp.filetransformer.raid.enums.*;

import java.io.Serializable;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Access implements Serializable {

    @JsonProperty("type")
    private Type type;

    @JsonProperty("embargoExpiry")
    private String embargoExpiry;

    @JsonProperty("statement")
    private Statement statement;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Type {
        private AccessTypeId id;

        private AccessTypeSchemaUri schemaUri;

        public AccessTypeId getId() {
            return id;
        }

        public void setId(AccessTypeId id) {
            this.id = id;
        }

        public AccessTypeSchemaUri getSchemaUri() {
            return schemaUri;
        }

        public void setSchemaUri(AccessTypeSchemaUri schemaUri) {
            this.schemaUri = schemaUri;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Statement {
        private String text;

        private Language language;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Language getLanguage() {
            return language;
        }

        public void setLanguage(Language language) {
            this.language = language;
        }
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getEmbargoExpiry() {
        return embargoExpiry;
    }

    public void setEmbargoExpiry(String embargoExpiry) {
        this.embargoExpiry = embargoExpiry;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }
}
