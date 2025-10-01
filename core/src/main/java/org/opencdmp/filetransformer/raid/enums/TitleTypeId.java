package org.opencdmp.filetransformer.raid.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.tools.exception.MyApplicationException;

import java.util.HashMap;
import java.util.Map;

public enum TitleTypeId {

	Primary("https://vocabulary.raid.org/title.type.id/380"),
	Short("https://vocabulary.raid.org/title.type.id/381"),
	Acronym("https://vocabulary.raid.org/title.type.id/378"),
	Alternative("https://vocabulary.raid.org/title.type.id/379");

	private final String value;
	private final static Map<String, TitleTypeId> CONSTANTS = new HashMap<>();

	static {
		for (TitleTypeId c: values()) {
			CONSTANTS.put(c.value, c);
		}
	}

	TitleTypeId(String value) {
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
	public static TitleTypeId fromValue(String value) {
		if (value != null && !value.isBlank() && value.endsWith("/")) {
			value = value.substring(0, value.length() - 1);
		}
		TitleTypeId constant = CONSTANTS.get(value);
		if (constant == null) {
			throw new MyApplicationException(value);
		} else {
			return constant;
		}
	}

}
