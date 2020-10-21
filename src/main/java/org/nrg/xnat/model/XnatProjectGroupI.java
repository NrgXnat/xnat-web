package org.nrg.xnat.model;

public interface XnatProjectGroupI {
	
	public String getId();

	public void setId(String id);

	public String getDisplayName();

	public void setDisplayName(String displayName);

	public String getTag();

	public void setTag(String tag);

	public int getXnatUserGroupId();

	public void setXnatUserGroupId(int xnatUserGroupId);

	public Long getUsers();

	public void setUsers(Long users);
}
