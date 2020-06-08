/**
 * 
 */
package org.nrg.xnat.dto;

import java.util.List;

import lombok.Data;

/**
 * @author afour
 *
 */
@Data
public class ErrorDto {

	private int row;
	
	List<String> errorsFound;
	
}
