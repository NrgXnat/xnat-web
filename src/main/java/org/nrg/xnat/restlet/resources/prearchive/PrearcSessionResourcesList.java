/*
 * web: org.nrg.xnat.restlet.resources.prearchive.PrearcSessionResourcesList
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.restlet.resources.prearchive;

import org.apache.log4j.Logger;
import org.nrg.action.ActionException;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.model.XnatResourcecatalogI;
import org.nrg.xft.XFTTable;
import org.nrg.xnat.services.archive.CatalogStats;
import org.nrg.xnat.utils.CatalogUtils;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

import java.util.Hashtable;

/**
 * @author tolsen01
 */
public class PrearcSessionResourcesList extends PrearcSessionResourceA {
    public PrearcSessionResourcesList(final Context context, final Request request, final Response response) {
        super(context, request, response);
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

        final XFTTable table = new XFTTable();
        table.initTable(PREARC_SESSION_COLUMNS);
        for (final XnatImagescandataI scan : info.session.getScans_scan()) {
            for (final XnatAbstractresourceI resource : scan.getFile()) {
                final String       rootPath = CatalogUtils.getCatalogFile(info.session.getPrearchivepath(), ((XnatResourcecatalogI) resource)).getParentFile().getAbsolutePath();
                final CatalogStats stats    = CatalogUtils.getFileStats(CatalogUtils.getCleanCatalog(info.session.getPrearchivepath(), (XnatResourcecatalogI) resource, false), rootPath);
                table.insertRow(new Object[]{"scans", scan.getId(), resource.getLabel(), stats.getCount(), stats.getSize()});
            }
        }
        return representTable(table, mediaType, new Hashtable<>());
    }

    private final static String[] PREARC_SESSION_COLUMNS = new String[]{"category", "cat_id", "label", "file_count", "file_size"};
}
