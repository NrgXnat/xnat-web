package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.nrg.xdat.om.XnatExperimentdata;
import org.nrg.xdat.om.XnatImagesessiondata;
import org.nrg.xdat.om.XnatSubjectassessordata;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class XnatSubjectassessordataDeserializer extends XnatExperimentdataDeserializer {
	private static final long serialVersionUID = 8210237025631857679L;

	public XnatSubjectassessordataDeserializer(final Class<? extends XnatExperimentdata> clazz) {
        super(clazz);
    }

    @Override
    protected XnatSubjectassessordata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
    	final XnatSubjectassessordata xnatSubjectassessordata = Optional.ofNullable((XnatSubjectassessordata) context.getAttribute("XnatItem")).orElseThrow(() -> new RuntimeException("xnatSubjectassessordata can't be created on its own"));

    	 final String field = parser.getCurrentName();
    	 if (StringUtils.equalsAny(field,"subjectId", "age")) {
    	 parser.nextToken();  //move to next token in string
            switch (field) {
                case "subjectId":
                	xnatSubjectassessordata.setSubjectId(parser.getText());
                    break;
                case "age":
                	xnatSubjectassessordata.setAge(Double.parseDouble(parser.getText()));
                    break;
            }
            return xnatSubjectassessordata;
    	 }
        return (XnatSubjectassessordata)super.deserializeImpl(parser, context);
    }

}
