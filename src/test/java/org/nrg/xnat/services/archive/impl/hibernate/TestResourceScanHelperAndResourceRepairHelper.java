package org.nrg.xnat.services.archive.impl.hibernate;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.dcm4che2.io.StopTagInputHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.nrg.dcm.DicomFileNamer;
import org.nrg.framework.services.ContextService;
import org.nrg.framework.services.SerializerService;
import org.nrg.framework.utilities.BasicXnatResourceLocator;
import org.nrg.xdat.XDAT;
import org.nrg.xdat.model.CatEntryI;
import org.nrg.xdat.om.WrkWorkflowdata;
import org.nrg.xdat.om.XnatMrscandata;
import org.nrg.xdat.om.XnatResourcecatalog;
import org.nrg.xft.event.EventMetaI;
import org.nrg.xft.security.UserI;
import org.nrg.xft.utils.SaveItemHelper;
import org.nrg.xnat.entities.ResourceScanRequest;
import org.nrg.xnat.services.archive.RemoteFilesService;
import org.nrg.xnat.services.archive.ResourceMitigationReport;
import org.nrg.xnat.services.archive.ResourceScanReport;
import org.nrg.xnat.utils.CatalogUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@PrepareForTest({ResourceRepairHelper.class, XDAT.class, SaveItemHelper.class})
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
    private static final String PROPER_FILENAME_LIST = "classpath:dicom/duplicates/expectedNames.txt";

    @Mock
    private XnatResourcecatalog _mockResource;

    @Mock
    private ContextService _mockContextService;

    @Mock
    private WrkWorkflowdata _mockWorkflow;

    @Mock
    private UserI _mockUser;

    private final Path _resourcePath;
    private final Path _cachePath;
    private final String[] _expectedFilenames;

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

        FileUtils.copyDirectory(BasicXnatResourceLocator.getResource(SCAN_CATALOG_URI).getFile().getParentFile(),
                resourcePath.toFile());
        _resourcePath = resourcePath.resolve(SCAN_CATALOG_FILE);

        _expectedFilenames = Files.readAllLines(BasicXnatResourceLocator.getResource(PROPER_FILENAME_LIST)
                .getFile().toPath()).toArray(new String[0]);
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
    public void testScanReport() throws Exception {
        final ResourceScanRequest request    = getScanRequest();
        final ResourceScanHelper  scanHelper = new ResourceScanHelper(request, _serializer
                , _dicomFileNamer, _stopTagInputHandler);

        final ResourceScanReport  scanReport = scanHelper.call();

        assertThat(scanReport).isNotNull();
        assertThat(scanReport.getUids()).isNotNull().isNotEmpty().hasSize(60);
        assertThat(scanReport.getBadFiles()).isNotNull().isEmpty();
        assertThat(scanReport.getDuplicates()).isNotNull().isNotEmpty().hasSize(5);
        assertThat(scanReport.getMismatchedFiles()).isNotNull().isNotEmpty().hasSize(26);

        request.setScanReport(scanReport);

        // CatalogUtils leverages these methods
        PowerMockito.mockStatic(XDAT.class);
        PowerMockito.when(XDAT.getSiteConfigurationProperty("checksums")).thenReturn("false");
        PowerMockito.when(XDAT.getSerializerService()).thenReturn(_serializer);
        PowerMockito.when(XDAT.getContextService()).thenReturn(_mockContextService);
        Mockito.when(_mockContextService.getBeanSafely(RemoteFilesService.class)).thenReturn(null);
        PowerMockito.mockStatic(SaveItemHelper.class);
        PowerMockito.when(SaveItemHelper.authorizedSave(eq(_mockResource), eq(_mockUser), anyBoolean(),
                anyBoolean(), any(EventMetaI.class))).thenReturn(true);

        // Gross private method mock bc XFT :(
        final ResourceRepairHelper repairHelper = PowerMockito.spy(new ResourceRepairHelper(request, _cachePath,
                _mockWorkflow, _mockUser));
        PowerMockito.doReturn(_mockResource).when(repairHelper, "getCatalogResource");
        Mockito.when(_mockResource.getUri()).thenReturn(_resourcePath.toString());

        // Ok, back to business...
        final ResourceMitigationReport mitigationReport = repairHelper.call();

        assertThat(mitigationReport).isNotNull();
        assertThat(mitigationReport.getRemovedFiles()).isNotNull().isNotEmpty().hasSize(36);
        assertThat(mitigationReport.getMovedFiles()).isNotNull().isNotEmpty().hasSize(31);

        CatalogUtils.CatalogData catalogData = new CatalogUtils.CatalogData(_resourcePath.toFile(), null);
        Assert.assertThat(catalogData.catBean.getEntries_entry(), hasSize(60));
        Assert.assertThat(catalogData.catBean.getEntries_entry().stream().map(CatEntryI::getUri).collect(Collectors.toList()), containsInAnyOrder(_expectedFilenames));
        Assert.assertThat(catalogData.catBean.getEntries_entry().stream().map(CatEntryI::getId).collect(Collectors.toList()),
                containsInAnyOrder(_expectedFilenames));
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
                                                               .xsiType(XnatMrscandata.SCHEMA_ELEMENT_NAME)
                                                               .build();
        request.setId(RandomUtils.nextLong(1, 1000));
        return request;
    }
}
