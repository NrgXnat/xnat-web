package org.nrg.xapi.model.subjects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import org.nrg.xnat.model.XnatProjectGroupI;

public class XnatProjectGroup implements XnatProjectGroupI {

	public XnatProjectGroup() {
	}

	public XnatProjectGroup(XnatProjectGroupI projectGroup) {
		_id = Objects.isNull(projectGroup.getId()) || projectGroup.getId().isEmpty() ?  EMPTY_STRING : projectGroup.getId();
		_displayName = Objects.isNull(projectGroup.getDisplayName()) || projectGroup.getDisplayName().isEmpty() ?  EMPTY_STRING : projectGroup.getDisplayName();
		_tag = Objects.isNull(projectGroup.getTag()) || projectGroup.getTag().isEmpty() ?  EMPTY_STRING : projectGroup.getTag();
		_xnatUserGroupId = projectGroup.getXnatUserGroupId();
		_users = projectGroup.getUsers();
		
	}

	public XnatProjectGroup(ResultSet  resultSet) throws SQLException {
		_id = Objects.isNull(resultSet.getString(1)) || resultSet.getString(1).isEmpty() ?  EMPTY_STRING : resultSet.getString(1);
		_displayName = Objects.isNull(resultSet.getString(2)) || resultSet.getString(2).isEmpty() ?  EMPTY_STRING : resultSet.getString(2);
		_tag = Objects.isNull(resultSet.getString(3)) || resultSet.getString(3).isEmpty() ?  EMPTY_STRING : resultSet.getString(3);
		_xnatUserGroupId = resultSet.getInt(4);
		_users = resultSet.getLong(5);
	}
	

	public String getId() {
		return _id;
	}

	public void setId(String id) {
		_id = id;
	}

	public String getDisplayName() {
		return _displayName;
	}

	public void setDisplayName(String displayName) {
		_displayName = displayName;
	}

	public String getTag() {
		return _tag;
	}

	public void setTag(String tag) {
		_tag = tag;
	}

	public int getXnatUserGroupId() {
		return _xnatUserGroupId;
	}

	public void setXnatUserGroupId(int xnatUserGroupId) {
		_xnatUserGroupId = xnatUserGroupId;
	}

	public Long getUsers() {
		return _users;
	}

	public void setUsers(Long users) {
		_users = users;
	}

	private String _id;
	private String _displayName;
	private String _tag;
	private int _xnatUserGroupId;
	private Long _users;
	private static final String EMPTY_STRING = "";
}
