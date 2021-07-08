package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import org.nrg.xdat.om.XnatValidationdata;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class XnatValidationDataDeserializer extends AbstractBaseElementDeserializer<XnatValidationdata> {
	private static final long serialVersionUID = 8714542973804658983L;

	public XnatValidationDataDeserializer() {
        super(XnatValidationdata.class);
    }

    @Override
    protected XnatValidationdata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
    	final XnatValidationdata xnatValidationdata = Optional.ofNullable((XnatValidationdata) context.getAttribute("XnatItem")).orElseThrow(() -> new RuntimeException("XnatValidationdata can't be created on its own"));
    	context.setAttribute("XnatItem", xnatValidationdata);
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            if(Objects.nonNull(field)){
            	   switch (field) {
                   case "method":
                	   xnatValidationdata.setMethod(parser.getText());
                       break;
                   case "date":
                	   xnatValidationdata.setValidatedBy(parser.getText());
                       break;
                   case "notes":
                	   xnatValidationdata.setNotes(parser.getText());
                       break;
                   case "validate_by":
                	   xnatValidationdata.setValidatedBy(parser.getText());
                       break;
                   case "status":
                	   xnatValidationdata.setStatus(parser.getText());
                       break;
				}
            }
         
        }
        return xnatValidationdata;
}

	@Override
	protected XnatValidationdata getNewInstance() throws JsonProcessingException {
		return null;
	}
}
