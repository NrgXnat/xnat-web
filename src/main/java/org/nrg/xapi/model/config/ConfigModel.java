package org.nrg.xapi.model.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigModel implements Serializable {
    private static final long serialVersionUID = 2238359642185590110L;

    private String  mode;
    private String  list;
    private boolean enabled;
    @NotNull
    private String  contents;
}
