package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatImagescandata;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class XnatImagescandataSerializer extends AbstractBaseElementSerializer<XnatImagescandata> {
    public XnatImagescandataSerializer() {
        super(XnatImagescandata.class);
    }
    
    @Override
    protected void serializeImpl(final XnatImagescandata scan, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
    	writeNonNullNumber(generator, "xnatImagescandataId", scan.getXnatImagescandataId());
        writeNonBlankField(generator, "seriesDescription", scan.getSeriesDescription());
        writeNonBlankField(generator, "type", scan.getType());
        writeNonBlankField(generator, "note", scan.getNote());
        writeNonBlankField(generator, "id", scan.getId());
        writeNonBlankField(generator, "quality", scan.getQuality());
        writeNonBlankField(generator, "modality",scan.getModality());
        writeNonNullField(generator, "file",scan.getFile());
        
        
    }
}
