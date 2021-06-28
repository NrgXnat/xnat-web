package org.nrg.xnat.extensions.util;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class XdatUserUtil implements Serializable{
	private static final long serialVersionUID = 4652298183625392051L;
	private String login;
	private String email;
	private String firstname;
	private String lastname;
	private boolean enabled;
	private boolean verified;

}
