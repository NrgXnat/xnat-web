/**
 * 
 */
package org.nrg.xnat.services.upload.csv;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.nrg.xft.utils.FieldMapping;
import org.nrg.xnat.dto.DataToUpload;
import org.nrg.xnat.dto.TemplateData;
import org.nrg.xnat.dto.TemplateDto;
import org.nrg.xnat.dto.ValidationResult;
import org.nrg.xnat.entities.CsvTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 * 
 *
 */
public interface CsvUploadService {

	List<TemplateData> getTemplates();

	void addTemplate(CsvTemplate templateDefination);

	List<TemplateData> getTemplatesByProjectId(String projectId);

	TemplateDto getTemplateById(String id);

	void deleteTemplate(String id);

	ValidationResult validateData(String projectId, MultipartFile multipartFile);

	String submitData(String projectId, List<DataToUpload> dataToUpload);

	/**
	 *
	 */
	void updateTemplate(String id, CsvTemplate template);

	File downloadTemplate(TemplateDto template);
	
	Hashtable<String,ArrayList<Object>> getAttributes(FieldMapping fm) throws Exception;

}
