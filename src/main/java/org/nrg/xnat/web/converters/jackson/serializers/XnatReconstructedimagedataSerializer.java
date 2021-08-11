package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatReconstructedimagedata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatReconstructedimagedataSerializer<T extends XnatReconstructedimagedata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -2434930617963538666L;

    @SuppressWarnings("unchecked")
    public XnatReconstructedimagedataSerializer() {
        this((Class<T>) XnatReconstructedimagedata.class);
    }

    protected XnatReconstructedimagedataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "basescantype" property here: String
        // TODO: Write out the "computations_datum" property here: java.util.List
        // TODO: Write out the "expectedSessionDir" property here: java.io.File
        // TODO: Write out the "imageSessionData" property here: org.nrg.xdat.om.XnatImagesessiondata
        // TODO: Write out the "imageSessionId" property here: String
        // TODO: Write out the "in_file" property here: java.util.List
        // TODO: Write out the "inscans_scanid" property here: java.util.List
        // TODO: Write out the "out_file" property here: java.util.List
        // TODO: Write out the "parameters_addparam" property here: java.util.List
        // TODO: Write out the "provenance" property here: org.nrg.xdat.om.ProvProcess
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "type" property here: String
        // TODO: Write out the "xnatReconstructedimagedataId" property here: Integer
    }
}

