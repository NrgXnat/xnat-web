package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xdat.om.XnatSubjectvariablesdata;
import org.nrg.xdat.om.XnatSubjectvariablesdataVariable;

import java.io.IOException;
import java.util.Map;

@XnatDeserializer
@Slf4j
public class XnatSubjectvariablesdataDeserializer<T extends XnatSubjectvariablesdata> extends XnatSubjectassessordataDeserializer<T> {
    private static final long serialVersionUID = -5218047536472296906L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatSubjectvariablesdataDeserializer() {
        this((Class<T>) XnatSubjectvariablesdata.class);
    }

    protected XnatSubjectvariablesdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "variables":
            	 final Map<String, String> fields = parser.readValueAs(MAP_STRING_STRING);
                 fields.forEach((key, value) -> {
                     final XnatSubjectvariablesdataVariable subjectVariable = new XnatSubjectvariablesdataVariable();
                     subjectVariable.setName(key);
                     subjectVariable.setVariable(value);
                     try {
                         instance.setVariables_variable(subjectVariable);
                     } catch (Exception e) {
                         log.error("Tried to set a field on an subject variables with name {} and variable {} but failed", key, value, e);
                     }
                 });
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

