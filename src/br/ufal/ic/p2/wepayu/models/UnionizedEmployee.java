package br.ufal.ic.p2.wepayu.models;

import java.util.List;

public class UnionizedEmployee extends Employee {
    private String unionizedId;
    private double unionFee;
    public UnionizedEmployee(
        String employeeId,
        String name,
        String type,
        double remuneration,
        List<Sale> sales,
        String unionizedId,
        double unionFee,
        boolean unionized
    ) {
        super(employeeId, name, type, remuneration, sales, unionized);

        this.unionizedId = unionizedId;
        this.unionFee = unionFee;
    }

    public String getUnionizedId() {
        return this.unionizedId;
    }

    public double getValue() {
        return this.unionFee;
    }
}
