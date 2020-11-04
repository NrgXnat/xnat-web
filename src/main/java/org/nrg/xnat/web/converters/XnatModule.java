package org.nrg.xnat.web.converters;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.utilities.BasicXnatResourceLocator;
import org.nrg.xdat.base.BaseElement;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Finds all INI files on the classpath that match the pattern <b>META-INF/xnat/serializers/*-jackson.ini</b> and loads
 * serializers and deserializers from configuration sections with the same names. The default XNAT configuration file is
 * named <b>core-xnat-jackson.ini</b>. Any INI file intended for use with this class must have the following structure:
 *
 * <pre>
 *     [serializers]
 *     package.ClassToBeSerialized1=package.Serializer1
 *     package.ClassToBeSerialized2=package.Serializer2
 *
 *     [deserializers]
 *     package.ClassToBeDeserialized1=package.Deserializer1
 *     package.ClassToBeDeserialized2=package.Deserializer2
 * </pre>
 *
 * The serializer and deserializer implementations may have either default no-argument constructors or a constructor
 * that takes the Spring <b>ApplicationContext</b> object as a parameter. Note that this implementation does not
 * currently check for duplicate entries (i.e. more than one serializer assigned for a single class type). It <i>does</i>
 * cache serializer and deserializer instances so that only a single instance of each particular class is created in the
 * case where a serializer or deserializer handles more than one serializable data type.
 */
@Component
@Slf4j
public class XnatModule extends SimpleModule {
    public XnatModule(final ApplicationContext context) throws IOException {
        super("XnatJacksonModule", new Version(1, 8, 0, "SNAPSHOT", "org.nrg.xnat", "web"));
        _context = context;
        for (final Resource resource : BasicXnatResourceLocator.getResources("classpath*:META-INF/xnat/serializers/*-jackson.ini")) {
            final INIConfiguration ini = new INIConfiguration();
            try (final Reader reader = new InputStreamReader(resource.getInputStream())) {
                ini.read(reader);
                final SubnodeConfiguration                     serializerMap  = ini.getSection("serializers");
                final Iterator<String>                         serializerKeys = serializerMap.getKeys();
                final Map<String, JsonSerializer<BaseElement>> serializers    = new HashMap<>();
                while (serializerKeys.hasNext()) {
                    final String serializerKey     = serializerKeys.next();
                    final String serializableClass = StringUtils.replace(serializerKey, "..", ".");
                    final String serializerClass   = serializerMap.getString(serializerKey);
                    try {
                        final Class<? extends BaseElement> serializable = Class.forName(serializableClass).asSubclass(BaseElement.class);
                        try {
                            final JsonSerializer<BaseElement> instance;
                            if (serializers.containsKey(serializerClass)) {
                                instance = serializers.get(serializerClass);
                            } else {
                                // noinspection unchecked
                                final Class<? extends JsonSerializer<BaseElement>> serializer = (Class<? extends JsonSerializer<BaseElement>>) Class.forName(serializerClass).asSubclass(JsonSerializer.class);
                                instance = getInstance(serializer);
                                serializers.put(serializerClass, instance);
                            }
                            addSerializer(serializable, instance);
                        } catch (ClassNotFoundException e) {
                            log.error("Couldn't find class definition for serializer class {}, skipping mapping to target {}", serializerClass, serializableClass, e);
                        } catch (IllegalAccessException | InstantiationException e) {
                            log.error("Couldn't find create instance of serializer class {}, skipping mapping to target {}", serializerClass, serializableClass, e);
                        }
                    } catch (ClassNotFoundException e) {
                        log.error("Couldn't find class definition for target class {}, skipping mapping to serializer {}", serializableClass, serializerClass, e);
                    }
                }
                final SubnodeConfiguration                       deserializerMap    = ini.getSection("deserializers");
                final Iterator<String>                           deserializableKeys = serializerMap.getKeys();
                final Map<String, JsonDeserializer<BaseElement>> deserializers      = new HashMap<>();
                while (deserializableKeys.hasNext()) {
                    final String deserializerKey     = deserializableKeys.next();
                    final String deserializableClass = StringUtils.replace(deserializerKey, "..", ".");
                    final String deserializerClass   = deserializerMap.getString(deserializerKey);
                    try {
                        final Class<? extends BaseElement> deserializable = Class.forName(deserializableClass).asSubclass(BaseElement.class);
                        try {
                            final JsonDeserializer<BaseElement> instance;
                            if (deserializers.containsKey(deserializerClass)) {
                                instance = deserializers.get(deserializerClass);
                            } else {
                                // noinspection unchecked
                                final Class<? extends JsonDeserializer<BaseElement>> deserializer = (Class<? extends JsonDeserializer<BaseElement>>) Class.forName(deserializerClass).asSubclass(JsonDeserializer.class);
                                instance = getInstance(deserializer);
                                deserializers.put(deserializerClass, instance);
                            }
                            //noinspection unchecked
                            addDeserializer((Class<BaseElement>) deserializable, instance);
                        } catch (ClassNotFoundException e) {
                            log.error("Couldn't find class definition for serializer class {}, skipping mapping to target {}", deserializerClass, deserializableClass, e);
                        } catch (IllegalAccessException | InstantiationException e) {
                            log.error("Couldn't find create instance of serializer class {}, skipping mapping to target {}", deserializerClass, deserializableClass, e);
                        }
                    } catch (ClassNotFoundException e) {
                        log.error("Couldn't find class definition for target class {}, skipping mapping to serializer {}", deserializableClass, deserializerClass, e);
                    }
                }
            } catch (ConfigurationException e) {
                log.error("Tried to read serialization configuration from resource {} but got an error", resource.getURI(), e);
            }
        }
    }

    private <T> T getInstance(final Class<? extends T> clazz) throws IllegalAccessException, InstantiationException {
        try {
            return clazz.getConstructor(ApplicationContext.class).newInstance(_context);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            return clazz.newInstance();
        }
    }

    private final ApplicationContext _context;
}
