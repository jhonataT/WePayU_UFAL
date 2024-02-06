package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;

public class Sale {
    private String id;
    private LocalDate date;
    private double value;

    public Sale(String id, LocalDate date, double value) {
        this.id = id;
        this.date = date;
        this.value = value;
    }

    public String getId() { return this.id; }

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
