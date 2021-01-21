package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatCtsessiondata;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class XnatCtsessiondataDeserializer extends AbstractBaseElementDeserializer<XnatCtsessiondata> {
    public XnatCtsessiondataDeserializer() {
        super(XnatCtsessiondata.class);
    }

    @Override
    protected XnatCtsessiondata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final XnatCtsessiondata xnatCtsessiondata = new XnatCtsessiondata();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "id":
                	xnatCtsessiondata.setId(parser.getText());
                    break;
                case "label":
                	xnatCtsessiondata.setLabel(parser.getText());
                    break;
                case "project":
                	xnatCtsessiondata.setProject(parser.getText());
                    break;
                case "note":
                	xnatCtsessiondata.setNote(parser.getText());
                    break;
                case "protocol":
                	xnatCtsessiondata.setProtocol(parser.getText());
                    break;
                case "original":
                	xnatCtsessiondata.setOriginal(parser.getText());
                    break;
                case "date":
                	xnatCtsessiondata.setDate(parseDate(parser.getText()));
                    break;
                case "delay":
                	xnatCtsessiondata.setDelay(parser.getIntValue());
                    break;
                case "version":
                	xnatCtsessiondata.setVersion(parser.getIntValue());
                    break;
                case "acquisitionSite":
                	xnatCtsessiondata.setAcquisitionSite(parser.getText());
                    break;
                case "visit":
                	xnatCtsessiondata.setVisit(parser.getText());
                    break;
                case "visitId":
                	xnatCtsessiondata.setVisitId(parser.getText());
                    break;
            }
        }
        return xnatCtsessiondata;
    }

}
