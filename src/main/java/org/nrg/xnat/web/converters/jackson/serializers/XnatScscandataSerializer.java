package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatScscandata;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

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
    protected void serializeImpl(final XnatScscandata scan, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(scan, generator, provider);
        writeNonNullNumber(generator, "xnatImagescandataId", scan.getXnatImagescandataId());
        writeNonBlankField(generator, "seriesDescription", scan.getSeriesDescription());
        writeNonBlankField(generator, "type", scan.getType());
        writeNonBlankField(generator, "note", scan.getNote());
        writeNonBlankField(generator, "id", scan.getId());
        writeNonBlankField(generator, "quality", scan.getQuality());
    }
}
