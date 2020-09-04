package org.nrg.xapi.rest.dicomweb;

import org.junit.Test;
import org.nrg.xapi.rest.dicomweb.search.xftItem.DateTimeService;
import org.nrg.xapi.rest.dicomweb.search.xftItem.Interval;

import java.time.*;

import static org.junit.Assert.*;

public class TestDateTimeService {

    @Test
    public void testNormalizedTimeString() {
        DateTimeService service = new DateTimeService();
        assertEquals( "000000.000000000", service.normalizedTimeString("00"));
        assertEquals( "012200.000000000", service.normalizedTimeString("0122"));
        assertEquals( "012233.000000000", service.normalizedTimeString("012233"));
        assertEquals( "012233.100000000", service.normalizedTimeString("012233.1"));
        assertEquals( "012233.120000000", service.normalizedTimeString("012233.12"));
        assertEquals( "012233.123000000", service.normalizedTimeString("012233.123"));
        assertEquals( "012233.123400000", service.normalizedTimeString("012233.1234"));
        assertEquals( "012233.123450000", service.normalizedTimeString("012233.12345"));
        assertEquals( "012233.123456000", service.normalizedTimeString("012233.123456"));
        assertEquals( "012233.123456700", service.normalizedTimeString("012233.1234567"));
        assertEquals( "012233.123456780", service.normalizedTimeString("012233.12345678"));
        assertEquals( "012233.123456789", service.normalizedTimeString("012233.123456789"));
        assertEquals( "012233.123456789", service.normalizedTimeString("012233.1234567899"));
    }

    @Test
    public void testParseDicomTimeString() {
        DateTimeService service = new DateTimeService();
        ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();
        assertEquals( OffsetTime.of( LocalTime.of(15,0), zoneOffset ), service.parseDicomTimeString("15", zoneOffset) );
        assertEquals( OffsetTime.of( LocalTime.of(1,22), zoneOffset ), service.parseDicomTimeString("0122", zoneOffset) );
        assertEquals( OffsetTime.of( LocalTime.of(1,22,33), zoneOffset ), service.parseDicomTimeString("012233", zoneOffset) );
        assertEquals( OffsetTime.of( LocalTime.of(1,22,33,100000000), zoneOffset ), service.parseDicomTimeString("012233.1", zoneOffset) );
        assertEquals( OffsetTime.of( LocalTime.of(1,22,33,120000000), zoneOffset ), service.parseDicomTimeString("012233.12", zoneOffset) );
        assertEquals( OffsetTime.of( LocalTime.of(1,22,33,123000000), zoneOffset ), service.parseDicomTimeString("012233.123", zoneOffset) );
        assertEquals( OffsetTime.of( LocalTime.of(1,22,33,123400000), zoneOffset ), service.parseDicomTimeString("012233.1234", zoneOffset) );
        assertEquals( OffsetTime.of( LocalTime.of(1,22,33,123450000), zoneOffset ), service.parseDicomTimeString("012233.12345", zoneOffset) );
        assertEquals( OffsetTime.of( LocalTime.of(1,22,33,123456000), zoneOffset ), service.parseDicomTimeString("012233.123456", zoneOffset) );
        assertEquals( OffsetTime.of( LocalTime.of(1,22,33,123456700), zoneOffset ), service.parseDicomTimeString("012233.1234567", zoneOffset) );
        assertEquals( OffsetTime.of( LocalTime.of(1,22,33,123456780), zoneOffset ), service.parseDicomTimeString("012233.12345678", zoneOffset) );
        assertEquals( OffsetTime.of( LocalTime.of(1,22,33,123456789), zoneOffset ), service.parseDicomTimeString("012233.123456789", zoneOffset) );
        assertEquals( OffsetTime.of( LocalTime.of(1,22,33,123456789), zoneOffset ), service.parseDicomTimeString("012233.1234567899", zoneOffset) );
    }

