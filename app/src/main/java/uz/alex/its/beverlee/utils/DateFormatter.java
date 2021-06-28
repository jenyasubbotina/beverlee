package uz.alex.its.beverlee.utils;

import android.util.Log;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {
    public static String timestampToStringDate(final long timestamp) {
        final Date date = new Date(timestamp * 1000);
        return dateFormat.format(date);
    }

    public static String timestampToDayMonthDate(final long timestamp) {
        final Date date = new Date(timestamp * 1000);
        return dayMonthFormat.format(date);
    }

    public static int getLastDayOfMonthAndYear(final int year, final int monthNumber) {
        final LocalDateTime dateTime = LocalDateTime.of(year, monthNumber, 1, 0, 0);
        return dateTime.getMonth().length(Year.isLeap(year));
    }

    public static String dayMonthYearToStringDate(final int year, final int month, final int day) {
        return LocalDateTime.of(year, month, day, 0, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));
    }

    public static long dayMonthYearToTimestamp(final int year, final int monthNumber, final int day) {
        return Timestamp.valueOf(
                LocalDateTime.of(year, monthNumber, day, 0, 0)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH))).getTime();
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
    private static final SimpleDateFormat dayMonthFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
    private static final String TAG = DateFormatter.class.toString();
}
