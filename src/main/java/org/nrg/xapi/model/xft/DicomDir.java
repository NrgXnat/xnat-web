package org.nrg.xapi.model.xft;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DicomDir implements Serializable {
    private static final long serialVersionUID = -8756439953379130216L;

    private Long    size;
    private String  name;
    private boolean dir;
    private String  uri;
}
