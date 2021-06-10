package org.nrg.xapi.rest.dicomweb;

import org.junit.Test;
import org.nrg.xapi.rest.dicomweb.search.InstanceFilter;
import org.nrg.xdat.bean.CatDcmentryBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestInstanceFilter {

    @Test
    public void testNoQueryParams() {
        InstanceFilter instanceFilter = new InstanceFilter();
        MultiValueMap<String, String> dicomRequestParams = new LinkedMultiValueMap<>();
        QueryParameters qp = new QueryParameters( dicomRequestParams);

        CatDcmentryBean entry = new CatDcmentryBean();
        entry.setInstancenumber( "1");
        entry.setUid( "1.222");

        assertTrue( instanceFilter.match( entry, qp));

        entry.setUid("1.2.3");
        assertTrue( instanceFilter.match( entry, qp));

        entry.setInstancenumber("2");
        assertTrue( instanceFilter.match( entry, qp));

        entry.setInstancenumber("1");
        assertTrue( instanceFilter.match( entry, qp));

    }

    @Test
    public void testOneQueryParam() {
        InstanceFilter instanceFilter = new InstanceFilter();
        MultiValueMap<String, String> dicomRequestParams = new LinkedMultiValueMap<>();
        dicomRequestParams.add( "sopInstanceUID", "1.2.3");
        QueryParameters qp = new QueryParameters( dicomRequestParams);

        CatDcmentryBean entry = new CatDcmentryBean();
        entry.setInstancenumber( "1");
        entry.setUid( "1.222");

        assertFalse( instanceFilter.match( entry, qp));

        entry.setUid("1.2.3");
        assertTrue( instanceFilter.match( entry, qp));

        entry.setInstancenumber("2");
        assertTrue( instanceFilter.match( entry, qp));

    }

    @Test
    public void testTwoQueryParam() {
        InstanceFilter instanceFilter = new InstanceFilter();
        MultiValueMap<String, String> dicomRequestParams = new LinkedMultiValueMap<>();
        dicomRequestParams.add( "sopInstanceUID", "1.2.3");
        dicomRequestParams.add( "instanceNumber", "1");
        QueryParameters qp = new QueryParameters( dicomRequestParams);

        CatDcmentryBean entry = new CatDcmentryBean();
        entry.setUid( "1.222");
        entry.setInstancenumber( "1");
        assertFalse( instanceFilter.match( entry, qp));

        entry.setUid("1.2.3");
        entry.setInstancenumber( "1");
        assertTrue( instanceFilter.match( entry, qp));

        entry.setUid("1.2.3");
        entry.setInstancenumber("2");
        assertFalse( instanceFilter.match( entry, qp));

        entry.setUid("1.222");
        entry.setInstancenumber("2");
        assertFalse( instanceFilter.match( entry, qp));

    }
}
