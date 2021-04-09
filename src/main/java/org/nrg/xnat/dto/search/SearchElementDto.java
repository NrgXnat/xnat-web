package org.nrg.xnat.dto.search;

import java.io.Serializable;

public class SearchElementDto implements Serializable {
	private static final long serialVersionUID = -3775531857512535678L;
	private String singular;
	private String plural;
	private Boolean secured;
	private String elementName;
	private Long count;

	public String getSingular() {
		return singular;
	}

	public void setSingular(String singular) {
		this.singular = singular;
	}

	public String getPlural() {
		return plural;
	}

	public void setPlural(String plural) {
		this.plural = plural;
	}

	public Boolean getSecured() {
		return secured;
	}

	public void setSecured(Boolean secured) {
		this.secured = secured;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

}
