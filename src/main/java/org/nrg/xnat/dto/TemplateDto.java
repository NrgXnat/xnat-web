/**
 * 
 */
package org.nrg.xnat.dto;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author afour
 *
 */

@Data
@Accessors(prefix = "_")
public class TemplateDto {
	private Long _id;

	private String _label;

	private String _xsiType;

	private String _user;

	private String _project;
	
	private List<String> _template;
}
