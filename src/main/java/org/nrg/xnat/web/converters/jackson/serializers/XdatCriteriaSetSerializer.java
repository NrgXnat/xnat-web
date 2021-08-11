package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatCriteriaSet;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatCriteriaSetSerializer<T extends XdatCriteriaSet> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 2232620719647355109L;

    @SuppressWarnings("unchecked")
    public XdatCriteriaSetSerializer() {
        this((Class<T>) XdatCriteriaSet.class);
    }

    protected XdatCriteriaSetSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "childSet" property here: java.util.ArrayList
        // TODO: Write out the "criteria" property here: java.util.ArrayList
        // TODO: Write out the "method" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "xdatCriteriaSetId" property here: Integer
    }
}

