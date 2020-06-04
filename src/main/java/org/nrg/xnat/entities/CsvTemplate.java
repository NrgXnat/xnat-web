/*
 * web: org.nrg.xnat.entities.HostInfo
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.entities;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * The Class CsvTemplate.
 */
@Table
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Access(AccessType.FIELD)
@Accessors(prefix = "_")
@EqualsAndHashCode(callSuper = true)
public class CsvTemplate extends AbstractHibernateEntity {

	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = -5742162667606724940L;

//	@Column(name = "label")
	private String _label;
//	@Column(name = "xsiType")
	private String _xsiType;
//	@Column(name = "user")
	private String _user;
//	@Column(name = "project")
	private String _project;

	@Column(columnDefinition = "TEXT")
	@Lob
	private String _template;

}
