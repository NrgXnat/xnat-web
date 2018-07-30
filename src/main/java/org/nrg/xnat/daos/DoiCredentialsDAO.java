/*
 * web: org.nrg.xnat.daos.HostInfoDAO
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.daos;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.nrg.framework.orm.hibernate.AbstractHibernateDAO;
import org.nrg.xnat.entities.Doi;
import org.nrg.xnat.entities.DoiCredentials;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class DoiCredentialsDAO extends AbstractHibernateDAO<DoiCredentials> {

    @Transactional
    public List<DoiCredentials> getAllDoiCredentials() {
        return findAll();
    }

    @Transactional
    public List<DoiCredentials> getDoiCredentialsForUsername(String xnatUsername){
        Criteria criteria = getCriteriaForType();
        criteria.add(Restrictions.eq("xnatUsername", xnatUsername));
        return criteria.list();
    }
}
