package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatSearch;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XdatSearchDeserializer<T extends XdatSearch> extends XdatStoredSearchDeserializer<T> {
    private static final long serialVersionUID = 3889468560202826434L;

    @SuppressWarnings({"unchecked", "unused"})
    public XdatSearchDeserializer() {
        this((Class<T>) XdatSearch.class);
    }

    protected XdatSearchDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "page":
                instance.setPage(parser.getIntValue());
                break;
            case "storedSearch":
                // TODO: Handle the "storedSearch" property here: org.nrg.xdat.om.XdatStoredSearchI
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

