package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import org.nrg.xdat.model.XnatImageassessordataI;
import org.nrg.xdat.model.XnatImagescandataI;
import org.nrg.xdat.model.XnatReconstructedimagedataI;
import org.nrg.xdat.model.XnatRegionresourceI;
import org.nrg.xdat.om.XnatImageassessordata;
import org.nrg.xdat.om.XnatImagescandata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatMrsessiondata;
import org.nrg.xdat.om.XnatReconstructedimagedata;
import org.nrg.xdat.om.XnatRegionresource;
import org.nrg.xft.ItemI;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XnatImagesessiondataDeserializer extends XnatSubjectassessordataDeserializer {
	private static final long serialVersionUID = 8714542973804658983L;

	public XnatImagesessiondataDeserializer(Class<XnatMrsessiondata> class1) {
        super(XnatImagesessiondata.class);
    }

    @Override
    protected XnatImagesessiondata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
    	final XnatImagesessiondata xnatImagesessiondata = Optional.ofNullable((XnatImagesessiondata) context.getAttribute("XnatItem")).orElseThrow(() -> new RuntimeException("XnatImagesessiondata can't be created on its own"));

    	XnatImageassessordataI imageAssessorData = new XnatImageassessordata();
    	XnatReconstructedimagedataI reconstructedimagedata = new XnatReconstructedimagedata();
    	XnatRegionresourceI regionresource= new XnatRegionresource();
    	XnatImagescandataI imagescandata= new XnatImagescandata();
    	 try {
    		 xnatImagesessiondata.setAssessors_assessor((ItemI) imageAssessorData);
    		 xnatImagesessiondata.setReconstructions_reconstructedimage((ItemI) reconstructedimagedata);
    		 xnatImagesessiondata.setRegions_region((ItemI) regionresource);
    		 xnatImagesessiondata.setScans_scan((ItemI) imagescandata);
         } catch (Exception e) {
             log.error("An error occurred trying to set demographics data while deserializing an object. Sorry about that.", e);
         }
    	while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            if(Objects.nonNull(field)){
            	   switch (field) {
                   case "dcmAccessionNumber":
                	   xnatImagesessiondata.setDcmaccessionnumber(parser.getText());
                       break;
                   case "dcmPatientBirthDate":
                	   xnatImagesessiondata.setDcmpatientbirthdate(parser.getText());
                       break;
                   case "dcmPatientId":
                	   xnatImagesessiondata.setDcmpatientid(parser.getText());
                       break;
                   case "dcmPatientName":
                	   xnatImagesessiondata.setDcmpatientname(parser.getText());
                       break;
                   case "dcmPatientWeight":
                	   xnatImagesessiondata.setDcmpatientweight(Double.parseDouble(parser.getText()));
                       break;
                   case "modality":
                	   xnatImagesessiondata.setModality(parser.getText());
                       break;
                   case "operator":
                	   xnatImagesessiondata.setOperator(parser.getText());
                       break;
                   case "prearchivePath":
                	   xnatImagesessiondata.setPrearchivepath(parser.getText());
                       break;
                   case "scanner":
                	   xnatImagesessiondata.setScanner(parser.getText());
                       break;
                   case "study_id":
                	   xnatImagesessiondata.setStudyId(parser.getText());
                       break;
                   case "UID":
                	   xnatImagesessiondata.setUid(parser.getText());
                       break;
				}
            }
         
        }
    	return (XnatImagesessiondata)super.deserializeImpl(parser, context);
    }

	@Override
	protected XnatImagesessiondata getNewInstance() throws JsonProcessingException {
		return null;
	}
}
