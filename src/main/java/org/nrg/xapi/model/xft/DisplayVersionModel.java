package org.nrg.xapi.model.xft;

import lombok.*;
import org.nrg.xapi.model.Version;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DisplayVersionModel implements Serializable {
    private static final long serialVersionUID = -8652407066308164382L;

    private String        elementName;
    private List<Version> versions;
}
