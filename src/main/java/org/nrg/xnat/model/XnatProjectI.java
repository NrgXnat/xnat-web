package org.nrg.xnat.model;

public interface XnatProjectI {
	
	public String getId();

	public void set_id(String id);

	public String getSecondaryId();

	public void setSecondaryId(String secondaryId);

	public String getName();

	public void setName(String name);

	public String getDescription();

	public void setDescription(String description);

	public String getPiFirstName();

	public void setPiFirstName(String piFirstName);

	public String getPiLastName();

	public void setPiLastName(String piLastName);
}
