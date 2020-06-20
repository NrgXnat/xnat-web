/**
 * 
 */
package org.nrg.xnat.services.upload.csv;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.nrg.xft.utils.FieldMapping;
import org.nrg.xnat.dto.TemplateData;
import org.nrg.xnat.dto.TemplateDto;
import org.nrg.xnat.dto.ValidationResult;
import org.nrg.xnat.entities.CsvTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author afour
 *
 */
public interface CsvUploadService {

	/**
	 * @return list of all templates
	 */
	List<TemplateData> getTemplates();

	/**
	 * @param templateDefination JSON containing attributes for creating new
	 *                           template
	 */
	void addTemplate(CsvTemplate templateDefination);

	/**
	 * @param projectId Id of project
	 * @return list of templates associated with projectId passed
	 */
	List<TemplateData> getTemplatesByProjectId(String projectId);

	/**
	 * @param id id of template
	 * @return template whose Id is passed
	 */
	TemplateDto getTemplateById(String id);

	/**
	 * @param id id of template to be removed
	 */
	void deleteTemplate(String id);

	/**
	 * @param id	id of CsvTemplate
	 * @param projectId     id of project
	 * @param multipartFile	file containing the data to be uploaded as a csv file
	 * @return
	 */
	ValidationResult validateData(String id, String projectId, MultipartFile multipartFile);
//	ValidationResult validateData(String projectId, MultipartFile multipartFile);

//	List<List<String>> submitData(String projectId, List<DataToUpload> dataToUpload);

	/**
	 * @param id       id of template to be updated
	 * @param template JSON containing attributes to be updated
	 */
	void updateTemplate(String id, CsvTemplate template);

	/**
	 * @param template template which is to be download
	 * @return File file containing template
	 */
	File downloadTemplate(TemplateDto template);

	/**
	 * @param fm FieldMapping containing root data type as element name
	 * @return all attributes for a root type passed
	 * @throws Exception
	 */
	Hashtable<String, ArrayList<Object>> getAttributes(FieldMapping fm) throws Exception;

	/**
	 * @param projectId    id of project
	 * @param dataToUpload data to be uploaded in JSON format
	 * @return
	 */
	List<List<String>> submitData(String id, String projectId, MultipartFile file);


}
