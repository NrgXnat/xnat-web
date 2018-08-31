/*
 * web: org.nrg.xnat.restlet.getResources().files.FileList
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.restlet.resources.files;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.nrg.action.ActionException;
import org.nrg.action.ClientException;
import org.nrg.dcm.Dcm2Jpg;
import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.xams.xchange.services.storage.domain.auditing.EntityHistory;
import org.nrg.xams.xchange.services.storage.domain.entities.ArchiveEntry;
import org.nrg.xams.xchange.services.storage.domain.entities.ArchiveFolder;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.bean.CatCatalogBean;
import org.nrg.xdat.bean.CatEntryBean;
import org.nrg.xdat.bean.CatEntryMetafieldBean;
import org.nrg.xdat.model.CatEntryI;
import org.nrg.xdat.om.*;
import org.nrg.xdat.security.helpers.Permissions;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.turbine.utils.TurbineUtils;
import org.nrg.xft.XFTItem;
import org.nrg.xft.XFTTable;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.XftItemEventI;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.FileUtils;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xnat.helpers.file.StoredFile;
import org.nrg.xnat.helpers.resource.direct.ResourceModifierA;
import org.nrg.xnat.helpers.resource.direct.ResourceModifierA.UpdateMeta;
import org.nrg.xnat.restlet.files.utils.RestFileUtils;
import org.nrg.xnat.restlet.representations.BeanRepresentation;
import org.nrg.xnat.restlet.representations.JSONObjectRepresentation;
import org.nrg.xnat.restlet.representations.ZipRepresentation;
import org.nrg.xnat.restlet.resources.SecureResource;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.nrg.xnat.services.cache.UserProjectCache;
import org.nrg.xnat.services.messaging.file.MoveStoredFileRequest;
import org.nrg.xnat.turbine.utils.ArchivableItem;
import org.nrg.xnat.utils.CatalogUtils;
import org.nrg.xnat.utils.CatalogUtils.CatEntryFilterI;
import org.nrg.xnat.utils.WorkflowUtils;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.*;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * @author timo
 */
