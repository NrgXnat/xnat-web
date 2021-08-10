package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatImageassessordata;

import java.io.IOException;

@Slf4j
public abstract class XnatImageassessordataSerializer<T extends XnatImageassessordata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -5866321865427372083L;

    @SuppressWarnings("unchecked")
    public XnatImageassessordataSerializer() {
        this((Class<T>) XnatImageassessordata.class);
    }

    protected XnatImageassessordataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final XnatImageassessordata assessor, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonBlankField(generator, "id", assessor.getId());
        writeNonBlankField(generator, "label", assessor.getLabel());
        writeNonBlankField(generator, "project", assessor.getProject());
        writeNonBlankField(generator, "note", assessor.getNote());
        writeNonBlankField(generator, "protocol", assessor.getProtocol());
        writeNonBlankField(generator, "original", assessor.getOriginal());
        writeNonNullField(generator, "date", assessor.getDate());
        writeNonNullNumber(generator, "delay", assessor.getDelay());
        writeNonNullNumber(generator, "version", assessor.getVersion());
        writeNonBlankField(generator, "description", assessor.getDescription());
        writeNonNullField(generator, "fields", assessor.getFields_field());
        writeNonNullField(generator, "resources", assessor.getResources_resource());
        writeNonNullField(generator, "experiment", assessor.getExperimentdata());
        writeNonNullField(generator, "sessionData", assessor.getImageSessionData());
        writeNonNullField(generator, "scans", assessor.getImageSessionData().getScans_scan());
        writeNonNullField(generator, "outFile", assessor.getOut_file());
    }
}
