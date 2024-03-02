package br.ufal.ic.p2.wepayu.utils;

import br.ufal.ic.p2.wepayu.exceptions.DateException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;

public class DateFormat {

    private static DateTimeFormatter getFormatter(boolean isStrict) {
        DateTimeFormatterBuilder df = new DateTimeFormatterBuilder();

        DateTimeFormatter newDf = df
            .optionalStart()
            .append(DateTimeFormatter.ofPattern("d/M/uuuu"))
            .optionalEnd()
            .optionalStart()
            .append(DateTimeFormatter.ofPattern("uuuu-MM-dd"))
            .optionalEnd()
            .toFormatter();

        if(isStrict) {
            newDf = newDf.withResolverStyle(ResolverStyle.STRICT);
        }

        return newDf;
    }

    public static LocalDate stringToDate(String newDate, boolean isStrict) {
        return LocalDate.parse(newDate, getFormatter(isStrict));
    }

    public static boolean isLastWorkingDayOfMonth(LocalDate date) {
        int lastDayOfMonth = date.lengthOfMonth();
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        return date.getDayOfMonth() == lastDayOfMonth && dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

    public static boolean isFriday(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        return dayOfWeek == DayOfWeek.FRIDAY;
    }

    public static long getDifferenceInDays(LocalDate date, LocalDate dateToCompare) {
        return ChronoUnit.DAYS.between(date, dateToCompare);
    }

    public static LocalDate dateVerify(String newDate, String type, boolean isStrict) throws ClassCastException {
        LocalDate date = null;

        try {
            date = DateFormat.stringToDate(newDate, isStrict);
        } catch(Exception e) {
            if(type.equals("start")) DateException.invalidStartDate();
            else if(type.equals("finish")) DateException.invalidFinalDate();
            else DateException.invalidDate();
        }

        return date;
    }
}
