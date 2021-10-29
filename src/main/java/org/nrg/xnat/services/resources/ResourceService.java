package org.nrg.xnat.services.resources;

import org.nrg.action.ActionException;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.xapi.exceptions.*;
import org.nrg.xapi.model.DIRResource;
import org.nrg.xapi.model.ResourceFile;
import org.nrg.xapi.model.TriageDto;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.model.XnatResourceI;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.exception.InvalidItemException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.resource.XnatResourceInfo;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.resources.impl.ResourceServiceImpl.InvalidFileCharacters;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("DuplicateThrows")
public interface ResourceService {
	
	 /** Start Resource Service Methods*/
	 List<XnatAbstractresource> findByExperimentId(final UserI user, final String experimentId) throws NotFoundException, DataFormatException;
	
	 Optional<XnatAbstractresourceI> findByIdAndExperimentId(final UserI user, final Integer resourceId, final String experimentId) throws DataFormatException, NotFoundException;

	 List<XnatAbstractresource> findByProjectIdAndSubjectIdAndExperimentId(final UserI user, final String projectId, final String subjectId, final String experimentId) throws DataFormatException, NotFoundException;
	
	 List<XnatAbstractresource>findByExperimentIdAndScanId(final UserI user, final String assessedId, final String scanId) throws DataFormatException, NotFoundException;
	
	 List<XnatAbstractresource>findByProjectId(final UserI user, final String projectId) throws DataFormatException, NotFoundException;
	
	 List<XnatAbstractresource>findByProjectIdAndLabel(final UserI user, final String projectId, String label) throws DataFormatException, NotFoundException;

	 Optional<XnatAbstractresource> findByIdAndProjectId(final UserI user, final Integer resourceId, final String projectId) throws DataFormatException, NotFoundException;

	 List<XnatAbstractresource>findBySubjectId(final UserI user, final String subjectId) throws DataFormatException, NotFoundException;
	
     List<XnatAbstractresource>findByProjectIdAndSubjectId(final UserI user, final String projectId, final String subjectId) throws DataFormatException, NotFoundException;
	
	 Optional<XnatAbstractresource> findByIdAndProjectIdAndSubjectId(final UserI user,final Integer resourceId, final String projectId, final String subjectId) throws DataFormatException, NotFoundException;

	 Optional<XnatAbstractresource> findByIdAndSubjectId(UserI sessionUser, Integer resourceId, String subjectId) throws DataFormatException, NotFoundException;

	 List<XnatAbstractresource>findByExperimentIdAndAssessedId(UserI user,String experimentId, String assessedId, String type) throws DataFormatException, NotFoundException;

	 Optional<XnatAbstractresource> findByExperimentIdAndAssessedIdAndResourceId(UserI sessionUser, String experimentId, String assessedId, String type, Integer resourceId) throws DataFormatException, NotFoundException;

	 List<XnatAbstractresource>findByProjectIdAndSubjectIdAndExperimentIdAndAssessorId(UserI sessionUser, String projectId, String subjectId, String experimentId, String assessedId, String type) throws DataFormatException, NotFoundException;
	
	 XnatResourcecatalog create(UserI user, String projectId, String subjectId, String experimentId, String assessorId, String scanId, String type, XnatResourceI xnatResourcecatalog, XnatEventUtil event, String description, String format, String content, String [] tags);
	
	 void delete(UserI user, String projectId, String subjectId, String experimentId,String assessorId,String scanId,String type, String resourceId,XnatEventUtil event);

	 List<XnatAbstractresource> findByProjectIdAndSubjectIdAndExperimentIdAndScanId(UserI user, String projectId, String subjectId, String assessedId, String scanId) throws DataFormatException, NotFoundException;
	 /* End Resource Service Methods*/
	 
	 
	 /** Start DIR Resource Service Methods
	 * @throws InvalidFileCharacters */
	 List<DIRResource>  findAllDIRResources(UserI user, String projectId, String experimentId, String filepath, boolean recursive, boolean isXarReference) throws NotFoundException, NotAuthenticatedException, InvalidFileCharacters;

