package org.nrg.xnat.services.resources.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.nrg.framework.exceptions.NrgServiceError;
import org.nrg.framework.exceptions.NrgServiceRuntimeException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.ItemI;
import org.nrg.xft.XFTTable;
import org.nrg.xft.search.QueryOrganizer;
import org.nrg.xft.security.UserI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ResourceXapiUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(ResourceXapiUtil.class);
	private UserI _user;
	protected XnatExperimentdata expt = null;
	protected ItemI parent = null;
	private String xmlPath = null;
	protected ArrayList<XnatImagescandata> scans = null;
	protected Map<String, String> fieldMapping = new HashMap<>();
	
	public XFTTable loadCatalogs(final List<String> resourceIds, final boolean includeURI, final boolean allowAll)
						throws Exception {
					// checkResourceIDs(resourceIds);
					ArrayList<XnatExperimentdata> expts = new ArrayList<>();
					final StringBuilder query = new StringBuilder();
					final boolean hasResourceIds = resourceIds != null && !resourceIds.isEmpty();
					// final boolean isInResource = StringUtils.equalsIgnoreCase("out", "in"); //
					// type =out
			
					final UserI user = getUser();
			
					if (expt != null && expt.canRead(user) ) {
						expts.add(expt);
					}
			
					if (!expts.isEmpty()) {
						// security = expts.get(0);
						parent = expts.get(0);
						final List<String> experimentIds = Lists.transform(expts, new Function<XnatExperimentdata, String>() {
							@Override
							public String apply(final XnatExperimentdata experiment) {
								return experiment.getId();
							}
						});
						if (allowAll && (true || resourceIds != null)) { // isQueryVariableTrue("all")
							xmlPath = "xnat:experimentData/resources/resource";
							final Map<String, String> variables = new HashMap<>();
							variables.put("username", user.getUsername());
							variables.put("sessionIds", StringUtils.join(experimentIds, "', '"));
							final String userAccessibleAccessorIds = StringSubstitutor.replace(USER_ACCESSIBLE_ASSESSOR_IDS,
									variables);
							// resources
			
							query.append("SELECT * FROM (").append(STARTER_FIELDS)
									.append(", 'resources'::TEXT AS category, NULL::TEXT AS cat_id,''::TEXT AS cat_desc");
							if (includeURI) {
								query.append(
										",'/experiments/' || res_map.xnat_experimentdata_id || '/resources/' || abst.xnat_abstractresource_id AS resource_path");
							}
							query.append(
									" FROM xnat_experimentdata_resource res_map JOIN xnat_abstractresource abst ON res_map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id WHERE res_map.xnat_experimentdata_id IN ('");
							query.append(StringUtils.join(experimentIds, "', '"));
							query.append("') ");
							query.append("  UNION ");
							query.append(STARTER_FIELDS);
							query.append(", 'scans'::TEXT,isd.id,isd.type");
							if (includeURI) {
								query.append(
										",'/experiments/' || isd.image_session_id || '/scans/' || isd.id || '/resources/' || abst.xnat_abstractresource_id AS resource_path");
							}
							query.append(
									" FROM xnat_imagescanData isd JOIN xnat_abstractresource abst ON isd.xnat_imagescandata_id=abst.xnat_imagescandata_xnat_imagescandata_id JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id WHERE isd.image_session_id IN ('");
							query.append(StringUtils.join(experimentIds, "', '"));
							query.append("') UNION ");
							query.append(STARTER_FIELDS);
							query.append(", 'reconstructions'::TEXT,recon.id,recon.type");
							if (includeURI) {
								query.append(
										",'/experiments/' || recon.image_session_id || '/reconstructions/' || recon.id || '/out' || '/resources/' || abst.xnat_abstractresource_id AS resource_path");
							}
							query.append(
									" FROM xnat_reconstructedimagedata recon JOIN recon_out_resource map ON recon.xnat_reconstructedimagedata_id=map.xnat_reconstructedimagedata_xnat_reconstructedimagedata_id JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id WHERE image_session_id IN ('");
							query.append(StringUtils.join(experimentIds, "', '"));
							query.append("') UNION ");
							query.append(STARTER_FIELDS);
							query.append(", 'assessors'::TEXT,iad.id,xes.singular");
							if (includeURI) {
								query.append(
										",'/experiments/' || iad.imagesession_id || '/assessors/' || iad.id || '/out' || '/resources/' || abst.xnat_abstractresource_id AS resource_path");
							}
							query.append(" FROM ").append(userAccessibleAccessorIds).append(
									" iad JOIN img_assessor_out_resource map ON iad.id=map.xnat_imageassessordata_id JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id LEFT JOIN xdat_element_security xes ON xme.element_name=xes.element_name WHERE iad.imagesession_id IN ('");
							query.append(StringUtils.join(experimentIds, "', '"));
							query.append("') UNION ");
							query.append(STARTER_FIELDS);
							query.append(", 'assessors'::TEXT,iad.id,xes.singular");
							if (includeURI) {
								query.append(
										",'/experiments/' || iad.imagesession_id || '/assessors/' || iad.id || '/resources/' || abst.xnat_abstractresource_id AS resource_path");
							}
							query.append(" FROM ").append(userAccessibleAccessorIds).append(
									" iad JOIN xnat_experimentdata_resource map ON iad.id=map.xnat_experimentdata_id JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id LEFT JOIN xdat_element_security xes ON xme.element_name=xes.element_name WHERE iad.imagesession_id IN ('");
							query.append(StringUtils.join(experimentIds, "', '"));
							query.append("')) all_resources");
			
							if (hasResourceIds) {
								query.append(" WHERE (")
										.append(getResourceIdsWhereClause(resourceIds, "xnat_abstractresource_id", "label"))
										.append(")");
							}
						} else {
							xmlPath = "xnat:experimentData/resources/resource";
							// resources
							query.append(STARTER_FIELDS);
							query.append(", 'resources'::TEXT AS category, expt.id::TEXT AS cat_id, ' '::TEXT AS cat_desc");
							if (includeURI) {
								query.append(
										",'/experiments/' || map.xnat_experimentdata_id || '/resources/' || abst.xnat_abstractresource_id AS resource_path");
							}
							query.append(
									" FROM xnat_experimentdata_resource map LEFT JOIN xnat_experimentdata expt ON map.xnat_experimentdata_id=expt.id LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id WHERE xnat_experimentdata_id IN ('");
							query.append(StringUtils.join(experimentIds, "', '"));
							query.append("') ");
							if (hasResourceIds) {
								query.append(" AND (").append(getResourceIdsWhereClause(resourceIds)).append(")");
							}
						}
					} else if (!scans.isEmpty()) {
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
					log.debug("Loading catalog for user '{}' using query: {}", user.getUsername(), completedQuery);
					return XFTTable.Execute(completedQuery, user.getDBName(), user.getUsername());
				}
	
	private String getResourceIdsWhereClause(final List<String> resourceIds) {
		return getResourceIdsWhereClause(resourceIds, "map.xnat_abstractresource_xnat_abstractresource_id",
				"abst.label");
	}
	
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
	
	public XFTTable formatHeaders(XFTTable table, QueryOrganizer qo, String idpath, String URIpath) {
		final ArrayList<String> newColumns = new ArrayList<>();
		for (String column : table.getColumns()) {
			String xPath = qo.getXPATHforAlias(column.toLowerCase());
			if (xPath == null) {
				newColumns.add(column);
			} else {
				String key = this.getLabelForFieldMapping(xPath);
				if (key == null) {
					newColumns.add(xPath);
				} else {
					newColumns.add(key);
				}
			}
		}

		int idIndex = table.getColumnIndex(qo.getFieldAlias(idpath));
		if (URIpath != null)
			newColumns.add("URI");
		XFTTable clone = new XFTTable();
		clone.initTable(newColumns);
		for (Object[] row : table.rows()) {
			Object[] newRow;
			if (URIpath != null)
				newRow = new Object[row.length + 1];
			else
				newRow = new Object[row.length];
			System.arraycopy(row, 0, newRow, 0, row.length);
			String id = (String) row[idIndex];
			if (URIpath != null)
				newRow[row.length] = URIpath + id;
			clone.insertRow(newRow);
		}
		return clone;
	}
	
	public String getLabelForFieldMapping(String xPath) {
		for (Map.Entry<String, String> entry : fieldMapping.entrySet()) {
			if (entry.getValue().equalsIgnoreCase(xPath)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	private static final String STARTER_FIELDS = "SELECT xnat_abstractresource_id, abst.label, xme.element_name ";
	private static final String USER_ACCESSIBLE_ASSESSOR_IDS = "( SELECT * FROM xnat_imageassessordata WHERE id IN (SELECT id "
			+ " FROM   (SELECT xea.element_name, " + "                xfm.field, " + "                xfm.field_value "
			+ "         FROM   xdat_user u " + "                JOIN xdat_user_groupid map "
			+ "                  ON u.xdat_user_id = map.groups_groupid_xdat_user_xdat_user_id "
			+ "                JOIN xdat_usergroup gp " + "                  ON map.groupid = gp.id "
			+ "                JOIN xdat_element_access xea "
			+ "                  ON gp.xdat_usergroup_id = xea.xdat_usergroup_xdat_usergroup_id "
			+ "                JOIN xdat_field_mapping_set xfms " + "                  ON "
			+ " xea.xdat_element_access_id = xfms.permissions_allow_set_xdat_elem_xdat_element_access_id "
			+ " JOIN xdat_field_mapping xfm " + "   ON "
			+ " xfms.xdat_field_mapping_set_id = xfm.xdat_field_mapping_set_xdat_field_mapping_set_id "
			+ " AND read_element = 1 " + " AND field_value != '' " + " AND field != '' " + " WHERE  u.login = 'guest' "
			+ "  UNION " + "  SELECT xea.element_name, " + "         xfm.field, " + "         xfm.field_value "
			+ "  FROM   xdat_user_groupid map "
			+ "         JOIN xdat_user u ON map.groups_groupid_xdat_user_xdat_user_id = u.xdat_user_id "
			+ "         JOIN xdat_usergroup gp " + "           ON map.groupid = gp.id "
			+ "         JOIN xdat_element_access xea "
			+ "           ON gp.xdat_usergroup_id = xea.xdat_usergroup_xdat_usergroup_id "
			+ "         JOIN xdat_field_mapping_set xfms " + "           ON "
			+ " xea.xdat_element_access_id = xfms.permissions_allow_set_xdat_elem_xdat_element_access_id "
			+ " JOIN xdat_field_mapping xfm " + "   ON "
			+ " xfms.xdat_field_mapping_set_id = xfm.xdat_field_mapping_set_xdat_field_mapping_set_id "
			+ " AND read_element = 1 " + " AND field_value != '' " + " AND field != '' "
			+ " WHERE u.login = '${username}' " + " OR xfm.field_value IN (SELECT proj.id "
			+ "         FROM   xnat_projectdata proj " + "         JOIN (SELECT field_value, "
			+ "                        read_element AS project_read "
			+ "                                FROM   xdat_element_access " + "                                ea "
			+ "                                LEFT JOIN xdat_field_mapping_set fms "
			+ "                                ON ea.xdat_element_access_id = "
			+ "                                fms.permissions_allow_set_xdat_elem_xdat_element_access_id "
			+ "                                LEFT JOIN xdat_user u "
			+ "                                ON ea.xdat_user_xdat_user_id = u.xdat_user_id "
			+ "                                LEFT JOIN xdat_field_mapping fm "
			+ "                                ON fms.xdat_field_mapping_set_id = "
			+ "                                fm.xdat_field_mapping_set_xdat_field_mapping_set_id "
			+ "                                WHERE  login = 'guest' "
			+ "                                AND read_element = 1 "
			+ "                                AND element_name = 'xnat:projectData')project_read "
			+ " ON proj.id = project_read.field_value " + " JOIN (SELECT field_value, "
			+ "       read_element AS subject_read " + "               FROM   xdat_element_access ea "
			+ "               LEFT JOIN xdat_field_mapping_set fms " + "               ON ea.xdat_element_access_id = "
			+ "               fms.permissions_allow_set_xdat_elem_xdat_element_access_id "
			+ "               LEFT JOIN xdat_user u " + "               ON ea.xdat_user_xdat_user_id = u.xdat_user_id "
			+ "               LEFT JOIN xdat_field_mapping fm " + "               ON fms.xdat_field_mapping_set_id = "
			+ "               fm.xdat_field_mapping_set_xdat_field_mapping_set_id "
			+ "               WHERE  login = 'guest' " + "               AND read_element = 1 "
			+ "               AND field = 'xnat:subjectData/project')subject_read "
			+ " ON proj.id = subject_read.field_value)) perms " + " INNER JOIN (SELECT iad.id, "
			+ "                    element_name " + "                    || '/project' AS field, "
			+ "                    expt.project, " + "                    expt.label "
			+ "             FROM   xnat_imageassessordata iad "
			+ "                    LEFT JOIN xnat_experimentdata expt "
			+ "                           ON iad.id = expt.id " + "                    LEFT JOIN xdat_meta_element xme "
			+ "                           ON expt.extension = xme.xdat_meta_element_id "
			+ "             WHERE  iad.imagesession_id IN ('${sessionIds}') " + "             UNION "
			+ "             SELECT expt.id, " + "                    xme.element_name "
			+ "                    || '/sharing/share/project', " + "                    shr.project, "
			+ "                    shr.label " + "             FROM   xnat_experimentdata_share shr "
			+ "                    LEFT JOIN xnat_experimentdata expt "
			+ "                           ON expt.id = shr.sharing_share_xnat_experimentda_id "
			+ "                    LEFT JOIN xdat_meta_element xme "
			+ "                           ON expt.extension = xme.xdat_meta_element_id) expts "
			+ "         ON perms.field = expts.field " + "            AND perms.field_value IN (expts.project, '*') "
			+ " ORDER  BY element_name) )";


}
