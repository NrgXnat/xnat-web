package org.nrg.xnat.customforms.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class XnatFormsIOEnv {

    private boolean siteHasProtocolsPluginDeployed;

    public XnatFormsIOEnv(boolean protocolPluginDeployed) {
        this.siteHasProtocolsPluginDeployed = protocolPluginDeployed;
    }

}
