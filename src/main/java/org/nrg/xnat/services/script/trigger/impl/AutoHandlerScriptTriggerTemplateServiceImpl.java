package org.nrg.xnat.services.script.trigger.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.nrg.automation.entities.ScriptTrigger;
import org.nrg.automation.entities.ScriptTriggerTemplate;
import org.nrg.automation.services.ScriptTriggerService;
import org.nrg.automation.services.ScriptTriggerTemplateService;
import org.nrg.framework.constants.Scope;
import org.nrg.xapi.exceptions.DataFormatException;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.InsufficientPrivilegesException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.helpers.UserHelper;
import org.nrg.xdat.security.services.UserHelperServiceI;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.script.trigger.AutoHandlerScriptTriggerTemplateService;
import org.nrg.xnat.services.script.trigger.dto.ScriptTriggerTemplateDto;
import org.nrg.xnat.services.script.trigger.utils.AutomationScriptTriggerUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;

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
	public T findScriptTriggerTemplate(UserI user, String templateId, String projectId) throws NotFoundException, InitializationException, InsufficientPrivilegesException {

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
	
	
	@Override
	public void update(UserI user, ScriptTriggerTemplate template, String templateId, HttpServletRequest request) throws InitializationException {
		try {
            if (StringUtils.isNotBlank(templateId)) {
                putTemplate(request.getContentType(), template, user);
            } else {
                throw new DataFormatException( "You must specify a template ID on the URL to PUT a template to the server.");
            }
        } catch (DataFormatException e) {
           
        }
	}

	private void putTemplate(String contentType, ScriptTriggerTemplate template, UserI user) throws DataFormatException, InitializationException {
		if(Objects.nonNull(template)) {
			throw new DataFormatException("Unable to find template parameters: no data sent?");
		}
		if(!MediaType.APPLICATION_JSON.equals(MediaType.parseMediaType(contentType)) && !MediaType.APPLICATION_FORM_URLENCODED.equals(MediaType.APPLICATION_FORM_URLENCODED)) {
			throw new DataFormatException("Usupported media type");
		}
		 ScriptTriggerTemplate found = null;
		if(MediaType.APPLICATION_FORM_URLENCODED.equals(MediaType.parseMediaType(contentType))) {
			try {
                //found =  createObjectFromFormData(ScriptTriggerTemplate.class);
            } catch (Exception e) {
                throw new InitializationException("An error occurred trying to handle the incoming form data.", e);
            }
		}else if (MediaType.APPLICATION_JSON.equals(MediaType.parseMediaType(contentType))) {
            found = template;
        }
		assert found != null;
        saveTemplate(found, user);
	}
	
	private void saveTemplate(final ScriptTriggerTemplate found, UserI user) {
        final ScriptTriggerTemplate existing = _templateService.getByName(found.getTemplateId());
        if (existing == null) {
            final Set<ScriptTrigger> triggers = saveTriggers(found.getTriggers(), user);
            if (triggers != null) {
                found.setTriggers(triggers);
            }
            _templateService.create(found);
            final String entities = Joiner.on(", ").join(found.getAssociatedEntities());
            recordAutomationEvent(found.getTemplateId(), entities, "Create", ScriptTriggerTemplate.class, user);
        } else {
            boolean isDirty = false;
            if (!existing.getTemplateId().equals(found.getTemplateId())) {
                existing.setTemplateId(found.getTemplateId());
                isDirty = true;
            }
            if (!existing.getDescription().equals(found.getDescription())) {
                existing.setDescription(found.getDescription());
                isDirty = true;
            }
            final Set<String> currentAssociates = new TreeSet<>(existing.getAssociatedEntities());
            final Set<String> proposedAssociates = new TreeSet<>(found.getAssociatedEntities());
            if (currentAssociates.size() != proposedAssociates.size() || !currentAssociates.equals(proposedAssociates)) {
                existing.setAssociatedEntities(proposedAssociates);
                isDirty = true;
            }
            final Set<ScriptTrigger> currentTriggers = new TreeSet<>(existing.getTriggers());
            final Set<ScriptTrigger> proposedTriggers = new TreeSet<>(found.getTriggers());
            if (currentTriggers.size() != proposedTriggers.size() || !currentTriggers.equals(proposedTriggers)) {
                existing.setTriggers(proposedTriggers);
                isDirty = true;
            }
            if (isDirty) {
                final Set<ScriptTrigger> triggers = saveTriggers(existing.getTriggers(), user);
                if (triggers != null) {
                    existing.setTriggers(triggers);
                }
                _templateService.update(existing);
                final String entities = Joiner.on(", ").join(existing.getAssociatedEntities());
                recordAutomationEvent(found.getTemplateId(), entities, "Update", ScriptTriggerTemplate.class, user);
            }
        }
    }
	
	  private Set<ScriptTrigger> saveTriggers(final Set<ScriptTrigger> triggers, UserI user) {
	        final Set<ScriptTrigger> persisted = new HashSet<>(triggers.size());
	        for (final ScriptTrigger trigger : triggers) {
	            final ScriptTrigger existing = _triggerService.getByTriggerId(trigger.getTriggerId());
	            if (existing == null) {
	                _triggerService.create(trigger);
	                persisted.add(trigger);
	                recordAutomationEvent(trigger.getTriggerId(), trigger.getAssociation(), "Create", ScriptTrigger.class, user);
	            } else if (existing.compareTo(trigger) != 0) {
	                if (!existing.getTriggerId().equals(trigger.getTriggerId())) {
	                    existing.setTriggerId(trigger.getTriggerId());
	                }
	                if (!existing.getDescription().equals(trigger.getDescription())) {
	                    existing.setDescription(trigger.getDescription());
	                }
	                if (!existing.getScriptId().equals(trigger.getScriptId())) {
	                    existing.setScriptId(trigger.getScriptId());
	                }
	                if (!existing.getAssociation().equals(trigger.getAssociation())) {
	                    existing.setAssociation(Scope.encode(Scope.Project, trigger.getAssociation()));
	                }
	                if (!existing.getEvent().equals(trigger.getEvent())) {
	                    existing.setEvent(trigger.getEvent());
	                }
	                _triggerService.update(existing);
	                persisted.add(existing);
	                recordAutomationEvent(existing.getTriggerId(), existing.getAssociation(), "Update", ScriptTrigger.class, user);
	            }
	        }
	        return persisted.size() > 0 ? persisted : null;
	    }
	

	private void validateAndInitialize(UserI user) throws InitializationException, InsufficientPrivilegesException, NotFoundException {
		if (getScope() == Scope.Site) {
            if (!Roles.isSiteAdmin(user)) {
                log.warn("User " + user.getLogin() + " attempted to access forbidden script trigger template resources");
                throw new InsufficientPrivilegesException("Only site admins can view or update script trigger templates for the entire site.");
            }
        } else {
            final UserHelperServiceI userHelperService = UserHelper.getUserHelperService(user);
            if (userHelperService == null) {
                log.error("An error occurred trying to retrieve the user helper service. Can't proceed with permissions check.");
                throw new InitializationException( "An error occurred trying to process your request. Please try again or, if the problem persists, contact your system administrator.");
            }
            if (!Roles.isSiteAdmin(user) || userHelperService.isOwner(getProjectId())) {
                log.warn("User " + user.getLogin() + " attempted to access forbidden script trigger template resources");
                throw new InsufficientPrivilegesException("Only site admins and project owners can view or update script trigger templates for the project " + getProjectId() + ".");
            }
        }

        // A bit much for now, but built to handle more scopes, e.g. subjects, users, etc.
        switch (getScope()) {
            case Project:
                XnatProjectdata project = XnatProjectdata.getProjectByIDorAlias(getProjectId(), user, false);
                if (project == null) {
                    throw new NotFoundException("Couldn't find the project with ID or alias " + getProjectId());
                }
                break;

            case Site:
                break;
            default:
               throw new NotFoundException("Unknown scope " + getScope());
        }

        if (log.isDebugEnabled()) {
            log.debug("Servicing script request for user " + user.getLogin());
        }
	}
	
	 private final ScriptTriggerService _triggerService;
	 private final ScriptTriggerTemplateService _templateService;

}