    @Test
    public void testParseDicomTimeRangeString() {
        DateTimeService service = new DateTimeService();
        ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();

        // exact time intervals
        Interval interval = new Interval( LocalTime.of(0,0,0,0), zoneOffset);
        assertEquals( interval, service.parseDicomTimeRangeString("00", zoneOffset));

        interval = new Interval( LocalTime.of(1,22,0,0), zoneOffset);
        assertEquals( interval, service.parseDicomTimeRangeString("0122", zoneOffset));

        interval = new Interval( LocalTime.of(1,22,33,0), zoneOffset);
        assertEquals( interval, service.parseDicomTimeRangeString("012233", zoneOffset));

        interval = new Interval( LocalTime.of(1,22,33,100000000), zoneOffset);
        assertEquals( interval, service.parseDicomTimeRangeString("012233.1", zoneOffset));

        interval = new Interval( LocalTime.of(1,22,33,120000000), zoneOffset);
        assertEquals( interval, service.parseDicomTimeRangeString("012233.12", zoneOffset));

        interval = new Interval( LocalTime.of(1,22,33,123000000), zoneOffset);
        assertEquals( interval, service.parseDicomTimeRangeString("012233.123", zoneOffset));

        interval = new Interval( LocalTime.of(1,22,33,123400000), zoneOffset);
        assertEquals( interval, service.parseDicomTimeRangeString("012233.1234", zoneOffset));

        interval = new Interval( LocalTime.of(1,22,33,123450000), zoneOffset);
        assertEquals( interval, service.parseDicomTimeRangeString("012233.12345", zoneOffset));

        interval = new Interval( LocalTime.of(1,22,33,123456000), zoneOffset);
        assertEquals( interval, service.parseDicomTimeRangeString("012233.123456", zoneOffset));

        interval = new Interval( LocalTime.of(1,22,33,123456700), zoneOffset);
        assertEquals( interval, service.parseDicomTimeRangeString("012233.1234567", zoneOffset));

        interval = new Interval( LocalTime.of(1,22,33,123456780), zoneOffset);
        assertEquals( interval, service.parseDicomTimeRangeString("012233.12345678", zoneOffset));

        interval = new Interval( LocalTime.of(1,22,33,123456789), zoneOffset);
        assertEquals( interval, service.parseDicomTimeRangeString("012233.123456789", zoneOffset));

        interval = new Interval( LocalTime.of(1,22,33,123456789), zoneOffset);
        assertEquals( interval, service.parseDicomTimeRangeString("012233.1234567899", zoneOffset));

        // closed-end time intervals
        interval = new Interval( LocalTime.of(0,0,0,0), LocalTime.of(01,02,03,0), zoneOffset);
        assertEquals( interval, service.parseDicomTimeRangeString("00-010203", zoneOffset));
        assertEquals( interval, service.parseDicomTimeRangeString(" 00 - 010203 ", zoneOffset));

        // open-begin time intervals
        interval = new Interval( LocalTime.MIN, LocalTime.of(01,02,03,0), zoneOffset);
        assertEquals( interval, service.parseDicomTimeRangeString("-010203", zoneOffset));
        assertEquals( interval, service.parseDicomTimeRangeString(" - 010203 ", zoneOffset));

        // open-end time intervals
        interval = new Interval( LocalTime.of(01,02,03,0), LocalTime.MAX, zoneOffset);
        assertEquals( interval, service.parseDicomTimeRangeString("010203-", zoneOffset));
        assertEquals( interval, service.parseDicomTimeRangeString(" 010203 - ", zoneOffset));
    }

    @Test
    public void testOverlap() {
        DateTimeService service = new DateTimeService();
        ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();

        // exact date, exact time
        // i1 before i2
        assertFalse( service.hasOverlap( "20191225", "003000", "20191225", "033000", zoneOffset));
        // i1 after i2
        assertFalse( service.hasOverlap( "20191225", "103000", "20191225", "033000", zoneOffset));
        // i1 end = i2 begin
        assertTrue( service.hasOverlap( "20191225", "103000", "20191225", "103000", zoneOffset));

        // exact date, time range
        // i1 before i2
        assertFalse( service.hasOverlap( "20191225", "003000-013000", "20191225", "033000-043000", zoneOffset));
        // i1 after i2
        assertFalse( service.hasOverlap( "20191225", "103000-113000", "20191225", "033000-043000", zoneOffset));
        // i1 end = i2 begin
        assertTrue( service.hasOverlap( "20191225", "103000-113000", "20191225", "113000-143000", zoneOffset));
        // i1 end > i2 begin
        assertTrue( service.hasOverlap( "20191225", "103000-123000", "20191225", "113000-143000", zoneOffset));
        // i1 end = i2 end
        assertTrue( service.hasOverlap( "20191225", "103000-143000", "20191225", "113000-143000", zoneOffset));
        // i1 end > i2 end (i2 inside i1)
        assertTrue( service.hasOverlap( "20191225", "103000-163000", "20191225", "113000-143000", zoneOffset));
        // i1 end > i2 end (i1 inside i2)
        assertTrue( service.hasOverlap( "20191225", "113000-143000", "20191225", "103000-163000", zoneOffset));

        // date range, exact time
        // i1 before i2
        assertFalse( service.hasOverlap( "20191224 - 20191225", "063000", "20191225 - 20191226", "073000", zoneOffset));
        // i1 after i2
        assertFalse( service.hasOverlap( "20191225 - 20191226", "103000", "20191224 - 20191225", "033000", zoneOffset));
        // i1 end = i2 begin
        assertTrue( service.hasOverlap( "20191225 - 20191226", "103000", "20191226 - 20191227", "103000", zoneOffset));
        // i1 end > i2 begin
        assertTrue( service.hasOverlap( "20191225 - 20191226", "233000", "20191226 - 20191227", "113000", zoneOffset));
        // i1 end = i2 end
        assertTrue( service.hasOverlap( "20191225 - 20191226", "103000", "20191224 - 20191226", "103000", zoneOffset));
        // i1 end > i2 end (i2 inside i1)
        assertTrue( service.hasOverlap( "20191224 - 20191227", "103000", "20191225 - 20191226", "113000", zoneOffset));
        // i1 end > i2 end (i1 inside i2)
        assertTrue( service.hasOverlap( "20191225 - 20191226", "113000", "20191224 - 20191227", "103000", zoneOffset));
    }
}
