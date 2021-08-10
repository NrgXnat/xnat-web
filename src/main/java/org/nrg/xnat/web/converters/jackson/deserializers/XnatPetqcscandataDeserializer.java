package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPetqcscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatPetqcscandataDeserializer<T extends XnatPetqcscandata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 6098203297984165315L;

    @SuppressWarnings("unchecked")
    public XnatPetqcscandataDeserializer() {
        this((Class<T>) XnatPetqcscandata.class);
    }

    public XnatPetqcscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "acceptablevoxelsize":
                // TODO: Handle the "acceptablevoxelsize" property here: String
                break;
            case "acquisition":
                // TODO: Handle the "acquisition" property here: String
                break;
            case "bottomcutoff":
                // TODO: Handle the "bottomcutoff" property here: String
                break;
            case "correctfilters":
                // TODO: Handle the "correctfilters" property here: String
                break;
            case "correctiterationsandsubsets":
                // TODO: Handle the "correctiterationsandsubsets" property here: String
                break;
            case "correctreconstructionalgorithm":
                // TODO: Handle the "correctreconstructionalgorithm" property here: String
                break;
            case "correctslicethickness":
                // TODO: Handle the "correctslicethickness" property here: String
                break;
            case "processingerrors_processingerror":
                // TODO: Handle the "processingerrors_processingerror" property here: java.util.List
                break;
            case "qcoutcome":
                // TODO: Handle the "qcoutcome" property here: String
                break;
            case "qcoutcomereason":
                // TODO: Handle the "qcoutcomereason" property here: String
                break;
            case "qcscandata":
                // TODO: Handle the "qcscandata" property here: org.nrg.xdat.om.XnatQcscandata
                break;
            case "reasonframesunacceptable":
                // TODO: Handle the "reasonframesunacceptable" property here: String
                break;
            case "reconstructionalgorithmused":
                // TODO: Handle the "reconstructionalgorithmused" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "topcutoff":
                // TODO: Handle the "topcutoff" property here: String
                break;
            case "unacceptableframes":
                // TODO: Handle the "unacceptableframes" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

