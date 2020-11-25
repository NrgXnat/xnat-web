package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
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

    @SuppressWarnings("null")
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
                case "collection":
                    resource.setLabel(parser.getText());
                    break;
                case "xnatAbstractResourceId":
                    resource.setXnatAbstractresourceId(parser.getIntValue());
                    break;
            }
        }
        return resource;
    }
}
