/**
 * 
 */
package org.nrg.xnat.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author afour
 *
 */
@Data
@Accessors(prefix = "_")
public class TemplateData {
	
	private Long _id;

	private String _label;

	private String _xsiType;

	private String _user;

	private String _project;
	
	private String _updateOn;
	
	
}
