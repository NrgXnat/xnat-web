package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XdatStoredSearchGroupid;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XdatStoredSearchGroupidSerializer<T extends XdatStoredSearchGroupid> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 5975463545047952368L;

    @SuppressWarnings("unchecked")
    public XdatStoredSearchGroupidSerializer() {
        this((Class<T>) XdatStoredSearchGroupid.class);
    }

    protected XdatStoredSearchGroupidSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "groupid" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "xdatStoredSearchGroupidId" property here: Integer
    }
}

