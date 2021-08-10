package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAsideeffectspittsburghdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatAsideeffectspittsburghdataDeserializer<T extends XnatAsideeffectspittsburghdata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 1960062034355761944L;

    @SuppressWarnings("unchecked")
    public XnatAsideeffectspittsburghdataDeserializer() {
        this((Class<T>) XnatAsideeffectspittsburghdata.class);
    }

    public XnatAsideeffectspittsburghdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "buccalLingualmovements":
                // TODO: Handle the "buccalLingualmovements" property here: Integer
                break;
            case "crabbyIrritable":
                // TODO: Handle the "crabbyIrritable" property here: Integer
                break;
            case "dizzinessLightheadedness":
                // TODO: Handle the "dizzinessLightheadedness" property here: Integer
                break;
            case "drymouth":
                // TODO: Handle the "drymouth" property here: Integer
                break;
            case "dullTiredListless":
                // TODO: Handle the "dullTiredListless" property here: Integer
                break;
            case "hallucinations":
                // TODO: Handle the "hallucinations" property here: Integer
                break;
            case "headaches":
                // TODO: Handle the "headaches" property here: Integer
                break;
            case "lossofappetite":
                // TODO: Handle the "lossofappetite" property here: Integer
                break;
            case "motortics":
                // TODO: Handle the "motortics" property here: Integer
                break;
            case "nauseaVomiting":
                // TODO: Handle the "nauseaVomiting" property here: Integer
                break;
            case "palpitations":
                // TODO: Handle the "palpitations" property here: Integer
                break;
            case "pickingSkinFingersNailsLip":
                // TODO: Handle the "pickingSkinFingersNailsLip" property here: Integer
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "sedation":
                // TODO: Handle the "sedation" property here: Integer
                break;
            case "socialwithdrawal":
                // TODO: Handle the "socialwithdrawal" property here: Integer
                break;
            case "stomachache":
                // TODO: Handle the "stomachache" property here: Integer
                break;
            case "subjectassessordata":
                // TODO: Handle the "subjectassessordata" property here: org.nrg.xdat.om.XnatSubjectassessordata
                break;
            case "tearfulSadDepressed":
                // TODO: Handle the "tearfulSadDepressed" property here: Integer
                break;
            case "troubleconcentratingDistractible":
                // TODO: Handle the "troubleconcentratingDistractible" property here: Integer
                break;
            case "troublesleeping":
                // TODO: Handle the "troublesleeping" property here: Integer
                break;
            case "worriedAnxious":
                // TODO: Handle the "worriedAnxious" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

