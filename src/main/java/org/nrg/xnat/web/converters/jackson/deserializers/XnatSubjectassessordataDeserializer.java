package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.nrg.xdat.om.XnatSubjectassessordata;

import java.io.IOException;

public abstract class XnatSubjectassessordataDeserializer<T extends XnatSubjectassessordata> extends XnatExperimentdataDeserializer<T> {
    private static final long serialVersionUID = 8210237025631857679L;

    protected XnatSubjectassessordataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "subjectId":
                instance.setSubjectId(parser.getText());
                break;
            case "age":
                instance.setAge(parser.getValueAsDouble());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
