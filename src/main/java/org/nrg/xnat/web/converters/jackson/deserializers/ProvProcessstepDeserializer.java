package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ProvProcessstep;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ProvProcessstepDeserializer<T extends ProvProcessstep> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -559791238462222057L;

    @SuppressWarnings({"unchecked", "unused"})
    public ProvProcessstepDeserializer() {
        this((Class<T>) ProvProcessstep.class);
    }

    protected ProvProcessstepDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "compiler":
                instance.setCompiler(parser.getText());
                break;
            case "compiler_version":
                instance.setCompiler_version(parser.getText());
                break;
            case "cvs":
                instance.setCvs(parser.getText());
                break;
            case "library":
                // TODO: Handle the "library" property here: java.util.List
                break;
            case "machine":
                instance.setMachine(parser.getText());
                break;
            case "platform":
                instance.setPlatform(parser.getText());
                break;
            case "platform_version":
                instance.setPlatform_version(parser.getText());
                break;
            case "program":
                instance.setProgram(parser.getText());
                break;
            case "program_arguments":
                instance.setProgram_arguments(parser.getText());
                break;
            case "program_version":
                instance.setProgram_version(parser.getText());
                break;
            case "provProcessstepId":
                instance.setProvProcessstepId(parser.getIntValue());
                break;
            case "timestamp":
                // TODO: Handle the "timestamp" property here: Object
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

