package org.opencdmp.filetransformer.raid.model.raid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.opencdmp.filetransformer.raid.enums.*;

import java.io.Serializable;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Contributor implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("schemaUri")
    private ContributorSchemaUri schemaUri;

    @JsonProperty("language")
    private Position position;

    @JsonProperty("leader")
    private ContributorLeader leader;

    @JsonProperty("contact")
    private ContributorLeader contact;

    @JsonProperty("role")
    private Role role;


    public static class Position {
        private ContributorPositionId id;

        private ContributorPositionSchemaUri schemaUri;

        private String startDate;

        private String endDate;

        public ContributorPositionId getId() {
            return id;
        }

        public void setId(ContributorPositionId id) {
            this.id = id;
        }

        public ContributorPositionSchemaUri getSchemaUri() {
            return schemaUri;
        }

        public void setSchemaUri(ContributorPositionSchemaUri schemaUri) {
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

    public static class Role {
        private ContributorRoleId id;

        private ContributorRoleSchemaUri schemaUri;

        public ContributorRoleId getId() {
            return id;
        }

        public void setId(ContributorRoleId id) {
            this.id = id;
        }

        public ContributorRoleSchemaUri getSchemaUri() {
            return schemaUri;
        }

        public void setSchemaUri(ContributorRoleSchemaUri schemaUri) {
            this.schemaUri = schemaUri;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ContributorSchemaUri getSchemaUri() {
        return schemaUri;
    }

    public void setSchemaUri(ContributorSchemaUri schemaUri) {
        this.schemaUri = schemaUri;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public ContributorLeader getLeader() {
        return leader;
    }

    public void setLeader(ContributorLeader leader) {
        this.leader = leader;
    }

    public ContributorLeader getContact() {
        return contact;
    }

    public void setContact(ContributorLeader contact) {
        this.contact = contact;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
