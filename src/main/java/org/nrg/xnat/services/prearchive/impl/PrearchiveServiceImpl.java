package org.nrg.xnat.services.prearchive.impl;

import static org.nrg.xnat.archive.Operation.Delete;
import static org.nrg.xnat.archive.Operation.Move;
import static org.nrg.xnat.archive.Operation.Rebuild;
import static org.restlet.data.Status.CLIENT_ERROR_BAD_REQUEST;
import static org.restlet.data.Status.CLIENT_ERROR_CONFLICT;
import static org.restlet.data.Status.CLIENT_ERROR_FORBIDDEN;
import static org.restlet.data.Status.CLIENT_ERROR_NOT_FOUND;
import static org.restlet.data.Status.SERVER_ERROR_INTERNAL;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.security.PermissionsServiceImpl;
import org.nrg.xdat.security.helpers.AccessLevel;
import org.nrg.xdat.security.helpers.Groups;
import org.nrg.xdat.security.services.PermissionsServiceI;
import org.nrg.xft.exception.InvalidPermissionException;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.predicates.ProjectAccessPredicate;
import org.nrg.xnat.dto.prearchive.PrearchiveDto;
import org.nrg.xnat.helpers.prearchive.DatabaseSession;
import org.nrg.xnat.helpers.prearchive.PrearcDatabase;
import org.nrg.xnat.helpers.prearchive.PrearcUtils;
import org.nrg.xnat.helpers.prearchive.SessionData;
import org.nrg.xnat.helpers.prearchive.SessionDataTriple;
import org.nrg.xnat.helpers.prearchive.SessionException;
import org.nrg.xnat.services.messaging.prearchive.PrearchiveOperationRequest;
import org.nrg.xnat.services.prearchive.PrearchiveService;
import org.nrg.xnat.utils.functions.Functions;
import org.nrg.xnat.utils.functions.UriToSessionDataTriple;
import org.restlet.data.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PrearchiveServiceImpl implements PrearchiveService {
	
	 public static final String PARAM_OVERRIDE_LOCK = "overrideLock";
	
	@Autowired
	public PrearchiveServiceImpl(final NamedParameterJdbcTemplate template) {
		 _template = template;
		 _permissions = XDAT.getContextService().getBean(PermissionsServiceImpl.class);
	}

	@Override
	public List<PrearchiveDto>  findAllPrearchives(UserI user,String projectId, String tag) throws SQLException, SessionException, Exception {
		boolean dataAccess = Groups.hasAllDataAccess(user);
		if (StringUtils.isNotBlank(tag)) {
			List<PrearchiveDto> prearchiveDtos = new ArrayList<>();
			Collection<SessionDataTriple> result = Lists.transform(new ArrayList<>(PrearcDatabase.getSessionByUID(tag)),Functions.SESSION_DATA_TO_SESSION_DATA_TRIPLE);
			if (Objects.isNull(result)) {
				throw new NotFoundException("Session Data Triple result wasn't found");
			} else {
				for (final SessionDataTriple s : result) {
					String query = DatabaseSession.findSessionSql(s.getFolderName(), s.getTimestamp(), s.getProject());
					if (StringUtils.isNotBlank(query)) {
						PrearchiveDto prearchiveDto = _template.queryForObject(query, new MapSqlParameterSource(),new PrearchiveRowMapper());
						prearchiveDtos.add(prearchiveDto);
					}
				}
				return prearchiveDtos;
			}
		} else {
			final List<String> projects = new ArrayList<>(StringUtils.isNotBlank(projectId) ? Arrays.asList(projectId.split("\\s*,\\s*")): _permissions.getUserEditableProjects(user.getUsername()));
			if (Objects.isNull(projects)&& projects.size()<=0) {
				throw new NotFoundException("List of project ID wasn't found");
			}
			if (dataAccess) {
				projects.add(null);
			}
			return _template.query(DatabaseSession.PROJECT.allMatchesSql(projects.toArray(new String[0])),new MapSqlParameterSource(), new PrearchiveRowMapper());
		}
	}
	
	
	
	@Override
	public PrearchiveDto createPrarchiveRebuild(UserI user,  List<String> src, boolean overrideLock) throws InitializationException, InsufficientPrivilegesException, NotFoundException, DataFormatException {
		final List<SessionDataTriple> triples = getSessionDataTriples(user, src);
		if (triples == null) {
			return null;
		}

		for (final SessionDataTriple triple : triples) {
			try {
				if (PrearcDatabase.setStatus(triple.getFolderName(), triple.getTimestamp(), triple.getProject(), PrearcUtils.PrearcStatus.QUEUED_BUILDING, overrideLock)) {
					XDAT.sendJmsRequest(new PrearchiveOperationRequest(user, Rebuild, triple, _additionalValues));
				} else {
					log.warn("Tried to reset the status of the session {} to QUEUED_BUILDING, but failed. This usually means the session is locked and the override lock parameter was false. This might be OK: I checked whether the session was locked before trying to update the status but maybe a new file arrived in the intervening millisecond(s).", triple);
				}
            } catch (IllegalArgumentException e) {
            	throw new DataFormatException(e.getMessage());
            } catch (InvalidPermissionException e) {
            	throw new InsufficientPrivilegesException(e.getMessage());
            } catch (Exception exception) {
                log.error("Error when setting prearchive session {} status to QUEUED for user {}", triple.toString(), user.getUsername(), exception);
                throw new InitializationException(exception);
            }
        }
		
		PrearchiveDto result =  getPrearchiveResult(triples);
		if(Objects.isNull(result)) {
			throw new NotFoundException("Prearchive rebuild wasn't found");
		}
		return result;
	}
	

	@Override
	public PrearchiveDto deletePrarchive(UserI user, List<String> src, boolean overrideLock) throws InitializationException, InsufficientPrivilegesException, NotFoundException {
		final List<SessionDataTriple> triples = getSessionDataTriples(user, src);
        if (triples == null) {
            return null;
        }

        for (final SessionDataTriple triple : triples) {
            try {
                if (PrearcDatabase.setStatus(triple.getFolderName(), triple.getTimestamp(), triple.getProject(), PrearcUtils.PrearcStatus.QUEUED_DELETING)) {
                    final SessionData session    = PrearcDatabase.getSession(triple.getFolderName(), triple.getTimestamp(), triple.getProject());
                    final File        sessionDir = PrearcUtils.getPrearcSessionDir(user, triple.getProject(), triple.getTimestamp(), triple.getFolderName(), false);
                    XDAT.sendJmsRequest(new PrearchiveOperationRequest(user, Delete, session, sessionDir));
                }
            } catch (Exception e) {
            	throw new InitializationException(e.getMessage());
            }
        }
        PrearchiveDto result =  getPrearchiveResult(triples);
		if(Objects.isNull(result)) {
			throw new NotFoundException("Prearchive delete wasn't found");
		}
		return result;
	}
	
	 protected List<SessionDataTriple> getSessionDataTriples(UserI user,  List<String> src) throws InitializationException, InsufficientPrivilegesException, NotFoundException {
	        final UriToSessionDataTriple  transformer = new UriToSessionDataTriple();
	        final List<SessionDataTriple> triples     = Lists.transform(src, transformer);
	        if (transformer.hasMalformedUrls()) {
	        	throw new InitializationException("One or more source values contained a malformed URL: " + StringUtils.join(transformer.getMalformedUrls(), ", "));
	        }

	        final Pair<List<String>, List<String>> deniedAndMissing = getDeniedAndMissingProjectsFromPrearcSources(user, triples);
	        final List<String>                     denied           = deniedAndMissing.getLeft();
	        final List<String>                     missing          = deniedAndMissing.getRight();
	        if (!denied.isEmpty()) {
	            if (!missing.isEmpty()) {
	            	throw new InsufficientPrivilegesException("Invalid permissions for user " + user.getUsername() + " to delete prearchive sessions in one or more projects: " + StringUtils.join(denied, ", ") + ". Also found one or more missing projects: " + StringUtils.join(missing, ", ") + ".");
	            } else {
	            	throw new InsufficientPrivilegesException("Invalid permissions for user " + user.getUsername() + " to delete prearchive sessions in one or more projects: " + StringUtils.join(denied, ", "));
	            }
	        }
	        if (!missing.isEmpty()) {
	        	throw new NotFoundException("One or more specified projects does not exist: " + StringUtils.join(missing, ", ") + ".");
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
	 
	 
	 private PrearchiveDto getPrearchiveResult(List<SessionDataTriple> triples) {
			for (final SessionDataTriple s : triples) {
				String query = DatabaseSession.findSessionSql(s.getFolderName(), s.getTimestamp(), s.getProject());
				if (StringUtils.isNotBlank(query)) {
					return _template.queryForObject(query, new MapSqlParameterSource(), new PrearchiveRowMapper());
				}
			}
			return null;
		}
	 
	 private static final Function<SessionDataTriple, String> FUNCTION_SESSION_DATA_TRIPLE_TO_PROJECT_ID = new Function<SessionDataTriple, String>() {
	        @Nullable
	        @Override
	        public String apply(final SessionDataTriple triple) {
	            return triple.getProject();
	        }
	    };
	    

	   
	private static class PrearchiveRowMapper implements RowMapper<PrearchiveDto>  {
        @Override
        public PrearchiveDto mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
        	ResultSetMetaData rsmd = resultSet.getMetaData();
        	return  PrearchiveDto.getPrearchiveData(resultSet, rsmd);
        }
	}
	
//	private static class PrearchiveTagRowMapper implements RowMapper<PrearchiveDto>  {
//        @Override
//        public PrearchiveDto mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
//        	ResultSetMetaData rsmd = resultSet.getMetaData();
//        	 return  PrearchiveDto.getPrearchiveData(resultSet, rsmd);
//        }
//	}
	
//    private static class PrearchiveRebuildRowMapper implements RowMapper<PrearchiveDto>  {
//    @Override
//    public PrearchiveDto mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
//    	ResultSetMetaData rsmd = resultSet.getMetaData();
//    	 return  PrearchiveDto.getPrearchiveData(resultSet, rsmd);
//    }
//}
	
	

	 private final NamedParameterJdbcTemplate _template;
	 private final PermissionsServiceImpl _permissions;
	 private final Map<String, Object> _additionalValues = new HashMap<>();

}
