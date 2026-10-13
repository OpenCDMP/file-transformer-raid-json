package org.opencdmp.filetransformer.raid.model.raid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.opencdmp.filetransformer.raid.enums.LanguageId;
import org.opencdmp.filetransformer.raid.enums.LanguageSchemaUri;
import org.opencdmp.filetransformer.raid.enums.TitleTypeId;
import org.opencdmp.filetransformer.raid.enums.TitleTypeSchemaUri;

import java.io.Serializable;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Title implements Serializable {

    @JsonProperty("text")
    private String text;

    @JsonProperty("type")
    private Type type;

    @JsonProperty("language")
    private Language language;

    @JsonProperty("startDate")
    private String startDate;

    @JsonProperty("endDate")
    private String endDate;



    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Type {
        private TitleTypeId id;

        private TitleTypeSchemaUri schemaUri;

        public TitleTypeId getId() {
            return id;
        }

        public void setId(TitleTypeId id) {
            this.id = id;
        }

        public TitleTypeSchemaUri getSchemaUri() {
            return schemaUri;
        }

        public void setSchemaUri(TitleTypeSchemaUri schemaUri) {
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
