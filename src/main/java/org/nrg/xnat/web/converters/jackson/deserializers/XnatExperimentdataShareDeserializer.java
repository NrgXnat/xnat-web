package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import org.nrg.xdat.om.XnatExperimentdataShare;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class XnatExperimentdataShareDeserializer extends AbstractBaseElementDeserializer<XnatExperimentdataShare> {
	private static final long serialVersionUID = 8714542973804658983L;

	public XnatExperimentdataShareDeserializer() {
        super(XnatExperimentdataShare.class);
    }

    @Override
    protected XnatExperimentdataShare deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
    	final XnatExperimentdataShare xnatExperimentdataShare = Optional.ofNullable((XnatExperimentdataShare) context.getAttribute("XnatItem")).orElseThrow(() -> new RuntimeException("XnatExperimentdataShare can't be created on its own"));
    	context.setAttribute("XnatItem", xnatExperimentdataShare);
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            if(Objects.nonNull(field)){
            	   switch (field) {
                   case "label":
                	   xnatExperimentdataShare.setLabel(parser.getText());
                       break;
                   case "visit":
                	   xnatExperimentdataShare.setVisit(parser.getText());
                       break;
                   case "project":
                	   xnatExperimentdataShare.setProject(parser.getText());
                       break;
                   case "protocol":
                	   xnatExperimentdataShare.setProtocol(parser.getText());
                       break;
				}
            }
         
        }
        return xnatExperimentdataShare;
}

	@Override
	protected XnatExperimentdataShare getNewInstance() throws JsonProcessingException {
		return null;
	}

}
