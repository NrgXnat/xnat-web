package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetassessordata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatPetassessordataSerializer<T extends XnatPetassessordata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -637593729543974431L;

    @SuppressWarnings("unchecked")
    public XnatPetassessordataSerializer() {
        this((Class<T>) XnatPetassessordata.class);
    }

    protected XnatPetassessordataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "imageassessordata" property here: org.nrg.xdat.om.XnatImageassessordata
        // TODO: Write out the "petSessionData" property here: org.nrg.xdat.om.XnatPetsessiondata
        // TODO: Write out the "schemaElementName" property here: String
    }
}

