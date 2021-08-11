package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStatisticsdataAddfield;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatStatisticsdataAddfieldSerializer<T extends XnatStatisticsdataAddfield> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 474206837182039612L;

    @SuppressWarnings("unchecked")
    public XnatStatisticsdataAddfieldSerializer() {
        this((Class<T>) XnatStatisticsdataAddfield.class);
    }

    protected XnatStatisticsdataAddfieldSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "addfield" property here: String
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "xnatStatisticsdataAddfieldId" property here: Integer
    }
}

