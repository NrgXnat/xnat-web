/*
 * web: org.nrg.xnat.servlet.ArchiveServlet
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.servlet;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.beans.XnatPluginBeanManager;
import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xnat.entities.Doi;
import org.nrg.xnat.services.logging.impl.DefaultLoggingService;
import org.nrg.xnat.services.system.DoiService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

@SuppressWarnings("serial")
@Slf4j
public class DoiServlet extends HttpServlet {
    public DoiServlet() {
        _instance = this;
    }

    public static DoiServlet getInstance() {
        return _instance;
    }

    /**
     * Initializes the logging system based on the configured application settings.
     */
    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        loadProperties(config.getServletContext().getRealPath(""));
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);

        String doi = request.getPathInfo();
        if(doi!=null){
            if(StringUtils.startsWith(doi,"/")){
                doi = StringUtils.substring(doi,1);
            }
            long longDoi = Long.parseLong(doi);
            try {
                DoiService service = (DoiService) XDAT.getContextService().getBean(DoiService.class);
                final Doi doiObject = service.get(longDoi);
                switch (doiObject.getXsiType()){
                    case "xnat:projectData":
                        response.sendRedirect("/data/doi/projects/"+doiObject.getObjectId());
                        break;
                    case "xnat:subjectData":
                        response.sendRedirect("/data/doi/projects/"+doiObject.getProjectId()+"/subjects/"+doiObject.getObjectId());
                        break;
                    case "xnat:mrSessionData":
                        response.sendRedirect("/data/doi/projects/"+doiObject.getProjectId()+"/experiments/"+doiObject.getObjectId());
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid DOI type");
                }
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadProperties(final String rootPath) {
        final List<String> paths   = DefaultLoggingService.getPluginLog4jResourcePaths(XnatPluginBeanManager.scanForXnatPluginBeans());
        final Properties   results = DefaultLoggingService.reset(DefaultLoggingService.getLog4jProperties(rootPath, paths));
        log.info("Completed initial configuration for log4j from {} properties", results.size());
        if (log.isTraceEnabled()) {
            log.trace("Configuration properties:");
            try (final StringWriter writer = new StringWriter()) {
                results.store(writer, "Generated properties for XNAT log4j configuration");
                log.trace(writer.getBuffer().toString());
            } catch (IOException e) {
                log.warn("An error occurred trying to write the log4j properties", e);
            }
        }
    }

    private static DoiServlet _instance;
}
