package br.ufal.ic.p2.wepayu.models;

import java.util.ArrayList;
import java.util.List;

public class Syndicate {
    private String id;
    private List<UnionizedEmployee> employeeList;
    private List<UnionFee> unionFeeList;

    public Syndicate(String id) {
        this.id = id;
        this.employeeList = new ArrayList<>();
    }

    public void addNewEmployee(UnionizedEmployee newEmployee) {
        this.employeeList.add(newEmployee);
    }

    public void addNewUnionFee(UnionFee newUnionFee) {
        this.unionFeeList.add(newUnionFee);
    }

    public List<UnionFee> getUnionFeeList() {
        return this.unionFeeList;
    }

    public List<UnionizedEmployee> getEmployees() {
        return this.employeeList;
    }

    public UnionizedEmployee getEmployeeById(String employeeId) {
        List<UnionizedEmployee> filteredEmployees = this.employeeList.stream().filter(item -> item.getId().equals(employeeId)).toList();

        if(filteredEmployees.isEmpty()) {
            return null;
        }

        return filteredEmployees.getFirst();
    }

    public String getId() { return this.id; }
}
