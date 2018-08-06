/*
 * web: org.nrg.xnat.turbine.modules.screens.XDATScreen_report_xnat_subjectData
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
import org.nrg.xdat.model.XnatProjectparticipantI;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.turbine.modules.screens.SecureReport;
import org.nrg.xnat.entities.Doi;
import org.nrg.xnat.services.system.DoiService;

import java.util.List;

/**
 * @author Tim
 *
 */
public class XDATScreen_report_xnat_subjectData extends SecureReport {
	static Logger logger = Logger.getLogger(XDATScreen_report_xnat_mrSessionData.class);

    /* (non-Javadoc)
     * @see org.nrg.xdat.turbine.modules.screens.SecureReport#finalProcessing(org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
     */
    public void finalProcessing(RunData data, Context context) {
        try {
            XnatSubjectdata sub = new XnatSubjectdata(item);
            Object projObj = context.get("project");
            String proj = "";
            if(projObj==null) {
                proj = sub.getProject();
                if (!Permissions.canReadProject(XDAT.getUserDetails(), proj)) {
                    // If user cannot read that project, look through the projects that session is shared into. If user
                    // can view the data in one of those projects they should view this subject from that project's context.
                    List<XnatProjectparticipantI> list = sub.getSharing_share();
                    for (XnatProjectparticipantI subShare : list) {
                        if (Permissions.canReadProject(XDAT.getUserDetails(), subShare.getProject())) {
                            proj = subShare.getProject();
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
                List<Doi> existingDois = service.getDoisForObjectAndProjectAndType(sub.getId(), proj,"xnat:subjectData");
                if (existingDois.size() > 0) {
                    context.put("doi", existingDois.get(0).getDoi());
                }
            }
            catch(Exception e){
                log.error("Error getting DOI for project",e);
            }

            context.put("subject",sub);
        } catch (Exception e) {
            logger.error("",e);
        }
    }
}
