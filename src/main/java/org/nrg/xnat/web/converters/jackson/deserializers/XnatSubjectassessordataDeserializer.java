package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatSubjectassessordata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatSubjectassessordataDeserializer<T extends XnatSubjectassessordata> extends XnatExperimentdataDeserializer<T> {
    private static final long serialVersionUID = -4853586378087807834L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatSubjectassessordataDeserializer() {
        this((Class<T>) XnatSubjectassessordata.class);
    }

    protected XnatSubjectassessordataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "age":
                instance.setAge(parser.getDoubleValue());
                break;
            case "subjectId":
                instance.setSubjectId(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
