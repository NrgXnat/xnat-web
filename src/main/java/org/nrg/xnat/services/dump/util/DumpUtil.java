package org.nrg.xnat.services.dump.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.DicomObjectToStringParam;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.io.StopTagInputHandler;
import org.dcm4che2.util.TagUtils;
import org.nrg.action.ClientException;
import org.nrg.action.ServerException;
import org.nrg.xdat.model.CatCatalogI;
import org.nrg.xdat.model.CatEntryI;
import org.nrg.xdat.model.XnatImageassessordataI;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.model.XnatImagesessiondataI;
import org.nrg.xdat.model.XnatReconstructedimagedataI;
import org.nrg.xdat.model.XnatResourcecatalogI;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xft.exception.InvalidPermissionException;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.helpers.dicom.DicomSummary;
import org.nrg.xnat.helpers.prearchive.PrearcTableBuilder;
import org.nrg.xnat.helpers.prearchive.PrearcUtils;
import org.nrg.xnat.helpers.uri.URIManager;
import org.nrg.xnat.utils.CatalogUtils;
import org.nrg.xnat.utils.CatalogUtils.CatEntryFilterI;
import org.restlet.util.Template;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;

public class DumpUtil {
	/** The files. */
    private  Iterable<File> files; // path to the DICOM file
    /** The fields. */
    private final Map<Integer,Set<String>> fields;
    
    private String file; // path to the DICOM file
    
	private static List<String> imageTypes = new ArrayList<>();
    private static final int MAXFILENUMBER=10000;
    
    ListMultimap<String, DicomSummary> map = Multimaps.newListMultimap(
  		  new TreeMap<String, Collection<DicomSummary>>(),
  		  new Supplier<List<DicomSummary>>() {
  		    public List<DicomSummary> get() {
  		      return Lists.newArrayList();
  		    }
  		  });
	public DumpUtil(Iterable<File> files, Map<Integer, Set<String>> fields2) {
    	this.files = files;
        this.fields = ImmutableMap.copyOf(fields2);
	}
	
	public DumpUtil(String file, Map<Integer, Set<String>> fields2) {
    	this.file = file;
        this.fields = ImmutableMap.copyOf(fields2);
	}
	
	 public  enum HeaderType {
	        FILE("/prearchive/projects/{PROJECT_ID}/{TIMESTAMP}/{EXPT_ID}/scans/{SCAN_ID}/resources/DICOM/files/{FILENAME}",
	                "/archive/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{EXPT_ID}/scans/{SCAN_ID}/resources/DICOM/files/{FILENAME}",
	                "/archive/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{EXPT_ID}/assessors/{SCAN_ID}/resources/DICOM/files/{FILENAME}",
	                "/archive/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{EXPT_ID}/recons/{SCAN_ID}/resources/DICOM/files/{FILENAME}",
	                "/prearchive/projects/{PROJECT_ID}/{TIMESTAMP}/{EXPT_ID}/scans/{SCAN_ID}/resources/DICOM/files/{FILENAME}"
	        ) {
	            private static final String FILENAME_PARAM = "FILENAME";

	            @Override
	            CatFilterWithPath getFilter(final Env env, final UserI user) {
	                final Object filename = env.attrs.get(FILENAME_PARAM);
	                final String project = env.getProject(env);
	                return new CatFilterWithPath() {
	                    public boolean accept(CatEntryI entry) {
	                        final File f = CatalogUtils.getFile(entry, path, project);
	                        return f != null && f.getName().equals(filename);
	                    }
	                };
	            }
	        },

	        SCAN("/archive/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{EXPT_ID}/scans/{SCAN_ID}",
	                "/archive/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{EXPT_ID}/assessors/{SCAN_ID}",
	                "/archive/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{EXPT_ID}/recons/{SCAN_ID}",
	                "/archive/projects/{PROJECT_ID}/experiments/{EXPT_ID}/scans/{SCAN_ID}",
	                "/archive/projects/{PROJECT_ID}/experiments/{EXPT_ID}/assessors/{SCAN_ID}",
	                "/archive/projects/{PROJECT_ID}/experiments/{EXPT_ID}/recons/{SCAN_ID}",
	                "/prearchive/projects/{PROJECT_ID}/{TIMESTAMP}/{EXPT_ID}/scans/{SCAN_ID}"
	        ),

