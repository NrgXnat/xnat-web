package org.nrg.xnat.model.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ClientException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xft.XFTTable;
import org.nrg.xft.security.UserI;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Accessors(prefix = "_")
public class XNATCatalogTemplateUtil extends XnatTemplateUtil {
	public static final String RESOURCE_ID = "RESOURCE_ID";

	public XNATCatalogTemplateUtil() {
		_template = XDAT.getNamedParameterJdbcTemplate();
	}
	public List<String> setResourcesIds(String requestedResourceId, UserI user,  final boolean allowAll) {
		_resourceIds = new ArrayList<String>();
	        final List<String> requestedResources  = StringUtils.isNotBlank(requestedResourceId) ? Arrays.asList(requestedResourceId.split("\\s*,\\s*")) : Collections.<String>emptyList();
	        if (!requestedResources.isEmpty()) {
	            // Separate numeric and non-numeric IDs to start. Non-numeric IDs get qualified by project/experiment/etc later.
	            _resourceIds.addAll(Lists.newArrayList(Iterables.filter(requestedResources, Predicates.not(Predicates.containsPattern("^\\d+$")))));
	            final List<String> requestedResourceIds = Lists.newArrayList(Iterables.filter(requestedResources, Predicates.containsPattern("^\\d+$")));

	            // Add all numeric IDs that are either permitted based on associated security ID or that have no security ID (in construction)
	            if (!requestedResourceIds.isEmpty()) {
	                final List<String> resourceIds = Lists.transform(Lists.newArrayList(Iterables.filter(getTemplate().query(QUERY_FIND_RESOURCE_SECURE_OBJECTS, new MapSqlParameterSource("resourceIds", requestedResourceIds), RESOURCE_MAP_ROW_MAPPER), new PermittedResourcePredicate(user))), ResourceMap.FUNCTION_RESOURCE_MAP);
	                if (resourceIds.size() < requestedResourceIds.size()) {
	                    final int requested = requestedResourceIds.size();
	                    requestedResourceIds.removeAll(resourceIds);
	                   // throw new ClientException(Status.CLIENT_ERROR_FORBIDDEN, "The user " + getUser().getUsername() + " requested " + requested + " resources by ID (i.e. not by qualified resource label) but was denied access to " + (resourceIds.isEmpty() ? "all" : requestedResourceIds.size()) + " of them: " + StringUtils.join(requestedResourceIds, ", "));
	                }
	                _resourceIds.addAll(resourceIds);
	            }
		}
	        try {
	        	XnatTemplateUtil xnatTemplateUtil = new XnatTemplateUtil();
	            setCatalogs(xnatTemplateUtil.loadCatalogs(_resourceIds, true, allowAll, user));
	        } catch (Exception e) {
	            log.error("An error occurred trying to load catalogs for user {} from the resource IDs: {}",user.getUsername(), _resourceIds, e);
	        }
			return _resourceIds;
		}
	protected NamedParameterJdbcTemplate getTemplate() {
        return _template;
    }
	
	
	 @Data
	    @Accessors(prefix = "_")
	    private static class ResourceMap {
	        private static final Function<ResourceMap, String> FUNCTION_RESOURCE_MAP = new Function<ResourceMap, String>() {
	            @Override
	            public String apply(final ResourceMap resourceMap) {
	                return Long.toString(resourceMap.getResourceId());
	            }
	        };
	        private final        long                          _resourceId;
	        private final        String                        _xsiType;
	        private final        String                        _securityId;
	        private final        String                        _projectId;
	    }
	 
	 @Data
	    @Accessors(prefix = "_")
	    private static class PermittedResourcePredicate implements Predicate<ResourceMap> {
	        public PermittedResourcePredicate(final UserI user) {
	            _user = user;
	        }

	        @Override
	        public boolean apply(final ResourceMap resourceMap) {
	            final String xsiType   = resourceMap.getXsiType();
	            final String projectId = resourceMap.getProjectId();

	            final List<String> xmlPaths = StringUtils.equalsIgnoreCase(xsiType, XnatProjectdata.SCHEMA_ELEMENT_NAME)
	                                          ? Collections.singletonList(XnatProjectdata.SCHEMA_ELEMENT_NAME + "/ID")
	                                          : Arrays.asList(xsiType + "/project", xsiType + "/sharing/share/project");
	            try {
	                if (Iterables.any(xmlPaths, new Predicate<String>() {
	                    @Override
	                    public boolean apply(final String xmlPath) {
	                        try {
	                            return Permissions.canRead(getUser(), xmlPath, projectId);
	                        } catch (Exception e) {
	                            return false;
	                        }
	                    }
	                })) {
	                    return true;
	                }
	                log.error("The user {} requested the resource with ID {} on the object {}/ID[{}] but was denied access", getUser().getUsername(), resourceMap.getResourceId(), xsiType, resourceMap.getSecurityId());
	                return false;
	            } catch (Exception e) {
	                log.error("An error occurred trying to check permissions for user {} on the object {}/ID[{}], denying by default", getUser().getUsername(), xsiType, resourceMap.getSecurityId(), e);
	                return false;
	            }
	        }

	        private final UserI _user;
	    }
	
