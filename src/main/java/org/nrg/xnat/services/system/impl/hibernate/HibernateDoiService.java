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
import org.nrg.xnat.daos.DoiDAO;
import org.nrg.xnat.entities.Doi;
import org.nrg.xnat.services.system.DoiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * {@inheritDoc}
 */
@Service
public class HibernateDoiService extends AbstractHibernateEntityService<Doi, DoiDAO> implements DoiService {
    @Override
    public List<Doi> getDois() {
        return getDao().getAllDois();
    }

    @Override
    public List<Doi> getDoisForObjectAndProjectAndType(String objectId, String projectId, String xsiType){
        return getDao().getAllDoisForObjectAndProjectAndType(objectId, projectId, xsiType);
    }

    @Override
    public List<Doi> getDoisForDoiString(String doiString){
        return getDao().getAllDoisForDoiString(doiString);
    }

    @Transactional
    @Override
    public void deleteDoi(int doiId,UserI user) throws NotFoundException, InsufficientPrivilegesException {
        Doi doi = get(doiId);
        if (!Roles.isSiteAdmin(user)) {
            throw new InsufficientPrivilegesException(user.getUsername());
        }
        getDao().delete(doi);
        return;
    }
}
