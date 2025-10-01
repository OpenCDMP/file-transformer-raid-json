package org.opencdmp.filetransformer.raid.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.tools.exception.MyApplicationException;

import java.util.HashMap;
import java.util.Map;

public enum IdentifierLicense {

	CC("Creative Commons CC-0");

	private final String value;
	private final static Map<String, IdentifierLicense> CONSTANTS = new HashMap<>();

	static {
		for (IdentifierLicense c: values()) {
			CONSTANTS.put(c.value, c);
		}
	}

	IdentifierLicense(String value) {
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
	public static IdentifierLicense fromValue(String value) {
		IdentifierLicense constant = CONSTANTS.get(value);
		if (constant == null) {
			throw new MyApplicationException(value);
		} else {
			return constant;
		}
	}

}
