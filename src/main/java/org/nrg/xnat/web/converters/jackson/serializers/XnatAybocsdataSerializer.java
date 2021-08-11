package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAybocsdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatAybocsdataSerializer<T extends XnatAybocsdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -2370108742538657968L;

    @SuppressWarnings("unchecked")
    public XnatAybocsdataSerializer() {
        this((Class<T>) XnatAybocsdata.class);
    }

    protected XnatAybocsdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "behaviordrivestrength" property here: Integer
        // TODO: Write out the "behaviorsinterferefunctioning" property here: Integer
        // TODO: Write out the "controloverthoughts" property here: Integer
        // TODO: Write out the "currentorworstever" property here: String
        // TODO: Write out the "distresscaused" property here: Integer
        // TODO: Write out the "efforttoresistbehaviors" property here: Integer
        // TODO: Write out the "efforttoresistthoughts" property here: Integer
        // TODO: Write out the "feelingifprevented" property here: Integer
        // TODO: Write out the "firstuntiljustrightage" property here: Double
        // TODO: Write out the "frequencyuntiljustright" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "subjectassessordata" property here: org.nrg.xdat.om.XnatSubjectassessordata
        // TODO: Write out the "thoughtsinterferefunctioning" property here: Integer
        // TODO: Write out the "timeoccupiedwiththoughts" property here: Integer
        // TODO: Write out the "timeperforming" property here: Integer
        // TODO: Write out the "untiljustright" property here: Boolean
        // TODO: Write out the "untiljustrightawareness" property here: String
        // TODO: Write out the "untiljustrightperceptions" property here: String
        // TODO: Write out the "whenstartuntiljustright" property here: String
    }
}

