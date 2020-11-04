package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatExperimentdata;

import java.io.IOException;

@Slf4j
public class XnatExperimentdataDeserializer extends AbstractBaseElementDeserializer<XnatExperimentdata> {
    public XnatExperimentdataDeserializer() {
        super(XnatExperimentdata.class);
    }

    @Override
    protected XnatExperimentdata deserializeImpl(JsonParser parser, DeserializationContext context) throws IOException {
        final XnatExperimentdata experiment = new XnatExperimentdata();

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            parser.nextToken();  //move to next token in string
            switch (field) {
                case "id":
                    experiment.setId(parser.getText());
                    break;
                case "label":
                    experiment.setLabel(parser.getText());
                    break;
                case "project":
                    experiment.setProject(parser.getText());
                    break;
                case "note":
                    experiment.setNote(parser.getText());
                    break;
                case "protocol":
                    experiment.setProtocol(parser.getText());
                    break;
                case "original":
                    experiment.setOriginal(parser.getText());
                    break;
                case "date":
                    experiment.setDate(parseDate(parser.getText()));
                    break;
                case "delay":
                    experiment.setDelay(parser.getIntValue());
                    break;
                case "version":
                    experiment.setVersion(parser.getIntValue());
                    break;
            }
        }
        return experiment;
    }
}
