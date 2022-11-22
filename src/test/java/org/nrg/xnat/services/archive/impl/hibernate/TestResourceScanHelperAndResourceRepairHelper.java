package org.nrg.xnat.services.archive.impl.hibernate;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.dcm4che2.io.StopTagInputHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nrg.dcm.DicomFileNamer;
import org.nrg.framework.services.SerializerService;
import org.nrg.framework.utilities.BasicXnatResourceLocator;
import org.nrg.xnat.entities.ResourceScanRequest;
import org.nrg.xnat.services.archive.ResourceMitigationReport;
import org.nrg.xnat.services.archive.ResourceScanReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestResourceScanHelperAndResourceRepairHelperConfig.class)
@Slf4j
public class TestResourceScanHelperAndResourceRepairHelper {
    private static final String SCAN_CATALOG_FILE = "scan_5_catalog.xml";
    private static final String SCAN_CATALOG_URI  = "classpath:dicom/duplicates/" + SCAN_CATALOG_FILE;
    private static final int    RESOURCE_ID       = 54;
    private static final String SCAN_DESCRIPTION  = "3D SPGR VOLUME 1";
    private static final String PROJECT_ID        = "Test";
    private static final String SUBJECT_ID        = "XNAT_S00001";
    private static final String EXPERIMENT_ID     = "XNAT_E00001";
    private static final String SUBJECT_LABEL     = "Test_01";
    private static final String EXPERIMENT_LABEL  = "Test_01_MR_02";
    private static final int    SCAN_ID           = 5;


    private final Path _resourcePath;
    private final Path _cachePath;

    private SerializerService   _serializer;
    private DicomFileNamer      _dicomFileNamer;
    private StopTagInputHandler _stopTagInputHandler;

    public TestResourceScanHelperAndResourceRepairHelper() throws IOException {
        final Path tempDirectory = Files.createTempDirectory("TestResourceScanHelperAndResourceRepairHelper-");
        tempDirectory.toFile().deleteOnExit();
        _cachePath = tempDirectory.resolve("cache");
        _cachePath.toFile().mkdirs();

        final Path resourcePath = tempDirectory.resolve("resource");
        resourcePath.toFile().mkdirs();

        FileUtils.copyDirectory(BasicXnatResourceLocator.getResource(SCAN_CATALOG_URI).getFile().getParentFile(), resourcePath.toFile());
        _resourcePath = resourcePath.resolve(SCAN_CATALOG_FILE);
    }

    @Autowired
    public void setSerializer(final SerializerService serializer) {
        _serializer = serializer;
    }

    @Autowired
    public void setDicomFileNamer(final DicomFileNamer dicomFileNamer) {
        _dicomFileNamer = dicomFileNamer;
    }

    @Autowired
    public void setStopTagInputHandler(final StopTagInputHandler stopTagInputHandler) {
        _stopTagInputHandler = stopTagInputHandler;
    }

    @Test
    public void testScanReport() {
        final ResourceScanRequest request    = getScanRequest();
        final ResourceScanHelper  scanHelper = new ResourceScanHelper(request, _serializer, _dicomFileNamer, _stopTagInputHandler);
        final ResourceScanReport  scanReport = scanHelper.call();

        assertThat(scanReport).isNotNull();
        assertThat(scanReport.getUids()).isNotNull().isNotEmpty().hasSize(60);
        assertThat(scanReport.getBadFiles()).isNotNull().isEmpty();
        assertThat(scanReport.getDuplicates()).isNotNull().isNotEmpty().hasSize(5);
        assertThat(scanReport.getMismatchedFiles()).isNotNull().isNotEmpty().hasSize(26);

        request.setScanReport(scanReport);

        final ResourceRepairHelper     repairHelper     = new ResourceRepairHelper(request, _cachePath);
        final ResourceMitigationReport mitigationReport = repairHelper.call();

        assertThat(mitigationReport).isNotNull();
        assertThat(mitigationReport.getRemovedFiles()).isNotNull().isNotEmpty().hasSize(36);
        assertThat(mitigationReport.getMovedFiles()).isNotNull().isNotEmpty().hasSize(31);
    }

    private ResourceScanRequest getScanRequest() {
        final ResourceScanRequest request = ResourceScanRequest.builder()
                                                               .resourceId(RESOURCE_ID)
                                                               .resourceUri(_resourcePath.toString())
                                                               .rsnStatus(ResourceScanRequest.Status.Created)
                                                               .projectId(PROJECT_ID)
                                                               .subjectId(SUBJECT_ID)
                                                               .subjectLabel(SUBJECT_LABEL)
                                                               .experimentId(EXPERIMENT_ID)
                                                               .experimentLabel(EXPERIMENT_LABEL)
                                                               .scanId(SCAN_ID)
                                                               .scanDescription(SCAN_DESCRIPTION)
                                                               .scanLabel(SCAN_DESCRIPTION)
                                                               .build();
        request.setId(RandomUtils.nextLong(1, 1000));
        return request;
    }
}
