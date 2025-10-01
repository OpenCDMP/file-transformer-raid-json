package org.opencdmp.filetransformer.raid.model.raid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.opencdmp.filetransformer.raid.enums.*;

import java.io.Serializable;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Identifier implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("schemaUri")
    private IdentifierSchemaUri schemaUri;

    @JsonProperty("registrationAgency")
    private RegistrationAgency registrationAgency;

    @JsonProperty("owner")
    private Owner owner;

    @JsonProperty("license")
    private IdentifierLicense license;

    @JsonProperty("version")
    private String version;

    public static class RegistrationAgency {
        private String id;

        private IdentifierRegistrationAgencySchemaUri schemaUri;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public IdentifierRegistrationAgencySchemaUri getSchemaUri() {
            return schemaUri;
        }

        public void setSchemaUri(IdentifierRegistrationAgencySchemaUri schemaUri) {
            this.schemaUri = schemaUri;
        }
    }

    public static class Owner {
        private String id;

        private IdentifierRegistrationAgencySchemaUri schemaUri;

        private String servicePoint;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public IdentifierRegistrationAgencySchemaUri getSchemaUri() {
            return schemaUri;
        }

        public void setSchemaUri(IdentifierRegistrationAgencySchemaUri schemaUri) {
            this.schemaUri = schemaUri;
        }

        public String getServicePoint() {
            return servicePoint;
        }

        public void setServicePoint(String servicePoint) {
            this.servicePoint = servicePoint;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public IdentifierSchemaUri getSchemaUri() {
        return schemaUri;
    }

    public void setSchemaUri(IdentifierSchemaUri schemaUri) {
        this.schemaUri = schemaUri;
    }

    public RegistrationAgency getRegistrationAgency() {
        return registrationAgency;
    }

    public void setRegistrationAgency(RegistrationAgency registrationAgency) {
        this.registrationAgency = registrationAgency;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public IdentifierLicense getLicense() {
        return license;
    }

    public void setLicense(IdentifierLicense license) {
        this.license = license;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
