package org.nrg.xapi.rest.dicomweb.populate.xft;

import org.nrg.dicom.mizer.objects.DicomObjectVisitor;
import org.nrg.xapi.model.dicomweb.DicomObjectFactory;
import org.nrg.xapi.model.dicomweb.DicomObjectI;
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
import org.nrg.xnat.daos.DicomInstanceDAO;
import org.nrg.xnat.entities.DicomFrame;
import org.nrg.xnat.entities.DicomInstance;
import org.nrg.xnat.services.archive.CatalogService;
import org.nrg.xnat.utils.CatalogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class XftPopulator implements PopulatorI {
    private UserI user;
    private UserManagementServiceI userManagementService;
    private static final Logger _log = LoggerFactory.getLogger(XftSearchEngine.class);
    private CatalogService catalogService;
    private DicomInstanceDAO dicomInstanceDAO;

    @Autowired
    public XftPopulator(final UserManagementServiceI userManagementService, final CatalogService catalogService, DicomInstanceDAO dicomInstanceDAO) {

        this.catalogService = catalogService;
        this.userManagementService = userManagementService;
        this.dicomInstanceDAO = dicomInstanceDAO;
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

            List<XnatImagescandataI> scans = session.getScans_scan();
            _log.debug( "Session {} has {} scans.", session.getLabel(), scans.size());
            for (XnatImagescandataI scan : scans) {
                long imagescandata_id = scan.getXnatImagescandataId();
                for( XnatAbstractresourceI resource: scan.getFile()) {
                    if( XnatResourcecatalog.class.isInstance( resource)) {
                        XnatResourcecatalog catResource = (XnatResourcecatalog) resource;
                        if( ("RAW".equals( catResource.getContent()) || "secondary".equals( catResource.getContent())) && "DICOM".equals( catResource.getFormat())) {
                            CatCatalogBean catalog1 = CatalogUtils.getCatalog(null, catResource, null);
                            File catalogFile = CatalogUtils.getCatalogFile( session.getArchiveRootPath(), catResource);
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
                                            processFile( imagescandata_id, file);
                                        }
                                    }
                                }
                            }
                        }
                    }
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

    public void processFile( long imagescandata_id, File file) throws IOException {
        DicomObjectI d = DicomObjectFactory.create(file, false);
        dicomInstanceDAO.saveDicomObject( imagescandata_id, d);
    }

}
