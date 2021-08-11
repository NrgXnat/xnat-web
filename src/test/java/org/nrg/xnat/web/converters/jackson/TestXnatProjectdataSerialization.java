package org.nrg.xnat.web.converters.jackson;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.nrg.framework.services.SerializerService;
import org.nrg.framework.utilities.BasicXnatResourceLocator;
import org.nrg.xdat.model.XnatProjectdataAliasI;
import org.nrg.xdat.model.XnatProjectdataFieldI;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xnat.config.TestSerializationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestSerializationConfig.class)
public class TestXnatProjectdataSerialization {
    private static final String PROJECT_1_JSON_URI = "classpath:org/nrg/xnat/web/converters/jackson/Project1.json";

    private SerializerService _serializer;

    @Autowired
    public void setSerializer(final SerializerService serializer) {
        _serializer = serializer;
    }

    @Test
    @Disabled("Can't run until XFT issues with unit testing have been resolved")
    public void testJsonLoad() throws IOException {
        final Resource project1Resource = BasicXnatResourceLocator.getResource(PROJECT_1_JSON_URI);

        // TODO: This is only here because the framework library doesn't support the commented-out call below.
        //          final XnatProjectdata project1 = _serializer.deserializeJson(project1Resource.getInputStream(), XnatProjectdata.class);
        final String text;
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(project1Resource.getInputStream(), StandardCharsets.UTF_8))) {
            text = reader.lines().collect(Collectors.joining("\n"));
        }

        final XnatProjectdata project1 = _serializer.deserializeJson(text, XnatProjectdata.class);
        assertThat(project1).isNotNull()
                            .hasFieldOrPropertyWithValue("id", "PROJECT_1")
                            .hasFieldOrPropertyWithValue("name", "Project 1")
                            .hasFieldOrPropertyWithValue("secondaryId", "P_1")
                            .hasFieldOrPropertyWithValue("type", "longitudinal")
                            .hasFieldOrPropertyWithValue("active", true)
                            .hasFieldOrPropertyWithValue("autoArchive", true)
                            .hasFieldOrPropertyWithValue("description", "This is longitudinal research project 1");

        final List<String> aliases = project1.getAliases_alias().stream().map(XnatProjectdataAliasI::getAlias).collect(Collectors.toList());
        assertThat(aliases).isNotNull().isNotEmpty().containsExactlyInAnyOrder("A", "Alpha");

        final Map<String, String> fields = project1.getFields_field().stream().collect(Collectors.toMap(XnatProjectdataFieldI::getName, XnatProjectdataFieldI::getField));
        assertThat(fields).isNotNull().isNotEmpty().hasSize(3).containsEntry("field1", "value1").containsEntry("field2", "value2").containsEntry("field3", "value3");

        final String project1Json = _serializer.toJson(project1);
        assertThat(project1Json).isNotNull().isNotBlank();
    }
}
