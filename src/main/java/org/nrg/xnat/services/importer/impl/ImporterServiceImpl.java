package org.nrg.xnat.services.importer.impl;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.framework.constants.PrearchiveCode;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.file.StoredFile;
import org.nrg.xnat.helpers.prearchive.PrearcUtils;
import org.nrg.xnat.helpers.resource.XnatResourceInfo;
import org.nrg.xnat.helpers.transactions.HTTPSessionStatusManagerQueue;
import org.nrg.xnat.helpers.transactions.PersistentStatusQueueManagerI;
import org.nrg.xnat.helpers.uri.UriParserUtils.UriParser;
import org.nrg.xnat.restlet.actions.importer.ImporterHandlerA;
import org.nrg.xnat.restlet.actions.importer.ImporterNotFoundException;
import org.nrg.xnat.restlet.util.FileWriterWrapperI;
import org.nrg.xnat.restlet.util.XNATRestConstants;
import org.nrg.xnat.services.importer.ImporterService;
import org.nrg.xnat.status.StatusList;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@Slf4j
public class ImporterServiceImpl implements ImporterService {

    public static final String HTTP_SESSION_LISTENER   = "http-session-listener";
    public static final String APPLICATION_DICOM       = "application/dicom";
    public static final String APPLICATION_XMIRC       = "application/x-mirc";
    public static final String APPLICATION_XMIRC_DICOM = "application/x-mirc-dicom";
    public static final String SESSION_IMPORTER        = "SI";
    public static final String XAR_IMPORTER            = "XAR";
    public static final String GRADUAL_DICOM_IMPORTER  = "gradual-DICOM";
    public static final String DICOM_INBOX_IMPORTER    = "inbox";
    public static final String DICOM_ZIP_IMPORTER      = "DICOM-zip";

    @Override
    public List<String> importFiles(UserI user, HttpServletRequest request, XnatResourceInfo xnatResourceInfo) throws DataFormatException, ServerException, ClientException, NotFoundException {
        boolean prearchive = true;
        fw.clear();
        response = new ArrayList<>();
        fw.add(xnatResourceInfo);
        // Set the overwrite flag if we are uploading directly to the archive (prearchive_code = 1)
        String prearchive_code = (String) params.get("prearchive_code");
        if ("1".equals(prearchive_code)) { // User has selected archive option
            prearchive = false;
            params = getSelectedArchiveOption(user);
        }

        ImporterHandlerA importer = null;
        if (fw.size() == 0 && handler != null && !HANDLERS_ALLOWING_CALLS_WITHOUT_FILES.contains(handler)) {
            throw new DataFormatException("Unable to identify upload format.");
        } else if (handler != null && fw.size() == 0) {
            response = callImporter(importer, user, request);
        } else if (fw.size() > 1) {
            throw new DataFormatException("Importer is limited to one uploaded resource at a time.");
        }
        if (handler == null && xnatResourceInfo != null) {
            if (APPLICATION_DICOM.equals(request.getContentType()) || APPLICATION_XMIRC.equals(request.getContentType()) || APPLICATION_XMIRC_DICOM.equals(request.getContentType())) {
                handler = ImporterHandlerA.GRADUAL_DICOM_IMPORTER;
            }
        }

        importer = getimporter(importer, user);

        storeStatusList(importer, request);

        response = getResponseWithCallImport(importer, prearchive, xnatResourceInfo, request);

        return response;
    }


    private List<String> getResponseWithCallImport(ImporterHandlerA importer, boolean prearchive, XnatResourceInfo xnatResourceInfo, HttpServletRequest request) throws ClientException, ServerException {
        ThreadPoolExecutor importerExecutorService;
        if (httpSessionListener && async && (importerExecutorService = XDAT.getContextService().getBeanSafely("threadPoolExecutorFactoryBean", ThreadPoolExecutor.class)) != null) {
            String task = prearchive ? "prearchival" : "archival";
            importerExecutorService.submit(importer);
        } else {
            response = importer.call();
        }
        return response;
    }


    private ImporterHandlerA getimporter(ImporterHandlerA importer, UserI user) throws ServerException, ClientException, DataFormatException, NotFoundException {
        try {
            importer = ImporterHandlerA.buildImporter(handler, listenerControl, user, fw.get(0), params);
        } catch (SecurityException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            log.error("", e);
            throw new ServerException(e.getMessage(), e);
        } catch (IllegalArgumentException | NoSuchMethodException e) {
            log.error("", e);
            throw new DataFormatException(e.getMessage(), e);
        } catch (ImporterNotFoundException e) {
            log.error("", e);
            throw new NotFoundException(e.getMessage());
        }
        return importer;
    }


