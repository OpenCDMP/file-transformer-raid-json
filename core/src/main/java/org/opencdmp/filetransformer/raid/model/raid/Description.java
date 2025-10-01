package org.opencdmp.filetransformer.raid.model.raid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.opencdmp.filetransformer.raid.enums.DescriptionTypeId;
import org.opencdmp.filetransformer.raid.enums.DescriptionTypeSchemaUri;
import org.opencdmp.filetransformer.raid.enums.TitleTypeId;
import org.opencdmp.filetransformer.raid.enums.TitleTypeSchemaUri;

import java.io.Serializable;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Description implements Serializable {

    @JsonProperty("text")
    private String text;

    @JsonProperty("type")
    private Type type;

    @JsonProperty("language")
    private Language language;


    public static class Type {
        private DescriptionTypeId id;

        private DescriptionTypeSchemaUri schemaUri;

        public DescriptionTypeId getId() {
            return id;
        }

        public void setId(DescriptionTypeId id) {
            this.id = id;
        }

        public DescriptionTypeSchemaUri getSchemaUri() {
            return schemaUri;
        }

        public void setSchemaUri(DescriptionTypeSchemaUri schemaUri) {
            this.schemaUri = schemaUri;
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

}
