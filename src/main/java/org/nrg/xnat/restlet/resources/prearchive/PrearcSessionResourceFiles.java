/*
 * web: org.nrg.xnat.restlet.resources.prearchive.PrearcSessionResourceFiles
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.restlet.resources.prearchive;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ActionException;
import org.nrg.dcm.Dcm2Jpg;
import org.nrg.xdat.model.CatCatalogI;
import org.nrg.xdat.model.CatEntryI;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.model.XnatResourcecatalogI;
import org.nrg.xft.XFTTable;
import org.nrg.xnat.helpers.merge.MergeUtils;
import org.nrg.xnat.restlet.resources.SecureResource;
import org.nrg.xnat.utils.CatalogUtils;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.InputRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

/**
 * @author tolsen01
 */
@Slf4j
public class PrearcSessionResourceFiles extends PrearcScanResourceList {
    public PrearcSessionResourceFiles(final Context context, final Request request, final Response response) {
        super(context, request, response);
        resourceId = (String) SecureResource.getParameter(request, RESOURCE_ID);
    }

    @Override
    public Representation represent(final Variant variant) {
        final MediaType mediaType = overrideVariant(variant);

        final PrearcInfo info;
        try {
            info = retrieveSessionBean();
        } catch (ActionException e) {
            setResponseStatus(e);
            return null;
        }

        final XnatImagescandataI scan = MergeUtils.getMatchingScanById(getScanId(), info.session.getScans_scan());

        if (scan == null) {
            getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return null;
        }

        final XnatResourcecatalogI resourceCatalog = (XnatResourcecatalogI) MergeUtils.getMatchingResourceByLabel(resourceId, scan.getFile());

        if (resourceCatalog == null) {
            getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return null;
        }

        final String rootPath = CatalogUtils.getCatalogFile(info.session.getPrearchivepath(), resourceCatalog).getParentFile().getAbsolutePath();

        final CatCatalogI catalog = CatalogUtils.getCleanCatalog(info.session.getPrearchivepath(), resourceCatalog, false);

        if (StringUtils.isNotEmpty(filepath)) {
            final CatEntryI entry = CatalogUtils.getEntryByURI(catalog, filepath);
            if (entry == null) {
                getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Unable to access the file at: " + filepath);
                return new StringRepresentation("");
            }

            final File file = CatalogUtils.getFile(entry, rootPath);
            if (mediaType.equals(MediaType.IMAGE_JPEG) && StringUtils.equals(resourceId, "DICOM") && Dcm2Jpg.isDicom(file)) {
                try {
                    return new InputRepresentation(new ByteArrayInputStream(Dcm2Jpg.convert(file)), mediaType);
                } catch (IOException e) {
                    log.error("Unable to convert the entry at {} from the catalog at {}", filepath, rootPath);
                    getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "Unable to convert this file to jpeg : " + e.getMessage());
                    return new StringRepresentation("");
                }
            }

            return representFile(file, mediaType);
        } else {
            final boolean  prettyPrint = isQueryVariableTrue("prettyPrint");
            final XFTTable table       = new XFTTable();
            table.initTable(columns);
            CatalogUtils.getEntriesByFilter(catalog, null).forEach(entry -> {
                final File file = CatalogUtils.getFile(entry, rootPath);
                table.insertRow(new Object[]{file.getName(), (prettyPrint) ? CatalogUtils.formatSize(file.length()) : file.length(), constructURI(entry.getUri())});
            });

            return representTable(table, mediaType, new Hashtable<>());
        }

    }

    private String constructURI(String resource) {
        return getHttpServletRequest().getServletPath() + getHttpServletRequest().getPathInfo() + "/" + resource;
    }

    private static final String   RESOURCE_ID = "RESOURCE_ID";
    private static final String[] columns     = new String[]{"Name", "Size", "URI"};
    private final        String   resourceId;
}
