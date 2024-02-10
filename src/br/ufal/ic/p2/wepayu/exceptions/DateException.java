package br.ufal.ic.p2.wepayu.exceptions;

public class DateException extends Exception {
    public static void invalidStartDate() throws ClassCastException  { throw new ClassCastException ("Data inicial invalida."); }

    public static void invalidFinalDate() throws ClassCastException  { throw new ClassCastException ("Data final invalida."); }

    public static void invalidDate() throws ClassCastException  { throw new ClassCastException ("Data invalida."); }

    public static void invalidDateOrder() throws ClassCastException  { throw new ClassCastException ("Data inicial nao pode ser posterior aa data final."); }
}
