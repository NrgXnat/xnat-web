package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import org.nrg.xdat.om.XnatMrsessiondata;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

@SuppressWarnings("rawtypes")
public class XnatMrsessiondataDeserializer extends XnatImagesessiondataDeserializer{
	private static final long serialVersionUID = 8714542973804658983L;

	@SuppressWarnings("unchecked")
	public XnatMrsessiondataDeserializer() {
        super(XnatMrsessiondata.class);
    }

    @Override
    protected XnatMrsessiondata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
    	final XnatMrsessiondata xnatMrsessiondata = Optional.ofNullable((XnatMrsessiondata) context.getAttribute("XnatItem")).orElseGet(XnatMrsessiondata::new); 
    	context.setAttribute("XnatItem", xnatMrsessiondata);
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            if(Objects.nonNull(field)){
            	   switch (field) {
                   case "coil":
                   	xnatMrsessiondata.setCoil(parser.getText());
                       break;
                   case "fieldStrength":
                   	xnatMrsessiondata.setFieldstrength(parser.getText());
                       break;
                   case "marker":
                       xnatMrsessiondata.setMarker(parser.getText());
                       break;
                   case "stabilization":
                       xnatMrsessiondata.setStabilization(parser.getText());
                       break;
                   case "protocol":
                       xnatMrsessiondata.setProtocol(parser.getText());
                       break;
				}
            }
         
        }
        return (XnatMrsessiondata)super.deserializeImpl(parser, context);
    }

	@Override
	protected XnatMrsessiondata getNewInstance() throws JsonProcessingException {
		return new XnatMrsessiondata();
	}
}
