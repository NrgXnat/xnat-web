package org.nrg.xnat.dto.prearchive;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrearchiveDto {

	private boolean prevent_anon;
	private String subject;
	private String PROTOCOL;
	private String project;
	private String url;
	private String autoarchive;
	private String  VISIT;
	private boolean prevent_auto_commit;
	private Timestamp uploaded;
	private String name;
	private String SOURCE;
	private String scan_time;
	private String folderName;
	private String tag;
	private String TIMEZONE;
	private Timestamp scan_date;
	private Timestamp lastmod;
	private String timestamp;
	private String status;
	
	public PrearchiveDto() {
	}

	public PrearchiveDto(boolean prevent_anon, String subject, String pROTOCOL, String project, String url,
			String autoarchive, String vISIT, boolean prevent_auto_commit, Timestamp uploaded, String name,
			String sOURCE, String scan_time, String folderName, String tag, String tIMEZONE, Timestamp scan_date,
			Timestamp lastmod, String timestamp, String status) {
		
		this.prevent_anon = prevent_anon;
		this.subject = subject;
		PROTOCOL = pROTOCOL;
		this.project = project;
		this.url = url;
		this.autoarchive = autoarchive;
		VISIT = vISIT;
		this.prevent_auto_commit = prevent_auto_commit;
		this.uploaded = uploaded;
		this.name = name;
		SOURCE = sOURCE;
		this.scan_time = scan_time;
		this.folderName = folderName;
		this.tag = tag;
		TIMEZONE = tIMEZONE;
		this.scan_date = scan_date;
		this.lastmod = lastmod;
		this.timestamp = timestamp;
		this.status = status;
	}

	public static PrearchiveDto getPrearchiveData(ResultSet resultSet, ResultSetMetaData rsmd) throws SQLException {
		PrearchiveDto prearchiveDto = new PrearchiveDto(); 
		int columnsNumber = rsmd.getColumnCount();
	   for (int i = 1; i <= columnsNumber; i++) {
	     String columnName = rsmd.getColumnName(i);
		if(columnName.equalsIgnoreCase("project")) {
			prearchiveDto.setProject(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("timestamp")) {
			prearchiveDto.setTimestamp(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("lastmod")) {
			prearchiveDto.setLastmod(resultSet.getTimestamp(i));
		}else if(columnName.equalsIgnoreCase("uploaded")) {
			prearchiveDto.setUploaded(resultSet.getTimestamp(i));
		}else if(columnName.equalsIgnoreCase("scan_date")) {
			prearchiveDto.setScan_date(resultSet.getTimestamp(i));
		}else if(columnName.equalsIgnoreCase("scan_time")) {
			prearchiveDto.setScan_time(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("subject")) {
			prearchiveDto.setSubject(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("foldername")) {
			prearchiveDto.setFolderName(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("name")) {
			prearchiveDto.setName(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("tag")) {
			prearchiveDto.setTag(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("status")) {
			prearchiveDto.setStatus(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("url")) {
			prearchiveDto.setUrl(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("autoarchive")) {
			prearchiveDto.setAutoarchive(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("prevent_anon")) {
			prearchiveDto.setPrevent_anon(resultSet.getBoolean(i));
		}else if(columnName.equalsIgnoreCase("prevent_auot_commit")) {
			prearchiveDto.setPrevent_auto_commit(resultSet.getBoolean(i));
		}else if(columnName.equalsIgnoreCase("source")) {
			prearchiveDto.setSOURCE(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("visit")) {
			prearchiveDto.setVISIT(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("protocol")) {
			prearchiveDto.setPROTOCOL(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("timezone")) {
			prearchiveDto.setTIMEZONE(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}
	}
	return prearchiveDto;
}
	
	
	
}
