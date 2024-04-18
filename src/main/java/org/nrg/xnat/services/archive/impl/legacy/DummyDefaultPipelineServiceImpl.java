package org.nrg.xnat.services.archive.impl.legacy;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.nrg.xdat.turbine.utils.TurbineUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.preferences.PipelinePreferences;
import org.nrg.xnat.restlet.util.XNATRestConstants;
import org.nrg.xnat.services.archive.PipelineService;
import org.nrg.xnat.turbine.utils.ArcSpecManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DummyDefaultPipelineServiceImpl implements PipelineService {

    @Autowired
    public DummyDefaultPipelineServiceImpl(final PipelinePreferences preferences, final SiteConfigPreferences siteConfigPreferences) {
        _preferences = preferences;
        _siteConfigPreferences = siteConfigPreferences;
    }

    @Override
    public boolean launchAutoRun(final XnatExperimentdata experiment, final boolean suppressEmail, final UserI user) {
        return true;
    }

    @Override
    public boolean launchAutoRun(final XnatExperimentdata experiment, final boolean suppressEmail, final UserI user, final boolean waitFor) {
        return true;
    }

    private final PipelinePreferences      _preferences;
    private final SiteConfigPreferences    _siteConfigPreferences;
}