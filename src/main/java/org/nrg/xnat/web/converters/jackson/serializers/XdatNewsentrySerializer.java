package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatNewsentry;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XdatNewsentrySerializer<T extends XdatNewsentry> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 7105237213881948132L;

    @SuppressWarnings({"unchecked", "unused"})
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
        writeNonBlankField(generator, "description", instance.getDescription());
        writeNonBlankField(generator, "link", instance.getLink());
        writeNonBlankField(generator, "title", instance.getTitle());
        writeNonNullNumber(generator, "xdatNewsentryId", instance.getXdatNewsentryId());
    }
}

