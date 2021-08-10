package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPvisitdata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatPvisitdataDeserializer<T extends XnatPvisitdata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -3097100098784711280L;

    @SuppressWarnings("unchecked")
    public XnatPvisitdataDeserializer() {
        this((Class<T>) XnatPvisitdata.class);
    }

    public XnatPvisitdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "closed":
                // TODO: Handle the "closed" property here: Boolean
                break;
            case "endDate":
                // TODO: Handle the "endDate" property here: Object
                break;
            case "genericdata":
                // TODO: Handle the "genericdata" property here: org.nrg.xdat.om.XnatGenericdata
                break;
            case "notes":
                // TODO: Handle the "notes" property here: String
                break;
            case "protocolid":
                // TODO: Handle the "protocolid" property here: String
                break;
            case "protocolversion":
                // TODO: Handle the "protocolversion" property here: Integer
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "startDate":
                // TODO: Handle the "startDate" property here: Object
                break;
            case "status":
                // TODO: Handle the "status" property here: String
                break;
            case "subjectData":
                // TODO: Handle the "subjectData" property here: org.nrg.xdat.om.XnatSubjectdata
                break;
            case "subjectId":
                // TODO: Handle the "subjectId" property here: String
                break;
            case "terminal":
                // TODO: Handle the "terminal" property here: Boolean
                break;
            case "visitName":
                // TODO: Handle the "visitName" property here: String
                break;
            case "visitType":
                // TODO: Handle the "visitType" property here: String
                break;
            case "visits":
                // TODO: Handle the "visits" property here: java.util.ArrayList
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

