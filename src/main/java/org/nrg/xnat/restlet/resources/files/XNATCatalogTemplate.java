/*
 * web: org.nrg.xnat.restlet._resources.files.XNATCatalogTemplate
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.restlet.resources.files;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ClientException;
import org.nrg.xdat.om.*;
import org.nrg.xft.XFTTable;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xnat.helpers.resource.XnatResourceInfo;
import org.nrg.xnat.helpers.resource.direct.DirectResourceModifierBuilder;
import org.nrg.xnat.helpers.resource.direct.ResourceModifierA;
import org.nrg.xnat.helpers.resource.direct.ResourceModifierBuilderI;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PROTECTED;

@Getter(PROTECTED)
@Setter(PROTECTED)
@Accessors(prefix = "_")
@Slf4j
public class XNATCatalogTemplate extends XNATTemplate {
    public XNATCatalogTemplate(final Context context, final Request request, final Response response, final boolean allowAll) throws ClientException {
        super(context, request, response);

        final String resourceId = (String) getParameter(request, "RESOURCE_ID");

        if (StringUtils.isNotBlank(resourceId)) {
            _resourceIds.addAll(Arrays.asList(StringUtils.split(resourceId, ",")));
        }

        try {
            _catalogs = loadCatalogs(_resourceIds, true, allowAll);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public String getBaseURI() {
        final StringBuilder buffer = new StringBuilder("/data");
        if (hasProject() && hasSubject()) {
            buffer.append("/projects/");
            buffer.append(getProject().getId());
            buffer.append("/subjects/");
            buffer.append(getSubject().getId());
        }
        if (hasReconstructions()) {
            buffer.append("/experiments/");
            buffer.append(StringUtils.join(getAssesseds().stream().map(XnatExperimentdata::getId).collect(Collectors.toList()), ","));
            buffer.append("/reconstructions/");
            buffer.append(StringUtils.join(getReconstructions().stream().map(XnatReconstructedimagedata::getId).collect(Collectors.toList()), ","));
            if (hasType()) {
                buffer.append("/").append(getType());
            }
        } else if (hasType()) {
            buffer.append("/experiments/");
            buffer.append(StringUtils.join(getAssesseds().stream().map(XnatExperimentdata::getId).collect(Collectors.toList()), ","));
            buffer.append("/scans/");
            buffer.append(StringUtils.join(getScans().stream().map(XnatImagescandata::getId).collect(Collectors.toList()), ","));
        } else if (hasExperiments()) {
            if (hasAssesseds()) {
                buffer.append("/experiments/");
                buffer.append(StringUtils.join(getAssesseds().stream().map(XnatExperimentdata::getId).collect(Collectors.toList()), ","));
                buffer.append("/assessors/");
                buffer.append(StringUtils.join(getExperiments().stream().map(XnatExperimentdata::getId).collect(Collectors.toList()), ","));
                if (hasType()) {
                    buffer.append("/").append(getType());
                }
            } else {
                buffer.append("/experiments/");
                buffer.append(StringUtils.join(getExperiments().stream().map(XnatExperimentdata::getId).collect(Collectors.toList()), ","));
            }
        } else if (hasProject() && !hasSubject()) {
            buffer.append("/projects/");
            buffer.append(getProject().getId());
        }
        return buffer.toString();
    }

    public XnatResourceInfo buildResourceInfo(EventMetaI ci) {
        final String description;
        if (this.getQueryVariable("description") != null) {
            description = this.getQueryVariable("description");
        } else {
            description = null;
        }

        final String format;
        if (this.getQueryVariable("format") != null) {
            format = this.getQueryVariable("format");
        } else {
            format = null;
        }

        final String content;
        if (this.getQueryVariable("content") != null) {
            content = this.getQueryVariable("content");
        } else {
            content = null;
        }

        String[] tags;
        if (this.getQueryVariables("tags") != null) {
            tags = this.getQueryVariables("tags");
        } else {
            tags = null;
        }

        Date d = EventUtils.getEventDate(ci, false);
        return XnatResourceInfo.buildResourceInfo(description, format, content, tags, getUser(), d, d, EventUtils.getEventId(ci));
    }

    protected ResourceModifierA buildResourceModifier(final boolean overwrite, EventMetaI ci) throws Exception {
        final XnatImagesessiondata assessed = getAssesseds().size() == 1 ? (XnatImagesessiondata) getAssesseds().get(0) : null;

        //this should allow dependency injection - TO
        final ResourceModifierBuilderI builder = new DirectResourceModifierBuilder();

        if (hasReconstructions()) {
            builder.setRecon(assessed, getReconstructions().get(0), getType());
        } else if (hasScans()) {
            builder.setScan(assessed, getScans().get(0));
        } else if (hasExperiments()) {
            final XnatExperimentdata experiment = getExperiments().get(0);
            if (experiment.getItem().instanceOf(XnatImageassessordata.SCHEMA_ELEMENT_NAME)) {
                builder.setAssess(ObjectUtils.defaultIfNull(assessed, ((XnatImageassessordata) experiment).getImageSessionData()), (XnatImageassessordata) experiment, getType());
            } else {
                builder.setExpt(ObjectUtils.defaultIfNull(getProject(), experiment.getProjectData()), experiment);
            }
        } else if (hasSubject()) {
            builder.setSubject(getProject(), getSubject());
        } else if (hasProject()) {
            builder.setProject(getProject());
        } else {
            throw new Exception("Unknown resource");
        }

        return builder.buildResourceModifier(overwrite, getUser(), ci);
    }

    protected boolean hasCatalogs() {
        return _catalogs != null && _catalogs.size() > 0;
    }

    protected void clearCatalogs() {
        _catalogs = null;
    }

    protected boolean hasResourceIds() {
        return !_resourceIds.isEmpty();
    }

    protected void clearResources() {
        _resources.clear();
    }

    private       XFTTable                   _catalogs    = null;
    private final List<String>               _resourceIds = new ArrayList<>();
    private final List<XnatAbstractresource> _resources   = new ArrayList<>();
}
