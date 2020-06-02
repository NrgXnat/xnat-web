/**
 * 
 */
package org.nrg.xnat.services.upload.csv.impl.base;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.nrg.xnat.daos.CsvTemplateDAO;
import org.nrg.xnat.entities.CsvTemplate;
import org.nrg.xnat.services.upload.csv.CsvUploadService;
import org.springframework.stereotype.Service;

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
	public List<CsvTemplate> getTemplates() {
		return getDao().findAll();
	}

	@Override
	public void addTemplate(CsvTemplate templateDefination) {
		log.info("templateDefination is " + templateDefination.toString());
		getDao().create(templateDefination);

	}

	@Override
	public List<CsvTemplate> getTemplatesByProjectId(String id) {

		log.info("Project id passed is " + id);
		List<CsvTemplate> templates = getDao().findByProperty("_project", id);
		log.info("Templates returned are " + templates.toString());
		return templates;
	}

	/**
	 *
	 */
	@Override
	public CsvTemplate getTemplateById(String id) throws NotFoundException {
		log.info("id passed is " + id);
		Long templateId = Long.parseLong(id);

		log.info("id converted is " + templateId);
		CsvTemplate csvTemplate = getDao().findById(templateId);
		Objects.requireNonNull(csvTemplate, "No Template for the given templateId:  \"" + templateId + "\" was found.");

		log.debug("Template found is " + csvTemplate);
		return csvTemplate;
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
		Objects.requireNonNull(updatedTemplate, "No Template for the given templateId:  \"" + templateId + "\" was found.");

		 if(Objects.nonNull(template)) {
			 if(Objects.nonNull(template.getProject())) {
				 updatedTemplate.setProject(template.getProject());
			 }
			 if(Objects.nonNull(template.getLabel())) {
				 updatedTemplate.setLabel(template.getLabel());
			 }
			 if(Objects.nonNull(template.getUser())) {
				 updatedTemplate.setUser(template.getUser());
			 }
			 if(Objects.nonNull(template.getXsiType())) {
				 updatedTemplate.setXsiType(template.getXsiType());
			 }
			 if(Objects.nonNull(template.getTemplate())) {
				 updatedTemplate.setTemplate(template.getTemplate());
			 }
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
	public String validateData(String projectId) {
		// TODO Auto-generated method stub
		return String.format(" Project Id passed is %s", projectId);
	}

	@Override
	public String submitData(String projectId) {
		// TODO Auto-generated method stub
		return String.format(" Project Id passed is %s", projectId);
	}

}
