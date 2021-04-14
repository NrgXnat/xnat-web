package org.nrg.xnat.dto.search;

import java.util.List;

public class DisplayVersionDto {

	private String elementName;
	private List<VersionDto> versions;

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public List<VersionDto> getVersions() {
		return versions;
	}

	public void setVersions(List<VersionDto> versions) {
		this.versions = versions;
	}

}
