package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcArchivespecificationNotificationType;

import java.io.IOException;

@XnatDeserializer
@Slf4j
public class ArcArchivespecificationNotificationTypeDeserializer<T extends ArcArchivespecificationNotificationType> extends AbstractBaseElementDeserializer<T> {
    private static final long serialVersionUID = -2943298433517835879L;

    @SuppressWarnings({"unchecked", "unused"})
    public ArcArchivespecificationNotificationTypeDeserializer() {
        this((Class<T>) ArcArchivespecificationNotificationType.class);
    }

    protected ArcArchivespecificationNotificationTypeDeserializer(final Class<T> clazz) {
        super(clazz);
    }

    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        // TODO: Implement datatype-specific deserialization
        switch (field) {
            case "arcArchivespecificationNotificationTypeId":
                instance.setArcArchivespecificationNotificationTypeId(parser.getIntValue());
                break;
            case "emailAddresses":
                instance.setEmailAddresses(parser.getText());
                break;
            case "notificationType":
                instance.setNotificationType(parser.getText());
                break;
            default:
                super.handleField(instance, field, parser, context);
        }
    }
}

