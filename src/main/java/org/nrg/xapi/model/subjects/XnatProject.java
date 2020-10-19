package org.nrg.xapi.model.subjects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import org.nrg.xnat.model.XnatProjectI;

public class XnatProject implements XnatProjectI {

	public XnatProject() {

	}

	public XnatProject(final XnatProjectI project) {
		_id = Objects.isNull(project.getId()) || project.getId().isEmpty() ?  EMPTY_STRING : project.getId();
		_secondaryId = Objects.isNull(project.getSecondaryId()) || project.getSecondaryId().isEmpty() ?  EMPTY_STRING : project.getSecondaryId();
		_name = Objects.isNull(project.getName()) || project.getName().isEmpty() ?  EMPTY_STRING : project.getName();
		_description = Objects.isNull(project.getDescription()) || project.getDescription().isEmpty() ?  EMPTY_STRING : project.getDescription();
		_piFirstName = Objects.isNull(project.getPiFirstName()) || project.getPiFirstName().isEmpty() ?  EMPTY_STRING : project.getPiFirstName();
		_piLastName = Objects.isNull(project.getPiLastName()) || project.getPiLastName().isEmpty() ?  EMPTY_STRING : project.getPiLastName();

	}

	public XnatProject(final ResultSet resultSet) throws SQLException {
		_id = Objects.isNull(resultSet.getString(1)) || resultSet.getString(1).isEmpty() ?  EMPTY_STRING : resultSet.getString(1);
		_secondaryId = Objects.isNull(resultSet.getString(2)) || resultSet.getString(2).isEmpty() ?  EMPTY_STRING : resultSet.getString(2);
		_name = Objects.isNull(resultSet.getString(3)) || resultSet.getString(3).isEmpty() ?  EMPTY_STRING : resultSet.getString(3);
		_description = Objects.isNull(resultSet.getString(4)) || resultSet.getString(4).isEmpty() ?  EMPTY_STRING : resultSet.getString(4);
		_piFirstName = Objects.isNull(resultSet.getString(5)) || resultSet.getString(5).isEmpty() ?  EMPTY_STRING : resultSet.getString(5);
		_piLastName = Objects.isNull(resultSet.getString(6)) || resultSet.getString(6).isEmpty() ?  EMPTY_STRING : resultSet.getString(6);
	}

	public String getId() {
		return _id;
	}

	public void set_id(String id) {
		_id = id;
	}

	public String getSecondaryId() {
		return _secondaryId;
	}

	public void setSecondaryId(String secondaryId) {
		_secondaryId = secondaryId;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public String getDescription() {
		return _description;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public String getPiFirstName() {
		return _piFirstName;
	}

	public void setPiFirstName(String piFirstName) {
		_piFirstName = piFirstName;
	}

	public String getPiLastName() {
		return _piLastName;
	}

	public void setPiLastName(String piLastName) {
		_piLastName = piLastName;
	}

	private String _id;
	private String _secondaryId;
	private String _name;
	private String _description;
	private String _piFirstName;
	private String _piLastName;
	private static final String EMPTY_STRING = "";

}
