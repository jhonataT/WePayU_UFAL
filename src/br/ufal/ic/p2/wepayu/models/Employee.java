package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.utils.DateFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Employee {
    private final String id;
    private String name;
    private String address;
    private String type;
    private double remuneration;
    private double commission;
    private boolean unionized;
    private final List<Sale> saleList;
    private List<Timestamp> timestamp;
    private String linkedSyndicateId;
    private double unionFee;
    private String formOfPayment;
    private EmployeeBank bankInfo;
    private LocalDate lastPayment;

    public Employee(String id, String name, String address, String type, double remuneration, double commission, boolean unionized) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.type = type;
        this.remuneration = remuneration;
        this.commission = commission;
        this.unionized = unionized;
        this.saleList = new ArrayList<>();
        this.timestamp = new ArrayList<>();
        this.formOfPayment = "emMaos";
        this.bankInfo = new EmployeeBank();
        this.lastPayment = DateFormat.stringToDate("1/1/2005", false);
        this.unionFee = 0;
    }

    public Employee(String id, String name, String type, double remuneration, List<Sale> sales, boolean unionized) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.remuneration = remuneration;
        this.saleList = sales;
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

    public void setUnionized(boolean newValue) {
        this.unionized = newValue;
    }

    public void setLinkedSyndicate(String newSyndicateId) {
        this.linkedSyndicateId = newSyndicateId;
    }

    public String getLinkedSyndicateId() { return this.linkedSyndicateId; }

    public void setUnionFee(double newUnionFee) {
        this.unionFee = newUnionFee;
    }

    public double getUnionFee() { return this.unionFee; }

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

    public void setType(String newType) {
        this.type = newType;
    }

    public double getRemuneration() {
        return this.remuneration;
    }

    public LocalDate getLastPayment() {
        return this.lastPayment;
    }

    public void setLastPaymentDate(LocalDate newLastPayment) {
        this.lastPayment = newLastPayment;
    }

    public void setCommission(double newCommission) {
        this.commission = newCommission;
    }

    public double getCommission() {
        return this.commission;
    }

    public void setRemuneration(int newRemuneration) {
        this.remuneration = newRemuneration;
    }

    public List<Sale> getSales() {
        return this.saleList;
    }

    public void setFormOfPayment(String newFormOfPayment) {
        this.formOfPayment = newFormOfPayment;
    }

    public String getFormOfPayment() { return this.formOfPayment; }

    public List<Timestamp> getTimestamp() {
        return this.timestamp;
    }

    public void setSale(Sale newSale) {
        this.saleList.add(newSale);
    }

    public EmployeeBank getEmployeeBank() {
        return this.bankInfo;
    }

    public void setEmployeeBank(String bankName, String bankBranch, String currentAccount) {
        this.bankInfo.setBankName(bankName);
        this.bankInfo.setBankBranch(bankBranch);
        this.bankInfo.setCurrentAccount(currentAccount);
    }

    public void setTimestamp(Timestamp newTimeStamp) {
        this.timestamp.add(newTimeStamp);
    }

}