	 StreamingResponseBody  findAllXARResources(UserI user, String projectId, String experimentId,String filepath, boolean recursive, boolean isXarReference, HttpServletRequest sRequest, HttpHeaders hRequest,String compression) throws NotFoundException, NotAuthenticatedException, InvalidFileCharacters, InitializationException;

     String getContentDisposition();
     /** End DIR Resource Service Methods*/
     
     
     /** Start File Service Methods*/
     List<ResourceFile> findByProjectId(UserI user, String projectId, String[] contents, String[] formats) throws DataFormatException, NotFoundException;

	 List<ResourceFile> findBySubjectId(UserI user, String subjectId, String[] contents, String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;

	 List<ResourceFile> findByProjectIdAndSubjectId(UserI user, String projectId, String subjectId, String[] contents, String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;

	 List<ResourceFile> findByProjectIdAndResourceId(UserI user, String projectId, Integer resourceId, String[] contents, String[] formats) throws DataFormatException, NotFoundException;

	 List<ResourceFile> findBySubjectIdAndResourceId(UserI user, String subjectId, Integer resourceId, String[] contents, String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;

	 List<ResourceFile> findByExperimentIdAndAssessorId(UserI user, String experimentId, String assessorId, String[] contents, String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;

	 List<ResourceFile> findByProjectIdAndSubjectIdAndExperimentIdAndAssessorId(UserI sessionUser, String projectId, String subjectId, String experimentId, String assessedId, String[] contents, String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;

	 List<ResourceFile> findByExperimentId(UserI user, String experimentId, String[] contents, String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;

	 List<ResourceFile> findByExperimentIdAndResourceId(UserI user, String experimentId, Integer resourceId, String[] contents, String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;
	
	 void deleteResourceFile(UserI user,String projectId, String subjectId, String experimentId, String assessorId, String scanId, String type,String resourceId, boolean removeFiles, XnatEventUtil event) throws Exception;

	 Integer createResourceFile(UserI user, XnatResourceInfo xnatResourceInfo, String projectId, String subjectId, String experimentId,String assessorId, String scanId, String type, String resourceId, XnatEventUtil event) throws Exception;

	 List<ResourceFile> findByExperimentIdAndAssessorIdAndResourceId(UserI user, String experimentId, String assessorId, Integer resourceId, String[] contents, String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;

	 List<ResourceFile> findByProjectIdAndSubjectIdAndExperimentId(UserI user, String projectId, String subjectId, String experimentId, String[] contents, String[] formats) throws DataFormatException, NotFoundException, ElementNotFoundException;
	 /** End File Service Methods*/
	 
	 
	 /** Start refresh catalog Service Methods*/
	 void createCatalogRefresh(UserI user, List<String> resources, boolean append, boolean checksum, boolean delete, boolean populateStats, List<String> options) throws ClientException, ServerException;
	 /** End refresh catalog Service Methods*/
	 
	 
	 /** Start Triage Service Methods*/
	 List<TriageDto> findTriageByProjectId(UserI user, String projectId, HttpServletRequest request);
		
	void findTriageFilesByProjectIdAndXname(UserI user, String projectId, String xName, HttpServletRequest request, String compression) throws Exception;

	void findTriageByProjectIdAndXname(UserI user, String projectId, String xName,String file, HttpServletRequest request, String compression) throws InvalidItemException, NotFoundException, InsufficientPrivilegesException, ActionException, Exception;

	void deleteTriage(UserI user, String projectId, String xname, String file, String eventReason, String eventComment, String eventId);

	void create(UserI user, String projectId, String xname, String file,String eventReason, String eventComment, String eventId,String target,boolean inbody, String overwrite,String format,String content,String extract,HttpServletRequest request);

	void update(UserI user, String projectId, String xname, String file,String eventReason, String eventComment, String eventId,String target,boolean inbody, String overwrite,String format,String content,String extract,HttpServletRequest request);
	 /** End Triage Service Methods*/
}