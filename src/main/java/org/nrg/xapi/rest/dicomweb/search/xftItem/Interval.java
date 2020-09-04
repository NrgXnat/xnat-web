package org.nrg.xapi.rest.dicomweb.search.xftItem;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * A class for specific time interval.
 *
 * An Interval may have a duration of zero, i.e. it is a single time point.
 * Constructor parameters without date are given current date.
 * Constructor parameters without zone offset are given local offset.
 *
 */
public class Interval {
    private OffsetDateTime begin, end;

    public Interval( LocalTime t1, LocalTime t2, ZoneOffset zoneOffset) {
        this( LocalDateTime.now().with(t1).atOffset(zoneOffset), LocalDateTime.now().with(t2).atOffset(zoneOffset));
    }

    public Interval( LocalTime t1, ZoneOffset zoneOffset) {
        this( LocalDateTime.now().with(t1).atOffset(zoneOffset));
    }

    public Interval( LocalDate d1, LocalDate d2, ZoneOffset zoneOffset) {
        this( LocalDateTime.now().with(d1).atOffset(zoneOffset), LocalDateTime.now().with(d2).atOffset(zoneOffset));
    }

    public Interval( LocalDate d1, LocalTime t1, LocalDate d2, LocalTime t2, ZoneOffset zoneOffset) {
        this( LocalDateTime.of( d1, t1).atOffset(zoneOffset), LocalDateTime.of( d2, t2).atOffset(zoneOffset));
    }

    public Interval( OffsetDateTime t1, OffsetDateTime t2) {
        // Do not reorder the interval. The begin time can be after the end time for times in combined date-time matching.
//        this.begin = t1.isBefore(t2)? t1: t2;
//        this.end = t1.isBefore(t2)? t2: t1;
        this.begin = t1;
        this.end = t2;
    }

    public Interval( OffsetDateTime t) {
        this.begin = t;
        this.end = t;
    }

    public OffsetDateTime getBegin() { return begin;}
    public OffsetDateTime getEnd() { return end;}

    public boolean hasOverlap( Interval other) {
        return !end.isBefore( other.getBegin()) && !begin.isAfter( other.getEnd());
    }

    private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern( "yyyyMMdd");
    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern( "HHmmss.SSSSSSSSS");

    private final static ZoneOffset defaultZoneOffset = OffsetDateTime.now().getOffset();
    private final static OffsetDateTime minOffsetDateTimeSQL = OffsetDateTime.of( 1000,1,1,0,0,0,0,defaultZoneOffset);
    private final static OffsetDateTime maxOffsetDateTimeSQL = OffsetDateTime.of( 9999,1,1,0,0,0,0,defaultZoneOffset);

    private OffsetDateTime restrictRange( OffsetDateTime odt) {
        OffsetDateTime rodt = odt;
        if( odt.isBefore( minOffsetDateTimeSQL)) {
            rodt = minOffsetDateTimeSQL;
        }
        else if( odt.isAfter( maxOffsetDateTimeSQL)) {
            rodt = maxOffsetDateTimeSQL;
        }
        return rodt;
    }

    public String getEndDateString() {
        return dateFormatter.format( restrictRange( end));
    }

    public String getStartDateString() {
        return dateFormatter.format( restrictRange( begin));
    }

    public String getStartTimeString() {
        return timeFormatter.format( begin);
    }
    public String getEndTimeString() {
        return timeFormatter.format( end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Interval interval = (Interval) o;
        return Objects.equals(begin, interval.getBegin()) &&
                Objects.equals(end, interval.getEnd());
    }

    @Override
    public int hashCode() {
        return Objects.hash(begin, end);
    }

    public boolean isZeroDuration() {
        return begin.equals(end);
    }

    public boolean isOpenStartDate() {
        return OffsetDateTime.MIN.equals( begin.with( OffsetTime.MIN));
    }

    public boolean isOpenEndDate() {
        return OffsetDateTime.MAX.equals( end.with( OffsetTime.MIN));
    }

    public boolean isOpenStartTime() {
        return OffsetTime.MIN.equals( begin.toOffsetTime());
    }

    public boolean isOpenEndTime() {
        return OffsetTime.MAX.equals( end.toOffsetTime());
    }
}
