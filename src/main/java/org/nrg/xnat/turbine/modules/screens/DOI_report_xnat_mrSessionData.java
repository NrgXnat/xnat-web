/*
 * web: org.nrg.xnat.turbine.modules.screens.XDATScreen_report_xnat_mrSessionData
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.turbine.modules.screens;

import org.apache.log4j.Logger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.model.XnatExperimentdataShareI;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.turbine.modules.screens.SecureReport;

import java.util.List;
import org.nrg.xdat.om.XnatImagesessiondata;


/**
 * @author Tim
 *
 */
public class DOI_report_xnat_mrSessionData extends SecureReport {
	static Logger logger = Logger.getLogger(DOI_report_xnat_mrSessionData.class);

    /* (non-Javadoc)
     * @see org.nrg.xdat.turbine.modules.screens.SecureReport#finalProcessing(org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
     */
    public void finalProcessing(RunData data, Context context) {
        try {
            XnatImagesessiondata session = new XnatImagesessiondata(item);
            context.put("session",session);
            
            
            context.put("workflows",session.getWorkflows());

            if(context.get("project")==null){
                String proj = session.getProject();
                if(!Permissions.canReadProject(XDAT.getUserDetails(),proj)){
                    // If user cannot read that project, look through the projects that session is shared into. If user
                    // can view the data in one of those projects they should view this session from that project's context.
                    List<XnatExperimentdataShareI> list = session.getSharing_share();
                    for(XnatExperimentdataShareI exptShare: list){
                        if(Permissions.canReadProject(XDAT.getUserDetails(),exptShare.getProject())){
                            proj=exptShare.getProject();
                            break;
                        }
                    }
                }
            	context.put("project", proj);
            }
            
            for(XnatImagescandataI scan:session.getSortedScans()){
            	((XnatImagescandata)scan).setImageSessionData(session);
            }
        } catch (Exception e) {
            logger.error("",e);
        }
    }

    
    /**
     * Return null to use the defualt settings (which are configured in xdat:element_security).  Otherwise, true will force a pre-load of the item.
     * @return
     */
    public Boolean preLoad()
    {
        return Boolean.FALSE;
    }
}
