package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMrassessordata;

import java.io.IOException;

@Slf4j
public abstract class XnatMrassessordataSerializer<T extends XnatMrassessordata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 4823901910957704202L;

    @SuppressWarnings("unchecked")
    public XnatMrassessordataSerializer() {
        this((Class<T>) XnatMrassessordata.class);
    }

    protected XnatMrassessordataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "addParametersByName" property here: java.util.Hashtable
        // TODO: Write out the "imageassessordata" property here: org.nrg.xdat.om.XnatImageassessordata
        // TODO: Write out the "mrSessionData" property here: org.nrg.xdat.om.XnatMrsessiondata
        // TODO: Write out the "schemaElementName" property here: String
    }
}

