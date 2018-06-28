/*
 * web: org.nrg.xnat.turbine.modules.screens.XDATScreen_report_xnat_subjectData
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.turbine.modules.screens;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.turbine.modules.screens.VelocityScreen;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.model.XnatProjectparticipantI;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.turbine.modules.screens.SecureReport;
import org.nrg.xdat.turbine.utils.TurbineUtils;
import org.nrg.xft.ItemI;
import org.nrg.xnat.entities.Doi;
import org.nrg.xnat.services.system.DoiService;

/**
 * @author Tim
 *
 */
public class DOI_report_xnat_subjectData extends VelocityScreen {
	static Logger logger = Logger.getLogger(XDATScreen_report_xnat_mrSessionData.class);

    public void doBuildTemplate(RunData data, Context context) {

        try {
            if (data.getParameters().containsKey("project")) {
                context.put("project", TurbineUtils.escapeParam(((String) TurbineUtils.GetPassedParameter("project", data))));
            }
            context.put("server", TurbineUtils.GetFullServerPath());

            ItemI item = TurbineUtils.getDataItem(data);

            if (item== null)
            {
                //System.out.println("No data item passed... looking for item passed by variables");
                try {
                    item = TurbineUtils.GetItemBySearch(data,preLoad());
                } catch (IllegalAccessException e1) {
                    logger.error("", e1);
                    data.setMessage(e1.getMessage());
                    return;
                } catch (Exception e1) {
                    logger.error("", e1);
                    data.setMessage(e1.getMessage());
                    data.setScreenTemplate("Error.vm");
                    return;
                }
            }
            if (data.getParameters().containsKey("doi")) {
                boolean doiFound = false;
                String doi = "";
                try {
                    doi = TurbineUtils.escapeParam(((String) TurbineUtils.GetPassedParameter("doi", data)));
                    DoiService service = XDAT.getContextService().getBean(DoiService.class);
                    Doi doiObject = service.get(Long.parseLong(doi));
                    if(doiObject!=null){
                        if(StringUtils.equalsIgnoreCase(doiObject.getObjectId(),item.getStringProperty("id"))) {
                            doiFound = true;
                        }
                        context.put("doiObject", doiObject);
                        String pubs = doiObject.getRelatedPublications();
                        String links = doiObject.getLinks();
                        String[] pubArray = pubs.split(" , ");
                        String[] linkArray = links.split(" , ");
                        context.put("pubArray", pubArray);
                        context.put("linkArray", linkArray);
                    }
                    context.put("doiId", doi);
                }
                catch(Exception e){
                }
                if(!doiFound){
                    throw new NotFoundException(doi);
                }
            }

            context.put("item", item.getItem());

            ItemI om = BaseElement.GetGeneratedItem(item);
            context.put("om", om);

            context.put("canReadAsGuest",Permissions.canRead(Users.getGuest(),item));
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
