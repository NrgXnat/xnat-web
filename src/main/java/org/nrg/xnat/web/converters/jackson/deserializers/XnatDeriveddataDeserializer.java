package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nrg.xdat.om.ProvProcess;
import org.nrg.xdat.om.XnatDeriveddata;
import org.nrg.xft.ItemI;

import java.io.IOException;

@Slf4j
public abstract class XnatDeriveddataDeserializer<T extends XnatDeriveddata> extends XnatExperimentdataDeserializer<T> {
    private static final long serialVersionUID = -1168750093984479213L;

    protected XnatDeriveddataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        if (StringUtils.equals(field, "provenance")) {
            // TODO: Requires deserialization for ProvProcess objects...
            final ProvProcess provProcess = parser.readValueAs(ProvProcess.class);
            try {
                instance.setProvenance((ItemI) provProcess);
            } catch (Exception e) {
                log.error("An error occurred trying to deserialize provenance", e);
            }
        } else {
            super.handleField(instance, field, parser, context);
        }
    }
}
