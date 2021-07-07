package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatImageassessordata;
import java.io.IOException;

@Slf4j
public class XnatImageassessordataDeserializer extends AbstractBaseElementDeserializer<XnatImageassessordata> {
    public XnatImageassessordataDeserializer() {
        super(XnatImageassessordata.class);
    }

    @Override
    protected XnatImageassessordata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final XnatImageassessordata assessor = new XnatImageassessordata();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "id":
                	assessor.setId(parser.getText());
                    break;
                case "label":
                	assessor.setLabel(parser.getText());
                    break;
                case "project":
                	assessor.setProject(parser.getText());
                    break;
                case "note":
                	assessor.setNote(parser.getText());
                    break;
                case "protocol":
                	assessor.setProtocol(parser.getText());
                    break;
                case "original":
                	assessor.setOriginal(parser.getText());
                    break;
                case "date":
                	assessor.setDate(parseDate(parser.getText()));
                    break;
                case "delay":
                	assessor.setDelay(parser.getIntValue());
                    break;
                case "version":
                	assessor.setVersion(parser.getIntValue());
                    break;
            }
        }
        return assessor;
    }

	@Override
	protected XnatImageassessordata getNewInstance() throws JsonProcessingException {
		// TODO Auto-generated method stub
		return null;
	}
}