    private List<String> callImporter(ImporterHandlerA importer, UserI user, HttpServletRequest request) throws ClientException, ServerException, DataFormatException {
        try {
            // FileWriterWrapperI is null because no files should have been uploaded.
            importer = ImporterHandlerA.buildImporter(handler, listenerControl, user, null, params);
        } catch (Exception e) {
            log.error("", e);
            throw new ServerException(e.getMessage(), e);
        }
        storeStatusList(importer, request);

        return importer.call();
    }

    public boolean storeStatusList(final ImporterHandlerA importer, HttpServletRequest request) throws DataFormatException {
        if (httpSessionListener) {
            if (StringUtils.isEmpty(listenerControl)) {
                throw new DataFormatException(XNATRestConstants.TRANSACTION_RECORD_ID + "' is required when requesting '" + HTTP_SESSION_LISTENER + "'.");
            }
            final StatusList sq = new StatusList();
            importer.addStatusListener(sq);
            storeStatusList(listenerControl, sq, request);
        }
        return false;
    }

    private Map<String, Object> getSelectedArchiveOption(UserI user) throws DataFormatException {
        // If the overwrite flag has been set by the user, make sure it is a valid option
        if (params.containsKey("overwrite")) {
            String ow = (String) params.get("overwrite");
            if (!PrearcUtils.DELETE.equalsIgnoreCase(ow) || !PrearcUtils.APPEND.equalsIgnoreCase(ow)) {
                throw new DataFormatException("Overwrite flag was not set to a valid option. ('append' or 'delete')");
            }
            // If the overwrite flag has not been set by the user, set the flag based on
            // the project setting.
        } else {
            // Get the prearchive code for the project specified.
            XnatProjectdata proj  = XnatProjectdata.getProjectByIDorAlias((String) params.get("project"), user, true);
            PrearchiveCode  pCode = PrearchiveCode.code(proj.getArcSpecification().getPrearchiveCode());

            // If the project is set to auto archive overwrite
            if (pCode == PrearchiveCode.AutoArchiveOverwrite) {
                params.put("overwrite", PrearcUtils.DELETE);
            } else { // If the project is set to append or prearchive-only.
                params.put("overwrite", PrearcUtils.APPEND);
            }
        }
        return params;
    }

    protected void storeStatusList(final String transaction_id, final StatusList sl, HttpServletRequest request) throws IllegalArgumentException {
        retrieveSQManager(request).storeStatusQueue(transaction_id, sl);
    }

    protected PersistentStatusQueueManagerI retrieveSQManager(HttpServletRequest request) {
        return new HTTPSessionStatusManagerQueue(getHttpSession(request));
    }

    public HttpSession getHttpSession(HttpServletRequest request) {
        return request.getSession();
    }

    public void handleParam(String key, Object value, UserI user) throws ClientException, DataFormatException, NotFoundException {
        switch (key) {
            case ImporterHandlerA.IMPORT_HANDLER_ATTR:
                handler = (String) value;
                break;
            case XNATRestConstants.TRANSACTION_RECORD_ID:
                listenerControl = (String) value;
                break;
            case "src":
                fw.add(retrievePrestoreFile((String) value, user));
                async = true;
                break;
            case HTTP_SESSION_LISTENER:
                listenerControl = (String) value;
                httpSessionListener = true;
                break;
            default:
                params.put(key, value);
                break;
        }
    }

    public FileWriterWrapperI retrievePrestoreFile(final String src, UserI user) throws ClientException, DataFormatException, NotFoundException {
        //Template.MODE_STARTS_WITH =1
        //Map<String, Object> map = new UriParser("/user/cache/resources/{XNAME}/files/", Template.MODE_STARTS_WITH).readUri(src);
        Map<String, Object> map = new UriParser("/user/cache/resources/{XNAME}/files/", 1).readUri(src);

        if (!map.containsKey("XNAME") || !map.containsKey("_REMAINDER")) {
            throw new DataFormatException("src uri is invalid.", new Exception());
        }
        File f = org.nrg.xdat.security.helpers.Users.getUserCacheFile(user, (String) map.get("XNAME"), (String) map.get("_REMAINDER"));

        if (f.exists()) {
            return new StoredFile(f, true);
        } else {
            throw new NotFoundException("Unknown src file.");
        }
    }


    String                   handler             = null;
    String                   listenerControl     = null;
    boolean                  httpSessionListener = false;
    boolean                  async               = false; // when importing a previously uploaded file, no need to keep the connection open while we import
    Map<String, Object>      params              = new Hashtable<>();
    List<FileWriterWrapperI> fw                  = new ArrayList<>();
    List<String>             response            = null;
    private static final List<String> HANDLERS_ALLOWING_CALLS_WITHOUT_FILES = Lists.newArrayList();
    private static final List<String> HANDLERS_PREFERRING_PARTIAL_URI_WRAP  = Lists.newArrayList();
}
