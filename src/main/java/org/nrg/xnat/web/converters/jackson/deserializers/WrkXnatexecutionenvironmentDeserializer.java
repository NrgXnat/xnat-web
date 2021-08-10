package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.WrkXnatexecutionenvironment;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class WrkXnatexecutionenvironmentDeserializer<T extends WrkXnatexecutionenvironment> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 7893179577482365764L;

    @SuppressWarnings("unchecked")
    public WrkXnatexecutionenvironmentDeserializer() {
        this((Class<T>) WrkXnatexecutionenvironment.class);
    }

    public WrkXnatexecutionenvironmentDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "abstractexecutionenvironment":
                // TODO: Handle the "abstractexecutionenvironment" property here: org.nrg.xdat.om.WrkAbstractexecutionenvironment
                break;
            case "catalogpath":
                // TODO: Handle the "catalogpath" property here: String
                break;
            case "datatype":
                // TODO: Handle the "datatype" property here: String
                break;
            case "host":
                // TODO: Handle the "host" property here: String
                break;
            case "notify":
                // TODO: Handle the "notify" property here: java.util.List
                break;
            case "parameterfile_path":
                // TODO: Handle the "parameterfile_path" property here: String
                break;
            case "parameterfile_xml":
                // TODO: Handle the "parameterfile_xml" property here: String
                break;
            case "parameters_parameter":
                // TODO: Handle the "parameters_parameter" property here: java.util.List
                break;
            case "pipeline":
                // TODO: Handle the "pipeline" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "startat":
                // TODO: Handle the "startat" property here: String
                break;
            case "supressnotification":
                // TODO: Handle the "supressnotification" property here: Boolean
                break;
            case "xnatuser":
                // TODO: Handle the "xnatuser" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

