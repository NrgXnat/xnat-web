package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ValProtocoldata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ValProtocoldataDeserializer<T extends ValProtocoldata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -7303808987647952414L;

    @SuppressWarnings("unchecked")
    public ValProtocoldataDeserializer() {
        this((Class<T>) ValProtocoldata.class);
    }

    public ValProtocoldataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "check_additionalval":
                // TODO: Handle the "check_additionalval" property here: org.nrg.xdat.model.ValAdditionalvalI
                break;
            case "check_comments_comment":
                // TODO: Handle the "check_comments_comment" property here: java.util.List
                break;
            case "check_conditions_condition":
                // TODO: Handle the "check_conditions_condition" property here: java.util.List
                break;
            case "check_status":
                // TODO: Handle the "check_status" property here: String
                break;
            case "header":
                // TODO: Handle the "header" property here: String
                break;
            case "imageassessordata":
                // TODO: Handle the "imageassessordata" property here: org.nrg.xdat.om.XnatImageassessordata
                break;
            case "precedence":
                // TODO: Handle the "precedence" property here: int
                break;
            case "scans_scanCheck":
                // TODO: Handle the "scans_scanCheck" property here: java.util.List
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

