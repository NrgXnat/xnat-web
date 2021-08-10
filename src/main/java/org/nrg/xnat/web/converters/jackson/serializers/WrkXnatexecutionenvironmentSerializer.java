package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.WrkXnatexecutionenvironment;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class WrkXnatexecutionenvironmentSerializer<T extends WrkXnatexecutionenvironment> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -929549265988116502L;

    @SuppressWarnings("unchecked")
    public WrkXnatexecutionenvironmentSerializer() {
        this((Class<T>) WrkXnatexecutionenvironment.class);
    }

    protected WrkXnatexecutionenvironmentSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "abstractexecutionenvironment" property here: org.nrg.xdat.om.WrkAbstractexecutionenvironment
        // TODO: Write out the "catalogpath" property here: String
        // TODO: Write out the "datatype" property here: String
        // TODO: Write out the "host" property here: String
        // TODO: Write out the "notify" property here: java.util.List
        // TODO: Write out the "parameterfile_path" property here: String
        // TODO: Write out the "parameterfile_xml" property here: String
        // TODO: Write out the "parameters_parameter" property here: java.util.List
        // TODO: Write out the "pipeline" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "startat" property here: String
        // TODO: Write out the "supressnotification" property here: Boolean
        // TODO: Write out the "xnatuser" property here: String
    }
}

