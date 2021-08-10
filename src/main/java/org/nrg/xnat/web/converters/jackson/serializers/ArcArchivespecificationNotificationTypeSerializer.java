package org.nrg.xnat.web.converters.jackson.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.om.ArcArchivespecificationNotificationType;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class ArcArchivespecificationNotificationTypeSerializer<T extends ArcArchivespecificationNotificationType> extends AbstractBaseElementSerializer<T> {
    private static final long serialVersionUID = 6698354143965275797L;

    @SuppressWarnings("unchecked")
    public ArcArchivespecificationNotificationTypeSerializer() {
        this((Class<T>) ArcArchivespecificationNotificationType.class);
    }

    protected ArcArchivespecificationNotificationTypeSerializer(final Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected void serializeImpl(final T instance, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        // TODO: Implement datatype-specific serialization
        // TODO: Write out the "arcArchivespecificationNotificationTypeId" property here: Integer
        // TODO: Write out the "emailAddresses" property here: String
        // TODO: Write out the "notificationType" property here: String
        // TODO: Write out the "schemaElementName" property here: String
    }
}

