package org.nrg.xnat.services.prearchive.util;

import java.io.File;

import org.nrg.action.ActionException;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.xdat.bean.XnatImagesessiondataBean;
import org.nrg.xft.exception.InvalidPermissionException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.prearchive.PrearcTableBuilder;
import org.nrg.xnat.helpers.prearchive.PrearcUtils;
import org.nrg.xnat.restlet.resources.prearchive.PrearcSessionResourceA.PrearcInfo;
import org.restlet.data.Status;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrearcInfoUtil {
	public final File sessionDIR;
	public final File sessionXML;
	public final XnatImagesessiondataBean session;
	
	public PrearcInfoUtil(final File dir, final File xml, final XnatImagesessiondataBean session){
		this.sessionDIR=dir;
		this.sessionXML=xml;
		this.session=session;
	}
	
	public static PrearcInfoUtil retrieveSessionBean(UserI user, String project, String timestamp, String session) throws ActionException {
		File sessionDIR;
		File srcXML;
		try {
			sessionDIR = PrearcUtils.getPrearcSessionDir(user, project, timestamp, session,false);
			srcXML=new File(sessionDIR.getAbsolutePath()+".xml");
		} catch (InvalidPermissionException e) {
			log.error("",e);
			throw new ClientException(Status.CLIENT_ERROR_FORBIDDEN,e);
		} catch (Exception e) {
			log.error("",e);
			throw new ServerException(e);
		}
		
		if(!srcXML.exists()){
			throw new ClientException(Status.CLIENT_ERROR_NOT_FOUND,"Unable to locate prearc resource.",new Exception());
		}
		
		try {
			return new PrearcInfoUtil(sessionDIR, srcXML, PrearcTableBuilder.parseSession(srcXML));
		} catch (Exception e) {
			log.error("",e);
			throw new ServerException(e);
		}
	}
}
