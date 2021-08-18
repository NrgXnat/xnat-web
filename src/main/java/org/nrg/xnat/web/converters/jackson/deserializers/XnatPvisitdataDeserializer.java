package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatPvisitdata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatPvisitdataDeserializer<T extends XnatPvisitdata> extends XnatGenericdataDeserializer<T> {
    private static final long serialVersionUID = 8739009186092951359L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatPvisitdataDeserializer() {
        this((Class<T>) XnatPvisitdata.class);
    }

    protected XnatPvisitdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "closed":
                instance.setClosed(parser.getBooleanValue());
                break;
            case "endDate":
                // TODO: Handle the "endDate" property here: Object
                break;
            case "notes":
                instance.setNotes(parser.getText());
                break;
            case "protocolid":
                instance.setProtocolid(parser.getText());
                break;
            case "protocolversion":
                instance.setProtocolversion(parser.getIntValue());
                break;
            case "startDate":
                // TODO: Handle the "startDate" property here: Object
                break;
            case "status":
                instance.setStatus(parser.getText());
                break;
            case "subjectId":
                instance.setSubjectId(parser.getText());
                break;
            case "terminal":
                instance.setTerminal(parser.getBooleanValue());
                break;
            case "visitName":
                instance.setVisitName(parser.getText());
                break;
            case "visitType":
                instance.setVisitType(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

