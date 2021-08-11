package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAupdrs3data;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatAupdrs3dataDeserializer<T extends XnatAupdrs3data> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -5392234993929839350L;

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
                // TODO: Handle the "actionposturaltremor_left" property here: Integer
                break;
            case "actionposturaltremor_right":
                // TODO: Handle the "actionposturaltremor_right" property here: Integer
                break;
            case "arisefromchair":
                // TODO: Handle the "arisefromchair" property here: Integer
                break;
            case "bodybradykinesiahypokinesia":
                // TODO: Handle the "bodybradykinesiahypokinesia" property here: Integer
                break;
            case "clicker_interval":
                // TODO: Handle the "clicker_interval" property here: Integer
                break;
            case "clicker_left":
                // TODO: Handle the "clicker_left" property here: Integer
                break;
            case "clicker_right":
                // TODO: Handle the "clicker_right" property here: Integer
                break;
            case "facialexpression":
                // TODO: Handle the "facialexpression" property here: Integer
                break;
            case "fingertaps_left":
                // TODO: Handle the "fingertaps_left" property here: Integer
                break;
            case "fingertaps_right":
                // TODO: Handle the "fingertaps_right" property here: Integer
                break;
            case "foottaps_left":
                // TODO: Handle the "foottaps_left" property here: Integer
                break;
            case "foottaps_right":
                // TODO: Handle the "foottaps_right" property here: Integer
                break;
            case "gait":
                // TODO: Handle the "gait" property here: Integer
                break;
            case "handmovementsgrip_left":
                // TODO: Handle the "handmovementsgrip_left" property here: Integer
                break;
            case "handmovementsgrip_right":
                // TODO: Handle the "handmovementsgrip_right" property here: Integer
                break;
            case "handsram_left":
                // TODO: Handle the "handsram_left" property here: Integer
                break;
            case "handsram_right":
                // TODO: Handle the "handsram_right" property here: Integer
                break;
            case "inscanner":
                // TODO: Handle the "inscanner" property here: Boolean
                break;
            case "posturalstability":
                // TODO: Handle the "posturalstability" property here: Integer
                break;
            case "posture":
                // TODO: Handle the "posture" property here: Integer
                break;
            case "problem":
                // TODO: Handle the "problem" property here: Boolean
                break;
            case "rigidity_lle":
                // TODO: Handle the "rigidity_lle" property here: String
                break;
            case "rigidity_lue":
                // TODO: Handle the "rigidity_lue" property here: String
                break;
            case "rigidity_neck":
                // TODO: Handle the "rigidity_neck" property here: String
                break;
            case "rigidity_rle":
                // TODO: Handle the "rigidity_rle" property here: String
                break;
            case "rigidity_rue":
                // TODO: Handle the "rigidity_rue" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "speech":
                // TODO: Handle the "speech" property here: Integer
                break;
            case "subjectassessordata":
                // TODO: Handle the "subjectassessordata" property here: org.nrg.xdat.om.XnatSubjectassessordata
                break;
            case "tremorrest_face":
                // TODO: Handle the "tremorrest_face" property here: String
                break;
            case "tremorrest_lle":
                // TODO: Handle the "tremorrest_lle" property here: String
                break;
            case "tremorrest_lue":
                // TODO: Handle the "tremorrest_lue" property here: String
                break;
            case "tremorrest_rle":
                // TODO: Handle the "tremorrest_rle" property here: String
                break;
            case "tremorrest_rue":
                // TODO: Handle the "tremorrest_rue" property here: String
                break;
            case "writing":
                // TODO: Handle the "writing" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

