package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatReconstructedimagedataScanid;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatReconstructedimagedataScanidSerializer<T extends XnatReconstructedimagedataScanid> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -1701724685556539224L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatReconstructedimagedataScanidSerializer() {
        this((Class<T>) XnatReconstructedimagedataScanid.class);
    }

    protected XnatReconstructedimagedataScanidSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "scanid", instance.getScanid());
        writeNonNullNumber(generator, "xnatReconstructedimagedataScanidId", instance.getXnatReconstructedimagedataScanidId());
    }
}

