package org.nrg.xnat.turbine.modules.screens;

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.nrg.xdat.turbine.modules.screens.SecureScreen;
import org.nrg.xdat.turbine.utils.TurbineUtils;

public class UserCacheExplorer extends SecureScreen {
    /* (non-Javadoc)
     * @see org.apache.turbine.modules.screens.VelocitySecureScreen#doBuildTemplate(org.apache.turbine.util.RunData, org.apache.velocity.context.Context)
     */
    @Override
    protected void doBuildTemplate(RunData data, Context context) throws Exception {
        if (data.getParameters().containsKey("project")) {
            context.put("project", TurbineUtils.GetPassedParameter("project", data));
        }
        if (data.getParameters().containsKey("subject")) {
            context.put("subject", TurbineUtils.GetPassedParameter("subject", data));
        }
        if (data.getParameters().containsKey("experiment")) {
            context.put("experiment", TurbineUtils.GetPassedParameter("experiment", data));
        }
    }
}
