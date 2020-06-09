/**
 * 
 */
package org.nrg.xnat.services.upload.csv.impl.base;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.transaction.Transactional;

import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.nrg.xdat.turbine.modules.actions.CSVUpload2;
import org.nrg.xft.utils.FieldMapping;
import org.nrg.xft.utils.FileUtils;
import org.nrg.xnat.daos.CsvTemplateDAO;
import org.nrg.xnat.dto.DataToUpload;
import org.nrg.xnat.dto.TemplateData;
import org.nrg.xnat.dto.TemplateDto;
import org.nrg.xnat.dto.ValidationResult;
import org.nrg.xnat.entities.CsvTemplate;
import org.nrg.xnat.services.upload.csv.CsvUploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 *
 */
@Slf4j
@Service
@Transactional
public class DefaultCsvUploadServiceImpl extends AbstractHibernateEntityService<CsvTemplate, CsvTemplateDAO>
		implements CsvUploadService {

	@Override
	public List<TemplateData> getTemplates() {
		Set<CsvTemplate> templates = new HashSet<>(getDao().findAll());

		List<TemplateData> templateDatas = convertCsvTemplateToTemplateData(templates);

		return templateDatas;
	}

	@Override
	public void addTemplate(CsvTemplate templateDefination) {
		log.info("templateDefination is " + templateDefination.toString());
		getDao().create(templateDefination);

	}

	@Override
	public List<TemplateData> getTemplatesByProjectId(String id) {

		log.info("Project id passed is " + id);
		Set<CsvTemplate> templates = new HashSet<>(getDao().findByProperty("_project", id));
		log.info("Templates returned are " + templates.toString());

		List<TemplateData> templateDatas = convertCsvTemplateToTemplateData(templates);

		return templateDatas;
	}

	private List<TemplateData> convertCsvTemplateToTemplateData(Set<CsvTemplate> templates) {

		List<TemplateData> templateDatas = new ArrayList<TemplateData>();

		for (CsvTemplate csvTemplate : templates) {
			TemplateData templateData = new TemplateData();
//			BeanUtils.copyProperties(csvTemplate, templateData);

			if (Objects.nonNull(csvTemplate)) {
				if (Objects.nonNull(csvTemplate.getId())) {
					templateData.setId(csvTemplate.getId());
				}
				if (Objects.nonNull(csvTemplate.getProject())) {
					templateData.setProject(csvTemplate.getProject());
				}
				if (Objects.nonNull(csvTemplate.getLabel())) {
					templateData.setLabel(csvTemplate.getLabel());
				}
				if (Objects.nonNull(csvTemplate.getUser())) {
					templateData.setUser(csvTemplate.getUser());
				}
				if (Objects.nonNull(csvTemplate.getXsiType())) {
					templateData.setXsiType(csvTemplate.getXsiType());
				}
				if (Objects.nonNull(csvTemplate.getTimestamp())) {
					templateData.setUpdateOn(csvTemplate.getTimestamp().toString());
				}
			}

			templateDatas.add(templateData);
		}

		return templateDatas;
	}

	/**
	 *
	 */
	@Override
	public TemplateDto getTemplateById(String id) {
		log.info("id passed is " + id);
		Long templateId = Long.parseLong(id);

		log.info("id converted is " + templateId);
		CsvTemplate csvTemplate = getDao().findById(templateId);
		Objects.requireNonNull(csvTemplate, "No Template for the given templateId:  \"" + templateId + "\" was found.");
		log.debug("Template found is " + csvTemplate);

		TemplateDto dto = convertCsvTemplateToTemplateDto(csvTemplate);

		log.debug("Template DTO is " + dto);

		return dto;
	}

	private TemplateDto convertCsvTemplateToTemplateDto(CsvTemplate csvTemplate) {

		TemplateDto templateDto = new TemplateDto();

//			TemplateData templateData = new TemplateData();
//			BeanUtils.copyProperties(csvTemplate, templateData);

		if (Objects.nonNull(csvTemplate)) {
			if (Objects.nonNull(csvTemplate.getId())) {
				templateDto.setId(csvTemplate.getId());
			}
			if (Objects.nonNull(csvTemplate.getProject())) {
				templateDto.setProject(csvTemplate.getProject());
			}
			if (Objects.nonNull(csvTemplate.getLabel())) {
				templateDto.setLabel(csvTemplate.getLabel());
			}
			if (Objects.nonNull(csvTemplate.getUser())) {
				templateDto.setUser(csvTemplate.getUser());
			}
			if (Objects.nonNull(csvTemplate.getXsiType())) {
				templateDto.setXsiType(csvTemplate.getXsiType());
			}
			if (Objects.nonNull(csvTemplate.getTemplate())) {
				templateDto.setTemplate(csvTemplate.getTemplate());
			}
		}

		return templateDto;
	}

	/**
	 *
	 */
	@Override
	public void updateTemplate(String id, CsvTemplate template) {
		log.info("id passed is " + id);
		Long templateId = Long.parseLong(id);

		log.info("id converted is " + templateId);
		CsvTemplate updatedTemplate = getDao().findById(templateId);
		Objects.requireNonNull(updatedTemplate,
				"No Template for the given templateId:  \"" + templateId + "\" was found.");

		if (Objects.nonNull(template)) {
			if (Objects.nonNull(template.getProject())) {
				updatedTemplate.setProject(template.getProject());
			}
			if (Objects.nonNull(template.getLabel())) {
				updatedTemplate.setLabel(template.getLabel());
			}
			if (Objects.nonNull(template.getUser())) {
				updatedTemplate.setUser(template.getUser());
			}
			if (Objects.nonNull(template.getXsiType())) {
				updatedTemplate.setXsiType(template.getXsiType());
			}
			if (Objects.nonNull(template.getTemplate())) {
				updatedTemplate.setTemplate(template.getTemplate());
			}
			updatedTemplate.setTimestamp(new Date());

		}

		getDao().update(updatedTemplate);

//		return String.format(" Template Id passed of template to be updated is %l", id);
	}

	/**
	 *
	 */
	@Override
	public void deleteTemplate(String id) {
		log.info("id passed is " + id);
		Long templateId = Long.parseLong(id);

		log.info("id converted is " + templateId);
		CsvTemplate csvTemplate = getDao().findById(templateId);
		Objects.requireNonNull(csvTemplate, "No Template for the given templateId:  \"" + templateId + "\" was found.");

		getDao().delete(csvTemplate);
		log.info("CSV Template Entity with id " + id + " is deleted.");
	}

	@Override
	public ValidationResult validateData(String projectId, MultipartFile multipartFile) {
		
		log.info("Project Id passed is " + projectId);
		ValidationResult result = new ValidationResult();

		List<DataToUpload> dataToUploads = new ArrayList<>();
		List<String> headers = new ArrayList<>();

		try {
			File file = multipartToFile(multipartFile, multipartFile.getOriginalFilename());

			if (file != null) {
				int lineCount = 0;
				int columnNumber = 0;
				result.setValidData(true);

				List<List<String>> rows = FileUtils.CSVFileToArrayList(file);
				for (List<String> row : rows) {
					columnNumber = 0;
					if (lineCount < 1) {
						headers = row;
					} else {
						for (String value : row) {
							DataToUpload dataToUpload = new DataToUpload();
							// some validation
							if (true) {
							}

							dataToUpload.setAttribute(headers.get(columnNumber));
							dataToUpload.setValue(value);
							dataToUpload.setDescription(String.format("The value for key %s for column %d is %s ",
									dataToUpload.getAttribute(), columnNumber+1, dataToUpload.getValue()));
							columnNumber++;
							dataToUploads.add(dataToUpload);
						}
					}

					lineCount++;
				}
			}
			
//			deleting the temporary file
			file.delete();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		result.setDataToUpload(dataToUploads);

		return result;
	}

	public static File multipartToFile(MultipartFile multipart, String fileName)
			throws IllegalStateException, IOException {
		File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
		multipart.transferTo(convFile);
		return convFile;
	}

	@Override
	public String submitData(String projectId, List<DataToUpload> dataToUpload) {
			CSVUpload2 upload2 = new CSVUpload2();
			
//			upload2.doStore(dataToUpload, );
		
		return String.format(" Project Id passed is %s", projectId);
	}

	@Override
	public FieldMapping getRoot(String root) {
		 FieldMapping fm = new FieldMapping();
         fm.setElementName(root);
         
         
		return fm;
	}

}
