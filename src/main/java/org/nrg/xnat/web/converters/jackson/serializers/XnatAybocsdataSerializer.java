package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAybocsdata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatAybocsdataSerializer<T extends XnatAybocsdata> extends XnatSubjectassessordataSerializer<T> {
    private static final long serialVersionUID = -5549086340670857802L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatAybocsdataSerializer() {
        this((Class<T>) XnatAybocsdata.class);
    }

    protected XnatAybocsdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "behaviordrivestrength", instance.getBehaviordrivestrength());
        writeNonNullNumber(generator, "behaviorsinterferefunctioning", instance.getBehaviorsinterferefunctioning());
        writeNonNullNumber(generator, "controloverthoughts", instance.getControloverthoughts());
        writeNonBlankField(generator, "currentorworstever", instance.getCurrentorworstever());
        writeNonNullNumber(generator, "distresscaused", instance.getDistresscaused());
        writeNonNullNumber(generator, "efforttoresistbehaviors", instance.getEfforttoresistbehaviors());
        writeNonNullNumber(generator, "efforttoresistthoughts", instance.getEfforttoresistthoughts());
        writeNonNullNumber(generator, "feelingifprevented", instance.getFeelingifprevented());
        writeNonNullNumber(generator, "firstuntiljustrightage", instance.getFirstuntiljustrightage());
        writeNonBlankField(generator, "frequencyuntiljustright", instance.getFrequencyuntiljustright());
        writeNonNullNumber(generator, "thoughtsinterferefunctioning", instance.getThoughtsinterferefunctioning());
        writeNonNullNumber(generator, "timeoccupiedwiththoughts", instance.getTimeoccupiedwiththoughts());
        writeNonNullNumber(generator, "timeperforming", instance.getTimeperforming());
        writeNonNullBoolean(generator, "untiljustright", instance.getUntiljustright());
        writeNonBlankField(generator, "untiljustrightawareness", instance.getUntiljustrightawareness());
        writeNonBlankField(generator, "untiljustrightperceptions", instance.getUntiljustrightperceptions());
        writeNonBlankField(generator, "whenstartuntiljustright", instance.getWhenstartuntiljustright());
        super.serializeImpl(instance, generator, provider);
    }
}

