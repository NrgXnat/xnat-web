package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAbstractresourceTag;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatAbstractresourceTagSerializer<T extends XnatAbstractresourceTag> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 852834119145277548L;

    @SuppressWarnings("unchecked")
    public XnatAbstractresourceTagSerializer() {
        this((Class<T>) XnatAbstractresourceTag.class);
    }

    protected XnatAbstractresourceTagSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "xnatAbstractresourceTagId" property here: Integer
    }
}

