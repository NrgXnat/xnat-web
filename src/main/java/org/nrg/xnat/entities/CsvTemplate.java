/*
 * web: org.nrg.xnat.entities.HostInfo
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnat.entities;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
//@TypeDefs({ @TypeDef(name = "json", typeClass = JsonStringType.class),
//    @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class) })
public class CsvTemplate extends AbstractHibernateEntity {

	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = -5742162667606724940L;

	private String _label;

	private String _xsiType;

	private String _user;

	private String _project;

//    @Type(type = "jsonb")
//    @Column(columnDefinition = "jsonb")

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "csv_template_data")
	private List<String> _template;

}
