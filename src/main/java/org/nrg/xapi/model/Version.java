package org.nrg.xapi.model;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.nrg.xapi.model.xft.DisplayFieldReference;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Version implements Serializable {
	private static final long           serialVersionUID = -2361324617208025730L;
	private String                      name;
	private String                      orderBy;
	private String                      lightColor;
	private String darkColor;
	private String                      defaultSortOrder;
	private List<DisplayFieldReference> fields;
}
