package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
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
            if(StringUtils.equalsAny(field,"coil","fieldStrength","marker","stabilization")){
            	   switch (field) {
                   case "coil":
                	parser.nextToken();  //move to next token in string
                   	xnatMrsessiondata.setCoil(parser.getText());
                       break;
                   case "fieldStrength":
                    parser.nextToken();  //move to next token in string
                   	xnatMrsessiondata.setFieldstrength(parser.getText());
                       break;
                   case "marker":
                	   parser.nextToken();  //move to next token in string
                       xnatMrsessiondata.setMarker(parser.getText());
                       break;
                   case "stabilization":
                	   parser.nextToken();  //move to next token in string
                       xnatMrsessiondata.setStabilization(parser.getText());
                       break;
                  
				}
            }else {
                super.deserializeImpl(parser, context);
            }
        }
        return (XnatMrsessiondata)super.deserializeImpl(parser, context);
    }

	@Override
	protected XnatMrsessiondata getNewInstance() throws JsonProcessingException {
		return new XnatMrsessiondata();
	}
}
