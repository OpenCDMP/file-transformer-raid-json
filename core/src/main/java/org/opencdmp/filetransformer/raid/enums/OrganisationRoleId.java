package org.opencdmp.filetransformer.raid.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.tools.exception.MyApplicationException;

import java.util.HashMap;
import java.util.Map;

public enum OrganisationRoleId {

	LeadResearchOrganisation("https://vocabulary.raid.org/organisation.role.schema/182"),
	OtherResearchOrganisation("https://vocabulary.raid.org/organisation.role.schema/183"),
	PartnerOrganisation("https://vocabulary.raid.org/organisation.role.schema/184"),
	Contractor("https://vocabulary.raid.org/organisation.role.schema/185"),
	Funder("https://vocabulary.raid.org/organisation.role.schema/186"),
	Facility("https://vocabulary.raid.org/organisation.role.schema/187"),
	Other ("https://vocabulary.raid.org/organisation.role.schema/188");

	private final String value;
	private final static Map<String, OrganisationRoleId> CONSTANTS = new HashMap<>();

	static {
		for (OrganisationRoleId c: values()) {
			CONSTANTS.put(c.value, c);
		}
	}

	OrganisationRoleId(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}

	@JsonValue
	public String value() {
		return this.value;
	}

	@JsonCreator
	public static OrganisationRoleId fromValue(String value) {
		if (value != null && !value.isBlank() && value.endsWith("/")) {
			value = value.substring(0, value.length() - 1);
		}
		OrganisationRoleId constant = CONSTANTS.get(value);
		if (constant == null) {
			throw new MyApplicationException(value);
		} else {
			return constant;
		}
	}

}
