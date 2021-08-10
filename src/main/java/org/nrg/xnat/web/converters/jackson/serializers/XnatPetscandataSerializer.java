package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatPetscandataSerializer<T extends XnatPetscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = 9186548345503113190L;

    @SuppressWarnings("unchecked")
    public XnatPetscandataSerializer() {
        this((Class<T>) XnatPetscandata.class);
    }

    protected XnatPetscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T scan, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(scan, generator, provider);
        // Handle all properties unique to petScanData
    }
}
