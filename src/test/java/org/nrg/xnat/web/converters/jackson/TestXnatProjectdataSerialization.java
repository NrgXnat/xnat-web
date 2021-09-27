package org.nrg.xnat.web.converters.jackson;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.nrg.framework.services.SerializerService;
import org.nrg.framework.utilities.BasicXnatResourceLocator;
import org.nrg.xdat.model.XnatProjectdataAliasI;
import org.nrg.xdat.model.XnatProjectdataFieldI;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xnat.test.extensions.XftDataTestExtension;
import org.nrg.xnat.config.TestXftDataSerializationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(XftDataTestExtension.class)
@ContextConfiguration(classes = TestXftDataSerializationConfig.class)
public class TestXnatProjectdataSerialization {
    private static final String              PROJECT_1_JSON_URI = "classpath:org/nrg/xnat/web/converters/jackson/Project1.json";
    private static final Map<String, String> PROJECT_1_FIELDS   = ImmutableMap.of("field1", "value1", "field2", "value2", "field3", "value3");

    private final SerializerService _serializer;

    @Autowired
    public TestXnatProjectdataSerialization(final SerializerService serializer) {
        _serializer = serializer;
    }

    @Test
    public void sanityCheck() {
        assertThat(_serializer).isNotNull();
    }

    @Test
    public void testJsonLoad() throws IOException {
        final XnatProjectdata project1 = _serializer.deserializeJson(BasicXnatResourceLocator.getResource(PROJECT_1_JSON_URI).getInputStream(), XnatProjectdata.class);

        assertThat(project1).isNotNull()
                            .hasFieldOrPropertyWithValue("id", "PROJECT_1")
                            .hasFieldOrPropertyWithValue("name", "Project 1")
                            .hasFieldOrPropertyWithValue("secondaryId", "P_1")
                            .hasFieldOrPropertyWithValue("type", "longitudinal")
                            .hasFieldOrPropertyWithValue("active", true)
                            .hasFieldOrPropertyWithValue("description", "This is longitudinal research project 1");

        final List<String> aliases = project1.getAliases_alias().stream().map(XnatProjectdataAliasI::getAlias).collect(Collectors.toList());
        assertThat(aliases).isNotNull()
                           .isNotEmpty()
                           .containsExactlyInAnyOrder("A", "Alpha");

        final Map<String, String> fields = project1.getFields_field().stream().collect(Collectors.toMap(XnatProjectdataFieldI::getName, XnatProjectdataFieldI::getField));
        assertThat(fields).isNotNull()
                          .isNotEmpty()
                          .hasSize(3)
                          .containsEntry("field1", "value1")
                          .containsEntry("field2", "value2")
                          .containsEntry("field3", "value3");
        assertThat(fields).isNotNull()
                          .isNotEmpty()
                          .hasSize(PROJECT_1_FIELDS.size())
                          .containsExactlyInAnyOrderEntriesOf(PROJECT_1_FIELDS);

        final String project1Json = _serializer.toJson(project1);
        assertThat(project1Json).isNotNull().isNotBlank();
    }
}
