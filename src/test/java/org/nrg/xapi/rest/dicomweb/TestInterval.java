package org.nrg.xapi.rest.dicomweb;

import org.junit.Test;
import org.nrg.xapi.rest.dicomweb.search.xftItem.Interval;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

import static org.junit.Assert.assertEquals;

public class TestInterval {

    @Test
    public void testString() {
        Interval interval = new Interval(LocalDate.now(), LocalTime.now(), LocalDate.now(), LocalTime.now(), OffsetDateTime.now().getOffset());

        String date = interval.getEndDateString();
        String time = interval.getEndTimeString();
        assertEquals( "foo", "foo");
    }

    @Test
    public void testMinDate() {
        Interval interval = new Interval(LocalDate.MIN, LocalTime.now(), LocalDate.now(), LocalTime.now(), OffsetDateTime.now().getOffset());

        String date = interval.getEndDateString();
        String time = interval.getEndTimeString();
        assertEquals( "foo", "foo");
    }

}
