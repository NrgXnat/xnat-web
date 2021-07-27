package org.nrg.xnat.web.converters.jackson;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nrg.framework.services.SerializerService;
import org.nrg.framework.utilities.BasicXnatResourceLocator;
import org.nrg.xdat.model.XnatProjectdataAliasI;
import org.nrg.xdat.model.XnatProjectdataFieldI;
import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xnat.config.TestSerializationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestSerializationConfig.class)
public class TestXnatProjectdataSerialization {
    private static final String PROJECT_1_JSON_URI = "classpath:org/nrg/xnat/web/converters/jackson/Project1.json";

    private SerializerService _serializer;

    @Autowired
    public void setSerializer(final SerializerService serializer) {
        _serializer = serializer;
    }

    @Test
    public void testJsonLoad() throws IOException {
        final Resource project1Resource = BasicXnatResourceLocator.getResource(PROJECT_1_JSON_URI);

        final XnatProjectdata project1 = _serializer.deserializeJson(project1Resource.getInputStream(), XnatProjectdata.class);
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