	private static final RowMapper<ResourceMap> RESOURCE_MAP_ROW_MAPPER = new RowMapper<ResourceMap>() {
		@Override
		public ResourceMap mapRow(final ResultSet resultSet, final int index) throws SQLException {
			return new ResourceMap(resultSet.getLong("resourceId"), resultSet.getString("xsiType"),
					resultSet.getString("securityId"), resultSet.getString("projectId"));
		}
	};
	private static final Function<XnatExperimentdata, String> FUNCTION_EXPERIMENT_IDS = new Function<XnatExperimentdata, String>() {
		@Override
		public String apply(final XnatExperimentdata experiment) {
			return experiment.getId();
		}
	};

			@Nonnull
		    private List<String> _resourceIds = new ArrayList<>();
		    @Nonnull
		    private final List<XnatAbstractresource> _resources   = new ArrayList<>();

		    private XFTTable _catalogs = null;
		    private final NamedParameterJdbcTemplate _template;
		    
		    private static final String QUERY_FIND_RESOURCE_SECURE_OBJECTS = "SELECT " +
		            "  a.xnat_abstractresource_id AS resourceId, " +
		            "  r.uri AS uri, " +
		            "  xme.element_name AS xsiType, " +
		            "  e.id AS securityId, " +
		            "  e.project AS projectId " +
		            "FROM " +
		            "  xnat_abstractresource a " +
		            "  LEFT JOIN xnat_resource r ON a.xnat_abstractresource_id = r.xnat_abstractresource_id " +
		            "  LEFT JOIN xnat_imagescandata s ON a.xnat_imagescandata_xnat_imagescandata_id = s.xnat_imagescandata_id " +
		            "  LEFT JOIN img_assessor_in_resource iain ON a.xnat_abstractresource_id = iain.xnat_abstractresource_xnat_abstractresource_id " +
		            "  LEFT JOIN img_assessor_out_resource iaout ON a.xnat_abstractresource_id = iaout.xnat_abstractresource_xnat_abstractresource_id " +
		            "  LEFT JOIN recon_in_resource rin ON a.xnat_abstractresource_id = rin.xnat_abstractresource_xnat_abstractresource_id " +
		            "  LEFT JOIN recon_out_resource rout ON a.xnat_abstractresource_id = rout.xnat_abstractresource_xnat_abstractresource_id " +
		            "  LEFT JOIN xnat_reconstructedimagedata recon ON COALESCE(rin.xnat_reconstructedimagedata_xnat_reconstructedimagedata_id, rout.xnat_reconstructedimagedata_xnat_reconstructedimagedata_id) = recon.xnat_reconstructedimagedata_id " +
		            "  LEFT JOIN xnat_experimentdata_resource eres ON a.xnat_abstractresource_id = eres.xnat_abstractresource_xnat_abstractresource_id " +
		            "  LEFT JOIN xnat_experimentdata e ON COALESCE(s.image_session_id, eres.xnat_experimentdata_id, iaout.xnat_imageassessordata_id, iain.xnat_imageassessordata_id, recon.image_session_id) = e.id " +
		            "  LEFT JOIN xdat_meta_element xme ON e.extension = xme.xdat_meta_element_id " +
		            "WHERE " +
		            "  a.xnat_abstractresource_id::VARCHAR(64) IN (:resourceIds) AND " +
		            "  e.id IS NOT NULL " +
		            "UNION " +
		            "SELECT " +
		            "  a.xnat_abstractresource_id AS resourceId, " +
		            "  r.uri AS uri, " +
		            "  'xnat:subjectData' AS xsiType, " +
		            "  s.id AS securityId, " +
		            "  s.project AS projectId " +
		            "FROM " +
		            "  xnat_subjectdata_resource res " +
		            "  LEFT JOIN xnat_abstractresource a ON res.xnat_abstractresource_xnat_abstractresource_id = a.xnat_abstractresource_id " +
		            "  LEFT JOIN xnat_resource r ON a.xnat_abstractresource_id = r.xnat_abstractresource_id " +
		            "  LEFT JOIN xnat_subjectdata S ON res.xnat_subjectdata_id = S.id " +
		            "WHERE " +
		            "  a.xnat_abstractresource_id::VARCHAR(64) IN (:resourceIds) " +
		            "UNION " +
		            "SELECT " +
		            "  a.xnat_abstractresource_id AS resourceId, " +
		            "  r.uri AS uri, " +
		            "  'xnat:projectData' AS xsiType, " +
		            "  p.id AS securityId, " +
		            "  p.id AS projectId " +
		            "FROM " +
		            "  xnat_projectdata_resource res " +
		            "  LEFT JOIN xnat_abstractresource a ON res.xnat_abstractresource_xnat_abstractresource_id = a.xnat_abstractresource_id " +
		            "  LEFT JOIN xnat_resource r ON a.xnat_abstractresource_id = r.xnat_abstractresource_id " +
		            "  LEFT JOIN xnat_projectdata p ON res.xnat_projectdata_id = p.id " +
		            "WHERE " +
		            "  a.xnat_abstractresource_id::VARCHAR(64) IN (:resourceIds)";

		
}
