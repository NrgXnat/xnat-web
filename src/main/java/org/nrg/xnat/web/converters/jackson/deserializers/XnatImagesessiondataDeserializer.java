package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatSubjectassessordata;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XnatImagesessiondataDeserializer<T> extends XnatSubjectassessordataDeserializer {
	private static final long serialVersionUID = 8714542973804658983L;

	public XnatImagesessiondataDeserializer(final Class<? extends XnatSubjectassessordata> clazz) {
        super(clazz);
    }

	@Override
    protected XnatImagesessiondata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
    	final XnatImagesessiondata xnatImagesessiondata = Optional.ofNullable((XnatImagesessiondata) context.getAttribute("XnatItem")).orElseThrow(() -> new RuntimeException("XnatImagesessiondata can't be created on its own"));

    	final String field = parser.getCurrentName();
    	if (StringUtils.equalsAny(field, "dcmAccessionNumber", "dcmPatientBirthDate","dcmPatientId","dcmPatientName","dcmPatientWeight","modality",
    			"operator","prearchivePath","scanner","studyId","UID")) {
            parser.nextToken();  //move to next token in string
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
                   case "studyId":
                	   xnatImagesessiondata.setStudyId(parser.getText());
                       break;
                   case "UID":
                	   xnatImagesessiondata.setUid(parser.getText());
                       break;
				}
            	   return xnatImagesessiondata;
            }
        
    	return (XnatImagesessiondata)super.deserializeImpl(parser, context);
    }

}
