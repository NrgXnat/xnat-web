package org.nrg.xnat.dicom.mizer.service;

import org.apache.commons.io.FileUtils;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.SequenceDicomElement;
import org.dcm4che2.data.Tag;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nrg.dicom.mizer.exceptions.MizerException;
import org.nrg.dicom.mizer.objects.AnonymizationResult;
import org.nrg.dicom.mizer.objects.DicomObjectFactory;
import org.nrg.dicom.mizer.objects.DicomObjectI;
import org.nrg.dicom.mizer.service.*;
import org.nrg.dicom.mizer.service.impl.MizerContextWithScript;
import org.nrg.test.utils.TestFileUtils;
import org.nrg.xnat.dicom.mizer.config.MizerServiceTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@SuppressWarnings("Duplicates")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MizerServiceTestConfig.class)
public class BaseMizerServiceTest extends BaseMizerTest {
    @Test
    public void createService() {
        assertNotNull(service);
    }

    @Test
    public void serviceHasMultipleHandlers() {
        Collection<Mizer> mizers = null;
        try {
            mizers = service.getMizers();
        } catch (MizerException e) {
            fail("Unexpected exception: " + e);
        }
        assertEquals(2, mizers.size());
    }

    @Test
    @Ignore
    public void mizersAreInDescendingMaxVersionOrder() {

    }

    @Test
    public void anonOnCopyTestv4() {
        long scriptId = 0L;

        String script = "- (0008,103e)\n";

        try (InputStream is = new ByteArrayInputStream(script.getBytes("UTF-8"))) {
            final File anonTestFile = TestFileUtils.copyTestFileToTemp(TEST_FILE);

            DicomObjectI pre_dobj = DicomObjectFactory.newInstance(anonTestFile);
            assertTrue(pre_dobj.contains(0x0008103e));

            service.anonymize(anonTestFile, PROJECT, SUBJECT, SESSION, false, false, scriptId, is);

            DicomObjectI post_dobj = DicomObjectFactory.newInstance(anonTestFile);
            assertFalse(post_dobj.contains(0x0008103e));
        } catch (Exception e) {
            fail("Unexpected exception " + e);
        }
    }

    @Test
    public void anonOnCopyTestv6() {
        String project = "my_project";
        String subject = "my_subject";
        String session = "my_session";
        long scriptId = 0L;

        String script = "version \"6.0\"\n- (0008,103e)\n";

        try (InputStream is = new ByteArrayInputStream(script.getBytes("UTF-8"))) {
            final File anonTestFile = TestFileUtils.copyTestFileToTemp(TEST_FILE);

            DicomObjectI pre_dobj = DicomObjectFactory.newInstance(anonTestFile);
            assertTrue(pre_dobj.contains(0x0008103e));

            service.anonymize(anonTestFile, project, subject, session, false, false, scriptId, is);

            DicomObjectI post_dobj = DicomObjectFactory.newInstance(anonTestFile);
            assertFalse(post_dobj.contains(0x0008103e));

        } catch (Exception e) {
            fail("Unexpected exception " + e);
        }
    }

    @Test
    public void mizerServiceDE6UpdatesFileDeidentificationMethodCodeSequence() {
        try {
            final File anonTestFile = TestFileUtils.copyTestFileToTemp(TEST_FILE);
            service.anonymize(anonTestFile, PROJECT, SUBJECT, SESSION, true, false, 1L, SET_STD_ATTRS_DE6);

            final DicomObjectI dicomObject = DicomObjectFactory.newInstance(anonTestFile);

            assertEquals(PROJECT, dicomObject.getString(TAG_PROJECT));
            assertEquals(SUBJECT, dicomObject.getString(TAG_SUBJECT));
            assertEquals(SESSION, dicomObject.getString(TAG_SESSION));

            final org.dcm4che2.data.DicomObject dcm4cheObject = dicomObject.getDcm4che2Object();
            final DicomElement element = dcm4cheObject.get(Tag.DeidentificationMethodCodeSequence);

            assertNotNull(element);
            assertTrue(element instanceof SequenceDicomElement);
            assertEquals(1, element.countItems());

            final org.dcm4che2.data.DicomObject sequence = element.getDicomObject();
            assertNotNull(sequence);
            assertEquals("1", sequence.getString(Tag.CodeValue));
            assertEquals("XNAT DicomEdit 6 Script", sequence.getString(Tag.CodeMeaning));
            assertEquals("XNAT", sequence.getString(Tag.CodingSchemeDesignator));
            assertEquals("1.0", sequence.getString(Tag.CodingSchemeVersion));
        } catch (IOException e) {
            fail("Test setup failed: " + e);
        } catch (MizerException ae) {
            fail("Unexpected exception: " + ae);
        }
    }

