package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMrscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatMrscandataSerializer<T extends XnatMrscandata> extends XnatImagescandataSerializer<T> {
    private static final long serialVersionUID = -2280595938099074217L;

    @SuppressWarnings("unchecked")
    public XnatMrscandataSerializer() {
        this((Class<T>) XnatMrscandata.class);
    }

    protected XnatMrscandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final XnatMrscandata scan, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(scan, generator, provider);
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
        writeNonBlankField(generator, "scannerManufacturer", scan.getScanner_manufacturer());
        writeNonBlankField(generator, "scannerModel", scan.getScanner_model());
        writeNonBlankField(generator, "scannerSoftwareVersion", scan.getScanner_softwareversion());
        writeNonBlankField(generator, "seriesClass", scan.getSeriesClass());
        writeNonBlankField(generator, "operator", scan.getOperator());
        writeNonNullNumber(generator, "frame", scan.getFrames());
        writeNonBlankField(generator, "modality", scan.getModality());
        writeNonNullField(generator, "file", scan.getFile());


    }
}
