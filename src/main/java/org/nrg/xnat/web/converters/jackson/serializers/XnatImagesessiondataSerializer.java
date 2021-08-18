package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.nrg.xdat.om.XnatImagesessiondata;

import java.io.IOException;

public abstract class XnatImagesessiondataSerializer<T extends XnatImagesessiondata> extends XnatSubjectassessordataSerializer<T> {
    private static final long serialVersionUID = 2637948626487231432L;

    protected XnatImagesessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        writeNonBlankField(generator, "dcmAccessionNumber", instance.getDcmaccessionnumber());
        writeNonNullField(generator, "dcmPatientBirthDate", instance.getDcmpatientbirthdate());
        writeNonBlankField(generator, "dcmPatientId", instance.getDcmpatientid());
        writeNonBlankField(generator, "dcmPatientName", instance.getDcmpatientname());
        writeNonNullNumber(generator, "dcmPatientWeight", instance.getDcmpatientweight());
        writeNonBlankField(generator, "modality", instance.getModality());
        writeNonBlankField(generator, "operator", instance.getOperator());
        writeNonBlankField(generator, "prearchivePath", instance.getPrearchivepath());
        writeNonBlankField(generator, "scanner", instance.getScanner());
        writeNonBlankField(generator, "scanner_manufacturer", instance.getScanner_manufacturer());
        writeNonBlankField(generator, "scanner_model", instance.getScanner_model());
        writeNonBlankField(generator, "sessionType", instance.getSessionType());
        writeNonBlankField(generator, "studyId", instance.getStudyId());
        writeNonBlankField(generator, "uid", instance.getUid());
        generator.writeObjectField("assessors", instance.getAssessors());
        generator.writeObjectField("regions", instance.getRegions_region());
        generator.writeObjectField("scans", instance.getScans_scan());
        super.serializeImpl(instance, generator, provider);
    }
}
