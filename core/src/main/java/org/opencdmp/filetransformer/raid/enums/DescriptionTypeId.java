package org.opencdmp.filetransformer.raid.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.tools.exception.MyApplicationException;

import java.util.HashMap;
import java.util.Map;

public enum DescriptionTypeId {

	Primary("https://vocabulary.raid.org/description.type.id/326"),
	Brief("https://vocabulary.raid.org/description.type.id/322"),
	SignificanceStatement("https://vocabulary.raid.org/description.type.id/327"),
	Methods("https://vocabulary.raid.org/description.type.id/323"),
	Objectives("https://vocabulary.raid.org/description.type.id/324"),
	Acknowledgements("https://vocabulary.raid.org/raid_placeholder/392"),
	Other("https://vocabulary.raid.org/description.type.id/325"),
	Alternative("https://vocabulary.raid.org/description.type.id/321");

	private final String value;
	private final static Map<String, DescriptionTypeId> CONSTANTS = new HashMap<>();

	static {
		for (DescriptionTypeId c: values()) {
			CONSTANTS.put(c.value, c);
		}
	}

	DescriptionTypeId(String value) {
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
	public static DescriptionTypeId fromValue(String value) {
		if (value != null && !value.isBlank() && value.endsWith("/")) {
			value = value.substring(0, value.length() - 1);
		}
		DescriptionTypeId constant = CONSTANTS.get(value);
		if (constant == null) {
			throw new MyApplicationException(value);
		} else {
			return constant;
		}
	}

}
