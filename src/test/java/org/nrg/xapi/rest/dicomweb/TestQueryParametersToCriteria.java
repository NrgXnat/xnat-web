package org.nrg.xapi.rest.dicomweb;

import org.junit.Test;
import org.nrg.xapi.rest.dicomweb.search.xftItem.QueryParametersToCriteria;

import static junit.framework.TestCase.assertEquals;

public class TestQueryParametersToCriteria {

    @Test
    public void testNormalizedTimeString() {
        assertEquals( "010000", QueryParametersToCriteria.normalizedTimeString( "01"));
        assertEquals( "010000", QueryParametersToCriteria.normalizedTimeString( "0100"));
        assertEquals( "010000", QueryParametersToCriteria.normalizedTimeString( "010000"));
        assertEquals( "010000.0", QueryParametersToCriteria.normalizedTimeString( "010000.0"));
    }
}
