package org.nrg.xapi.extensions;

import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.rest.AbstractXapiProjectRestController;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xnat.services.extensions.TriageApprovalSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Api("XNAT Triage Approval Management API")
@XapiRestController
@ResponseBody
@Slf4j
public class TriageApprovalApi  extends AbstractXapiProjectRestController {
	@Autowired
    public TriageApprovalApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder, final TriageApprovalSerivce triageApprovalSerivce) {
        super(userManagementService, roleHolder);
        _triageApprovalSerivce = triageApprovalSerivce;
    }
	
	
	private final TriageApprovalSerivce _triageApprovalSerivce;
}
