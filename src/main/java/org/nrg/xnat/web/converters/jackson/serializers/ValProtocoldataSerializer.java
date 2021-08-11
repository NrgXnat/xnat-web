package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValProtocoldata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ValProtocoldataSerializer<T extends ValProtocoldata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 3695567943817688114L;

    @SuppressWarnings("unchecked")
    public ValProtocoldataSerializer() {
        this((Class<T>) ValProtocoldata.class);
    }

    protected ValProtocoldataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "check_additionalval" property here: org.nrg.xdat.model.ValAdditionalvalI
        // TODO: Write out the "check_comments_comment" property here: java.util.List
        // TODO: Write out the "check_conditions_condition" property here: java.util.List
        // TODO: Write out the "check_status" property here: String
        // TODO: Write out the "header" property here: String
        // TODO: Write out the "imageassessordata" property here: org.nrg.xdat.om.XnatImageassessordata
        // TODO: Write out the "precedence" property here: int
        // TODO: Write out the "scans_scanCheck" property here: java.util.List
        // TODO: Write out the "schemaElementName" property here: String
    }
}

