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
import org.nrg.xnat.helpers.uri.archive.DoiCreationHelper;

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

    /** The DOI identifier. */
    private String doi;

    /** The data type of the data object this DOI maps to. */
    private String xsiType;

    /** The ID of the DOI Credentials object used to create the DOI. */
    private Long issuerId;

    /** The username of the XNAT user who created the DOI. */
    private String xnatUsername;

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

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public Long getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(Long issuerId) {
        this.issuerId = issuerId;
    }

    public String getXnatUsername() {
        return xnatUsername;
    }

    public void setXnatUsername(String xnatUsername) {
        this.xnatUsername = xnatUsername;
    }

    public Doi() {
    	super();
    }

    public Doi(DoiCreationHelper helper) {
        super();
        this.projectId = helper.getProjectId();
        this.objectId = helper.getObjectId();
        this.doi = helper.getDoi();
        this.xsiType = helper.getXsiType();
        this.issuerId = helper.getIssuerId();
        this.xnatUsername = helper.getXnatUsername();
    }
}
