package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAybocsdata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatAybocsdataDeserializer<T extends XnatAybocsdata> extends XnatSubjectassessordataDeserializer<T> {
    private static final long serialVersionUID = -8675613870496517340L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatAybocsdataDeserializer() {
        this((Class<T>) XnatAybocsdata.class);
    }

    protected XnatAybocsdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "behaviordrivestrength":
                instance.setBehaviordrivestrength(parser.getIntValue());
                break;
            case "behaviorsinterferefunctioning":
                instance.setBehaviorsinterferefunctioning(parser.getIntValue());
                break;
            case "controloverthoughts":
                instance.setControloverthoughts(parser.getIntValue());
                break;
            case "currentorworstever":
                instance.setCurrentorworstever(parser.getText());
                break;
            case "distresscaused":
                instance.setDistresscaused(parser.getIntValue());
                break;
            case "efforttoresistbehaviors":
                instance.setEfforttoresistbehaviors(parser.getIntValue());
                break;
            case "efforttoresistthoughts":
                instance.setEfforttoresistthoughts(parser.getIntValue());
                break;
            case "feelingifprevented":
                instance.setFeelingifprevented(parser.getIntValue());
                break;
            case "firstuntiljustrightage":
                instance.setFirstuntiljustrightage(parser.getDoubleValue());
                break;
            case "frequencyuntiljustright":
                instance.setFrequencyuntiljustright(parser.getText());
                break;
            case "thoughtsinterferefunctioning":
                instance.setThoughtsinterferefunctioning(parser.getIntValue());
                break;
            case "timeoccupiedwiththoughts":
                instance.setTimeoccupiedwiththoughts(parser.getIntValue());
                break;
            case "timeperforming":
                instance.setTimeperforming(parser.getIntValue());
                break;
            case "untiljustright":
                instance.setUntiljustright(parser.getBooleanValue());
                break;
            case "untiljustrightawareness":
                instance.setUntiljustrightawareness(parser.getText());
                break;
            case "untiljustrightperceptions":
                instance.setUntiljustrightperceptions(parser.getText());
                break;
            case "whenstartuntiljustright":
                instance.setWhenstartuntiljustright(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

