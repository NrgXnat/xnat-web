package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetqcscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatPetqcscandataSerializer<T extends XnatPetqcscandata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 8865714089481418100L;

    @SuppressWarnings("unchecked")
    public XnatPetqcscandataSerializer() {
        this((Class<T>) XnatPetqcscandata.class);
    }

    protected XnatPetqcscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "acceptablevoxelsize" property here: String
        // TODO: Write out the "acquisition" property here: String
        // TODO: Write out the "bottomcutoff" property here: String
        // TODO: Write out the "correctfilters" property here: String
        // TODO: Write out the "correctiterationsandsubsets" property here: String
        // TODO: Write out the "correctreconstructionalgorithm" property here: String
        // TODO: Write out the "correctslicethickness" property here: String
        // TODO: Write out the "processingerrors_processingerror" property here: java.util.List
        // TODO: Write out the "qcoutcome" property here: String
        // TODO: Write out the "qcoutcomereason" property here: String
        // TODO: Write out the "qcscandata" property here: org.nrg.xdat.om.XnatQcscandata
        // TODO: Write out the "reasonframesunacceptable" property here: String
        // TODO: Write out the "reconstructionalgorithmused" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "topcutoff" property here: String
        // TODO: Write out the "unacceptableframes" property here: String
    }
}

