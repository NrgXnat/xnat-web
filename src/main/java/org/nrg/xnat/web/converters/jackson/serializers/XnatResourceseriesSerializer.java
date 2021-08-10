package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatResourceseries;

import java.io.IOException;

@Slf4j
public abstract class XnatResourceseriesSerializer<T extends XnatResourceseries> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 5747797703911644376L;

    @SuppressWarnings("unchecked")
    public XnatResourceseriesSerializer() {
        this((Class<T>) XnatResourceseries.class);
    }

    protected XnatResourceseriesSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "abstractresource" property here: org.nrg.xdat.om.XnatAbstractresource
        // TODO: Write out the "cachepath" property here: String
        // TODO: Write out the "content" property here: String
        // TODO: Write out the "count" property here: Integer
        // TODO: Write out the "description" property here: String
        // TODO: Write out the "format" property here: String
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "path" property here: String
        // TODO: Write out the "pattern" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "unresolvedPaths" property here: java.util.ArrayList
    }
}

