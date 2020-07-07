package org.nrg.xnat.export.notifications;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xdat.turbine.utils.AdminUtils;
import org.nrg.xdat.turbine.utils.TurbineUtils;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.notifications.NotifyProjectListeners;
import org.nrg.xnat.notifications.NotifyProjectListeners.ProjectListenersI;
import org.nrg.xnat.notifications.NotifyProjectListeners.ResourceBasedProjectListeners;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mohana Ramaratnam
 *
 */
@Slf4j
public class NotifyProjectExportListeners extends NotifyProjectListeners {
    private final XnatProjectdata _project;
    private final String _template,_action;
    private final UserI _user;
    private final Map<String,Object> _params;
    private final List<String> _emails;
    private final Map<String,File> _attachments;
    private final String _type;
    private final ProjectListenersI listenersBuilder;

    public NotifyProjectExportListeners(XnatProjectdata project, String template, UserI user, Map<String,Object> params, String action, List<String> emails, String type, ProjectListenersI listeners){
        super(null,template,null,user,params,action,emails,listeners);
        this._project = project;
        this._template=template;
        this._user=user;
        if(params==null){
            this._params= Maps.newHashMap();
        }else{
            this._params=params;
        }
        this._action=action;
        this._emails=emails;
        this._type = type;
        this.listenersBuilder=(listeners!=null)?listeners:new ResourceBasedProjectListeners();

        if (_params.get("attachments") != null && _params.get("attachments") instanceof Map) {
            _attachments = Maps.newHashMap();
            try {
                _attachments.putAll((Map<String, File>) _params.get("attachments"));
            } catch (ClassCastException e) {
                log.error("",e);
            }
        } else {
            _attachments = null;
        }

    }

    public NotifyProjectExportListeners(XnatProjectdata project, String template, UserI user, Map<String,Object> params, String action, List<String> emails, String type){
        this(project,template,user,params,action,emails,type,null);
    }

    public Boolean send() throws Exception{
        try {
                _emails.add(_user.getEmail());
                _emails.add(XDAT.getSiteConfigPreferences().getAdminEmail());

            if(_emails.size()>0){
                Context context =new VelocityContext(Maps.newHashMap());


                String from = XDAT.getSiteConfigPreferences().getAdminEmail();
                context.put("projectId", _project.getId());
                context.put("user", _user);
                context.put("username", _user.getUsername());
                context.put("server", TurbineUtils.GetFullServerPath());
                context.put("siteLogoPath", XDAT.getSiteLogoPath());
                context.put("system", TurbineUtils.GetSystemName());
                context.put("admin_email", XDAT.getSiteConfigPreferences().getAdminEmail());
                context.put("contactEmail", XDAT.getNotificationsPreferences().getHelpContactInfo());
                context.put("params", _params);
                for(Map.Entry<String,Object> entry : _params.entrySet()) {
                    context.put(entry.getKey(),entry.getValue());
                }
                if(_params.get("justification")==null && _params.get("event_reason")!=null){
                    context.put("justification",_params.get("event_reason"));
                }
                String body = AdminUtils.populateVmTemplate(context, _template);

                if (_type.equalsIgnoreCase("failure")) {
                    XDAT.getMailService().sendHtmlMessage(from, _emails.toArray(new String[_emails.size()]),null,null, null, body, body, _attachments);
                } else if (_type.equalsIgnoreCase("success")) {
                    XDAT.getMailService().sendHtmlMessage(from, _emails.toArray(new String[_emails.size()]), null, body);
                }
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            log.error("", e);
            return false;
        }
    }
    

}
