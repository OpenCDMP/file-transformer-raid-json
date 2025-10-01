package org.opencdmp.filetransformer.raid.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.tools.exception.MyApplicationException;

import java.util.HashMap;
import java.util.Map;

public enum RelatedObjectCategoryId {

	Input("https://vocabulary.raid.org/relatedObject.category.id/191"),
	InternalProcessDocumentOrArtefact("https://vocabulary.raid.org/relatedObject.category.id/192"),
	Output("https://vocabulary.raid.org/relatedObject.category.id/190");

	private final String value;
	private final static Map<String, RelatedObjectCategoryId> CONSTANTS = new HashMap<>();

	static {
		for (RelatedObjectCategoryId c: values()) {
			CONSTANTS.put(c.value, c);
		}
	}

	RelatedObjectCategoryId(String value) {
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
	public static RelatedObjectCategoryId fromValue(String value) {
		if (value != null && !value.isBlank() && value.endsWith("/")) {
			value = value.substring(0, value.length() - 1);
		}
		RelatedObjectCategoryId constant = CONSTANTS.get(value);
		if (constant == null) {
			throw new MyApplicationException(value);
		} else {
			return constant;
		}
	}

}
