package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAbstractresource;
import org.nrg.xdat.om.XnatResource;

import java.io.IOException;

@Slf4j
public class XnatAbstractResourceDeserializer extends AbstractBaseElementDeserializer<XnatAbstractresource> {
    public XnatAbstractResourceDeserializer() {
        super(XnatAbstractresource.class);
    }

    @SuppressWarnings("null")
    @Override
    protected XnatAbstractresource deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final XnatAbstractresource resource = new XnatResource();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "fileCount":
                    resource.setFileCount(parser.getIntValue());
                    break;
                case "label":
                    resource.setLabel(parser.getText());
                    break;
                case "xnatAbstractResourceId":
                    resource.setXnatAbstractresourceId(parser.getIntValue());
                    break;
            }
        }
        return resource;
    }

	@Override
	protected XnatAbstractresource getNewInstance() throws JsonProcessingException {
		// TODO Auto-generated method stub
		return null;
	}
}
