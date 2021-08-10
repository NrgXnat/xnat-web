package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatQcassessmentdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatQcassessmentdataDeserializer<T extends XnatQcassessmentdata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -760581860503397795L;

    @SuppressWarnings("unchecked")
    public XnatQcassessmentdataDeserializer() {
        this((Class<T>) XnatQcassessmentdata.class);
    }

    public XnatQcassessmentdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "header":
                // TODO: Handle the "header" property here: String
                break;
            case "mrassessordata":
                // TODO: Handle the "mrassessordata" property here: org.nrg.xdat.om.XnatMrassessordata
                break;
            case "precedence":
                // TODO: Handle the "precedence" property here: int
                break;
            case "scans_scan":
                // TODO: Handle the "scans_scan" property here: java.util.List
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "type":
                // TODO: Handle the "type" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

