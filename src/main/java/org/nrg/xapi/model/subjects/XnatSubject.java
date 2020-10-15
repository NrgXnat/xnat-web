package org.nrg.xapi.model.subjects;

import java.io.Writer;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.nrg.xdat.om.base.BaseXnatSubjectdata;
import org.nrg.xnat.model.XnatSubjectI;

public class XnatSubject implements XnatSubjectI {

	public XnatSubject() {
	}

	public XnatSubject(final XnatSubjectI subject, final Collection<String> subjectProjects) {
		_id = subject.getId();
		_label = subject.getLabel();
	}

	public XnatSubject(final ResultSet resultSet) throws SQLException {
		_id = resultSet.getString(1);
		_label = resultSet.getString(2);
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

	private String _id;
	private String _label;
}
