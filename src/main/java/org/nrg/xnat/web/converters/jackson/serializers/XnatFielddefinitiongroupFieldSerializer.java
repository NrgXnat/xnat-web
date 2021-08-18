package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatFielddefinitiongroupField;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatFielddefinitiongroupFieldSerializer<T extends XnatFielddefinitiongroupField> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -5835717402032422055L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatFielddefinitiongroupFieldSerializer() {
        this((Class<T>) XnatFielddefinitiongroupField.class);
    }

    protected XnatFielddefinitiongroupFieldSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "datatype", instance.getDatatype());
        writeNonBlankField(generator, "group", instance.getGroup());
        writeNonBlankField(generator, "name", instance.getName());
        // TODO: Write out the "possiblevalues_possiblevalue" property here: java.util.List
        writeNonNullBoolean(generator, "required", instance.getRequired());
        writeNonNullNumber(generator, "sequence", instance.getSequence());
        writeNonBlankField(generator, "type", instance.getType());
        writeNonBlankField(generator, "xmlpath", instance.getXmlpath());
        writeNonNullNumber(generator, "xnatFielddefinitiongroupFieldId", instance.getXnatFielddefinitiongroupFieldId());
    }
}

