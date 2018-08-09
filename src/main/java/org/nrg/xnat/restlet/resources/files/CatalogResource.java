/*
 * web: org.nrg.xnat.restlet.resources.files.CatalogResource
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.restlet.resources.files;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.nrg.action.ActionException;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.bean.CatCatalogBean;
import org.nrg.xdat.om.*;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xft.XFTItem;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xnat.restlet.representations.BeanRepresentation;
import org.nrg.xnat.restlet.representations.ItemXMLRepresentation;
import org.nrg.xnat.turbine.utils.ArchivableItem;
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
import java.util.Objects;

@Slf4j
public class CatalogResource extends XNATCatalogTemplate {
    public CatalogResource(Context context, Request request, Response response) {
        super(context, request, response, false);

        _filePathIsEmpty = checkForNonEmptyFilePath(getRequest().getResourceRef().getRemainingPart());

        try {
            if (!org.springframework.util.ObjectUtils.isEmpty(catalogs)) {
                for (final Object[] row : catalogs.rows()) {
                    final Integer id    = (Integer) row[0];
                    final String  label = (String) row[1];
                    resource_ids.stream().filter(resourceId -> id.toString().equals(resourceId) || StringUtils.equals(label, resourceId)).forEach(resourceId -> resources.add(XnatAbstractresource.getXnatAbstractresourcesByXnatAbstractresourceId(row[0], getUser(), false)));
                }
            }

            getVariants().add(new Variant(MediaType.TEXT_XML));
        } catch (Exception e) {
            log.error("", e);
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
    public boolean allowDelete() {
        return true;
    }

    @Override
    public Representation represent(Variant variant) {
        if (failFastDueToNonEmptyFilePath()) {
            return null;
        }

        getAllMatches();

        if (resources.isEmpty()) {
            getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND, "Unable to find the specified catalog.");
        } else if (resources.size() == 1) {
            XnatAbstractresource resource = resources.get(0);
            try {
                if (proj == null) {
                    initializeProjectFromExperiment();
                }

                if (resource.getItem().instanceOf("xnat:resourceCatalog")) {
                    final boolean             includeRoot     = isQueryVariableTrue("includeRootPath");
                    final XnatResourcecatalog resourceCatalog = (XnatResourcecatalog) resource;
                    final CatCatalogBean      catalog         = resourceCatalog.getCleanCatalog(proj.getRootArchivePath(), includeRoot, null, null);

                    if (catalog != null) {
                        return new BeanRepresentation(catalog, MediaType.TEXT_XML);
                    } else {
                        getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND, "Unable to find catalog file.");
                    }
                } else {
                    return new ItemXMLRepresentation(resource.getItem(), MediaType.TEXT_XML);
                }
            } catch (ElementNotFoundException e) {
                log.error("", e);
            }
        }

        return null;
    }

    @Override
    public void handlePut() {
        handlePost();
    }

    @Override
    public void handlePost() {
        if (failFastDueToNonEmptyFilePath()) {
            return;
        }

        if (ObjectUtils.allNotNull(parent, security)) {
            final UserI user = getUser();
            try {
                if (!Permissions.canEdit(user, security)) {
                    getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, "User account doesn't have permission to modify this session.");
                    return;
                }

                if (!resources.isEmpty()) {
                    getResponse().setStatus(Status.CLIENT_ERROR_CONFLICT, "Specified resource already exists.");
                    return;
                }

                final XFTItem item = loadItem("xnat:resourceCatalog", true);
                if (item == null) {
                    getResponse().setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, "Need POST Contents");
                    return;
                }

                if (!item.instanceOf("xnat:resourceCatalog")) {
                    getResponse().setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, "Only ResourceCatalog documents can be PUT to this address.");
                    return;
                }

                final XnatResourcecatalog resourceCatalog = (XnatResourcecatalog) BaseElement.GetGeneratedItem(item);
                if (!validateNewResourceCatalog(user, resourceCatalog)) {
                    return;
                }

                setCatalogAttributes(user, resourceCatalog);
                resourceCatalog.setLabel(resource_ids.get(0));

                PersistentWorkflowI workflow = PersistentWorkflowUtils.getWorkflowByEventId(user, getEventId());
                if (workflow == null && "SNAPSHOTS".equals(resourceCatalog.getLabel())) {
                    if (getSecurityItem() instanceof XnatExperimentdata) {
                        final Collection<? extends PersistentWorkflowI> workflows = PersistentWorkflowUtils.getOpenWorkflows(user, ((ArchivableItem) getSecurityItem()).getId());
                        if (workflows != null && workflows.size() == 1) {
                            workflow = (WrkWorkflowdata) CollectionUtils.get(workflows, 0);
                            if (!"xnat_tools/AutoRun.xml".equals(workflow.getPipelineName())) {
                                workflow = null;
                            } else {
                                if (StringUtils.isBlank(workflow.getCategory())) {
                                    workflow.setCategory(EventUtils.CATEGORY.DATA);
                                    workflow.setType(EventUtils.TYPE.PROCESS);
                                    WorkflowUtils.save(workflow, workflow.buildEvent());
                                }
                            }
                        }
                    }
                }

                final boolean isNew;
                if (workflow == null) {
                    isNew = true;
                    workflow = PersistentWorkflowUtils.buildOpenWorkflow(user, getSecurityItem().getItem(), newEventInstance(EventUtils.CATEGORY.DATA, (getAction() != null) ? getAction() : EventUtils.CREATE_RESOURCE));
                } else {
                    isNew = false;
                }

                assert workflow != null;
                final EventMetaI event = workflow.buildEvent();

                insertCatalog(resourceCatalog);

                if (isNew) {
                    WorkflowUtils.complete(workflow, event);
                }
            } catch (ActionException e) {
                getResponse().setStatus(e.getStatus(), e.getMessage());
            } catch (Exception e) {
                getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
                log.error("", e);
            }
        }
    }

    @Override
    public void handleDelete() {
        if (failFastDueToNonEmptyFilePath()) {
            return;
        }

        if (resources.isEmpty() || !ObjectUtils.allNotNull(parent, security)) {
            return;
        }

        final UserI   user         = getUser();
        final XFTItem securityItem = security.getItem();

        try {
            if (!(securityItem.isActive() || securityItem.isQuarantine())) {
                getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, "Trying to modify an inactive or quarantined item");
                return;
            }

            if (!Permissions.canDelete(user, security)) {
                getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, "User account doesn't have permission to modify this session.");
                return;
            }

            for (final XnatAbstractresource resource : resources) {
                final Pair<String, String> attributes = getResourceAttributes();
                if (Objects.equals(attributes, ImmutablePair.nullPair())) {
                    getResponse().setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, "Unknown object type for the parent and security items.");
                    return;
                }

                final String               securityId = attributes.getLeft();
                final String               xsiType    = attributes.getRight();
                final String               rootPath   = proj.getRootArchivePath();

                final PersistentWorkflowI workflow = PersistentWorkflowUtils.getOrCreateWorkflowData(getEventId(), user, xsiType, securityId, (proj == null) ? null : proj.getId(), newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.REMOVE_CATALOG));
                final EventMetaI          event    = workflow.buildEvent();

                try {
                    resource.deleteWithBackup(rootPath, user, event);
                    SaveItemHelper.authorizedRemoveChild(parent.getItem(), xmlPath, resource.getItem(), user, event);
                    PersistentWorkflowUtils.complete(workflow, event);
                } catch (Exception e) {
                    PersistentWorkflowUtils.fail(workflow, event);
                    throw e;
                }
            }
        } catch (Exception e) {
            log.error("", e);
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
        }
    }

    private Pair<String, String> getResourceAttributes() throws ElementNotFoundException {
        final XFTItem parentItem   = parent.getItem();
        final XFTItem securityItem = security.getItem();
        if (parentItem.instanceOf("xnat:experimentData")) {
            if (proj == null) {
                proj = ((XnatExperimentdata) parent).getPrimaryProject(false);
            }
            return ImmutablePair.of(((XnatExperimentdata) parent).getId(), parent.getXSIType());
        } else if (securityItem.instanceOf("xnat:experimentData")) {
            if (proj == null) {
                proj = ((XnatExperimentdata) security).getPrimaryProject(false);
            }
            return ImmutablePair.of(((XnatExperimentdata) security).getId(), security.getXSIType());
        } else if (parentItem.instanceOf("xnat:subjectData")) {
            if (proj == null) {
                proj = ((XnatSubjectdata) parent).getPrimaryProject(false);
            }
            return ImmutablePair.of(((XnatSubjectdata) parent).getId(), parent.getXSIType());
        } else if (securityItem.instanceOf("xnat:subjectData")) {
            if (proj == null) {
                proj = ((XnatSubjectdata) security).getPrimaryProject(false);
            }
            return ImmutablePair.of(((XnatSubjectdata) security).getId(), security.getXSIType());
        } else if (parentItem.instanceOf("xnat:projectData")) {
            if (proj == null) {
                proj = ((XnatProjectdata) security);
            }
            return ImmutablePair.of(((XnatProjectdata) parent).getId(), parent.getXSIType());
        } else if (securityItem.instanceOf("xnat:projectData")) {
            if (proj == null) {
                proj = ((XnatProjectdata) security);
            }
            return ImmutablePair.of(((XnatProjectdata) security).getId(), security.getXSIType());
        }
        return ImmutablePair.nullPair();
    }

    private boolean checkForNonEmptyFilePath(final String remainingUrlPart) {
        // we don't care about path separators or query parameters
        // everything else will be rejected
        return StringUtils.isBlank(remainingUrlPart) || remainingUrlPart.matches("^/+") || remainingUrlPart.matches("^/*\\?.*");
    }

    /**
     * See XNAT-1674.  If the client mistakenly passes a file path to us, slap the wrist.
     * This is better than discarding the file path and naively processing the request
     * (and say, deleting the whole catalog instead of the individual file delete that was desired).
     */
    private boolean failFastDueToNonEmptyFilePath() {
        if (_filePathIsEmpty) {
            return false;
        } else {
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "This resource works at the catalog level only and does not accept file paths.  To work with the resource files, append '/files' after the resource ID.");
            return true;
        }
    }

    private void getAllMatches() {
        catalogs = null;
        resources = new ArrayList<>();
        try {
            catalogs = loadCatalogs(resource_ids, false, true);
        } catch (Exception e) {
            log.error("", e);
        }

        if (catalogs != null && catalogs.size() > 0) {
            for (Object[] row : catalogs.rows()) {
                Integer id    = (Integer) row[0];
                String  label = (String) row[1];

                for (String resourceID : resource_ids) {
                    if (id.toString().equals(resourceID) || (label != null && label.equals(resourceID))) {
                        resources.add(XnatAbstractresource.getXnatAbstractresourcesByXnatAbstractresourceId(row[0], getUser(), false));
                    }
                }

            }
        }
    }

    private final boolean _filePathIsEmpty;
}
