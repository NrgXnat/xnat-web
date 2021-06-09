package org.nrg.xnat.services.protocol;

import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatDatatypeprotocol;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.model.util.XnatEventUtil;

public interface ProtocolService {

	XnatDatatypeprotocol findByProjectIdAndProtocolId(UserI user, String projectId, String protocolId, String dataType,  XnatEventUtil event ) throws NotFoundException;
	
	XnatDatatypeprotocol update(UserI user, String projectId, String protocolId, String dataType, String gender, XnatDatatypeprotocol protocol, XnatEventUtil event );
}
