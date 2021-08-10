package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatReconstructedimagedataScanid;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatReconstructedimagedataScanidSerializer<T extends XnatReconstructedimagedataScanid> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -1701724685556539224L;

    @SuppressWarnings("unchecked")
    public XnatReconstructedimagedataScanidSerializer() {
        this((Class<T>) XnatReconstructedimagedataScanid.class);
    }

    protected XnatReconstructedimagedataScanidSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "scanid" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "xnatReconstructedimagedataScanidId" property here: Integer
    }
}

