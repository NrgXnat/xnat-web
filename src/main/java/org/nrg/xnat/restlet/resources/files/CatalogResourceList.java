/*
 * web: org.nrg.xnat.restlet.resources.files.CatalogResourceList
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.restlet.resources.files;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.nrg.action.ActionException;
import org.nrg.action.ServerException;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.om.*;
import org.nrg.xft.XFTItem;
import org.nrg.xft.XFTTable;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.turbine.utils.ArchivableItem;
import org.nrg.xnat.utils.CatalogUtils;
import org.nrg.xnat.utils.WorkflowUtils;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

@Slf4j
public class CatalogResourceList extends XNATTemplate {
    public CatalogResourceList(Context context, Request request, Response response) throws ServerException {
        super(context, request, response);

        if (!recons.isEmpty() || !scans.isEmpty() || !expts.isEmpty() || sub != null || proj != null) {
            getVariants().add(new Variant(MediaType.APPLICATION_JSON));
            getVariants().add(new Variant(MediaType.TEXT_HTML));
            getVariants().add(new Variant(MediaType.TEXT_XML));
        } else {
            throw new ServerException(Status.CLIENT_ERROR_NOT_FOUND, "You must specify an entity for which you want to retrieve resources, e.g. experiments, scan IDs, subjects, or projects.");
        }
    }

    @Override
    public boolean allowPut() {
        return true;
    }

    @Override
    public boolean allowPost() {
        return true;
    }

    @Override
    public void handlePut() {
        handlePost();
    }

    @Override
    public void handlePost() {
        XFTItem item;

        try {
            final UserI user = getUser();

            item=loadItem("xnat:resourceCatalog", true);

            if(item==null){
                getResponse().setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, "Need POST Contents");
                return;
            }

            if(item.instanceOf("xnat:resourceCatalog")){
                final XnatResourcecatalog resourceCatalog = (XnatResourcecatalog)BaseElement.GetGeneratedItem(item);

                if (!validateNewResourceCatalog(user, resourceCatalog)) {
                    return;
                }

                setCatalogAttributes(user, resourceCatalog);

                PersistentWorkflowI wrk=PersistentWorkflowUtils.getWorkflowByEventId(user,getEventId());
                if(wrk==null && "SNAPSHOTS".equals(resourceCatalog.getLabel())){
                    if(getSecurityItem() instanceof XnatExperimentdata){
                        Collection<? extends PersistentWorkflowI> workflows = PersistentWorkflowUtils.getOpenWorkflows(user,((ArchivableItem)getSecurityItem()).getId());
                        if(workflows!=null && workflows.size()==1){
                            wrk=(WrkWorkflowdata)CollectionUtils.get(workflows, 0);
                            if(!"xnat_tools/AutoRun.xml".equals(wrk.getPipelineName())){
                                wrk=null;
                            }
                        }
                    }
                }


                boolean isNew=false;
                if(wrk==null){
                    isNew=true;
                    wrk=PersistentWorkflowUtils.buildOpenWorkflow(user, getSecurityItem().getItem(), newEventInstance(EventUtils.CATEGORY.DATA,(getAction()!=null)?getAction():EventUtils.CREATE_RESOURCE));
                }

                assert wrk != null;
                EventMetaI ci=wrk.buildEvent();

                insertCatalog(resourceCatalog);

                if(isNew){
                    WorkflowUtils.complete(wrk, ci);
                }

                returnSuccessfulCreateFromList(resourceCatalog.getXnatAbstractresourceId() + "");
            }else{
                getResponse().setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY,"Only ResourceCatalog documents can be PUT to this address.");
            }
        } catch (ActionException e) {
			this.getResponse().setStatus(e.getStatus(),e.getMessage());
		} catch (Exception e) {
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,e.getMessage());
            log.error("", e);
        }
    }


    @Override
    public Representation represent(Variant variant) {
        final UserI user = getUser();

        XFTTable table = null;

        if (recons.size() > 0 || scans.size() > 0 || expts.size() > 0 || sub != null || proj != null) {
            try {
                table = loadCatalogs(null, false, isQueryVariableTrue("all"));
            } catch (Exception e) {
                log.error("", e);
            }
        }

        final boolean fileStats      = isQueryVariableTrue("file_stats");
        final boolean cacheFileStats = isQueryVariableTrue("cache_file_stats");
        if (fileStats) {
            try {
                if (proj == null) {
                    if (parent.getItem().instanceOf("xnat:experimentData")) {
                        proj = ((XnatExperimentdata) parent).getPrimaryProject(false);
                        // Per FogBugz 4746, prevent NPE when user doesn't have access to resource (MRH)
                        // Check access through shared project when user doesn't have access to primary project
                        if (proj == null) {
                            proj = (XnatProjectdata) ((XnatExperimentdata) parent).getFirstProject();
                        }
                    } else if (security.getItem().instanceOf("xnat:experimentData")) {
                        proj = ((XnatExperimentdata) security).getPrimaryProject(false);
                        // Per FogBugz 4746, ....
                        if (proj == null) {
                            proj = (XnatProjectdata) ((XnatExperimentdata) security).getFirstProject();
                        }
                    } else if (security.getItem().instanceOf("xnat:subjectData")) {
                        proj = ((XnatSubjectdata) security).getPrimaryProject(false);
                        // Per FogBugz 4746, ....
                        if (proj == null) {
                            proj = (XnatProjectdata) ((XnatSubjectdata) security).getFirstProject();
                        }
                    } else if (security.getItem().instanceOf("xnat:projectData")) {
                        proj = (XnatProjectdata) security;
                    }
                }

            } catch (ElementNotFoundException e) {
                log.error("", e);
            }
        }


        final Hashtable<String, Object> params = new Hashtable<>();
        params.put("title", "Resources");

        if (table != null) {
            table = CatalogUtils.populateTable(table, user, proj, cacheFileStats);

            // If table.rows() is null, set recordCount to 0
            final ArrayList<Object[]> records     = table.rows();
            final int                 recordCount = (records != null) ? records.size() : 0;

            if (log.isDebugEnabled()) {
                log.debug("Found a total of " + recordCount + " records");
            }
            params.put("totalRecords", recordCount);
        }

        return representTable(table, overrideVariant(variant), params);
    }
}
