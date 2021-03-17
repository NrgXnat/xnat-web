package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xdat.om.ArcProject;
import java.io.IOException;

@Slf4j
public class ArcProjectDeserializer extends AbstractBaseElementDeserializer<ArcProject> {
    public ArcProjectDeserializer() {
        super(ArcProject.class);
    }

    @Override
    protected ArcProject deserializeImpl(final JsonParser parser, final DeserializationContext context) throws IOException {
        final ArcProject arcProject = new ArcProject();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "id":
                	arcProject.setId(parser.getText());
                    break;
                case "currentArc":
                	arcProject.setCurrentArc(parser.getText());
                    break;
                case "prearchiveCode":
                	arcProject.setPrearchiveCode(parser.getIntValue());
                    break;
            }
        }
        return arcProject;
    }
}
