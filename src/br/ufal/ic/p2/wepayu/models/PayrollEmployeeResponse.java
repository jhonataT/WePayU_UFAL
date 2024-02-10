package br.ufal.ic.p2.wepayu.models;

public class PayrollEmployeeResponse {
    private Employee employee;
    private double hours;
    private double extraHours;
    private double remuneration;
    private double discounts;
    private String paymentMethod;
    private double sales;

    public PayrollEmployeeResponse(
        Employee employee,
        double hours,
        double extraHours,
        double remuneration,
        double discounts,
        String paymentMethod,
        double sales
    ) {
        this.employee = employee;
        this.hours = hours;
        this.extraHours = extraHours;
        this.remuneration = remuneration;
        this.discounts = discounts;
        this.paymentMethod = paymentMethod;
        this.sales = sales;
    }

    public Employee getEmployeeName() { return this.employee; }

    public double getHours() { return this.hours; }

    public double getExtraHours() { return this.extraHours; }

    public double getRemuneration() { return this.remuneration; }

    public double getDiscounts() { return this.discounts; }

    public String getPaymentMethod() { return this.paymentMethod; }
}
