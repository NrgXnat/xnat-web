package org.nrg.xnat.dto.resource;

import org.springframework.http.MediaType;

public class MediaTypeUtil {
	
	public static final String APPLICATION_XLIST = "application/xList";
	
	public static final String APPLICATION_ZIP = "application/zip";

	public static final String APPLICATION_XCAT = "application/xcat";

	public static final String APPLICATION_XAR = "application/xar";

	public static final String APPLICATION_DICOM = "application/dicom";

	public static final String APPLICATION_XMIRC = "application/x-mirc";

	public static final String APPLICATION_XMIRC_DICOM = "application/x-mirc-dicom";

	public static final String TEXT_CSV = "text/csv";
	
	public static final String APPLICATION_GNU_TAR = "application/gnutar";
	
	public static final String APPLICATION_TAR = "application/tar";

	public static MediaType getRequestedMediaType(String requested_format) {
		if (requested_format != null) {
			if (requested_format.equalsIgnoreCase("xml")) {
				return MediaType.TEXT_XML;
			} else if (requested_format.equalsIgnoreCase("json")) {
				return MediaType.APPLICATION_JSON;
			} else if (requested_format.equalsIgnoreCase("csv")) {
				return MediaType.parseMediaType(TEXT_CSV);
			} else if (requested_format.equalsIgnoreCase("txt")) {
				return MediaType.TEXT_PLAIN;
			} else if (requested_format.equalsIgnoreCase("html")) {
				return MediaType.TEXT_HTML;
			} else if (requested_format.equalsIgnoreCase("zip")) {
				return MediaType.parseMediaType(APPLICATION_ZIP);
			} else if (requested_format.equalsIgnoreCase("tar.gz")) {
				return  MediaType.parseMediaType(APPLICATION_GNU_TAR);
			} else if (requested_format.equalsIgnoreCase("tar")) {
				return  MediaType.parseMediaType(APPLICATION_TAR);
			} else if (requested_format.equalsIgnoreCase("xList")) {
				return  MediaType.parseMediaType(APPLICATION_XLIST);
			} else if (requested_format.equalsIgnoreCase("xcat")) {
				return  MediaType.parseMediaType(APPLICATION_XCAT);
			} else if (requested_format.equalsIgnoreCase("xar")) {
				return  MediaType.parseMediaType(APPLICATION_XAR);
			} else if (MediaType.valueOf(requested_format) != null) {
				return MediaType.valueOf(requested_format);
			}
		}
		return null;
	}
}
