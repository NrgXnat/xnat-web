package org.nrg.xnat.export.manifest.serializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.om.XnatImageassessordata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xdat.om.XnatSubjectassessordata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xnat.export.manifest.TransportManifest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * @author Mohana Ramaratnam
 *
 */
public class TransportManifestSerializer extends StdSerializer<TransportManifest> {
	     
	    public TransportManifestSerializer() {
	        this(null);
	    }
	   
	    public TransportManifestSerializer(Class<TransportManifest> t) {
	        super(t);
	    }
	 
	    @Override
	    public void serialize(TransportManifest tManifest, JsonGenerator jgen, SerializerProvider provider) 
	      throws IOException, JsonProcessingException {
	  
	        jgen.writeStartObject();
	        jgen.writeStringField("project", tManifest.getExportManifest().getProjectId());
	        jgen.writeStringField("label", tManifest.getExportManifest().getEndpointDefinition().getLabel());
	        jgen.writeStringField("export-handler", tManifest.getExportManifest().getEndpointDefinition().getExportHandler());
	        jgen.writeStringField("authorized_by", tManifest.getAuthorizedBy().getUsername());
	        jgen.writeNumberField("estimatedFileCount", tManifest.getEstimatedFileCountToBeExported());
	        jgen.writeNumberField("estimatedSize", tManifest.getEstimatedAmountofDataToBeExported() );
	        List<XnatSubjectdata> subjects = tManifest.getSubjects();
	        if (subjects != null && subjects.size() > 0) {
	        	jgen.writeArrayFieldStart("subjects");
		        //Project Resources
		        //Subjects : id,label,resources, subjectAssessor/id,label,resources, imagesession/id,label,resources, scans/id/type,resources, assessors/id,label,resources
                for (XnatSubjectdata s : subjects) {
                	jgen.writeStartObject(); //Start Subjects tag
    	        	jgen.writeStringField("id", s.getId());
    	        	jgen.writeStringField("label", s.getLabel());
    	        	List<XnatAbstractresourceI> rscs = s.getResources_resource();
    	        	if (rscs != null && rscs.size() > 0) {
    		        	jgen.writeArrayFieldStart("resources");
    	                for (XnatAbstractresourceI a : rscs) {
    	                	jgen.writeStartObject();
    	                	jgen.writeStringField("label", a.getLabel());
    	                	if (a instanceof XnatResourcecatalog) {
    	                		jgen.writeStringField("uri",((XnatResourcecatalog)a).getUri());
    	                	}
    	                	jgen.writeNumberField("file_count", a.getFileCount());
    	                	jgen.writeNumberField("file_size", Long.parseLong(a.getFileSize().toString()));
    	                	jgen.writeEndObject();
    	                }    	        		
    	                jgen.writeEndArray();
    	        	}
    	        	List<XnatImagesessiondata> imagingSessions = new ArrayList<XnatImagesessiondata>();
    	        	//Now write all subject assessors
    	        	List<XnatSubjectassessordata> exps = s.getExperiments_experiment();
    	        	if (exps != null && exps.size() > 0)  {
    	        		for (XnatSubjectassessordata sa : exps) {
    	        			if (sa instanceof XnatImagesessiondata) {
    	        				imagingSessions.add((XnatImagesessiondata)sa);
    	        			}else {
    	        	        	jgen.writeArrayFieldStart("subject_assessors");
    	                    	jgen.writeStartObject(); //Start Subjects tag
    	        	        	jgen.writeStringField("id", sa.getId());
    	        	        	jgen.writeStringField("label", sa.getLabel());
    	        	        	List<XnatAbstractresourceI> saRscs = sa.getResources_resource();
    	        	        	if (saRscs != null && saRscs.size() > 0) {
    	        		        	jgen.writeArrayFieldStart("resources");
    	        	                for (XnatAbstractresourceI a : saRscs) {
    	        	                	jgen.writeStartObject();
    	        	                	jgen.writeStringField("label", a.getLabel());
    	        	                	if (a instanceof XnatResourcecatalog) {
    	        	                		jgen.writeStringField("uri",((XnatResourcecatalog)a).getUri());
    	        	                	}
    	        	                	jgen.writeNumberField("file_count", a.getFileCount());
    	        	                	jgen.writeNumberField("file_size", Long.parseLong(a.getFileSize().toString()));
    	        	                	jgen.writeEndObject();
    	        	                }    	        		
    	        	                jgen.writeEndArray();
    	        	        	}
    	        	        	jgen.writeEndObject();
    	        	        	jgen.writeEndArray();
    	        			}
    	        		}
    	        	}
    	        	//End of all subject assessor

    	        	//Now write all Imaging sessions
    	        	if (imagingSessions != null && imagingSessions.size() > 0)  {
    	        		for (XnatImagesessiondata isa : imagingSessions) {
    	        	        	jgen.writeArrayFieldStart("image_sessions");
    	                    	jgen.writeStartObject(); //Start Imaging Sessions
    	        	        	jgen.writeStringField("id", isa.getId());
    	        	        	jgen.writeStringField("label", isa.getLabel());
    	        	        	jgen.writeStringField("xsiType", isa.getXSIType());
    	        	        	List<XnatAbstractresourceI> saRscs = isa.getResources_resource();
    	        	        	if (saRscs != null && saRscs.size() > 0) {
    	        		        	jgen.writeArrayFieldStart("resources");
    	        	                for (XnatAbstractresourceI a : saRscs) {
    	        	                	jgen.writeStartObject();
    	        	                	jgen.writeStringField("label", a.getLabel());
    	        	                	if (a instanceof XnatResourcecatalog) {
    	        	                		jgen.writeStringField("uri",((XnatResourcecatalog)a).getUri());
    	        	                	}
    	        	                	jgen.writeNumberField("file_count", a.getFileCount());
    	        	                	jgen.writeNumberField("file_size", Long.parseLong(a.getFileSize().toString()));
    	        	                	jgen.writeEndObject();
    	        	                }    	        		
    	        	                jgen.writeEndArray();
    	        	        	}
    	        	        	//Now write the scans
    	        	        	List<XnatImagescandata> scans = isa.getScans_scan();
    	        	        	if (scans != null && scans.size() > 0) {
    	        	        		jgen.writeArrayFieldStart("scans");
    	        	        		for (XnatImagescandata sc:scans) {
    	        	        			jgen.writeStartObject();
    	        	        			jgen.writeStringField("id", sc.getId());
    	        	        			jgen.writeStringField("type",sc.getXSIType());
    	    	        	        	List<XnatAbstractresourceI> scRscs = sc.getFile();
    	    	        	        	if (scRscs != null && scRscs.size() > 0) {
    	    	        		        	jgen.writeArrayFieldStart("resources");
    	    	        	                for (XnatAbstractresourceI a : scRscs) {
    	    	        	                	jgen.writeStartObject();
    	    	        	                	jgen.writeStringField("label", a.getLabel());
    	    	        	                	if (a instanceof XnatResourcecatalog) {
    	    	        	                		jgen.writeStringField("uri",((XnatResourcecatalog)a).getUri());
    	    	        	                	}
    	    	        	                	jgen.writeNumberField("file_count", a.getFileCount());
    	    	        	                	jgen.writeNumberField("file_size", Long.parseLong(a.getFileSize().toString()));
    	    	        	                	jgen.writeEndObject();
    	    	        	                }    	        		
    	    	        	                jgen.writeEndArray();
    	    	        	        	}
    	        	        			jgen.writeEndObject();
    	        	        		}
    	        	        		jgen.writeEndArray();
    	        	        	}
    	        	        	//Now write the image assessors
    	        	        	List<XnatImageassessordata> imgAssesors = isa.getAssessors_assessor();
    	        	        	if (imgAssesors != null && imgAssesors.size() > 0) {
    	        	        		jgen.writeArrayFieldStart("image_assessors");
    	        	        		for (XnatImageassessordata imgA:imgAssesors) {
    	        	        			jgen.writeStartObject();
    	        	        			jgen.writeStringField("id", imgA.getId());
    	        	        			jgen.writeStringField("label",imgA.getLabel() );
    	        	        			jgen.writeStringField("xsiType", imgA.getXSIType());
    	    	        	        	List<XnatAbstractresourceI> imgARscs = imgA.getResources_resource();
    	    	        	        	if (imgARscs != null && imgARscs.size() > 0) {
    	    	        		        	jgen.writeArrayFieldStart("resources");
    	    	        	                for (XnatAbstractresourceI a : imgARscs) {
    	    	        	                	jgen.writeStartObject();
    	    	        	                	jgen.writeStringField("label", a.getLabel());
    	    	        	                	if (a instanceof XnatResourcecatalog) {
    	    	        	                		jgen.writeStringField("uri",((XnatResourcecatalog)a).getUri());
    	    	        	                	}
    	    	        	                	jgen.writeNumberField("file_count", a.getFileCount());
    	    	        	                	jgen.writeNumberField("file_size", Long.parseLong(a.getFileSize().toString()));
    	    	        	                	jgen.writeEndObject();
    	    	        	                }    	        		
    	    	        	                jgen.writeEndArray();
    	    	        	        	}
    	        	        			jgen.writeEndObject();
    	        	        		}
    	        	        		jgen.writeEndArray();
    	        	        	}
    	        	        	jgen.writeEndObject();
    	        	        	jgen.writeEndArray();
    	        		}
    	        	}
    	        	
    	        	//End of all subject assessor
    	        	jgen.writeEndObject(); //end of subjects tag
                }
		        jgen.writeEndArray();
	        }
	        jgen.writeEndObject();
	    }
}