	        SESSION("/archive/projects/{PROJECT_ID}/subjects/{SUBJECT_ID}/experiments/{EXPT_ID}",
	                "/archive/projects/{PROJECT_ID}/experiments/{EXPT_ID}",
	                "/prearchive/projects/{PROJECT_ID}/{TIMESTAMP}/{EXPT_ID}"
	        ),

	        UNKNOWN() {
	            @Override
	            public String retrieve(Env env, UserI user) {
	                return null;
	            }
	        };

	        private final ImmutableMap<Template, ResourceType> templates;

	        HeaderType(final String... templates) {
	            // Convert the provided string templates to Template objects
	            final ImmutableMap.Builder<Template, ResourceType> builder = ImmutableMap.builder();
	            for (final String st : templates) {
	                final Template t = new Template(st, Template.MODE_STARTS_WITH);
	                final ResourceType r;
	                if (st.contains("scans")) {
	                    r = ResourceType.SCAN;
	                } else if (st.contains("assessors")) {
	                    r = ResourceType.ASSESSOR;
	                } else if (st.contains("recons")) {
	                    r = ResourceType.RECON;
	                } else {
	                    r = ResourceType.UNKNOWN;
	                }
	                builder.put(t, r);
	            }
	            this.templates = builder.build();
	        }


	        /**
	         * The URI templates associated with type
	         *
	         * @return The available templates.
	         */
	        final List<Template> getTemplates() {
	            return Lists.newArrayList(templates.keySet());
	        }

	        /**
	         * Based on the matching template output the correct resource type
	         *
	         * @param matchingTemplate The template to match.
	         * @return The resource type matching the submitted template.
	         */
	        final ResourceType getResourceType(final Template matchingTemplate) {
	            final ResourceType r = templates.get(matchingTemplate);
	            return null == r ? ResourceType.UNKNOWN : r;
	        }

	        /**
	         * Returns the filter used to determine whether to use a provided catalog entry.
	         * Default implementation always passes.
	         *
	         * @param env  The environment object.
	         * @param user The user.
	         * @return The catalog filter.
	         */
	        CatFilterWithPath getFilter(Env env, UserI user) {
	            return alwaysCatWithPath;
	        }

	        /**
	         * Retrieve the file path to the first matching file.
	         * <p/>
	         * Returns null if no DICOM file is found.
	         *
	         * @param env  The environment object.
	         * @param user The user.
	         * @return The file path to the first matching file.
	         * @throws ClientException
	         * @throws IOException
	         * @throws InvalidPermissionException
	         * @throws Exception
	         */
	        public String retrieve(final Env env, final UserI user) throws Exception {
	            for (final File f : env.r.getFiles(env, user, getFilter(env, user), 1)) {
	                if (null != f) {
	                    return f.getAbsolutePath();
	                }
	            }
	            return null;
	        }

