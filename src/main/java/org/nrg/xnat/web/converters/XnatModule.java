package org.nrg.xnat.web.converters;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.nrg.framework.utilities.BasicXnatResourceLocator;
import org.nrg.xdat.base.BaseElement;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

@Component
@Slf4j
public class XnatModule extends SimpleModule {
    public XnatModule() throws IOException {
        super("XnatJacksonModule", new Version(1, 8, 0, "SNAPSHOT", "org.nrg.xnat", "web"));
        for (final Resource resource : BasicXnatResourceLocator.getResources("classpath*:META-INF/xnat/serializers/*-jackson.ini")) {
            final INIConfiguration ini = new INIConfiguration();
            try (final Reader reader = new InputStreamReader(resource.getInputStream())) {
                ini.read(reader);
                final Properties serializers = ini.getProperties("serializers");
                for (final String target : serializers.stringPropertyNames()) {
                    final String serializer = serializers.getProperty(target);
                    try {
                        final Class<? extends BaseElement> targetClass = Class.forName(target).asSubclass(BaseElement.class);
                        try {
                            // noinspection unchecked
                            final Class<? extends JsonSerializer<BaseElement>> serializerClass = (Class<? extends JsonSerializer<BaseElement>>) Class.forName(serializer).asSubclass(JsonSerializer.class);
                            addSerializer(targetClass, serializerClass.newInstance());
                        } catch (ClassNotFoundException e) {
                            log.error("Couldn't find class definition for serializer class {}, skipping mapping to target {}", serializer, target, e);
                        } catch (IllegalAccessException | InstantiationException e) {
                            log.error("Couldn't find create instance of serializer class {}, skipping mapping to target {}", serializer, target, e);
                        }
                    } catch (ClassNotFoundException e) {
                        log.error("Couldn't find class definition for target class {}, skipping mapping to serializer {}", target, serializer, e);
                    }
                }
                final Properties deserializers = ini.getProperties("deserializers");
                for (final String target : deserializers.stringPropertyNames()) {
                    final String deserializer = deserializers.getProperty(target);
                    try {
                        final Class<? extends BaseElement> targetClass = Class.forName(target).asSubclass(BaseElement.class);
                        try {
                            // noinspection unchecked
                            final Class<? extends JsonDeserializer<BaseElement>> deserializerClass = (Class<? extends JsonDeserializer<BaseElement>>) Class.forName(deserializer).asSubclass(JsonDeserializer.class);
                            //noinspection unchecked
                            addDeserializer((Class<BaseElement>) targetClass, deserializerClass.newInstance());
                        } catch (ClassNotFoundException e) {
                            log.error("Couldn't find class definition for serializer class {}, skipping mapping to target {}", deserializer, target, e);
                        } catch (IllegalAccessException | InstantiationException e) {
                            log.error("Couldn't find create instance of serializer class {}, skipping mapping to target {}", deserializer, target, e);
                        }
                    } catch (ClassNotFoundException e) {
                        log.error("Couldn't find class definition for target class {}, skipping mapping to serializer {}", target, deserializer, e);
                    }
                }
            } catch (ConfigurationException e) {
                log.error("Tried to read serialization configuration from resource {} but got an error", resource.getURI(), e);
            }
        }
    }
}
