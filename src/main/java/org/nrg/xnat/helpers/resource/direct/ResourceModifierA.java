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
import org.nrg.xnat.utils.CatalogUtils;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author timo
 */
public abstract class ResourceModifierA implements Serializable {
    final boolean overwrite;
    final UserI user;
    final EventMetaI ci;

    public ResourceModifierA(final boolean overwrite, final UserI user, final EventMetaI ci) {
        this.overwrite = overwrite;
        this.user = user;
        this.ci = ci;
    }

    public static class UpdateMeta implements EventMetaI, Serializable {
        private static final long serialVersionUID = 42L;
        final EventMetaI i;
        final boolean update;
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

    public List<String> addFile(final List<? extends FileWriterWrapperI> writers, final Object resourceIdentifier, final String type, final String filepath, final XnatResourceInfo info, final boolean extract) throws Exception {
        if (writers == null || writers.size() == 0) {
            return Collections.emptyList();
        }

        XnatAbstractresource abst = (XnatAbstractresource) getResourceByIdentifier(resourceIdentifier, type);

        boolean isNew = false;
        if (abst == null) {
            isNew = true;
            //new resource
            abst = new XnatResourcecatalog(user);

            if (resourceIdentifier != null) {
                abst.setLabel(resourceIdentifier.toString());
            }
            abst.setFileCount(0);
            abst.setFileSize(0);

            createCatalog((XnatResourcecatalog) abst, info);
        } else {
            if (!(abst instanceof XnatResourcecatalog)) {
                throw new Exception("Conflict:Non-catalog resource already exits.");
            }
        }

        try {
            return new ArrayList<>(CatalogUtils.storeCatalogEntry(writers, filepath, (XnatResourcecatalog) abst, getProject(), extract, info, overwrite, ci));
        } finally {
            CatalogUtils.populateStats(abst, null);
            if (isNew) {
                addResource((XnatResourcecatalog) abst, type, user);
            } else {
                if ((!(ci instanceof UpdateMeta)) || ((UpdateMeta) ci).getUpdate()) {
                    SaveItemHelper.authorizedSave(abst, user, false, false, ci);
                }
            }
        }
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

    private void createCatalog(final XnatResourcecatalog resource, final XnatResourceInfo info) throws Exception {
        CatalogUtils.configureEntry(resource, info, user);

        final CatCatalogBean cat = new CatCatalogBean();
        cat.setId(StringUtils.defaultIfBlank(resource.getLabel(), getDefaultUID()));

        final Path parent = Paths.get(buildDestinationPath(), cat.getId());
        parent.toFile().mkdirs();
        final Path target = parent.resolve(cat.getId() + "_catalog.xml");

        CatalogUtils.writeCatalogToFile(cat, target.toFile());
        resource.setUri(target.toAbsolutePath().toString());
    }


}
