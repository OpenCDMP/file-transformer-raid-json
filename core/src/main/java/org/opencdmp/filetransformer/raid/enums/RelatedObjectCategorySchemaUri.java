package org.opencdmp.filetransformer.raid.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.tools.exception.MyApplicationException;

import java.util.HashMap;
import java.util.Map;

public enum RelatedObjectCategorySchemaUri {

	URI("https://vocabulary.raid.org/relatedObject.category.schema/385");

	private final String value;
	private final static Map<String, RelatedObjectCategorySchemaUri> CONSTANTS = new HashMap<>();

	static {
		for (RelatedObjectCategorySchemaUri c: values()) {
			CONSTANTS.put(c.value, c);
		}
	}

	RelatedObjectCategorySchemaUri(String value) {
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
	public static RelatedObjectCategorySchemaUri fromValue(String value) {
		if (value != null && !value.isBlank() && value.endsWith("/")) {
			value = value.substring(0, value.length() - 1);
		}
		RelatedObjectCategorySchemaUri constant = CONSTANTS.get(value);
		if (constant == null) {
			throw new MyApplicationException(value);
		} else {
			return constant;
		}
	}

}
