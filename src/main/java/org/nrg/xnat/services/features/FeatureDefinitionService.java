package org.nrg.xnat.services.features;

import java.sql.SQLException;

import org.nrg.xft.exception.DBPoolException;
import org.nrg.xft.security.UserI;

public interface FeatureDefinitionService<T> {

	T findAll(UserI user, String [] tags, String type, String group) throws SQLException, DBPoolException;
	
	void create(UserI user);
	
}
