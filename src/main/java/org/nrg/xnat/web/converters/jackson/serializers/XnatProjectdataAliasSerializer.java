package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatProjectdataAlias;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatProjectdataAliasSerializer<T extends XnatProjectdataAlias> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -2025902598767922682L;

    @SuppressWarnings("unchecked")
    public XnatProjectdataAliasSerializer() {
        this((Class<T>) XnatProjectdataAlias.class);
    }

    protected XnatProjectdataAliasSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "alias" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "source" property here: String
        // TODO: Write out the "xnatProjectdataAliasId" property here: Integer
    }
}

