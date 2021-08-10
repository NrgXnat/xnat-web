package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatSubjectassessordata;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

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
    protected void serializeImpl(final XnatImagesessiondata xnatImagesessiondata, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        super.serializeImpl(xnatImagesessiondata, generator, provider);
        writeNonBlankField(generator, "dcmAccessionNumber", xnatImagesessiondata.getDcmaccessionnumber());
        writeNonNullField(generator, "dcmPatientBirthDate", xnatImagesessiondata.getDcmpatientbirthdate());
        writeNonBlankField(generator, "dcmPatientId", xnatImagesessiondata.getDcmpatientid());
        writeNonBlankField(generator, "dcmPatientName", xnatImagesessiondata.getDcmpatientname());
        writeNonNullNumber(generator, "dcmPatientWeight", xnatImagesessiondata.getDcmpatientweight());
        writeNonBlankField(generator, "modality", xnatImagesessiondata.getModality());
        writeNonBlankField(generator, "operator", xnatImagesessiondata.getOperator());
        writeNonBlankField(generator, "prearchivePath", xnatImagesessiondata.getPrearchivepath());
        writeNonBlankField(generator, "scanner", xnatImagesessiondata.getScanner());
        writeNonBlankField(generator, "studyId", xnatImagesessiondata.getStudyId());
        writeNonBlankField(generator, "sessionType", xnatImagesessiondata.getSessionType());
        writeNonBlankField(generator, "uid", xnatImagesessiondata.getUid());
        generator.writeObjectField("assessors", xnatImagesessiondata.getAssessors());
        generator.writeObjectField("regions", xnatImagesessiondata.getRegions_region());
        generator.writeObjectField("scans", xnatImagesessiondata.getScans_scan());
        generator.writeFieldName("subjectAssessor");
        provider.findValueSerializer(XnatSubjectassessordata.class).serialize(xnatImagesessiondata, generator, provider);
    }

}
