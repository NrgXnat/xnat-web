package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatFielddefinitiongroupField;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatFielddefinitiongroupFieldSerializer<T extends XnatFielddefinitiongroupField> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -5835717402032422055L;

    @SuppressWarnings("unchecked")
    public XnatFielddefinitiongroupFieldSerializer() {
        this((Class<T>) XnatFielddefinitiongroupField.class);
    }

    protected XnatFielddefinitiongroupFieldSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "cleanedXMLPath" property here: String
        // TODO: Write out the "datatype" property here: String
        // TODO: Write out the "group" property here: String
        // TODO: Write out the "name" property here: String
        // TODO: Write out the "possiblevalues_possiblevalue" property here: java.util.List
        // TODO: Write out the "required" property here: Boolean
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "sequence" property here: Integer
        // TODO: Write out the "type" property here: String
        // TODO: Write out the "xmlpath" property here: String
        // TODO: Write out the "xnatFielddefinitiongroupFieldId" property here: Integer
    }
}

