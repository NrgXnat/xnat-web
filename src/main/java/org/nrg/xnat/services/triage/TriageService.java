
//Author: James Dickson <james@radiologics.com>
package org.nrg.xnat.services.triage;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.nrg.action.ActionException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.model.TriageDto;
import org.nrg.xft.exception.InvalidItemException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.uri.URIManager.DataURIA;
import org.nrg.xnat.helpers.uri.archive.ResourceURII;

import com.google.common.collect.ListMultimap;

/**
 * @author james
 *
 */
public interface TriageService {
	abstract  public List<String> move(UserI user,Integer eventId,Boolean overwrite,ListMultimap<String,Object> params,DataURIA src,ResourceURII dest) throws Exception;
	abstract  public boolean isLocked(UserI user, Integer eventId, Boolean overwrite,ListMultimap<String, Object> otherParams, DataURIA key, ResourceURII value)throws Exception;
    
	List<TriageDto> findTriageByProjectId(UserI user, String projectId, HttpServletRequest request);
	
	void findTriagefilesByProjectIdAndXname(UserI user, String projectId, String xName, HttpServletRequest request, String compression) throws Exception;
	
	void findTriageByProjectIdAndXname(UserI user, String projectId, String xName,String file, HttpServletRequest request, String compression) throws InvalidItemException, NotFoundException, InsufficientPrivilegesException, ActionException, Exception;
	
	void deleteTriage(UserI user, String projectId, String xname, String file, String eventReason, String eventComment, String eventId);
	
	void create(UserI user, String projectId, String xname, String file,String eventReason, String eventComment, String eventId,String target,boolean inbody, String overwrite,String format,String content,String event_reason,String extract,HttpServletRequest request);
	
	void updte(UserI user, String projectId, String xname, String file,String eventReason, String eventComment, String eventId,String target,boolean inbody, String overwrite,String format,String content,String event_reason,String extract,HttpServletRequest request);
	
	void approve(UserI user, String src, String dest, String overwrite, String eventId) throws InitializationException, ActionException;
}