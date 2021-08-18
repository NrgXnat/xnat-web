package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAupdrs3data;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatAupdrs3dataSerializer<T extends XnatAupdrs3data> extends XnatSubjectassessordataSerializer<T> {
    private static final long serialVersionUID = 1707032806823320928L;

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
        writeNonNullNumber(generator, "actionposturaltremor_left", instance.getActionposturaltremor_left());
        writeNonNullNumber(generator, "actionposturaltremor_right", instance.getActionposturaltremor_right());
        writeNonNullNumber(generator, "arisefromchair", instance.getArisefromchair());
        writeNonNullNumber(generator, "bodybradykinesiahypokinesia", instance.getBodybradykinesiahypokinesia());
        writeNonNullNumber(generator, "clicker_interval", instance.getClicker_interval());
        writeNonNullNumber(generator, "clicker_left", instance.getClicker_left());
        writeNonNullNumber(generator, "clicker_right", instance.getClicker_right());
        writeNonNullNumber(generator, "facialexpression", instance.getFacialexpression());
        writeNonNullNumber(generator, "fingertaps_left", instance.getFingertaps_left());
        writeNonNullNumber(generator, "fingertaps_right", instance.getFingertaps_right());
        writeNonNullNumber(generator, "foottaps_left", instance.getFoottaps_left());
        writeNonNullNumber(generator, "foottaps_right", instance.getFoottaps_right());
        writeNonNullNumber(generator, "gait", instance.getGait());
        writeNonNullNumber(generator, "handmovementsgrip_left", instance.getHandmovementsgrip_left());
        writeNonNullNumber(generator, "handmovementsgrip_right", instance.getHandmovementsgrip_right());
        writeNonNullNumber(generator, "handsram_left", instance.getHandsram_left());
        writeNonNullNumber(generator, "handsram_right", instance.getHandsram_right());
        writeNonNullBoolean(generator, "inscanner", instance.getInscanner());
        writeNonNullNumber(generator, "posturalstability", instance.getPosturalstability());
        writeNonNullNumber(generator, "posture", instance.getPosture());
        writeNonNullBoolean(generator, "problem", instance.getProblem());
        writeNonBlankField(generator, "rigidity_lle", instance.getRigidity_lle());
        writeNonBlankField(generator, "rigidity_lue", instance.getRigidity_lue());
        writeNonBlankField(generator, "rigidity_neck", instance.getRigidity_neck());
        writeNonBlankField(generator, "rigidity_rle", instance.getRigidity_rle());
        writeNonBlankField(generator, "rigidity_rue", instance.getRigidity_rue());
        writeNonNullNumber(generator, "speech", instance.getSpeech());
        writeNonBlankField(generator, "tremorrest_face", instance.getTremorrest_face());
        writeNonBlankField(generator, "tremorrest_lle", instance.getTremorrest_lle());
        writeNonBlankField(generator, "tremorrest_lue", instance.getTremorrest_lue());
        writeNonBlankField(generator, "tremorrest_rle", instance.getTremorrest_rle());
        writeNonBlankField(generator, "tremorrest_rue", instance.getTremorrest_rue());
        writeNonNullNumber(generator, "writing", instance.getWriting());
        super.serializeImpl(instance, generator, provider);
    }
}

