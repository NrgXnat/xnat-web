package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatCrsessiondata;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class XnatCrsessiondataDeserializer extends AbstractBaseElementDeserializer<XnatCrsessiondata> {
    public XnatCrsessiondataDeserializer() {
        super(XnatCrsessiondata.class);
    }

    @Override
    protected XnatCrsessiondata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final XnatCrsessiondata xnatCrsessiondata = new XnatCrsessiondata();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "id":
                	xnatCrsessiondata.setId(parser.getText());
                    break;
                case "label":
                	xnatCrsessiondata.setLabel(parser.getText());
                    break;
                case "project":
                	xnatCrsessiondata.setProject(parser.getText());
                    break;
                case "note":
                	xnatCrsessiondata.setNote(parser.getText());
                    break;
                case "protocol":
                	xnatCrsessiondata.setProtocol(parser.getText());
                    break;
                case "original":
                	xnatCrsessiondata.setOriginal(parser.getText());
                    break;
                case "date":
                	xnatCrsessiondata.setDate(parseDate(parser.getText()));
                    break;
                case "delay":
                	xnatCrsessiondata.setDelay(parser.getIntValue());
                    break;
                case "version":
                	xnatCrsessiondata.setVersion(parser.getIntValue());
                    break;
                case "acquisitionSite":
                	xnatCrsessiondata.setAcquisitionSite(parser.getText());
                    break;
                case "visit":
                	xnatCrsessiondata.setVisit(parser.getText());
                    break;
                case "visitId":
                	xnatCrsessiondata.setVisitId(parser.getText());
                    break;
            }
        }
        return xnatCrsessiondata;
    }

	@Override
	protected XnatCrsessiondata getNewInstance() throws JsonProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

}
