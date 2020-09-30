package org.nrg.xnat.services.resources.files.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.exceptions.NrgServiceError;
import org.nrg.framework.exceptions.NrgServiceRuntimeException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.ItemI;
import org.nrg.xft.XFTTable;
import org.nrg.xft.search.CriteriaCollection;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.XftStringUtils;
import org.nrg.xnat.restlet.representations.JSONTableRepresentation;
import org.nrg.xnat.services.resources.files.FileListService;
import org.nrg.xnat.utils.CatalogUtils;
import org.restlet.data.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileListServiceImpl implements FileListService {
	private final static Logger logger = LoggerFactory.getLogger(FileListServiceImpl.class);
	private UserI _user;
	private XnatProjectdata proj = null;
	private ArrayList<XnatImagescandata> scans = null;
	private ItemI security = null;
	private  String xmlPath = null;
	
	@Override
	public String getResourceFiles(UserI sessionUser, String assessedId, String scanId) {
		return null;
	}

	@Override
	public String getResources(UserI sessionUser, String assessedId, String scanId) throws IOException {
		final UserI user = getUser();
		ArrayList<XnatExperimentdata> assesseds = new ArrayList<>();
		XFTTable table = null;
		if (assessedId != null) {
			for (String s : XftStringUtils.CommaDelimitedStringToArrayList(assessedId)) {
				XnatExperimentdata assessed = XnatImagesessiondata.getXnatImagesessiondatasById(s, user, false);

				if (assessed != null && (proj != null && !assessed.hasProject(proj.getId()))) {
					assessed = null;
				}
				if (assessed == null && proj != null) {
					assessed = XnatImagesessiondata.GetExptByProjectIdentifier(proj.getId(), s, user, false);
				}
				if (assessed != null) {
					try {
						if (assessed.canRead(user)) {
							assesseds.add(assessed);
						}
					} catch (Exception ignored) {
					}
				}
			}
		}

		if (scanId != null && assesseds.size() > 0) {

			scanId = scanId.replace("[SLASH]", "/");// this is such an ugly hack. If a slash is included in the scan
													// type and thus in the URL, it breaks the GET command. Even if it
													// is properly escaped. So, I'm adding this alternative encoding of
													// slash to allow us to work around the issue. Hopefully Spring MVC
													// will eliminate it.

			CriteriaCollection cc = new CriteriaCollection("OR");
			for (XnatExperimentdata assessed : assesseds) {
				CriteriaCollection subcc = new CriteriaCollection("AND");
				subcc.addClause("xnat:imageScanData/image_session_ID", assessed.getId());
				if (!(scanId.equals("*") || scanId.equals("ALL"))) {
					if (!scanId.contains(",")) {
						subcc.addClause("xnat:imageScanData/ID", scanId);
					} else {
						CriteriaCollection subsubcc = new CriteriaCollection("OR");
						for (String s : XftStringUtils.CommaDelimitedStringToArrayList(scanId, true)) {
							subsubcc.addClause("xnat:imageScanData/ID", s);
						}
						subcc.add(subsubcc);
					}
				}
				cc.add(subcc);

				subcc = new CriteriaCollection("AND");
				subcc.addClause("xnat:imageScanData/image_session_ID", assessed.getId());
				if (!(scanId.equals("*") || scanId.equals("ALL"))) {
					if (!scanId.contains(",")) {
						if (scanId.equals("NULL")) {
							CriteriaCollection subsubcc = new CriteriaCollection("OR");
							subsubcc.addClause("xnat:imageScanData/type", "", " IS NULL ", true);
							subsubcc.addClause("xnat:imageScanData/type", "");
							subcc.add(subsubcc);
						} else {
							subcc.addClause("xnat:imageScanData/type", scanId.replace("[COMMA]", ","));
						}
					} else {
						CriteriaCollection subsubcc = new CriteriaCollection("OR");
						for (String s : XftStringUtils.CommaDelimitedStringToArrayList(scanId, true)) {
							if (s.equals("NULL")) {
								subsubcc.addClause("xnat:imageScanData/type", "", " IS NULL ", true);
								subsubcc.addClause("xnat:imageScanData/type", "");
							} else {
								subsubcc.addClause("xnat:imageScanData/type", s.replace("[COMMA]", ","));
							}
						}
						subcc.add(subsubcc);
					}
				}
				cc.add(subcc);
			}

			scans = XnatImagescandata.getXnatImagescandatasByField(cc, user, false);

		}
		if (scans.size() > 0) {
			try {
				table = loadCatalogs(null, false, true); // isQueryVariableTrue("all")
			} catch (Exception e) {
				logger.error("", e);
			}
		}

		final boolean fileStats = false; // isQueryVariableTrue("file_stats");
		final boolean cacheFileStats = false; // isQueryVariableTrue("cache_file_stats");

		final Hashtable<String, Object> params = new Hashtable<>();
		params.put("title", "Resources");

		if (table != null) {
			table = CatalogUtils.populateTable(table, user, proj, cacheFileStats);

			// If table.rows() is null, set recordCount to 0
			final ArrayList<Object[]> records = table.rows();
			final int recordCount = (records != null) ? records.size() : 0;

			if (logger.isDebugEnabled()) {
				logger.debug("Found a total of " + recordCount + " records");
			}
			params.put("totalRecords", recordCount);
		}

		return new JSONTableRepresentation(table, null, params, MediaType.APPLICATION_JSON).getText();
	}

	@Nonnull
	public UserI getUser() {
		try {
			_user = ObjectUtils.defaultIfNull(XDAT.getUserDetails(), Users.getGuest());
			return ObjectUtils.defaultIfNull(_user, Users.getGuest());
		} catch (UserNotFoundException | UserInitException e) {
			throw new NrgServiceRuntimeException(NrgServiceError.UserServiceError,
					"An error occurred retrieving the guest user.", e);
		}
	
	}

	public XFTTable loadCatalogs(final List<String> resourceIds, final boolean includeURI, final boolean allowAll)
			throws Exception {
		final StringBuilder query = new StringBuilder();
		final boolean hasResourceIds = resourceIds != null && !resourceIds.isEmpty();
		final boolean isInResource = StringUtils.equalsIgnoreCase("out", "in");

		final UserI user = getUser();
		if (!scans.isEmpty()) {
			final List<Integer> scanIds = Lists.transform(scans, new Function<XnatImagescandata, Integer>() {
				@Override
				public Integer apply(final XnatImagescandata scan) {
					return scan.getXnatImagescandataId();
				}
			});
			xmlPath = "xnat:imageScanData/file";
			query.append(STARTER_FIELDS);
			query.append(", 'scans'::TEXT AS category, scan.id::TEXT AS cat_id, scan.type::TEXT AS cat_desc");
			if (includeURI) {
				query.append(
						",'/experiments/' || scan.image_session_id || '/scans/' || scan.id || '/resources/' || abst.xnat_abstractresource_id AS resource_path");
			}
			query.append(
					" FROM xnat_abstractresource abst LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id LEFT JOIN xnat_imagescandata scan ON abst.xnat_imagescandata_xnat_imagescandata_id=scan.xnat_imagescandata_id WHERE xnat_imagescandata_xnat_imagescandata_id IN ('");
			query.append(StringUtils.join(scanIds, "', '"));
			query.append("') ");
			if (hasResourceIds) {
				query.append(" AND (").append(getResourceIdsWhereClause(resourceIds, "abst.xnat_abstractresource_id"))
						.append(")");
			}
		} else {
			query.append(STARTER_FIELDS);
			query.append(
					", 'resources'::TEXT AS category, NULL::TEXT AS cat_id, ' '::TEXT AS cat_desc FROM xnat_abstractresource abst LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id WHERE xnat_abstractresource_id IS NULL");
		}

		final String completedQuery = query.toString();
		logger.debug("Loading catalog for user '{}' using query: {}", user.getUsername(), completedQuery);
		return XFTTable.Execute(completedQuery, user.getDBName(), user.getUsername());
	}

	private static final String STARTER_FIELDS = "SELECT xnat_abstractresource_id, abst.label, xme.element_name ";

	/*private String getResourceIdsWhereClause(final List<String> resourceIds) {
		return getResourceIdsWhereClause(resourceIds, "map.xnat_abstractresource_xnat_abstractresource_id",
				"abst.label");
	}
*/
	private String getResourceIdsWhereClause(final List<String> resourceIds, final String idKey) {
		return getResourceIdsWhereClause(resourceIds, idKey, "abst.label");
	}

	private String getResourceIdsWhereClause(final List<String> resourceIds, final String idKey,
			final String labelKey) {
		// Numeric resource IDs are those that contain only digits.
		final List<String> numericIds = Lists.newArrayList(Iterables.filter(resourceIds, new Predicate<String>() {
			@Override
			public boolean apply(@Nullable final String resourceId) {
				return StringUtils.isNumeric(resourceId);
			}
		}));
		// Text resource IDs are those that are not the literal value "NULL". This
		// includes the numeric resource IDs.
		final List<String> textIds = Lists.newArrayList(Iterables.filter(resourceIds, new Predicate<String>() {
			@Override
			public boolean apply(@Nullable final String resourceId) {
				return !StringUtils.equalsIgnoreCase("NULL", resourceId);
			}
		}));
		// We can detect the literal value "NULL" implicitly, because it would have been
		// filtered out of the text resource IDs.
		final boolean hasNull = resourceIds.size() > textIds.size();
		final boolean hasNumerics = !numericIds.isEmpty();
		final boolean hasTexts = !textIds.isEmpty();

		final StringBuilder whereClause = new StringBuilder();
		if (hasNumerics) {
			whereClause.append(idKey).append(" IN (").append(StringUtils.join(numericIds, ", ")).append(")");
		}
		if (hasNumerics && hasTexts) {
			whereClause.append(" OR ");
		}
		if (hasTexts) {
			whereClause.append(labelKey).append(" IN ('").append(StringUtils.join(textIds, "', '")).append("')");
		}
		if ((hasNumerics || hasTexts) && hasNull) {
			whereClause.append(" OR ");
		}
		if (hasNull) {
			whereClause.append(labelKey).append(" IS NULL");
		}
		return whereClause.toString();
	}

}
