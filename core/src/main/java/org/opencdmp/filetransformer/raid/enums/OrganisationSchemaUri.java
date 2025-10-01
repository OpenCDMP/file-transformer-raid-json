package org.opencdmp.filetransformer.raid.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.tools.exception.MyApplicationException;

import java.util.HashMap;
import java.util.Map;

public enum OrganisationSchemaUri {

	Schema("https://ror.org");

	private final String value;
	private final static Map<String, OrganisationSchemaUri> CONSTANTS = new HashMap<>();

	static {
		for (OrganisationSchemaUri c: values()) {
			CONSTANTS.put(c.value, c);
		}
	}

	OrganisationSchemaUri(String value) {
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
	public static OrganisationSchemaUri fromValue(String value) {
		OrganisationSchemaUri constant = CONSTANTS.get(value);
		if (constant == null) {
			throw new MyApplicationException(value);
		} else {
			return constant;
		}
	}

}
