package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetqcscandataProcessingerror;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatPetqcscandataProcessingerrorSerializer<T extends XnatPetqcscandataProcessingerror> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 1343526936937476984L;

    @SuppressWarnings("unchecked")
    public XnatPetqcscandataProcessingerrorSerializer() {
        this((Class<T>) XnatPetqcscandataProcessingerror.class);
    }

    protected XnatPetqcscandataProcessingerrorSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "processingerror" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "xnatPetqcscandataProcessingerrorId" property here: Integer
    }
}

