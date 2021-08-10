package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAbstractstatistics;

import java.io.IOException;

@Slf4j
public abstract class XnatAbstractstatisticsDeserializer<T extends XnatAbstractstatistics> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 5068998685195685216L;

    protected XnatAbstractstatisticsDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "xnatAbstractstatisticsId":
                // TODO: Handle the "xnatAbstractstatisticsId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

