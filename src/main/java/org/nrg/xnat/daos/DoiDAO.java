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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@Repository
public class DoiDAO extends AbstractHibernateDAO<Doi> {

    @Transactional
    public List<Doi> getAllDois() {
        return findAll();
    }

    @Transactional
    public List<Doi> getAllDoisForObjectAndProject(String objectId, String projectId) {
        final Criteria criteria = getSession().createCriteria(getParameterizedType());
        criteria.add(Restrictions.eq("objectId", objectId));
        criteria.add(Restrictions.eq("projectId", projectId));
        return criteria.list();
    }

    @Transactional
    public List<Doi> getAllDoisForDoiString(String doiString) {
        final Criteria criteria = getSession().createCriteria(getParameterizedType());
        criteria.add(Restrictions.eq("doi", doiString));
        return criteria.list();
    }
}
