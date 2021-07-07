package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatDemographicdata;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xft.ItemI;

import java.io.IOException;

@Slf4j
public class XnatSubjectdataDeserializer extends AbstractBaseElementDeserializer<XnatSubjectdata> {
    public XnatSubjectdataDeserializer() {
        super(XnatSubjectdata.class);
    }

    @Override
    protected XnatSubjectdata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final XnatSubjectdata     subject      = new XnatSubjectdata();
        final XnatDemographicdata demographics = new XnatDemographicdata();
        try {
            subject.setDemographics((ItemI) demographics);
        } catch (Exception e) {
            log.error("An error occurred trying to set demographics data while deserializing an object. Sorry about that.", e);
        }
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "id":
                    subject.setId(parser.getText());
                    break;
                case "label":
                    subject.setLabel(parser.getText());
                    break;
                case "project":
                    subject.setProject(parser.getText());
                    break;
                case "group":
                    subject.setGroup(parser.getText());
                    break;
                case "dob":
                    demographics.setDob(parseDate(parser.getText()));
                    break;
                case "educationDesc":
                    demographics.setEducationdesc(parser.getText());
                    break;
                case "education":
                    demographics.setEducation(parser.getIntValue());
                    break;
                case "age":
                    demographics.setAge(parser.getIntValue());
                    break;
                case "ses":
                    demographics.setSes(parser.getIntValue());
                    break;
                case "gender":
                    demographics.setGender(parser.getText());
                    break;
                case "handedness":
                    demographics.setHandedness(parser.getText());
                    break;
                case "ethnicity":
                    demographics.setEthnicity(parser.getText());
                    break;
                case "race":
                    demographics.setRace(parser.getText());
                    break;
                case "initials":
                    subject.setInitials(parser.getText());
                    break;
            }
        }
        return subject;
    }

	@Override
	protected XnatSubjectdata getNewInstance() throws JsonProcessingException {
		// TODO Auto-generated method stub
		return null;
	}
}
