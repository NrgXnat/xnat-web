package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatScscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatScscandataSerializer<T extends XnatScscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = -762316148395102126L;

    @SuppressWarnings("unchecked")
    public XnatScscandataSerializer() {
        this((Class<T>) XnatScscandata.class);
    }

    protected XnatScscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T scan, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(scan, generator, provider);
        writeNonBlankField(generator, "seriesDescription", scan.getSeriesDescription());
        writeNonBlankField(generator, "type", scan.getType());
        writeNonBlankField(generator, "note", scan.getNote());
        writeNonBlankField(generator, "id", scan.getId());
        writeNonBlankField(generator, "quality", scan.getQuality());
    }
}
