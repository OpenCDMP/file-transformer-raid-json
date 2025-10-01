package org.opencdmp.filetransformer.raid.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.tools.exception.MyApplicationException;

import java.util.HashMap;
import java.util.Map;

public enum ContributorSchemaUri {

	ORCID("https://orcid.org"),
	ISNI("https://isni.org");

	private final String value;
	private final static Map<String, ContributorSchemaUri> CONSTANTS = new HashMap<>();

	static {
		for (ContributorSchemaUri c: values()) {
			CONSTANTS.put(c.value, c);
		}
	}

	ContributorSchemaUri(String value) {
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
	public static ContributorSchemaUri fromValue(String value) {
		if (value != null && !value.isBlank() && value.endsWith("/")) {
			value = value.substring(0, value.length() - 1);
		}
		ContributorSchemaUri constant = CONSTANTS.get(value);
		if (constant == null) {
			throw new MyApplicationException(value);
		} else {
			return constant;
		}
	}

}
