
package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;

import org.nrg.xdat.model.XnatDicomcodedvalueI;
import org.nrg.xdat.om.XnatAlgorithm;
import org.nrg.xdat.om.XnatDicomcodedvalue;
import org.nrg.xdat.om.XnatQcassessmentdataScan;
import org.nrg.xft.ItemI;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class XnatAlgorithmDeserializer<T extends XnatAlgorithm> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -2866520238185881707L;

    @SuppressWarnings({"unchecked", "unused"})
    public XnatAlgorithmDeserializer() {
        this((Class<T>) XnatAlgorithm.class);
    }

    protected XnatAlgorithmDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "family":
                // TODO: Handle the "family" property here: org.nrg.xdat.model.XnatDicomcodedvalueI
            	try {
            		instance.setFamily((ItemI)parser.readValueAs(XnatDicomcodedvalue.class));
            	} catch (Exception e) {
            		log.error("Tried to set a field family in xnat Dicomcoded value but failed", e);
            	}
                break;
            case "name":
                instance.setName(parser.getText());
                break;
            case "namecode":
                // TODO: Handle the "namecode" property here: org.nrg.xdat.model.XnatDicomcodedvalueI
            	try {
            		instance.setNamecode((ItemI)parser.readValueAs(XnatDicomcodedvalueI.class));
            	} catch (Exception e) {
            		log.error("Tried to set a field family in xnat Dicomcoded value but failed", e);
            	}
                break;
            case "parameters":
                instance.setParameters(parser.getText());
                break;
            case "source":
                instance.setSource(parser.getText());
                break;
            case "version":
                instance.setVersion(parser.getText());
                break;
            case "xnatAlgorithmId":
                instance.setXnatAlgorithmId(parser.getIntValue());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

