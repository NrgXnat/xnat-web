package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEegsessiondata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatEegsessiondataDeserializer<T extends XnatEegsessiondata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -9106381451671943238L;

    @SuppressWarnings("unchecked")
    public XnatEegsessiondataDeserializer() {
        this((Class<T>) XnatEegsessiondata.class);
    }

    public XnatEegsessiondataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "dataformatversion":
                // TODO: Handle the "dataformatversion" property here: String
                break;
            case "imagesessiondata":
                // TODO: Handle the "imagesessiondata" property here: org.nrg.xdat.om.XnatImagesessiondata
                break;
            case "numberofchannels":
                // TODO: Handle the "numberofchannels" property here: Integer
                break;
            case "samplinginterval":
                // TODO: Handle the "samplinginterval" property here: Double
                break;
            case "samplinginterval_units":
                // TODO: Handle the "samplinginterval_units" property here: String
                break;
            case "samplingrate":
                // TODO: Handle the "samplingrate" property here: Double
                break;
            case "samplingrate_units":
                // TODO: Handle the "samplingrate_units" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

