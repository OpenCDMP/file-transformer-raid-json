package org.opencdmp.filetransformer.raid.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.tools.exception.MyApplicationException;

import java.util.HashMap;
import java.util.Map;

public enum ContributorRoleId {

	Conceptualization("https://credit.niso.org/contributor-role/conceptualization"),
	DataCuration("https://credit.niso.org/contributor-role/data-curation"),
	FormalAnalysis("https://credit.niso.org/contributor-role/formal-analysis"),
	FundingAcquisition("https://credit.niso.org/contributor-role/funding-acquisition"),
	Investigation("https://credit.niso.org/contributor-role/investigation"),
	Methodology("https://credit.niso.org/contributor-role/methodology"),
	ProjectAdministration("https://credit.niso.org/contributor-role/project-administration"),
	Resources("https://credit.niso.org/contributor-role/resources"),
	Software("https://credit.niso.org/contributor-role/software"),
	Supervision("https://credit.niso.org/contributor-role/supervision"),
	Validation("https://credit.niso.org/contributor-role/validation"),
	Visualization("https://credit.niso.org/contributor-role/visualization"),
	WritingOriginalDraft("https://credit.niso.org/contributor-role/writing-original-draft"),
	WritingReviewEditing("https://credit.niso.org/contributor-role/writing-review-editing");

	private final String value;
	private final static Map<String, ContributorRoleId> CONSTANTS = new HashMap<>();

	static {
		for (ContributorRoleId c: values()) {
			CONSTANTS.put(c.value, c);
		}
	}

	ContributorRoleId(String value) {
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
	public static ContributorRoleId fromValue(String value) {
		if (value != null && !value.isBlank() && value.endsWith("/")) {
			value = value.substring(0, value.length() - 1);
		}
		ContributorRoleId constant = CONSTANTS.get(value);
		if (constant == null) {
			throw new MyApplicationException(value);
		} else {
			return constant;
		}
	}

}
