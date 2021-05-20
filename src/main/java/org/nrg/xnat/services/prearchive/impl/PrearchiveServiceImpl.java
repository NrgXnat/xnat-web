package org.nrg.xnat.services.prearchive.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.security.PermissionsServiceImpl;
import org.nrg.xdat.security.helpers.Groups;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.dto.prearchive.PrearchiveDto;
import org.nrg.xnat.helpers.prearchive.DatabaseSession;
import org.nrg.xnat.services.prearchive.PrearchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PrearchiveServiceImpl implements PrearchiveService {
	
	@Autowired
	public PrearchiveServiceImpl(final NamedParameterJdbcTemplate template) {
		 _template = template;
		 _permissions = XDAT.getContextService().getBean(PermissionsServiceImpl.class);
	}

	@Override
	public List<PrearchiveDto>  findAllPrearchives(UserI user,String projectId) {
		boolean dataAccess = Groups.hasAllDataAccess(user);
		final List<String> projects = new ArrayList<>(StringUtils.isNotBlank(projectId) ? Arrays.asList(projectId.split("\\s*,\\s*")) : _permissions.getUserEditableProjects(user.getUsername()));
        if (dataAccess) {
            projects.add(null);
        }
        return _template.query(DatabaseSession.PROJECT.allMatchesSql(projects.toArray(new String[0])), new MapSqlParameterSource(), new PrearchiveRowMapper());
	}
	
	private static class PrearchiveRowMapper implements RowMapper<PrearchiveDto>  {
        @Override
        public PrearchiveDto mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
        	ResultSetMetaData rsmd = resultSet.getMetaData();
        	while (resultSet.next()) {
        		 return  getPreacgiveData(resultSet, rsmd);
        	}
			return null;
        }
	}
	
	private static PrearchiveDto getPreacgiveData(ResultSet resultSet, ResultSetMetaData rsmd) throws SQLException {
		PrearchiveDto prearchiveDto = new PrearchiveDto(); 
		int columnsNumber = rsmd.getColumnCount();
	for (int i = 1; i <= columnsNumber; i++) {
	     String columnName = rsmd.getColumnName(i);
		if(columnName.equalsIgnoreCase("project")) {
			prearchiveDto.setProject(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("timestamp")) {
			prearchiveDto.setTimestamp(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("lastmod")) {
			prearchiveDto.setLastmod(resultSet.getTimestamp(i));
		}else if(columnName.equalsIgnoreCase("uploaded")) {
			prearchiveDto.setUploaded(resultSet.getTimestamp(i));
		}else if(columnName.equalsIgnoreCase("scan_date")) {
			prearchiveDto.setScan_date(resultSet.getTimestamp(i));
		}else if(columnName.equalsIgnoreCase("scan_time")) {
			prearchiveDto.setScan_time(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("subject")) {
			prearchiveDto.setSubject(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("foldername")) {
			prearchiveDto.setFolderName(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("name")) {
			prearchiveDto.setName(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("tag")) {
			prearchiveDto.setTag(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("status")) {
			prearchiveDto.setStatus(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("url")) {
			prearchiveDto.setUrl(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("autoarchive")) {
			prearchiveDto.setAutoarchive(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("prevent_anon")) {
			prearchiveDto.setPrevent_anon(resultSet.getBoolean(i));
		}else if(columnName.equalsIgnoreCase("prevent_auot_commit")) {
			prearchiveDto.setPrevent_auto_commit(resultSet.getBoolean(i));
		}else if(columnName.equalsIgnoreCase("source")) {
			prearchiveDto.setSOURCE(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("visit")) {
			prearchiveDto.setVISIT(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("protocol")) {
			prearchiveDto.setPROTOCOL(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}else if(columnName.equalsIgnoreCase("timezone")) {
			prearchiveDto.setTIMEZONE(Objects.nonNull(resultSet.getString(i))?resultSet.getString(i):"");
		}
	}
	return prearchiveDto;
}

	 private final NamedParameterJdbcTemplate _template;
	 private final PermissionsServiceImpl _permissions;
}
