package org.nrg.xnat.dto.prearchive;

import java.sql.Timestamp;
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

	
	
	
}
