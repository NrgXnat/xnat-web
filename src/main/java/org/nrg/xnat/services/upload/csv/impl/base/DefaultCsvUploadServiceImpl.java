/**
 * 
 */
package org.nrg.xnat.services.upload.csv.impl.base;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.nrg.xdat.turbine.modules.actions.CSVUpload2;
import org.nrg.xdat.turbine.utils.TurbineUtils;
import org.nrg.xft.db.ViewManager;
import org.nrg.xft.schema.Wrappers.GenericWrapper.GenericWrapperElement;
import org.nrg.xft.schema.Wrappers.GenericWrapper.GenericWrapperField;
import org.nrg.xft.schema.design.SchemaElementI;
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
					templateData.setUpdateOn(csvTemplate.getTimestamp().toInstant().toString());
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
									dataToUpload.getAttribute(), columnNumber + 1, dataToUpload.getValue()));
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

	/*
	 * @Override public FieldMapping getRoot(String root) { FieldMapping fm = new
	 * FieldMapping(); fm.setElementName(root);
	 * 
	 * 
	 * return fm; }
	 */
	public Hashtable<String, ArrayList<Object>> getAttributes(FieldMapping fm) throws Exception {
		Hashtable<String, ArrayList<Object>> all = new Hashtable<String, ArrayList<Object>>();
		Hashtable<String, ArrayList<String>> extendable = new Hashtable<String, ArrayList<String>>();
		ArrayList<String> cleaned = new ArrayList<String>();
		ArrayList<String> required = new ArrayList<String>();

		String fm_id = fm.getID();
		String root = fm.getElementName();

		GenericWrapperElement gwe = GenericWrapperElement.GetElement(root);
		for (String s : ViewManager.GetFieldNames(gwe, ViewManager.ACTIVE, false, true)) {
			s = root + "/" + GenericWrapperElement.GetCompactXMLPath(s);
			if ((!s.endsWith("/meta/last_modified")) && (!s.endsWith("/meta/status"))
					&& (!s.endsWith("/meta/activation_date")) && (!s.endsWith("/meta/insert_date"))
					&& (!s.endsWith("/meta/activation_user_xdat_user_id"))
					&& (!s.endsWith("/meta/insert_user_xdat_user_id")) && (!s.endsWith("/meta/origin"))
					&& (!s.endsWith("/meta/modified")) && (!s.endsWith("/meta/meta_data_id"))
					&& (!s.endsWith("/meta/shareable")) && (!s.endsWith("/extension")) && (!s.equals(root + "/project"))
					&& (!s.equals(root + "/ID")) && (!s.endsWith("_info"))
					&& (!s.endsWith("/extension_item/element_name"))
					&& (!s.endsWith("/extension_item/xdat_meta_element_id"))) {
				if (!cleaned.contains(s))
					cleaned.add(s);
			} else if (s.endsWith("/extension")) {
				String xmlPath = s.substring(0, s.length() - 10);
				if (xmlPath.indexOf("/") > -1) {
					GenericWrapperField f = GenericWrapperElement.GetFieldForXMLPath(xmlPath);
					if (f.isReference()) {
						if (extendable.get(xmlPath) == null) {
							extendable.put(xmlPath, new ArrayList<String>());
							extendable.get(xmlPath).add(f.getReferenceElementName().getFullForeignType());
						}
						for (SchemaElementI se : f.getReferenceElement().getGenericXFTElement()
								.getPossibleExtenders()) {
							extendable.get(xmlPath).add(se.getFullXMLName());
						}
					} else {
						if (!cleaned.contains(s))
							cleaned.add(s);
					}
				}
			} else if ((s.equals(root + "/project"))) {
				// context.put("hasProject",true);
			} else if (s.equals(root + "/ID")) {
				if (!required.contains(s))
					required.add(s);
			}
		}

		ArrayList<String> toRemove = new ArrayList<String>();
		for (String key : extendable.keySet()) {
			for (String value : cleaned) {
				if (value.startsWith(key)) {
					toRemove.add(value);
				}
			}
		}

		for (String key : toRemove) {
			cleaned.remove(key);
		}

		ArrayList<Object> temp = new ArrayList<Object>();
		temp.add(cleaned);
		temp.add(extendable);
		temp.add(required);
		all.put(root, temp);

		for (Map.Entry<String, ArrayList<String>> entry : extendable.entrySet()) {
			for (String relation : entry.getValue()) {
				root = relation;
				if (!all.containsKey(root)) {
					gwe = GenericWrapperElement.GetElement(root);
					cleaned = new ArrayList<String>();
					extendable = new Hashtable<String, ArrayList<String>>();

					for (String s : ViewManager.GetFieldNames(gwe, ViewManager.ACTIVE, false, true)) {
						s = root + "/" + GenericWrapperElement.GetCompactXMLPath(s);
						if ((!s.endsWith("/meta/last_modified")) && (!s.endsWith("/meta/status"))
								&& (!s.endsWith("/meta/activation_date")) && (!s.endsWith("/meta/insert_date"))
								&& (!s.endsWith("/meta/activation_user_xdat_user_id"))
								&& (!s.endsWith("/meta/insert_user_xdat_user_id")) && (!s.endsWith("/meta/origin"))
								&& (!s.endsWith("/meta/modified")) && (!s.endsWith("/meta/meta_data_id"))
								&& (!s.endsWith("/meta/shareable")) && (!s.endsWith("/extension"))
								&& (!s.endsWith("_info")) && (!s.endsWith("/extension_item/element_name"))
								&& (!s.endsWith("/extension_item/xdat_meta_element_id"))) {
							if (!cleaned.contains(s))
								cleaned.add(s);
						} else if (s.endsWith("/extension")) {
							String xmlPath = s.substring(0, s.length() - 10);
							if (xmlPath.indexOf("/") > -1) {
								GenericWrapperField f = GenericWrapperElement.GetFieldForXMLPath(xmlPath);
								if (f.isReference()) {
									if (extendable.get(xmlPath) == null) {
										extendable.put(xmlPath, new ArrayList<String>());
										extendable.get(xmlPath).add(f.getReferenceElementName().getFullForeignType());
									}
									for (SchemaElementI se : f.getReferenceElement().getGenericXFTElement()
											.getPossibleExtenders()) {
										extendable.get(xmlPath).add(se.getFullXMLName());
									}
								} else {
									if (!cleaned.contains(s))
										cleaned.add(s);
								}
							}
						}
					}

					toRemove = new ArrayList<String>();
					for (String key : extendable.keySet()) {
						for (String value : cleaned) {
							if (value.startsWith(key)) {
								toRemove.add(value);
							}
						}
					}

					for (String key : toRemove) {
						cleaned.remove(key);
					}

					temp = new ArrayList<Object>();
					temp.add(cleaned);
					temp.add(extendable);
					temp.add(new ArrayList());
					all.put(root, temp);
				}
			}

		}
		return all;
	}

	@Override
	public File downloadTemplate(TemplateDto templateDto) {

		File file =  new File(System.getProperty("java.io.tmpdir") + "/" + templateDto.getXsiType());

		try {
			String	templateString =  convertListToCommaSeperatedString(templateDto) ;
			
			log.info("string is "+ templateString);
			
			FileWriter writer = new FileWriter(file);
			writer.append(templateString);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return file;

	}

	private String convertListToCommaSeperatedString(TemplateDto templateDto) throws IOException {

		StringBuffer sb = new StringBuffer();
		List<String> fields = templateDto.getTemplate();
		for (int i = 0; i < fields.size(); i++) {
			String xmlPath = fields.get(i);
			if (i > 0)
				sb.append(", ");
			sb.append(xmlPath.substring(xmlPath.lastIndexOf("/") + 1));
		}
		
		return sb.toString();
	}

}
