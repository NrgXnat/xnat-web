package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatNewsentry;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatNewsentrySerializer<T extends XdatNewsentry> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 7105237213881948132L;

    @SuppressWarnings("unchecked")
    public XdatNewsentrySerializer() {
        this((Class<T>) XdatNewsentry.class);
    }

    protected XdatNewsentrySerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "date" property here: Object
        // TODO: Write out the "description" property here: String
        // TODO: Write out the "link" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "title" property here: String
        // TODO: Write out the "xdatNewsentryId" property here: Integer
    }
}

