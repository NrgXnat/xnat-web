package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatDemographicdata;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.ItemI;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XnatExperimentdataDeserializer extends AbstractBaseElementDeserializer<XnatExperimentdata> {

	public XnatExperimentdataDeserializer() {
		super(XnatExperimentdata.class);
	}

	@Override
	public XnatExperimentdata deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		 if (parser.getCurrentToken() != JsonToken.START_OBJECT) {
	            throw new IOException("invalid start marker");
	        }
	        final XnatExperimentdata     experiment      = new XnatExperimentdata();

	        while (parser.nextToken() != JsonToken.END_OBJECT) {
	            final String field = parser.getCurrentName();
	            parser.nextToken();  //move to next token in string
	            switch (field) {
	                case "id":
	                	experiment.setId(parser.getText());
	                    break;
	                case "label":
	                	experiment.setLabel(parser.getText());
	                    break;
	                case "project":
	                	experiment.setProject(parser.getText());
	                    break;
	                case "note":
	                	experiment.setNote(parser.getText());
	                    break;
	                case "protocol":
	                	experiment.setProtocol(parser.getText());
	                    break;
	                case "original":
	                	experiment.setOriginal(parser.getText());
	                    break;
	                case "date":
	                	experiment.setDate(parseDate(parser.getText()));
	                    break;
	                case "delay":
	                	experiment.setDelay(parser.getIntValue());
	                    break;
	                case "version":
	                	experiment.setVersion(parser.getIntValue());
	                    break;
	            }
	        }
	        return experiment;
	}

}
