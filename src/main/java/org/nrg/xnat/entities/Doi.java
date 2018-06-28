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

    private String doi;

    private String description;

    private String keywords;

    private String contactName;

    private String contactEmail;

    private String dataAvailability;

    private String dataUseTerms;

    private String relatedPublications;

    private String links;

    private String notes;

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


    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getDataAvailability() {
        return dataAvailability;
    }

    public void setDataAvailability(String dataAvailability) {
        this.dataAvailability = dataAvailability;
    }

    public String getDataUseTerms() {
        return dataUseTerms;
    }

    public void setDataUseTerms(String dataUseTerms) {
        this.dataUseTerms = dataUseTerms;
    }

    public String getRelatedPublications() {
        return relatedPublications;
    }

    public void setRelatedPublications(String relatedPublications) {
        this.relatedPublications = relatedPublications;
    }

    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
