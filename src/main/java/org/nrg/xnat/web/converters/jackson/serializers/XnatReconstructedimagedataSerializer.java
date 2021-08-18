package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatReconstructedimagedata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatReconstructedimagedataSerializer<T extends XnatReconstructedimagedata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -2434930617963538666L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatReconstructedimagedataSerializer() {
        this((Class<T>) XnatReconstructedimagedata.class);
    }

    protected XnatReconstructedimagedataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "basescantype", instance.getBasescantype());
        // TODO: Write out the "computations_datum" property here: java.util.List
        writeNonBlankField(generator, "id", instance.getId());
        writeNonBlankField(generator, "imageSessionId", instance.getImageSessionId());
        // TODO: Write out the "in_file" property here: java.util.List
        // TODO: Write out the "inscans_scanid" property here: java.util.List
        // TODO: Write out the "out_file" property here: java.util.List
        // TODO: Write out the "parameters_addparam" property here: java.util.List
        // TODO: Write out the "provenance" property here: org.nrg.xdat.om.ProvProcess
        writeNonBlankField(generator, "type", instance.getType());
        writeNonNullNumber(generator, "xnatReconstructedimagedataId", instance.getXnatReconstructedimagedataId());
    }
}

