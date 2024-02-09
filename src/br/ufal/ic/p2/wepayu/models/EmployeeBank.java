package br.ufal.ic.p2.wepayu.models;

public class EmployeeBank {
    private String bankName;
    private String bankBranch;
    public String currentAccount;

    public String getBankName() { return this.bankName; }

    public String getBankBranch() { return this.bankBranch; }

    public String getCurrentAccount() { return this.currentAccount; }

    public void setBankName(String newBankName) {
        this.bankName = newBankName;
    }

    public void setBankBranch(String newBankBranch) {
        this.bankBranch = newBankBranch;
    }

    public void setCurrentAccount(String newCurrentAccount) {
        this.currentAccount = newCurrentAccount;
    }
}
