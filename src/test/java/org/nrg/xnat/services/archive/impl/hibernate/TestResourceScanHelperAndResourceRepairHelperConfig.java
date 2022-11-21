package org.nrg.xnat.services.archive.impl.hibernate;

import lombok.extern.slf4j.Slf4j;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.StopTagInputHandler;
import org.nrg.dcm.DicomFileNamer;
import org.nrg.dcm.id.TemplatizedDicomFileNamer;
import org.nrg.framework.configuration.SerializerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(SerializerConfig.class)
@Slf4j
public class TestResourceScanHelperAndResourceRepairHelperConfig {
    @Bean
    public DicomFileNamer dicomFileNamer() {
        return new TemplatizedDicomFileNamer("${StudyInstanceUID}-${SeriesNumber}-${InstanceNumber}-${HashSOPClassUIDWithSOPInstanceUID}");
    }

    @Bean
    public StopTagInputHandler stopTagInputHandler() {
        return new StopTagInputHandler(Tag.PixelData);
    }
}
