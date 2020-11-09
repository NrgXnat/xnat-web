package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatScscandata;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class XnatScscandataSerializer extends AbstractBaseElementSerializer<XnatScscandata> {
    public XnatScscandataSerializer() {
        super(XnatScscandata.class);
    }
    
    @Override
    protected void serializeImpl(final XnatScscandata scan, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
    	writeNonNullNumber(generator, "xnatImagescandataId", scan.getXnatImagescandataId());
        writeNonBlankField(generator, "seriesDescription", scan.getSeriesDescription());
        writeNonBlankField(generator, "type", scan.getType());
        writeNonBlankField(generator, "note", scan.getNote());
        writeNonBlankField(generator, "id", scan.getId());
        writeNonBlankField(generator, "quality", scan.getQuality());
    }
}
