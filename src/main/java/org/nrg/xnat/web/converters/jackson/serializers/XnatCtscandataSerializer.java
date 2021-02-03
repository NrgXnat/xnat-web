package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatCtscandata;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class XnatCtscandataSerializer extends AbstractBaseElementSerializer<XnatCtscandata> {
    public XnatCtscandataSerializer() {
        super(XnatCtscandata.class);
    }
    
    @Override
    protected void serializeImpl(final XnatCtscandata scan, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
    	writeNonNullNumber(generator, "xnatImagescandataId", scan.getXnatImagescandataId());
    	writeNonBlankField(generator, "id", scan.getId());
    	writeNonBlankField(generator, "project", scan.getProject());
    	writeNonBlankField(generator, "imageSessionId", scan.getImageSessionId());
        writeNonBlankField(generator, "seriesDescription", scan.getSeriesDescription());
        writeNonBlankField(generator, "type", scan.getType());
        writeNonBlankField(generator, "note", scan.getNote());
        writeNonBlankField(generator, "quality", scan.getQuality());
        writeNonBlankField(generator, "condition", scan.getCondition());
        writeNonBlankField(generator, "documentation", scan.getDocumentation());
        writeNonBlankField(generator, "scanner", scan.getScanner());
        writeNonBlankField(generator, "scannerManufacturer",scan.getScanner_manufacturer());
        writeNonBlankField(generator, "scannerModel", scan.getScanner_model());
        writeNonBlankField(generator, "scannerSoftwareVersion", scan.getScanner_softwareversion());
        writeNonBlankField(generator, "seriesClass", scan.getSeriesClass());
        writeNonBlankField(generator, "operator", scan.getOperator());
        writeNonNullNumber(generator, "frame", scan.getFrames());
        writeNonBlankField(generator, "modality",scan.getModality());
        writeNonNullField(generator, "file",scan.getFile());
        
        
    }
}
