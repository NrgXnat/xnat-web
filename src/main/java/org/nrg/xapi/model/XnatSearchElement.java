package org.nrg.xapi.model;

import lombok.*;
import org.nrg.xapi.model.xft.DisplayVersionModel;

import java.io.Serializable;

// TODOR2X: What is the difference between this class and SearchElement?
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class XnatSearchElement implements Serializable {
    private static final long serialVersionUID = -7765541182699651606L;

    private String              summary;
    private String              fieldId;
    private String              header;
    private Boolean             requiresValue;
    private String              elementName;
    private String              type;
    private String              description;
    private Integer             source;
    private DisplayVersionModel displayVersion;
}
