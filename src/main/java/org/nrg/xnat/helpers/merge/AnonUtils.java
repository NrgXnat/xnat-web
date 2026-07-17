/*
 * web: org.nrg.xnat.helpers.merge.AnonUtils
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.helpers.merge;

import org.nrg.config.entities.Configuration;
import org.nrg.config.exceptions.ConfigServiceException;

import java.util.List;

/**
 * Defines the interface for managing XNAT's DICOM anonymization scripts.
 */
public interface AnonUtils {
    Configuration getSiteWideScriptConfiguration();

    Configuration getProjectScriptConfiguration(String projectId);

    boolean isSiteWideScriptEnabled();

    boolean isProjectScriptEnabled(String projectId);

    List<Configuration> getAllScripts();

    List<Configuration> getAllScripts(String projectId);

    String getStudyScript(String studyId) throws ConfigServiceException;

    String getProjectScript(String projectId) throws ConfigServiceException;

    void setProjectScript(String login, String script, String projectId) throws ConfigServiceException;

    String getSiteWideScript() throws ConfigServiceException;

    /**
     * Sets the site-wide anonymization script and/or its enabled state as a single operation. This is the
     * canonical write path for the site-wide settings: it writes the config service copy (the copy that is
     * actually applied to incoming DICOM) with the submitted user, preserves or sets the enabled status
     * explicitly, invalidates the local script cache, and mirrors the values to the site-config preferences
     * so that preference consumers and other nodes (via the preference events) stay consistent.
     *
     * @param login  The user setting the script.
     * @param script The new script contents, or null to leave the script unchanged.
     * @param enable The new enabled state, or null to preserve the current state.
     */
    void setSiteWideSettings(String login, String script, Boolean enable) throws ConfigServiceException;

    /**
     * Equivalent to {@link #setSiteWideSettings(String, String, Boolean)} with the current enabled state
     * preserved.
     */
    void setSiteWideScript(String login, String script) throws ConfigServiceException;

    void enableSiteWide(String login) throws ConfigServiceException;

    void enableProjectSpecific(String login, String projectId) throws ConfigServiceException;

    void disableSiteWide(String login) throws ConfigServiceException;

    void disableProjectSpecific(String login, String projectId) throws ConfigServiceException;

    void disableStudy(String login, final String studyId) throws ConfigServiceException;
}
