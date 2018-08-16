/*
 * web: org.nrg.xnat.restlet._resources.files.XNATTemplate
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
import org.nrg.xdat.XDAT;
import org.nrg.xdat.model.XnatImageassessordataI;
import org.nrg.xdat.om.*;
import org.nrg.xft.ItemI;
import org.nrg.xft.XFTTable;
import org.nrg.xft.db.PoolDBUtils;
import org.nrg.xft.exception.ElementNotFoundException;
import org.nrg.xft.schema.Wrappers.GenericWrapper.GenericWrapperElement;
import org.nrg.xft.search.CriteriaCollection;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.uri.UriParserUtils;
import org.nrg.xnat.restlet.resources.SecureResource;
import org.nrg.xnat.services.archive.CatalogService;
import org.restlet.Context;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PROTECTED;

@Getter(PROTECTED)
@Setter(PROTECTED)
@Accessors(prefix = "_")
@Slf4j
public class XNATTemplate extends SecureResource {
    public XNATTemplate(Context context, Request request, Response response) throws ClientException {
        super(context, request, response);

        _catalogService = XDAT.getContextService().getBean(CatalogService.class);

        final UserI user = getUser();

        final String projectId = (String) getParameter(request, "PROJECT_ID");
        if (StringUtils.isNotBlank(projectId)) {
            _project = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
            if (_project == null) {
                throw new ClientException(Status.CLIENT_ERROR_NOT_FOUND, "Unable to identify project with ID " + projectId);
            }
        }

        final String subjectId = (String) getParameter(request, "SUBJECT_ID");
        if (StringUtils.isNotBlank(subjectId)) {
            if (_project != null) {
                _subject = XnatSubjectdata.GetSubjectByProjectIdentifier(projectId, subjectId, user, false);
            }

            if (_subject == null) {
                _subject = XnatSubjectdata.getXnatSubjectdatasById(subjectId, user, false);
                if (_subject != null && (_project != null && !_subject.hasProject(_project.getId()))) {
                    _subject = null;
                }
            }

            if (_subject == null) {
                if (_project == null) {
                    throw new ClientException(Status.CLIENT_ERROR_NOT_FOUND, "Unable to identify subject with ID " + subjectId);
                }
                throw new ClientException(Status.CLIENT_ERROR_NOT_FOUND, "Unable to identify subject with ID " + subjectId + " in the project " + projectId);
            }
        }

        final boolean isGetCall = getRequest().getMethod().equals(Method.GET);

        final String assessedId = (String) getParameter(request, "ASSESSED_ID");
        if (StringUtils.isNotBlank(assessedId)) {
            for (final String particular : StringUtils.split(assessedId, ",")) {
                XnatExperimentdata assessed = XnatImagesessiondata.getXnatImagesessiondatasById(particular, user, false);
                if (assessed != null && (_project != null && !assessed.hasProject(_project.getId()))) {
                    assessed = null;
                }
                if (assessed == null && _project != null) {
                    assessed = XnatImagesessiondata.GetExptByProjectIdentifier(_project.getId(), particular, user, false);
                }
                if (assessed != null) {
                    try {
                        if (assessed.canRead(user)) {
                            _assesseds.add(assessed);
                        }
                    } catch (Exception ignored) {
                    }
                }

                if (_assesseds.size() != 1 && !isGetCall) {
                    throw new ClientException(Status.CLIENT_ERROR_NOT_FOUND, "Unable to identify image session");
                }
            }
        }

        _type = (String) getParameter(request, "TYPE");

        final String experimentId = (String) getParameter(request, "EXPT_ID");
        if (experimentId != null) {
            for (final String particular : StringUtils.split(experimentId, ",")) {
                XnatExperimentdata experiment = XnatExperimentdata.getXnatExperimentdatasById(particular, user, false);

                if (experiment == null && _project != null) {
                    experiment = XnatExperimentdata.GetExptByProjectIdentifier(projectId, particular, user, false);
                }

                if (experiment != null && hasAssesseds() && !hasType()) {
                    _type = "out";
                }

                if (experiment != null) {
                    try {
                        if (experiment.canRead(user)) {
                            _experiments.add(experiment);
                        }
                    } catch (Exception ignored) {
                    }
                } else if (hasAssesseds()) {
                    for (final XnatExperimentdata assessed : _assesseds) {
                        for (final XnatImageassessordataI iad : ((XnatImagesessiondata) assessed).getMinimalLoadAssessors()) {
                            if (iad.getId().equals(particular) || (iad.getLabel() != null && iad.getLabel().equals(particular))) {
                                try {
                                    if (((XnatImageassessordata) iad).canRead(user)) {
                                        _experiments.add(((XnatImageassessordata) iad));
                                    }
                                } catch (Exception ignored) {
                                }
                            } else if (particular.equals("*") || particular.equals("ALL")) {
                                try {
                                    if (((XnatImageassessordata) iad).canRead(user)) {
                                        _experiments.add(((XnatImageassessordata) iad));
                                    }
                                } catch (Exception ignored) {
                                }
                            } else {
                                try {
                                    final GenericWrapperElement gwe = GenericWrapperElement.GetElement(particular);

                                    if (((XnatImageassessordata) iad).getItem().instanceOf(gwe.getFullXMLName())) {
                                        if (((XnatImageassessordata) iad).canRead(user)) {
                                            _experiments.add(((XnatImageassessordata) iad));
                                        }
                                    }
                                } catch (Exception ignored) {
                                }
                            }
                        }
                    }
                }
            }

            if (_experiments.size() != 1 && !isGetCall) {
                throw new ClientException(Status.CLIENT_ERROR_NOT_FOUND, "Unable to identify experiment");
            }
        }

        final String unescaped = getUrlEncodedParameter(request, "SCAN_ID");
        if (StringUtils.isNotBlank(unescaped) && hasAssesseds()) {
            final String  scanId              = unescaped.replace("[SLASH]", "/");//this is such an ugly hack.  If a slash is included in the scan type and thus in the URL, it breaks the GET command.  Even if it is properly escaped.  So, I'm adding this alternative encoding of slash to allow us to work around the issue.  Hopefully Spring MVC will eliminate it.
            final boolean scanIdMatchesAll    = StringUtils.equalsAny(scanId, "*", "ALL");
            final boolean scanIdContainsComma = !scanId.contains(",");

            final CriteriaCollection cc = new CriteriaCollection("OR");
            for (final XnatExperimentdata assessed : getAssesseds()) {
                final CriteriaCollection sub1 = new CriteriaCollection("AND");
                sub1.addClause("xnat:imageScanData/image_session_ID", assessed.getId());
                if (!scanIdMatchesAll) {
                    if (scanIdContainsComma) {
                        sub1.addClause("xnat:imageScanData/ID", scanId);
                    } else {
                        final CriteriaCollection subsubcc = new CriteriaCollection("OR");
                        for (final String particular : StringUtils.split(scanId, ",")) {
                            subsubcc.addClause("xnat:imageScanData/ID", particular);
                        }
                        sub1.add(subsubcc);
                    }
                }
                cc.add(sub1);

                final CriteriaCollection sub2 = new CriteriaCollection("AND");
                sub2.addClause("xnat:imageScanData/image_session_ID", assessed.getId());
                if (!scanIdMatchesAll) {
                    if (scanIdContainsComma) {
                        if (scanId.equals("NULL")) {
                            final CriteriaCollection subsubcc = new CriteriaCollection("OR");
                            subsubcc.addClause("xnat:imageScanData/type", "", " IS NULL ", true);
                            subsubcc.addClause("xnat:imageScanData/type", "");
                            sub2.add(subsubcc);
                        } else {
                            sub2.addClause("xnat:imageScanData/type", scanId.replace("[COMMA]", ","));
                        }
                    } else {
                        final CriteriaCollection subsubcc = new CriteriaCollection("OR");
                        for (final String particular : StringUtils.split(scanId, ",")) {
                            if (particular.equals("NULL")) {
                                subsubcc.addClause("xnat:imageScanData/type", "", " IS NULL ", true);
                                subsubcc.addClause("xnat:imageScanData/type", "");
                            } else {
                                subsubcc.addClause("xnat:imageScanData/type", particular.replace("[COMMA]", ","));
                            }
                        }
                        sub2.add(subsubcc);
                    }
                }
                cc.add(sub2);
            }

            setScans(XnatImagescandata.getXnatImagescandatasByField(cc, user, completeDocument));

            if (_scans.size() != 1 && !isGetCall) {
                throw new ClientException(Status.CLIENT_ERROR_NOT_FOUND, "Unable to identify scan");
            }
        }

        final String reconstructionId = getUrlEncodedParameter(request, "RECON_ID");
        if (StringUtils.isNotBlank(reconstructionId) && hasAssesseds()) {
            final boolean reconstructionIdMatchesAll    = StringUtils.equalsAny(reconstructionId, "*", "ALL");
            final boolean reconstructionIdContainsComma = reconstructionId.contains(",");

            final CriteriaCollection cc = new CriteriaCollection("OR");
            for (final XnatExperimentdata assessed : _assesseds) {
                final CriteriaCollection sub1 = new CriteriaCollection("AND");
                sub1.addClause("xnat:reconstructedImageData/image_session_ID", assessed.getId());
                if (!reconstructionIdMatchesAll) {
                    if (!reconstructionIdContainsComma) {
                        sub1.addClause("xnat:reconstructedImageData/ID", reconstructionId);
                    } else {
                        final CriteriaCollection subsubcc = new CriteriaCollection("OR");
                        Arrays.stream(StringUtils.split(reconstructionId, ",")).forEach(each -> subsubcc.addClause("xnat:reconstructedImageData/ID", each));
                        sub1.add(subsubcc);
                    }
                }
                cc.add(sub1);

                final CriteriaCollection sub2 = new CriteriaCollection("AND");
                sub2.addClause("xnat:reconstructedImageData/image_session_ID", assessed.getId());
                if (!reconstructionIdMatchesAll) {
                    if (!reconstructionIdContainsComma) {
                        if (reconstructionId.equals("NULL")) {
                            final CriteriaCollection subsubcc = new CriteriaCollection("OR");
                            subsubcc.addClause("xnat:reconstructedImageData/type", "", " IS NULL ", true);
                            subsubcc.addClause("xnat:reconstructedImageData/type", "");
                            sub2.add(subsubcc);
                        } else {
                            sub2.addClause("xnat:reconstructedImageData/type", reconstructionId);
                        }
                    } else {
                        final CriteriaCollection subsubcc = new CriteriaCollection("OR");
                        for (final String particular : StringUtils.split(reconstructionId, ",")) {
                            if (particular.equals("NULL")) {
                                subsubcc.addClause("xnat:reconstructedImageData/type", "", " IS NULL ", true);
                                subsubcc.addClause("xnat:reconstructedImageData/type", "");
                            } else {
                                subsubcc.addClause("xnat:reconstructedImageData/type", particular.replace("[COMMA]", ","));
                            }
                        }
                        sub2.add(subsubcc);
                    }
                }
                cc.add(sub2);
            }

            setReconstructions(XnatReconstructedimagedata.getXnatReconstructedimagedatasByField(cc, user, completeDocument));
            if (hasReconstructions() && !hasType()) {
                _type = "out";
            }

            if (_reconstructions.size() != 1 && !isGetCall) {
                throw new ClientException(Status.CLIENT_ERROR_NOT_FOUND, "Unable to identify reconstruction");
            }
        }
    }

    public ItemI getSecurityItem() {
        if (_security != null) {
            return _security;
        }

        XnatExperimentdata assessed = null;
        if (_assesseds.size() == 1) {
            assessed = _assesseds.get(0);
        }

        if (_reconstructions.size() > 0) {
            return assessed;
        } else if (_scans.size() > 0) {
            return assessed;
        } else if (_experiments.size() > 0) {
//			experiment
            return _experiments.get(0);
        } else if (_subject != null) {
            return _subject;
        } else if (_project != null) {
            return _project;
        } else {
            return null;
        }
    }

    public boolean insertCatalog(final XnatResourcecatalog resourceCatalog) throws Exception {
        final XnatExperimentdata assessed = _assesseds.size() == 1 ? _assesseds.get(0) : null;

        final UserI user = getUser();
        if (_reconstructions.size() > 0) {
            if (assessed == null) {
                getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND, "Invalid session id.");
                return false;
            }

            final XnatReconstructedimagedata reconstruction = _reconstructions.get(0);
            return _catalogService.insertResourceCatalog(user, UriParserUtils.getArchiveUri(assessed, reconstruction), resourceCatalog) != null;
        } else if (_scans.size() > 0) {
            if (assessed == null) {
                getResponse().setStatus(Status.CLIENT_ERROR_GONE, "Invalid session id.");
                return false;
            }
            final XnatImagescandata scan = _scans.get(0);
            return _catalogService.insertResourceCatalog(user, UriParserUtils.getArchiveUri(assessed, scan), resourceCatalog) != null;
        } else if (_experiments.size() > 0) {
            final XnatExperimentdata experiment = _experiments.get(0);
            return _catalogService.insertResourceCatalog(user, UriParserUtils.getArchiveUri(experiment), resourceCatalog) != null;
        } else if (_subject != null) {
            return _catalogService.insertResourceCatalog(user, UriParserUtils.getArchiveUri(_subject), resourceCatalog) != null;
        } else if (_project != null) {
            return _catalogService.insertResourceCatalog(user, UriParserUtils.getArchiveUri(_project), resourceCatalog) != null;
        }
        return true;
    }

    public void checkResourceIDs(final List<String> resourceIds) throws Exception {
        if (resourceIds != null) {
            for (String resourceID : resourceIds) {
                if (resourceID != null) {
                    if (resourceID.contains("'")) {
                        throw new Exception("Possible SQL Injection attempt. ' is not allowed in resource labels: " + resourceID);
                    } else {
                        if (PoolDBUtils.HackCheck(resourceID)) {
                            throw new Exception("Possible SQL Injection attempt: " + resourceID);
                        }
                    }
                }
            }
        }
    }

    public XFTTable loadCatalogs(final List<String> resourceIds, final boolean includeURI, final boolean allowAll) throws Exception {
        checkResourceIDs(resourceIds);

        final StringBuilder query         = new StringBuilder();
        String              starterFields = "SELECT xnat_abstractresource_id,abst.label,xme.element_name ";

        if (_reconstructions.size() > 0) {
            _security = _assesseds.get(0);
            _parent = _reconstructions.get(0);
            _xmlPath = "xnat:reconstructedImageData/in/file";
            if (StringUtils.equals("in", getType())) {
                query.append(starterFields);
                query.append(", 'reconstructions'::TEXT AS category, recon.id::TEXT AS cat_id");
                query.append(", recon.type::TEXT AS cat_desc");
                if (includeURI) {
                    query.append(",'/experiments/' || recon.image_session_id");
                    query.append(" || '/reconstructions/' || recon.id || '/in'");
                    query.append(" || '/_resources/' || abst.xnat_abstractresource_id AS resource_path");
                }
                query.append(" FROM recon_in_resource map ");
                query.append(" LEFT JOIN xnat_reconstructedimagedata recon ON map.xnat_reconstructedimagedata_xnat_reconstructedimagedata_id=recon.xnat_reconstructedimagedata_id ");
                query.append(" LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id");
                query.append(" LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id");
                query.append(" WHERE (");
                int sC = 0;
                for (XnatReconstructedimagedata recon : _reconstructions) {
                    if (sC++ > 0) {
                        query.append(" OR ");
                    }
                    query.append("xnat_reconstructedimagedata_xnat_reconstructedimagedata_id=");
                    query.append(recon.getXnatReconstructedimagedataId());
                }
                query.append(") ");
                if (resourceIds != null && resourceIds.size() > 0) {
                    int c = 0;
                    query.append(" AND ( ");
                    for (String resourceID : resourceIds) {
                        if (c++ > 0) {
                            query.append(" OR ");
                        }
                        if (StringUtils.isNumeric(resourceID)) {
                            query.append(" (map.xnat_abstractresource_xnat_abstractresource_id=");
                            query.append(resourceID);
                            query.append(" OR abst.label='");
                            query.append(resourceID);
                            query.append("')");
                        } else if (resourceID.equalsIgnoreCase("NULL")) {
                            query.append(" abst.label IS NULL");
                        } else {
                            query.append(" abst.label='");
                            query.append(resourceID);
                            query.append("'");
                        }
                    }
                    query.append(")");
                }
            } else {
                _xmlPath = "xnat:reconstructedImageData/out/file";
                query.append(starterFields);
                query.append(", 'reconstructions'::TEXT AS category, recon.id::TEXT AS cat_id");
                query.append(", recon.type::TEXT AS cat_desc");
                if (includeURI) {
                    query.append(",'/experiments/' || recon.image_session_id");
                    query.append(" || '/reconstructions/' || recon.id || '/out'");
                    query.append(" || '/_resources/' || abst.xnat_abstractresource_id AS resource_path");
                }
                query.append(" FROM recon_out_resource map ");
                query.append(" LEFT JOIN xnat_reconstructedimagedata recon ON map.xnat_reconstructedimagedata_xnat_reconstructedimagedata_id=recon.xnat_reconstructedimagedata_id ");
                query.append(" LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id");
                query.append(" LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id");

                query.append(" WHERE (");
                int sC = 0;
                for (XnatReconstructedimagedata recon : _reconstructions) {
                    if (sC++ > 0) {
                        query.append(" OR ");
                    }
                    query.append("xnat_reconstructedimagedata_xnat_reconstructedimagedata_id=");
                    query.append(recon.getXnatReconstructedimagedataId());
                }
                query.append(") ");

                if (resourceIds != null && resourceIds.size() > 0) {
                    int c = 0;
                    query.append(" AND ( ");
                    for (String resourceID : resourceIds) {
                        if (c++ > 0) {
                            query.append(" OR ");
                        }
                        if (StringUtils
                                .isNumeric(resourceID)) {
                            query
                                    .append(" (map.xnat_abstractresource_xnat_abstractresource_id=");
                            query.append(resourceID);
                            query.append(" OR abst.label='");
                            query.append(resourceID);
                            query.append("')");
                        } else if (resourceID.equalsIgnoreCase("NULL")) {
                            query.append(" abst.label IS NULL");
                        } else {
                            query.append(" abst.label='");
                            query.append(resourceID);
                            query.append("'");
                        }
                    }
                    query.append(")");
                }
            }
        } else if (_scans.size() > 0) {
            _security = _assesseds.get(0);
            _parent = _scans.get(0);
            _xmlPath = "xnat:imageScanData/file";
            query.append(starterFields);
            query.append(", 'scans'::TEXT AS category, scan.id::TEXT AS cat_id");
            query.append(", scan.type::TEXT AS cat_desc");
            if (includeURI) {
                query.append(",'/experiments/' || scan.image_session_id");
                query.append(" || '/scans/' || scan.id");
                query.append(" || '/_resources/' || abst.xnat_abstractresource_id AS resource_path");
            }
            query.append(" FROM xnat_abstractresource abst LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id");
            query.append(" LEFT JOIN xnat_imagescandata scan ON abst.xnat_imagescandata_xnat_imagescandata_id=scan.xnat_imagescandata_id");
            query.append(" WHERE (");
            int sC = 0;
            for (XnatImagescandata scan : _scans) {
                if (sC++ > 0) {
                    query.append(" OR ");
                }
                query.append("xnat_imagescandata_xnat_imagescandata_id=");
                query.append(scan.getXnatImagescandataId());
            }
            query.append(") ");
            if (resourceIds != null && resourceIds.size() > 0) {
                int c = 0;
                query.append(" AND ( ");
                for (String resourceID : resourceIds) {
                    if (c++ > 0) {
                        query.append(" OR ");
                    }
                    if (StringUtils
                            .isNumeric(resourceID)) {
                        query.append(" (abst.xnat_abstractresource_id=");
                        query.append(resourceID);
                        query.append(" OR abst.label='");
                        query.append(resourceID);
                        query.append("')");
                    } else if (resourceID.equalsIgnoreCase("NULL")) {
                        query.append(" abst.label IS NULL");
                    } else {
                        query.append(" abst.label='");
                        query.append(resourceID);
                        query.append("'");
                    }
                }
                query.append(")");
            }
        } else if (_experiments.size() > 0) {
            _security = _experiments.get(0);
            _parent = _experiments.get(0);
            if (_assesseds.size() > 0) {
                _security = _assesseds.get(0);
                if (StringUtils.equals("in", getType())) {
                    _xmlPath = "xnat:imageAssessorData/in/file";
                    query.append(starterFields);
                    query.append(", 'assessors'::TEXT AS category, expt.id::TEXT AS cat_id");
                    query.append(", COALESCE(xes.singular,xmeexpt.element_name)::TEXT AS cat_desc");
                    if (includeURI) {
                        query.append(",'/experiments/' || xiad.imagesession_id");
                        query.append(" || '/assessors/' || expt.id || '/in'");
                        query.append(" || '/_resources/' || abst.xnat_abstractresource_id AS resource_path");
                    }
                    query.append(" FROM img_assessor_in_resource map ");
                    query.append(" LEFT JOIN xnat_experimentdata expt ON map.xnat_imageassessordata_id=expt.id ");
                    if (includeURI) {
                        query.append(" LEFT JOIN xnat_imageassessordata xiad ON expt.id=xiad.id ");
                    }
                    query.append(" LEFT JOIN xdat_meta_element xmeexpt ON expt.extension=xmeexpt.xdat_meta_element_id ");
                    query.append(" LEFT JOIN xdat_element_security xes ON xmeexpt.element_name=xes.element_name ");
                    query.append(" LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id");
                    query.append(" LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id");
                    query.append(" WHERE (");
                    int sC = 0;
                    for (XnatExperimentdata expt : _experiments) {
                        if (sC++ > 0) {
                            query.append(" OR ");
                        }
                        query.append("map.xnat_imageassessordata_id='");
                        query.append(expt.getId());
                        query.append("'");
                    }
                    query.append(") ");
                    if (resourceIds != null && resourceIds.size() > 0) {
                        int c = 0;
                        query.append(" AND ( ");
                        for (String resourceID : resourceIds) {
                            if (c++ > 0) {
                                query.append(" OR ");
                            }
                            if (StringUtils
                                    .isNumeric(resourceID)) {
                                query
                                        .append(" (map.xnat_abstractresource_xnat_abstractresource_id=");
                                query.append(resourceID);
                                query.append(" OR abst.label='");
                                query.append(resourceID);
                                query.append("')");
                            } else if (resourceID.equalsIgnoreCase("NULL")) {
                                query.append(" abst.label IS NULL");
                            } else {
                                query.append(" abst.label='");
                                query.append(resourceID);
                                query.append("'");
                            }
                        }
                        query.append(")");
                    }
                } else {
                    _xmlPath = "xnat:imageAssessorData/out/file";
                    query.append(starterFields);
                    query.append(", 'assessors'::TEXT AS category, expt.id::TEXT AS cat_id");
                    query.append(", COALESCE(xes.singular,xmeexpt.element_name)::TEXT AS cat_desc");
                    if (includeURI) {
                        query.append(",'/experiments/' || xiad.imagesession_id");
                        query.append(" || '/assessors/' || expt.id || '/out'");
                        query.append(" || '/_resources/' || abst.xnat_abstractresource_id AS resource_path");
                    }
                    query.append(" FROM img_assessor_out_resource map ");
                    query.append(" LEFT JOIN xnat_experimentdata expt ON map.xnat_imageassessordata_id=expt.id ");
                    if (includeURI) {
                        query.append(" LEFT JOIN xnat_imageassessordata xiad ON expt.id=xiad.id ");
                    }
                    query.append(" LEFT JOIN xdat_meta_element xmeexpt ON expt.extension=xmeexpt.xdat_meta_element_id ");
                    query.append(" LEFT JOIN xdat_element_security xes ON xmeexpt.element_name=xes.element_name ");
                    query.append(" LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id");
                    query.append(" LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id");
                    query.append(" WHERE (");
                    int sC = 0;
                    for (XnatExperimentdata expt : _experiments) {
                        if (sC++ > 0) {
                            query.append(" OR ");
                        }
                        query.append("map.xnat_imageassessordata_id='");
                        query.append(expt.getId());
                        query.append("'");
                    }
                    query.append(") ");
                    if (resourceIds != null && resourceIds.size() > 0) {
                        int c = 0;
                        query.append(" AND ( ");
                        for (String resourceID : resourceIds) {
                            if (c++ > 0) {
                                query.append(" OR ");
                            }
                            if (StringUtils
                                    .isNumeric(resourceID)) {
                                query
                                        .append(" (map.xnat_abstractresource_xnat_abstractresource_id=");
                                query.append(resourceID);
                                query.append(" OR abst.label='");
                                query.append(resourceID);
                                query.append("')");
                            } else if (resourceID.equalsIgnoreCase("NULL")) {
                                query.append(" abst.label IS NULL");
                            } else {
                                query.append(" abst.label='");
                                query.append(resourceID);
                                query.append("'");
                            }
                        }
                        query.append(")");
                    }
                }

            } else if ((allowAll) && (isQueryVariableTrue("all") || resourceIds != null)) {
                _xmlPath = "xnat:experimentData/_resources/resource";
                final String assessorIdsUserCanAccessSelect = "( SELECT * FROM xnat_imageassessordata WHERE id IN (SELECT id " +
                                                              " FROM   (SELECT xea.element_name, " +
                                                              "                xfm.field, " +
                                                              "                xfm.field_value " +
                                                              "         FROM   xdat_user u " +
                                                              "                JOIN xdat_user_groupid map " +
                                                              "                  ON u.xdat_user_id = map.groups_groupid_xdat_user_xdat_user_id " +
                                                              "                JOIN xdat_usergroup gp " +
                                                              "                  ON map.groupid = gp.id " +
                                                              "                JOIN xdat_element_access xea " +
                                                              "                  ON gp.xdat_usergroup_id = xea.xdat_usergroup_xdat_usergroup_id " +
                                                              "                JOIN xdat_field_mapping_set xfms " +
                                                              "                  ON " +
                                                              " xea.xdat_element_access_id = xfms.permissions_allow_set_xdat_elem_xdat_element_access_id " +
                                                              " JOIN xdat_field_mapping xfm " +
                                                              "   ON " +
                                                              " xfms.xdat_field_mapping_set_id = xfm.xdat_field_mapping_set_xdat_field_mapping_set_id " +
                                                              " AND read_element = 1 " +
                                                              " AND field_value != '' " +
                                                              " AND field != '' " +
                                                              " WHERE  u.login = 'guest' " +
                                                              "  UNION " +
                                                              "  SELECT xea.element_name, " +
                                                              "         xfm.field, " +
                                                              "         xfm.field_value " +
                                                              "  FROM   xdat_user_groupid map " +
                                                              "         JOIN xdat_usergroup gp " +
                                                              "           ON map.groupid = gp.id " +
                                                              "         JOIN xdat_element_access xea " +
                                                              "           ON gp.xdat_usergroup_id = xea.xdat_usergroup_xdat_usergroup_id " +
                                                              "         JOIN xdat_field_mapping_set xfms " +
                                                              "           ON " +
                                                              " xea.xdat_element_access_id = xfms.permissions_allow_set_xdat_elem_xdat_element_access_id " +
                                                              " JOIN xdat_field_mapping xfm " +
                                                              "   ON " +
                                                              " xfms.xdat_field_mapping_set_id = xfm.xdat_field_mapping_set_xdat_field_mapping_set_id " +
                                                              " AND read_element = 1 " +
                                                              " AND field_value != '' " +
                                                              " AND field != '' " +
                                                              " WHERE  map.groups_groupid_xdat_user_xdat_user_id = " + getUser().getID() + " " +
                                                              " OR xfm.field_value IN (SELECT proj.id " +
                                                              "         FROM   xnat_projectdata proj " +
                                                              "         JOIN (SELECT field_value, " +
                                                              "                        read_element AS " +
                                                              "                                                        project_read " +
                                                              "                                FROM   xdat_element_access " +
                                                              "                                ea " +
                                                              "                                LEFT JOIN xdat_field_mapping_set fms " +
                                                              "                                ON ea.xdat_element_access_id = " +
                                                              "                                fms.permissions_allow_set_xdat_elem_xdat_element_access_id " +
                                                              "                                LEFT JOIN xdat_user u " +
                                                              "                                ON ea.xdat_user_xdat_user_id = u.xdat_user_id " +
                                                              "                                LEFT JOIN xdat_field_mapping fm " +
                                                              "                                ON fms.xdat_field_mapping_set_id = " +
                                                              "                                fm.xdat_field_mapping_set_xdat_field_mapping_set_id " +
                                                              "                                WHERE  login = 'guest' " +
                                                              "                                AND read_element = 1 " +
                                                              "                                AND element_name = 'xnat:projectData')project_read " +
                                                              " ON proj.id = project_read.field_value " +
                                                              " JOIN (SELECT field_value, " +
                                                              "       read_element AS subject_read " +
                                                              "               FROM   xdat_element_access ea " +
                                                              "               LEFT JOIN xdat_field_mapping_set fms " +
                                                              "               ON ea.xdat_element_access_id = " +
                                                              "               fms.permissions_allow_set_xdat_elem_xdat_element_access_id " +
                                                              "               LEFT JOIN xdat_user u " +
                                                              "               ON ea.xdat_user_xdat_user_id = u.xdat_user_id " +
                                                              "               LEFT JOIN xdat_field_mapping fm " +
                                                              "               ON fms.xdat_field_mapping_set_id = " +
                                                              "               fm.xdat_field_mapping_set_xdat_field_mapping_set_id " +
                                                              "               WHERE  login = 'guest' " +
                                                              "               AND read_element = 1 " +
                                                              "               AND field = 'xnat:subjectData/project')subject_read " +
                                                              " ON proj.id = subject_read.field_value)) perms " +
                                                              " INNER JOIN (SELECT iad.id, " +
                                                              "                    element_name " +
                                                              "                    || '/project' AS field, " +
                                                              "                    expt.project, " +
                                                              "                    expt.label " +
                                                              "             FROM   xnat_imageassessordata iad " +
                                                              "                    LEFT JOIN xnat_experimentdata expt " +
                                                              "                           ON iad.id = expt.id " +
                                                              "                    LEFT JOIN xdat_meta_element xme " +
                                                              "                           ON expt.extension = xme.xdat_meta_element_id " +
                                                              "             WHERE  iad.imagesession_id IN ( '" +
                                                              StringUtils.join(_experiments.stream().map(XnatExperimentdata::getId).collect(Collectors.toList()), "', '") +
                                                              "' ) " +
                                                              "             UNION " +
                                                              "             SELECT expt.id, " +
                                                              "                    xme.element_name " +
                                                              "                    || '/sharing/share/project', " +
                                                              "                    shr.project, " +
                                                              "                    shr.label " +
                                                              "             FROM   xnat_experimentdata_share shr " +
                                                              "                    LEFT JOIN xnat_experimentdata expt " +
                                                              "                           ON expt.id = shr.sharing_share_xnat_experimentda_id " +
                                                              "                    LEFT JOIN xdat_meta_element xme " +
                                                              "                           ON expt.extension = xme.xdat_meta_element_id) _experiments " +
                                                              "         ON perms.field = _experiments.field " +
                                                              "            AND perms.field_value = _experiments.project " +
                                                              " ORDER  BY element_name) )";
                // _resources

                query.append("SELECT * FROM (");
                query.append(starterFields);
                query.append(", '_resources'::TEXT AS category, NULL::TEXT AS cat_id,''::TEXT AS cat_desc");
                if (includeURI) {
                    query.append(",'/experiments/' || res_map.xnat_experimentdata_id");
                    query.append(" || '/_resources/' || abst.xnat_abstractresource_id AS resource_path");
                }
                query.append(" FROM xnat_experimentdata_resource res_map");
                query.append(" JOIN xnat_abstractresource abst ON res_map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id");
                query.append(" JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id");
                query.append(" WHERE (");
                int sC = 0;
                for (XnatExperimentdata expt : _experiments) {
                    if (sC++ > 0) {
                        query.append(" OR ");
                    }
                    query.append("res_map.xnat_experimentdata_id='");
                    query.append(expt.getId());
                    query.append("'");
                }
                query.append(") ");
                query.append("  UNION ");
                query.append(starterFields);
                query.append(", 'scans'::TEXT,isd.id,isd.type");
                if (includeURI) {
                    query.append(",'/experiments/' || isd.image_session_id");
                    query.append(" || '/scans/' || isd.id");
                    query.append(" || '/_resources/' || abst.xnat_abstractresource_id AS resource_path");
                }
                query.append(" FROM xnat_imagescanData isd  ");
                query.append(" JOIN xnat_abstractresource abst ON isd.xnat_imagescandata_id=abst.xnat_imagescandata_xnat_imagescandata_id");
                query.append(" JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id");
                query.append(" WHERE (");
                sC = 0;
                for (XnatExperimentdata expt : _experiments) {
                    if (sC++ > 0) {
                        query.append(" OR ");
                    }
                    query.append("isd.image_session_id='");
                    query.append(expt.getId());
                    query.append("'");
                }
                query.append(") ");
                query.append(" UNION ");
                query.append(starterFields);
                query.append(", 'reconstructions'::TEXT,recon.id,recon.type");
                if (includeURI) {
                    query.append(",'/experiments/' || recon.image_session_id");
                    query.append(" || '/reconstructions/' || recon.id || '/out'");
                    query.append(" || '/_resources/' || abst.xnat_abstractresource_id AS resource_path");
                }
                query.append(" FROM xnat_reconstructedimagedata recon");
                query.append(" JOIN recon_out_resource map ON recon.xnat_reconstructedimagedata_id=map.xnat_reconstructedimagedata_xnat_reconstructedimagedata_id");
                query.append(" JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id");
                query.append(" JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id");
                query.append(" WHERE (");
                sC = 0;
                for (XnatExperimentdata expt : _experiments) {
                    if (sC++ > 0) {
                        query.append(" OR ");
                    }
                    query.append("image_session_id='");
                    query.append(expt.getId());
                    query.append("'");
                }
                query.append(") ");
                query.append(" UNION ");
                query.append(starterFields);
                query.append(", 'assessors'::TEXT,iad.id,xes.singular");
                if (includeURI) {
                    query.append(",'/experiments/' || iad.imagesession_id");
                    query.append(" || '/assessors/' || iad.id || '/out'");
                    query.append(" || '/_resources/' || abst.xnat_abstractresource_id AS resource_path");
                }
                query.append(" FROM ").append(assessorIdsUserCanAccessSelect).append(" iad");
                query.append(" JOIN img_assessor_out_resource map ON iad.id=map.xnat_imageassessordata_id");
                query.append(" JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id");
                query.append(" JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id");
                query.append(" LEFT JOIN xdat_element_security xes ON xme.element_name=xes.element_name");
                query.append(" WHERE (");
                sC = 0;
                for (XnatExperimentdata expt : _experiments) {
                    if (sC++ > 0) {
                        query.append(" OR ");
                    }
                    query.append("iad.imagesession_id='");
                    query.append(expt.getId());
                    query.append("'");
                }
                query.append(") ");
                query.append(" UNION ");
                query.append(starterFields);
                query.append(", 'assessors'::TEXT,iad.id,xes.singular");
                if (includeURI) {
                    query.append(",'/experiments/' || iad.imagesession_id");
                    query.append(" || '/assessors/' || iad.id");
                    query.append(" || '/_resources/' || abst.xnat_abstractresource_id AS resource_path");
                }
                query.append(" FROM ").append(assessorIdsUserCanAccessSelect).append(" iad");
                query.append(" JOIN xnat_experimentdata_resource map ON iad.id=map.xnat_experimentdata_id");
                query.append(" JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id");
                query.append(" JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id");
                query.append(" LEFT JOIN xdat_element_security xes ON xme.element_name=xes.element_name");
                query.append(" WHERE (");
                sC = 0;
                for (XnatExperimentdata expt : _experiments) {
                    if (sC++ > 0) {
                        query.append(" OR ");
                    }
                    query.append("iad.imagesession_id='");
                    query.append(expt.getId());
                    query.append("'");
                }
                query.append(") ");
                query.append(") all_resources");

                if (resourceIds != null && resourceIds.size() > 0) {
                    int c = 0;
                    query.append(" WHERE ");
                    for (String resourceID : resourceIds) {
                        if (c++ > 0) {
                            query.append(" OR ");
                        }
                        if (StringUtils
                                .isNumeric(resourceID)) {
                            query.append(" (xnat_abstractresource_id=");
                            query.append(resourceID);
                            query.append(" OR label='");
                            query.append(resourceID);
                            query.append("')");
                        } else if (resourceID.equalsIgnoreCase("NULL")) {
                            query.append(" label IS NULL");
                        } else {
                            query.append(" label='");
                            query.append(resourceID);
                            query.append("'");
                        }
                    }
                }
            } else {
                _xmlPath = "xnat:experimentData/_resources/resource";
                // _resources
                query.append(starterFields);
                query.append(", '_resources'::TEXT AS category, expt.id::TEXT AS cat_id");
                query.append(", ' '::TEXT AS cat_desc");
                if (includeURI) {
                    query.append(",'/experiments/' || map.xnat_experimentdata_id");
                    query.append(" || '/_resources/' || abst.xnat_abstractresource_id AS resource_path");
                }
                query.append(" FROM xnat_experimentdata_resource map ");
                query.append(" LEFT JOIN xnat_experimentdata expt ON map.xnat_experimentdata_id=expt.id ");
                query.append(" LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id");
                query.append(" LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id");
                query.append(" WHERE (");
                int sC = 0;
                for (XnatExperimentdata expt : _experiments) {
                    if (sC++ > 0) {
                        query.append(" OR ");
                    }
                    query.append("xnat_experimentdata_id='");
                    query.append(expt.getId());
                    query.append("'");
                }
                query.append(") ");
                if (resourceIds != null && resourceIds.size() > 0) {
                    int c = 0;
                    query.append(" AND ( ");
                    for (String resourceID : resourceIds) {
                        if (c++ > 0) {
                            query.append(" OR ");
                        }
                        if (StringUtils
                                .isNumeric(resourceID)) {
                            query
                                    .append(" (map.xnat_abstractresource_xnat_abstractresource_id=");
                            query.append(resourceID);
                            query.append(" OR abst.label='");
                            query.append(resourceID);
                            query.append("')");
                        } else if (resourceID.equalsIgnoreCase("NULL")) {
                            query.append(" abst.label IS NULL");
                        } else {
                            query.append(" abst.label='");
                            query.append(resourceID);
                            query.append("'");
                        }
                    }
                    query.append(")");
                }
            }
        } else if (_subject != null) {
            _security = _subject;
            _parent = _subject;
            _xmlPath = "xnat:subjectData/_resources/resource";
            // _resources
            query.append(starterFields);
            query
                    .append(", '_resources'::TEXT AS category, NULL::TEXT AS cat_id");
            query.append(", ' '::TEXT AS cat_desc");
            if (includeURI) {
                query.append(",'/projects/' || sub.project");
                query.append(" || '/subjects/' || sub.id");
                query.append(" || '/_resources/' || abst.xnat_abstractresource_id AS resource_path");
            }
            query.append(" FROM xnat_subjectdata_resource map ");
            query.append(" LEFT JOIN xnat_subjectdata sub ON map.xnat_subjectdata_id=sub.id");
            query.append(" LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id");
            query.append(" LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id");
            query.append(" WHERE xnat_subjectdata_id='");
            query.append(_subject.getId());
            query.append("'");
            if (resourceIds != null && resourceIds.size() > 0) {
                int c = 0;
                query.append(" AND ( ");
                for (String resourceID : resourceIds) {
                    if (c++ > 0) {
                        query.append(" OR ");
                    }
                    if (StringUtils
                            .isNumeric(resourceID)) {
                        query
                                .append(" (map.xnat_abstractresource_xnat_abstractresource_id=");
                        query.append(resourceID);
                        query.append(" OR abst.label='");
                        query.append(resourceID);
                        query.append("')");
                    } else if (resourceID.equalsIgnoreCase("NULL")) {
                        query.append(" abst.label IS NULL");
                    } else {
                        query.append(" abst.label='");
                        query.append(resourceID);
                        query.append("'");
                    }
                }
                query.append(")");
            }
        } else if (_project != null) {
            _security = _project;
            _parent = _project;
            _xmlPath = "xnat:projectData/_resources/resource";
            // _resources
            query.append(starterFields);
            query
                    .append(", '_resources'::TEXT AS category, NULL::TEXT AS cat_id");
            query.append(", ' '::TEXT AS cat_desc");
            if (includeURI) {
                query.append(",'/projects/' || map.xnat_projectdata_id");
                query.append(" || '/_resources/' || abst.xnat_abstractresource_id AS resource_path");
            }
            query.append(" FROM xnat_projectdata_resource map ");
            query.append(" LEFT JOIN xnat_abstractresource abst ON map.xnat_abstractresource_xnat_abstractresource_id=abst.xnat_abstractresource_id");
            query.append(" LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id");
            query.append(" WHERE xnat_projectdata_id='");
            query.append(_project.getId());
            query.append("'");
            if (resourceIds != null && resourceIds.size() > 0) {
                int c = 0;
                query.append(" AND ( ");
                for (String resourceID : resourceIds) {
                    if (c++ > 0) {
                        query.append(" OR ");
                    }
                    if (StringUtils
                            .isNumeric(resourceID)) {
                        query
                                .append(" (map.xnat_abstractresource_xnat_abstractresource_id=");
                        query.append(resourceID);
                        query.append(" OR abst.label='");
                        query.append(resourceID);
                        query.append("')");
                    } else if (resourceID.equalsIgnoreCase("NULL")) {
                        query.append(" abst.label IS NULL");
                    } else {
                        query.append(" abst.label='");
                        query.append(resourceID);
                        query.append("'");
                    }
                }
                query.append(")");
            }
        } else {
            query.append(starterFields);
            query
                    .append(", '_resources'::TEXT AS category, NULL::TEXT AS cat_id");
            query.append(", ' '::TEXT AS cat_desc");
            query.append(" FROM xnat_abstractresource abst");
            query
                    .append(" LEFT JOIN xdat_meta_element xme ON abst.extension=xme.xdat_meta_element_id");
            query.append(" WHERE xnat_abstractresource_id IS NULL");
        }

        return XFTTable.Execute(query.toString(), getUser().getDBName(), userName);
    }

    @SuppressWarnings("Duplicates")
    protected void initializeProjectFromExperiment() throws ElementNotFoundException {
        if (_project == null) {
            if (_parent.getItem().instanceOf("xnat:experimentData")) {
                _project = ((XnatExperimentdata) _parent).getPrimaryProject(false);
            } else if (_security.getItem().instanceOf("xnat:experimentData")) {
                _project = ((XnatExperimentdata) _security).getPrimaryProject(false);
            }
        }
    }

    protected boolean validateNewResourceCatalog(final UserI user, final XnatResourcecatalog resourceCatalog) {
        final Integer xnatAbstractresourceId = resourceCatalog.getXnatAbstractresourceId();
        if (xnatAbstractresourceId == null) {
            return true;
        }
        final XnatAbstractresource existing = XnatAbstractresource.getXnatAbstractresourcesByXnatAbstractresourceId(xnatAbstractresourceId, user, false);
        if (existing != null) {
            getResponse().setStatus(Status.CLIENT_ERROR_CONFLICT, "Specified catalog already exists.");
        } else {
            getResponse().setStatus(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, "Contains erroneous generated fields (xnat_abstractresource_id).");
        }
        return false;
    }

    protected void setCatalogAttributes(final UserI user, final XnatResourcecatalog catalog) throws Exception {
        if (StringUtils.isNotBlank(getQueryVariable("description"))) {
            catalog.setDescription(getQueryVariable("description"));
        }
        if (StringUtils.isNotBlank(getQueryVariable("format"))) {
            catalog.setFormat(getQueryVariable("format"));
        }
        if (StringUtils.isNotBlank(getQueryVariable("content"))) {
            catalog.setContent(getQueryVariable("content"));
        }

        final String[] tags = getQueryVariables("tags");
        if (tags != null) {
            for (final String variable : tags) {
                if (StringUtils.isNotBlank(variable)) {
                    for (final String instance : variable.split("\\s*,\\s*")) {
                        final XnatAbstractresourceTag tag = new XnatAbstractresourceTag(user);
                        if (instance.contains("=")) {
                            final String[] atoms = instance.split("=");
                            tag.setName(atoms[0]);
                            tag.setTag(atoms[1]);
                        } else if (instance.contains(":")) {
                            final String[] atoms = instance.split(":");
                            tag.setName(atoms[0]);
                            tag.setTag(atoms[1]);
                        } else {
                            tag.setTag(instance);
                        }
                        catalog.setTags_tag(tag);
                    }
                }
            }
        }
    }

    protected XnatProjectdata getProjectFromRelatedItems() throws ElementNotFoundException {
        return getProjectFromRelatedItems(true);
    }

    protected XnatProjectdata getProjectFromRelatedItems(final boolean allowSharedProject) throws ElementNotFoundException {
        if (hasParent() && getParent().getItem().instanceOf(XnatExperimentdata.SCHEMA_ELEMENT_NAME)) {
            final XnatExperimentdata parent = (XnatExperimentdata) getParent();
            return allowSharedProject ? ObjectUtils.defaultIfNull(parent.getPrimaryProject(false), (XnatProjectdata) parent.getFirstProject()) : parent.getPrimaryProject(false);
        }
        if (hasSecurity() && getSecurity().getItem().instanceOf(XnatExperimentdata.SCHEMA_ELEMENT_NAME)) {
            final XnatExperimentdata security = (XnatExperimentdata) getSecurity();
            return allowSharedProject ? ObjectUtils.defaultIfNull(security.getPrimaryProject(false), (XnatProjectdata) security.getFirstProject()) : security.getPrimaryProject(false);
        }
        if (hasSecurity() && getSecurity().getItem().instanceOf(XnatSubjectdata.SCHEMA_ELEMENT_NAME)) {
            final XnatSubjectdata security = (XnatSubjectdata) getSecurity();
            return allowSharedProject ? ObjectUtils.defaultIfNull(security.getPrimaryProject(false), (XnatProjectdata) security.getFirstProject()) : security.getPrimaryProject(false);
        }
        if (hasSecurity() && getSecurity().getItem().instanceOf(XnatProjectdata.SCHEMA_ELEMENT_NAME)) {
            return (XnatProjectdata) getSecurity();
        }
        return null;
    }

    protected boolean hasExperiments() {
        return !_experiments.isEmpty();
    }

    protected void setExperiments(final List<XnatExperimentdata> experiments) {
        _experiments.clear();
        _experiments.addAll(experiments);
    }

    protected boolean hasAssesseds() {
        return !_assesseds.isEmpty();
    }

    @SuppressWarnings("unused")
    protected void setAssesseds(final List<XnatExperimentdata> assesseds) {
        _assesseds.clear();
        _assesseds.addAll(assesseds);
    }

    protected boolean hasReconstructions() {
        return !_reconstructions.isEmpty();
    }

    protected void setReconstructions(final List<XnatReconstructedimagedata> reconstructions) {
        _reconstructions.clear();
        _reconstructions.addAll(reconstructions);
    }

    protected boolean hasScans() {
        return !_scans.isEmpty();
    }

    protected void setScans(final List<XnatImagescandata> scans) {
        _scans.clear();
        _scans.addAll(scans);
    }

    protected boolean hasProject() {
        return _project != null;
    }

    protected boolean hasSubject() {
        return _subject != null;
    }

    protected boolean hasType() {
        return StringUtils.isNotBlank(_type);
    }

    protected boolean hasParent() {
        return _parent != null;
    }

    protected boolean hasSecurity() {
        return _security != null;
    }

    @SuppressWarnings("unused")
    protected boolean hasXmlPath() {
        return StringUtils.isNotBlank(_xmlPath);
    }

    private final List<XnatExperimentdata>         _experiments     = new ArrayList<>();
    private final List<XnatExperimentdata>         _assesseds       = new ArrayList<>();
    private final List<XnatReconstructedimagedata> _reconstructions = new ArrayList<>();
    private final List<XnatImagescandata>          _scans           = new ArrayList<>();

    private final CatalogService _catalogService;

    private XnatProjectdata _project  = null;
    private XnatSubjectdata _subject  = null;
    private ItemI           _parent   = null;
    private ItemI           _security = null;
    private String          _xmlPath  = null;

    private String          _type;
}
