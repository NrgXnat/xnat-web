package org.nrg.xnat.web.converters.jackson;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.nrg.framework.services.SerializerService;
import org.nrg.framework.utilities.BasicXnatResourceLocator;
import org.nrg.xdat.om.XnatSubjectdata;
import org.nrg.xnat.test.extensions.XftDataTestExtension;
import org.nrg.xnat.config.TestXftDataSerializationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(XftDataTestExtension.class)
@ContextConfiguration(classes = TestXftDataSerializationConfig.class)
public class TestXnatSubjectdataSerialization {
    private static final String SUBJECT_1_JSON_URI = "classpath:org/nrg/xnat/web/converters/jackson/Subject1.json";

    private final SerializerService _serializer;

    @Autowired
    public TestXnatSubjectdataSerialization(final SerializerService serializer) {
        _serializer = serializer;
    }

    @Test
    public void sanityCheck() {
        assertThat(_serializer).isNotNull();
    }

    @Test
    @Disabled("Currently fails with the warning: Conflicting setter definitions for property \"demographics\": org.nrg.xdat.om.base.auto.AutoXnatSubjectdata#setDemographics(1 params) vs org.nrg.xdat.om.base.auto.AutoXnatSubjectdata#setDemographics(1 params)")
    public void testJsonLoad() throws IOException {
        final XnatSubjectdata subject1 = _serializer.deserializeJson(BasicXnatResourceLocator.getResource(SUBJECT_1_JSON_URI).getInputStream(), XnatSubjectdata.class);

        assertThat(subject1).isNotNull()
                            .hasFieldOrPropertyWithValue("id", "XNAT_S00001")
                            .hasFieldOrPropertyWithValue("project", "PROJECT_1")
                            .hasFieldOrPropertyWithValue("label", "PROJECT_1_01");

        final String subject1Json = _serializer.toJson(subject1);
        assertThat(subject1Json).isNotNull().isNotBlank();
    }
}
