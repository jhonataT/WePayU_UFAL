package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;

public class UnionFee {
    private String id;
    private LocalDate date;
    private double value;

    public UnionFee(String id, LocalDate date, double value) {
        this.id = id;
        this.date = date;
        this.value = value;
    }

    public LocalDate getDate() { return this.date; }

    public String getId() { return this.id; }

    public double getValue() { return this.value; }

}
