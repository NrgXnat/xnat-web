package org.nrg.xnat.web.converters;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.reflect.ClassPath;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xnat.web.converters.jackson.deserializers.AbstractBaseElementDeserializer;
import org.nrg.xnat.web.converters.jackson.deserializers.XnatDeserializer;
import org.nrg.xnat.web.converters.jackson.serializers.AbstractBaseElementSerializer;
import org.nrg.xnat.web.converters.jackson.serializers.XnatSerializer;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Gets all implementations of {@link AbstractBaseElementSerializer} and {@link AbstractBaseElementDeserializer} from
 * the application context and adds them as serializers and deserializers of the serializable type for each instance.
 */
@Component
@Slf4j
public class XnatModule extends SimpleModule {
    private static final long serialVersionUID = -8435865736483844675L;

    @SuppressWarnings({"UnstableApiUsage"})
    public XnatModule() throws IOException {
        super("XnatJacksonModule", new Version(1, 8, 2, "SNAPSHOT", "org.nrg.xnat", "web"));
        final ClassPath classPath = ClassPath.from(this.getClass().getClassLoader());
        classPath.getTopLevelClasses(AbstractBaseElementSerializer.class.getPackage().getName())
                 .stream()
                 .map(ClassPath.ClassInfo::load)
                 .filter(clazz -> clazz.isAnnotationPresent(XnatSerializer.class))
                 .forEach(this::createSerializerClass);
        classPath.getTopLevelClasses(AbstractBaseElementDeserializer.class.getPackage().getName())
                 .stream()
                 .map(ClassPath.ClassInfo::load)
                 .filter(clazz -> clazz.isAnnotationPresent(XnatDeserializer.class))
                 .forEach(this::createDeserializerClass);
    }

    @SuppressWarnings("unchecked")
    private void createSerializerClass(final Class<?> serializerClass) {
        try {
            final AbstractBaseElementSerializer<?> serializer = serializerClass.asSubclass(AbstractBaseElementSerializer.class).newInstance();
            addSerializer(serializer.getSerializableType(), (JsonSerializer<BaseElement>) serializer);
            log.info("Added the {} serializer for handling instances of the {} class", serializer.getClass().getName(), serializer.getSerializableType().getName());
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("An error occurred trying to create an instance of the {} class", serializerClass.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private void createDeserializerClass(final Class<?> deserializerClass) {
        try {
            final AbstractBaseElementDeserializer<?> deserializer = deserializerClass.asSubclass(AbstractBaseElementDeserializer.class).newInstance();
            addDeserializer((Class<BaseElement>) deserializer.getSerializableType(), deserializer);
            log.info("Added the {} deserializer for handling instances of the {} class", deserializer.getClass().getName(), deserializer.getSerializableType().getName());
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("An error occurred trying to create an instance of the {} class", deserializerClass.getName(), e);
        }
    }
}
