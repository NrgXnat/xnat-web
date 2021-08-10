package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.XnatMgscandata;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class XnatMgscandataDeserializer<T extends XnatMgscandata> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -6683741787164916514L;

    @SuppressWarnings("unchecked")
    public XnatMgscandataDeserializer() {
        this((Class<T>) XnatMgscandata.class);
    }

    public XnatMgscandataDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "imagescandata":
                // TODO: Handle the "imagescandata" property here: org.nrg.xdat.om.XnatImagescandata
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

