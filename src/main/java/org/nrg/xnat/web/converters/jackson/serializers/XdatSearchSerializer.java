package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatSearch;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XdatSearchSerializer<T extends XdatSearch> extends XdatStoredSearchSerializer<T> {
    private static final long serialVersionUID = -8263142834077214372L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatSearchSerializer() {
        this((Class<T>) XdatSearch.class);
    }

    protected XdatSearchSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "page", instance.getPage());
        // TODO: Write out the "storedSearch" property here: org.nrg.xdat.om.XdatStoredSearchI
        super.serializeImpl(instance, generator, provider);
    }
}

