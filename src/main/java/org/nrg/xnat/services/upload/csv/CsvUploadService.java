/**
 * 
 */
package org.nrg.xnat.services.upload.csv;

import java.util.List;

import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.xnat.dto.TemplateData;
import org.nrg.xnat.entities.CsvTemplate;

/**
 * 
 *
 */
public interface CsvUploadService {

	List<TemplateData> getTemplates();

	void addTemplate(CsvTemplate templateDefination);

	List<TemplateData> getTemplatesByProjectId(String projectId);

	CsvTemplate getTemplateById(String id) throws NotFoundException;

	void deleteTemplate(String id);

	String validateData(String projectId);

	String submitData(String projectId);

	/**
	 *
	 */
	void updateTemplate(String id, CsvTemplate template);

}
