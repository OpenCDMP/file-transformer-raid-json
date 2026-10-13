package org.opencdmp.filetransformer.raid.model.raid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.opencdmp.filetransformer.raid.enums.*;

import java.io.Serializable;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Organisation implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("schemaUri")
    private OrganisationSchemaUri schemaUri;

    @JsonProperty("role")
    private List<Role> role;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Role {
        private OrganisationRoleId id;

        private OrganisationRoleSchemaUri schemaUri;

        private String startDate;

        private String endDate;

        public OrganisationRoleId getId() {
            return id;
        }

        public void setId(OrganisationRoleId id) {
            this.id = id;
        }

        public OrganisationRoleSchemaUri getSchemaUri() {
            return schemaUri;
        }

        public void setSchemaUri(OrganisationRoleSchemaUri schemaUri) {
            this.schemaUri = schemaUri;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OrganisationSchemaUri getSchemaUri() {
        return schemaUri;
    }

    public void setSchemaUri(OrganisationSchemaUri schemaUri) {
        this.schemaUri = schemaUri;
    }

    public List<Role> getRole() {
        return role;
    }

    public void setRole(List<Role> role) {
        this.role = role;
    }
}