			public Iterable<File> retrieveAll(Env env, UserI user, List<String> imagetypes) throws Exception {
				imageTypes = imagetypes;
				final Iterable<File> matches = env.r.getFiles(env, user,getFilter(env, user), MAXFILENUMBER);
	            return matches;    
	        }
	    }
	   
	   
	    public enum ArchiveType {
	        PREARCHIVE() {
	            @Override
	            CatCatalogI getCatalog(XnatResourcecatalogI r) {
	                CatalogUtils.CatalogData catalogData;
	                try {
	                    catalogData = CatalogUtils.CatalogData.getOrCreateAndClean(this.x.getPrearchivepath(), r, false, this.x.getProject()
	                    );
	                } catch (ServerException e) {
	                    return null;
	                }
	                this.rootPath = catalogData.catPath;
	                return catalogData.catBean;
	            }

	            @Override
	            XnatImagesessiondataI retrieve(Env env, UserI user) throws Exception {
	                String project = (String) env.attrs.get("PROJECT_ID");
	                String experiment = (String) env.attrs.get("EXPT_ID");
	                String timestamp = (String) env.attrs.get("TIMESTAMP");
	                File sessionDIR;
	                File srcXML;
	                sessionDIR = PrearcUtils.getPrearcSessionDir(user, project, timestamp, experiment, false);
	                srcXML = new File(sessionDIR.getAbsolutePath() + ".xml");
	                XnatImagesessiondataI x = PrearcTableBuilder.parseSession(srcXML);
	                this.x = x;
	                return x;
	            }
	        },
	        ARCHIVE() {
	            @Override
	            CatCatalogI getCatalog(XnatResourcecatalogI r) {
	                this.rootPath = (new File(r.getUri())).getParent();
	                CatalogUtils.CatalogData catalogData;
	                try {
	                    catalogData = CatalogUtils.CatalogData.getOrCreateAndClean(this.rootPath, r, true, this.x.getProject()
	                    );
	                } catch (ServerException e) {
	                    return null;
	                }
	                this.rootPath = catalogData.catPath;
	                return catalogData.catBean;
	            }

	            @Override
	            XnatImagesessiondataI retrieve(Env env, UserI user) throws Exception {
	                String project = (String) env.attrs.get("PROJECT_ID");
	                String experiment = (String) env.attrs.get("EXPT_ID");
	                XnatImagesessiondata x = (XnatImagesessiondata) XnatExperimentdata.GetExptByProjectIdentifier(project, experiment, user, false);
	                if (x == null || null == x.getId()) {
	                    x = (XnatImagesessiondata) XnatExperimentdata.getXnatExperimentdatasById(experiment, user, false);
	                    if (x != null && !x.hasProject(project)) {
	                        x = null;
	                    }
	                }
	                if (x == null) {
	                    //throw new ClientException(Status.CLIENT_ERROR_NOT_FOUND, "Experiment or project not found",  new Exception("Experiment or project not found"));
	                }
	                this.x = x;
	                return x;
	            }
	        },

	        UNKNOWN() {
	            @Override
	            CatCatalogI getCatalog(XnatResourcecatalogI r) {
	                return null;
	            }

	            @Override
	            XnatImagesessiondataI retrieve(Env env, UserI user) {
	                return null;
	            }
	        };

	        XnatImagesessiondataI x = null;
	        String rootPath = null;

	        /**
	         * Retrieve the catalog for this resource. Additionally this also updates the
	         * "rootPath" global class variable. This function is dependent on the
	         * {@link XnatImageassessordataI} having been populated.
	         *
	         * @param r The resource catalog reference.
	         * @return The catalog for the resource.
	         */
	        abstract CatCatalogI getCatalog(XnatResourcecatalogI r);

	        /**
	         * Retrieve the image session object for this session. Additionally this also updates the
	         * XnatImagesessiondataI global.
	         *
	         * @param env  The environment object.
	         * @param user The user.
	         * @return The image session object.
	         * @throws ClientException
	         * @throws IOException
	         * @throws InvalidPermissionException
	         * @throws Exception
	         */
	        abstract XnatImagesessiondataI retrieve(Env env, UserI user) throws Exception;
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
	                        files.addAll(this.findMatchingFile(env, resources, filter, enough));
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
	                        files.addAll(this.findMatchingFile(env, resources, filter, enough));
	                        if (files.size() >= enough) {
	                            return files;
	                        }
	                        final List<XnatResourcecatalogI> in_resources = assessor.getIn_file();
	                        files.addAll(this.findMatchingFile(env, in_resources, filter, enough));
	                        if (files.size() >= enough) {
	                            return files;
	                        }
	                        final List<XnatResourcecatalogI> out_resources = assessor.getOut_file();
	                        files.addAll(this.findMatchingFile(env, out_resources, filter, enough));
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
	                        files.addAll(this.findMatchingFile(env, in_resources, filter, enough));
	                        if (files.size() >= enough) {
	                            return files;
	                        }
	                        List<XnatResourcecatalogI> out_resources = recon.getOut_file();
	                        files.addAll(this.findMatchingFile(env, out_resources, filter, enough));
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
	            this.path = path;
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
	    
