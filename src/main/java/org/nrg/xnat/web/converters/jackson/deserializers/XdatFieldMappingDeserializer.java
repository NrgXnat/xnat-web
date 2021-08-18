package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatFieldMapping;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatFieldMappingDeserializer<T extends XdatFieldMapping> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 3193645982034119956L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatFieldMappingDeserializer() {
        this((Class<T>) XdatFieldMapping.class);
    }

    protected XdatFieldMappingDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "activeElement":
                instance.setActiveElement(parser.getBooleanValue());
                break;
            case "comparisonType":
                instance.setComparisonType(parser.getText());
                break;
            case "createElement":
                instance.setCreateElement(parser.getBooleanValue());
                break;
            case "deleteElement":
                instance.setDeleteElement(parser.getBooleanValue());
                break;
            case "editElement":
                instance.setEditElement(parser.getBooleanValue());
                break;
            case "field":
                instance.setField(parser.getText());
                break;
            case "fieldValue":
                instance.setFieldValue(parser.getText());
                break;
            case "readElement":
                instance.setReadElement(parser.getBooleanValue());
                break;
            case "xdatFieldMappingId":
                instance.setXdatFieldMappingId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

