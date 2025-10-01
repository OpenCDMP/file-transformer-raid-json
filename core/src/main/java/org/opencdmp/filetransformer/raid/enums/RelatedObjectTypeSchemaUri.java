package org.opencdmp.filetransformer.raid.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.tools.exception.MyApplicationException;

import java.util.HashMap;
import java.util.Map;

public enum RelatedObjectTypeSchemaUri {

	URI("https://vocabulary.raid.org/relatedObject.type.schema/329");

	private final String value;
	private final static Map<String, RelatedObjectTypeSchemaUri> CONSTANTS = new HashMap<>();

	static {
		for (RelatedObjectTypeSchemaUri c: values()) {
			CONSTANTS.put(c.value, c);
		}
	}

	RelatedObjectTypeSchemaUri(String value) {
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
	public static RelatedObjectTypeSchemaUri fromValue(String value) {
		if (value != null && !value.isBlank() && value.endsWith("/")) {
			value = value.substring(0, value.length() - 1);
		}
		RelatedObjectTypeSchemaUri constant = CONSTANTS.get(value);
		if (constant == null) {
			throw new MyApplicationException(value);
		} else {
			return constant;
		}
	}

}
