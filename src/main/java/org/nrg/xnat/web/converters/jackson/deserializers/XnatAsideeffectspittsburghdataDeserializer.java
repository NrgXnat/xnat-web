package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAsideeffectspittsburghdata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatAsideeffectspittsburghdataDeserializer<T extends XnatAsideeffectspittsburghdata> extends XnatSubjectassessordataDeserializer<T> {
    private static final long serialVersionUID = 7718805059312657300L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatAsideeffectspittsburghdataDeserializer() {
        this((Class<T>) XnatAsideeffectspittsburghdata.class);
    }

    protected XnatAsideeffectspittsburghdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "buccalLingualmovements":
                instance.setBuccalLingualmovements(parser.getIntValue());
                break;
            case "crabbyIrritable":
                instance.setCrabbyIrritable(parser.getIntValue());
                break;
            case "dizzinessLightheadedness":
                instance.setDizzinessLightheadedness(parser.getIntValue());
                break;
            case "drymouth":
                instance.setDrymouth(parser.getIntValue());
                break;
            case "dullTiredListless":
                instance.setDullTiredListless(parser.getIntValue());
                break;
            case "hallucinations":
                instance.setHallucinations(parser.getIntValue());
                break;
            case "headaches":
                instance.setHeadaches(parser.getIntValue());
                break;
            case "lossofappetite":
                instance.setLossofappetite(parser.getIntValue());
                break;
            case "motortics":
                instance.setMotortics(parser.getIntValue());
                break;
            case "nauseaVomiting":
                instance.setNauseaVomiting(parser.getIntValue());
                break;
            case "palpitations":
                instance.setPalpitations(parser.getIntValue());
                break;
            case "pickingSkinFingersNailsLip":
                instance.setPickingSkinFingersNailsLip(parser.getIntValue());
                break;
            case "sedation":
                instance.setSedation(parser.getIntValue());
                break;
            case "socialwithdrawal":
                instance.setSocialwithdrawal(parser.getIntValue());
                break;
            case "stomachache":
                instance.setStomachache(parser.getIntValue());
                break;
            case "tearfulSadDepressed":
                instance.setTearfulSadDepressed(parser.getIntValue());
                break;
            case "troubleconcentratingDistractible":
                instance.setTroubleconcentratingDistractible(parser.getIntValue());
                break;
            case "troublesleeping":
                instance.setTroublesleeping(parser.getIntValue());
                break;
            case "worriedAnxious":
                instance.setWorriedAnxious(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

