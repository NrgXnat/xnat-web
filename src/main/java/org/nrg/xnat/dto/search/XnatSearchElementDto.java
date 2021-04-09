package org.nrg.xnat.dto.search;

import java.io.Serializable;

import org.json.JSONObject;
import org.nrg.xdat.display.ElementDisplay;

public class XnatSearchElementDto implements Serializable{
	
	private static final long serialVersionUID = -7765541182699651606L;
	private String summary;
	private String fieldId;
	private String header;
	private Boolean requiresValue;
	private String elementName;
	private String type;
	private String description;
	private Integer src;
	private JSONObject versions;

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getFieldId() {
		return fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public Boolean getRequiresValue() {
		return requiresValue;
	}

	public void setRequiresValue(Boolean requiresValue) {
		this.requiresValue = requiresValue;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getSrc() {
		return src;
	}

	public void setSrc(Integer src) {
		this.src = src;
	}

	public JSONObject getVersions() {
		return versions;
	}

	public void setVersions(JSONObject versions) {
		this.versions = versions;
	}
	

}
