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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TypeDefs({ @TypeDef(name = "json", typeClass = JsonStringType.class),
		@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class) })
public class ValidationResult extends AbstractHibernateEntity {

	/**
	 * The Constant serialVersionUID.
	 */
	private static final long serialVersionUID = -7672058761367018378L;

	private boolean validData;
	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private List<Object> errors;
	
	@OneToMany(mappedBy = "result", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<DataToUpload> dataToUpload;
}
