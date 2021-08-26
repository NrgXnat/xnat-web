package org.nrg.xapi.rest.dicomweb.populate.xft;

import org.nrg.xapi.model.dicomweb.DicomImageObject;
import org.nrg.xapi.model.dicomweb.DicomObjectFactory;
import org.nrg.xapi.rest.dicomweb.populate.PopulatorI;
import org.nrg.xapi.rest.dicomweb.search.xftItem.XftSearchEngine;
import org.nrg.xdat.bean.CatCatalogBean;
import org.nrg.xdat.bean.CatDcmcatalogBean;
import org.nrg.xdat.model.CatDcmentryI;
import org.nrg.xdat.model.CatEntryI;
import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.ItemI;
import org.nrg.xft.collections.ItemCollection;
import org.nrg.xft.search.CriteriaCollection;
import org.nrg.xft.search.ItemSearch;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.archive.CatalogService;
import org.nrg.xnat.utils.CatalogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Populator that populates the modified XNAT tables and assumes instance-level metadata is in scan catalog.
 */
public class XftPopulatorClassic implements PopulatorI {
    private UserI user;
    private UserManagementServiceI userManagementService;
    private static final Logger _log = LoggerFactory.getLogger(XftSearchEngine.class);
    private final CatalogService catalogService;
    private final DicomObjectFactory dicomObjectFactory;

    public XftPopulatorClassic(final UserManagementServiceI userManagementService, final CatalogService catalogService, final DicomObjectFactory dicomObjectFactory) {
        this.catalogService = catalogService;
        this.userManagementService = userManagementService;
        this.dicomObjectFactory = dicomObjectFactory;
        // need to get the authenticated user here....
        try {
            this.user = userManagementService.getUser( "admin");
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        } catch (UserInitException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void populate(String project) throws Exception {
        ItemCollection ic = getImageSessionsForProject( project, user);
        _log.debug("Found {} sessions.", ic.size());
        for( ItemI item: ic.getItems()) {
            XnatImagesessiondata session = new XnatImagesessiondata(item);

            List<XnatImagescandata> scans = session.getScans_scan().stream()
                    .filter(XnatImagescandata.class::isInstance)
                    .map(XnatImagescandata.class::cast)
                    .collect(Collectors.toList());

            _log.debug( "Session {} has {} scans.", session.getLabel(), scans.size());
            for (XnatImagescandata scan : scans) {
                Optional<File> file = getOneDicomFile( scan, session.getArchiveRootPath());
                if( file.isPresent()) {
                    processFile( file.get(), scan);
                }
                else {
                    _log.warn(String.format("No DICOM file found in project %s, session %s, scan %s", project, session.getLabel(), scan.getId()));
                }
            }
        }
    }

    private ItemCollection getImageSessionsForProject(String project, UserI user) throws Exception {
        CriteriaCollection cc = new CriteriaCollection("AND");

        cc.addClause("xnat:imageSessionData/project", project);
        ItemCollection ic = ItemSearch.GetItems( "xnat:imageSessionData", cc, user, false);
        return ic;
    }

    private Optional<File> getOneDicomFile(XnatImagescandataI scandata, String archiveRootPath) {
        for( XnatAbstractresourceI resource: scandata.getFile()) {
            if( XnatResourcecatalog.class.isInstance( resource)) {
                XnatResourcecatalog catResource = (XnatResourcecatalog) resource;
                if( ("RAW".equals( catResource.getContent()) || "secondary".equals( catResource.getContent())) && "DICOM".equals( catResource.getFormat())) {
                    CatCatalogBean catalog1 = CatalogUtils.getCatalog(null, catResource, null);
                    File catalogFile = CatalogUtils.getCatalogFile( archiveRootPath, catResource);
                    String scanRootPath = catalogFile.getParentFile().getAbsolutePath();
                    if( CatDcmcatalogBean.class.isInstance( catalog1)) {
                        CatDcmcatalogBean dcmcatalog = (CatDcmcatalogBean) catalog1;
                        for( CatEntryI entry: CatalogUtils.getEntriesByFilter(dcmcatalog, new CatalogUtils.CatEntryFilterI() {
                            @Override
                            public boolean accept(CatEntryI entry) {
                                return true;
                            }
                        })) {
                            CatDcmentryI dcmentry = (CatDcmentryI) entry;
                            if( dcmentry != null) {
                                File file = CatalogUtils.getFile( dcmentry, scanRootPath, null);
                                if( file != null) {
                                    return Optional.of(file);
                                }
                            }
                        }
                    }
                }
            }
        }
        return Optional.ofNullable( null);
    }

    private void processFile( File file, XnatImagescandata scan) throws Exception {
        DicomImageObject d = dicomObjectFactory.createDicomObject( file, false);
        scan.setModality( d.getModality());
        scan.save(user, false, false, null);
    }

}
