package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatStatisticsdataAddfield;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatStatisticsdataAddfieldDeserializer<T extends XnatStatisticsdataAddfield> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = 501478757003583418L;

    @SuppressWarnings("unchecked")
    public XnatStatisticsdataAddfieldDeserializer() {
        this((Class<T>) XnatStatisticsdataAddfield.class);
    }

    public XnatStatisticsdataAddfieldDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "addfield":
                // TODO: Handle the "addfield" property here: String
                break;
            case "name":
                // TODO: Handle the "name" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            case "xnatStatisticsdataAddfieldId":
                // TODO: Handle the "xnatStatisticsdataAddfieldId" property here: Integer
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

