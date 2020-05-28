/*
 * web: org.nrg.xnat.entities.HostInfo
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.entities;

import javax.persistence.Entity;

import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class CsvTemplate.
 */
@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CsvTemplate extends AbstractHibernateEntity {

	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = -5742162667606724940L;

	private String label;
	private String xsiType;
	private String user;
	private String project;
	private String template;

}
