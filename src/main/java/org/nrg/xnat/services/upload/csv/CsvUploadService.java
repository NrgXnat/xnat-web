/**
 * 
 */
package org.nrg.xnat.services.upload.csv;

/**
 * 
 *
 */
public interface CsvUploadService {

	String listTemplates();

	String addTemplate(String templateDefination);

	String listProjectTemplates(String projectId);

	String getSingleTemplate(String id);

	String updateTemplate(String id);

	String deleteTemplate(String id);

	String validateData(String projectId);

	String submitData(String projectId);

}
