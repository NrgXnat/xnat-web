/*
 * web: org.nrg.xnat.entities.HostInfo
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;

import javax.persistence.*;

/**
 * The Class Doi.
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"id"}))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "nrg")
public class Doi extends AbstractHibernateEntity {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1264374836830855705L;

	/** The unique data object identifier. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The project ID. */
    private String projectId;

    /** The ID of the XNAT data object. */
    private String objectId;

    /** The data type of the data object this DOI maps to. */
    private String xsiType;

    public long getId() {
        return id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getXsiType() {
        return xsiType;
    }

    public void setXsiType(String xsiType) {
        this.xsiType = xsiType;
    }

    /**
     * Instantiates a new host info.
     */
    public Doi() {
    	super();
    }


}
