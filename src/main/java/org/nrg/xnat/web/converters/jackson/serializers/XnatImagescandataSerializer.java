package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatImagescandata;

import java.io.IOException;

@Slf4j
public abstract class XnatImagescandataSerializer<T extends XnatImagescandata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 4541279478153604801L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatImagescandataSerializer() {
        this((Class<T>) XnatImagescandata.class);
    }

    protected XnatImagescandataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonBlankField(generator, "bodypartexamined", instance.getBodypartexamined());
        writeNonBlankField(generator, "condition", instance.getCondition());
        writeNonBlankField(generator, "documentation", instance.getDocumentation());
        writeNonNullField(generator, "file", instance.getFile());
        writeNonNullNumber(generator, "frames", instance.getFrames());
        writeNonBlankField(generator, "id", instance.getId());
        writeNonBlankField(generator, "imageSessionId", instance.getImageSessionId());
        writeNonBlankField(generator, "modality", instance.getModality());
        writeNonBlankField(generator, "note", instance.getNote());
        writeNonBlankField(generator, "operator", instance.getOperator());
        writeNonBlankField(generator, "project", instance.getProject());
        writeNonBlankField(generator, "protocolname", instance.getProtocolname());
        writeNonBlankField(generator, "quality", instance.getQuality());
        writeNonBlankField(generator, "requestedproceduredescription", instance.getRequestedproceduredescription());
        writeNonBlankField(generator, "scanner", instance.getScanner());
        writeNonBlankField(generator, "scannerManufacturer", instance.getScanner_manufacturer());
        writeNonBlankField(generator, "scannerModel", instance.getScanner_model());
        writeNonBlankField(generator, "scannerSoftwareVersion", instance.getScanner_softwareversion());
        writeNonBlankField(generator, "seriesClass", instance.getSeriesClass());
        writeNonBlankField(generator, "seriesDescription", instance.getSeriesDescription());
        // TODO: Write out the "sharing_share" property here: java.util.List
        // TODO: Write out the "startDate" property here: Object
        // TODO: Write out the "starttime" property here: Object
        writeNonBlankField(generator, "type", instance.getType());
        writeNonBlankField(generator, "uid", instance.getUid());
        // TODO: Write out the "validation" property here: org.nrg.xdat.model.XnatValidationdataI
        writeNonNullNumber(generator, "xnatImagescandataId", instance.getXnatImagescandataId());
    }
}
