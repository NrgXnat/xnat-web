package org.nrg.xnat.dto.search;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DisplayFieldReferenceIDto {

	private String id;
	private String elementName;
	private Object value;
	private boolean visible;
	private String header;
	private String type;
}
