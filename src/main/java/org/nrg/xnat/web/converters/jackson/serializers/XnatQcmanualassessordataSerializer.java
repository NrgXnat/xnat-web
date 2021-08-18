package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatQcmanualassessordata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatQcmanualassessordataSerializer<T extends XnatQcmanualassessordata> extends XnatImageassessordataSerializer<T> {
    private static final long serialVersionUID = 6659736131479041313L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatQcmanualassessordataSerializer() {
        this((Class<T>) XnatQcmanualassessordata.class);
    }

    protected XnatQcmanualassessordataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonBlankField(generator, "comments", instance.getComments());
        writeNonBlankField(generator, "incidentalfindings", instance.getIncidentalfindings());
        writeNonBlankField(generator, "pass", instance.getPass());
        writeNonBlankField(generator, "payable", instance.getPayable());
        writeNonBlankField(generator, "protocol", instance.getProtocol());
        writeNonBlankField(generator, "protocolcomments", instance.getProtocolcomments());
        writeNonBlankField(generator, "rater", instance.getRater());
        writeNonBlankField(generator, "rescan", instance.getRescan());
        writeNonBlankField(generator, "resolvable", instance.getResolvable());
        writeNonBlankField(generator, "retrain", instance.getRetrain());
        // TODO: Write out the "scans_scan" property here: java.util.List
        writeNonBlankField(generator, "stereotacticmarker", instance.getStereotacticmarker());
        super.serializeImpl(instance, generator, provider);
    }
}