	    /**
	     * 
	     * @param header
	     * @param formatParams
	     * @param e
	     * @param dicomSummaries
	     * @return
	     */
	    public List<DicomSummary> dicomSummaryDumpWrite(DicomObject header,DicomObjectToStringParam formatParams,DicomElement e, List<DicomSummary> dicomSummaries){
	    	if (fields.isEmpty() || fields.containsKey(e.tag())) {
	            if (e.hasDicomObjects()) {
	                for (int i = 0; i < e.countItems(); i++) {
	                    DicomObject o = e.getDicomObject(i);
	                    dicomSummaries = dicomSummaryDumpMakeRow(header, e, TagUtils.toString(e.tag()), formatParams.valueLength, dicomSummaries);
	                    for (Iterator<DicomElement> it1 = o.iterator(); it1.hasNext();) {
	                        DicomElement e1 = it1.next();
	                        dicomSummaryDumpWrite(header, formatParams, e1, dicomSummaries);	
	                    }
	                }
//	            } else if (SiemensShadowHeader.isShadowHeader(header, e)) {
//	                SiemensShadowHeader.addRows(t, header, e, fields.get(e.tag()));
//	            } 
	            } else {
	            	dicomSummaries = dicomSummaryDumpMakeRow(header, e, null, formatParams.valueLength, dicomSummaries);		
	            }
	    	}
			return dicomSummaries;
	    }
	    
	    /**
	     * 
	     * @return
	     * @throws IOException
	     * @throws FileNotFoundException
	     */
	    public  List<DicomSummary> dicomSummaryDumpRender() throws IOException,FileNotFoundException {
	    	 List<DicomSummary> dicomSummaries = new ArrayList<>();
	        for (File file : files) {
				DicomObject header = getHeader(file, fields);
		        DicomObjectToStringParam formatParams = DicomObjectToStringParam.getDefaultParam();
		
		        for (Iterator<DicomElement> it = header.iterator(); it.hasNext();) {
		            DicomElement e = it.next();
		            try {
		            	
		            	dicomSummaries =dicomSummaryDumpWrite(header, formatParams, e, dicomSummaries) ;
		            }catch(Exception ex){
		                //logger.error("Error reading dicom tag,"+ e.tag(),ex);
		            }
		        }
	        }
	        return dicomSummaryDumpReformat(dicomSummaries);
	    }
	    
	    
	    /**
	     * 
	     * @param f
	     * @param fields
	     * @return
	     * @throws IOException
	     * @throws FileNotFoundException
	     */
	    DicomObject getHeader(File f, Map<Integer,Set<String>> fields) throws IOException, FileNotFoundException {
	        final int stopTag;
	        if (fields.isEmpty()) {
	            stopTag = Tag.PixelData;
	        } else {
	            stopTag = 1 + Collections.max(fields.keySet());
	        }
	        final StopTagInputHandler stopHandler = new StopTagInputHandler(stopTag);

	        IOException ioexception = null;
	        final DicomInputStream dis = new DicomInputStream(f);
	        try {
	            dis.setHandler(stopHandler);
	            return dis.readDicomObject();
	        } catch (IOException e) {
	            throw ioexception = e;
	        } finally {
	            try {
	                dis.close();
	            } catch (IOException e) {
	                if (null != ioexception) {
	                    //logger.error("unable to close DicomInputStream", e);
	                    throw ioexception;
	                } else {
	                    throw e;
	                }
	            }
	        }
	    }
	   
	    /**
	     * 
	     * @param o
	     * @param e
	     * @param parentTag
	     * @param maxLen
	     * @param dicomSummaries
	     * @return
	     */
	    List<DicomSummary> dicomSummaryDumpMakeRow(DicomObject o, DicomElement e, String parentTag , int maxLen, List<DicomSummary> dicomSummaries) {
	    	DicomSummary dicomSummary = new DicomSummary();
	        String tag = TagUtils.toString(e.tag());
	        String value = "";
	        // If this element has nested tags it doesn't have a value and trying to 
	        // extract one using dcm4che will result in an UnsupportedOperationException 
	        // so check first.
	        try {
	            if (!e.hasDicomObjects()) {
	                value = e.getValueAsString(null, maxLen);	
	            }
	            else {
	                value = "";
	            } 
	        }catch(UnsupportedOperationException usex) {
	            value = "UnsupportedBinarySequence";
	        }
	        String vr = e.vr().toString();
	        String desc = o.nameOf(e.tag());
	        dicomSummary.setDesc(desc);
	        dicomSummary.setTag1(tag);
	        dicomSummary.setValue(value);
	        dicomSummary.setVr(vr);
	        if (parentTag == null) {
	        	dicomSummary.setTag2("");
	        	 dicomSummaries.add(dicomSummary);
	        	return dicomSummaries;
	        }
	        else {
	            dicomSummary.setTag2(parentTag);
	            dicomSummaries.add(dicomSummary);
	            return dicomSummaries;
	        }
	    }
	    
