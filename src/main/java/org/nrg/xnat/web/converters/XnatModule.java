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
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

@Component
@Slf4j
public class XnatModule extends SimpleModule {
    public XnatModule() throws IOException {
        super("XnatJacksonModule", new Version(1, 8, 0, "SNAPSHOT", "org.nrg.xnat", "web"));
        for (final Resource resource : BasicXnatResourceLocator.getResources("classpath*:META-INF/xnat/serializers/*-jackson.ini")) {
            final INIConfiguration ini = new INIConfiguration();
            try (final Reader reader = new InputStreamReader(resource.getInputStream())) {
                ini.read(reader);
                final SubnodeConfiguration serializers         = ini.getSection("serializers");
                final Iterator<String>     serializableClasses = serializers.getKeys();
                while (serializableClasses.hasNext()) {
                    final String serializableClass = serializableClasses.next();
                    final String serializer        = serializers.getString(serializableClass);
                    try {
                        final Class<? extends BaseElement> targetClass = Class.forName("org.nrg.xdat.om." + serializableClass).asSubclass(BaseElement.class);
                        try {
                            // noinspection unchecked
                            final Class<? extends JsonSerializer<BaseElement>> serializerClass = (Class<? extends JsonSerializer<BaseElement>>) Class.forName(serializer).asSubclass(JsonSerializer.class);
                            addSerializer(targetClass, serializerClass.newInstance());
                        } catch (ClassNotFoundException e) {
                            log.error("Couldn't find class definition for serializer class {}, skipping mapping to target {}", serializer, targetClass, e);
                        } catch (IllegalAccessException | InstantiationException e) {
                            log.error("Couldn't find create instance of serializer class {}, skipping mapping to target {}", serializer, targetClass, e);
                        }
                    } catch (ClassNotFoundException e) {
                        log.error("Couldn't find class definition for target class {}, skipping mapping to serializer {}", serializableClass, serializer, e);
                    }
                }
                final SubnodeConfiguration deserializers         = ini.getSection("deserializers");
                final Iterator<String>     deserializableClasses = serializers.getKeys();
                while (deserializableClasses.hasNext()) {
                    final String deserializableClass = deserializableClasses.next();
                    final String deserializer        = deserializers.getString(deserializableClass);
                    try {
                        final Class<? extends BaseElement> targetClass = Class.forName("org.nrg.xdat.om." + deserializableClass).asSubclass(BaseElement.class);
                        try {
                            // noinspection unchecked
                            final Class<? extends JsonDeserializer<BaseElement>> deserializerClass = (Class<? extends JsonDeserializer<BaseElement>>) Class.forName(deserializer).asSubclass(JsonDeserializer.class);
                            //noinspection unchecked
                            addDeserializer((Class<BaseElement>) targetClass, deserializerClass.newInstance());
                        } catch (ClassNotFoundException e) {
                            log.error("Couldn't find class definition for serializer class {}, skipping mapping to target {}", deserializer, deserializableClass, e);
                        } catch (IllegalAccessException | InstantiationException e) {
                            log.error("Couldn't find create instance of serializer class {}, skipping mapping to target {}", deserializer, deserializableClass, e);
                        }
                    } catch (ClassNotFoundException e) {
                        log.error("Couldn't find class definition for target class {}, skipping mapping to serializer {}", deserializableClass, deserializer, e);
                    }
                }
            } catch (ConfigurationException e) {
                log.error("Tried to read serialization configuration from resource {} but got an error", resource.getURI(), e);
            }
        }
    }
}
