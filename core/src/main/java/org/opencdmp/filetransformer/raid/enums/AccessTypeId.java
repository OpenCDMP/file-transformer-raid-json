package org.opencdmp.filetransformer.raid.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.tools.exception.MyApplicationException;

import java.util.HashMap;
import java.util.Map;

public enum AccessTypeId {

	OpenAccess("https://vocabularies.coar-repositories.org/access_rights/c_abf2"),
	EmbargoedAccess("https://vocabularies.coar-repositories.org/access_rights/c_f1cf");

	private final String value;
	private final static Map<String, AccessTypeId> CONSTANTS = new HashMap<>();

	static {
		for (AccessTypeId c: values()) {
			CONSTANTS.put(c.value, c);
		}
	}

	AccessTypeId(String value) {
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
	public static AccessTypeId fromValue(String value) {
		if (value != null && !value.isBlank() && value.endsWith("/")) {
			value = value.substring(0, value.length() - 1);
		}

		AccessTypeId constant = CONSTANTS.get(value);
		if (constant == null) {
			throw new MyApplicationException(value);
		} else {
			return constant;
		}
	}

}
