package org.nrg.xapi.rest.dicomweb.search.xftItem;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.util.regex.Pattern;

/**
 * Its about time - tools to get DICOM Date/Time strings into Java and do something useful.
 *
 */
public class DateTimeService {

    /**
     * True if the two combined date-time ranges overlap.
     *
     * The date range and time range strings are combined into a single combined dateTime Interval. The interval will
     * have a duration of zero (begin and end Instant are equal) if the range strings encode single values.
     * A time interval may have a duration of zero, i.e. its begin and end times are equal.
     * interval1 and interval2 overlap if:
     *     - interval1 and interval2 are both exact dateTimes and are identical,
     *     - an exact interval falls into an interval (inclusive),
     *     - both intervals are ranges and the ranges overlap (inclusive).
     *
     * @param dicomDateRangeString1 DICOM formatted Date or Date-Range String.
     * @param dicomTimeRangeString1 DICOM formatted Time or Time-Range String.
     * @param dicomDateRangeString2 DICOM formatted Date or Date-Range String.
     * @param dicomTimeRangeString2 DICOM formatted Time or Time-Range String.
     * @return true if the two combined date-time ranges overlap.
     */
    public boolean hasOverlap( String dicomDateRangeString1, String dicomTimeRangeString1, String dicomDateRangeString2, String dicomTimeRangeString2, ZoneOffset zoneOffset) {
        Interval interval = parseDicomDateAndTimeRangeStrings( dicomDateRangeString2, dicomTimeRangeString2, zoneOffset);
        return hasOverlap( dicomDateRangeString1, dicomTimeRangeString1, zoneOffset, interval);
    }

    /**
     * True if the two combined date-time ranges overlap.
     *
     * This is useful if multiple tests need to be done against a single target interval.
     *
     * @param dicomDateString
     * @param dicomTimeString
     * @param targetInterval
     * @return
     */
    public boolean hasOverlap( String dicomDateString, String dicomTimeString, ZoneOffset zoneOffset, Interval targetInterval) {
        Interval i1 = parseDicomDateAndTimeRangeStrings( dicomDateString, dicomTimeString, zoneOffset);
        return targetInterval.hasOverlap( i1);
    }

    public boolean hasOverlap( Interval i1, Interval i2) {
        return i1.hasOverlap( i2);
    }

    /**
     * Create a combined Date-Time Interval from separate DICOM Date and Time values.
     *
     * @param dateRangeString
     * @param timeRangeString
     * @return
     */
    public Interval parseDicomDateAndTimeRangeStrings( String dateRangeString, String timeRangeString, ZoneOffset zoneOffset) {
        Interval time = parseDicomTimeRangeString( timeRangeString, zoneOffset);
        Interval date = parseDicomDateRangeString( dateRangeString, zoneOffset);
        return combinedDateTimeInterval( date, time);
    }

    /**
     * Combine separate Date and Time intervals into a a combined Date-Time interval.
     *
     * @param date
     * @param time
     * @return
     */
    private Interval combinedDateTimeInterval(Interval date, Interval time) {
        Interval i = new Interval( combinedDateTime( date.getBegin(), time.getBegin()), combinedDateTime( date.getEnd(), time.getEnd()));
        return i;
    }

    /**
     * Create OffsetDateTime with date from date and time from time.
     *
     * @param date
     * @param time
     * @return
     */
    private OffsetDateTime combinedDateTime( OffsetDateTime date, OffsetDateTime time) {
        return date.with( time.toLocalTime());
    }

    /**
     * Parse the DICOM formatted time or time-range into an Interval.
     *
     * @param timeString
     * @return
     */
    public Interval parseDicomTimeRangeString(String timeString, ZoneOffset zoneOffset) {
        String ts = timeString.trim();
        Interval interval;
        if( ts.contains("-")) {
            if( ts.startsWith("-")) {
                interval = new Interval( LocalTime.MIN, parseDicomTimeStringLocalTime( ts.substring(1)), zoneOffset);
            }
            else if( ts.endsWith("-")) {
                interval = new Interval( parseDicomTimeStringLocalTime( ts.substring(0, ts.length()-1)),LocalTime.MAX, zoneOffset);
            }
            else {
                String[] tokens = ts.split("-");
                interval = new Interval(parseDicomTimeStringLocalTime( tokens[0]), parseDicomTimeStringLocalTime( tokens[1]), zoneOffset);
            }
        }
        else {
            interval = new Interval( parseDicomTimeStringLocalTime( ts), zoneOffset);
        }
        return interval;
    }

