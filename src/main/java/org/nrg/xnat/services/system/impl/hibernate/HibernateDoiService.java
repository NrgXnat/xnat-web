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

import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
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
}
