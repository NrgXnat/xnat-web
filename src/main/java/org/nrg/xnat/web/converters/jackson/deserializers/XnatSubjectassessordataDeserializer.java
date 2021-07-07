package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatSubjectassessordata;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class XnatSubjectassessordataDeserializer extends AbstractBaseElementDeserializer<XnatSubjectassessordata> {
	private static final long serialVersionUID = 8210237025631857679L;

	public XnatSubjectassessordataDeserializer() {
        super(XnatSubjectassessordata.class);
    }

    @Override
    protected XnatSubjectassessordata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final XnatSubjectassessordata xnatSubjectassessordata = new XnatSubjectassessordata();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "id":
                	xnatSubjectassessordata.setId(parser.getText());
                    break;
                case "label":
                	xnatSubjectassessordata.setLabel(parser.getText());
                    break;
                case "project":
                    xnatSubjectassessordata.setProject(parser.getText());
                    break;
                case "note":
                    xnatSubjectassessordata.setNote(parser.getText());
                    break;
                case "protocol":
                    xnatSubjectassessordata.setProtocol(parser.getText());
                    break;
                case "original":
                    xnatSubjectassessordata.setOriginal(parser.getText());
                    break;
                case "date":
                    xnatSubjectassessordata.setDate(parseDate(parser.getText()));
                    break;
                case "delay":
                    xnatSubjectassessordata.setDelay(parser.getIntValue());
                    break;
                case "version":
                    xnatSubjectassessordata.setVersion(parser.getIntValue());
                    break;
                case "acquisitionSite":
                    xnatSubjectassessordata.setAcquisitionSite(parser.getText());
                    break;
                case "visit":
                    xnatSubjectassessordata.setVisit(parser.getText());
                    break;
                case "visitId":
                    xnatSubjectassessordata.setVisitId(parser.getText());
                    break;
                case "xsiType":
                	xnatSubjectassessordata.getItem().setXmlType("xnat:mrSessionData");
                    break;
            }
        }
        return xnatSubjectassessordata;
    }

	@Override
	protected XnatSubjectassessordata getNewInstance() throws JsonProcessingException {
		return null;
	}

}
