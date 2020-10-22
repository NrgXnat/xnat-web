package org.nrg.xapi.model.subjects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import org.nrg.xnat.model.XnatScanI;

public class XnatScan implements XnatScanI {

	public XnatScan() {
	}

	public XnatScan(XnatScanI scan) {
		_xnatImageScanDataId = Objects.nonNull(scan.getXnatImageScanDataId())?scan.getXnatImageScanDataId() : null;
		_id = Objects.isNull(scan.getId()) || scan.getId().isEmpty() ?  EMPTY_STRING : scan.getId();
		_type = Objects.isNull(scan.getType()) || scan.getType().isEmpty() ?  EMPTY_STRING : scan.getType();
		_quality = Objects.isNull(scan.getQuality()) || scan.getQuality().isEmpty() ?  EMPTY_STRING : scan.getQuality();
		_xsiType = Objects.isNull(scan.getXsiType()) || scan.getXsiType().isEmpty() ?  EMPTY_STRING : scan.getXsiType();
		_note = Objects.isNull(scan.getNote()) || scan.getNote().isEmpty() ?  EMPTY_STRING : scan.getNote();
		_seriesDescription = Objects.isNull(scan.getSeriesDescription()) || scan.getSeriesDescription().isEmpty() ?  EMPTY_STRING : scan.getSeriesDescription();
	}

	public XnatScan(ResultSet resultSet) throws SQLException {
		_xnatImageScanDataId =  Objects.nonNull(resultSet.getInt(1))? resultSet.getInt(1) : null;
		_id = Objects.isNull(resultSet.getString(2)) || resultSet.getString(2).isEmpty() ?  EMPTY_STRING : resultSet.getString(2);
		_type = Objects.isNull(resultSet.getString(3)) || resultSet.getString(3).isEmpty() ?  EMPTY_STRING : resultSet.getString(3);
		_quality = Objects.isNull(resultSet.getString(4)) || resultSet.getString(4).isEmpty() ?  EMPTY_STRING : resultSet.getString(4);
		_xsiType = Objects.isNull(resultSet.getString(5)) || resultSet.getString(5).isEmpty() ?  EMPTY_STRING : resultSet.getString(5);
		_note = Objects.isNull(resultSet.getString(6)) || resultSet.getString(6).isEmpty() ?  EMPTY_STRING : resultSet.getString(6);
		_seriesDescription = Objects.isNull(resultSet.getString(7)) || resultSet.getString(7).isEmpty() ?  EMPTY_STRING : resultSet.getString(7);
	}

	@Override
	public int getXnatImageScanDataId() {
		return _xnatImageScanDataId;
	}

	@Override
	public void setXnatImageScanDataId(int xnatImageScanDataId) {
		_xnatImageScanDataId = xnatImageScanDataId;
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
	public String getType() {
		return _type;
	}

	@Override
	public void setType(String type) {
		_type = type;
	}

	public String getQuality() {
		return _quality;
	}

	@Override
	public void setQuality(String quality) {
		_quality = quality;
	}

	@Override
	public String getXsiType() {
		return _xsiType;
	}

	@Override
	public void setXsiType(String xsiType) {
		_xsiType = xsiType;
	}

	public String getNote() {
		return _note;
	}

	@Override
	public void setNote(String note) {
		_note = note;
	}

	public String getSeriesDescription() {
		return _seriesDescription;
	}

	@Override
	public void setSeriesDescription(String seriesDescription) {
		_seriesDescription = seriesDescription;
	}

	private int _xnatImageScanDataId;
	private String _id;
	private String _type;
	private String _quality;
	private String _xsiType;
	private String _note;
	private String _seriesDescription;
	private static final String EMPTY_STRING = "";

}
