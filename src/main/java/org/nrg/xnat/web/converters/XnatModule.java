package org.nrg.xnat.web.converters;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xnat.web.converters.jackson.deserializers.AbstractBaseElementDeserializer;
import org.nrg.xnat.web.converters.jackson.serializers.AbstractBaseElementSerializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * Gets all implementations of {@link AbstractBaseElementSerializer} and {@link AbstractBaseElementDeserializer} from
 * the application context and adds them as serializers and deserializers of the serializable type for each instance.
 */
@Component
@Slf4j
public class XnatModule extends SimpleModule {
    private static final long serialVersionUID = -8435865736483844675L;

    @SuppressWarnings("unchecked")
    public XnatModule(final List<? extends AbstractBaseElementSerializer<? extends BaseElement>> serializers, final List<? extends AbstractBaseElementDeserializer<? extends BaseElement>> deserializers) throws IOException {
        super("XnatJacksonModule", new Version(1, 8, 0, "SNAPSHOT", "org.nrg.xnat", "web"));
        for (final AbstractBaseElementSerializer<?> serializer : serializers) {
            addSerializer(serializer.getSerializableType(), (JsonSerializer<BaseElement>) serializer);
            log.info("Added the {} serializer for handling instances of the {} class", serializer.getClass().getName(), serializer.getSerializableType().getName());
        }
        for (final AbstractBaseElementDeserializer<?> deserializer : deserializers) {
            addDeserializer((Class<BaseElement>) deserializer.getSerializableType(), deserializer);
            log.info("Added the {} deserializer for handling instances of the {} class", deserializer.getClass().getName(), deserializer.getSerializableType().getName());
        }
    }
}
