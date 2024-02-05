package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;

public class EmployeeSales {
    private String id;
    private String employeeId;
    private LocalDate date;
    private double value;

    public EmployeeSales(String id, String employeeId, LocalDate date, double value) {
        this.id = id;
        this.employeeId = employeeId;
        this.date = date;
        this.value = value;
    }

    public String getId() { return this.id; }

    public String getEmployeeId() { return this.employeeId; }

    public LocalDate getDate() {
        return this.date;
    }

    public void setDate(LocalDate newDate) {
        this.date = newDate;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double newValue) {
        this.value = newValue;
    }
}
