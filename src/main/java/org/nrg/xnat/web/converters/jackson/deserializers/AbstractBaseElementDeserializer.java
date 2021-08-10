package org.nrg.xnat.web.converters.jackson.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.nrg.framework.exceptions.NrgServiceRuntimeException;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xdat.om.XdatUser;
import org.nrg.xdat.security.XDATUser;
import org.nrg.xdat.security.helpers.Users;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.security.UserI;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Getter
@Accessors(prefix = "_")
@Slf4j
public abstract class AbstractBaseElementDeserializer<T extends BaseElement> extends StdDeserializer<T> {
    public static final TypeReference<? extends Map<String, String>> MAP_STRING_STRING = new TypeReference<HashMap<String, String>>() {
    };
    public static final TypeReference<? extends List<String>>        LIST_STRING       = new TypeReference<ArrayList<String>>() {
    };

    private static final long serialVersionUID = -3794209460383176862L;

    private final Class<T> _serializableType;

    public AbstractBaseElementDeserializer(final Class<T> clazz) {
        super(clazz);
        _serializableType = clazz;
        log.info("Created the {} deserializer for handling instances of the {} class", getClass().getName(), _serializableType.getName());
    }

    /**
     * The standard deserialization method from the Jackson implementation. This mostly checks that the parser
     * is in a good state, then calls {@link #deserializeImpl(JsonParser, DeserializationContext, T)}.
     *
     * @param parser  Parser used for reading JSON content
     * @param context Context that can be used to access information about this deserialization activity.
     *
     * @return A deserialized instance of the top-level data type.
     *
     * @throws JsonProcessingException When a JSON handling or processing error occurs.
     */
    @Override
    public T deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        if (parser.getCurrentToken() != JsonToken.START_OBJECT) {
            throw new InvalidFormatException("Invalid start marker", parser.getCurrentToken(), _serializableType);
        }

        final T deserialized = deserialize(parser, context, getInstance(context));
        log.debug("Deserialized object of type {}", deserialized.getClass().getName());
        return deserialized;
    }

    /**
     * The standard deserialization method from the Jackson implementation. This mostly checks that the parser
     * is in a good state, then calls {@link #deserializeImpl(JsonParser, DeserializationContext, T)}.
     *
     * @param parser   Parser used for reading JSON content
     * @param context  Context that can be used to access information about this deserialization activity.
     * @param instance An instance of the top-level data type to be populated with deserialized values.
     *
     * @return A deserialized instance of the top-level data type.
     *
     * @throws JsonProcessingException When a JSON handling or processing error occurs.
     */
    public T deserialize(final JsonParser parser, final DeserializationContext context, final T instance) throws IOException {
        return deserializeImpl(parser, context, instance);
    }

    /**
     * This is the primary deserialization method for top-level data types. This can be overridden but generally superclasses
     * should just implement the {@link #handleField(BaseElement, String, JsonParser, DeserializationContext)} method.
     *
     * @param parser   Parser used for reading JSON content
     * @param context  Context that can be used to access information about this deserialization activity.
     * @param instance An instance of the top-level data type to be populated with deserialized values.
     *
     * @return A deserialized instance of this implementation's data type
     *
     * @throws IOException When an error occurs reading or writing data.
     */
    protected T deserializeImpl(final JsonParser parser, final DeserializationContext context, final T instance) throws IOException {
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            final String field = parser.getCurrentName();
            handleField(instance, field, parser, context);
        }
        return instance;
    }

    /**
     * This method provides a hook to let each deserializer try to deserialize fields from superclasses. The default version
     * of this method doesn't handle any fields and just logs a warning that no classes in the deserializer hierarchy knows
     * how to handle the field.
     *
     * @param instance An instance of the top-level deserializer's data type
     * @param field    The name of the field to be handled
     * @param parser   The parser with the JSON content
     * @param context  Context that can be used to access information about this deserialization activity.
     */
    protected void handleField(final T instance, final String field, final JsonParser parser, final DeserializationContext context) throws IOException {
        log.warn("Found an unknown field {} for type {}", field, _serializableType.getName());
    }

    /**
     * Creates a new instance of the <b>T</b> class by calling the default no-arguments constructor. If the class
     * can't be created with just the default constructor, you must override this method in your subclass.
     *
     * @return A new instance of the <b>T</b> class.
     */
    protected T getNewInstance() {
        try {
            return _serializableType.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new NrgServiceRuntimeException("Unable to create a new " + _serializableType.getName() + " instance with the default constructor", e);
        }
    }

    /**
     * Gets an instance of the <b>T</b> class, first by checking the deserialization context for a cached instance,
     * then by calling the {@link #getNewInstance()} method.
     *
     * @param context The deserialization context.
     *
     * @return A new instance of the <b>T</b> class.
     */
    protected T getInstance(final DeserializationContext context) {
        return _serializableType.cast(Optional.ofNullable(context.getAttribute("XnatItem")).orElseGet(() -> {
            final T newInstance = getNewInstance();
            context.setAttribute("XnatItem", newInstance);
            return newInstance;
        }));
    }

    protected XdatUser getUser(final String username) {
        return XDATUser.getXdatUsersByLogin(username, null, false);
    }

    protected UserI getUserI(final String username) {
        try {
            return Users.getUser(username);
        } catch (UserInitException | UserNotFoundException e) {
            log.error("Tried to fetch user {} but failed for some reason", username, e);
            return null;
        }
    }

    protected T cast(final T instance) {
        return _serializableType.cast(instance);
    }

    protected Date parseDate(final String date) {
        return Date.from(LocalDate.parse(date).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    protected String stringify(final JsonParser parser, final DeserializationContext context) throws IOException {
        final JsonToken token = parser.getCurrentToken();
        if (token == JsonToken.START_ARRAY || token == JsonToken.START_OBJECT) {
            return context.readValue(parser, JsonNode.class).toString();
        }
        if (token == JsonToken.VALUE_FALSE) {
            return Boolean.FALSE.toString();
        }
        if (token == JsonToken.VALUE_TRUE) {
            return Boolean.TRUE.toString();
        }
        if (token == JsonToken.VALUE_NULL) {
            return null;
        }
        if (token == JsonToken.VALUE_NUMBER_FLOAT) {
            return Float.toString(parser.getFloatValue());
        }
        if (token == JsonToken.VALUE_NUMBER_INT) {
            return Integer.toString(parser.getIntValue());
        }
        if (token == JsonToken.VALUE_STRING) {
            return parser.getText();
        } else {
            return parser.getValueAsString();
        }
    }
}
