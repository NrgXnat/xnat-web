/**
 * 
 */
package org.nrg.xnat.services.upload.csv.impl.base;

import org.nrg.xnat.services.upload.csv.CsvUploadService;

/**
 * 
 *
 */
public class DefaultCsvUploadServiceImpl implements CsvUploadService {

	@Override
	public String listTemplates() {
		// TODO Auto-generated method stub
		return "Get successfull";
	}

	@Override
	public String addTemplate(String templateDefination) {
		// TODO Auto-generated method stub
		return String.format(" Template Defination is {0} ", templateDefination);
	}

	@Override
	public String listProjectTemplates(String projectId) {
		// TODO Auto-generated method stub
		return String.format(" Project Id passed is {0}", projectId);
	}

	@Override
	public String getSingleTemplate(String id) {
		// TODO Auto-generated method stub
		return String.format(" Template Id passed is {0}", id);
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
