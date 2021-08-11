package org.nrg.xnat.services.workflow;

import java.util.List;

import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.WrkWorkflowdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.workflow.dto.WrkWorkflowdataDto;

public interface WorkflowService {

	List<WrkWorkflowdataDto> findAllWrkWorkflowdata(UserI user) throws NotFoundException;
	
	WrkWorkflowdata findWrkWorkflowdata(UserI user, String workflowId ) throws DataFormatException, NotFoundException;
}
