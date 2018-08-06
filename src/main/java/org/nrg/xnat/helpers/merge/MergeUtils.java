/*
 * web: org.nrg.xnat.helpers.merge.MergeUtils
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.helpers.merge;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.model.XnatResourceI;
import org.nrg.xdat.model.XnatResourceseriesI;

import java.util.List;
import java.util.Objects;

public class MergeUtils {
    @SuppressWarnings("unused")
    public static boolean compareResources(final XnatAbstractresourceI source, final XnatAbstractresourceI destination) {
        if (source instanceof XnatResourceseriesI) {
            final XnatResourceseriesI sourceSeries      = (XnatResourceseriesI) source;
            final XnatResourceseriesI destinationSeries = (XnatResourceseriesI) destination;
            return StringUtils.equals(sourceSeries.getPath(), destinationSeries.getPath()) && StringUtils.equals(sourceSeries.getPattern(), destinationSeries.getPattern());
        }
        return StringUtils.equals(((XnatResourceI) source).getUri(), ((XnatResourceI) destination).getUri());
    }

    public static XnatImagescandataI getMatchingScanById(final String scanId, final List<XnatImagescandataI> scans) {
        return scans.stream().filter(Objects::nonNull).filter(candidate -> StringUtils.equals(scanId, candidate.getId())).findAny().orElse(null);
    }

    @SuppressWarnings("WeakerAccess")
    public static XnatImagescandataI getMatchingScan(final XnatImagescandataI scan, final List<XnatImagescandataI> scans) {
        return getMatchingScanById(scan.getId(), scans);
    }

    public static XnatImagescandataI getMatchingScanByUID(final XnatImagescandataI scan, final List<XnatImagescandataI> scans) {
        final String scanUid = scan.getUid();
        return scans.stream().filter(Objects::nonNull).filter(candidate -> StringUtils.equals(scanUid, candidate.getId())).findAny().orElse(null);
    }

    public static XnatAbstractresourceI getMatchingResourceByLabel(final String label, final List<XnatAbstractresourceI> resources) {
        return resources.stream().filter(Objects::nonNull).filter(candidate -> StringUtils.equals(label, candidate.getLabel())).findAny().orElse(null);
    }

    public static XnatAbstractresourceI getMatchingResource(final XnatAbstractresourceI resource, final List<XnatAbstractresourceI> resources) {
        return getMatchingResourceByLabel(resource.getLabel(), resources);
    }
}
