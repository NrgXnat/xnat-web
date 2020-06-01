/**
 * 
 */
package org.nrg.xnat.services.upload.csv;

import java.util.List;

import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.xnat.entities.CsvTemplate;
import org.springframework.stereotype.Service;

/**
 * 
 *
 */
public interface CsvUploadService {

	List<CsvTemplate> getTemplates();

	boolean addTemplate(CsvTemplate templateDefination);

	List<CsvTemplate> getTemplatesByProjectId(String projectId);

	CsvTemplate getTemplateById(String id) throws NotFoundException;

	String updateTemplate(String id);

	String deleteTemplate(String id);

	String validateData(String projectId);

	String submitData(String projectId);

}
