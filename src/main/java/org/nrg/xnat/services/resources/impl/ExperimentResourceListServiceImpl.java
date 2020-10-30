package org.nrg.xnat.services.resources.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Function;
import org.apache.commons.lang3.StringUtils;
import org.nrg.xapi.model.subjects.XnatExperimentResource;
import org.nrg.xapi.model.util.XnatTemplateUtil;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xft.ItemI;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.resources.ExperimentResourceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class ExperimentResourceListServiceImpl implements ExperimentResourceListService {

	@Autowired
	public ExperimentResourceListServiceImpl(final NamedParameterJdbcTemplate template) {
		_template = template;
	}

	@Override
	public XnatExperimentResource create(UserI user, XnatExperimentResource item) {
		return null;
	}

	@Override
	public List<XnatExperimentResource> getAll(UserI user) {
		return null;
	}

	@Override
	public XnatExperimentResource get(UserI user, int itemId) {
		return null;
	}

	@Override
	public List<XnatExperimentResource> findResourceByExperimentId(UserI user, String experimentId) {
		List<XnatExperimentResource> xnatExperResources = new ArrayList<>();
		xnatExperResources = _template.query(EXPERIMENT_QUERY, new MapSqlParameterSource("experimentId", experimentId), ROW_MAPPER);
		return getXnatExperimentResourceData(xnatExperResources, user);
	}
	
	@Override
	public List<XnatExperimentResource> findExperimentScanResourcesByAssessedIdAndScanId(UserI user, String assessedId, String scanId) {
		List<XnatExperimentResource> xnatExperResources = new ArrayList<>();
		
		ArrayList<XnatExperimentdata> assesseds = XnatTemplateUtil.getXnatExperimentdata(assessedId, user,null);
		ArrayList<XnatImagescandata> scans = XnatTemplateUtil.getXnatImageScanData(scanId, user, assesseds);

		String query = getQuery(scans,assesseds, null);
		
		xnatExperResources = _template.query(query, ROW_MAPPER);
		
		return getXnatExperimentResourceData(xnatExperResources, user);
	}
	
	
	private String getQuery(ArrayList<XnatImagescandata> scans, ArrayList<XnatExperimentdata> assesseds, final List<String> resourceIds) {
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
	
	 

	private List<XnatExperimentResource> getXnatExperimentResourceData(List<XnatExperimentResource> xnatExperResources, UserI user) {
		List<XnatExperimentResource> xnatExperimentResources = new ArrayList<>();
		if(xnatExperResources.size()>0) {
			for (XnatExperimentResource temp : xnatExperResources) {
				XnatExperimentResource xnatExperimentResource = new XnatExperimentResource();
				XnatAbstractresource res = XnatAbstractresource.getXnatAbstractresourcesByXnatAbstractresourceId(temp.getXnatAbstractResourceId(), user, false);
				xnatExperimentResource.setFileCount(Objects.nonNull(res.getFileCount())?res.getFileCount():0);
				xnatExperimentResource.setFileSize(Objects.nonNull(res.getFileSize())?res.getFileSize():new Object());
				xnatExperimentResource.setTags(Objects.isNull(res.getTagString()) || res.getTagString().isEmpty()?XnatExperimentResource.EMPTY_STRING:res.getTagString());
				xnatExperimentResource.setFormat(Objects.isNull(res.getFormat()) || res.getFormat().isEmpty()?XnatExperimentResource.EMPTY_STRING:res.getFormat());
				xnatExperimentResource.setContent(Objects.isNull(res.getContent()) || res.getContent().isEmpty()?XnatExperimentResource.EMPTY_STRING:res.getContent());
				xnatExperimentResource.setCategory(temp.getCategory());
				xnatExperimentResource.setCategoryDescription(temp.getCategoryDescription());
				xnatExperimentResource.setElementName(temp.getElementName());
				xnatExperimentResource.setLabel(temp.getLabel());
				xnatExperimentResource.setXnatAbstractResourceId(temp.getXnatAbstractResourceId());
				xnatExperimentResource.setCategoryId(temp.getCategoryId());
				xnatExperimentResources.add(xnatExperimentResource);
			}	
		}
		return xnatExperimentResources;
	}


	
	private static final String STARTER_FIELDS = "SELECT xnat_abstractresource_id, abst.label, xme.element_name ";
	
	private static final String BY_ID_WHERE_RES_MAP = " WHERE res_map.xnat_experimentdata_id = :experimentId";
	
	private static final String BY_ID_WHERE_ISD_IMAGE = " WHERE isd.image_session_id = :experimentId";
	
	private static final String EXPERIMENT_QUERY = "SELECT * FROM (\n" + 
			"SELECT xnat_abstractresource_id, abst.label, xme.element_name,'resources'::TEXT AS category, NULL::TEXT AS cat_id, ''::TEXT AS cat_desc \n" + 
			"FROM xnat_experimentdata_resource res_map \n" + 
			"JOIN xnat_abstractresource abst ON res_map.xnat_abstractresource_xnat_abstractresource_id = abst.xnat_abstractresource_id \n" + 
			"JOIN xdat_meta_element xme ON abst.extension = xme.xdat_meta_element_id \n" + 
			BY_ID_WHERE_RES_MAP + " \n" + 
			"UNION\n" + 
			"SELECT xnat_abstractresource_id, abst.label, xme.element_name, 'scans'::TEXT, isd.id, isd.type \n" + 
			"FROM xnat_imagescanData isd \n" + 
			"JOIN  xnat_abstractresource abst  ON isd.xnat_imagescandata_id = abst.xnat_imagescandata_xnat_imagescandata_id \n" + 
			"JOIN xdat_meta_element xme ON abst.extension = xme.xdat_meta_element_id\n  "     + 
			BY_ID_WHERE_ISD_IMAGE + " ) all_resources";

	private static final RowMapper<XnatExperimentResource> ROW_MAPPER = new RowMapper<XnatExperimentResource>() {
		@Override
		public XnatExperimentResource mapRow(final ResultSet resultSet, final int i) throws SQLException {
			return new XnatExperimentResource(resultSet);
		}
	};

	private final NamedParameterJdbcTemplate _template;

	

}
