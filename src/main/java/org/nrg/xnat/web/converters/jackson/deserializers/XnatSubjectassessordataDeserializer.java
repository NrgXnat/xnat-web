package org.nrg.xnat.web.converters.jackson.deserializers;

import java.io.IOException;
import java.util.Optional;

import org.nrg.xdat.om.XnatSubjectassessordata;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;

public class XnatSubjectassessordataDeserializer extends AbstractBaseElementDeserializer<XnatSubjectassessordata> {
	private static final long serialVersionUID = 8210237025631857679L;

	public XnatSubjectassessordataDeserializer() {
        super(XnatSubjectassessordata.class);
    }

    @Override
    protected XnatSubjectassessordata deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        //final XnatSubjectassessordata xnatSubjectassessordata = getInstance(context);
    	final XnatSubjectassessordata xnatSubjectassessordata = Optional.ofNullable((XnatSubjectassessordata) context.getAttribute("XnatItem")).orElseThrow(() -> new RuntimeException("xnatSubjectassessordata can't be created on its own"));

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "subjectId":
                	xnatSubjectassessordata.setSubjectId(parser.getText());
                    break;
                case "age":
                	xnatSubjectassessordata.setAge(Double.parseDouble(parser.getText()));
                    break;
                case "xsiType":
                	xnatSubjectassessordata.getItem().setXmlType("xnat:mrSessionData");
                    break;
            }
        }
        return xnatSubjectassessordata;
    }

	@Override
	protected XnatSubjectassessordata getNewInstance() throws JsonProcessingException {
		return null;
	}

}
