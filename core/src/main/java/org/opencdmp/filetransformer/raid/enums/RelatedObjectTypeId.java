package org.opencdmp.filetransformer.raid.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import gr.cite.tools.exception.MyApplicationException;

import java.util.HashMap;
import java.util.Map;

public enum RelatedObjectTypeId {

	Audiovisual("https://vocabulary.raid.org/relatedObject.type.schema/273"),
	Book("https://vocabulary.raid.org/relatedObject.type.schema/258"),
	BookChapter("https://vocabulary.raid.org/relatedObject.type.schema/271"),
	ComputationalNotebook("https://vocabulary.raid.org/relatedObject.type.schema/256"),
	ConferencePaper("https://vocabulary.raid.org/relatedObject.type.schema/264"),
	ConferencePoster("https://vocabulary.raid.org/relatedObject.type.schema/248"),
	ConferenceProceeding("https://vocabulary.raid.org/relatedObject.type.schema/262"),
	DataPaper("https://vocabulary.raid.org/relatedObject.type.schema/255"),
	Dataset("https://vocabulary.raid.org/relatedObject.type.schema/269"),
	Dissertation("https://vocabulary.raid.org/relatedObject.type.schema/253"),
	Event("https://vocabulary.raid.org/relatedObject.type.schema/260"),
	Funding("https://vocabulary.raid.org/relatedObject.type.schema/272"),
	Image("https://vocabulary.raid.org/relatedObject.type.schema/257"),
	Instrument("https://vocabulary.raid.org/relatedObject.type.schema/266"),
	JournalArticle("https://vocabulary.raid.org/relatedObject.type.schema/250"),
	LearningObject("https://vocabulary.raid.org/relatedObject.type.schema/267"),
	Model("https://vocabulary.raid.org/relatedObject.type.schema/263"),
	OutputManagementPlan("https://vocabulary.raid.org/relatedObject.type.schema/247"),
	PhysicalObject("https://vocabulary.raid.org/relatedObject.type.schema/270"),
	Preprint("https://vocabulary.raid.org/relatedObject.type.schema/254"),
	Prize("https://vocabulary.raid.org/relatedObject.type.schema/268"),
	Report("https://vocabulary.raid.org/relatedObject.type.schema/252"),
	Service("https://vocabulary.raid.org/relatedObject.type.schema/274"),
	Software("https://vocabulary.raid.org/relatedObject.type.schema/259"),
	Sound("https://vocabulary.raid.org/relatedObject.type.schema/261"),
	Standard("https://vocabulary.raid.org/relatedObject.type.schema/251"),
	Text("https://vocabulary.raid.org/relatedObject.type.schema/265"),
	Workflow("https://vocabulary.raid.org/relatedObject.type.schema/249");

	private final String value;
	private final static Map<String, RelatedObjectTypeId> CONSTANTS = new HashMap<>();

	static {
		for (RelatedObjectTypeId c: values()) {
			CONSTANTS.put(c.value, c);
		}
	}

	RelatedObjectTypeId(String value) {
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
	public static RelatedObjectTypeId fromValue(String value) {
		if (value != null && !value.isBlank() && value.endsWith("/")) {
			value = value.substring(0, value.length() - 1);
		}
		RelatedObjectTypeId constant = CONSTANTS.get(value);
		if (constant == null) {
			throw new MyApplicationException(value);
		} else {
			return constant;
		}
	}

}
