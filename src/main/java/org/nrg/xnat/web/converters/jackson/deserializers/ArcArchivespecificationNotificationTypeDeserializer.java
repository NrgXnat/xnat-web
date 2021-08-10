package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcArchivespecificationNotificationType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ArcArchivespecificationNotificationTypeDeserializer<T extends ArcArchivespecificationNotificationType> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -8701778431378912404L;

    @SuppressWarnings("unchecked")
    public ArcArchivespecificationNotificationTypeDeserializer() {
        this((Class<T>) ArcArchivespecificationNotificationType.class);
    }

    public ArcArchivespecificationNotificationTypeDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "arcArchivespecificationNotificationTypeId":
                // TODO: Handle the "arcArchivespecificationNotificationTypeId" property here: Integer
                break;
            case "emailAddresses":
                // TODO: Handle the "emailAddresses" property here: String
                break;
            case "notificationType":
                // TODO: Handle the "notificationType" property here: String
                break;
            case "schemaElementName":
                // TODO: Handle the "schemaElementName" property here: String
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}
