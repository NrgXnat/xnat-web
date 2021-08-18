package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValProtocoldataScanCheck;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class ValProtocoldataScanCheckSerializer<T extends ValProtocoldataScanCheck> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 1914063372336402405L;

    @SuppressWarnings({"unchecked", "unused"})
    public ValProtocoldataScanCheckSerializer() {
        this((Class<T>) ValProtocoldataScanCheck.class);
    }

    protected ValProtocoldataScanCheckSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "additionalval" property here: org.nrg.xdat.model.ValAdditionalvalI
        // TODO: Write out the "comments_comment" property here: java.util.List
        // TODO: Write out the "conditions_condition" property here: java.util.List
        writeNonBlankField(generator, "scanId", instance.getScanId());
        writeNonBlankField(generator, "status", instance.getStatus());
        writeNonNullNumber(generator, "valProtocoldataScanCheckId", instance.getValProtocoldataScanCheckId());
    }
}

