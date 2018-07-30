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
 * The Class DoiCredentials.
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"id"}))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "nrg")
public class DoiCredentials extends AbstractHibernateEntity {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1264374836830855705L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String xnatUsername;

    private String issuerLabel;

    private String issuerDescription;

    private String issuerLogin;

    private String issuerSite;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIssuerLabel() {
        return issuerLabel;
    }

    public void setIssuerLabel(String issuerLabel) {
        this.issuerLabel = issuerLabel;
    }

    public String getIssuerDescription() {
        return issuerDescription;
    }

    public void setIssuerDescription(String issuerDescription) {
        this.issuerDescription = issuerDescription;
    }

    public String getIssuerLogin() {
        return issuerLogin;
    }

    public void setIssuerLogin(String issuerLogin) {
        this.issuerLogin = issuerLogin;
    }

    public String getXnatUsername() {
        return xnatUsername;
    }

    public void setXnatUsername(String xnatUsername) {
        this.xnatUsername = xnatUsername;
    }

    public String getIssuerSite() {
        return issuerSite;
    }

    public void setIssuerSite(String issuerSite) {
        this.issuerSite = issuerSite;
    }

    /**
     * Instantiates a new host info.
     */
    public DoiCredentials() {
    	super();
    }


}
