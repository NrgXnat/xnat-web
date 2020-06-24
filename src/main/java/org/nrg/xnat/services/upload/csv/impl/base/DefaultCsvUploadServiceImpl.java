/**
 * 
 */
package org.nrg.xnat.services.upload.csv.impl.base;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.transaction.Transactional;

import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.schema.SchemaElement;
import org.nrg.xft.ItemI;
import org.nrg.xft.XFT;
import org.nrg.xft.XFTItem;
import org.nrg.xft.collections.ItemCollection;
import org.nrg.xft.db.DBAction;
import org.nrg.xft.db.ViewManager;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.ActionNameAbsent;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.IDAbsent;
import org.nrg.xft.event.persist.PersistentWorkflowUtils.JustificationAbsent;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.FieldNotFoundException;
import org.nrg.xft.exception.InvalidValueException;
import org.nrg.xft.exception.XFTInitException;
import org.nrg.xft.schema.Wrappers.GenericWrapper.GenericWrapperElement;
import org.nrg.xft.schema.Wrappers.GenericWrapper.GenericWrapperField;
import org.nrg.xft.schema.design.SchemaElementI;
import org.nrg.xft.search.CriteriaCollection;
import org.nrg.xft.search.ItemSearch;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.FieldMapping;
import org.nrg.xft.utils.FileUtils;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xft.utils.ValidationUtils.ValidationResults;
import org.nrg.xft.utils.ValidationUtils.XFTValidator;
import org.nrg.xnat.daos.CsvTemplateDAO;
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

		return convertCsvTemplateToTemplateData(templates);
	}

	@Override
	public void addTemplate(CsvTemplate templateDefination) {

		UserI user = XDAT.getUserDetails();
		templateDefination.setUser(user.getUsername());

		if (templateDefination.getTemplate().contains("on")) {
			templateDefination.getTemplate().remove("on");
		}

		log.info("templateDefination is " + templateDefination.toString());
		getDao().create(templateDefination);

	}

	@Override
	public List<TemplateData> getTemplatesByProjectId(String id) {

		log.info("Project id passed is " + id);
		Set<CsvTemplate> templates = new HashSet<>(getDao().findByProperty("_project", id));
		log.info("Templates returned are " + templates.toString());

		return convertCsvTemplateToTemplateData(templates);
	}

	private List<TemplateData> convertCsvTemplateToTemplateData(Set<CsvTemplate> templates) {

		List<TemplateData> templateDatas = new ArrayList<>();

		for (CsvTemplate csvTemplate : templates) {
			TemplateData templateData = new TemplateData();

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

	}

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
	public ValidationResult validateData(String id, String projectId, MultipartFile multipartFile) {

		log.info("Project Id passed is " + projectId);
		ValidationResult result = new ValidationResult();

		Long templateId = Long.parseLong(id);

		CsvTemplate template = getDao().findTemplateById(templateId);
		result.setValidData(true);

		try {
			File file = multipartToFile(multipartFile, multipartFile.getOriginalFilename());
			List<List<String>> rows = FileUtils.CSVFileToArrayList(file);
			result = validation(template, rows, projectId, template.getTemplate());

//			deleting the temporary file
			file.delete();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static File multipartToFile(MultipartFile multipart, String fileName) throws IOException {
		File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
		multipart.transferTo(convFile);
		return convFile;
	}

	public Map<String, ArrayList<Object>> getAttributes(FieldMapping fm) throws Exception {

		Hashtable<String, ArrayList<Object>> all = new Hashtable<>();
		Hashtable<String, ArrayList<String>> extendable = new Hashtable<>();
		ArrayList<String> cleaned = new ArrayList<>();
		ArrayList<String> required = new ArrayList<>();

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

		ArrayList<String> toRemove = new ArrayList<>();
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

		ArrayList<Object> temp = new ArrayList<>();
		temp.add(cleaned);
		temp.add(extendable);
		temp.add(required);
		all.put(root, temp);

		for (Map.Entry<String, ArrayList<String>> entry : extendable.entrySet()) {
			for (String relation : entry.getValue()) {
				root = relation;
				if (!all.containsKey(root)) {
					gwe = GenericWrapperElement.GetElement(root);
					cleaned = new ArrayList<>();
					extendable = new Hashtable<>();

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

					toRemove = new ArrayList<>();
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

					temp = new ArrayList<>();
					temp.add(cleaned);
					temp.add(extendable);
					all.put(root, temp);
				}
			}

		}
		return all;
	}

	@Override
	public File downloadTemplate(TemplateDto templateDto) {

		File file = new File(System.getProperty("java.io.tmpdir") + "/" + templateDto.getXsiType());

		try (FileWriter writer = new FileWriter(file);) {
			String templateString = convertListToCommaSeperatedString(templateDto);
			log.info("string is " + templateString);
			writer.append(templateString);

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

	private List<List<String>> doStore(CsvTemplate template, List<List<String>> rows, String project,
			List<String> fields) throws XFTInitException, ElementNotFoundException, JustificationAbsent,
			ActionNameAbsent, IDAbsent, Exception {
		String rootElementName = template.getXsiType();
		GenericWrapperElement.GetElement(rootElementName);
		List<List<String>> displaySummary = new ArrayList<>();
		rows.get(0).add("Status");
		displaySummary.add(rows.get(0));
		rows.remove(0);
		UserI user = XDAT.getUserDetails();
		Iterator<List<String>> iter = rows.iterator();
		while (iter.hasNext()) {
			List<String> rowSummary = new ArrayList<>();
			List<String> row = iter.next();
			XFTItem item = XFTItem.NewItem(rootElementName, user);
			Iterator<String> iter2 = row.iterator();
			int columnIndex = 0;
			while (iter2.hasNext()) {
				String column = (String) iter2.next();
				String xmlPath = (String) fields.get(columnIndex);

				if (!column.equals("")) {
					rowSummary.add(column);

					GenericWrapperField gwf = null;
					try {
						gwf = GenericWrapperElement.GetFieldForXMLPath(xmlPath);
					} catch (Exception ignored) {
					}

					if (gwf != null && gwf.getBaseElement() != null && !gwf.getBaseElement().equals("")) {
						try {
							ItemSearch search = ItemSearch.GetItemSearch(gwf.getBaseElement(), user);
							SchemaElement se = SchemaElement.GetElement(gwf.getBaseElement());
							if ((project != null && !project.equals(""))
									&& se.hasField(se.getFullXMLName() + "/sharing/share/project")) {
								CriteriaCollection cc = new CriteriaCollection("OR");
								cc.addClause(se.getFullXMLName() + "/" + gwf.getBaseCol(), column);

								CriteriaCollection sub = new CriteriaCollection("AND");
								sub.addClause(se.getFullXMLName() + "/sharing/share/project", project);
								sub.addClause(se.getFullXMLName() + "/sharing/share/label", column);
								cc.add(sub);

								sub = new CriteriaCollection("AND");
								sub.addClause(se.getFullXMLName() + "/project", project);
								sub.addClause(se.getFullXMLName() + "/label", column);
								cc.add(sub);

								search.add(cc);
							} else {
								search.addCriteria(se.getFullXMLName() + "/" + gwf.getBaseCol(), column);
							}

							ItemCollection items = search.exec(false);

							if (items.size() == 1) {
								item.setProperty(xmlPath, items.getFirst().getProperty("ID"));
								columnIndex++;
								continue;
							}
						} catch (Exception ignored) {
						}

					}

					try {
						item.setProperty(xmlPath, column);
					} catch (FieldNotFoundException | InvalidValueException e) {
						log.error("", e);
					}
				}
				columnIndex++;
			}

			if (project != null && !project.equals("")) {
				SchemaElement se = SchemaElement.GetElement(rootElementName);

				try {
					String id = item.getStringProperty("ID");
					if (item.getStringProperty("project") == null) {
						item.setProperty(rootElementName + "/project", project);
						if (item.getStringProperty("label") == null) {
							item.setProperty(rootElementName + "/label", id);
						}
					} else {
						if (item.getStringProperty("project").equals(project)) {
							if (item.getStringProperty("label") == null) {
								item.setProperty(rootElementName + "/label", id);
							}
						} else {
							item.setProperty(rootElementName + "/sharing/share/project", project);
							item.setProperty(rootElementName + "/sharing/share/label", id);
						}
					}

					ItemSearch search = ItemSearch.GetItemSearch(rootElementName, user);
					CriteriaCollection cc = new CriteriaCollection("OR");
					cc.addClause(se.getFullXMLName() + "/ID", id);

					CriteriaCollection sub = new CriteriaCollection("AND");
					sub.addClause(se.getFullXMLName() + "/sharing/share/project", project);
					sub.addClause(se.getFullXMLName() + "/sharing/share/label", id);
					cc.add(sub);

					sub = new CriteriaCollection("AND");
					sub.addClause(se.getFullXMLName() + "/project", project);
					sub.addClause(se.getFullXMLName() + "/label", id);
					cc.add(sub);

					search.add(cc);
					ItemCollection items = search.exec(false);

					if (items.size() > 0) {
						item.setProperty("ID", items.getFirst().getProperty("ID"));
					} else {
						if (item.getStringProperty("label") != null
								&& item.getStringProperty("label").equals(item.getStringProperty("ID"))) {
							ItemI om = BaseElement.GetGeneratedItem(item);
							Class c = om.getClass();
							Object[] intArgs = new Object[] {};
							Class[] intArgsClass = new Class[] {};

							String newID = null;
							try {
								Method m = c.getMethod("CreateNewID", intArgsClass);
								if (m != null) {
									try {
										try {
											newID = (String) m.invoke(null, intArgs);
										} catch (RuntimeException e3) {
											log.error("", e3);
										}
									} catch (IllegalArgumentException | InvocationTargetException e2) {
										log.error("", e2);
									}
								}
							} catch (SecurityException | NoSuchMethodException e1) {
								log.error("", e1);
							}

							if (newID != null) {
								item.setProperty("ID", newID);
							} else {
								item.setProperty("ID", XFT.CreateIDFromBase(XDAT.getSiteConfigPreferences().getSiteId(),
										5, "ID", se.getSQLName(), null, null));
							}
						}
					}
				} catch (FieldNotFoundException | InvalidValueException  e) {
					log.error("", e);
				}catch(Exception e) {
					log.error("", e);
				}
			}

			EventDetails eventDetails = EventUtils.newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.TYPE.WEB_FORM,
					"Upload Spreadsheet", null, null);

			PersistentWorkflowI wrk = PersistentWorkflowUtils.buildOpenWorkflow(user, item, eventDetails);

			try {
				SaveItemHelper.unauthorizedSave(item, user, false, false, wrk.buildEvent());
				PersistentWorkflowUtils.complete(wrk, wrk.buildEvent());
				rowSummary.add("Successful");
			} catch (Throwable e1) {
				log.error("", e1);
				PersistentWorkflowUtils.fail(wrk, wrk.buildEvent());
				rowSummary.add(e1.getMessage());
			}

			displaySummary.add(rowSummary);
		}
		return displaySummary;
	}

	@Override
	public List<Map<String, String>> submitData(String id, String projectId, MultipartFile multipartFile) {

		Long templateId = Long.parseLong(id);

		CsvTemplate template = getDao().findTemplateById(templateId);

		List<List<String>> rows = null;
		List<Map<String, String>> summary = new ArrayList<>();

		try {
			File file = multipartToFile(multipartFile, multipartFile.getOriginalFilename());
			rows = FileUtils.CSVFileToArrayList(file);

			List<String> fields = template.getTemplate();
			rows = doStore(template, rows, projectId, fields);
			List<String> header = rows.get(0);
			rows.remove(0);
			
			for (List<String> row : rows) {
				Map<String, String> data = new LinkedHashMap<>();
				int columnNumber = 0;
				for (String datum : row) {
					data.putIfAbsent(header.get(columnNumber), datum);
					columnNumber++;
				}
				summary.add(data);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return summary;
	}

	private ValidationResult validation(CsvTemplate csvTemplate, List<List<String>> rows, String project,
			List<String> fields) {

		Map<Integer, List<String>> errorsDto = new Hashtable<>();
		int rowNumber = 0;
		List<String> errors = new ArrayList<>();

		List<List<String>> displaySummary = new ArrayList<>();
		rows.get(0).add("Status");
		displaySummary.add(rows.get(0));
		rows.remove(0);
		try {
			String rootElementName = csvTemplate.getXsiType();

			UserI user = XDAT.getUserDetails();
			Iterator<List<String>> iter = rows.iterator();
			while (iter.hasNext()) {

				List<String> row = iter.next();
				XFTItem item = XFTItem.NewItem(rootElementName, user);
				Iterator<String> iter2 = row.iterator();
				int columnIndex = 0;
				while (iter2.hasNext()) {
					String column =  iter2.next();
					String xmlPath = fields.get(columnIndex);
					if (!column.equals("")) {
						try {
							item.setProperty(xmlPath, column);
						} catch (FieldNotFoundException e) {
							log.error("", e);
							errors.add("Field Not Found");
						} catch (InvalidValueException e) {
							log.error("", e);
							errors.add("Invalid Value");
						}
					}
					columnIndex++;
				}
				XFTItem dbVersion = null;
				boolean matchedPK = false;
				if (project != null && !project.equals("")) {
					SchemaElement se = SchemaElement.GetElement(rootElementName);

					try {
						String id = item.getStringProperty("ID");

						ItemSearch search = ItemSearch.GetItemSearch(rootElementName, user);
						CriteriaCollection cc = new CriteriaCollection("OR");
						cc.addClause(se.getFullXMLName() + "/ID", id);

						CriteriaCollection sub = new CriteriaCollection("AND");
						sub.addClause(se.getFullXMLName() + "/sharing/share/project", project);
						sub.addClause(se.getFullXMLName() + "/sharing/share/label", id);
						cc.add(sub);

						sub = new CriteriaCollection("AND");
						sub.addClause(se.getFullXMLName() + "/project", project);
						sub.addClause(se.getFullXMLName() + "/label", id);
						cc.add(sub);

						search.add(cc);
						ItemCollection items = search.exec(false);

						if (items.size() > 0) {
							dbVersion = (XFTItem) items.getFirst();
							matchedPK = true;
						}
					} catch (FieldNotFoundException e) {
						log.error("", e);
						errors.add("Field Not Found");
					} catch (InvalidValueException e) {
						log.error("", e);
						errors.add("Invalid Value");
					} catch (Exception e) {
						log.error("", e);
						errors.add(e.toString());
					}
				} else {
					dbVersion = item.getCurrentDBVersion(false);
				}

				List<String> rowSummary = new ArrayList<>();

				if (dbVersion == null) {
					Iterator<String> fieldIter = fields.iterator();
					while (fieldIter.hasNext()) {

						String xmlPath = (String) fieldIter.next();
						GenericWrapperField gwf = null;
						StringBuffer sb = new StringBuffer();
						try {
							Object nValue = item.getProperty(xmlPath);
							try {
								gwf = GenericWrapperElement.GetFieldForXMLPath(xmlPath);

							} catch (FieldNotFoundException e) {
							}

							if (gwf != null && gwf.getBaseElement() != null && !gwf.getBaseElement().equals("")) {
								try {
									ItemSearch search = ItemSearch.GetItemSearch(gwf.getBaseElement(), user);
									SchemaElement se = SchemaElement.GetElement(gwf.getBaseElement());
									if ((project != null && !project.equals(""))
											&& se.hasField(se.getFullXMLName() + "/sharing/share/project")) {
										CriteriaCollection cc = new CriteriaCollection("OR");
										cc.addClause(se.getFullXMLName() + "/" + gwf.getBaseCol(), nValue);
										cc.addClause(se.getFullXMLName() + "/label", nValue);

										CriteriaCollection sub = new CriteriaCollection("AND");
										sub.addClause(se.getFullXMLName() + "/sharing/share/project", project);
										sub.addClause(se.getFullXMLName() + "/sharing/share/label", nValue);

										cc.add(sub);

										search.add(cc);
									} else {
										search.addCriteria(se.getFullXMLName() + "/" + gwf.getBaseCol(), nValue);
									}

									ItemCollection items = search.exec(false);

									if (items.size() > 0) {
										sb.append(nValue);
										rowSummary.add(sb.toString());
									} else {
										sb.append("Value does not match an existing " + gwf.getBaseElement() + "/"
												+ gwf.getBaseCol() + ".\" " + nValue);
										rowSummary.add(sb.toString());
									}
								} catch (Exception e) {
									log.error("", e);
									sb.append(nValue);
									errors.add(sb.toString());
								}

							} else {
								if (gwf != null) {
									ValidationResults vr = XFTValidator.ValidateValue(nValue, gwf.getRules(), "xs", gwf,
											xmlPath, gwf.getParentElement().getGenericXFTElement());
									if (!vr.isValid()) {
										sb.append(vr.getResults().get(0)[1] + " " + nValue);
										errors.add(sb.toString());
										continue;
									}
								}

								sb.append(nValue);
								rowSummary.add(sb.toString());
							}
						} catch (FieldNotFoundException e) {
							log.error("", e);
							sb.append("Unknown field: " + xmlPath + "ERROR");
							errors.add(sb.toString());
						}
					}
					rowSummary.add("NEW");
				} else {
					boolean modified = false;
					Iterator<String> fieldIter = fields.iterator();
					while (fieldIter.hasNext()) {

						String xmlPath = (String) fieldIter.next();
						GenericWrapperField gwf = null;
						StringBuffer sb = new StringBuffer();
						Object oValue = null;
						Object nValue = null;
						try {
							gwf = GenericWrapperElement.GetFieldForXMLPath(xmlPath);
						} catch (FieldNotFoundException e) {
						}

						try {
							oValue = dbVersion.getProperty(xmlPath);
							nValue = item.getProperty(xmlPath);
						} catch (FieldNotFoundException e) {
							log.error("", e);
							sb.append(xmlPath + nValue);
							errors.add(sb.toString());
							continue;
						}

						if (gwf != null) {
							ValidationResults vr = XFTValidator.ValidateValue(nValue, gwf.getRules(), "xs", gwf,
									xmlPath, gwf.getParentElement().getGenericXFTElement());
							if (!vr.isValid()) {
								sb.append(vr.getResults().get(0)[1]).append(nValue);
								errors.add(sb.toString());
								continue;
							}
						}

						if (oValue == null || oValue.equals("")) {
							if (nValue != null) {
								sb.append(nValue);
								modified = true;
								rowSummary.add(sb.toString());
								continue;
							} else {
								rowSummary.add("");
								continue;
							}
						} else if (nValue == null || nValue.equals("")) {
							if (oValue != null && !oValue.equals("")) {
								sb.append(oValue);
								modified = true;
								rowSummary.add(sb.toString());
								continue;
							}
						}
						try {
							String newValue = DBAction.ValueParser(nValue, gwf, false);
							String oldValue = DBAction.ValueParser(oValue, gwf, false);
							String type = null;
							if (gwf != null) {
								type = gwf.getXMLType().getLocalType();
							}

							if (!matchedPK || !xmlPath.equals(rootElementName + "/ID")) {
								if (DBAction.IsNewValue(type, oldValue, newValue)) {
									sb.append(nValue).append(oValue);
									modified = true;
								} else {
									sb.append(nValue);
								}
							} else {
								sb.append(nValue).append(oValue);
							}
							rowSummary.add(sb.toString());
						} catch (InvalidValueException e) {
							log.error("", e);
							errors.add("invalid value");
						}
					}
					if (modified)
						rowSummary.add("MODIFIED");
					else
						rowSummary.add("NO CHANGE");
				}

				if (!errors.isEmpty()) {
					errorsDto.put(rowNumber + 1, errors);
				}
				displaySummary.add(rowSummary);
				rowNumber++;
			}
		} catch (XFTInitException | ElementNotFoundException e) {
			log.error("", e);
		}

		return convertToJSONArray(displaySummary, errorsDto);
	}

	private ValidationResult convertToJSONArray(List<List<String>> rows, Map<Integer, List<String>> errorsDto) {
		ValidationResult result = new ValidationResult();
		if(errorsDto.isEmpty()) {
			result.setValidData(true);
		}
		List<Map<String, String>> maps = new ArrayList<>();
		List<String> header = rows.get(0);
		rows.remove(0);
		List<String> statTypes = Arrays.asList("NEW", "MODIFIED");
		int rowNumber = 1;

		for (List<String> row : rows) {
			Map<String, String> data = new LinkedHashMap<>();
			int columnNumber = 0;
			for (String datum : row) {
				List<String> errors;
				data.putIfAbsent(header.get(columnNumber), datum);
				columnNumber++;
				if (columnNumber == header.size() && !statTypes.contains(data.get("Status"))) {
					result.setValidData(false);
					errors = errorsDto.get(rowNumber);
					errors.add(data.get("Status"));
					errorsDto.put(rowNumber, errors);

				}
			}
			maps.add(data);
			rowNumber++;
		}
		result.setDataToUpload(maps);
		result.setErrors(errorsDto);

		return result;
	}
}
