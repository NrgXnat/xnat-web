package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatExperimentdataShare;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatExperimentdataShareDeserializer<T extends XnatExperimentdataShare> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -7447976197589741928L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatExperimentdataShareDeserializer() {
        this((Class<T>) XnatExperimentdataShare.class);
    }

    protected XnatExperimentdataShareDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "label":
                instance.setLabel(parser.getText());
                break;
            case "project":
                instance.setProject(parser.getText());
                break;
            case "protocol":
                instance.setProtocol(parser.getText());
                break;
            case "share":
                instance.setShare(parser.getText());
                break;
            case "visit":
                instance.setVisit(parser.getText());
                break;
            case "xnatExperimentdataShareId":
                instance.setXnatExperimentdataShareId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

