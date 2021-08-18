package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAbstractprojectasset;

import java.io.IOException;

@Slf4j
public abstract class XnatAbstractprojectassetSerializer<T extends XnatAbstractprojectasset> extends XnatGenericdataSerializer<T> {
    private static final long serialVersionUID = 221158028789593825L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatAbstractprojectassetSerializer() {
        this((Class<T>) XnatAbstractprojectasset.class);
    }

    protected XnatAbstractprojectassetSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "experiments_experiment" property here: java.util.List
        // TODO: Write out the "subjects_subject" property here: java.util.List
        super.serializeImpl(instance, generator, provider);
    }
}

