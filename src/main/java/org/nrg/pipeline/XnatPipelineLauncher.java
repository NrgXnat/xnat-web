/*
 * web: org.nrg.pipeline.XnatPipelineLauncher
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.pipeline;

import org.apache.commons.lang3.StringUtils;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.nrg.pipeline.services.PipelineLauncherService;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.entities.AliasToken;
import org.nrg.xdat.om.WrkWorkflowdata;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.services.AliasTokenService;
import org.nrg.xdat.turbine.utils.TurbineUtils;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.turbine.utils.ArcSpecManager;
import org.nrg.xnat.utils.WorkflowUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class XnatPipelineLauncher {

    public XnatPipelineLauncher(UserI user) {
       this.pipelineLaunchParameters = PipelineLaunchParameters.builder()
               .user(user)
               .build();
       pipelineLaunchParameters.notificationEmailId(user.getEmail(), XDAT.getSiteConfigPreferences().getAdminEmail());
    }

    public XnatPipelineLauncher(final PipelineLaunchParameters pipelineLaunchParameters) {
        this.pipelineLaunchParameters = pipelineLaunchParameters;
    }


    /*
     * Use this method when you want the job to be executed after schedule
     * command gets hold of the command string. Schedule could log the string
     * into a file and/or submit to a GRID
     */

    public boolean launch() {
        return launcher.launch();
    }

    /*
     * Setting cmdPrefix to null will launch the job directly.
     */

    public boolean launch(String cmdPrefix) {
        return launcher.launch(cmdPrefix);
    }

    @Autowired
    private PipelineLauncherService launcher;
    private PipelineLaunchParameters pipelineLaunchParameters;
}