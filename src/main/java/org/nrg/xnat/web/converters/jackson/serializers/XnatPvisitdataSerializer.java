package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPvisitdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatPvisitdataSerializer<T extends XnatPvisitdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -6798417821975590191L;

    @SuppressWarnings("unchecked")
    public XnatPvisitdataSerializer() {
        this((Class<T>) XnatPvisitdata.class);
    }

    protected XnatPvisitdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "closed" property here: Boolean
        // TODO: Write out the "endDate" property here: Object
        // TODO: Write out the "genericdata" property here: org.nrg.xdat.om.XnatGenericdata
        // TODO: Write out the "notes" property here: String
        // TODO: Write out the "protocolid" property here: String
        // TODO: Write out the "protocolversion" property here: Integer
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "startDate" property here: Object
        // TODO: Write out the "status" property here: String
        // TODO: Write out the "subjectData" property here: org.nrg.xdat.om.XnatSubjectdata
        // TODO: Write out the "subjectId" property here: String
        // TODO: Write out the "terminal" property here: Boolean
        // TODO: Write out the "visitName" property here: String
        // TODO: Write out the "visitType" property here: String
        // TODO: Write out the "visits" property here: java.util.ArrayList
    }
}

