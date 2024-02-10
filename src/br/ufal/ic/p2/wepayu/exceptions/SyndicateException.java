package br.ufal.ic.p2.wepayu.exceptions;

public class SyndicateException extends Exception {
    public static void emptySyndicateId() throws NoSuchFieldException { throw new NoSuchFieldException("Identificacao do membro nao pode ser nula."); }

    public static void syndicateNotFound() throws ClassNotFoundException { throw new ClassNotFoundException("Membro nao existe."); }

    public static void syndicateIdNotExists() throws NoSuchFieldException { throw new NoSuchFieldException("Identificacao do sindicato nao pode ser nula."); }

    public static void syndicateUnionFeeNotExists() throws NoSuchFieldException { throw new NoSuchFieldException("Taxa sindical nao pode ser nula."); }

    public static void invalidBooleanFormat() throws ClassCastException { throw new ClassCastException("Valor deve ser true ou false."); }

    public static void negativeValue() throws ClassCastException { throw new ClassCastException("Valor deve ser positivo."); }

    public static void notNumericUnionFee() throws ClassCastException { throw new ClassCastException("Taxa sindical deve ser numerica."); }

    public static void negativeUnionFee() throws ClassCastException { throw new ClassCastException("Taxa sindical deve ser nao-negativa."); }

    public static void employeeAlreadyExists() throws ClassCastException { throw new ClassCastException("Ha outro empregado com esta identificacao de sindicato"); }

}