    @Ignore
    @Test
    public void mizerServiceDE6UpdatesDicomObjectDeidentificationMethodCodeSequence() {
        try {
            getTestDicomObject();

            final MizerContextWithScript context = new MizerContextWithScript();
            context.setElement("project", "project");
            context.setScript("version \"6.1\"\nstudyDescription := project\n(0008,103e) := studyDescription\n");

//            service.anonymize(dicomObject, );
//            final DicomObjectI                  result        = mizer.anonymize(dicomObject, context, 1L);
//            final org.dcm4che2.data.DicomObject dcm4cheObject = result.getDcm4che2Object();
//            final DicomElement                  sequence      = dcm4cheObject.get(Tag.DeidentificationMethodCodeSequence);
//
//            assertEquals("project", dicomObject.getString(0x0008103e));

        } catch (IOException e) {
            fail("Test setup failed: " + e);
        } catch (MizerException ae) {
            fail("Unexpected exception: " + ae);
        }
    }

    @Test
    public void mizerServiceDE4UpdatesFileDeidentificationMethodCodeSequence() {
        try {
            final File anonTestFile = TestFileUtils.copyTestFileToTemp(TEST_FILE);
            service.anonymize(anonTestFile, PROJECT, SUBJECT, SESSION, true, false, 1L, SET_STD_ATTRS_DE4);

            final DicomObjectI dicomObject = DicomObjectFactory.newInstance(anonTestFile);

            assertEquals(PROJECT, dicomObject.getString(TAG_PROJECT));
            assertEquals(SUBJECT, dicomObject.getString(TAG_SUBJECT));
            assertEquals(SESSION, dicomObject.getString(TAG_SESSION));

            final org.dcm4che2.data.DicomObject dcm4cheObject = dicomObject.getDcm4che2Object();
            final DicomElement element = dcm4cheObject.get(Tag.DeidentificationMethodCodeSequence);

            assertNotNull(element);
            assertTrue(element instanceof SequenceDicomElement);
            assertEquals(1, element.countItems());

            final org.dcm4che2.data.DicomObject sequence = element.getDicomObject();
            assertNotNull(sequence);
            assertEquals("1", sequence.getString(Tag.CodeValue));
            assertEquals("XNAT DicomEdit 4 Script", sequence.getString(Tag.CodeMeaning));
            assertEquals("XNAT", sequence.getString(Tag.CodingSchemeDesignator));
            assertEquals("1.0", sequence.getString(Tag.CodingSchemeVersion));
        } catch (IOException e) {
            fail("Test setup failed: " + e);
        } catch (MizerException ae) {
            fail("Unexpected exception: " + ae);
        }
    }

    @Ignore
    @Test
    public void mizerServiceDE4UpdatesDicomObjectDeidentificationMethodCodeSequence() {
        try {
            final DicomObjectI dicomObject = DicomObjectFactory.newInstance(TEST_FILE);
            final MizerContextWithScript context = new MizerContextWithScript();

            context.setElement("project", "project");
            context.setScript("version \"6.1\"\nstudyDescription := project\n(0008,103e) := studyDescription\n");
        } catch (MizerException ae) {
            fail("Unexpected exception: " + ae);
        }
    }


    @Test
    public void testThreadedAnonymization() throws MizerException {
        for (int i=0; i<1; i++) {// set this to 10 or 100, what evern it takes to expose the issue in 1.8.10.0
            System.out.println("Starting testThreadedAnonymization");
            FileAnonThread thread = new FileAnonThread(i+"test",10);// set this to 1000 or something that runs long enough to expose issues
            thread.start();
        }
    }

    public class FileAnonThread extends Thread {
        private final String _customString;
        private final int _iterations;

        public FileAnonThread(String customString, Integer iterations) {
            _customString=customString;
            _iterations=iterations;
        }

