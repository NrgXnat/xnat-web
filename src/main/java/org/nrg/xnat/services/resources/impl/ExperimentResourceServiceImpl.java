/**
 * 
 */
package org.nrg.xnat.services.resources.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ServerException;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xft.XFTTable;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xnat.services.resources.ExperimentResourceService;
import org.nrg.xnat.services.resources.util.BeanRepresentationUtil;
import org.nrg.xnat.services.resources.util.ItemXMLRepresentationUtil;
import org.nrg.xnat.services.resources.util.JSONTableRepresentationUtil;
import org.nrg.xnat.services.resources.util.ResourceXapiUtil;
import org.nrg.xnat.utils.CatalogUtils;
import org.restlet.data.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author afour
 *
 */
@Service
@Slf4j
public class ExperimentResourceServiceImpl extends ResourceXapiUtil implements ExperimentResourceService {
	private final static Logger logger = LoggerFactory.getLogger(ExperimentResourceServiceImpl.class);

	private XFTTable catalogs = null;
	private XnatProjectdata proj = null;

	@Autowired
	public ExperimentResourceServiceImpl() {
	}

	@Override
	public String getExperimentResource(String experimentId, String resourceId) throws IOException {
		if (experimentId != null && resourceId != null) {
			return getExperimentResourceByIds(experimentId, resourceId);
		} else if (experimentId != null) {
			return getExperimentById(experimentId);
		}
		return "invalid resource";
	}

	private String getExperimentById(String experimentId) throws IOException {
		XFTTable table = null;
		if (experimentId != null) {
			expt = XnatExperimentdata.getXnatExperimentdatasById(experimentId, getUser(), false);
		}
		if (expt != null) {
			try {
				table = loadCatalogs(null, false, true);// isQueryVariableTrue("all") -> true
			} catch (Exception e) {
				logger.error("", e);
			}
		}

		final boolean fileStats = false; // isQueryVariableTrue("file_stats");
		final boolean cacheFileStats = false;// isQueryVariableTrue("cache_file_stats");
		final Hashtable<String, Object> params = new Hashtable<>();
		params.put("title", "Resources");

		if (table != null) {
			table = CatalogUtils.populateTable(table, getUser(), null, cacheFileStats);

			// If table.rows() is null, set recordCount to 0
			final ArrayList<Object[]> records = table.rows();
			final int recordCount = (records != null) ? records.size() : 0;

			if (logger.isDebugEnabled()) {
				logger.debug("Found a total of " + recordCount + " records");
			}
			params.put("totalRecords", recordCount);
		}
		return new JSONTableRepresentationUtil(table, null, params).getText();
	}

	private String getExperimentResourceByIds(String experimentId, String resourceId) throws IOException {
		List<String> resourceIds = new ArrayList<>();
		List<XnatAbstractresource> resources = new ArrayList<>();
		XFTTable table = null;
		resourceIds.add(resourceId);
		getAllMatches(resourceIds, resources);

		if (experimentId != null) {
			expt = XnatExperimentdata.getXnatExperimentdatasById(experimentId, getUser(), false);
		}
		if (expt != null) {
			try {
				table = loadCatalogs(resourceIds, false, true);// isQueryVariableTrue("all") -> true
			} catch (Exception e) {
				logger.error("", e);
			}
		}

		if (resources.size() == 1) {
			final XnatAbstractresource resource = resources.get(0);
			try {
				if (proj == null) {
					if (parent.getItem().instanceOf("xnat:experimentData")) {
						proj = ((XnatExperimentdata) parent).getPrimaryProject(false);
					}
				}

				if (resource.getItem().instanceOf("xnat:resourceCatalog")) {
					try {
						CatalogUtils.CatalogData catalogData = CatalogUtils.CatalogData.getOrCreateAndClean(
								proj.getRootArchivePath(), ((XnatResourcecatalog) resource), false, proj.getId()); // isQueryVariableTrue("includeRootPath")
						return new BeanRepresentationUtil(catalogData.catBean).getText();
					} catch (ServerException e) {
						log.error("Unable to find catalog file: ", e.getMessage());

					}
				} else {
					return new ItemXMLRepresentationUtil(resource.getItem()).getText();
				}
			} catch (ElementNotFoundException e) {
				log.error("", e);
			}
		}

		return "input null";
	}
	private void getAllMatches(List<String> resourceIds, List<XnatAbstractresource> resources) {
		resources.clear();
		try {
			catalogs = loadCatalogs(resourceIds, false, true);
			// setCatalogs(loadCatalogs(resourceIds, false, true));
		} catch (Exception e) {
			log.error("An error occurred trying to load catalogs from the resource IDs: {}", resourceIds, e);
		}
		initializeResourcesFromIdsAndCatalogs(resourceIds, resources);
	}

	private void initializeResourcesFromIdsAndCatalogs(List<String> resourceIds, List<XnatAbstractresource> resources) {
		if (catalogs != null && catalogs.size() > 0) {
			for (final Object[] row : catalogs.rows()) {
				final String id = Integer.toString((Integer) row[0]);
				final String label = (String) row[1];
				resources.addAll(
						Lists.transform(Lists.newArrayList(Iterables.filter(resourceIds, new Predicate<String>() {
							@Override
							public boolean apply(@Nullable final String resourceId) {
								return StringUtils.equalsAny(resourceId, id, label);
							}
						})), new Function<String, XnatAbstractresource>() {
							@Override
							public XnatAbstractresource apply(final String resourceId) {
								return XnatAbstractresource.getXnatAbstractresourcesByXnatAbstractresourceId(row[0],
										getUser(), false);
							}
						}));
			}
		}
	}
}
