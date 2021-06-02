package org.nrg.xnat.services.dump.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
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
import org.nrg.ecat.MatrixDataFile;
import org.nrg.ecat.Variable;
import org.nrg.xnat.helpers.dicom.DicomSummary;
import org.nrg.xnat.helpers.ecat.EcatSummary;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DumpUtil<T> {
	/** The files. */
    private  Iterable<File> files; // path to the DICOM file
    /** The fields. */
    private final Map<Integer,Set<String>> fields;
    
    private String file; // path to the DICOM file
    
    private static String dumpType;
    
	ListMultimap<String, T> map = Multimaps.newListMultimap(
  		  new TreeMap<String, Collection<T>>(),
  		  new Supplier<List<T>>() {
  		    public List<T> get() {
  		      return Lists.newArrayList();
  		    }
  		  });
	public DumpUtil(Iterable<File> files, Map<Integer, Set<String>> fields2, String dumpType) {
    	this.files = files;
        this.fields = ImmutableMap.copyOf(fields2);
        DumpUtil.dumpType = dumpType;
	}
	
	public DumpUtil(String file, Map<Integer, Set<String>> fields2, String dumpType) {
    	this.file = file;
        this.fields = ImmutableMap.copyOf(fields2);
        DumpUtil.dumpType = dumpType;
	}
	
	/**
     * 
     * @param f
     * @param fields
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    DicomObject getDicomHeader(File f) throws IOException, FileNotFoundException {
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
                	log.error("unable to close DicomInputStream", e);
                    throw ioexception;
                } else {
                    throw e;
                }
            }
        }
    }
   
   
    
    
    /**
     * 
     * @param tag1
     * @param summary
     */
    void add2Map(String tag1, T summary){
    	map.put(tag1,summary);
    }
    
	  
    /**
     * 
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    @SuppressWarnings("unchecked")
	public  List<T> summaryDumpRender() throws IOException,FileNotFoundException {
    	if(dumpType.equals("DICOM")) {
    		 return (List<T>) getDicomSummaryDumpRender();
    	}else if(dumpType.equals("ECAT")) {
    		return (List<T>) getEcatSummaryDumpRender();
    	}
		return null;
      
    }
    
    private List<T> getEcatSummaryDumpRender() {
		return null;
	}

	/**
     * 
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private List<DicomSummary> getDicomSummaryDumpRender() throws FileNotFoundException, IOException {
   	 List<DicomSummary> dicomSummaries = new ArrayList<>();
   	  for (File file : files) {
				DicomObject header = getDicomHeader(file);
		        DicomObjectToStringParam formatParams = DicomObjectToStringParam.getDefaultParam();
		
		        for (Iterator<DicomElement> it = header.iterator(); it.hasNext();) {
		            DicomElement e = it.next();
		            try {
		            	dicomSummaries = getDicomSummaryDumpWrite(header, formatParams, e, dicomSummaries) ;
		            }catch(Exception ex){
		            	log.error("Error reading dicom tag,"+ e.tag(),ex);
		            }
		        }
	        }
	        return dicomSummaryDumpReformat(dicomSummaries);
	}
    

    /**
     * 
     * @param dicomSummaries
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    @SuppressWarnings("unchecked")
	public List<DicomSummary> dicomSummaryDumpReformat(List<DicomSummary> dicomSummaries) throws IOException,FileNotFoundException {
    	List<DicomSummary>finalDicomSummaries = new ArrayList<DicomSummary>();
         for (DicomSummary dicomSummary : dicomSummaries) {
				add2Map(dicomSummary.getTag1(),(T) dicomSummary);
         }
         
         for (String key :map.keySet()){
        	 Collection<DicomSummary> dsummary=(Collection<DicomSummary>) map.get(key);
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
     * @param header
     * @param formatParams
     * @param e
     * @param dicomSummaries
     * @return
     */
	private List<DicomSummary> getDicomSummaryDumpWrite(DicomObject header, DicomObjectToStringParam formatParams, DicomElement e, List<DicomSummary> dicomSummaries) {
		if (fields.isEmpty() || fields.containsKey(e.tag())) {
            if (e.hasDicomObjects()) {
                for (int i = 0; i < e.countItems(); i++) {
                    DicomObject o = e.getDicomObject(i);
                    dicomSummaries = dicomSummaryDumpMakeRow(header, e, TagUtils.toString(e.tag()), formatParams.valueLength, dicomSummaries);
                    for (Iterator<DicomElement> it1 = o.iterator(); it1.hasNext();) {
                        DicomElement e1 = it1.next();
                        getDicomSummaryDumpWrite(header, formatParams, e1, dicomSummaries);	
                    }
                }
//            } else if (SiemensShadowHeader.isShadowHeader(header, e)) {
//                SiemensShadowHeader.addRows(t, header, e, fields.get(e.tag()));
//            } 
            } else {
            	dicomSummaries = dicomSummaryDumpMakeRow(header, e, null, formatParams.valueLength, dicomSummaries);		
            }
    	}
		return dicomSummaries;
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
		        
	   /** ---------Dicom Header Dump Start----------*/
	    
	    
	    /**
	     * 
	     * @return
	     * @throws IOException
	     * @throws FileNotFoundException
	     */
	    public List<DicomSummary> dicomHeaderDumpRender() throws IOException,FileNotFoundException {
	    	 List<DicomSummary> dicomSummaries = new ArrayList<>();
	        if (file == null) {
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
	            	log.error("Error reading dicom tag,"+ e.tag(),ex);
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
	                	log.error("unable to close DicomInputStream", e);
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
