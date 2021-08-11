package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMrqcscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatMrqcscandataDeserializer<T extends XnatMrqcscandata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 7165340820277203642L;

    @SuppressWarnings("unchecked")
    public XnatMrqcscandataDeserializer() {
        this((Class<T>) XnatMrqcscandata.class);
    }

    public XnatMrqcscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "blurring":
                // TODO: Handle the "blurring" property here: String
                break;
            case "flow":
                // TODO: Handle the "flow" property here: String
                break;
            case "imagecontrast":
                // TODO: Handle the "imagecontrast" property here: String
                break;
            case "inhomogeneity":
                // TODO: Handle the "inhomogeneity" property here: String
                break;
            case "interpacmotion":
                // TODO: Handle the "interpacmotion" property here: String
                break;
            case "qcscandata":
                // TODO: Handle the "qcscandata" property here: org.nrg.xdat.om.XnatQcscandata
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "susceptibility":
                // TODO: Handle the "susceptibility" property here: String
                break;
            case "wrap":
                // TODO: Handle the "wrap" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

