package org.nrg.xnat.services.catalog;

import java.util.List;

import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.xft.security.UserI;

public interface RefreshCatalogService {

	void createCatalogRefresh(UserI user, List<String> resources, boolean append, boolean checksum, boolean delete, boolean populateStats, List<String> options) throws ClientException, ServerException;

}