    public Interval parseDicomDateRangeString(String dateRangeString, ZoneOffset zoneOffset) {
        String ts = dateRangeString.trim();
        Interval interval;
        if( ts.contains("-")) {
            if( ts.startsWith("-")) {
                interval = new Interval( LocalDate.MIN, parseDicomDateStringLocalDate( ts.substring(1)), zoneOffset);
            }
            else if( ts.endsWith("-")) {
                interval = new Interval( parseDicomDateStringLocalDate( ts.substring(0, ts.length()-1)), LocalDate.MAX, zoneOffset);
            }
            else {
                String[] tokens = ts.split("-");
                interval = new Interval( parseDicomDateString( tokens[0].trim(), zoneOffset), parseDicomDateString( tokens[1].trim(), zoneOffset));
            }
        }
        else {
            interval = new Interval( parseDicomDateString( ts, zoneOffset));
        }
        return interval;
    }

    /**
     * Parse DICOM DT VR formatted String into Local.
     *
     * @param dateString
     * @return
     */
    public OffsetDateTime parseDicomDateString( String dateString) {
        return parseDicomDateString( dateString, OffsetDateTime.now().getOffset());
    }

    public OffsetDateTime parseDicomDateString(String dateString, ZoneOffset zoneOffset) {
        LocalDate ld = parseDicomDateStringLocalDate( dateString);
        return ld.atStartOfDay().atOffset( zoneOffset);
    }

    private LocalDate parseDicomDateStringLocalDate(String dateString) {
        return LocalDate.parse( dateString, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    /**
     * Parse DICOM TM VR formatted String into OffsetDateTime with current date and zone offset.
     *
     * @param timeString
     * @return
     */
    public OffsetTime parseDicomTimeString(String timeString) {
        return parseDicomTimeStringLocalTime( timeString).atOffset( OffsetDateTime.now().getOffset());
    }

    public OffsetTime parseDicomTimeString(String timeString, ZoneOffset zoneOffset) {
        LocalTime lt = parseDicomTimeStringLocalTime( timeString);
        return lt.atOffset( zoneOffset);
    }

    private LocalTime parseDicomTimeStringLocalTime( String timeString) {
        return LocalTime.parse( normalizedTimeString( timeString), DateTimeFormatter.ofPattern("HHmmss.SSSSSSSSS"));
    }

    /**
     * Return Time String in format HHMMSS.FFFFFFFFF where fractional digits F are 9 in number.
     *
     * The DICOM Time VR is allowed to truncate MM, SS, or FFFFFF. Normalizing the string to a specific format
     * simplifies creating Java representations such as LocalTime.
     *
     * @param dicomTimeString
     * @return
     */
    public String normalizedTimeString( String dicomTimeString) {
        String ts = dicomTimeString.trim();
        String time = "";
        String fractionalSeconds = "";
        StringBuilder normalizedTimeString = new StringBuilder();
        if( ts.contains(".")) {
            String[] tokens = ts.split( Pattern.quote("."));
            time = tokens[0].trim();
            fractionalSeconds = tokens[1].trim();
        }
        else {
            time = ts;
        }
        normalizedTimeString.append( String.format("%1$-6s", time).replace(' ', '0'));
        if( fractionalSeconds.isEmpty()) {
            fractionalSeconds = "000000000";
        }
        else {
            fractionalSeconds = String.format("%1$-9.9s", fractionalSeconds).replace(' ', '0');
        }
        normalizedTimeString.append(".").append( fractionalSeconds);
        return normalizedTimeString.toString();
    }

    /**
     * Dicom Zone Offset
     * &ZZXX is an optional suffix for offset from Coordinated Universal Time (UTC), where & = "+" or "-", and ZZ = Hours and XX = Minutes of offset.
     *
     * @param dicomZoneOffsetString
     * @return
     */
    public ZoneOffset parseZoneOffsetString( String dicomZoneOffsetString) {
        int hrs = Integer.parseInt( dicomZoneOffsetString.substring(0,3));
        int mins = Integer.parseInt( dicomZoneOffsetString.substring(3,5));
        return ZoneOffset.ofHoursMinutes( hrs, mins);
    }

    public Instant parseDicomCombinedDateAndTimeStrings( String dateString, String timeString, String zoneOffsetString) {
        LocalDate ld = parseDicomDateStringLocalDate( dateString);
        LocalTime tl = parseDicomTimeStringLocalTime( timeString);
        ZoneOffset zo = parseZoneOffsetString( zoneOffsetString);
        return LocalDateTime.of( ld, tl).atOffset( zo).toInstant();
    }
}