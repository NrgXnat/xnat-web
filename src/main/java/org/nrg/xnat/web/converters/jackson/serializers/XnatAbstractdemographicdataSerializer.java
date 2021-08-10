package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAbstractdemographicdata;

import java.io.IOException;

@Slf4j
public abstract class XnatAbstractdemographicdataSerializer<T extends XnatAbstractdemographicdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 7888323666317690764L;

    protected XnatAbstractdemographicdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T demographic, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonNullNumber(generator, "xnatAbstractDemographicDataId", demographic.getXnatAbstractdemographicdataId());
    }
}
