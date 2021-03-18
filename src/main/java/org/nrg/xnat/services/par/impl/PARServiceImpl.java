package org.nrg.xnat.services.par.impl;

import java.util.Hashtable;

import org.nrg.xapi.exceptions.InitializationException;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.XFTTable;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.par.PARService;
import org.nrg.xnat.turbine.utils.ProjectAccessRequest;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PARServiceImpl implements PARService {

	@Override
	public void getParList(UserI user) throws InitializationException {
		final Hashtable<String, Object> params = new Hashtable<>();
		try {
			final XFTTable table = XFTTable.Execute(String.format(PAR_QUERY, user.getEmail().toLowerCase()),
					user.getDBName(), user.getLogin());

			if (table != null) {
				params.put("totalRecords", table.size());
			}

			//return representTable(table, overrideVariant(variant), params);

		} catch (Exception e) {
			log.error("An error occurred attempting to access the project invitations for user " + user.getLogin(), e);
			throw new InitializationException("An error occurred attempting to access the project invitations.");
		}
	}

	@Override
	public void getParResourceByParId(UserI user, String parId) {
		 par = ProjectAccessRequest.RequestPARByGUID(parId, user);
		 XFTTable table = new XFTTable();
	        table.initTable(new String[]{"id", "proj_id", "create_date", "level"});
	        Hashtable<String, Object> params = new Hashtable<>();
	        try {
	            for (final ProjectAccessRequest par : ProjectAccessRequest.RequestPARsByUserEmail(user.getEmail(), user)) {
	                final Object[] row = new Object[4];
	                row[0] = par.getRequestId();
	                row[1] = par.getProjectId();
	                row[2] = par.getCreateDate();
	                row[3] = par.getLevel();
	                table.rows().add(row);
	            }

	        } catch (Exception e) {
	            log.error("Error retrieving PAR " + par.getRequestId(), e);
	        }

	        //return representTable(table, overrideVariant(variant), params);
	}

	@Override
	public void getProjectParListByProjectId(UserI user, String projectId) {
		XnatProjectdata proj=null;
		proj = XnatProjectdata.getProjectByIDorAlias(projectId, user, false);
		XFTTable table = new XFTTable();
		Hashtable<String,Object> params= new Hashtable<>();
		if (ProjectAccessRequest.isParTableCreated()) {
			try {
				table = XFTTable.Execute( PROJECT_PAR_QUERY  + proj.getId() + "'", user.getDBName(), user.getLogin());

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			table = new XFTTable();
			String[] headers = { "par_id", "proj_id", "level", "create_date",
					"email", "login", "secondary_id", "approved",
					"approval_date" };
			table.initTable(headers);
		}

		//MediaType mt = overrideVariant(variant);

		//return representTable(table, mt, params);
	}
	
	 private ProjectAccessRequest par;
	private static final String PAR_QUERY = "SELECT par.par_id,par.proj_id,par.level,par.create_date,u.login, u.firstname, u.lastname,p.secondary_id,p.name,p.id,SUBSTRING(p.description,0,300) as description,pi.firstname || ' ' || pi.lastname FROM xs_par_table par LEFT JOIN xnat_projectData p ON par.proj_id=p.id LEFT JOIN xnat_investigatordata pi ON p.pi_xnat_investigatordata_id=pi.xnat_investigatordata_id LEFT JOIN xdat_user u ON par.approver_id=u.xdat_user_id WHERE LOWER(par.email)='%s' AND approval_date IS NULL";
	private static final String PROJECT_PAR_QUERY = "SELECT par.par_id,par.proj_id,par.level,par.create_date,par.email,u.login,p.secondary_id,par.approved, par.approval_date FROM xs_par_table par LEFT JOIN xnat_projectData p ON par.proj_id=p.id LEFT JOIN xdat_user u ON par.approver_id=u.xdat_user_id WHERE par.proj_id='";
}
