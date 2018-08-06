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
import org.nrg.xdat.om.XnatMrsessiondata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.turbine.modules.screens.SecureReport;
import org.nrg.xnat.entities.Doi;
import org.nrg.xnat.services.system.DoiService;

import java.util.List;

/**
 * @author Tim
 *
 */
public class XDATScreen_report_xnat_mrSessionData extends SecureReport {
	static Logger logger = Logger.getLogger(XDATScreen_report_xnat_mrSessionData.class);

    /* (non-Javadoc)
     * @see org.nrg.xdat.turbine.modules.screens.SecureReport#finalProcessing(org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
     */
    public void finalProcessing(RunData data, Context context) {
        try {
            XnatMrsessiondata mr = new XnatMrsessiondata(item);
            context.put("mr",mr);
            
            
            context.put("workflows",mr.getWorkflows());

            Object projObj = context.get("project");
            String proj = "";
            if(projObj==null) {
                proj = mr.getProject();
                if(!Permissions.canReadProject(XDAT.getUserDetails(),proj)){
                    // If user cannot read that project, look through the projects that session is shared into. If user
                    // can view the data in one of those projects they should view this session from that project's context.
                    List<XnatExperimentdataShareI> list = mr.getSharing_share();
                    for(XnatExperimentdataShareI exptShare: list){
                        if(Permissions.canReadProject(XDAT.getUserDetails(),exptShare.getProject())){
                            proj=exptShare.getProject();
                            break;
                        }
                    }
                }
            	context.put("project", proj);
            }
            else{
                proj = projObj.toString();
            }

            try {
                DoiService service = XDAT.getContextService().getBean(DoiService.class);
                List<Doi> existingDois = service.getDoisForObjectAndProjectAndType(mr.getId(), proj,"xnat:mrSessionData");
                if (existingDois.size() > 0) {
                    context.put("doi", existingDois.get(0).getDoi());
                }
            }
            catch(Exception e){
                log.error("Error getting DOI for project",e);
            }
            
            for(XnatImagescandataI scan:mr.getSortedScans()){
            	((XnatImagescandata)scan).setImageSessionData(mr);
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
