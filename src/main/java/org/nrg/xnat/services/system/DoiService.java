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
import org.nrg.xnat.entities.Doi;

import java.util.List;

/**
 * Provides information about the current host.
 */
public interface DoiService extends BaseHibernateService<Doi> {
    List<Doi> getDois();
    List<Doi> getDoisForObjectAndProject(String objectId, String projectId);
    List<Doi> getDoisForDoiString(String doiString);
    void deleteDoi(int doi,UserI user) throws NotFoundException, InsufficientPrivilegesException;
}
