package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xdat.om.XnatDatatypeprotocol;
import org.nrg.xdat.om.XnatFielddefinitiongroup;
import org.nrg.xft.ItemI;
import java.io.IOException;

@Slf4j
public class XnatDatatypeprotocolDeserializer extends AbstractBaseElementDeserializer<XnatDatatypeprotocol> {
	private static final long serialVersionUID = 3313417256437766930L;

	public XnatDatatypeprotocolDeserializer() {
        super(XnatDatatypeprotocol.class);
    }

    @Override
    protected XnatDatatypeprotocol deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final XnatDatatypeprotocol protocol = new XnatDatatypeprotocol();
//        final XnatFielddefinitiongroup definition = new XnatFielddefinitiongroup();
//        try {
//        	protocol.setDefinitions_definition((ItemI)definition);
//        } catch (Exception e) {
//            log.error("An error occurred trying to set demographics data while deserializing an object. Sorry about that.", e);
//        }
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "id":
                	protocol.setId(parser.getText());
                    break;
                case "description":
                	protocol.setDescription(parser.getText());
                    break;
                case "name":
                	protocol.setName(parser.getText());
                    break;
                case "xnatAbstractProtocolId":
                   protocol.setXnatAbstractprotocolId(parser.getIntValue());
                    break;
                case "dataType":
                	protocol.setDataType(parser.getText());
                    break;
//                case "projectSpecific":
//                	definition.setProjectSpecific(parser.getText());
//                    break;
//                case "ID":
//                	definition.setId(parser.getText());
//                    break;
            }
        }
        return protocol;
    }
}
