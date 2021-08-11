package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatEegscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatEegscandataDeserializer<T extends XnatEegscandata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -2432170054339054812L;

    @SuppressWarnings("unchecked")
    public XnatEegscandataDeserializer() {
        this((Class<T>) XnatEegscandata.class);
    }

    public XnatEegscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "channels_channel":
                // TODO: Handle the "channels_channel" property here: java.util.List
                break;
            case "imagescandata":
                // TODO: Handle the "imagescandata" property here: org.nrg.xdat.om.XnatImagescandata
                break;
            case "parameters_datarecord_duration":
                // TODO: Handle the "parameters_datarecord_duration" property here: Double
                break;
            case "parameters_datarecord_units":
                // TODO: Handle the "parameters_datarecord_units" property here: String
                break;
            case "parameters_numberofdatarecords":
                // TODO: Handle the "parameters_numberofdatarecords" property here: Integer
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "softwarefiltersimpedances_impedance":
                // TODO: Handle the "softwarefiltersimpedances_impedance" property here: java.util.List
                break;
            case "softwarefiltersimpedances_mean":
                // TODO: Handle the "softwarefiltersimpedances_mean" property here: Double
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

