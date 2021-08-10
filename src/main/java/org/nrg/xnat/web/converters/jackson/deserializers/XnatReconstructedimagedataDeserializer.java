package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatReconstructedimagedata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatReconstructedimagedataDeserializer<T extends XnatReconstructedimagedata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 8893444659693752316L;

    @SuppressWarnings("unchecked")
    public XnatReconstructedimagedataDeserializer() {
        this((Class<T>) XnatReconstructedimagedata.class);
    }

    public XnatReconstructedimagedataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "basescantype":
                // TODO: Handle the "basescantype" property here: String
                break;
            case "computations_datum":
                // TODO: Handle the "computations_datum" property here: java.util.List
                break;
            case "expectedSessionDir":
                // TODO: Handle the "expectedSessionDir" property here: java.io.File
                break;
            case "imageSessionData":
                // TODO: Handle the "imageSessionData" property here: org.nrg.xdat.om.XnatImagesessiondata
                break;
            case "imageSessionId":
                // TODO: Handle the "imageSessionId" property here: String
                break;
            case "in_file":
                // TODO: Handle the "in_file" property here: java.util.List
                break;
            case "inscans_scanid":
                // TODO: Handle the "inscans_scanid" property here: java.util.List
                break;
            case "out_file":
                // TODO: Handle the "out_file" property here: java.util.List
                break;
            case "parameters_addparam":
                // TODO: Handle the "parameters_addparam" property here: java.util.List
                break;
            case "provenance":
                // TODO: Handle the "provenance" property here: org.nrg.xdat.om.ProvProcess
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "type":
                // TODO: Handle the "type" property here: String
                break;
            case "xnatReconstructedimagedataId":
                // TODO: Handle the "xnatReconstructedimagedataId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

