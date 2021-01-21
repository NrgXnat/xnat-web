package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatMrsessiondata;
import org.nrg.xdat.om.XnatPetsessiondata;
import org.nrg.xdat.om.XnatSubjectassessordata;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class XnatPetsessiondataDeserializer extends AbstractBaseElementDeserializer<XnatPetsessiondata> {
    public XnatPetsessiondataDeserializer() {
        super(XnatPetsessiondata.class);
    }

    @Override
    protected XnatPetsessiondata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final XnatPetsessiondata xnatPetsessiondata = new XnatPetsessiondata();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "id":
                	xnatPetsessiondata.setId(parser.getText());
                    break;
                case "label":
                	xnatPetsessiondata.setLabel(parser.getText());
                    break;
                case "project":
                    xnatPetsessiondata.setProject(parser.getText());
                    break;
                case "note":
                    xnatPetsessiondata.setNote(parser.getText());
                    break;
                case "protocol":
                    xnatPetsessiondata.setProtocol(parser.getText());
                    break;
                case "original":
                    xnatPetsessiondata.setOriginal(parser.getText());
                    break;
                case "date":
                    xnatPetsessiondata.setDate(parseDate(parser.getText()));
                    break;
                case "delay":
                    xnatPetsessiondata.setDelay(parser.getIntValue());
                    break;
                case "version":
                    xnatPetsessiondata.setVersion(parser.getIntValue());
                    break;
                case "acquisitionSite":
                    xnatPetsessiondata.setAcquisitionSite(parser.getText());
                    break;
                case "visit":
                    xnatPetsessiondata.setVisit(parser.getText());
                    break;
                case "visitId":
                    xnatPetsessiondata.setVisitId(parser.getText());
                    break;
            }
        }
        return xnatPetsessiondata;
    }

}