        @Override
        public void run() {
            System.out.println("***************************************************");
            System.out.println("**     THREAD " + _customString + "              **");
            System.out.println("***************************************************");

            //prep files
            File dir = new File("/tmp/threadedAnon/"+_customString);
            if(dir.exists()) {
                try {
                    System.out.println("Deleting directory " + dir);
                    FileUtils.deleteDirectory(dir);
                } catch (Throwable e) {
                }
            }

            System.out.println("Creating directory " + dir);
            dir.mkdirs();

            for (int i=0; i<_iterations; i++) {
                File newFile = new File(dir,"DICOM_" + i + ".dcm");
                try {
                    System.out.println("Writing " + newFile);
                    FileUtils.copyFile(TEST_FILE,newFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //run actual anon test
            for (int i=0; i<_iterations; i++) {
                try {
                    File newFile = new File(dir,"DICOM_" + i + ".dcm");

                    // This may not adequately test our new code...
                    // I think I may habe been using Anonymizar or soething
                    MizerContextWithScript siteContext = new MizerContextWithScript();
                    siteContext.setScript("(0010,0030) := \"DE4 site script:" + _customString +i+"\"\n");
                    MizerContextWithScript projectContext = new MizerContextWithScript();
                    projectContext.setScript("version \"6.0\"\n(0010,0040) := \"DE6 project script:" + _customString +i+"\"\n");

                    System.out.println("Anonymizing " + newFile);
                    service.anonymize(newFile, new ArrayList<MizerContext>(Arrays.asList(siteContext, projectContext)));

                    DicomObjectI post_dobj = DicomObjectFactory.newInstance(newFile);
                    if(!("DE4 site script:" + _customString +i).equals(post_dobj.getString(0x00100030))){
                        System.out.println(String.format("ERROR: Expected {} but got {}","DE4 site script:" + _customString +i,post_dobj.getString(0x00100030) ));
                        fail(String.format("ERROR: Expected {} but got {}","DE4 site script:" + _customString +i,post_dobj.getString(0x00100030) ));
                    }else{
                        System.out.println("ANON WORKED");
                    }

                    if(!("DE6 project script:" + _customString +i).equals(post_dobj.getString(0x00100040))){
                        System.out.println(String.format("ERROR: Expected {} but got {}","DE6 proj(ect script:" + _customString +i,post_dobj.getString(0x00100040) ));
                        fail(String.format("ERROR: Expected {} but got {}","DE6 proj(ect script:" + _customString +i,post_dobj.getString(0x00100040) ));
                    }else{
                        System.out.println("ANON WORKED");
                    }

                    post_dobj=null;
                } catch (Throwable e) {
                    e.printStackTrace();
                    fail(e.getMessage());
                }
            }

            //clean up files
            for (int i=0; i<_iterations; i++) {
                System.out.println("Deleting file");
                File newFile = new File(dir,"DICOM_" + i + ".dcm");
                try {
                    FileUtils.forceDelete(newFile);
                } catch (IOException e) {}
            }
        }
    }

    @Test
    public void multiContextAnonTestHappyPath() {
        try {
            MizerContextWithScript siteContext = new MizerContextWithScript();
            siteContext.setScript("(0010,0030) := \"DE4 site script\"\n");
            MizerContextWithScript projectContext = new MizerContextWithScript();
            projectContext.setScript("version \"6.0\"\n(0010,0040) := \"DE6 project script\"\n");

            File anonTestFile = new File("/tmp/anonTestFile");
            FileUtils.copyFile(TEST_FILE, anonTestFile);

            DicomObjectI pre_dobj = DicomObjectFactory.newInstance(anonTestFile);
            assertTrue("19751231".equals(pre_dobj.getString(0x00100030)));
            assertTrue("F".equals(pre_dobj.getString(0x00100040)));

            service.anonymize(anonTestFile, new ArrayList<MizerContext>(Arrays.asList(siteContext, projectContext)));

            DicomObjectI post_dobj = DicomObjectFactory.newInstance(anonTestFile);
            assertTrue("DE4 site script".equals(post_dobj.getString(0x00100030)));
            assertTrue("DE6 project script".equals(post_dobj.getString(0x00100040)));

        } catch (Exception e) {
            fail("Unexpected exception " + e);
        }
    }

    @Test
    @Ignore
    public void rollbackOn2ndStepErrorTest() {
        File anonTestFile = new File("/tmp/anonTestFile");
        DicomObjectI post_dobj;
        try {
            MizerContextWithScript siteContext = new MizerContextWithScript();
            siteContext.setScript("(0010,0030) := \"DE4 site script\"\n");
            MizerContextWithScript projectContext = new MizerContextWithScript();
            //syntax error to induce anon error
            projectContext.setScript("version \"6.0\"\n(0010,00gg) := \"DE6 project script\"\n");

            FileUtils.copyFile(TEST_FILE, anonTestFile);

            DicomObjectI pre_dobj = DicomObjectFactory.newInstance(anonTestFile);
            assertTrue("19751231".equals(pre_dobj.getString(0x00100030)));
            assertTrue("F".equals(pre_dobj.getString(0x00100040)));

            service.anonymize(anonTestFile, new ArrayList<MizerContext>(Arrays.asList(siteContext, projectContext)));

            fail("servicce.anonymize should have thrown an error.");
        } catch (Exception e) {
            try {
                post_dobj = DicomObjectFactory.newInstance(anonTestFile);
                assertTrue("19751231".equals(post_dobj.getString(0x00100030)));
                assertTrue("F".equals(post_dobj.getString(0x00100040)));
            } catch (Exception ioe) {
                fail("Unexpected excpetion: " + ioe);
            }
        }
    }

    @Autowired
    private MizerService service;
}
