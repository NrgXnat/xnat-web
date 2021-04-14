package org.nrg.xnat.dto.search;

import java.util.List;

public class VersionDto {

	private String name;
	private String orderBy;
	private String lightColor;
	private String darkColor;
	private String defaultSortOrder;
	private List<DisplayFieldReferenceIDto> fields;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getLightColor() {
		return lightColor;
	}

	public void setLightColor(String lightColor) {
		this.lightColor = lightColor;
	}

	public String getDarkColor() {
		return darkColor;
	}

	public void setDarkColor(String darkColor) {
		this.darkColor = darkColor;
	}

	public String getDefaultSortOrder() {
		return defaultSortOrder;
	}

	public void setDefaultSortOrder(String defaultSortOrder) {
		this.defaultSortOrder = defaultSortOrder;
	}

	public List<DisplayFieldReferenceIDto> getFields() {
		return fields;
	}

	public void setFields(List<DisplayFieldReferenceIDto> fields) {
		this.fields = fields;
	}

}
