package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAbstractprojectasset;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatAbstractprojectassetSerializer<T extends XnatAbstractprojectasset> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -8284400512882620955L;

    @SuppressWarnings("unchecked")
    public XnatAbstractprojectassetSerializer() {
        this((Class<T>) XnatAbstractprojectasset.class);
    }

    protected XnatAbstractprojectassetSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "experimentIds" property here: java.util.List
        // TODO: Write out the "experiments_experiment" property here: java.util.List
        // TODO: Write out the "genericdata" property here: org.nrg.xdat.om.XnatGenericdata
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "subjectIds" property here: java.util.List
        // TODO: Write out the "subjects_subject" property here: java.util.List
    }
}

