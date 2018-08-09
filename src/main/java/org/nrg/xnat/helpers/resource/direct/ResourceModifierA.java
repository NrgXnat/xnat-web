/*
 * web: org.nrg.xnat.helpers.resource.direct.ResourceModifierA
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.helpers.resource.direct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.bean.CatCatalogBean;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.XnatResource;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.om.base.BaseXnatExperimentdata.UnknownPrimaryProjectException;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.DateUtils;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xnat.exceptions.InvalidArchiveStructure;
import org.nrg.xnat.helpers.resource.XnatResourceInfo;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.nrg.xnat.services.archive.CatalogService;
import org.nrg.xnat.utils.CatalogUtils;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author timo
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class ResourceModifierA implements Serializable {
    final boolean    overwrite;
    final UserI      user;
    final EventMetaI ci;

    public ResourceModifierA(final boolean overwrite, final UserI user, final EventMetaI ci) {
        this.overwrite = overwrite;
        this.user = user;
        this.ci = ci;
    }

    public static class UpdateMeta implements EventMetaI, Serializable {
        private static final long       serialVersionUID = 42L;
        final                EventMetaI i;
        final                boolean    update;

        public UpdateMeta(EventMetaI i, boolean update) {
            this.i = i;
            this.update = update;
        }

        @Override
        public String getMessage() {
            return i.getMessage();
        }

        @Override
        public Date getEventDate() {
            return i.getEventDate();
        }

        @Override
        public String getTimestamp() {
            return i.getTimestamp();
        }

        @Override
        public UserI getUser() {
            return i.getUser();
        }

        @Override
        public Number getEventId() {
            return i.getEventId();
        }

        public boolean getUpdate() {
            return update;
        }

    }

    public abstract XnatProjectdata getProject();

    public abstract boolean addResource(final XnatResource resource, final String type, final UserI user) throws Exception;

    public String getRootPath() {
        return getProject().getRootArchivePath();
    }

    public List<String> addFile(final List<? extends FileWriterWrapperI> writers, final Object resourceIdentifier, final String type, final String filePath, final XnatResourceInfo info, final boolean extract) throws Exception {
        if (ObjectUtils.isEmpty(writers)) {
            return Collections.emptyList();
        }

        final Pair<XnatResourcecatalog, Boolean> resourceCatalogPackage = createOrGetResourceCatalog(resourceIdentifier, type, info);
        final XnatResourcecatalog                resourceCatalog        = resourceCatalogPackage.getLeft();

        try {
            return new ArrayList<>(getCatalogService().storeCatalogEntry(writers, getProject(), resourceCatalog, filePath, info, extract, overwrite, ci));
        } finally {
            CatalogUtils.populateStats(resourceCatalog, null);
            if (resourceCatalogPackage.getRight()) {
                addResource(resourceCatalog, type, user);
            } else {
                if ((!(ci instanceof UpdateMeta)) || ((UpdateMeta) ci).getUpdate()) {
                    SaveItemHelper.authorizedSave(resourceCatalog, user, false, false, ci);
                }
            }
        }
    }

    private Pair<XnatResourcecatalog, Boolean> createOrGetResourceCatalog(final Object resourceIdentifier, final String type, final XnatResourceInfo info) throws Exception {
        final XnatAbstractresource abstractResource = (XnatAbstractresource) getResourceByIdentifier(resourceIdentifier, type);

        if (abstractResource != null) {
            if (abstractResource instanceof XnatResourcecatalog) {
                return ImmutablePair.of((XnatResourcecatalog) abstractResource, false);
            }
            throw new Exception("Conflict: A resource with the identifier " + resourceIdentifier.toString() + " already exists, but it's not a resource catalog.");
        }

        final String label              = Objects.isNull(resourceIdentifier) ? null : resourceIdentifier.toString();
        final String catalogId          = StringUtils.defaultIfBlank(label, getDefaultUID());
        final Path   parent             = Paths.get(buildDestinationPath(), catalogId);
        final Path   target             = parent.resolve(catalogId + "_catalog.xml");
        final String resourceCatalogUri = target.toAbsolutePath().toString();

        final XnatResourcecatalog resourceCatalog = getCatalogService().createAndInsertResourceCatalog(user, parent.toString(), label, info.getDescription(),info.getFormat(), info.getContent(), info.getTags());

        final CatCatalogBean catalog = new CatCatalogBean();
        catalog.setId(catalogId);
        CatalogUtils.writeCatalogToFile(catalog, target.toFile());
        resourceCatalog.setUri(resourceCatalogUri);

        return ImmutablePair.of(resourceCatalog, true);
    }

    public XnatAbstractresourceI getResourceByIdentifier(final Object resourceIdentifier, final String type) {
        if (resourceIdentifier == null) {
            return null;
        }

        XnatAbstractresourceI resource = null;

        if (resourceIdentifier instanceof Integer) {
            resource = getResourceById((Integer) resourceIdentifier, type);
        }

        if (resource != null) {
            return resource;
        }

        resource = getResourceByLabel(resourceIdentifier.toString(), type);

        if (resource != null) {
            return resource;
        }

        if (StringUtils.isNumeric(resourceIdentifier.toString())) {
            resource = getResourceById(Integer.valueOf(resourceIdentifier.toString()), type);
        }

        return resource;
    }

    @SuppressWarnings("WeakerAccess")
    protected static String getDefaultUID() {
        return DateUtils.format(Calendar.getInstance().getTime(), "yyyyMMdd_HHmmss");
    }

    protected abstract String buildDestinationPath() throws InvalidArchiveStructure, UnknownPrimaryProjectException;

    protected abstract XnatAbstractresourceI getResourceById(final Integer resourceId, final String type);

    protected abstract XnatAbstractresourceI getResourceByLabel(final String resourceLabel, final String type);

    protected CatalogService getCatalogService() {
        if (_catalogService == null) {
            synchronized (CATALOG_SERVICE_MUTEX) {
                _catalogService = XDAT.getContextService().getBean(CatalogService.class);
            }
        }
        return _catalogService;
    }

    private static final Object CATALOG_SERVICE_MUTEX = new Object();

    private static CatalogService _catalogService;
}
