package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xdat.om.XnatDemographicdata;
import org.nrg.xdat.om.XnatSubjectdata;

import java.io.IOException;

@Slf4j
public class XnatDemographicdataSerializer extends AbstractBaseElementSerializer<XnatDemographicdata> {
    public XnatDemographicdataSerializer() {
        super(XnatDemographicdata.class);
    }

    @Override
    protected void serializeImpl(final XnatDemographicdata demographic, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
    	writeNonNullNumber(generator, "xnatAbstractDemographicDataId", demographic.getXnatAbstractdemographicdataId());
        writeNonBlankField(generator, "ethnicity", demographic.getEthnicity());
        writeNonBlankField(generator, "race", demographic.getRace());
        writeNonBlankField(generator, "gender", demographic.getGender());
        writeNonBlankField(generator, "handedness", demographic.getHandedness());
        writeNonNullField(generator,  "dob", demographic.getDob());
        writeNonNullNumber(generator, "weight", demographic.getWeight());
        writeNonNullNumber(generator, "age", demographic.getAge());
        writeNonNullNumber(generator, "height", demographic.getHeight());
        writeNonNullNumber(generator, "ses", demographic.getSes());
        writeNonBlankField(generator, "gender", demographic.getGender());
        writeNonBlankField(generator, "educationDesc", demographic.getEducationdesc());
        writeNonNullNumber(generator, "education", demographic.getEducation());
        
    }
}
