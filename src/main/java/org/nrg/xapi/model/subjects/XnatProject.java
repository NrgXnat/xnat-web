package org.nrg.xapi.model.subjects;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.nrg.xnat.model.XnatProjectI;

public class XnatProject implements XnatProjectI {

	public XnatProject() {

	}

	public XnatProject(final XnatProjectI project) {
		_id = project.getId();
		_secondaryId = project.getSecondaryId();
		_name = project.getName();
		_description = project.getDescription();
		_piFirstName = project.getPiFirstName();
		_piLastName = project.getPiLastName();

	}

	public XnatProject(final ResultSet resultSet) throws SQLException {
		_id = resultSet.getString(1);
		_secondaryId = resultSet.getString(2);
		_name = resultSet.getString(3);
		_description = resultSet.getString(4);
		_piFirstName = resultSet.getString(5);
		_piLastName = resultSet.getString(6);
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

}
