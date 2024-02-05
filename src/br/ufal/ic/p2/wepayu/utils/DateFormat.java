package br.ufal.ic.p2.wepayu.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;

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
}
