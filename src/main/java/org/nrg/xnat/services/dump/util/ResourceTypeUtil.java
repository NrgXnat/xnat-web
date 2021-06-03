package org.nrg.xnat.services.dump.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.nrg.action.ClientException;
import org.nrg.xdat.model.CatCatalogI;
import org.nrg.xdat.model.CatEntryI;
import org.nrg.xdat.model.XnatImageassessordataI;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.model.XnatImagesessiondataI;
import org.nrg.xdat.model.XnatReconstructedimagedataI;
import org.nrg.xdat.model.XnatResourcecatalogI;
import org.nrg.xft.exception.InvalidPermissionException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.uri.URIManager;

import org.nrg.xnat.utils.CatalogUtils;
import org.nrg.xnat.utils.CatalogUtils.CatEntryFilterI;

import com.google.common.collect.Lists;

public class ResourceTypeUtil {
	
	private static List<String> imageTypes = new ArrayList<>();
	 //private static String dumpType;
	 
	public ResourceTypeUtil(List<String> imageTypes, String dumpType) {
		ResourceTypeUtil.imageTypes = imageTypes;
		//ResourceTypeUtil.dumpType = dumpType;
	}
	   public enum ResourceType {
	        SCAN {
	            Iterable<File> getFiles(Env env, UserI user, CatFilterWithPath filter, int enough) throws Exception {
	                final XnatImagesessiondataI x = env.a.retrieve(env, user);
	                final List<File> files = new ArrayList<>();
	                final Object scanID = env.attrs.get(URIManager.SCAN_ID);
	                for (final XnatImagescandataI scan : x.getScans_scan()) {
	                    if (null == scanID || scanID.equals(scan.getId())) {
	                        final List<XnatResourcecatalogI> resources = scan.getFile();
	                        files.addAll(findMatchingFile(env, resources, filter, enough));
	                        if (files.size() >= enough) {
	                            return files;
	                        }
	                    }
	                }
	                return files;
	            }
	        },

	        ASSESSOR {
	            Iterable<File> getFiles(Env env, UserI user, CatFilterWithPath filter, int enough) throws Exception {
	                final XnatImagesessiondataI x = env.a.retrieve(env, user);
	                final Object id = env.attrs.get(URIManager.SCAN_ID);
	                final List<File> files = new ArrayList<>();
	                for (XnatImageassessordataI assessor : x.getAssessors_assessor()) {
	                    if (null == id || id.equals(assessor.getId())) {
	                        final List<XnatResourcecatalogI> resources = assessor.getResources_resource();
	                        files.addAll(findMatchingFile(env, resources, filter, enough));
	                        if (files.size() >= enough) {
	                            return files;
	                        }
	                        final List<XnatResourcecatalogI> in_resources = assessor.getIn_file();
	                        files.addAll(findMatchingFile(env, in_resources, filter, enough));
	                        if (files.size() >= enough) {
	                            return files;
	                        }
	                        final List<XnatResourcecatalogI> out_resources = assessor.getOut_file();
	                        files.addAll(findMatchingFile(env, out_resources, filter, enough));
	                        if (files.size() >= enough) {
	                            return files;
	                        }
	                    }
	                }
	                return files;
	            }
	        },

	        RECON {
	            Iterable<File> getFiles(Env env, UserI user, CatFilterWithPath filter, int enough) throws Exception {
	                final XnatImagesessiondataI x = env.a.retrieve(env, user);
	                final Object id = env.attrs.get(URIManager.SCAN_ID);
	                final Collection<File> files = new ArrayList<>();
	                for (XnatReconstructedimagedataI recon : x.getReconstructions_reconstructedimage()) {
	                    if (null == id || id.equals(recon.getId())) {
	                        List<XnatResourcecatalogI> in_resources = recon.getIn_file();
	                        files.addAll(findMatchingFile(env, in_resources, filter, enough));
	                        if (files.size() >= enough) {
	                            return files;
	                        }
	                        List<XnatResourcecatalogI> out_resources = recon.getOut_file();
	                        files.addAll(findMatchingFile(env, out_resources, filter, enough));
	                        if (files.size() >= enough) {
	                            return files;
	                        }
	                    }
	                }
	                return files;
	            }
	        },

	        UNKNOWN {
	            Iterable<File> getFiles(Env env, UserI user, CatFilterWithPath filter, int enough) {
	                return Collections.emptyList();
	            }
	        };

	        List<File> findMatchingFile(final Env env, final List<XnatResourcecatalogI> resources, final CatFilterWithPath filter, final int enough) {
	            final List<File> files = Lists.newArrayList();
	            for (XnatResourcecatalogI resource : resources) {
	                final String type = resource.getLabel();
	                if (imageTypes.contains(type)) {
	                    final CatCatalogI catalog = env.a.getCatalog(resource);
	                    final String project = env.getProject(env);
	                    filter.setPath(env.a.rootPath);
	                    for (CatEntryI match : CatalogUtils.getEntriesByFilter(catalog, filter)) {
	                        File f = CatalogUtils.getFile(match, env.a.rootPath, project);
	                        if (f != null) files.add(f);
	                        if (files.size() >= enough) {
	                            return files;
	                        }
	                    }
	                }
	            }
	            return files;
	        }

	        /**
	         * Retrieve the DICOM files at this resource level.
	         *
	         * @param env    The environment object.
	         * @param user   The user.
	         * @param filter The filter.
	         * @param enough How many files is enough?
	         * @return A collection of matching files.
	         * @throws ClientException
	         * @throws IOException
	         * @throws InvalidPermissionException
	         * @throws Exception
	         */
	        abstract Iterable<File> getFiles(Env env, UserI user, CatFilterWithPath filter, int enough) throws Exception;
	    }
	   
	   static abstract class CatFilterWithPath implements CatEntryFilterI {
	        String path;

	        public void setPath(String path) {
	            path = path;
	        }

	        public abstract boolean accept(CatEntryI entry);
	    }
	   

		 final static CatFilterWithPath alwaysCatWithPath = new CatFilterWithPath() {
		        {
		            setPath(null);
		        }
	
		        public boolean accept(CatEntryI entry) {
		            return true;
		        }
		    };
}
