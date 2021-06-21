package org.nrg.xnat.services.catalog.impl;

import java.util.List;
import java.util.Objects;

import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.xdat.XDAT;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.services.archive.CatalogService;
import org.nrg.xnat.services.catalog.RefreshCatalogService;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

@Service
public class RefreshCatalogServiceImpl implements  RefreshCatalogService{

	@Override
	public void createCatalogRefresh(UserI user,List<String> resources, boolean append, boolean checksum, boolean delete, boolean populateStats, List<String> options) throws ClientException, ServerException {
		_catalogService = XDAT.getContextService().getBean(CatalogService.class);
		
		loadValues(resources, append , checksum, delete , populateStats, options);
		
		_catalogService.refreshResourceCatalogs(user, _resources, _operations.toArray(new CatalogService.Operation[_operations.size()]));
	}
	
	 private void loadValues(List<String> resources, boolean append, boolean checksum, boolean delete, boolean populateStats, List<String> options) throws ClientException {
		 if(Objects.nonNull(resources)) {
			 _resources = resources;
		 }
		 if(Objects.nonNull(append)) {
			 _operations.add(CatalogService.Operation.Append);
		 }
		 if(Objects.nonNull(checksum)) {
			 _operations.add(CatalogService.Operation.Checksum);
		 }
		 if(Objects.nonNull(delete)) {
			  _operations.add(CatalogService.Operation.Delete);
		 }
		 if(Objects.nonNull(populateStats)) {
			 _operations.add(CatalogService.Operation.PopulateStats);
		 }
		 if(Objects.nonNull(options)) {
			 loadOptions(options);
		 }
		 if (_operations.contains(CatalogService.Operation.All)) {
	            _operations.clear();
	            _operations.addAll(CatalogService.Operation.ALL);
	        }
	}

	 	private void loadOptions(List<String> options) {
            if (options.contains(APPEND)) {
                _operations.add(CatalogService.Operation.Append);
            }
            if (options.contains(CHECKSUM)) {
                _operations.add(CatalogService.Operation.Checksum);
            }
            if (options.contains(DELETE)) {
                _operations.add(CatalogService.Operation.Delete);
            }
            if (options.contains(POPULATE_STATS)) {
                _operations.add(CatalogService.Operation.PopulateStats);
            }
	}

		private CatalogService _catalogService;
	    private List<String> _resources   = Lists.newArrayList();
	    private final List<CatalogService.Operation> _operations  = Lists.newArrayList();
	    private static final String APPEND = "append";
	    private static final String CHECKSUM = "checksum";
	    private static final String DELETE = "delete";
	    private static final String POPULATE_STATS = "populateStats";

}
