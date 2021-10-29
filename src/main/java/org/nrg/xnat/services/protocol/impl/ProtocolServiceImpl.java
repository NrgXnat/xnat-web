package org.nrg.xnat.services.protocol.impl;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.model.XnatDatatypeprotocolI;
import org.nrg.xdat.model.XnatProjectdataI;
import org.nrg.xdat.om.XnatAbstractprotocol;
import org.nrg.xdat.om.XnatDatatypeprotocol;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.base.BaseXnatAbstractprotocol;
import org.nrg.xdat.om.base.BaseXnatProjectdata;
import org.nrg.xdat.security.ElementSecurity;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.services.DataTypeAwareEventService;
import org.nrg.xft.ItemI;
import org.nrg.xft.db.MaterializedView;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.XftItemEvent;
import org.nrg.xft.event.persist.PersistentWorkflowI;
import org.nrg.xft.event.persist.PersistentWorkflowUtils;
import org.nrg.xft.exception.XftItemException;
import org.nrg.xft.schema.Wrappers.GenericWrapper.GenericWrapperElement;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xnat.model.util.XnatEventUtil;
import org.nrg.xnat.services.protocol.ProtocolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProtocolServiceImpl implements ProtocolService {

	@Autowired
	public ProtocolServiceImpl(final DataTypeAwareEventService eventService) {
		_eventService = eventService;
	}

	@Override
	public XnatDatatypeprotocolI findByProjectIdAndProtocolId(UserI user, String projectId, String protocolId, String dataType, XnatEventUtil event ) throws NotFoundException {
		
		validate(projectId, protocolId, dataType);
		
		XnatDatatypeprotocolI xnatDatatypeprotocol = null;
		final XnatProjectdataI project = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);;
		final XnatDatatypeprotocolI protocol = (XnatDatatypeprotocol) XnatAbstractprotocol.getXnatAbstractprotocolsById(protocolId, user, true);
		try {
			 xnatDatatypeprotocol = ObjectUtils.defaultIfNull(protocol, getXnatDatatypeprotocol(user, dataType, project, protocol,protocolId,event ));
		} catch (Exception e) {
			e.printStackTrace();
		}
        if (xnatDatatypeprotocol == null) {
        	throw new NotFoundException("Unable to find the specified protocol by data type or protocol ID");
        }
		return xnatDatatypeprotocol;
	}
	
	
	@Override
	public void delete(UserI user, String projectId, String protocolId, String dataType, XnatEventUtil event) throws InitializationException, NotFoundException {
		
		validate(projectId, protocolId, dataType);
		
		final XnatProjectdataI project = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
		
		final XnatDatatypeprotocolI protocol = (XnatDatatypeprotocol) XnatAbstractprotocol.getXnatAbstractprotocolsById(protocolId, user, true);
		try {
	            final PersistentWorkflowI workflow          = PersistentWorkflowUtils.getOrCreateWorkflowData(null, user, ((ItemI)project).getItem(), XnatEventUtil.newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN, "Deleted event data-type protocol.", event));
	           
	            final boolean             isProjectSpecific = XnatDatatypeprotocol.isProjectSpecific(protocol);
	            
	            if (isProjectSpecific) {

//	                XDAT.triggerXftItemEvent(project, XftItemEvent.UPDATE);
					_eventService.triggerXftItemEvent((BaseElement) project, XftItemEvent.UPDATE);



	            } else {
//	                XDAT.triggerXftItemEvent(XnatDatatypeprotocol.SCHEMA_ELEMENT_NAME, protocolId, XftItemEvent.DELETE);
					_eventService.triggerXftItemEvent(XnatDatatypeprotocol.SCHEMA_ELEMENT_NAME, protocolId, XftItemEvent.DELETE);
	            }
	            try {
	                SaveItemHelper.authorizedDelete(((ItemI)protocol).getItem().getCurrentDBVersion(), user, workflow.buildEvent());
	                PersistentWorkflowUtils.complete(workflow, workflow.buildEvent());
	            } catch (Exception e) {
	                PersistentWorkflowUtils.fail(workflow, workflow.buildEvent());
	                throw e;
	            }
	            Users.clearCache(user);
	            MaterializedView.deleteByUser(user);
	        } catch (Exception e) {
	            log.error("", e);
	            throw new InitializationException(e.getMessage());
	        }
	}
	
	
	@Override
	public XnatDatatypeprotocolI update(UserI user, String projectId, String protocolId, String dataType, String gender, XnatDatatypeprotocolI protocol, XnatEventUtil event) throws InitializationException, NotFoundException {
		
		validate(projectId, protocolId, dataType);

		final XnatProjectdataI project = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);

		if (Objects.isNull(project)) {
			throw new NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME);
		}
		final XnatDatatypeprotocolI existingProtocol = (XnatDatatypeprotocol) XnatAbstractprotocol.getXnatAbstractprotocolsById(protocolId, user, true);
		try {
			if (StringUtils.isBlank(((BaseXnatAbstractprotocol)protocol).getProject())) {
				((BaseXnatAbstractprotocol)protocol).setProperty("xnat_projectdata_id", project.getId());
			}
			if (StringUtils.isBlank(protocol.getId())) {
				protocol.setId(existingProtocol == null ? protocol.getDataType() : existingProtocol.getId());
			}
			if (StringUtils.isNotBlank(gender)) {
				((BaseXnatAbstractprotocol)protocol).setProperty("xnat:subjectData/demographics[@xsi:type=xnat:demographicData]/gender", gender);
			}

			protocol = updateProtocol(protocol, user, existingProtocol, project, event);

		} catch (Exception e) {
			log.error("An unknown error occurred trying to store the protocol", e);
			throw new InitializationException(e.getMessage());

		}
		return protocol;
	}
	
	
	private void validate(String projectId, String protocolId, String dataType) throws NotFoundException {
		if(StringUtils.isBlank(projectId)) {
			throw new  NotFoundException(XnatProjectdata.SCHEMA_ELEMENT_NAME) ;
		}
		if (StringUtils.isBlank(protocolId)) {
			throw new  NotFoundException(XnatDatatypeprotocol.SCHEMA_ELEMENT_NAME) ;
		}
		if (StringUtils.isBlank(dataType)) {
			throw new  NotFoundException("Datatype wasn't found") ;
		}
	}


	private XnatDatatypeprotocolI updateProtocol(XnatDatatypeprotocolI protocol, UserI user, XnatDatatypeprotocolI existingProtocol, XnatProjectdataI project, XnatEventUtil event) throws Exception {
		 final PersistentWorkflowI workflow = PersistentWorkflowUtils.getOrCreateWorkflowData(null, user, ((ItemI)project).getItem(), XnatEventUtil.newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN, "Modified event data-type protocol.", event));
         try {
             SaveItemHelper.authorizedSave((ItemI) protocol, user, false, true, workflow.buildEvent());

             // We only need to trigger an event if something changed in how the field definitions relate to projects.
             final Changed changed = hasChanged(protocol, existingProtocol);
             switch (changed) {
                 case ProjectSpecific:
                     // If the added groups were all project specific, we just need to update that project.
//                     XDAT.triggerXftItemEvent(project, XftItemEvent.UPDATE);
					 _eventService.triggerXftItemEvent((BaseElement) project, XftItemEvent.UPDATE);

                     break;

                 case SiteWide:
//                     XDAT.triggerXftItemEvent(protocol, existingProtocol == null ? XftItemEvent.CREATE : XftItemEvent.UPDATE);
					 _eventService.triggerXftItemEvent((BaseElement) protocol, existingProtocol == null ? XftItemEvent.CREATE : XftItemEvent.UPDATE);
                     break;

                 case Unchanged:
                	 log.info("Something happened with the protocol {} but it doesn't seem to have changed.", StringUtils.defaultIfBlank(protocol.getDescription(), protocol.getName()));
                     break;
             }
//             XDAT.triggerXftItemEvent(project, XftItemEvent.UPDATE);
			 _eventService.triggerXftItemEvent((BaseElement) project, XftItemEvent.UPDATE);
             PersistentWorkflowUtils.complete(workflow, workflow.buildEvent());
             MaterializedView.deleteByUser(user);
             return protocol;
         } catch (Exception e) {
             PersistentWorkflowUtils.fail(workflow, workflow.buildEvent());
             throw e;
         }
	}


	@Nonnull
    private XnatDatatypeprotocolI getXnatDatatypeprotocol(final UserI user, final String dataType, XnatProjectdataI project, XnatDatatypeprotocolI protocol2, String protocolId, XnatEventUtil event ) throws Exception {
        final XnatDatatypeprotocolI existing = (XnatDatatypeprotocol) ((BaseXnatProjectdata)project).getProtocolByDataType(dataType);
        if (existing != null) {
            return existing;
        }

        final ElementSecurity elementSecurity = ElementSecurity.GetElementSecurity(dataType);
        if (elementSecurity == null) {
            throw new XftItemException("Tried to get the element security instance for the data type \"" + dataType + "\" but it wasn't found. This means something's very wrong.");
        }
        final GenericWrapperElement element  = GenericWrapperElement.GetElement(dataType);
        final XnatDatatypeprotocolI  protocol = new XnatDatatypeprotocol(user);
		((BaseXnatAbstractprotocol)protocol).setProperty("xnat_projectdata_id", project.getId());
        protocol.setDataType(element.getXSIType());
        protocol.setId(project.getId() + "_" + element.getSQLName());
        if (StringUtils.isBlank((String) ((BaseXnatAbstractprotocol)protocol).getProperty("name"))) {
			((BaseXnatAbstractprotocol)protocol).setProperty("name", elementSecurity.getPluralDescription());
        }
        if (StringUtils.equals(XnatDatatypeprotocol.SCHEMA_ELEMENT_NAME, protocol.getXSIType())) {
			((BaseXnatAbstractprotocol)protocol).setProperty("xnat:datatypeProtocol/definitions/definition[ID=default]/data-type", ((BaseXnatAbstractprotocol)protocol).getProperty("data-type"));
			((BaseXnatAbstractprotocol)protocol).setProperty("xnat:datatypeProtocol/definitions/definition[ID=default]/project-specific", "false");
        }

        final PersistentWorkflowI workflow = PersistentWorkflowUtils.getOrCreateWorkflowData(null, user, ((ItemI)project).getItem(), XnatEventUtil.newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN, "Modified event data-type protocol.", event));
        try {
            SaveItemHelper.authorizedSave((ItemI) protocol, user, false, false, workflow.buildEvent());
            if (XnatDatatypeprotocol.isProjectSpecific(protocol)) {
//                XDAT.triggerXftItemEvent(project, XftItemEvent.UPDATE);
				_eventService.triggerXftItemEvent((BaseElement) project, XftItemEvent.UPDATE);
            } else {
//                XDAT.triggerXftItemEvent(XnatDatatypeprotocol.SCHEMA_ELEMENT_NAME, protocolId, XftItemEvent.CREATE);
				_eventService.triggerXftItemEvent(XnatDatatypeprotocol.SCHEMA_ELEMENT_NAME, protocolId, XftItemEvent.CREATE);
            }
            PersistentWorkflowUtils.complete(workflow, workflow.buildEvent());
            return protocol;
        } catch (Exception e) {
            PersistentWorkflowUtils.fail(workflow, workflow.buildEvent());
            throw e;
        }
    }

	
	  private Changed hasChanged(final XnatDatatypeprotocolI protocol, final XnatDatatypeprotocolI existingProtocol) {
	        // Determine whether the updated protocol is project specific.
	        final boolean projectSpecific = XnatDatatypeprotocol.isProjectSpecific(protocol);

	        // If the "existing" protocol doesn't exist, this is a new protocol and the effect is whatever
	        // the protocol is as far as project specific vs site wide.
	        if (existingProtocol == null) {
	            return projectSpecific ? Changed.ProjectSpecific : Changed.SiteWide;
	        }

	        // If the state of the protocol changed from project specific to site wide or vice versa,
	        // then it's a site-wide change, since changing from site wide to project specific means
	        // other projects need to update to remove the now non-site-wide protocol.
	        if (XnatDatatypeprotocol.isProjectSpecific(existingProtocol) != projectSpecific) {
	            return Changed.SiteWide;
	        }

	        // Otherwise we just see if the existing and updated protocols are the same. If so, we need to update to whatever
	        // scope the protocol is set for.
	        return protocol.equals(existingProtocol) ? Changed.Unchanged : projectSpecific ? Changed.ProjectSpecific : Changed.SiteWide;
	    }
	
	
	enum Changed {
        Unchanged,
        ProjectSpecific,
        SiteWide
    }
	private final DataTypeAwareEventService _eventService;

}
