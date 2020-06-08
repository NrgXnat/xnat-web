/*
 * web: org.nrg.xnat.entities.HostInfo
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */
package org.nrg.xnat.dto;

import lombok.Data;

/**
 * @author afour
 *
 */
@Data
public class DataToUpload {

	private String description;
	
	private String attribute;
	
	private String value;

}
