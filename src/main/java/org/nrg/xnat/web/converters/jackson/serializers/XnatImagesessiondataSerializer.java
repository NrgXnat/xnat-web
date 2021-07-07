package org.nrg.xnat.web.converters.jackson.serializers;

import java.io.IOException;

import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatSubjectassessordata;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

public class XnatImagesessiondataSerializer extends AbstractBaseElementSerializer<XnatImagesessiondata> {
	private static final long serialVersionUID = 5305227943944237734L;

	public XnatImagesessiondataSerializer() {
        super(XnatImagesessiondata.class);
    }
	
    @Override
    protected void serializeImpl(final XnatImagesessiondata xnatImagesessiondata, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
    	
		writeNonBlankField(generator, "dcmAccessionNumber", xnatImagesessiondata.getDcmaccessionnumber());
		writeNonNullField(generator, "dcmPatientBirthDate", xnatImagesessiondata.getDcmpatientbirthdate());
		writeNonBlankField(generator, "dcmPatientId", xnatImagesessiondata.getDcmpatientid());
		writeNonBlankField(generator, "dcmPatientName", xnatImagesessiondata.getDcmpatientname());
		writeNonNullNumber(generator, "dcmPatientWeight", xnatImagesessiondata.getDcmpatientweight());
		writeNonBlankField(generator, "modality", xnatImagesessiondata.getModality());
		writeNonBlankField(generator, "operator", xnatImagesessiondata.getOperator());
		writeNonBlankField(generator, "prearchivePath", xnatImagesessiondata.getPrearchivepath());
		writeNonBlankField(generator, "scanner", xnatImagesessiondata.getScanner());
		writeNonBlankField(generator, "study_id", xnatImagesessiondata.getStudyId());
		writeNonBlankField(generator, "UID", xnatImagesessiondata.getUid());
		generator.writeObjectField("assessors", xnatImagesessiondata.getAssessors());
		generator.writeObjectField("reconstructions", xnatImagesessiondata.getReconstructions_reconstructedimage());
		generator.writeObjectField("regions", xnatImagesessiondata.getRegions_region());
		generator.writeObjectField("scans", xnatImagesessiondata.getScans_scan());
		provider.findValueSerializer(XnatSubjectassessordata.class).serialize(this, generator, provider);
    }

}
