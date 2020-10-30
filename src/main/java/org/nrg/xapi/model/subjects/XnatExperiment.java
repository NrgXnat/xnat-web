package org.nrg.xapi.model.subjects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

import org.nrg.xnat.model.XnatExperimentI;

public class XnatExperiment implements XnatExperimentI {

	public XnatExperiment() {

	}

	public XnatExperiment(XnatExperimentI experiment) {
		_id = Objects.isNull(experiment.getId()) || experiment.getId().isEmpty() ?  EMPTY_STRING : experiment.getId();
		_project = Objects.isNull(experiment.getLable()) || experiment.getLable().isEmpty() ?  EMPTY_STRING : experiment.getLable();
		_date = experiment.getDate();
		_xsiType = Objects.isNull(experiment.getXsiType()) || experiment.getXsiType().isEmpty() ?  EMPTY_STRING : experiment.getXsiType();
		_lable = Objects.isNull(experiment.getLable()) || experiment.getLable().isEmpty() ?  EMPTY_STRING : experiment.getLable();
		_insertDate = experiment.getInsertDate();
	}

	public XnatExperiment(ResultSet resultSet) throws SQLException {
		_id = Objects.isNull(resultSet.getString(1)) || resultSet.getString(1).isEmpty() ?  EMPTY_STRING : resultSet.getString(1);
		_project = Objects.isNull(resultSet.getString(2)) || resultSet.getString(2).isEmpty() ?  EMPTY_STRING : resultSet.getString(2);
		_date = resultSet.getDate(3);
		_xsiType = Objects.isNull(resultSet.getString(4)) || resultSet.getString(4).isEmpty() ?  EMPTY_STRING : resultSet.getString(4);
		_lable = Objects.isNull(resultSet.getString(5)) || resultSet.getString(5).isEmpty() ?  EMPTY_STRING : resultSet.getString(5);
		_insertDate = resultSet.getDate(6);
	}

	@Override
	public String getId() {
		return _id;
	}

	@Override
	public void setId(String id) {
		_id = id;
	}

	@Override
	public String getLable() {
		return _lable;
	}

	@Override
	public void setLable(String lable) {
		_lable = lable;
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
	public Date getDate() {
		return _date;
	}

	@Override
	public void setDate(Date date) {
		_date = date;
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
	public String getXsiType() {
		return _xsiType;
	}

	@Override
	public void setXsiType(String xsiType) {
		_xsiType = xsiType;
	}

	protected String _id;
	protected String _lable;
	protected String _project;
	protected Date _date;
	protected Date _insertDate;
	protected String _xsiType;
	protected final static String EMPTY_STRING = ""; 

}
