/**
 * 
 */
package org.nrg.xnat.services.upload.csv.impl.base;

import java.util.List;

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
public class DefaultCsvUploadServiceImpl extends AbstractHibernateEntityService<CsvTemplate, CsvTemplateDAO> implements CsvUploadService {

	@Override
	public List<CsvTemplate> getTemplates() {
		// TODO Auto-generated method stub
		return getDao().findAll();
	}

	@Override
	public boolean addTemplate(CsvTemplate templateDefination) {
		// TODO Auto-generated method stub
		boolean flag = false;
				
		  getDao().create(templateDefination);
		  flag = true;
		  
		return flag;
	}

	@Override
	public List<CsvTemplate> getTemplatesByProjectId(String id) {
		// TODO Auto-generated method stub
		
//		Long projectId = Long.getLong(id);
		
		List<CsvTemplate> templates = getDao().findByProperty("_project", id);
		
		return templates;
	}

	@Override
	public CsvTemplate getTemplateById(String id) throws NotFoundException {
		// TODO Auto-generated method stub
		Long templateId = Long.getLong(id);
		return getDao().findById(templateId);
	}

	@Override
	public String updateTemplate(String id) {
		// TODO Auto-generated method stub
		return String.format(" Template Id passed of template to be updated is {0}", id);
	}

	@Override
	public String deleteTemplate(String id) {
		// TODO Auto-generated method stub
		return String.format(" Template Id passed of template to be deleted is {0}", id);
	}

	@Override
	public String validateData(String projectId) {
		// TODO Auto-generated method stub
		return String.format(" Project Id passed is {0}", projectId);
	}

	@Override
	public String submitData(String projectId) {
		// TODO Auto-generated method stub
		return String.format(" Project Id passed is {0}", projectId);
	}

}
