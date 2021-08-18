package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAupdrs3data;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatAupdrs3dataDeserializer<T extends XnatAupdrs3data> extends XnatSubjectassessordataDeserializer<T> {
    private static final long serialVersionUID = 1206404689880735076L;

    @SuppressWarnings("unchecked")
    public XnatAupdrs3dataDeserializer() {
        this((Class<T>) XnatAupdrs3data.class);
    }

    public XnatAupdrs3dataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "actionposturaltremor_left":
                instance.setActionposturaltremor_left(parser.getIntValue());
                break;
            case "actionposturaltremor_right":
                instance.setActionposturaltremor_right(parser.getIntValue());
                break;
            case "arisefromchair":
                instance.setArisefromchair(parser.getIntValue());
                break;
            case "bodybradykinesiahypokinesia":
                instance.setBodybradykinesiahypokinesia(parser.getIntValue());
                break;
            case "clicker_interval":
                instance.setClicker_interval(parser.getIntValue());
                break;
            case "clicker_left":
                instance.setClicker_left(parser.getIntValue());
                break;
            case "clicker_right":
                instance.setClicker_right(parser.getIntValue());
                break;
            case "facialexpression":
                instance.setFacialexpression(parser.getIntValue());
                break;
            case "fingertaps_left":
                instance.setFingertaps_left(parser.getIntValue());
                break;
            case "fingertaps_right":
                instance.setFingertaps_right(parser.getIntValue());
                break;
            case "foottaps_left":
                instance.setFoottaps_left(parser.getIntValue());
                break;
            case "foottaps_right":
                instance.setFoottaps_right(parser.getIntValue());
                break;
            case "gait":
                instance.setGait(parser.getIntValue());
                break;
            case "handmovementsgrip_left":
                instance.setHandmovementsgrip_left(parser.getIntValue());
                break;
            case "handmovementsgrip_right":
                instance.setHandmovementsgrip_right(parser.getIntValue());
                break;
            case "handsram_left":
                instance.setHandsram_left(parser.getIntValue());
                break;
            case "handsram_right":
                instance.setHandsram_right(parser.getIntValue());
                break;
            case "inscanner":
                instance.setInscanner(parser.getBooleanValue());
                break;
            case "posturalstability":
                instance.setPosturalstability(parser.getIntValue());
                break;
            case "posture":
                instance.setPosture(parser.getIntValue());
                break;
            case "problem":
                instance.setProblem(parser.getBooleanValue());
                break;
            case "rigidity_lle":
                instance.setRigidity_lle(parser.getText());
                break;
            case "rigidity_lue":
                instance.setRigidity_lue(parser.getText());
                break;
            case "rigidity_neck":
                instance.setRigidity_neck(parser.getText());
                break;
            case "rigidity_rle":
                instance.setRigidity_rle(parser.getText());
                break;
            case "rigidity_rue":
                instance.setRigidity_rue(parser.getText());
                break;
            case "speech":
                instance.setSpeech(parser.getIntValue());
                break;
            case "tremorrest_face":
                instance.setTremorrest_face(parser.getText());
                break;
            case "tremorrest_lle":
                instance.setTremorrest_lle(parser.getText());
                break;
            case "tremorrest_lue":
                instance.setTremorrest_lue(parser.getText());
                break;
            case "tremorrest_rle":
                instance.setTremorrest_rle(parser.getText());
                break;
            case "tremorrest_rue":
                instance.setTremorrest_rue(parser.getText());
                break;
            case "writing":
                instance.setWriting(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

