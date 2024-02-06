package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;

public class EmployeeTimestamp {
    private String id;
    private LocalDate date;
    private double hours;

    public EmployeeTimestamp(String id, LocalDate date, double hours) {
        this.id = id;
        this.date = date;
        this.hours = hours;
    }

    public String getId() { return this.id; }

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
