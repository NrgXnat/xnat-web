/*
 * web: org.nrg.xnat.turbine.modules.actions.ExptFileUpload
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.turbine.modules.actions;

import com.google.common.collect.ArrayListMultimap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.parser.ParameterParser;
import org.apache.velocity.context.Context;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.bean.CatCatalogBean;
import org.nrg.xdat.bean.CatCatalogTagBean;
import org.nrg.xdat.bean.base.BaseElement;
import org.nrg.xdat.bean.reader.XDATXMLReader;
import org.nrg.xdat.model.CatCatalogTagI;
import org.nrg.xdat.om.XnatAbstractresourceTag;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.turbine.modules.actions.SecureAction;
import org.nrg.xdat.turbine.utils.TurbineUtils;
import org.nrg.xft.ItemI;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.utils.FileUtils;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xft.utils.zip.TarUtils;
import org.nrg.xft.utils.zip.ZipI;
import org.nrg.xft.utils.zip.ZipUtils;
import org.nrg.xnat.services.archive.CatalogService;
import org.nrg.xnat.turbine.utils.ArcSpecManager;
import org.nrg.xnat.turbine.utils.XNATUtils;
import org.nrg.xnat.utils.WorkflowUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipOutputStream;

@Component
@Slf4j
public class ExptFileUpload extends SecureAction {
    @Override
    public void doPerform(RunData data, Context context) throws Exception {
        final ParameterParser params  = data.getParameters();
        final HttpSession     session = data.getSession();

        String uploadID = null;
        if (params.get("ID") != null && !params.get("ID").equals("")) {
            uploadID = params.get("ID");
            session.setAttribute(uploadID + "Upload", 0);
            session.setAttribute(uploadID + "Extract", 0);
            session.setAttribute(uploadID + "Analyze", 0);
        }
        if (uploadID != null) {
            session.setAttribute(uploadID + "Upload", 0);
        }
        try {
            final FileItem fileItem = params.getFileItem("image_archive");
            if (fileItem != null) {
                final Path cachePath = Paths.get(ArcSpecManager.GetInstance().getGlobalCachePath(), "user_uploads", uploadID);
                final File dir       = cachePath.toFile();

                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        log.warn("It appears that I failed to create the directory: {}. If there's some error later, this may be why.", dir);
                    }
                }

                final String filename = FilenameUtils.getName(fileItem.getName());
                log.info("Uploading file {} to folder {}", filename, dir);

                final String normalized = filename.toLowerCase();
                final String extension  = FilenameUtils.getExtension(normalized).toLowerCase();
                final String compressionMethod;
                switch (extension) {
                    case "tar.gz":
                        compressionMethod = "tgz";
                        break;

                    case "":
                        compressionMethod = "zip";
                        break;

                    default:
                        compressionMethod = extension;
                }

                if (uploadID != null) {
                    session.setAttribute(uploadID + "Upload", 100);
                }

                if (StringUtils.equalsAnyIgnoreCase(compressionMethod, "tar", "gz", "tgz", "zip", "zar")) {
                    log.debug("Extracting file: {}", filename);

                    try (final InputStream fileItemInputStream = fileItem.getInputStream()) {
                        final ZipI zipper;
                        switch (compressionMethod) {
                            case "tar":
                                zipper = new TarUtils();
                                break;
                            case "tgz":
                                zipper = new TarUtils();
                                zipper.setCompressionMethod(ZipOutputStream.DEFLATED);
                                break;
                            default:
                                zipper = new ZipUtils();
                        }

                        try {
                            zipper.extract(fileItemInputStream, cachePath.toString());
                        } catch (Throwable e1) {
                            error(e1, data);
                            session.setAttribute(uploadID + "Extract", -1);
                            session.setAttribute(uploadID + "Analyze", -1);
                            return;
                        }
                    }
                } else {
                    //PLACE UPLOADED IMAGE INTO FOLDER
                    fileItem.write(cachePath.resolve(filename).toFile());
                }

                if (uploadID != null) {
                    session.setAttribute(uploadID + "Extract", 100);
                }

                fileItem.delete();
                this.addTag(dir, (String) TurbineUtils.GetPassedParameter("tags", data));

                log.debug("File Upload Complete.");

                data.setMessage("File Uploaded.");
                context.put("search_element", TurbineUtils.GetPassedParameter("search_element", data));
                context.put("search_field", TurbineUtils.GetPassedParameter("search_field", data));
                context.put("search_value", TurbineUtils.GetPassedParameter("search_value", data));
                context.put("uploadID", uploadID);
                context.put("destination", "ExptUploadConfirm.vm");
                data.setScreenTemplate("FileUploadSummary.vm");
            }
        } catch (IOException | RuntimeException e) {
            error(e, data);
            session.setAttribute(uploadID + "Upload", -1);
            session.setAttribute(uploadID + "Extract", -1);
            session.setAttribute(uploadID + "Analyze", -1);
        }
    }

    public void addTag(File dir, String tag) {
        if (!dir.exists()) {
            return;
        }

        final File[] listFiles = dir.listFiles();
        if (ObjectUtils.isEmpty(listFiles)) {
            return;
        }

        final AtomicInteger counter = new AtomicInteger();
        Arrays.stream(listFiles).forEach(listFile -> {
            if (!listFile.isDirectory()) {
                if (listFile.getName().endsWith(".xml")) {
                    if (listFile.exists()) {
                        try (final FileInputStream fileInputStream = new FileInputStream(listFile)) {
                            final XDATXMLReader reader = new XDATXMLReader();
                            final BaseElement   base   = reader.parse(fileInputStream);

                            if (base instanceof CatCatalogBean) {
                                final CatCatalogBean    catalog    = (CatCatalogBean) base;
                                final CatCatalogTagBean catalogTag = new CatCatalogTagBean();
                                catalogTag.setTag(tag);
                                catalog.addTags_tag(catalogTag);

                                try (final FileWriter catalogWriter = new FileWriter(listFile)) {
                                    catalog.toXML(catalogWriter, true);
                                }

                                counter.incrementAndGet();
                            }
                        } catch (IOException | SAXException e) {
                            log.error("", e);
                        }
                    }
                }
            } else {
                final File[] files = listFile.listFiles();
                if (!ObjectUtils.isEmpty(files)) {
                    Arrays.stream(files).filter(file -> !file.isDirectory() && file.getName().endsWith(".xml")).forEach(xml -> {
                        try (final FileInputStream fileInputStream = new FileInputStream(xml)) {
                            final XDATXMLReader reader = new XDATXMLReader();
                            final BaseElement   base   = reader.parse(fileInputStream);
                            if (base instanceof CatCatalogBean) {
                                counter.incrementAndGet();
                            }
                        } catch (IOException | SAXException e) {
                            log.error("", e);
                        }
                    });
                }
            }
        });

        if (counter.get() == 0) {
            final CatCatalogBean catalog = new CatCatalogBean();
            Arrays.stream(listFiles).forEach(file -> XNATUtils.populateCatalogBean(catalog, "", file));

            if (tag != null) {
                final CatCatalogTagBean catalogTag = new CatCatalogTagBean();
                catalogTag.setTag(tag);
                catalog.addTags_tag(catalogTag);
            }

            final File catalogFile = new File(dir, "generated_catalog.xml");
            try (final FileWriter fileWriter = new FileWriter(catalogFile)) {
                catalog.toXML(fileWriter, true);
            } catch (IOException e) {
                log.error("", e);
            }
        }
    }

    public void doFinalize(final RunData data, final Context context) throws Exception {
        final ItemI                temp      = TurbineUtils.GetItemBySearch(data, false);
        final XnatImagesessiondata tempMR    = (XnatImagesessiondata) org.nrg.xdat.base.BaseElement.GetGeneratedItem(temp);
        final String               uploadID  = ((String) TurbineUtils.GetPassedParameter("uploadID", data));
        final Path                 cachePath = Paths.get(ArcSpecManager.GetInstance().getGlobalCachePath(), "user_uploads", uploadID);
        final File                 dir       = cachePath.toFile();
        if (dir.exists()) {
            final File[] listFiles = dir.listFiles();
            if (ObjectUtils.isEmpty(listFiles)) {
                log.info("No files found in the specified folder {}, leaving.", cachePath);
                return;
            }

            final ArrayListMultimap<File, File> catalogFiles = ArrayListMultimap.create();
            for (final File listFile : listFiles) {
                if (!listFile.isDirectory()) {
                    if (listFile.getName().endsWith(".xml") || listFile.getName().endsWith(".xcat")) {
                        catalogFiles.put(listFile, listFile);
                    }
                } else {
                    final File[] files = listFile.listFiles();
                    if (!ObjectUtils.isEmpty(files)) {
                        Arrays.stream(files).forEach(file -> {
                            if (!file.isDirectory()) {
                                if (file.getName().endsWith(".xml") || listFile.getName().endsWith(".xcat")) {
                                    catalogFiles.put(listFile, file);
                                }
                            }
                        });
                    }
                }
            }

            final AtomicInteger counter         = new AtomicInteger();
            final Path          destinationPath = Paths.get(tempMR.getRelativeArchivePath(), "UPLOADS", uploadID);
            catalogFiles.keySet().forEach(listFile -> catalogFiles.get(listFile).forEach(xml -> {
                final XDATXMLReader reader = new XDATXMLReader();
                try (final FileInputStream fileInputStream = new FileInputStream(xml)) {
                    final BaseElement base = reader.parse(fileInputStream);

                    if (base instanceof CatCatalogBean) {
                        final CatCatalogBean      catalog         = (CatCatalogBean) base;
                        final XnatResourcecatalog resourceCatalog = new XnatResourcecatalog(XDAT.getUserDetails());

                        if (StringUtils.isNotBlank(catalog.getId())) {
                            resourceCatalog.setLabel(catalog.getId());
                        } else {
                            resourceCatalog.setLabel(Calendar.getInstance().getTime().toString());
                        }

                        for (final CatCatalogTagI tag : catalog.getTags_tag()) {
                            final XnatAbstractresourceTag resourceTag = new XnatAbstractresourceTag(XDAT.getUserDetails());
                            resourceTag.setTag(tag.getTag());
                            resourceCatalog.setTags_tag(resourceTag);
                        }


                        resourceCatalog.setUri(destinationPath.resolve(listFile.getName()).resolve(xml.getName()).toString());
                        tempMR.setResources_resource(resourceCatalog);
                        counter.incrementAndGet();
                    }
                } catch (Throwable e) {
                    log.error("", e);
                }
            }));

            if (counter.get() > 0) {
                final PersistentWorkflowI workflow = WorkflowUtils.buildOpenWorkflow(XDAT.getUserDetails(), tempMR.getItem(), newEventInstance(data, EventUtils.CATEGORY.DATA, EventUtils.ADDED_MISC_FILES));
                final EventMetaI          event    = workflow.buildEvent();

                try {
                    final File dest = new File(FileUtils.AppendRootPath(tempMR.getArchiveRootPath(), destinationPath.toString()));
                    FileUtils.MoveDir(dir, dest, true);
                    FileUtils.DeleteFile(dir);

                    SaveItemHelper.authorizedSave(tempMR, XDAT.getUserDetails(), false, false, event);
                    PersistentWorkflowUtils.complete(workflow, event);
                    data.setMessage("Files successfully uploaded.");
                } catch (Exception e) {
                    PersistentWorkflowUtils.fail(workflow, event);
                    error(e, data);
                }

                if (tempMR.getProject() != null) {
                    data.getParameters().setString("project", tempMR.getProject());
                }

                final CatalogService catalogService = XDAT.getContextService().getBean(CatalogService.class);
                catalogService.refreshResourceCatalog(XDAT.getUserDetails(), "/archive/experiments/" + tempMR.getId());

                if (TurbineUtils.HasPassedParameter("destination", data)) {
                    this.redirectToReportScreen((String) TurbineUtils.GetPassedParameter("destination", data), tempMR.getItem(), data);
                } else {
                    this.redirectToReportScreen(tempMR.getItem(), data);
                }
            }
        }
    }
}