@SuppressWarnings("RegExpRedundantEscape")
@Component
@Slf4j
public class FileList extends XNATCatalogTemplate {
    public FileList(Context context, Request request, Response response) throws ClientException {
        super(context, request, response, isQueryVariableTrue("all", request));

        _reference = getQueryVariable("reference");
        _acceptNotFound = isQueryVariableTrueHelper(getQueryVariable("accept-not-found"));
        _delete = isQueryVariableTrue("delete", request);
        _async = isQueryVariableTrue("async", request);
        _notifyList = isQueryVariableTrue("notify", request) ? getQueryVariable("notify").split(",") : new String[0];
        _structure = getQueryVariable("structure", "default");
        _fileContent = getQueryVariables("file_content");
        _fileFormat = getQueryVariables("file_format");
        _locator = getQueryVariable("locator");
        _index = getQueryVariableAsInteger("index");
        _listContents = isQueryVariableTrueHelper(getQueryVariable("listContents"));
        _history = isQueryVariableTrueHelper(getQueryVariable("history"));

        final String fullRemainingPart = getRequest().getResourceRef().getRemainingPart();
        final String remainingPart     = StringUtils.startsWith(fullRemainingPart, "?") ? null : StringUtils.split(StringUtils.defaultIfBlank(fullRemainingPart, ""), "?")[0];
        _filePath = StringUtils.isNotBlank(remainingPart) ? StringUtils.removeStart(remainingPart, "/") : null;

        try {
            final UserI user = getUser();
            if (hasResourceIds()) {
                final List<Integer>   alreadyAdded          = new ArrayList<>();
                final XnatProjectdata project               = getProject();
                final boolean         nullOrReadableProject = project == null || Permissions.canReadProject(user, project.getId());
                if (hasCatalogs()) {
                    for (final Object[] row : getCatalogs().rows()) {
                        final Integer id    = (Integer) row[0];
                        final String  label = (String) row[1];

                        for (final String resourceId : getResourceIds()) {
                            if (!alreadyAdded.contains(id) && (id.toString().equals(resourceId) || (label != null && label.equals(resourceId)))) {
                                final XnatAbstractresource resource = XnatAbstractresource.getXnatAbstractresourcesByXnatAbstractresourceId(id, user, false);
                                if (row.length == 7) {
                                    resource.setBaseURI((String) row[6]);
                                }
                                if (nullOrReadableProject) {
                                    getResources().add(resource);
                                    alreadyAdded.add(id);
                                }
                            }
                        }
                    }
                }
                // if caller is asking for the files directly by resource ID (e.g. /experiments/{EXPT_ID}/getResources()/{RESOURCE_ID}/files),
                // the catalog will not be found by the superclass
                // (unless caller passes all=true, which seems clunky to require given that they are passing in the resource PK).
                // So here we provide an alternate path finding the resource
                // added check to make sure it's an number.  You can also reference resource labels here (not just pks).
                for (final int resourceId : getResourceIds().stream().filter(NumberUtils::isParsable).map(Integer::parseInt).collect(Collectors.toList())) {
                    final XnatAbstractresource resource = XnatAbstractresource.getXnatAbstractresourcesByXnatAbstractresourceId(resourceId, user, false);
                    if (resource != null && !alreadyAdded.contains(resourceId)) {
                        XnatImageassessordata assessorObject = null;
                        try {
                            final Matcher matcher = Pattern.compile("\\/[aA][sS][sS][eE][sS][sS][oO][rR][sS]\\/([^\\/]+)").matcher(((XnatResourcecatalog) resource).getUri());
                            if (matcher.find()) {
                                final String assessorId = matcher.group(1);
                                if (StringUtils.isNotBlank(assessorId)) {
                                    assessorObject = (XnatImageassessordata) XnatExperimentdata.getXnatExperimentdatasById(assessorId, Users.getAdminUser(), false);

                                    if (assessorObject == null) {
                                        final Matcher m2 = Pattern.compile("\\/[aA][rR][cC][hH][iI][vV][eE]\\/([^\\/]+)").matcher(((XnatResourcecatalog) resource).getUri());
                                        if (m2.find()) {
                                            String projectString = m2.group(1);
                                            assessorObject = (XnatImageassessordata) XnatExperimentdata.GetExptByProjectIdentifier(projectString, assessorId, Users.getAdminUser(), false);
                                        }
                                    }

                                }

                            }
                        } catch (Exception e) {
                            log.error("Error getting assessor object to check permissions.", e);
                        }
                        if (nullOrReadableProject && (assessorObject == null || Permissions.canRead(user, assessorObject))) {
                            getResources().add(resource);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error occurred while initializing FileList service", e);
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e, "Error during service initialization");
        }

        _resource = !getResources().isEmpty() ? getResources().get(0) : null;

        getVariants().add(new Variant(MediaType.APPLICATION_JSON));
        getVariants().add(new Variant(MediaType.TEXT_HTML));
        getVariants().add(new Variant(MediaType.TEXT_XML));
        getVariants().add(new Variant(MediaType.IMAGE_JPEG));
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

    /**
     * ****************************************
     * if(filePath>"")then returns File
     * else returns table of files
     */
    @Override
    @SuppressWarnings("unchecked")
    public Representation represent(final Variant variant) {
        final MediaType mediaType = overrideVariant(variant);

        try {
            if (!hasProject()) {
                setProject(getProjectFromRelatedItems());
            }

            if (getResources().size() == 1 && !isZIPRequest(mediaType)) {
                //one catalog`
                return handleSingleCatalog(mediaType);
            } else if (getResources().size() > 1) {
                //multiple getCatalogs()
                return handleMultipleCatalogs(mediaType);
            } else {
                try {
                    // Check project access before iterating through all of the getResources().
                    if (!hasProject() || Permissions.canReadProject(getUser(), getProject().getId())) {
                        //all getCatalogs()
                        getCatalogs().resetRowCursor();
                        for (final Hashtable<String, Object> rowHash : getCatalogs().rowHashs()) {
                            final XnatAbstractresource resource = XnatAbstractresource.getXnatAbstractresourcesByXnatAbstractresourceId(rowHash.get("xnat_abstractresource_id"), getUser(), false);
                            if (rowHash.containsKey("resource_path")) {
                                resource.setBaseURI((String) rowHash.get("resource_path"));
                            }
                            getResources().add(resource);
                        }
                    }
                } catch (Exception e) {
                    log.error("Exception checking whether user has project access.", e);
                }

                return handleMultipleCatalogs(mediaType);
            }
        } catch (ElementNotFoundException e) {
            if (_acceptNotFound) {
                getResponse().setStatus(Status.SUCCESS_NO_CONTENT, "Unable to find file.");
            } else {
                log.error("", e);
                getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND, "Unable to find file.");
            }
            return new StringRepresentation("");
        }
    }

    @Override
    public void handlePut() {
        handlePost();
    }

    @Override
    public void handlePost() {
        if (hasParent() && hasSecurity()) {
            try {
                final UserI user = getUser();
                if (Permissions.canEdit(user, getSecurity())) {
                    if (!hasProject()) {
                        setProject(getProjectFromRelatedItems(false));
                    }

                    final Object resourceIdentifier;

                    if (_resource == null) {
                        if (getCatalogs().rows().size() > 0) {
                            resourceIdentifier = getCatalogs().getFirstObject();
                        } else {
                            if (getResourceIds() != null && getResourceIds().size() > 0) {
                                resourceIdentifier = getResourceIds().get(0);
                            } else {
                                resourceIdentifier = null;
                            }
                        }
                    } else {
                        resourceIdentifier = _resource.getXnatAbstractresourceId();
                    }

                    final boolean overwrite = isQueryVariableTrue("overwrite");
                    final boolean extract   = isQueryVariableTrue("extract");

                    PersistentWorkflowI wrk = PersistentWorkflowUtils.getWorkflowByEventId(user, getEventId());
                    if (wrk == null && _resource != null && "SNAPSHOTS".equals(_resource.getLabel())) {
                        if (getSecurityItem() instanceof XnatExperimentdata) {
                            Collection<? extends PersistentWorkflowI> workflows = PersistentWorkflowUtils.getOpenWorkflows(user, ((ArchivableItem) getSecurity()).getId());
                            if (workflows != null && workflows.size() == 1) {
                                wrk = (WrkWorkflowdata) CollectionUtils.get(workflows, 0);
                                if (!"xnat_tools/AutoRun.xml".equals(wrk.getPipelineName())) {
                                    wrk = null;
                                }
                            }
                        }
                    }

                    boolean skipUpdateStats = isQueryVariableFalse("update-stats");

                    boolean isNew = false;
                    if (wrk == null && !skipUpdateStats) {
                        isNew = true;
                        wrk = PersistentWorkflowUtils.buildOpenWorkflow(user, getSecurityItem().getItem(), newEventInstance(EventUtils.CATEGORY.DATA, (getAction() != null) ? getAction() : EventUtils.UPLOAD_FILE));
                    }

                    final EventMetaI i;
                    if (wrk == null) {
                        i = EventUtils.DEFAULT_EVENT(user, null);
                    } else {
                        i = wrk.buildEvent();
                    }

                    final UpdateMeta um = new UpdateMeta(i, !(skipUpdateStats));

                    try {
                        final List<FileWriterWrapperI> writers = getFileWriters();
                        if (writers == null || writers.isEmpty()) {
                            final String method = getRequest().getMethod().toString();
                            final long   size   = getRequest().getEntity().getAvailableSize();
                            if (size == 0) {
                                getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "You tried to " + method + " to this service, but didn't provide any data (found request entity size of 0). Please check the format of your service request.");
                            } else {
                                getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "You tried to " + method + " a payload of " + CatalogUtils.formatSize(size) + " to this service, but didn't provide any data. If you think you sent data to upload, you can try to " + method + " with the query-string parameter inbody=true or use multipart/form-data encoding.");
                            }
                            return;
                        }

                        final ResourceModifierA resourceModifier = buildResourceModifier(overwrite, um);
                        final String            projectId        = getProject().getId();
                        if (!_async || StringUtils.isEmpty(_reference)) {
                            final List<String> duplicates = resourceModifier.addFile(writers, resourceIdentifier, getType(), _filePath, buildResourceInfo(um), extract);
                            if (!overwrite && duplicates.size() > 0) {
                                getResponse().setStatus(Status.SUCCESS_OK);
                                getResponse().setEntity(new JSONObjectRepresentation(MediaType.TEXT_HTML, new JSONObject(ImmutableMap.of("duplicates", duplicates))));
                            } else {
                                getResponse().setStatus(Status.SUCCESS_OK);
                                getResponse().setEntity(new StringRepresentation("", MediaType.TEXT_PLAIN));
                            }

                            if (StringUtils.equals(XnatProjectdata.SCHEMA_ELEMENT_NAME, getParent().getXSIType())) {
                                final UserProjectCache cache = XDAT.getContextService().getBeanSafely(UserProjectCache.class);
                                if (cache != null) {
                                    cache.clearProjectCacheEntry(projectId);
                                }
                                XDAT.triggerXftItemEvent(getProject(), XftItemEventI.UPDATE);
                            }
                        } else {
                            assert wrk != null;
                            wrk.setStatus(PersistentWorkflowUtils.QUEUED);
                            WorkflowUtils.save(wrk, wrk.buildEvent());

                            final MoveStoredFileRequest request;
                            if (StringUtils.equals(XnatProjectdata.SCHEMA_ELEMENT_NAME, getParent().getXSIType())) {
                                request = new MoveStoredFileRequest(resourceModifier, resourceIdentifier, writers, user, wrk.getWorkflowId(), _delete, _notifyList, getType(), _filePath, buildResourceInfo(um), extract, projectId);
                            } else {
                                request = new MoveStoredFileRequest(resourceModifier, resourceIdentifier, writers, user, wrk.getWorkflowId(), _delete, _notifyList, getType(), _filePath, buildResourceInfo(um), extract);
                            }
                            XDAT.sendJmsRequest(request);

                            getResponse().setStatus(Status.SUCCESS_OK);
                            getResponse().setEntity(new JSONObjectRepresentation(MediaType.TEXT_HTML, new JSONObject(ImmutableMap.of("workflowId", wrk.getWorkflowId()))));
                        }
                    } catch (Exception e) {
                        log.error("Error occurred while trying to POST file", e);
                        throw e;
                    }

                    if (StringUtils.isEmpty(_reference) && wrk != null && isNew) {
                        WorkflowUtils.complete(wrk, i);
                    }
                }
            } catch (IllegalArgumentException e) { // XNAT-2989
                getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
                log.error("", e);
            } catch (Exception e) {
                getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
                log.error("", e);
            }
        }
    }

    @Override
    public void handleDelete() {
        if (_resource != null && hasParent() && hasSecurity()) {
            try {
                final UserI user = getUser();
                if (Permissions.canDelete(user, getSecurity())) {
                    final XFTItem securityItem = getSecurity().getItem();
                    if (!securityItem.isActive() && !securityItem.isQuarantine()) {
                        //cannot modify it if it isn't active
                        throw new ClientException(Status.CLIENT_ERROR_FORBIDDEN, new Exception());
                    }

                    if (!hasProject()) {
                        initializeProjectFromExperiment();
                    }

                    if (_resource instanceof XnatResourcecatalog) {
                        Collection<CatEntryI> entries = new ArrayList<>();

                        final XnatResourcecatalog catResource = (XnatResourcecatalog) _resource;

                        final String         rootArchivePath = getProject().getRootArchivePath();
                        final File           catFile         = catResource.getCatalogFile(rootArchivePath);
                        final String         parentPath      = catFile.getParent();
                        final CatCatalogBean catalog         = catResource.getCleanCatalog(rootArchivePath, false, null, null);

                        final CatEntryBean entryBean = (CatEntryBean) getEntryByUriOrId(catalog, _filePath);
                        if (entryBean != null) {
                            entries.add(entryBean);
                        }

                        if (entries.size() == 0 && _filePath.endsWith("/")) {
                            entries.addAll(CatalogUtils.getEntriesByFilter(catalog, entry -> entry.getUri().startsWith(_filePath)));
                        }

                        addEntriesByRegex(entries, catalog);

                        final AtomicInteger deletedCount = new AtomicInteger(0);
                        for (final CatEntryI entry : entries) {
                            final File file = new File(parentPath, entry.getUri());
                            if (file.exists()) {
                                PersistentWorkflowI work = WorkflowUtils.getOrCreateWorkflowData(getEventId(), user, securityItem, newEventInstance(EventUtils.CATEGORY.DATA, EventUtils.REMOVE_FILE));
                                EventMetaI          ci   = work.buildEvent();

                                CatalogUtils.removeEntry(catalog, entry);
                                CatalogUtils.writeCatalogToFile(catalog, catFile);

                                if (!isQueryVariableFalse("removeFiles") && !file.delete()) {
                                    log.warn("Error attempting to delete physical file for deleted _resource: " + file.getAbsolutePath());
                                }

                                //if parent folder is empty, then delete folder
                                if (FileUtils.CountFiles(file.getParentFile(), true) == 0) {
                                    FileUtils.DeleteFile(file.getParentFile());
                                }

                                CatalogUtils.populateStats(catResource, rootArchivePath);
                                SaveItemHelper.authorizedSave(catResource, user, false, false, ci);
                                deletedCount.getAndIncrement();

                                WorkflowUtils.complete(work, ci);
                            } else {
                                getResponse().setStatus(_acceptNotFound ? Status.SUCCESS_NO_CONTENT : Status.CLIENT_ERROR_NOT_FOUND, "File missing");
                            }
                        }
                        if (deletedCount.get() > 0 && StringUtils.equals(XnatProjectdata.SCHEMA_ELEMENT_NAME, getParent().getXSIType())) {
                            XDAT.triggerXftItemEvent(XnatProjectdata.SCHEMA_ELEMENT_NAME, getParent().getStringProperty("ID"), XftItemEventI.DELETE);
                        }
                    } else {
                        getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "File is not an instance of XnatResourcecatalog. Delete operation not supported.");
                    }
                } else {
                    getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, "User account doesn't have permission to modify this session.");
                }
            } catch (Exception e) {
                getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e.getMessage());
            }
        }
    }

    @Override
    public List<FileWriterWrapperI> getFileWritersAndLoadParams(final Representation entity, boolean useFileFieldName) throws FileUploadException, ClientException {
        if (StringUtils.isNotEmpty(_reference)) {
            return getReferenceWrapper(_reference);
        } else {
            return super.getFileWritersAndLoadParams(entity, useFileFieldName);
        }
    }

    protected Representation handleMultipleCatalogs(final MediaType mediaType) throws ElementNotFoundException {
        final boolean isZip = isZIPRequest(mediaType);

        File                    file     = null;
        final Map<String, File> fileList = new HashMap<>();

        final XFTTable table = new XFTTable();

        final String[] headers = isZip ? CatalogUtils.FILE_HEADERS_W_FILE : CatalogUtils.FILE_HEADERS;

        // NOTE: zip representations must have URI so we exclude them from locator check.
        final String locatorToken = getLocatorToken(headers, !isZip);
        table.initTable(headers);

        final String          baseURI     = getBaseURI();
        final CatEntryFilterI entryFilter = buildFilter();

        for (final XnatAbstractresource abstractResource : getResources()) {
            final String rootArchivePath = getProject().getRootArchivePath();
            if (abstractResource.getItem().instanceOf("xnat:resourceCatalog")) {
                final boolean             includeRoot     = isQueryVariableTrue("includeRootPath");
                final XnatResourcecatalog resourceCatalog = (XnatResourcecatalog) abstractResource;
                final CatCatalogBean      catalog         = resourceCatalog.getCleanCatalog(rootArchivePath, includeRoot, null, null);
                final String              parentPath      = resourceCatalog.getCatalogFile(rootArchivePath).getParent();

                if (catalog != null) {
                    if (StringUtils.isBlank(_filePath)) {
                        final String uriPath = resourceCatalog.getBaseURI() != null ? resourceCatalog.getBaseURI() + "/files" : baseURI + "/resources/" + resourceCatalog.getXnatAbstractresourceId() + "/files";
                        table.rows().addAll(CatalogUtils.getEntryDetails(catalog, parentPath, uriPath, resourceCatalog, isZip || _index != null, entryFilter, getProject(), locatorToken));
                    } else {
                        final ArrayList<CatEntryI> entries = new ArrayList<>();

                        final CatEntryBean entryByURI = (CatEntryBean) CatalogUtils.getEntryByURI(catalog, _filePath);
                        if (entryByURI != null) {
                            entries.add(entryByURI);
                        }
                        if (entries.isEmpty()) {
                            final CatEntryBean entryById = (CatEntryBean) CatalogUtils.getEntryById(catalog, _filePath);
                            if (entryById != null) {
                                entries.add(entryById);
                            }
                        }
                        if (entries.isEmpty() && _filePath.endsWith("/")) {
                            //recursion is on by default
                            final boolean recursive = !(isQueryVariableFalse("recursive"));
                            final String  dir       = _filePath;
                            final CatalogUtils.CatEntryFilterI folderFilter = entry -> {
                                if (entry.getUri().startsWith(dir)) {
                                    if (recursive || StringUtils.contains(entry.getUri().substring(dir.length() + 1), "/")) {
                                        return entryFilter == null || entryFilter.accept(entry);
                                    }
                                }
                                return false;
                            };
                            entries.addAll(CatalogUtils.getEntriesByFilter(catalog, folderFilter));
                        }

                        addEntriesByRegex(entries, catalog);

                        if (entries.size() == 1) {
                            if (FileUtils.IsAbsolutePath(entries.get(0).getUri())) {
                                file = new File(entries.get(0).getUri());
                            } else {
                                file = new File(parentPath, entries.get(0).getUri());
                            }

                            if (file.exists()) {
                                break;
                            }

                        } else {

                            for (CatEntryI entry : entries) {
                                if (FileUtils.IsAbsolutePath(entry.getUri())) {
                                    file = new File(entry.getUri());
                                } else {
                                    file = new File(parentPath, entry.getUri());
                                }

                                if (file.exists()) {
                                    fileList.put(entry.getUri(), file);
                                }

                            }
                            break;
                        }
                    }
                }
            } else {
                //not catalog
                if (entryFilter == null) {
                    ArrayList<File> files = abstractResource.getCorrespondingFiles(rootArchivePath);
                    if (files != null && files.size() > 0) {
                        final boolean checksums = XDAT.getSiteConfigPreferences().getChecksums();
                        for (final File subFile : files) {
                            final List<Object> row = Lists.newArrayList();
                            row.add(subFile.getName());
                            row.add(subFile.length());
                            if (locatorToken.equalsIgnoreCase("URI")) {
                                row.add(abstractResource.getBaseURI() != null ? abstractResource.getBaseURI() + "/files/" + subFile.getName() : baseURI + "/resources/" + abstractResource.getXnatAbstractresourceId() + "/files/" + subFile.getName());
                            } else if (locatorToken.equalsIgnoreCase("absolutePath")) {
                                row.add(subFile.getAbsolutePath());
                            } else if (locatorToken.equalsIgnoreCase("projectPath")) {
                                row.add(subFile.getAbsolutePath().substring(rootArchivePath.substring(0, rootArchivePath.lastIndexOf(getProject().getId())).length()));
                            }
                            row.add(abstractResource.getLabel());
                            row.add(abstractResource.getTagString());
                            row.add(abstractResource.getFormat());
                            row.add(abstractResource.getContent());
                            row.add(abstractResource.getXnatAbstractresourceId());
                            if (isZip) {
                                row.add(subFile);
                            }
                            row.add(checksums ? CatalogUtils.getHash(subFile) : "");
                            table.rows().add(row.toArray());
                        }
                    }
                }
            }
        }

        final String downloadName = hasSecurity() ? ((ArchivableItem) getSecurity()).getArchiveDirectoryName() : getSessionMaps().get(Integer.toString(0));
        final String extension;
        if (mediaType.equals(MediaType.APPLICATION_ZIP)) {
            extension = ".zip";
        } else if (mediaType.equals(MediaType.APPLICATION_GNU_TAR)) {
            extension = ".tar.gz";
        } else if (mediaType.equals(MediaType.APPLICATION_TAR)) {
            extension = ".tar";
        } else {
            extension = "";
        }
        setContentDisposition(downloadName + extension);

        if (StringUtils.isEmpty(_filePath) && _index == null) {
            Hashtable<String, Object> params = new Hashtable<>();
            params.put("title", "Files");

            Map<String, Map<String, String>> cp = new Hashtable<>();
            cp.put("URI", new Hashtable<>());
            String rootPath = getRequest().getRootRef().getPath();
            if (rootPath.endsWith("/data")) {
                rootPath = rootPath.substring(0, rootPath.indexOf("/data"));
            }
            if (rootPath.endsWith("/REST")) {
                rootPath = rootPath.substring(0, rootPath.indexOf("/REST"));
            }
            cp.get("URI").put("serverRoot", rootPath);

            return representTable(table, mediaType, params, cp, getSessionMaps());
        } else {
            if (_index != null && table.rows().size() > _index) {
                file = (File) table.rows().get(_index)[8];
            }

            if (file == null || !file.exists()) {
                getResponse().setStatus(_acceptNotFound ? Status.SUCCESS_NO_CONTENT : Status.CLIENT_ERROR_NOT_FOUND, "Unable to find file.");
                return null;
            }

            final String name = file.getName();

            //return file
            if (fileList.size() > 0) {
                if ((mediaType.equals(MediaType.APPLICATION_ZIP) && !name.toLowerCase().endsWith(".zip"))
                    || (mediaType.equals(MediaType.APPLICATION_GNU_TAR) && !name.toLowerCase().endsWith(".tar.gz"))
                    || (mediaType.equals(MediaType.APPLICATION_TAR) && !name.toLowerCase().endsWith(".tar"))) {
                    final ZipRepresentation representation;
                    representation = getZipRepresentation(mediaType);
                    if (representation == null) {
                        return null;
                    }
                    for (String fn : fileList.keySet()) {
                        representation.addEntry(fn, fileList.get(fn));
                    }
                    return representation;
                }
            } else {
                if ((mediaType.equals(MediaType.APPLICATION_ZIP) && !name.toLowerCase().endsWith(".zip"))
                    || (mediaType.equals(MediaType.APPLICATION_GNU_TAR) && !name.toLowerCase().endsWith(".tar.gz"))
                    || (mediaType.equals(MediaType.APPLICATION_TAR) && !name.toLowerCase().endsWith(".tar"))) {
                    final ZipRepresentation representation = getZipRepresentation(mediaType);
                    if (representation == null) {
                        return null;
                    }
                    representation.addEntry(name, file);
                    return representation;
                } else {
                    return getFileRepresentation(file, mediaType);
                }
            }
        }
        return null;
    }

    protected Representation handleSingleCatalog(MediaType mediaType) throws ElementNotFoundException {
        XFTTable table = new XFTTable();

        final String locator = getLocatorToken(CatalogUtils.FILE_HEADERS);
        table.initTable(CatalogUtils.FILE_HEADERS);

        final CatalogUtils.CatEntryFilterI entryFilter = buildFilter();

        final String rootArchivePath = getProject().getRootArchivePath();
        if (_history) {
            final String parentPath = ((XnatResourcecatalog) _resource).getCatalogFile(rootArchivePath).getParent();
            try {
                final ArchiveFolder folder = getStorageService().getArchiveFolder(Paths.get(parentPath));

                // If there's no file path, this is a top-level thing.
                if (StringUtils.isEmpty(_filePath) && _index == null) {
                    final List<EntityHistory<ArchiveFolder>> history = getStorageService().getHistory(folder);
                    return new StringRepresentation(getSerializer().toJson(history), mediaType);
                } else {
                    // Get the specified file.
                    final Optional<ArchiveEntry> optional = folder.getEntries().stream().filter(entry -> StringUtils.equals(_filePath, entry.getPath())).findFirst();
                    if (optional.isPresent()) {
                        final ArchiveEntry                      entry   = optional.get();
                        final List<EntityHistory<ArchiveEntry>> history = getStorageService().getHistory(entry);
                        return new StringRepresentation(getSerializer().toJson(history), mediaType);
                    } else {
                        throw new NotFoundException("");
                    }
                }
            } catch (NotFoundException e) {
                log.error("Couldn't find an archive folder corresponding to the resource catalog at {}. Maybe history isn't tracked for this resource?", parentPath);
            } catch (IOException e) {
                log.error("An error occurred trying to serialize");
            }

        } else if (_resource.getItem().instanceOf("xnat:resourceCatalog")) {
            final XnatResourcecatalog resourceCatalog = (XnatResourcecatalog) _resource;
            final CatCatalogBean      catalog         = resourceCatalog.getCleanCatalog(rootArchivePath, isQueryVariableTrue("includeRootPath"), null, null);
            final String              parentPath      = resourceCatalog.getCatalogFile(rootArchivePath).getParent();

            if (StringUtils.isEmpty(_filePath) && _index == null) {
                if (catalog != null) {
                    table.rows().addAll(CatalogUtils.getEntryDetails(catalog, parentPath, getBaseURI() + "/resources/" + resourceCatalog.getXnatAbstractresourceId() + "/files", resourceCatalog, false, entryFilter, getProject(), locator));
                }
            } else {
                String zipEntry = null;

                CatEntryI entry;
                if (_index != null) {
                    entry = CatalogUtils.getEntryByFilter(catalog, new CatEntryFilterI() {
                        private int count = 0;
                        private CatEntryFilterI filter = entryFilter;

                        public boolean accept(final CatEntryI entry) {
                            if (filter.accept(entry)) {
                                return _index.equals(count++);
                            }

                            return false;
                        }

                    });
                } else {
                    final String           lowercase = _filePath.toLowerCase();
                    final String           entryPath;
                    final Optional<String> first     = Arrays.stream(XDAT.getSiteConfigPreferences().getZipExtensionsAsArray()).filter(extension -> lowercase.contains("." + extension + "!") || lowercase.contains("." + extension + "/")).findFirst();
                    if (first.isPresent()) {
                        final String extension = first.get();
                        zipEntry = _filePath.substring(lowercase.indexOf(extension) + extension.length());
                        entryPath = _filePath.substring(0, lowercase.indexOf(extension) + extension.length());
                    } else {
                        entryPath = _filePath;
                    }
                    entry = getEntryByUriOrId(catalog, entryPath);
                }

                if (entry == null && _filePath.endsWith("/")) {
                    //if no exact matches, look for a folder
                    final String baseURI = getBaseURI();

                    //recursion is on by default
                    final boolean recursive = !(this.isQueryVariableFalse("recursive"));
                    final String  dir       = _filePath;
                    final CatalogUtils.CatEntryFilterI folderFilter = entry1 -> {
                        if (entry1.getUri().startsWith(dir)) {
                            if (recursive || StringUtils.contains(entry1.getUri().substring(dir.length() + 1), "/")) {
                                return (entryFilter == null || entryFilter.accept(entry1));
                            }
                        }
                        return false;
                    };


                    //If there are no matching entries, I'm not sure if this should throw a 404, or return an empty list.
                    if (_filePath.endsWith("/")) {
                        table.rows().addAll(CatalogUtils.getEntryDetails(catalog, parentPath, baseURI + "/resources/" + resourceCatalog.getXnatAbstractresourceId() + "/files", resourceCatalog, false, folderFilter, getProject(), locator));
                    } else {
                        getResponse().setStatus(_acceptNotFound ? Status.SUCCESS_NO_CONTENT : Status.CLIENT_ERROR_NOT_FOUND, "Unable to find catalog entry for given uri.");
                        return new StringRepresentation("");
                    }
                } else if (entry == null) {
                    getResponse().setStatus(_acceptNotFound ? Status.SUCCESS_NO_CONTENT : Status.CLIENT_ERROR_NOT_FOUND, "Unable to find catalog entry for given uri.");
                    return new StringRepresentation("");
                } else {
                    final File file;
                    if (FileUtils.IsAbsolutePath(entry.getUri())) {
                        file = new File(entry.getUri());
                    } else {
                        file = new File(parentPath, entry.getUri());
                    }

                    if (file.exists()) {
                        String fName;
                        if (zipEntry == null) {
                            fName = file.getName().toLowerCase();
                        } else {
                            fName = zipEntry.toLowerCase();
                        }

                        if (mediaType.equals(MediaType.IMAGE_JPEG) && Dcm2Jpg.isDicom(file)) {
                            try {
                                return new InputRepresentation(new ByteArrayInputStream(Dcm2Jpg.convert(file)), mediaType);
                            } catch (IOException e) {
                                getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Unable to convert this file to jpeg : " + e.getMessage());
                                return new StringRepresentation("");
                            }
                        }

                        try {
                            // If the user is requesting a file within the zip archive
                            if (zipEntry != null) {
                                // Get the zip entry requested
                                ZipFile  zF = new ZipFile(file);
                                ZipEntry zE = zF.getEntry(URLDecoder.decode(zipEntry, "UTF-8"));
                                if (zE == null) {
                                    getResponse().setStatus(_acceptNotFound ? Status.SUCCESS_NO_CONTENT : Status.CLIENT_ERROR_NOT_FOUND, "Unable to find file.");
                                    return new StringRepresentation("");
                                } else { // Return the requested zip entry
                                    return new InputRepresentation(zF.getInputStream(zE), buildMediaType(mediaType, fName));
                                }
                                // If the user is requesting a list of the contents within the zip file
                            } else if (_listContents && isFileZipArchive(fName)) {
                                // Get the contents of the zip file
                                ZipFile                         zF      = new ZipFile(file);
                                Enumeration<? extends ZipEntry> entries = zF.entries();

                                // Create a new XFTTable with File Name and Size columns
                                XFTTable t = new XFTTable();
                                t.initTable(new String[]{"File Name", "Size"});

                                // Populate table rows and add the row to the table
                                while (entries.hasMoreElements()) {
                                    ZipEntry zE = entries.nextElement();
                                    t.rows().add(new Object[]{zE.getName(), zE.getSize()});
                                }
                                zF.close();

                                // Set the table, if t has rows
                                if (t.rows().size() != 0) {
                                    table = t;  // table gets passed into representTable() below
                                }
                            } else {
                                // Return the requested file
                                return getFileRepresentation(file, buildMediaType(mediaType, fName));
                            }
                        } catch (ZipException e) {
                            getResponse().setStatus(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE, e.getMessage());
                            return new StringRepresentation("");
                        } catch (IOException e) {
                            getResponse().setStatus(_acceptNotFound ? Status.SUCCESS_NO_CONTENT : Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
                            return new StringRepresentation("");
                        }

                    } else { // If file does not exist
                        getResponse().setStatus(_acceptNotFound ? Status.SUCCESS_NO_CONTENT : Status.CLIENT_ERROR_NOT_FOUND, "Unable to find file.");
                        return new StringRepresentation("");
                    }
                }
            }
        } else {
            if (_filePath == null || _filePath.equals("")) {
                String baseURI = getBaseURI();
                if (entryFilter == null) {
                    ArrayList<File> files = _resource.getCorrespondingFiles(rootArchivePath);
                    for (File subFile : files) {
                        Object[] row = new Object[13];
                        row[0] = (subFile.getName());
                        row[1] = (subFile.length());
                        if (locator.equalsIgnoreCase("URI")) {
                            row[2] = baseURI + "/resources/" + _resource.getXnatAbstractresourceId() + "/files/" + subFile.getName();
                        } else if (locator.equalsIgnoreCase("absolutePath")) {
                            row[2] = subFile.getAbsolutePath();
                        } else {
                            row[2] = subFile.getAbsolutePath().substring(rootArchivePath.substring(0, rootArchivePath.lastIndexOf(getProject().getId())).length());
                        }
                        row[3] = _resource.getLabel();
                        row[4] = _resource.getTagString();
                        row[5] = _resource.getFormat();
                        row[6] = _resource.getContent();
                        row[7] = _resource.getXnatAbstractresourceId();
                        table.rows().add(row);
                    }
                }
            } else {
                final Optional<File> file = _resource.getCorrespondingFiles(rootArchivePath).stream().filter(candidate -> StringUtils.equals(_filePath, candidate.getName())).findFirst();
                if (!file.isPresent()) {
                    getResponse().setStatus(_acceptNotFound ? Status.SUCCESS_NO_CONTENT : Status.CLIENT_ERROR_NOT_FOUND, "Unable to find file.");
                    return new StringRepresentation("");
                }
                return getFileRepresentation(file.get(), mediaType);
            }
        }

        Hashtable<String, Object> params = new Hashtable<>();
        params.put("title", "Files");

        Map<String, Map<String, String>> cp = new Hashtable<>();
        cp.put("URI", new Hashtable<>());
        cp.get("URI").put("serverRoot", getContextPath());

        return representTable(table, mediaType, params, cp, getSessionMaps());
    }

    private Representation representTable(XFTTable table, MediaType mediaType, Hashtable<String, Object> params, Map<String, Map<String, String>> cp, Map<String, String> session_mapping) {
        if (mediaType.equals(SecureResource.APPLICATION_XCAT)) {
            //"Name","Size","URI","collection","file_tags","file_format","file_content","cat_ID"
            CatCatalogBean cat = new CatCatalogBean();

            String server = TurbineUtils.GetFullServerPath(getHttpServletRequest());
            if (server.endsWith("/")) {
                server = server.substring(0, server.length() - 1);
            }

            final int uriIndex  = table.getColumnIndex("URI");
            final int sizeIndex = table.getColumnIndex("Size");

            final int collectionIndex = table.getColumnIndex("collection");
            final int cat_IDIndex     = table.getColumnIndex("cat_ID");

            Map<String, String> valuesToReplace = getReMaps();

            for (Object[] row : table.rows()) {

                CatEntryBean entry = new CatEntryBean();

                String uri      = (String) row[uriIndex];
                String relative = RestFileUtils.getRelativePath(uri, session_mapping);

                entry.setUri(server + uri);

                relative = relative.replace('\\', '/');

                relative = RestFileUtils.replaceResourceLabel(relative, row[cat_IDIndex], (String) row[collectionIndex]);

                for (Map.Entry<String, String> e : valuesToReplace.entrySet()) {
                    relative = RestFileUtils.replaceInPath(relative, e.getKey(), e.getValue());
                }

                entry.setCachepath(relative);

                CatEntryMetafieldBean meta = new CatEntryMetafieldBean();
                meta.setMetafield(relative);
                meta.setName("RELATIVE_PATH");
                entry.addMetafields_metafield(meta);

                meta = new CatEntryMetafieldBean();
                meta.setMetafield(row[sizeIndex].toString());
                meta.setName("SIZE");
                entry.addMetafields_metafield(meta);

                cat.addEntries_entry(entry);
            }

            setContentDisposition("files.xcat", false);

            return new BeanRepresentation(cat, mediaType, false);
        } else if (isZIPRequest(mediaType)) {
            ZipRepresentation rep;
            try {
                rep = new ZipRepresentation(mediaType, getSessionIds(), identifyCompression(null));
            } catch (ActionException e) {
                log.error("", e);
                setResponseStatus(e);
                return null;
            }

            final int uriIndex  = table.getColumnIndex("URI");
            final int fileIndex = table.getColumnIndex("file");

            final int collectionIndex = table.getColumnIndex("collection");
            final int cat_IDIndex     = table.getColumnIndex("cat_ID");

            final Map<String, String> valuesToReplace;
            if (_structure.equalsIgnoreCase("legacy") || _structure.equalsIgnoreCase("simplified")) {
                valuesToReplace = new Hashtable<>();
            } else {
                valuesToReplace = getReMaps();
            }

            //TODO: This should all be rewritten.  The implementation of the path relativization should be injectable, particularly to support other possible structures.
            for (final Object[] row : table.rows()) {
                final String uri   = (String) row[uriIndex];
                final File   child = (File) row[fileIndex];

                if (child != null && child.exists()) {
                    final String pathForZip;
                    if (_structure.equalsIgnoreCase("improved")) {
                        pathForZip = getImprovedPath(uri, row[cat_IDIndex], mediaType);
                    } else if (_structure.equalsIgnoreCase("legacy")) {
                        pathForZip = child.getAbsolutePath();
                    } else {
                        pathForZip = uri;
                    }

                    final String relative;
                    switch (_structure) {
                        case "improved":
                            relative = pathForZip;
                            break;
                        case "simplified":
                            relative = RestFileUtils.buildRelativePath(pathForZip, session_mapping, valuesToReplace, row[cat_IDIndex], (String) row[collectionIndex]).replace("/resources", "").replace("/files", "");
                            break;
                        default:
                            relative = RestFileUtils.buildRelativePath(pathForZip, session_mapping, valuesToReplace, row[cat_IDIndex], (String) row[collectionIndex]);
                    }

                    rep.addEntry(relative, child);
                }
            }

            if (rep.getEntryCount() == 0) {
                getResponse().setStatus(_acceptNotFound ? Status.SUCCESS_NO_CONTENT : Status.CLIENT_ERROR_NOT_FOUND);
                return null;
            }

            return rep;
        } else {
            return super.representTable(table, mediaType, params, cp);
        }
    }

    private CatEntryFilterI buildFilter() {
        if ((_fileContent != null && _fileContent.length > 0) || (_fileFormat != null && _fileFormat.length > 0)) {
            return entry -> {
                if (_fileFormat != null && _fileFormat.length > 0) {
                    if (entry.getFormat() == null) {
                        if (!ArrayUtils.contains(_fileFormat, "NULL")) {
                            return false;
                        }
                    } else {
                        if (!ArrayUtils.contains(_fileFormat, entry.getFormat())) {
                            return false;
                        }
                    }
                }

                if (_fileContent != null && _fileContent.length > 0) {
                    if (entry.getContent() == null) {
                        return ArrayUtils.contains(_fileContent, "NULL");
                    } else {
                        return ArrayUtils.contains(_fileContent, entry.getContent());
                    }
                }

                return true;
            };
        }

        return null;
    }

    private List<FileWriterWrapperI> getReferenceWrapper(String value) throws FileUploadException {
        File file = new File(value);
        if (!file.exists()) {
            throw new FileUploadException("The resource referenced does not exist: " + value);
        }
        List<FileWriterWrapperI> files = new ArrayList<>();
        if (file.isFile()) {
            files.add(new StoredFile(file, true, "", true));
        } else {
            // TODO: This is a simple recursive find of all files underneath the specified root. It'd be nice to support manifest files containing ant path specifiers or something similar to that.
            Collection found = org.apache.commons.io.FileUtils.listFiles(file, FileFileFilter.FILE, DirectoryFileFilter.DIRECTORY);
            for (Object foundObject : found) {
                if (!(foundObject instanceof File)) {

                    throw new RuntimeException("Something went really wrong");
                }
                File foundFile = (File) foundObject;
                if (foundFile.isFile()) {
                    files.add(new StoredFile(foundFile, true, file.toURI().relativize(foundFile.getParentFile().toURI()).getPath(), true));
                }
            }
        }
        return files;
    }

    private String getImprovedPath(final String fileUri, final Object catNumber, final MediaType mediaType) {
        final boolean        isTar = mediaType.equals(MediaType.APPLICATION_TAR) || mediaType.equals(MediaType.APPLICATION_GNU_TAR);
        final StringBuilder  root  = new StringBuilder();
        final List<Object[]> rows  = getCatalogs().rows();
        for (final Object[] row : rows) {         // iterate through the rows of the catalog to find
            if (row[0].equals(catNumber)) { // the catalog entry matching the current object
                root.append(row[3].toString()).append("/"); // resource type, e.g. scans, resources, assessors
                if (row[4] != null && !row[4].equals("")) { // folder name, usually scan number_scan type
                    root.append(row[4].toString());
                    // extend the folder name with scan type as long as it's not a tar (tar's have a 100 character limit)
                    if (!isTar && row[5] != null && !row[5].equals("")) {
                        // session types can have special characters that interfere with file-path creation, so those should be replaced with underscores
                        root.append("_").append(row[5].toString().replaceAll("[\\/\\\\:\\*\\?\"<>\\|]", "_"));
                    }
                    root.append("/");
                }
                if (row[1] != null && !row[1].equals("")) {
                    root.append(row[1].toString()).append("/"); // data subfolder, most commonly DICOM
                } else {
                    root.append(row[0].toString()).append("/"); // if no subfolder name, use resource id
                }
            }
        }
        return root.append(fileUri.substring(fileUri.lastIndexOf("/files/") + 7)).toString();
    }

    private void addEntriesByRegex(final Collection<CatEntryI> entries, final CatCatalogBean cat) {
        if (entries.isEmpty() && _filePath.endsWith("*")) {
            final StringBuilder regex     = new StringBuilder(_filePath);
            final int           lastIndex = _filePath.lastIndexOf("*");
            regex.replace(lastIndex, lastIndex + 1, ".*");
            entries.addAll(CatalogUtils.getEntriesByRegex(cat, regex.toString()));
        }
    }

    private CatEntryI getEntryByUriOrId(final CatCatalogBean catalog, final String entryPath) {
        return ObjectUtils.defaultIfNull(CatalogUtils.getEntryByURI(catalog, entryPath), CatalogUtils.getEntryById(catalog, entryPath));
    }

    /**
     * Function determines if the given file is a zip archive by
     * checking whether the fileName contains a zip extension
     *
     * @param f - the file name
     *
     * @return - true / false is the file a zip file?
     */
    private boolean isFileZipArchive(String f) {
        for (String s : XDAT.getSiteConfigPreferences().getZipExtensionsAsArray()) {
            if (f.contains(s)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private ZipRepresentation getZipRepresentation(final MediaType mediaType) {
        try {
            return new ZipRepresentation(mediaType, ((ArchivableItem) getSecurity()).getArchiveDirectoryName(), identifyCompression(null));
        } catch (ActionException e) {
            log.error("", e);
            setResponseStatus(e);
            return null;
        }
    }

    private Map<String, String> getReMaps() {
        return RestFileUtils.getReMaps(getScans(), getReconstructions());
    }

    private Map<String, String> getSessionMaps() {
        // Check if the session is an assessor to an "assessed" session
        if (hasAssesseds()) {
            // Check if the session containing the assessor has an "ASSESSORS" directory.
            // This signifies that the directory structure is based on a "modern" version of XNAT.
            if (new File(getAssesseds().get(0).getSessionDir(), "ASSESSORS").isDirectory() && hasExperiments()) {
                return getExperiments().stream().collect(Collectors.toMap(XnatExperimentdata::getId, XnatExperimentdata::getArchiveDirectoryName));
            } else {
                //IOWA customization: to include project and subject in path
                final boolean projectIncludedInPath = isQueryVariableTrue("projectIncludedInPath");
                final boolean subjectIncludedInPath = isQueryVariableTrue("subjectIncludedInPath");
                return getAssesseds().stream().collect(Collectors.toMap(XnatExperimentdata::getId, session -> getPath(session, projectIncludedInPath, subjectIncludedInPath)));
            }
        } else if (hasExperiments()) {
            return getExperiments().stream().collect(Collectors.toMap(XnatExperimentdata::getId, XnatExperimentdata::getArchiveDirectoryName));
        } else if (hasSubject()) {
            return ImmutableMap.of(getSubject().getId(), getSubject().getArchiveDirectoryName());
        } else if (hasProject()) {
            final String id = getProject().getId();
            return ImmutableMap.of(id, id);
        }

        return Collections.emptyMap();
    }

    private String getPath(final XnatExperimentdata session, final boolean projectIncludedInPath, final boolean subjectIncludedInPath) {
        final List<String> paths = new ArrayList<>();
        if (projectIncludedInPath) {
            paths.add(session.getProject());
        }
        if (subjectIncludedInPath && session instanceof XnatImagesessiondata) {
            final XnatSubjectdata subject = XnatSubjectdata.getXnatSubjectdatasById(((XnatImagesessiondata) session).getSubjectId(), getUser(), false);
            paths.add(subject.getLabel());
        }
        paths.add(session.getArchiveDirectoryName());
        return StringUtils.join(paths, "/");
    }

    private List<String> getSessionIds() {
        if (hasAssesseds()) {
            return getAssesseds().stream().map(XnatExperimentdata::getArchiveDirectoryName).collect(Collectors.toList());
        }
        if (hasExperiments()) {
            return getExperiments().stream().map(XnatExperimentdata::getArchiveDirectoryName).collect(Collectors.toList());
        }
        if (hasSubject()) {
            return Collections.singletonList(getSubject().getArchiveDirectoryName());
        }
        if (hasProject()) {
            return Collections.singletonList(getProject().getId());
        }
        return Collections.emptyList();
    }

    private FileRepresentation getFileRepresentation(File f, MediaType mt) {
        return setFileRepresentation(f, mt);
    }

    private FileRepresentation setFileRepresentation(File f, MediaType mt) {
        setResponseHeader("Cache-Control", "must-revalidate");
        return representFile(f, mt);
    }

    private String getLocatorToken(@SuppressWarnings("SameParameterValue") final String[] headers) {
        return getLocatorToken(headers, true);
    }

    private String getLocatorToken(final String[] headers, final boolean checkLocatorToken) {
        if (checkLocatorToken && StringUtils.equalsIgnoreCase(_locator, "absolutePath")) {
            return headers[ArrayUtils.indexOf(headers, "URI")] = "absolutePath";
        }
        if (checkLocatorToken && StringUtils.equalsIgnoreCase(_locator, "projectPath")) {
            return headers[ArrayUtils.indexOf(headers, "URI")] = "projectPath";
        }
        return "URI";
    }

    private final String               _filePath;
    private final XnatAbstractresource _resource;

    // Query variables
    private final String   _reference;
    private final boolean  _acceptNotFound;
    private final boolean  _delete;
    private final boolean  _async;
    private final String[] _notifyList;
    private final String   _structure;
    private final String[] _fileContent;
    private final String[] _fileFormat;
    private final String   _locator;
    private final Integer  _index;
    private final boolean  _listContents;
    private final boolean  _history;
}
