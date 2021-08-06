package org.nrg.xnat.services.script.trigger.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.nrg.automation.entities.ScriptTrigger;
import org.nrg.automation.entities.ScriptTriggerTemplate;
import org.nrg.automation.services.ScriptTriggerService;
import org.nrg.automation.services.ScriptTriggerTemplateService;
import org.nrg.framework.constants.Scope;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.helpers.UserHelper;
import org.nrg.xdat.security.services.UserHelperServiceI;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.script.trigger.AutoHandlerScriptTriggerTemplateService;
import org.nrg.xnat.services.script.trigger.dto.ScriptTriggerTemplateDto;
import org.nrg.xnat.services.script.trigger.utils.AutomationScriptTriggerUtils;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@Service
@Slf4j
public class AutoHandlerScriptTriggerTemplateServiceImpl<T> extends AutomationScriptTriggerUtils implements AutoHandlerScriptTriggerTemplateService  {
	
	public AutoHandlerScriptTriggerTemplateServiceImpl(final ScriptTriggerService triggerService,final ScriptTriggerTemplateService templateService) {
		 this._triggerService = triggerService;
	     this._templateService = templateService;
	}
	
	@Override
	public T findScriptTriggerTemplate(UserI user, String templateId, String projectId) throws NotFoundException {

		validateAndInitialize(user);
		
		if (StringUtils.isBlank(templateId)) {
            // List the available templates (that method figures out if it's site-wide or for a project.
            return listTemplates();
        }
        // If there was a template ID, then list the triggers for that template ID.
            return listTriggers(templateId);
	}
	
	@SuppressWarnings("unchecked")
	private T listTemplates() {
		List<ScriptTriggerTemplateDto> scriptTriggerTemplates = new ArrayList<>();
        final List<ScriptTriggerTemplate> templates = StringUtils.isBlank(getProjectId()) ? _templateService.getAll() : _templateService.getTemplatesForEntity(getProjectId());
        for (final ScriptTriggerTemplate template : templates) {
        	scriptTriggerTemplates.add(ScriptTriggerTemplateDto.builder()
        			.name(template.getTemplateId())
        			.decription(template.getDescription())
        			.build());
        }
		return (T) scriptTriggerTemplates;
    }
	
	@SuppressWarnings("unchecked")
	private T listTriggers(String templateId) throws NotFoundException {
		List<ScriptTriggerTemplateDto> scriptTriggerTemplateDtos = new ArrayList<>();
		ScriptTriggerTemplate template = _templateService.getByName(templateId);
		if (template == null) {
			throw new NotFoundException("Couldn't find a template with ID " + templateId);
		}

		for (final ScriptTrigger trigger : template.getTriggers()) {
			scriptTriggerTemplateDtos.add(ScriptTriggerTemplateDto.builder()
					.name(trigger.getTriggerId())
					.scriptId(trigger.getScriptId())
					.dataType(trigger.getAssociation())
					.event(trigger.getEvent())
					.decription(trigger.getDescription())
					.build());
		}
		return (T) scriptTriggerTemplateDtos;
	}

	private void validateAndInitialize(UserI user) {
		if (getScope() == Scope.Site) {
            if (!Roles.isSiteAdmin(user)) {
                log.warn("User " + user.getLogin() + " attempted to access forbidden script trigger template resources");
                //throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN, "Only site admins can view or update script trigger templates for the entire site.");
            }
        } else {
            final UserHelperServiceI userHelperService = UserHelper.getUserHelperService(user);
            if (userHelperService == null) {
                log.error("An error occurred trying to retrieve the user helper service. Can't proceed with permissions check.");
                //throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "An error occurred trying to process your request. Please try again or, if the problem persists, contact your system administrator.");
            }
            if (!Roles.isSiteAdmin(user) || userHelperService.isOwner(getProjectId())) {
                log.warn("User " + user.getLogin() + " attempted to access forbidden script trigger template resources");
                //throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN, "Only site admins and project owners can view or update script trigger templates for the project " + getProjectId() + ".");
            }
        }

        // A bit much for now, but built to handle more scopes, e.g. subjects, users, etc.
        switch (getScope()) {
            case Project:
                XnatProjectdata project = XnatProjectdata.getProjectByIDorAlias(getProjectId(), user, false);
                if (project == null) {
                    //throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Couldn't find the project with ID or alias " + getProjectId());
                }
                break;

            case Site:
                break;
            default:
               //throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Unknown scope " + getScope());
        }

        if (log.isDebugEnabled()) {
            log.debug("Servicing script request for user " + user.getLogin());
        }
	}
	
	 private final ScriptTriggerService _triggerService;
	 private final ScriptTriggerTemplateService _templateService;

}
