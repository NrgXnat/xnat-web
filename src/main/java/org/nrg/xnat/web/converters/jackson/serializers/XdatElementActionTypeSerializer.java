package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatElementActionType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatElementActionTypeSerializer<T extends XdatElementActionType> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -6799689820540613247L;

    @SuppressWarnings("unchecked")
    public XdatElementActionTypeSerializer() {
        this((Class<T>) XdatElementActionType.class);
    }

    protected XdatElementActionTypeSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "displayName" property here: String
        // TODO: Write out the "elementActionName" property here: String
        // TODO: Write out the "image" property here: String
        // TODO: Write out the "parameterstring" property here: String
        // TODO: Write out the "popup" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "secureaccess" property here: String
        // TODO: Write out the "securefeature" property here: String
        // TODO: Write out the "sequence" property here: Integer
        // TODO: Write out the "xdatElementActionTypeId" property here: Integer
    }
}

