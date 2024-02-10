package br.ufal.ic.p2.wepayu.models;

public class PayrollEmployeeResponse {
    private Employee employee;
    private int hours;
    private int extraHours;
    private double remuneration;
    private double discounts;
    private String paymentMethod;
    private double sales;
    private double fixedRemuneration;
    private double commission;

    public PayrollEmployeeResponse(
        Employee employee,
        int hours,
        int extraHours,
        double remuneration,
        double discounts,
        String paymentMethod,
        double sales,
        double fixedRemuneration,
        double commission
    ) {
        this.employee = employee;
        this.hours = hours;
        this.extraHours = extraHours;
        this.remuneration = remuneration;
        this.discounts = discounts;
        this.paymentMethod = paymentMethod;
        this.sales = sales;
        this.fixedRemuneration = fixedRemuneration;
        this.commission = commission;
    }

    public Employee getEmployee() { return this.employee; }

    public int getHours() { return this.hours; }

    public int getExtraHours() { return this.extraHours; }

    public double getRemuneration() { return this.remuneration; }

    public double getDiscounts() { return this.discounts; }

    public String getPaymentMethod() { return this.paymentMethod; }

    public double getFixedRemuneration() { return this.fixedRemuneration; }

    public double getCommission() { return this.commission; }

    public double getTotalSales() { return this.sales; }
}
