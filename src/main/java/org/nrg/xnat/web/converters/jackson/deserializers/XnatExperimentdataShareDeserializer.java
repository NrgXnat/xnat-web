package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatExperimentdataShare;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatExperimentdataShareDeserializer<T extends XnatExperimentdataShare> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 8714542973804658983L;

    @SuppressWarnings("unchecked")
    public XnatExperimentdataShareDeserializer() {
        super((Class<T>) XnatExperimentdataShare.class);
    }

    protected XnatExperimentdataShareDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        switch (field) {
            case "label":
                instance.setLabel(parser.getText());
                break;
            case "visit":
                instance.setVisit(parser.getText());
                break;
            case "project":
                instance.setProject(parser.getText());
                break;
            case "protocol":
                instance.setProtocol(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
