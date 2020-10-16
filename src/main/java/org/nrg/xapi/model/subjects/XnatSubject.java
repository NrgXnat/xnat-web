package org.nrg.xapi.model.subjects;

import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import org.nrg.xnat.model.XnatSubjectI;

public class XnatSubject implements XnatSubjectI {

	public XnatSubject() {
	}

	public XnatSubject(final XnatSubjectI subject) {
		_id = subject.getId();
		_project = subject.getProject();
		_label = subject.getLabel();
		_insertDate = subject.getInsertDate();
		_insertUser = subject.getInsertUser();
		
		
	}

	public XnatSubject(final ResultSet resultSet) throws SQLException {
		_id = resultSet.getString(1);
		_project = resultSet.getString(2);
		_label = resultSet.getString(3);
		_insertDate = resultSet.getDate(4);
		_insertUser = resultSet.getString(5);
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
}
