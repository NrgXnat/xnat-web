package org.nrg.xnat.tracking.services;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import org.nrg.xnat.tracking.model.EventLog;

import java.io.IOException;

public class EventTrackingPayloadDeserializer extends JsonDeserializer<EventLog> implements ContextualDeserializer {
    @Override
    @SuppressWarnings("unchecked")
    public EventLog deserialize(JsonParser parser, DeserializationContext context)
            throws IOException, JsonProcessingException {
        final ObjectCodec codec = parser.getCodec();
        final JsonNode node = codec.readTree(parser);
        final String type = node.get("type").asText();
        Class<?> typeClass;
        try {
            typeClass = Class.forName(type);
        } catch (ClassNotFoundException e) {
            typeClass = null;
        }
        if (typeClass == null || !EventLog.class.isAssignableFrom(typeClass)) {
            context.reportInputMismatch(typeClass, "Invalid EventTrackingPayload type: " + type);
            return null;
        }
        final Class<? extends EventLog> payloadClass = (Class<? extends EventLog>) typeClass;
        return codec.treeToValue(node, payloadClass);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        return null;
    }
}
