package org.opencdmp.filetransformer.raid.model.raid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.opencdmp.filetransformer.raid.enums.LanguageId;
import org.opencdmp.filetransformer.raid.enums.LanguageSchemaUri;

import java.io.Serializable;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Language implements Serializable {

    private LanguageId id;

    private LanguageSchemaUri schemaUri;

    public LanguageId getId() {
        return id;
    }

    public void setId(LanguageId id) {
        this.id = id;
    }

    public LanguageSchemaUri getSchemaUri() {
        return schemaUri;
    }

    public void setSchemaUri(LanguageSchemaUri schemaUri) {
        this.schemaUri = schemaUri;
    }
}
