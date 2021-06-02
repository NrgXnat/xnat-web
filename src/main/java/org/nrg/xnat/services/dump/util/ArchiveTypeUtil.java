package org.nrg.xnat.services.dump.util;

import java.io.File;
import java.io.IOException;

import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.xdat.model.CatCatalogI;
import org.nrg.xdat.model.XnatImageassessordataI;
import org.nrg.xdat.model.XnatImagesessiondataI;
import org.nrg.xdat.model.XnatResourcecatalogI;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xft.exception.InvalidPermissionException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.prearchive.PrearcTableBuilder;
import org.nrg.xnat.helpers.prearchive.PrearcUtils;
import org.nrg.xnat.utils.CatalogUtils;

public class ArchiveTypeUtil {

    public enum ArchiveType {
        PREARCHIVE() {
            @Override
            CatCatalogI getCatalog(XnatResourcecatalogI r) {
                CatalogUtils.CatalogData catalogData;
                try {
                    catalogData = CatalogUtils.CatalogData.getOrCreateAndClean(this.x.getPrearchivepath(), r, false, this.x.getProject()
                    );
                } catch (ServerException e) {
                    return null;
                }
                this.rootPath = catalogData.catPath;
                return catalogData.catBean;
            }

            @Override
            XnatImagesessiondataI retrieve(Env env, UserI user) throws Exception {
                String project = (String) env.attrs.get("PROJECT_ID");
                String experiment = (String) env.attrs.get("EXPT_ID");
                String timestamp = (String) env.attrs.get("TIMESTAMP");
                File sessionDIR;
                File srcXML;
                sessionDIR = PrearcUtils.getPrearcSessionDir(user, project, timestamp, experiment, false);
                srcXML = new File(sessionDIR.getAbsolutePath() + ".xml");
                XnatImagesessiondataI x = PrearcTableBuilder.parseSession(srcXML);
                this.x = x;
                return x;
            }
        },
        ARCHIVE() {
            @Override
            CatCatalogI getCatalog(XnatResourcecatalogI r) {
                this.rootPath = (new File(r.getUri())).getParent();
                CatalogUtils.CatalogData catalogData;
                try {
                    catalogData = CatalogUtils.CatalogData.getOrCreateAndClean(this.rootPath, r, true, this.x.getProject()
                    );
                } catch (ServerException e) {
                    return null;
                }
                this.rootPath = catalogData.catPath;
                return catalogData.catBean;
            }

            @Override
            XnatImagesessiondataI retrieve(Env env, UserI user) throws Exception {
                String project = (String) env.attrs.get("PROJECT_ID");
                String experiment = (String) env.attrs.get("EXPT_ID");
                XnatImagesessiondata x = (XnatImagesessiondata) XnatExperimentdata.GetExptByProjectIdentifier(project, experiment, user, false);
                if (x == null || null == x.getId()) {
                    x = (XnatImagesessiondata) XnatExperimentdata.getXnatExperimentdatasById(experiment, user, false);
                    if (x != null && !x.hasProject(project)) {
                        x = null;
                    }
                }
                if (x == null) {
                    //throw new ClientException(Status.CLIENT_ERROR_NOT_FOUND, "Experiment or project not found",  new Exception("Experiment or project not found"));
                }
                this.x = x;
                return x;
            }
        },

        UNKNOWN() {
            @Override
            CatCatalogI getCatalog(XnatResourcecatalogI r) {
                return null;
            }

            @Override
            XnatImagesessiondataI retrieve(Env env, UserI user) {
                return null;
            }
        };

        XnatImagesessiondataI x = null;
        String rootPath = null;

        /**
         * Retrieve the catalog for this resource. Additionally this also updates the
         * "rootPath" global class variable. This function is dependent on the
         * {@link XnatImageassessordataI} having been populated.
         *
         * @param r The resource catalog reference.
         * @return The catalog for the resource.
         */
        abstract CatCatalogI getCatalog(XnatResourcecatalogI r);

        /**
         * Retrieve the image session object for this session. Additionally this also updates the
         * XnatImagesessiondataI global.
         *
         * @param env  The environment object.
         * @param user The user.
         * @return The image session object.
         * @throws ClientException
         * @throws IOException
         * @throws InvalidPermissionException
         * @throws Exception
         */
        abstract XnatImagesessiondataI retrieve(Env env, UserI user) throws Exception;
    }
}
