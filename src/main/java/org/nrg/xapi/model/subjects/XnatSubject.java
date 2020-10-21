package org.nrg.xapi.model.subjects;

import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

import org.nrg.xnat.model.XnatSubjectI;

public class XnatSubject implements XnatSubjectI {

	public XnatSubject() {
	}

	public XnatSubject(final XnatSubjectI subject) {
		_id = Objects.isNull(subject.getId()) || subject.getId().isEmpty() ?  EMPTY_STRING : subject.getId();
		_project = Objects.isNull(subject.getProject()) || subject.getProject().isEmpty() ?  EMPTY_STRING : subject.getProject();
		_label = Objects.isNull(subject.getLabel()) || subject.getLabel().isEmpty() ?  EMPTY_STRING : subject.getLabel();
		_insertDate = subject.getInsertDate();
		_insertUser = Objects.isNull(subject.getInsertUser()) || subject.getInsertUser().isEmpty() ?  EMPTY_STRING : subject.getInsertUser();
		
		
	}

	public XnatSubject(final ResultSet resultSet) throws SQLException {
		_id = Objects.isNull(resultSet.getString(1)) || resultSet.getString(1).isEmpty() ?  EMPTY_STRING : resultSet.getString(1);
		_project = Objects.isNull(resultSet.getString(2)) || resultSet.getString(2).isEmpty() ?  EMPTY_STRING : resultSet.getString(2);
		_label = Objects.isNull(resultSet.getString(3)) || resultSet.getString(3).isEmpty() ?  EMPTY_STRING : resultSet.getString(3);
		_insertDate = resultSet.getDate(4);
		_insertUser = Objects.isNull(resultSet.getString(5)) || resultSet.getString(5).isEmpty() ?  EMPTY_STRING : resultSet.getString(5);
	}

	@Override
	public void toXML(Writer writer) throws Exception {
	}

	@Override
	public String getId() {
		return _id;
	}

	@Override
	public void setId(final String id) {
		_id = id;
	}

	@Override
	public String getLabel() {
		return _label;
	}

	@Override
	public void setLabel(final String label) {
		_label = label;
	}

	@Override
	public String getProject() {
		return _project;
	}

	@Override
	public void setProject(String project) {
		_project = project;
	}

	@Override
	public Date getInsertDate() {
		return _insertDate;
	}

	@Override
	public void setInsertDate(Date insertDate) {
		_insertDate = insertDate;
	}

	@Override
	public String getInsertUser() {
		return _insertUser;
	}

	@Override
	public void setInsertUser(String insertUser) {
		_insertUser = insertUser;
	}

	private String _id;
	private String _label;
	private String _project;
	private Date _insertDate;
	private String _insertUser;
	private static final String EMPTY_STRING = "";
}
