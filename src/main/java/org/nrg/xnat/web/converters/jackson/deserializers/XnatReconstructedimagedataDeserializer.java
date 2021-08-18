package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatReconstructedimagedata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatReconstructedimagedataDeserializer<T extends XnatReconstructedimagedata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 7742094265779221661L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatReconstructedimagedataDeserializer() {
        this((Class<T>) XnatReconstructedimagedata.class);
    }

    protected XnatReconstructedimagedataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "basescantype":
                instance.setBasescantype(parser.getText());
                break;
            case "computations_datum":
                // TODO: Handle the "computations_datum" property here: java.util.List
                break;
            case "id":
                instance.setId(parser.getText());
                break;
            case "imageSessionId":
                instance.setImageSessionId(parser.getText());
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
            case "type":
                instance.setType(parser.getText());
                break;
            case "xnatReconstructedimagedataId":
                instance.setXnatReconstructedimagedataId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

