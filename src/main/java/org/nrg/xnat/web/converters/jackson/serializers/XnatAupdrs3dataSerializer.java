package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAupdrs3data;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatAupdrs3dataSerializer<T extends XnatAupdrs3data> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -5595743721084830420L;

    @SuppressWarnings("unchecked")
    public XnatAupdrs3dataSerializer() {
        this((Class<T>) XnatAupdrs3data.class);
    }

    protected XnatAupdrs3dataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "actionposturaltremor_left" property here: Integer
        // TODO: Write out the "actionposturaltremor_right" property here: Integer
        // TODO: Write out the "arisefromchair" property here: Integer
        // TODO: Write out the "bodybradykinesiahypokinesia" property here: Integer
        // TODO: Write out the "clicker_interval" property here: Integer
        // TODO: Write out the "clicker_left" property here: Integer
        // TODO: Write out the "clicker_right" property here: Integer
        // TODO: Write out the "facialexpression" property here: Integer
        // TODO: Write out the "fingertaps_left" property here: Integer
        // TODO: Write out the "fingertaps_right" property here: Integer
        // TODO: Write out the "foottaps_left" property here: Integer
        // TODO: Write out the "foottaps_right" property here: Integer
        // TODO: Write out the "gait" property here: Integer
        // TODO: Write out the "handmovementsgrip_left" property here: Integer
        // TODO: Write out the "handmovementsgrip_right" property here: Integer
        // TODO: Write out the "handsram_left" property here: Integer
        // TODO: Write out the "handsram_right" property here: Integer
        // TODO: Write out the "inscanner" property here: Boolean
        // TODO: Write out the "posturalstability" property here: Integer
        // TODO: Write out the "posture" property here: Integer
        // TODO: Write out the "problem" property here: Boolean
        // TODO: Write out the "rigidity_lle" property here: String
        // TODO: Write out the "rigidity_lue" property here: String
        // TODO: Write out the "rigidity_neck" property here: String
        // TODO: Write out the "rigidity_rle" property here: String
        // TODO: Write out the "rigidity_rue" property here: String
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "speech" property here: Integer
        // TODO: Write out the "subjectassessordata" property here: org.nrg.xdat.om.XnatSubjectassessordata
        // TODO: Write out the "tremorrest_face" property here: String
        // TODO: Write out the "tremorrest_lle" property here: String
        // TODO: Write out the "tremorrest_lue" property here: String
        // TODO: Write out the "tremorrest_rle" property here: String
        // TODO: Write out the "tremorrest_rue" property here: String
        // TODO: Write out the "writing" property here: Integer
    }
}

