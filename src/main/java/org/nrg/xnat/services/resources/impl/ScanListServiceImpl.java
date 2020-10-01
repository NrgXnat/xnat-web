/**
 * 
 */
package org.nrg.xnat.services.resources.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.XFTTable;
import org.nrg.xft.db.ViewManager;
import org.nrg.xft.search.CriteriaCollection;
import org.nrg.xft.search.QueryOrganizer;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.xmlpath.XMLPathShortcuts;
import org.nrg.xnat.services.resources.ScanListService;
import org.nrg.xnat.services.resources.util.ItemXMLRepresentationUtil;
import org.nrg.xnat.services.resources.util.JSONTableRepresentationUtil;
import org.nrg.xnat.services.resources.util.ResourceXapiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author afour
 *
 */
@Service
@Slf4j
public class ScanListServiceImpl extends ResourceXapiUtil implements ScanListService {

	XnatProjectdata proj = null;
	XnatSubjectdata sub = null;
	XnatImagesessiondata session = null;
	XnatImagescandata scan = null;

	@Autowired
	public ScanListServiceImpl() {
		fieldMapping.putAll(XMLPathShortcuts.getInstance().getShortcuts(XnatImagescandata.SCHEMA_ELEMENT_NAME, true));
	}
	
	@Override
	public String getScanResource(UserI user, String accessedId) throws IOException {
		session = XnatImagesessiondata.getXnatImagesessiondatasById(accessedId, user, false);

		if (session == null) {
			session = (XnatImagesessiondata) XnatImagesessiondata.GetExptByProjectIdentifier(proj.getId(), accessedId,
					user, false);
		}

		if (scan != null) {
			 return new ItemXMLRepresentationUtil(scan.getItem()).getText();
		} else {
			XFTTable table;
			try {
				final QueryOrganizer qo = new QueryOrganizer("xnat:imageScanData", user, ViewManager.ALL);

				qo.addField("xnat:imageScanData/ID");
				qo.addField("xnat:imageScanData/type");
				qo.addField("xnat:imageScanData/quality");
				qo.addField("xnat:imageScanData/extension_item/element_name");
				qo.addField("xnat:imageScanData/note");
				qo.addField("xnat:imageScanData/series_description");
				CriteriaCollection cc = new CriteriaCollection("AND");
				cc.addClause("xnat:imageScanData/image_session_id", session.getId());
				qo.setWhere(cc);

				String query = qo.buildQuery();

				table = XFTTable.Execute(query, user.getDBName(), user.getUsername());

				table = formatHeaders(table, qo, "xnat:imageScanData/ID",
						String.format("/data/experiments/%s/scans/", session.getId()));
			} catch (Exception e) {
				// logger.error("",e);
				// getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
				return null;
			}

			Hashtable<String, Object> params = new Hashtable<String, Object>();
			if (table != null)
				params.put("totalRecords", table.size());
			return new JSONTableRepresentationUtil(table, null, params).getText();
		}
	}

	public ArrayList<String> columns = null;

}
