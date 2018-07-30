/*
 * web: org.nrg.xnat.services.system.impl.hibernate.HibernateHostInfoService
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

/*
 * 
 */
package org.nrg.xnat.services.system.impl.hibernate;

import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.exceptions.NotFoundException;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.daos.DoiCredentialsDAO;
import org.nrg.xnat.daos.DoiDAO;
import org.nrg.xnat.entities.Doi;
import org.nrg.xnat.entities.DoiCredentials;
import org.nrg.xnat.services.system.DoiCredentialsService;
import org.nrg.xnat.services.system.DoiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * {@inheritDoc}
 */
@Service
public class HibernateDoiCredentialsService extends AbstractHibernateEntityService<DoiCredentials, DoiCredentialsDAO> implements DoiCredentialsService {
    @Override
    public List<DoiCredentials> getDoiCredentials() {
        return getDao().getAllDoiCredentials();
    }

    @Override
    public List<DoiCredentials> getDoiCredentialsForUsername(String xnatUsername) {
        return getDao().getDoiCredentialsForUsername(xnatUsername);
    }

    @Transactional
    @Override
    public void deleteCredentials(int credentialsId, UserI user) throws NotFoundException, InsufficientPrivilegesException {
        DoiCredentials credentials = get(credentialsId);
        if (!Roles.isSiteAdmin(user) && !StringUtils.equals(credentials.getXnatUsername(),user.getUsername())) {
            throw new InsufficientPrivilegesException(user.getUsername());
        }
        getDao().delete(credentials);
        return;
    }

}
