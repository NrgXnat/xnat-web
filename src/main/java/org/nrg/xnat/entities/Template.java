/*
 * web: org.nrg.xnat.entities.HostInfo
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */
package org.nrg.xnat.entities;

import javax.persistence.Column;

import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class Template.
 */
//@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
//@Table
public class Template extends AbstractHibernateEntity {

	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = -7198554882365445210L;

	@Column(unique = true)
	private String label;
	@Column(unique = true)
	private String xsiType;
	@Column
	private String user;
	@Column
	private String lastModified;
	@Column
	private String project;

}
