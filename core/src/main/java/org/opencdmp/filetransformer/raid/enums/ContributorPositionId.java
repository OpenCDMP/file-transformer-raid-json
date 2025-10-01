package org.opencdmp.filetransformer.raid.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.tools.exception.MyApplicationException;

import java.util.HashMap;
import java.util.Map;

public enum ContributorPositionId {

	PrincipalOrChiefInvestigator("https://vocabulary.raid.org/contributor.position.schema/307"),
	CoInvestigatorOrCollaborator("https://vocabulary.raid.org/contributor.position.schema/308"),
	PartnerInvestigator("https://vocabulary.raid.org/contributor.position.schema/309"),
	Consultant("https://vocabulary.raid.org/contributor.position.schema/310"),
	Other("https://vocabulary.raid.org/contributor.position.schema/311");

	private final String value;
	private final static Map<String, ContributorPositionId> CONSTANTS = new HashMap<>();

	static {
		for (ContributorPositionId c: values()) {
			CONSTANTS.put(c.value, c);
		}
	}

	ContributorPositionId(String value) {
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
	public static ContributorPositionId fromValue(String value) {
		if (value != null && !value.isBlank() && value.endsWith("/")) {
			value = value.substring(0, value.length() - 1);
		}
		ContributorPositionId constant = CONSTANTS.get(value);
		if (constant == null) {
			throw new MyApplicationException(value);
		} else {
			return constant;
		}
	}

}
