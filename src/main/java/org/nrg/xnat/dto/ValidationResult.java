/*
 * web: org.nrg.xnat.entities.HostInfo
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */
package org.nrg.xnat.dto;

import java.util.List;

import lombok.Data;

/**
 * @author afour
 *
 */
@Data
public class ValidationResult {

	private boolean validData;

	private List<ErrorDto> errors;

	private List<DataToUpload> dataToUpload;

}
