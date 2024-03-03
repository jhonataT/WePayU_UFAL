package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;
import java.util.List;

public class UnionizedEmployee extends Employee {
    private String unionizedId;
    private double unionFee;
    private LocalDate date;
    public UnionizedEmployee(
        String employeeId,
        String name,
        String type,
        double remuneration,
        List<Sale> sales,
        String unionizedId,
        double unionFee,
        boolean unionized,
        LocalDate date
    ) {
        super(employeeId, name, type, remuneration, sales, unionized);

        this.unionizedId = unionizedId;
        this.unionFee = unionFee;
        this.date = date;
    }

    public String getUnionizedId() {
        return this.unionizedId;
    }

    public double getValue() {
        return this.unionFee;
    }

    public LocalDate getDate() {
        return this.date;
    }
}
