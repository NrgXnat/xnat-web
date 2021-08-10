package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatResourcecatalog;

import java.io.IOException;

@Slf4j
public class XnatResourcecatalogDeserializer<T extends XnatResourcecatalog> extends XnatResourceDeserializer<T> {
    private static final long serialVersionUID = 3231255490942201806L;

    @SuppressWarnings("unchecked")
    public XnatResourcecatalogDeserializer() {
        this((Class<T>) XnatResourcecatalog.class);
    }

    protected XnatResourcecatalogDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: These are all lower-level properties.
        switch (field) {
            case "fileCount":
                instance.setFileCount(parser.getIntValue());
                break;
            case "label":
                instance.setLabel(parser.getText());
                break;
            case "note":
                instance.setNote(parser.getText());
                break;
            case "description":
                instance.setDescription(parser.getText());
                break;
            case "content":
                instance.setContent(parser.getText());
                break;
            case "format":
                instance.setFormat(parser.getText());
                break;
            case "fileSize":
                instance.setFileSize(parser.getText());
                break;
            case "uri":
                instance.setUri(parser.getText());
                break;
            case "xnatAbstractResourceId":
                instance.setXnatAbstractresourceId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
