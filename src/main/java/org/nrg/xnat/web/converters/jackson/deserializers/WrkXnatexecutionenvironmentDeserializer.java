package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.WrkXnatexecutionenvironment;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class WrkXnatexecutionenvironmentDeserializer<T extends WrkXnatexecutionenvironment> extends WrkAbstractexecutionenvironmentDeserializer<T> {
    private static final long serialVersionUID = -7716101583701743075L;

    @SuppressWarnings({"unchecked", "unused"})
    public WrkXnatexecutionenvironmentDeserializer() {
        this((Class<T>) WrkXnatexecutionenvironment.class);
    }

    protected WrkXnatexecutionenvironmentDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "catalogpath":
                instance.setCatalogpath(parser.getText());
                break;
            case "datatype":
                instance.setDatatype(parser.getText());
                break;
            case "host":
                instance.setHost(parser.getText());
                break;
            case "id":
                instance.setId(parser.getText());
                break;
            case "log":
                instance.setLog(parser.getText());
                break;
            case "notify":
                // TODO: Handle the "notify" property here: java.util.List
                break;
            case "parameterfile_path":
                instance.setParameterfile_path(parser.getText());
                break;
            case "parameterfile_xml":
                instance.setParameterfile_xml(parser.getText());
                break;
            case "parameters_parameter":
                // TODO: Handle the "parameters_parameter" property here: java.util.List
                break;
            case "pipeline":
                instance.setPipeline(parser.getText());
                break;
            case "startat":
                instance.setStartat(parser.getText());
                break;
            case "supressnotification":
                instance.setSupressnotification(parser.getBooleanValue());
                break;
            case "xnatuser":
                instance.setXnatuser(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

