package org.nrg.xnat.services.projects.impl;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.om.base.BaseXnatProjectdata;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xft.XFTItem;
import org.nrg.xft.event.EventDetails;
import org.nrg.xft.event.EventUtils;
import org.nrg.xft.event.EventUtils.TYPE;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.XftStringUtils;
import org.nrg.xnat.services.archive.impl.legacy.AbstractXftServiceImpl;
import org.nrg.xnat.services.projects.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ProjectServiceImpl extends AbstractXftServiceImpl implements ProjectService {
    @Autowired
    public ProjectServiceImpl(final NamedParameterJdbcTemplate template) {
        super(template);
    }

    @Override
    public List<XnatProjectdata> getAll(final UserI user) {
        return XnatProjectdata.getAllXnatProjectdatas(user, false);
    }

    @Override
    public XnatProjectdata findById(final UserI user, final String projectId) {
        if (Objects.nonNull(projectId)) {
            return XnatProjectdata.getXnatProjectdatasById(projectId, user, false);
        } else {
            throw new NullPointerException("ProjectId is Null");
        }
    }

    @Override
    public XnatProjectdata create(final UserI user, final XnatProjectdata project) {
        log.debug("User {} is creating the project {}", user.getUsername(), project.getId());
        XFTItem item;
        try {
            item = project.getItem();
            	
            if (item == null) {
                String xsiType = this.getQueryVariable("xsiType");
                if (xsiType != null) {
                    item = XFTItem.NewItem(xsiType, user);
                }
            }
            if (item == null) {
                //this.getResponse().setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, "Need POST Contents");
                //return;
            }

            boolean allowDataDeletion = false;
            if (this.getQueryVariable("allowDataDeletion") != null && this.getQueryVariable("allowDataDeletion").equalsIgnoreCase("true")) {
                allowDataDeletion = true;
            }
            
            if (item.instanceOf("xnat:projectData")) {
                XnatProjectdata proj = new XnatProjectdata(item);

                if (StringUtils.isBlank(proj.getId())) {
                    //this.getResponse().setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, "Requires XNAT ProjectData ID");
                    //return;
                }

                if (!XftStringUtils.isValidId(proj.getId())) {
                    //this.getResponse().setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED, "Invalid character in project ID.");
                    //return;
                }

                if (item.getCurrentDBVersion() == null) {
                    if (XDAT.getSiteConfigPreferences().getUiAllowNonAdminProjectCreation() || Roles.isSiteAdmin(user)) {
                        final XnatProjectdata saved = BaseXnatProjectdata.createProject(proj, user, allowDataDeletion, false, newEventInstance(EventUtils.CATEGORY.PROJECT_ADMIN), getQueryVariable("accessibility"));
                        //returnSuccessfulCreateFromList(saved.getId());
                    } else {
                       // getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, "User account doesn't have permission to edit this project.");
                    }
                } else {
                    //getResponse().setStatus(Status.CLIENT_ERROR_CONFLICT, "Project already exists.");
                }
            }
            
        }catch (Exception e) {
			// TODO: handle exception
		}
		return XnatProjectdata.getXnatProjectdatasById(project.getId(), user, false);
    }
    

    private EventDetails newEventInstance(EventUtils.CATEGORY cat) {
    	return EventUtils.newEventInstance(cat, getEventType(), getAction(), getReason(), getComment());
	}

	private String getComment() {
		return null;
	}

	private String getReason() {
		return null;
	}

	private String getAction() {
		return null;
	}

	private TYPE getEventType() {
		 final String id = null;
				 //getQueryVariable(EventUtils.EVENT_TYPE);
	        if (id != null) {
	            return EventUtils.getType(id, EventUtils.TYPE.WEB_SERVICE);
	        } else {
	            return EventUtils.TYPE.WEB_SERVICE;
	        }
	}

	private String getQueryVariable(String string) {
		return null;
	}

	@Override
    public XnatProjectdata update(final UserI user, final XnatProjectdata project) {
        log.debug("User {} is updating the project {}", user.getUsername(), project.getId());
        return null;
    }

    @Override
    public void deleteById(final UserI user, final String projectId) {
        delete(user, findById(user, projectId));
    }

    @Override
    public void delete(final UserI user, final XnatProjectdata project) {
        log.debug("User {} is deleting the project {}", user.getUsername(), project.getId());
    }
}
