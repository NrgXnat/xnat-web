package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatResourcecatalog;

import java.io.IOException;

@Slf4j
public class XnatResourcecatalogDeserializer extends AbstractBaseElementDeserializer<XnatResourcecatalog> {
    public XnatResourcecatalogDeserializer() {
        super(XnatResourcecatalog.class);
    }

  
    @Override
    protected XnatResourcecatalog deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final XnatResourcecatalog resource = new XnatResourcecatalog();

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
                case "note":
                    resource.setNote(parser.getText());
                    break;
                case "description":
                    resource.setDescription(parser.getText());
                    break;
                case "content":
                    resource.setContent(parser.getText());
                    break;
                case "format":
                    resource.setFormat(parser.getText());
                    break;
                case "fileSize":
                    resource.setFileSize(parser.getText());
                    break;
                case "uri":
                    resource.setUri(parser.getText());
                    break;
                case "xnatAbstractResourceId":
                    resource.setXnatAbstractresourceId(parser.getIntValue());
                    break;
            }
        }
        return resource;
    }


	@Override
	protected XnatResourcecatalog getNewInstance() throws JsonProcessingException {
		// TODO Auto-generated method stub
		return null;
	}
}
