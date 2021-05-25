package org.nrg.xnat.services.prearchive.impl;

import static org.nrg.xnat.archive.Operation.Rebuild;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.security.helpers.AccessLevel;
import org.nrg.xdat.security.services.PermissionsServiceI;
import org.nrg.xft.exception.InvalidPermissionException;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.predicates.ProjectAccessPredicate;
import org.nrg.xnat.dto.prearchive.PrearchiveDto;
import org.nrg.xnat.helpers.prearchive.DatabaseSession;
import org.nrg.xnat.helpers.prearchive.PrearcDatabase;
import org.nrg.xnat.helpers.prearchive.PrearcUtils;
import org.nrg.xnat.helpers.prearchive.SessionDataTriple;
import org.nrg.xnat.services.messaging.prearchive.PrearchiveOperationRequest;
import org.nrg.xnat.services.prearchive.PrearchiveRebuildService;
import org.nrg.xnat.utils.functions.UriToSessionDataTriple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PrearchiveRebuildServiceImpl implements PrearchiveRebuildService {

	@Autowired
	public PrearchiveRebuildServiceImpl(final NamedParameterJdbcTemplate template) {
		 _template = template;
	}
	@Override
	public PrearchiveDto createPrarchiveRebuild(UserI user, String src, MultipartFile file) {
		final List<SessionDataTriple> triples = getSessionDataTriples(user, src);
		if (triples == null) {
			return null;
		}

		for (final SessionDataTriple triple : triples) {
			try {
				if (PrearcDatabase.setStatus(triple.getFolderName(), triple.getTimestamp(), triple.getProject(), PrearcUtils.PrearcStatus.QUEUED_BUILDING, _overrideLock)) {
					XDAT.sendJmsRequest(new PrearchiveOperationRequest(user, Rebuild, triple, _additionalValues));
				} else {
					log.warn("Tried to reset the status of the session {} to QUEUED_BUILDING, but failed. This usually means the session is locked and the override lock parameter was false. This might be OK: I checked whether the session was locked before trying to update the status but maybe a new file arrived in the intervening millisecond(s).", triple);
				}
            } catch (IllegalArgumentException e) {
				//getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e);
            } catch (InvalidPermissionException e) {
				//getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, e);
            } catch (Exception exception) {
                log.error("Error when setting prearchive session {} status to QUEUED for user {}", triple.toString(), user.getUsername(), exception);
				//getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,exception);
            }
        }
		for (final SessionDataTriple s : triples) {
			String query = DatabaseSession.findSessionSql(s.getFolderName(), s.getTimestamp(), s.getProject());
			if (StringUtils.isNotBlank(query)) {
				PrearchiveDto prearchiveDto = _template.queryForObject(query, new MapSqlParameterSource(), new PrearchiveRebuildRowMapper());
				return prearchiveDto;
			}
		}
		return null;
	}
	
	 protected List<SessionDataTriple> getSessionDataTriples(UserI user, String src) {
	        final UriToSessionDataTriple  transformer = new UriToSessionDataTriple();
	        final List<SessionDataTriple> triples     = Lists.transform(Arrays.asList(src), transformer);
	        if (transformer.hasMalformedUrls()) {
	           // getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, "One or more source values contained a malformed URL: " + StringUtils.join(transformer.getMalformedUrls(), ", "));
	            return null;
	        }

	        //final UserI                            user             = getUser();
	        final Pair<List<String>, List<String>> deniedAndMissing = getDeniedAndMissingProjectsFromPrearcSources(user, triples);
	        final List<String>                     denied           = deniedAndMissing.getLeft();
	        final List<String>                     missing          = deniedAndMissing.getRight();
	        if (!denied.isEmpty()) {
	            if (!missing.isEmpty()) {
	                //getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Invalid permissions for user " + user.getUsername() + " to delete prearchive sessions in one or more projects: " + StringUtils.join(denied, ", ") + ". Also found one or more missing projects: " + StringUtils.join(missing, ", ") + ".");
	            } else {
	                ///getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Invalid permissions for user " + user.getUsername() + " to delete prearchive sessions in one or more projects: " + StringUtils.join(denied, ", "));
	            }
	            return null;
	        }
	        if (!missing.isEmpty()) {
	           // getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND, "One or more specified projects does not exist: " + StringUtils.join(missing, ", ") + ".");
	        }
	        return triples;
	    }
	 
	 protected static Pair<List<String>, List<String>> getDeniedAndMissingProjectsFromPrearcSources(final UserI user, final Collection<SessionDataTriple> triples) {
	        final ProjectAccessPredicate predicate = new ProjectAccessPredicate(XDAT.getContextService().getBean(PermissionsServiceI.class), XDAT.getNamedParameterJdbcTemplate(), user, AccessLevel.Edit);
	        final List<String>           missing   = predicate.getMissing();
	        final List<String> denied = Lists.newArrayList(Iterables.filter(Iterables.filter(Iterables.transform(triples, FUNCTION_SESSION_DATA_TRIPLE_TO_PROJECT_ID), Predicates.not(predicate)), new Predicate<String>() {
	            @Override
	            public boolean apply(final String projectId) {
	                return !missing.contains(projectId);
	            }
	        }));
	        return ImmutablePair.of(denied, missing);
	    }
	 
	 private static final Function<SessionDataTriple, String> FUNCTION_SESSION_DATA_TRIPLE_TO_PROJECT_ID = new Function<SessionDataTriple, String>() {
	        @Nullable
	        @Override
	        public String apply(final SessionDataTriple triple) {
	            return triple.getProject();
	        }
	    };
	    
	    private static class PrearchiveRebuildRowMapper implements RowMapper<PrearchiveDto>  {
	        @Override
	        public PrearchiveDto mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
	        	ResultSetMetaData rsmd = resultSet.getMetaData();
	        	 return  PrearchiveDto.getPrearchiveData(resultSet, rsmd);
	        }
		}
	    private final Map<String, Object> _additionalValues = new HashMap<>();
	    private boolean _overrideLock = false;
	    private final NamedParameterJdbcTemplate _template;

}
