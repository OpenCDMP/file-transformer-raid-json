package org.opencdmp.filetransformer.raid.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.tools.exception.MyApplicationException;

import java.util.HashMap;
import java.util.Map;

public enum RelatedRaidTypeId {

	Continues("https://vocabulary.raid.org/relatedRaid.type.schema/204"),
	IsContinuedBy("https://vocabulary.raid.org/relatedRaid.type.schema/203"),
	HasPart("https://vocabulary.raid.org/relatedRaid.type.schema/201"),
	IsPartOf("https://vocabulary.raid.org/relatedRaid.type.schema/202"),
	IsSourceOf("https://vocabulary.raid.org/relatedRaid.type.schema/199"),
	IsDerivedFrom("https://vocabulary.raid.org/relatedRaid.type.schema/200"),
	Obsoletes("https://vocabulary.raid.org/relatedRaid.type.schema/198"),
	IsObsoletedBy("https://vocabulary.raid.org/relatedRaid.type.schema/205");

	private final String value;
	private final static Map<String, RelatedRaidTypeId> CONSTANTS = new HashMap<>();

	static {
		for (RelatedRaidTypeId c: values()) {
			CONSTANTS.put(c.value, c);
		}
	}

	RelatedRaidTypeId(String value) {
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
	public static RelatedRaidTypeId fromValue(String value) {
		if (value != null && !value.isBlank() && value.endsWith("/")) {
			value = value.substring(0, value.length() - 1);
		}
		RelatedRaidTypeId constant = CONSTANTS.get(value);
		if (constant == null) {
			throw new MyApplicationException(value);
		} else {
			return constant;
		}
	}

}
