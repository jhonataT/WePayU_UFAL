package br.ufal.ic.p2.wepayu.exceptions;

public class EmployeeException extends Exception {
    public static void employeeIdNotExists() throws NoSuchFieldException { throw new NoSuchFieldException("Identificacao do empregado nao pode ser nula."); }

    public static void employeeNotExists() throws ClassNotFoundException { throw new ClassNotFoundException("Empregado nao existe."); }

    public static void employeeNameNotExists() throws ClassNotFoundException { throw new ClassNotFoundException("Nao ha empregado com esse nome."); }

    public static void emptyEmployeeName() throws NoSuchFieldException { throw new NoSuchFieldException("Nome nao pode ser nulo."); }

    public static void emptyEmployeeAddress() throws NoSuchFieldException { throw new NoSuchFieldException("Endereco nao pode ser nulo."); }

    public static void emptyEmployeeRemuneration() throws NoSuchFieldException { throw new NoSuchFieldException("Salario nao pode ser nulo."); }

    public static void emptyEmployeeCommission() throws NoSuchFieldException { throw new NoSuchFieldException("Comissao nao pode ser nula."); }

    public static void negativeEmployeeRemuneration() throws ClassCastException { throw new ClassCastException("Salario deve ser nao-negativo."); }

    public static void negativeEmployeeCommission() throws ClassCastException { throw new ClassCastException("Comissao deve ser nao-negativa."); }

    public static void negativeEmployeeHours() throws ClassCastException { throw new ClassCastException("Horas devem ser positivas."); }

    public static void negativeValue() throws ClassCastException { throw new ClassCastException("Valor deve ser positivo."); }

    public static void wrongRemunerationType() throws ClassCastException  { throw new ClassCastException ("Salario deve ser numerico."); }

    public static void wrongCommissionType() throws ClassCastException  { throw new ClassCastException ("Comissao deve ser numerica."); }

    public static void invalidEmployeeType() throws IllegalArgumentException  { throw new IllegalArgumentException ("Tipo nao aplicavel."); }

    public static void invalidType() throws IllegalArgumentException  { throw new IllegalArgumentException ("Tipo invalido."); }

    public static void withoutBankAccount() throws NoSuchFieldException  { throw new NoSuchFieldException ("Empregado nao recebe em banco."); }

    public static void nonCommissioned() throws IllegalArgumentException  { throw new IllegalArgumentException ("Empregado nao eh comissionado."); }

    public static void nonHourly() throws IllegalArgumentException  { throw new IllegalArgumentException ("Empregado nao eh horista."); }

    public static void employeeIsNotUnionized() throws IllegalArgumentException  { throw new IllegalArgumentException ("Empregado nao eh sindicalizado."); }

    public static void propertyNotExists() throws NoSuchFieldException { throw new NoSuchFieldException ("Atributo nao existe."); }

    public static void isBankNameEmpty() throws NoSuchFieldException { throw new NoSuchFieldException ("Banco nao pode ser nulo."); }

    public static void isBankBranchEmpty() throws NoSuchFieldException { throw new NoSuchFieldException ("Agencia nao pode ser nulo."); }

    public static void isCurrentAccountEmpty() throws NoSuchFieldException { throw new NoSuchFieldException ("Conta corrente nao pode ser nulo."); }

    public static void invalidPaymentMethod() throws IllegalArgumentException { throw new IllegalArgumentException ("Metodo de pagamento invalido."); }

    public static void invalidBooleanFormat() throws ClassCastException { throw new ClassCastException("Valor deve ser true ou false."); }
}
