package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatSubjectassessordata;

import java.io.IOException;

public abstract class XnatImagesessiondataSerializer<T extends XnatImagesessiondata> extends XnatSubjectassessordataSerializer<T> {
    private static final long serialVersionUID = 5305227943944237734L;

    @SuppressWarnings("unchecked")
    public XnatImagesessiondataSerializer() {
        this((Class<T>) XnatImagesessiondata.class);
    }

    protected XnatImagesessiondataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(instance, generator, provider);
        writeNonBlankField(generator, "dcmAccessionNumber", instance.getDcmaccessionnumber());
        writeNonNullField(generator, "dcmPatientBirthDate", instance.getDcmpatientbirthdate());
        writeNonBlankField(generator, "dcmPatientId", instance.getDcmpatientid());
        writeNonBlankField(generator, "dcmPatientName", instance.getDcmpatientname());
        writeNonNullNumber(generator, "dcmPatientWeight", instance.getDcmpatientweight());
        writeNonBlankField(generator, "modality", instance.getModality());
        writeNonBlankField(generator, "operator", instance.getOperator());
        writeNonBlankField(generator, "prearchivePath", instance.getPrearchivepath());
        writeNonBlankField(generator, "scanner", instance.getScanner());
        writeNonBlankField(generator, "studyId", instance.getStudyId());
        writeNonBlankField(generator, "sessionType", instance.getSessionType());
        writeNonBlankField(generator, "uid", instance.getUid());
        generator.writeObjectField("assessors", instance.getAssessors());
        generator.writeObjectField("regions", instance.getRegions_region());
        generator.writeObjectField("scans", instance.getScans_scan());
    }

}
