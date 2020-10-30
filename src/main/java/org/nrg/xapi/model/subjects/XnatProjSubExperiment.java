package org.nrg.xapi.model.subjects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import org.nrg.xnat.model.XnatProjSubExperimentI;

public class XnatProjSubExperiment extends XnatExperiment {

	public XnatProjSubExperiment() {

	}
	public XnatProjSubExperiment(XnatProjSubExperimentI experiment) {
		_id = Objects.isNull(experiment.getId()) || experiment.getId().isEmpty() ?  EMPTY_STRING : experiment.getId();
		_xnatSubjectAccessorDataId = Objects.isNull(experiment.getXnatSubjectAccessorDataId()) || experiment.getXnatSubjectAccessorDataId().isEmpty() ?  EMPTY_STRING : experiment.getXnatSubjectAccessorDataId();
		_project = Objects.isNull(experiment.getProject()) || experiment.getProject().isEmpty() ?  EMPTY_STRING : experiment.getProject();
		_date = experiment.getDate();
		_xsiType = Objects.isNull(experiment.getXsiType()) || experiment.getXsiType().isEmpty() ?  EMPTY_STRING : experiment.getXsiType();
		_lable = Objects.isNull(experiment.getLable()) || experiment.getLable().isEmpty() ?  EMPTY_STRING : experiment.getLable();
		_insertDate = experiment.getInsertDate();
	}

	public XnatProjSubExperiment(ResultSet resultSet) throws SQLException {
		_id = Objects.isNull(resultSet.getString(1)) || resultSet.getString(1).isEmpty() ?  EMPTY_STRING : resultSet.getString(1);
		_xnatSubjectAccessorDataId = Objects.isNull(resultSet.getString(2)) || resultSet.getString(2).isEmpty() ?  EMPTY_STRING : resultSet.getString(2);
		_project = Objects.isNull(resultSet.getString(3)) || resultSet.getString(3).isEmpty() ?  EMPTY_STRING : resultSet.getString(3);
		_date = resultSet.getDate(4);
		_xsiType = Objects.isNull(resultSet.getString(5)) || resultSet.getString(5).isEmpty() ?  EMPTY_STRING : resultSet.getString(5);
		_lable = Objects.isNull(resultSet.getString(6)) || resultSet.getString(6).isEmpty() ?  EMPTY_STRING : resultSet.getString(6);
		_insertDate = resultSet.getDate(7);
	}

	public String getXnatSubjectAccessorDataId() {
		return _xnatSubjectAccessorDataId;
	}
	public void setXnatSubjectAccessorDataId(String xnatSubjectAccessorDataId) {
		_xnatSubjectAccessorDataId = xnatSubjectAccessorDataId;
	}
	
	private String _xnatSubjectAccessorDataId;
	
}
