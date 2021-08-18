package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetqcscandata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatPetqcscandataDeserializer<T extends XnatPetqcscandata> extends XnatQcscandataDeserializer<T> {
    private static final long serialVersionUID = -4983053614309290984L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatPetqcscandataDeserializer() {
        this((Class<T>) XnatPetqcscandata.class);
    }

    protected XnatPetqcscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "acceptablevoxelsize":
                instance.setAcceptablevoxelsize(parser.getText());
                break;
            case "acquisition":
                instance.setAcquisition(parser.getText());
                break;
            case "bottomcutoff":
                instance.setBottomcutoff(parser.getText());
                break;
            case "correctfilters":
                instance.setCorrectfilters(parser.getText());
                break;
            case "correctiterationsandsubsets":
                instance.setCorrectiterationsandsubsets(parser.getText());
                break;
            case "correctreconstructionalgorithm":
                instance.setCorrectreconstructionalgorithm(parser.getText());
                break;
            case "correctslicethickness":
                instance.setCorrectslicethickness(parser.getText());
                break;
            case "processingerrors_processingerror":
                // TODO: Handle the "processingerrors_processingerror" property here: java.util.List
                break;
            case "qcoutcome":
                instance.setQcoutcome(parser.getText());
                break;
            case "qcoutcomereason":
                instance.setQcoutcomereason(parser.getText());
                break;
            case "reasonframesunacceptable":
                instance.setReasonframesunacceptable(parser.getText());
                break;
            case "reconstructionalgorithmused":
                instance.setReconstructionalgorithmused(parser.getText());
                break;
            case "topcutoff":
                instance.setTopcutoff(parser.getText());
                break;
            case "unacceptableframes":
                instance.setUnacceptableframes(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

