package org.opencdmp.filetransformer.raid.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.tools.exception.MyApplicationException;

import java.util.HashMap;
import java.util.Map;

public enum ContributorPositionSchemaUri {

	Other("https://vocabulary.raid.org/contributor.position.schema/305");

	private final String value;
	private final static Map<String, ContributorPositionSchemaUri> CONSTANTS = new HashMap<>();

	static {
		for (ContributorPositionSchemaUri c: values()) {
			CONSTANTS.put(c.value, c);
		}
	}

	ContributorPositionSchemaUri(String value) {
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
	public static ContributorPositionSchemaUri fromValue(String value) {
		if (value != null && !value.isBlank() && value.endsWith("/")) {
			value = value.substring(0, value.length() - 1);
		}
		ContributorPositionSchemaUri constant = CONSTANTS.get(value);
		if (constant == null) {
			throw new MyApplicationException(value);
		} else {
			return constant;
		}
	}

}
