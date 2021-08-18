package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSubjectdataAddid;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatSubjectdataAddidSerializer<T extends XnatSubjectdataAddid> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 1043930964307149361L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatSubjectdataAddidSerializer() {
        this((Class<T>) XnatSubjectdataAddid.class);
    }

    protected XnatSubjectdataAddidSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "addid", instance.getAddid());
        writeNonBlankField(generator, "name", instance.getName());
        writeNonNullNumber(generator, "xnatSubjectdataAddidId", instance.getXnatSubjectdataAddidId());
    }
}

