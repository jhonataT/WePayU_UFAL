package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;

public class EmployeeHistory {
    private String id;
    private String employeeId;
    private LocalDate date;
    private double hours;

    public EmployeeHistory(String id, String employeeId, LocalDate date, double hours) {
        this.id = id;
        this.employeeId = employeeId;
        this.date = date;
        this.hours = hours;
    }

    public String getId() { return this.id; }

    public void setId(String newId) {
        this.id = newId;
    }

    public void setEmployeeId(String newEmployeeId) {
        this.employeeId = newEmployeeId;
    }

    public String getEmployeeId() { return this.employeeId; }

    public void setDate(LocalDate newDate) {
        this.date = newDate;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public void setHours(Integer newHours) {
        this.hours = newHours;
    }

    public double getHours() { return this.hours; }

}
