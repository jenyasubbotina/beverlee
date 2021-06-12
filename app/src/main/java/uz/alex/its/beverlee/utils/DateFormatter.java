package uz.alex.its.beverlee.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {
    public static String timestampToStringDate(final long timestamp) {
        final Date date = new Date(timestamp * 1000);
        return dateFormat.format(date);
    }

    public static long stringDateToTimestamp(final String dateStart) {
        try {
            return yearMonthDayFormat.parse(dateStart).getTime();
        }
        catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static String timestampToDayMonthDate(final long timestamp) {
        final Date date = new Date(timestamp * 1000);
        return dayMonthFormat.format(date);
    }

    public static String dateToYearMonthDay(final Date date) {
        return yearMonthDayFormat.format(date);
    }

    public static Date getFirstDayOfMonth(final int month) {
        final Calendar firstDayOfCurrentMonthCalendar = Calendar.getInstance();
        firstDayOfCurrentMonthCalendar.set(Calendar.MONTH, month);
        firstDayOfCurrentMonthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        firstDayOfCurrentMonthCalendar.set(Calendar.HOUR_OF_DAY, 0);
        firstDayOfCurrentMonthCalendar.set(Calendar.MINUTE, 0);
        firstDayOfCurrentMonthCalendar.set(Calendar.SECOND, 0);
        return firstDayOfCurrentMonthCalendar.getTime();
    }

    public static Date getLastDayOfMonth(final int month) {
        final Calendar lastDayOfCurrentMonthCalendar = Calendar.getInstance();
        lastDayOfCurrentMonthCalendar.set(Calendar.MONTH, month);
        //todo: check last day of month if 30/31 of february
        lastDayOfCurrentMonthCalendar.set(Calendar.DAY_OF_MONTH, lastDayOfCurrentMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        lastDayOfCurrentMonthCalendar.set(Calendar.HOUR_OF_DAY, 23);
        lastDayOfCurrentMonthCalendar.set(Calendar.MINUTE, 59);
        lastDayOfCurrentMonthCalendar.set(Calendar.SECOND, 59);
        return lastDayOfCurrentMonthCalendar.getTime();
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
    private static final SimpleDateFormat dayMonthFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
    private static final SimpleDateFormat yearMonthDayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private static final String TAG = DateFormatter.class.toString();
}
