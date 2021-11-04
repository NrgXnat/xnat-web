package org.nrg.xapi.model.xft;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DisplayFieldReference implements Serializable {
    private static final long serialVersionUID = -8091603376405476724L;

    private String  id;
    private String  elementName;
    private Object  value;
    private boolean visible;
    private String  header;
    private String  type;
}