	    /**
	     * 
	     * @param dicomSummaries
	     * @return
	     * @throws IOException
	     * @throws FileNotFoundException
	     */
	    public List<DicomSummary> dicomSummaryDumpReformat(List<DicomSummary> dicomSummaries) throws IOException,FileNotFoundException {
	    	List<DicomSummary>finalDicomSummaries = new ArrayList<DicomSummary>();
	         for (DicomSummary dicomSummary : dicomSummaries) {
					add2Map( (String) dicomSummary.getTag1(),(String)  dicomSummary.getTag2(),(String)  dicomSummary.getVr(),(String)  dicomSummary.getValue(), (String)  dicomSummary.getDesc());
	         }
	         
	         for (String key :map.keySet()){
	        	 Collection<DicomSummary> dsummary=map.get(key);
	        	 String val="";
	        	 int i=0;
	        	 DicomSummary consolidated=new DicomSummary();
	        	 for (DicomSummary dicomSummary : dsummary) {
					if (StringUtils.contains(val, dicomSummary.getValue())!=true && StringUtils.isNotBlank(dicomSummary.getValue())){
						if("".equals(val)){
							val=dicomSummary.getValue();
						}else{
							val=val+", "+dicomSummary.getValue();;
						}
					}
					if (i== dsummary.size()-1){
						consolidated=new DicomSummary(key,dicomSummary.getTag2(),consolidated.getVr(),val,dicomSummary.getDesc());
					}
					i++;
				 }
	        	 if(dsummary.size()>0){
	        		 finalDicomSummaries.add(new DicomSummary(
	        				 Objects.nonNull(consolidated.getTag1())?consolidated.getTag1():"",
	        				 Objects.nonNull(consolidated.getTag2())?consolidated.getTag2():"",
	        				 Objects.nonNull(consolidated.getVr())?consolidated.getVr():"",
	        				 Objects.nonNull(consolidated.getValue())?consolidated.getValue():"",
	        				 Objects.nonNull(consolidated.getDesc())?consolidated.getDesc():""));
	        	 }
	         }
	   
	        return finalDicomSummaries;
	    }
	    
	    /**
	     * 
	     * @param tag1
	     * @param tag2
	     * @param vr
	     * @param value
	     * @param desc
	     */
	    void add2Map(String  tag1,String tag2,String vr,String value, String desc){
	    	DicomSummary summary=new DicomSummary(tag1, tag2, vr, value, desc);
	    	
	    	map.put(tag1,summary);
	    }
	    
	    
	    
	   /** ---------Dicom Header Dump Start----------*/
	    
	    
	    /**
	     * 
	     * @return
	     * @throws IOException
	     * @throws FileNotFoundException
	     */
	    public List<DicomSummary> dicomHeaderDumpRender() throws IOException,FileNotFoundException {
	    	 List<DicomSummary> dicomSummaries = new ArrayList<>();
	        if (this.file == null) {
	            return dicomSummaries ;
	        }

	        DicomObject header = getDicomHeaderDumpHeader(new File(this.file));
	        DicomObjectToStringParam DEFAULT_PARAM = DicomObjectToStringParam.getDefaultParam();
	        DicomObjectToStringParam formatParams = new DicomObjectToStringParam(
	        		DEFAULT_PARAM.name, 			// name
	        		255,							// valueLength;
	        		DEFAULT_PARAM.numItems,			// numItems;
	        		DEFAULT_PARAM.lineLength,		// lineLength;
	        		DEFAULT_PARAM.numLines, 		// numLines;
	        		DEFAULT_PARAM.indent,			// indent
	        		DEFAULT_PARAM.lineSeparator);	// line separator

	        for (Iterator<DicomElement> it = header.iterator(); it.hasNext();) {
	            DicomElement e = it.next();
	            try{
	            	dicomSummaries = dicomHeaderDumpWrite(header, formatParams, e, dicomSummaries);
	            }catch(Exception ex){
	                //logger.error("Error reading dicom tag,"+ e.tag(),ex);
	            }
	        }
	         return dicomSummaryDumpReformat(dicomSummaries);
	    }
	    
