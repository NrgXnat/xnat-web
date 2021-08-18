package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAsideeffectspittsburghdata;

import java.io.IOException;

@XnatSerializer
@Slf4j
public class XnatAsideeffectspittsburghdataSerializer<T extends XnatAsideeffectspittsburghdata> extends XnatSubjectassessordataSerializer<T> {
    private static final long serialVersionUID = -7102134939545744174L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatAsideeffectspittsburghdataSerializer() {
        this((Class<T>) XnatAsideeffectspittsburghdata.class);
    }

    protected XnatAsideeffectspittsburghdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        writeNonNullNumber(generator, "buccalLingualmovements", instance.getBuccalLingualmovements());
        writeNonNullNumber(generator, "crabbyIrritable", instance.getCrabbyIrritable());
        writeNonNullNumber(generator, "dizzinessLightheadedness", instance.getDizzinessLightheadedness());
        writeNonNullNumber(generator, "drymouth", instance.getDrymouth());
        writeNonNullNumber(generator, "dullTiredListless", instance.getDullTiredListless());
        writeNonNullNumber(generator, "hallucinations", instance.getHallucinations());
        writeNonNullNumber(generator, "headaches", instance.getHeadaches());
        writeNonNullNumber(generator, "lossofappetite", instance.getLossofappetite());
        writeNonNullNumber(generator, "motortics", instance.getMotortics());
        writeNonNullNumber(generator, "nauseaVomiting", instance.getNauseaVomiting());
        writeNonNullNumber(generator, "palpitations", instance.getPalpitations());
        writeNonNullNumber(generator, "pickingSkinFingersNailsLip", instance.getPickingSkinFingersNailsLip());
        writeNonNullNumber(generator, "sedation", instance.getSedation());
        writeNonNullNumber(generator, "socialwithdrawal", instance.getSocialwithdrawal());
        writeNonNullNumber(generator, "stomachache", instance.getStomachache());
        writeNonNullNumber(generator, "tearfulSadDepressed", instance.getTearfulSadDepressed());
        writeNonNullNumber(generator, "troubleconcentratingDistractible", instance.getTroubleconcentratingDistractible());
        writeNonNullNumber(generator, "troublesleeping", instance.getTroublesleeping());
        writeNonNullNumber(generator, "worriedAnxious", instance.getWorriedAnxious());
        super.serializeImpl(instance, generator, provider);
    }
}

