package org.nrg.xapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Prearchive {

	private boolean preventAnon;
	private String subject;
	//what to do with Capital text
	private String PROTOCOL;
	private String project;
	private String url;
	private String autoarchive;
	private String  VISIT;
	private boolean preventAutoCommit;
	private Timestamp uploaded;
	private String name;
	private String SOURCE;
	private String scanTime;
	private String folderName;
	private String tag;
	private String TIMEZONE;
	private Timestamp scanDate;
	private Timestamp lastmod;
	private String timestamp;
	private String status;


	public static Prearchive getPrearchiveData(ResultSet resultSet, ResultSetMetaData rsmd) throws SQLException {
		Prearchive prearchive = new Prearchive();
		int columnsNumber = rsmd.getColumnCount();
		for (int i = 1; i <= columnsNumber; i++) {
			String columnName = rsmd.getColumnName(i);
			switch(columnName){
				case "project":
					prearchive.setProject(StringUtils.defaultIfBlank(resultSet.getString(i), ""));
					break;
				case "timestamp":
					prearchive.setTimestamp(StringUtils.defaultIfBlank(resultSet.getString(i), ""));
					break;
				case "lastmod":
					prearchive.setLastmod(resultSet.getTimestamp(i));
					break;
				case "uploaded":
					prearchive.setUploaded(resultSet.getTimestamp(i));
					break;
				case "scan_date":
					prearchive.setScanDate(resultSet.getTimestamp(i));
					break;
				case "scan_time":
					prearchive.setScanTime(StringUtils.defaultIfBlank(resultSet.getString(i), ""));
					break;
				case "subject":
					prearchive.setSubject(StringUtils.defaultIfBlank(resultSet.getString(i), ""));
					break;
				case"foldername":
					prearchive.setFolderName(StringUtils.defaultIfBlank(resultSet.getString(i), ""));
					break;
				case "name":
					prearchive.setName(StringUtils.defaultIfBlank(resultSet.getString(i), ""));
					break;
				case "tag":
					prearchive.setTag(StringUtils.defaultIfBlank(resultSet.getString(i), ""));
					break;
				case "status":
					prearchive.setStatus(StringUtils.defaultIfBlank(resultSet.getString(i), ""));
					break;
				case "url":
					prearchive.setUrl(StringUtils.defaultIfBlank(resultSet.getString(i), ""));
					break;
				case "autoarchive":
					prearchive.setAutoarchive(StringUtils.defaultIfBlank(resultSet.getString(i), ""));
					break;
				case "prevent_anon":
					prearchive.setPreventAnon(resultSet.getBoolean(i));
					break;
				case "prevent_auot_commit":
					prearchive.setPreventAutoCommit(resultSet.getBoolean(i));
					break;
				case "source":
					prearchive.setSOURCE(StringUtils.defaultIfBlank(resultSet.getString(i), ""));
					break;
				case "visit":
					prearchive.setVISIT(StringUtils.defaultIfBlank(resultSet.getString(i), ""));
					break;
				case "protocol":
					prearchive.setPROTOCOL(StringUtils.defaultIfBlank(resultSet.getString(i), ""));
					break;
				case "timezone":
					prearchive.setTIMEZONE(StringUtils.defaultIfBlank(resultSet.getString(i), ""));
					break;
			}
		}
		return prearchive;
	}


}