	    /**
	     * 
	     * @param file
	     * @return
	     * @throws IOException
	     * @throws FileNotFoundException
	     */
	    DicomObject getDicomHeaderDumpHeader(File file) throws IOException, FileNotFoundException {
	        final int stopTag;
	        if (fields.isEmpty()) {
	            stopTag = Tag.PixelData;
	        } else {
	            stopTag = 1 + Collections.max(fields.keySet());
	        }
	        final StopTagInputHandler stopHandler = new StopTagInputHandler(stopTag);

	        IOException ioexception = null;
	        final DicomInputStream dis = new DicomInputStream(file);
	        try {
	            dis.setHandler(stopHandler);
	            return dis.readDicomObject();
	        } catch (IOException e) {
	            throw ioexception = e;
	        } finally {
	            try {
	                dis.close();
	            } catch (IOException e) {
	                if (null != ioexception) {
	                    //logger.error("unable to close DicomInputStream", e);
	                    throw ioexception;
	                } else {
	                    throw e;
	                }
	            }
	        }
	    }
	    
	    /**
	     * 
	     * @param header
	     * @param formatParams
	     * @param e
	     * @param dicomSummaries
	     * @return
	     */
	    public List<DicomSummary> dicomHeaderDumpWrite(DicomObject header,DicomObjectToStringParam formatParams,DicomElement e, List<DicomSummary> dicomSummaries){
	        if (fields.isEmpty() || fields.containsKey(e.tag())) {
	            if (e.hasDicomObjects()) {
	                for (int i = 0; i < e.countItems(); i++) {
	                    DicomObject o = e.getDicomObject(i);
	                    dicomSummaries = dicomHeaderDumpMakeRow(header, e, TagUtils.toString(e.tag()), formatParams.valueLength, dicomSummaries);
	                    for (Iterator<DicomElement> it1 = o.iterator(); it1.hasNext();) {
	                        DicomElement e1 = it1.next();
	                        dicomSummaries = dicomHeaderDumpMakeRow(header, e1, TagUtils.toString(e.tag()), formatParams.valueLength, dicomSummaries);
	                    }
	                }
//	            } else if (SiemensShadowHeader.isShadowHeader(header, e)) {
//	                SiemensShadowHeader.addRows(header, e, fields.get(e.tag()));
//	            } 
	            }else {
	            	dicomSummaries = dicomHeaderDumpMakeRow(header, e, null, formatParams.valueLength, dicomSummaries);		
	            }
	        }
			return dicomSummaries;
	    }
	    
	    /**
	     * 
	     * @param object
	     * @param element
	     * @param parentTag
	     * @param maxLen
	     * @param dicomSummaries
	     * @return
	     */
	    List<DicomSummary> dicomHeaderDumpMakeRow(final DicomObject object, final DicomElement element, final String parentTag, final int maxLen, List<DicomSummary> dicomSummaries) {
	       
	    	DicomSummary dicomSummary = new DicomSummary();
	    	final String tag = TagUtils.toString(element.tag());

	        final String value = getValueAsString(element,maxLen);
	       
	        final String vr = element.vr().toString();

	        // This fixes the unfortunate tendency of DICOM tags to use good typographical but poor programming practices.
	        final String desc = escapeHTML(object.nameOf(element.tag()));
	        dicomSummary.setDesc(desc);
	        dicomSummary.setValue(value);
	        dicomSummary.setVr(vr);
	        if(parentTag == null) {
	        	dicomSummary.setTag2("");
	        	dicomSummary.setTag1(tag);
	        }else {
	        	dicomSummary.setTag1(parentTag);
	        	dicomSummary.setTag2(tag);
	        }
	    
	       dicomSummaries.add(dicomSummary);
	       return dicomSummaries;
	    }

	    public static String escapeHTML(final String value) {
	        return value == null ? null : StringEscapeUtils.escapeHtml4(value);
	    }
	    
	    private String getValueAsString(final DicomElement e, final int length) {
	        try {
	            return !e.hasDicomObjects() ? escapeHTML(e.getValueAsString(null, length)) : "";
	        }catch(UnsupportedOperationException usex) {
	            return "UnsupportedBinarySequence";
	        }
	    }
}
