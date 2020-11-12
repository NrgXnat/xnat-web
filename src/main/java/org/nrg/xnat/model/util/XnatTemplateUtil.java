package org.nrg.xnat.model.util;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import com.google.common.base.Function;

import com.google.common.base.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatReconstructedimagedata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.search.CriteriaCollection;
import org.nrg.xft.ItemI;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.XftStringUtils;


import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class XnatTemplateUtil {
	XnatSubjectdata sub = null;

	ArrayList<XnatExperimentdata> expts = new ArrayList<>();

	

	ArrayList<XnatReconstructedimagedata> recons = new ArrayList<>();

	String type = null;

	ItemI parent = null;

	ItemI security = null;

	String xmlPath = null;
	
	private final static boolean completeDocument = false;

	public static ArrayList<XnatExperimentdata> getXnatExperimentdata(String assessid , UserI user, XnatProjectdata proj){
		ArrayList<XnatExperimentdata> assesseds = new ArrayList<>();
		if (assessid != null) {
			for (String s : XftStringUtils.CommaDelimitedStringToArrayList(assessid)) {
				XnatExperimentdata assessed = XnatImagesessiondata.getXnatImagesessiondatasById(s, user, false);
				if (assessed != null && (proj != null && !assessed.hasProject(proj.getId())))
					assessed = null;
				if (assessed == null && proj != null)
					assessed = XnatImagesessiondata.GetExptByProjectIdentifier(proj.getId(), s, user, false);
				if (assessed != null) {
					try {
						if (assessed.canRead(user))
							assesseds.add(assessed);

					} catch (Exception ignored) {
					}
				}
			}
		}
		return assesseds;
	}
	
	public static ArrayList<XnatImagescandata> getXnatImageScanData(String scanID, UserI user, ArrayList<XnatExperimentdata> assesseds) {
		ArrayList<XnatImagescandata> scans = new ArrayList<>();
	        if (scanID != null && assesseds.size() > 0) {

	            scanID = scanID.replace("[SLASH]", "/");//this is such an ugly hack.  If a slash is included in the scan type and thus in the URL, it breaks the GET command.  Even if it is properly escaped.  So, I'm adding this alternative encoding of slash to allow us to work around the issue.  Hopefully Spring MVC will eliminate it.

	            CriteriaCollection cc = new CriteriaCollection("OR");
	            for (XnatExperimentdata assessed : assesseds) {
	                CriteriaCollection subcc = new CriteriaCollection("AND");
	                subcc.addClause("xnat:imageScanData/image_session_ID", assessed .getId());
	                subcc = getSubccForAll(scanID, subcc);
	                cc.add(subcc);

	                subcc = new CriteriaCollection("AND");
	                subcc.addClause("xnat:imageScanData/image_session_ID", assessed
	                        .getId());
	                if (!(scanID.equals("*") || scanID.equals("ALL"))) {
	                    if (!scanID.contains(","))
	                    	subcc = getSubccForScanIdNull(scanID, subcc);
	                    else 
	                    	subcc = getSubccForScanIdNotNull(scanID, subcc);
	                }
	                cc.add(subcc);
	            }
				scans = XnatImagescandata.getXnatImagescandatasByField(cc, user,completeDocument);
	        }
			return scans;
	}

	private static CriteriaCollection getSubccForScanIdNotNull(String scanID, CriteriaCollection subcc) {
		CriteriaCollection subsubcc = new CriteriaCollection("OR");
        for (String s : XftStringUtils.CommaDelimitedStringToArrayList(scanID, true)) {
            if (s.equals("NULL")) {
                subsubcc.addClause("xnat:imageScanData/type", "", " IS NULL ", true);
                subsubcc.addClause("xnat:imageScanData/type", "");
            } else {
                subsubcc.addClause("xnat:imageScanData/type", s.replace("[COMMA]", ","));
            }
        }
        subcc.add(subsubcc);
		return subsubcc;
	}

	private static CriteriaCollection getSubccForScanIdNull(String scanID, CriteriaCollection subcc) {
		if (scanID.equals("NULL")) {
            CriteriaCollection subsubcc = new CriteriaCollection("OR");
            subsubcc.addClause("xnat:imageScanData/type", "", " IS NULL ", true);
            subsubcc.addClause("xnat:imageScanData/type", "");
            subcc.add(subsubcc);
        } else {
            subcc.addClause("xnat:imageScanData/type", scanID.replace("[COMMA]", ","));
        }
		return subcc;
	}

	private static CriteriaCollection getSubccForAll(String scanID, CriteriaCollection subcc) {
		if (!(scanID.equals("*") || scanID.equals("ALL"))) {
            if (!scanID.contains(",")) {
                subcc.addClause("xnat:imageScanData/ID", scanID);
            } else {
                CriteriaCollection subsubcc = new CriteriaCollection("OR");
                for (String s : XftStringUtils.CommaDelimitedStringToArrayList(scanID, true)) {
                    subsubcc.addClause("xnat:imageScanData/ID", s);
                }
                subcc.add(subsubcc);
            }
        }
		return subcc;
	}
	
	public String getResourceIdsWhereClause(final List<String> resourceIds, final String idKey) {
        return getResourceIdsWhereClause(resourceIds, idKey, "abst.label");
    }
 
	private String getResourceIdsWhereClause(final List<String> resourceIds, final String idKey, final String labelKey) {
        // Numeric resource IDs are those that contain only digits.
        final List<String> numericIds = Lists.newArrayList(Iterables.filter(resourceIds, new Predicate<String>() {
            public boolean apply(@Nullable final String resourceId) {
                return StringUtils.isNumeric(resourceId);
            }
        }));
        // Text resource IDs are those that are not the literal value "NULL". This includes the numeric resource IDs.
        final List<String> textIds = Lists.newArrayList(Iterables.filter(resourceIds, new Predicate<String>() {
            public boolean apply(@Nullable final String resourceId) {
                return !StringUtils.equalsIgnoreCase("NULL", resourceId);
            }
        }));
        // We can detect the literal value "NULL" implicitly, because it would have been filtered out of the text resource IDs.
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
	
	
	
	public static String getQuery(ArrayList<XnatImagescandata> scans, ArrayList<XnatExperimentdata> assesseds, final List<String> resourceIds) {
		final StringBuilder query = new StringBuilder();
		 boolean includeURI= false;
		 final boolean hasResourceIds = resourceIds != null && !resourceIds.isEmpty();
		if (!scans.isEmpty()) {
            final List<Integer> scanIds = Lists.transform(scans, new Function<XnatImagescandata, Integer>() {
                @Override
                public Integer apply(final XnatImagescandata scan) {
                    return scan.getXnatImagescandataId();
                }
            });
            query.append(STARTER_FIELDS);
            query.append(", 'scans'::TEXT AS category, scan.id::TEXT AS cat_id, scan.type::TEXT AS cat_desc");
          
			if (includeURI) {
                query.append(",'/experiments/' || scan.image_session_id || '/scans/' || scan.id || '/resources/' || abst.xnat_abstractresource_id AS resource_path");
            }
            query.append(" FROM xnat_abstractresource abst LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id LEFT JOIN xnat_imagescandata scan ON abst.xnat_imagescandata_xnat_imagescandata_id=scan.xnat_imagescandata_id WHERE xnat_imagescandata_xnat_imagescandata_id IN ('");
            query.append(StringUtils.join(scanIds, "', '"));
            query.append("') ");
            if (hasResourceIds) {
            	XnatTemplateUtil xnatTemplateUtil = new XnatTemplateUtil();
                query.append(" AND (").append(xnatTemplateUtil.getResourceIdsWhereClause(resourceIds, "abst.xnat_abstractresource_id")).append(")");
            }
		}
		return query.toString();
	}
	
	private static final String STARTER_FIELDS = "SELECT xnat_abstractresource_id, abst.label, xme.element_name ";
	
}
