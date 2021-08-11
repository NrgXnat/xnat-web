package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatQcassessmentdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatQcassessmentdataSerializer<T extends XnatQcassessmentdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -6663045768801399164L;

    @SuppressWarnings("unchecked")
    public XnatQcassessmentdataSerializer() {
        this((Class<T>) XnatQcassessmentdata.class);
    }

    protected XnatQcassessmentdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "header" property here: String
        // TODO: Write out the "mrassessordata" property here: org.nrg.xdat.om.XnatMrassessordata
        // TODO: Write out the "precedence" property here: int
        // TODO: Write out the "scans_scan" property here: java.util.List
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "type" property here: String
    }
}

