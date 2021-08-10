package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatSearch;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatSearchSerializer<T extends XdatSearch> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -2480487214662792484L;

    @SuppressWarnings("unchecked")
    public XdatSearchSerializer() {
        this((Class<T>) XdatSearch.class);
    }

    protected XdatSearchSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "page" property here: Integer
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "storedSearch" property here: org.nrg.xdat.om.XdatStoredSearchI
    }
}

