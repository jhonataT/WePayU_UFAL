package br.ufal.ic.p2.wepayu.models;

import java.util.Arrays;

public class Employee {
    private final String id;

    private String name;
    private String address;
    private String type;
    private double remuneration;
    private double commission;
    private boolean unionized;

    public Employee(String id, String name, String address, String type, double remuneration, double commission, boolean unionized) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.type = type;
        this.remuneration = remuneration;
        this.commission = commission;
        this.unionized = unionized;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public Boolean getUnionized()  {
        return this.unionized;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String newAddress) {
        this.address = newAddress;
    }

    public String getType() {
        return this.type;
    }

    public void setTypo(String newType) {
        this.type = newType;
    }

    public double getRemuneration() {
        return this.remuneration;
    }

    public double getCommission() {
        return this.commission;
    }

    public void setRemuneration(int newRemuneration) {
        this.remuneration = newRemuneration;
    }

}
