/*
 * web: org.nrg.xnat.services.system.HostInfoService
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.services.system;

import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.framework.orm.hibernate.BaseHibernateService;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.entities.DoiCredentials;

import java.util.List;

public interface DoiCredentialsService extends BaseHibernateService<DoiCredentials> {
    List<DoiCredentials> getDoiCredentials();
    List<DoiCredentials> getDoiCredentialsForUsername(String xnatUsername);
    void deleteCredentials(int credentialsId, UserI user) throws NotFoundException, InsufficientPrivilegesException;
}
