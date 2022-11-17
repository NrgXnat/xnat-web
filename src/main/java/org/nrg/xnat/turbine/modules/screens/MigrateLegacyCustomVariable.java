package org.nrg.xnat.turbine.modules.screens;

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.turbine.modules.screens.SecureScreen;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.customforms.utils.CustomFormsConstants;

public class MigrateLegacyCustomVariable extends SecureScreen {

    @Override
    protected void doBuildTemplate(RunData data, Context context) throws Exception {
        UserI user = XDAT.getUserDetails();
        if (!Roles.isSiteAdmin(user) && !Roles.checkRole(user, CustomFormsConstants.DATAFORM_MANAGER_ROLE)) {
            data.setMessage("Unauthorized: You do not have sufficient permission to access this page");
            data.setScreenTemplate("Error.vm");
            return;
        }

    }
}
