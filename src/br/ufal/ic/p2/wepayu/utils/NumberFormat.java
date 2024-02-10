package br.ufal.ic.p2.wepayu.utils;

public class NumberFormat {
    public static boolean isValueNumeric(String str) {
        try {
            Double.parseDouble(replaceCommaToDot(str));
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public static double stringToDouble(String value) {
        return Double.parseDouble(replaceCommaToDot(value));
    }

    public static String doubleToString(double value) {
        return Double.toString(value);
    }

    public static String replaceCommaToDot(String value) { return value.replace(',', '.'); }

    public static String doubleToCommaFormat(double value) {
        return String.format("%.2f", value).replace('.', ',');
    }

    public static Integer doubleToInt(double value) {
        return (int) Math.round(value);
    }
}
