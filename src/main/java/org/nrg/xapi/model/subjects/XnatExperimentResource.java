package org.nrg.xapi.model.subjects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import org.nrg.xnat.model.XnatExperimentResourceI;

public class XnatExperimentResource implements XnatExperimentResourceI {

	public XnatExperimentResource() {
	}
	
	
	public XnatExperimentResource(XnatExperimentResourceI experimentResource) {
		_xnatAbstractResourceId = experimentResource.getXnatAbstractResourceId();
		_categoryId =  Objects.isNull(experimentResource.getCategoryId()) || experimentResource.getCategoryId().isEmpty() ?  EMPTY_STRING : experimentResource.getCategoryId();
		_categoryDescription =  Objects.isNull(experimentResource.getCategoryDescription()) || experimentResource.getCategoryDescription().isEmpty() ?  EMPTY_STRING : experimentResource.getCategoryDescription();
		_elementName = Objects.isNull(experimentResource.getElementName()) || experimentResource.getElementName().isEmpty() ?  EMPTY_STRING : experimentResource.getElementName();
		_category =  Objects.isNull(experimentResource.getCategory()) || experimentResource.getCategory().isEmpty() ?  EMPTY_STRING : experimentResource.getCategory();
	}

	public XnatExperimentResource(ResultSet resultSet) throws SQLException {
		_xnatAbstractResourceId = resultSet.getInt(1);
		_categoryId = Objects.isNull(resultSet.getString(2)) || resultSet.getString(2).isEmpty() ?  EMPTY_STRING : resultSet.getString(2);
		_categoryDescription =  Objects.isNull(resultSet.getString(3)) || resultSet.getString(3).isEmpty() ?  EMPTY_STRING : resultSet.getString(3);
		_elementName = Objects.isNull(resultSet.getString(4)) || resultSet.getString(4).isEmpty() ?  EMPTY_STRING : resultSet.getString(4);
		_elementName = Objects.isNull(resultSet.getString(5)) || resultSet.getString(5).isEmpty() ?  EMPTY_STRING : resultSet.getString(5);
		
	}
	@Override
	public int getXnatAbstractResourceId() {
		return _xnatAbstractResourceId;
	}

	@Override
	public void setXnatAbstractResourceId(int xnatAbstractResourceId) {
		_xnatAbstractResourceId = xnatAbstractResourceId;
	}

	@Override
	public String getCategoryId() {
		return _categoryId;
	}

	@Override
	public void setCategoryId(String categoryId) {
		_categoryId = categoryId;
	}

	@Override
	public String getCategoryDescription() {
		return _categoryDescription;
	}

	@Override
	public void setCategoryDescription(String categoryDescription) {
		_categoryDescription = categoryDescription;
	}

	@Override
	public String getCategory() {
		return _category;
	}

	@Override
	public void setCategory(String category) {
		_category = category;
	}

	@Override
	public String getElementName() {
		return _elementName;
	}

	@Override
	public void setElementName(String elementName) {
		_elementName = elementName;
	}
	
	@Override
	public int getFileCount() {
		return _fileCount;
	}

	@Override
	public void setFileCount(int fileCount) {
		_fileCount = fileCount;
	}

	@Override
	public long getFileSize() {
		return _fileSize;
	}

	@Override
	public void setFileSize(long fileSize) {
		_fileSize = fileSize;
	}

	@Override
	public String getFormat() {
		return _format;
	}

	@Override
	public void setFormat(String format) {
		_format = format;
	}

	@Override
	public String getContent() {
		return _content;
	}

	@Override
	public void setContent(String content) {
		_content = content;
	}

	@Override
	public String getTags() {
		return _tags;
	}

	@Override
	public void setTags(String tags) {
		_tags = tags;
	}

	private int _xnatAbstractResourceId;
	private String _categoryId;
	private String _categoryDescription;
	private String _category;
	private String _elementName;
	private int _fileCount;
	private long _fileSize;
	private String _format;
	private String _content;
	private String _tags;
	private final static String EMPTY_STRING = ""; 

}
