package org.nrg.xapi.rest.dicomweb;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DicomWebTestConfig.class)
public class TestSearchForSplitStudy {

    DicomWebSearchClient client = new DicomWebSearchClient("http://10.1.1.17/xapi/dicomweb");

    @Test
    public void byStudyInstanceUID() {

        Map<String, Properties> expectedResponses = new HashMap<>();

        String uid = "1.3.6.1.4.1.14519.5.2.1.3344.2526.135741059622478367178432395189";
        Properties p = new Properties();
        p.setProperty("Date", "19981012");
        p.setProperty("Time", "120227");
        p.setProperty("AccessionNumber", "9324247368316197");
        p.setProperty("InstanceAvailability", "ONLINE");
        p.setProperty("ModalitiesInStudy", "MR");
        p.setProperty("ReferringPhysicianName", "ignore");
        p.setProperty("TimeZoneOffset", "ignore");
        p.setProperty("RetrieveURL", "ignore");
        p.setProperty("PatientName", "LGG-660");
        p.setProperty("PatientID", "LGG-660");
        p.setProperty("PatientDOB", "1960");
        p.setProperty("PatientGender", "male");
        p.setProperty("StudyID", "333");
        p.setProperty("NumberOfSeries", "2");
        p.setProperty("NumberOfInstancesInStudy", "20");
        expectedResponses.put(uid, p);

        runTest("/studies",
                "StudyInstanceUID=1.3.6.1.4.1.14519.5.2.1.4334.1501.227933499470131058806289574760",
                "Basic YWRtaW46YWRtaW4=",
                expectedResponses);
    }

    public void runTest( String path, String params, String userAuthenticationHeaderValue, Map<String, Properties> expectedResponses) {
        try {

            JsonArray responses = client.doGetJson( path, params, userAuthenticationHeaderValue);
            assertEquals("Number of responses", expectedResponses.size(), responses.size());

            for (JsonValue responseValue : responses) {
                JsonStudyResponse response = new JsonStudyResponse((JsonObject) responseValue);

                assertTrue(expectedResponses.containsKey(response.studyInstanceUID));

                Properties expectedProperties = expectedResponses.get(response.studyInstanceUID);

                response.assertMatch(expectedProperties);
            }

        } catch (IOException e) {
            fail("Unexpected exception: " + e);
        }
    }
}


