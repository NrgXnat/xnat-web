package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMrqcscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatMrqcscandataSerializer<T extends XnatMrqcscandata> extends XnatQcscandataSerializer<T> {
    private static final long serialVersionUID = 6055751745499328379L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatMrqcscandataSerializer() {
        this((Class<T>) XnatMrqcscandata.class);
    }

    protected XnatMrqcscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "blurring", instance.getBlurring());
        writeNonBlankField(generator, "flow", instance.getFlow());
        writeNonBlankField(generator, "imagecontrast", instance.getImagecontrast());
        writeNonBlankField(generator, "inhomogeneity", instance.getInhomogeneity());
        writeNonBlankField(generator, "interpacmotion", instance.getInterpacmotion());
        writeNonBlankField(generator, "susceptibility", instance.getSusceptibility());
        writeNonBlankField(generator, "wrap", instance.getWrap());
        super.serializeImpl(instance, generator, provider);
    }
}

