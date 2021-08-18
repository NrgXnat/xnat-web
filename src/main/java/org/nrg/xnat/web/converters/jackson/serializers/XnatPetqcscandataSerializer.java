package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetqcscandata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatPetqcscandataSerializer<T extends XnatPetqcscandata> extends XnatQcscandataSerializer<T> {
    private static final long serialVersionUID = 412645608710474220L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatPetqcscandataSerializer() {
        this((Class<T>) XnatPetqcscandata.class);
    }

    protected XnatPetqcscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "acceptablevoxelsize", instance.getAcceptablevoxelsize());
        writeNonBlankField(generator, "acquisition", instance.getAcquisition());
        writeNonBlankField(generator, "bottomcutoff", instance.getBottomcutoff());
        writeNonBlankField(generator, "correctfilters", instance.getCorrectfilters());
        writeNonBlankField(generator, "correctiterationsandsubsets", instance.getCorrectiterationsandsubsets());
        writeNonBlankField(generator, "correctreconstructionalgorithm", instance.getCorrectreconstructionalgorithm());
        writeNonBlankField(generator, "correctslicethickness", instance.getCorrectslicethickness());
        // TODO: Write out the "processingerrors_processingerror" property here: java.util.List
        writeNonBlankField(generator, "qcoutcome", instance.getQcoutcome());
        writeNonBlankField(generator, "qcoutcomereason", instance.getQcoutcomereason());
        writeNonBlankField(generator, "reasonframesunacceptable", instance.getReasonframesunacceptable());
        writeNonBlankField(generator, "reconstructionalgorithmused", instance.getReconstructionalgorithmused());
        writeNonBlankField(generator, "topcutoff", instance.getTopcutoff());
        writeNonBlankField(generator, "unacceptableframes", instance.getUnacceptableframes());
        super.serializeImpl(instance, generator, provider);
    }
}

