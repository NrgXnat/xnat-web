package org.nrg.xnat.features.util;

import java.io.Serializable;
import java.util.Collection;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeatureUserRoleUtil implements Serializable{
	private static final long serialVersionUID = -7234120257486845113L;
	private String id;
	private String name;
	private boolean isRole;
	private Collection<String> features;
	private Collection<String> blocked;
	
}
