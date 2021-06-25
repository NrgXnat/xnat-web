package org.nrg.xnat.services.extensions.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.security.SecurityManager;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.helpers.UserHelper;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.extensions.util.StudyRoutingUtil;
import org.nrg.xnat.services.extensions.StudyRoutingService;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StudyRoutingServiceImpl implements StudyRoutingService {

	@Override
	public List<StudyRoutingUtil> findAll(UserI user) throws InitializationException {
			return getAllStudyRouting(user, getRoutingService());
	}
	
	@Override
	public StudyRoutingUtil findByStudyInstanceUid(UserI user, String studyInstanceUid) throws NotFoundException, InitializationException {
		StudyRoutingUtil responce = getAllStudyRoutingByInstanceUid(user, studyInstanceUid, getRoutingService());
		if(Objects.isNull(responce)) {
			throw new NotFoundException("Study Routing wasn't found");
		}
		return responce;
	}
	
	@Override
	public void updateStudyRouting(UserI user, String studyInstanceUid, String projectId) throws InitializationException, DataFormatException {
		 org.nrg.xdat.services.StudyRoutingService routingService = getRoutingService();
		if (StringUtils.isBlank(studyInstanceUid) || StringUtils.isBlank(projectId)) {
             throw new DataFormatException("You must specify both a study instance UID and the project ID that you wish to assign.");
         }
		try {
			if (!Permissions.can(user, "xnat:mrSessionData/project", projectId, SecurityManager.EDIT)) {
				throw new InsufficientPrivilegesException( "You do not have sufficient privileges to modify study routings for this project.");
			}
			final Map<String, String> routing = routingService.findStudyRouting(studyInstanceUid);
			if (routing == null) {
				if (log.isDebugEnabled()) {
					log.debug("Creating new study routing assignment for study instance UID {} to project {}, created by user {}", studyInstanceUid, projectId, user.getLogin());
				}
				routingService.assign(studyInstanceUid, projectId, user.getLogin());
			} else if (!StringUtils.equals(routing.get(org.nrg.xdat.services.StudyRoutingService.PROJECT), projectId)) {
				final String existing = routing.get(org.nrg.xdat.services.StudyRoutingService.PROJECT);
				if (!Permissions.can(user, "xnat:mrSessionData/project", existing, SecurityManager.EDIT)) {
					throw new InsufficientPrivilegesException("You are trying to reassign study instance UID " + studyInstanceUid + " from project " + existing + " but do not have sufficient privileges to modify study routings for this project.");
				}
				if (log.isDebugEnabled()) {
					log.debug("Updating routing assignment for study instance UID {} to project {}, updated by user {}", studyInstanceUid, projectId, user.getLogin());
				}
				routing.put(org.nrg.xdat.services.StudyRoutingService.PROJECT, projectId);
				routingService.update(studyInstanceUid, routing);
			}
		} catch (Exception e) {
			log.error("An error occurred checking user permissions", e);
			throw new InitializationException("An error occurred checking user permissions");
		}
	}

	@Override
	public void deleteStudyRouting(UserI user, String studyInstanceUid) throws InsufficientPrivilegesException, InitializationException {
		 org.nrg.xdat.services.StudyRoutingService routingService =getRoutingService();
		if (StringUtils.isBlank(studyInstanceUid)) {
			if (!Roles.isSiteAdmin(user)) {
				throw new InsufficientPrivilegesException("You must be a site administrator to delete all study routings for this site.");
			}
			log.warn("Closing all study instance UID assignments, per user {}", user.getLogin());
			routingService.closeAll();
		} else {
			try {
				Map<String, String> routing = routingService.findStudyRouting(studyInstanceUid);
				if (routing == null) {
					throw new NotFoundException("Couldn't find a routing for the study instance UID: " + studyInstanceUid);
				}
				if (!Permissions.can(user, "xnat:mrSessionData/project",
						routing.get(org.nrg.xdat.services.StudyRoutingService.PROJECT), SecurityManager.EDIT)) {
					throw new InsufficientPrivilegesException( "You do not have sufficient privileges to delete study routings for this project.");
				}
				log.info("Closing study instance UID {} assignment, per user {}", studyInstanceUid, user.getLogin());
				routingService.close(studyInstanceUid);
			} catch (Exception e) {
				log.error("An error occurred checking user permissions", e);
				throw new InitializationException("An error occurred checking user permissions");
			}
		}
	}

	private List<StudyRoutingUtil> getAllStudyRouting(UserI user, org.nrg.xdat.services.StudyRoutingService routingService) throws InitializationException {
		List<StudyRoutingUtil> response = new ArrayList<>();
		Map<String, Map<String, String>> routings = routingService.findAllRoutings();
		if (routings != null && routings.size() > 0) {
			if (log.isDebugEnabled()) {
				log.debug("Request made for all system routings, found {} results", routings.size());
			}
			for(final String studyInstanceUid : routings.keySet()) {
				final Map<String, String> routing = routings.get(studyInstanceUid);
				try {
					if (UserHelper.getUserHelperService(user).hasEditAccessToSessionDataByTag(getRoutingAttribute(routing, org.nrg.xdat.services.StudyRoutingService.PROJECT))) {
						response = getStudyRouting(studyInstanceUid, routing);
						return response;
					}
				} catch (Exception e) {
					final String message = "An error occurred trying to resolve privileges on the study routing for study instance UID: " + studyInstanceUid;
					log.error(message, e);
					throw new InitializationException(e.getMessage());
				}
			}
		} else if (log.isDebugEnabled()) {
			log.debug("Request made for all system routings, found no results!");
		}
		return response;
	}


	private StudyRoutingUtil getAllStudyRoutingByInstanceUid(UserI user, String studyInstanceUid, org.nrg.xdat.services.StudyRoutingService routingService) throws NotFoundException, InitializationException {
		  final Map<String, String> routing = routingService.findStudyRouting(studyInstanceUid);
          if (routing == null || routing.size() == 0) {
              log.info("Request made for routing for study instance UID {}, but nothing was found for that value.", studyInstanceUid);
              throw new NotFoundException("Couldn't find a routing for the study instance UID: " + studyInstanceUid);
          } else {
              try {
                  if (!UserHelper.getUserHelperService(user).hasEditAccessToSessionDataByTag(getRoutingAttribute(routing, org.nrg.xdat.services.StudyRoutingService.PROJECT))) {
                      throw new InsufficientPrivilegesException("You do not have sufficient privileges to view the routing for this study instance UID.");
                  }
              } catch (Exception e) {
                  final String message = "An error occurred trying to resolve privileges on the study routing for study instance UID: " + studyInstanceUid;
                  log.error(message, e);
                  throw new InitializationException(e.getMessage());
              }
              if (log.isDebugEnabled()) {
                  log.debug("Request made for routing for study instance UID {}, found routing: {}", studyInstanceUid, routing.toString());
			}
			return getStudyRoutingByInstanceId(studyInstanceUid, routing);
		}
	}
	
	private List<StudyRoutingUtil> getStudyRouting(String studyInstanceUid, Map<String, String> routing) {
		List<StudyRoutingUtil> response = new ArrayList<>();
		 response.add(StudyRoutingUtil.builder()
    			 .studyInstanceUid(studyInstanceUid)
    			 .project(getRoutingAttribute(routing, org.nrg.xdat.services.StudyRoutingService.PROJECT))
    			 .subject(getRoutingAttribute(routing, org.nrg.xdat.services.StudyRoutingService.SUBJECT))
    			 .user(getRoutingAttribute(routing, org.nrg.xdat.services.StudyRoutingService.USER))
    			 .created(getRoutingAttribute(routing, org.nrg.xdat.services.StudyRoutingService.CREATED))
    			 .accessed(getRoutingAttribute(routing, org.nrg.xdat.services.StudyRoutingService.ACCESSED))
    			 .label(getRoutingAttribute(routing, org.nrg.xdat.services.StudyRoutingService.LABEL))
    			 .build());
		return response;
	}
	
	private StudyRoutingUtil getStudyRoutingByInstanceId(String studyInstanceUid, Map<String, String> routing) {
		return  StudyRoutingUtil.builder()
    			 .studyInstanceUid(studyInstanceUid)
    			 .project(getRoutingAttribute(routing, org.nrg.xdat.services.StudyRoutingService.PROJECT))
    			 .subject(getRoutingAttribute(routing, org.nrg.xdat.services.StudyRoutingService.SUBJECT))
    			 .user(getRoutingAttribute(routing, org.nrg.xdat.services.StudyRoutingService.USER))
    			 .created(getRoutingAttribute(routing, org.nrg.xdat.services.StudyRoutingService.CREATED))
    			 .accessed(getRoutingAttribute(routing, org.nrg.xdat.services.StudyRoutingService.ACCESSED))
    			 .label(getRoutingAttribute(routing, org.nrg.xdat.services.StudyRoutingService.LABEL))
    			 .build();
	}
          
	private String getRoutingAttribute(final Map<String, String> routing, final String attribute) {
		return routing.containsKey(attribute) ? routing.get(attribute) : "";
	}
	
	private org.nrg.xdat.services.StudyRoutingService getRoutingService() {
		return  XDAT.getContextService().getBean( org.nrg.xdat.services.StudyRoutingService.class);
	}
}
