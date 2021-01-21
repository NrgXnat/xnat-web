package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatMrsessiondata;
import org.nrg.xdat.om.XnatPetmrsessiondata;
import org.nrg.xdat.om.XnatSubjectassessordata;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class XnatPetmrsessiondataDeserializer extends AbstractBaseElementDeserializer<XnatPetmrsessiondata> {
    public XnatPetmrsessiondataDeserializer() {
        super(XnatPetmrsessiondata.class);
    }

    @Override
    protected XnatPetmrsessiondata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final XnatPetmrsessiondata xnatPetmrsessiondata = new XnatPetmrsessiondata();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "id":
                	xnatPetmrsessiondata.setId(parser.getText());
                    break;
                case "label":
                	xnatPetmrsessiondata.setLabel(parser.getText());
                    break;
                case "project":
                    xnatPetmrsessiondata.setProject(parser.getText());
                    break;
                case "note":
                    xnatPetmrsessiondata.setNote(parser.getText());
                    break;
                case "protocol":
                    xnatPetmrsessiondata.setProtocol(parser.getText());
                    break;
                case "original":
                    xnatPetmrsessiondata.setOriginal(parser.getText());
                    break;
                case "date":
                    xnatPetmrsessiondata.setDate(parseDate(parser.getText()));
                    break;
                case "delay":
                    xnatPetmrsessiondata.setDelay(parser.getIntValue());
                    break;
                case "version":
                    xnatPetmrsessiondata.setVersion(parser.getIntValue());
                    break;
                case "acquisitionSite":
                    xnatPetmrsessiondata.setAcquisitionSite(parser.getText());
                    break;
                case "visit":
                    xnatPetmrsessiondata.setVisit(parser.getText());
                    break;
                case "visitId":
                    xnatPetmrsessiondata.setVisitId(parser.getText());
                    break;
            }
        }
        return xnatPetmrsessiondata;
    }

}
