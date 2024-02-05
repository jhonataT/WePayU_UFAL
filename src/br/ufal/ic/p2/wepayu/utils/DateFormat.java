package br.ufal.ic.p2.wepayu.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class DateFormat {

    private static DateTimeFormatter getFormatter() {
        DateTimeFormatterBuilder df = new DateTimeFormatterBuilder();
        return df
            .optionalStart()
            .append(DateTimeFormatter.ofPattern("d/M/yyyy"))
            .optionalEnd()
            .optionalStart()
            .append(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            .optionalEnd()
            .toFormatter();
    }

    public static LocalDate stringToDate(String newDate) {
        return LocalDate.parse(newDate, getFormatter());
    }
}
