package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatQcmanualassessordata;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatQcmanualassessordataDeserializer<T extends XnatQcmanualassessordata> extends XnatImageassessordataDeserializer<T> {
    private static final long serialVersionUID = 3560004371107087516L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatQcmanualassessordataDeserializer() {
        this((Class<T>) XnatQcmanualassessordata.class);
    }

    protected XnatQcmanualassessordataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "comments":
                instance.setComments(parser.getText());
                break;
            case "incidentalfindings":
                instance.setIncidentalfindings(parser.getText());
                break;
            case "pass":
                instance.setPass(parser.getText());
                break;
            case "payable":
                instance.setPayable(parser.getText());
                break;
            case "protocol":
                instance.setProtocol(parser.getText());
                break;
            case "protocolcomments":
                instance.setProtocolcomments(parser.getText());
                break;
            case "rater":
                instance.setRater(parser.getText());
                break;
            case "rescan":
                instance.setRescan(parser.getText());
                break;
            case "resolvable":
                instance.setResolvable(parser.getText());
                break;
            case "retrain":
                instance.setRetrain(parser.getText());
                break;
            case "scans_scan":
                // TODO: Handle the "scans_scan" property here: java.util.List
                break;
            case "stereotacticmarker":
                instance.setStereotacticmarker(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

