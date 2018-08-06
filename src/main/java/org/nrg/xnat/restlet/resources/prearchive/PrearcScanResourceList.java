/*
 * web: org.nrg.xnat.restlet.resources.prearchive.PrearcScanResourceList
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.restlet.resources.prearchive;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.nrg.action.ActionException;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.model.XnatResourcecatalogI;
import org.nrg.xft.XFTTable;
import org.nrg.xnat.helpers.merge.MergeUtils;
import org.nrg.xnat.services.archive.CatalogStats;
import org.nrg.xnat.utils.CatalogUtils;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

import java.util.Hashtable;

import static lombok.AccessLevel.PROTECTED;

/**
 * @author tolsen01
 */
@Getter(PROTECTED)
@Accessors(prefix = "_")
public class PrearcScanResourceList extends PrearcSessionResourceA {
    public PrearcScanResourceList(final Context context, final Request request, final Response response) {
        super(context, request, response);
        _scanId = (String) getParameter(request, SCAN_ID);
    }

    @Override
    public Representation represent(final Variant variant) {
        final PrearcInfo info;
        try {
            info = retrieveSessionBean();
        } catch (ActionException e) {
            setResponseStatus(e);
            return null;
        }

        final XnatImagescandataI scan = MergeUtils.getMatchingScanById(_scanId, info.session.getScans_scan());

        if (scan == null) {
            getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return null;
        }

        final XFTTable table = new XFTTable();
        table.initTable(PREARC_SCAN_COLUMNS);
        for (final XnatAbstractresourceI resource : scan.getFile()) {
            final String       rootPath = CatalogUtils.getCatalogFile(info.session.getPrearchivepath(), ((XnatResourcecatalogI) resource)).getParentFile().getAbsolutePath();
            final CatalogStats stats    = CatalogUtils.getFileStats(CatalogUtils.getCleanCatalog(info.session.getPrearchivepath(), (XnatResourcecatalogI) resource, false), rootPath);
            table.insertRow(new Object[]{resource.getLabel(), stats.getCount(), stats.getSize()});
        }

        return representTable(table, overrideVariant(variant), new Hashtable<>());
    }

    private static final String   SCAN_ID             = "SCAN_ID";
    private static final String[] PREARC_SCAN_COLUMNS = new String[]{"label", "file_count", "file_size"};

    private final String _scanId;
}
