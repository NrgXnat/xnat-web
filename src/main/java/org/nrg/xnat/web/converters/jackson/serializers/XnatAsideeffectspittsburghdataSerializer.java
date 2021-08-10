package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatAsideeffectspittsburghdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatAsideeffectspittsburghdataSerializer<T extends XnatAsideeffectspittsburghdata> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = -1939345542023291613L;

    @SuppressWarnings("unchecked")
    public XnatAsideeffectspittsburghdataSerializer() {
        this((Class<T>) XnatAsideeffectspittsburghdata.class);
    }

    protected XnatAsideeffectspittsburghdataSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "buccalLingualmovements" property here: Integer
        // TODO: Write out the "crabbyIrritable" property here: Integer
        // TODO: Write out the "dizzinessLightheadedness" property here: Integer
        // TODO: Write out the "drymouth" property here: Integer
        // TODO: Write out the "dullTiredListless" property here: Integer
        // TODO: Write out the "hallucinations" property here: Integer
        // TODO: Write out the "headaches" property here: Integer
        // TODO: Write out the "lossofappetite" property here: Integer
        // TODO: Write out the "motortics" property here: Integer
        // TODO: Write out the "nauseaVomiting" property here: Integer
        // TODO: Write out the "palpitations" property here: Integer
        // TODO: Write out the "pickingSkinFingersNailsLip" property here: Integer
        // TODO: Write out the "schemaElementName" property here: String
        // TODO: Write out the "sedation" property here: Integer
        // TODO: Write out the "socialwithdrawal" property here: Integer
        // TODO: Write out the "stomachache" property here: Integer
        // TODO: Write out the "subjectassessordata" property here: org.nrg.xdat.om.XnatSubjectassessordata
        // TODO: Write out the "tearfulSadDepressed" property here: Integer
        // TODO: Write out the "troubleconcentratingDistractible" property here: Integer
        // TODO: Write out the "troublesleeping" property here: Integer
        // TODO: Write out the "worriedAnxious" property here: Integer
    }
}

