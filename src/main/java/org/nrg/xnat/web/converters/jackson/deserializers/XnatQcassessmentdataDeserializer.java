package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatQcassessmentdata;
import org.nrg.xdat.om.XnatQcassessmentdataScan;
import org.nrg.xdat.om.XnatRegionresourceLabel;
import org.nrg.xft.ItemI;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatQcassessmentdataDeserializer<T extends XnatQcassessmentdata> extends XnatMrassessordataDeserializer<T> {
    private static final long serialVersionUID = -9055330766995158772L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatQcassessmentdataDeserializer() {
        this((Class<T>) XnatQcassessmentdata.class);
    }

    protected XnatQcassessmentdataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "scans":
            	try {
            		instance.setScans_scan(parser.readValueAs(XnatQcassessmentdataScan.class));
            	} catch (Exception e) {
            		log.error("Tried to set a field base image in xnat Qcassessment data but failed", e);
            	}
                break;
            case "type":
                instance.setType(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

