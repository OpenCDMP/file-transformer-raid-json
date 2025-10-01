package org.opencdmp.filetransformer.raid.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.tools.exception.MyApplicationException;

import java.util.HashMap;
import java.util.Map;

public enum RelatedObjectSchemaUri {

	Arks("https://arks.org"),
	DOI("http://doi.org"),
	NonDOI("http://hdl.handle.net"),
	ISBN("https://www.isbn-international.org"),
	RRID("https://scicrunch.org/resolver"),
	Other("https://archive.org");

	private final String value;
	private final static Map<String, RelatedObjectSchemaUri> CONSTANTS = new HashMap<>();

	static {
		for (RelatedObjectSchemaUri c: values()) {
			CONSTANTS.put(c.value, c);
		}
	}

	RelatedObjectSchemaUri(String value) {
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
	public static RelatedObjectSchemaUri fromValue(String value) {
		if (value != null && !value.isBlank() && value.endsWith("/")) {
			value = value.substring(0, value.length() - 1);
		}
		RelatedObjectSchemaUri constant = CONSTANTS.get(value);
		if (constant == null) {
			throw new MyApplicationException(value);
		} else {
			return constant;
		}
	}

}
