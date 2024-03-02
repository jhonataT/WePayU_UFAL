package br.ufal.ic.p2.wepayu.controllers;

import br.ufal.ic.p2.wepayu.exceptions.DateException;
import br.ufal.ic.p2.wepayu.exceptions.EmployeeException;
import br.ufal.ic.p2.wepayu.exceptions.SyndicateException;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.utils.NumberFormat;
import br.ufal.ic.p2.wepayu.utils.XMLEmployeeManager;

import java.time.LocalDate;
import java.util.*;

public abstract class EmployeeController {
    private static final String[] typeOptions = { "horista", "assalariado", "comissionado" };
    private static final String[] formOfPaymentOptions = { "banco", "correios", "emMaos" };
    private static final String[] employeeProperties = { "id", "nome", "endereco", "tipo", "salario", "sindicalizado", "comissao", "hours", "sales", "banco" };
    private static Map<String, Employee> employees;
    private static final EmployeeController instance = new EmployeeController() {
        @Override
        public String getEmployeeProperty(String employeeId, String property) throws Exception {
            return null;
        }
    };

    protected EmployeeController() {}

    public static EmployeeController getInstance() {
        return instance;
    }

    public void initializeEmployees(XMLEmployeeManager database) {
        employees = database.readAndGetEmployeeFile();
    }

    public void resetEmployees() { employees = new HashMap<>(); }

    public Map<String, Employee> getEmployees() { return employees; }

    public Employee getEmployeeById(String employeeId) throws ClassNotFoundException, NoSuchFieldException {
        if(employeeId.isEmpty()) EmployeeException.employeeIdNotExists();

        Employee employeeToReturn = employees.get(employeeId);

        if(employeeToReturn == null)
            EmployeeException.employeeNotExists();

        return employeeToReturn;
    }

    public void updateEmployeeList(Employee employee) {
        employees.put(employee.getId(), employee);
    }

    public String getEmployeesByName(String employeeName, int index) throws ClassNotFoundException {
        List<Employee> employeesToReturn = new ArrayList<>();

        for (Employee employee : employees.values()) {
            if (employee.getName().equals(employeeName)) {
                employeesToReturn.add(employee);
            }
        }

        if(!employeesToReturn.isEmpty() && index >= 0) {
            Employee employeeToReturn = employeesToReturn.get(index - 1);
            return employeeToReturn.getId();
        }

        EmployeeException.employeeNameNotExists();
        return "";
    }

    private static void validateEmployeeArguments(String name, String address, String type, String remuneration, String commission) throws NoSuchFieldException, ClassCastException, IllegalArgumentException {

        if(name.isEmpty()) EmployeeException.emptyEmployeeName();
        else if(address.isEmpty()) EmployeeException.emptyEmployeeAddress();
        else if(remuneration.isEmpty()) EmployeeException.emptyEmployeeRemuneration();
        else if(type.equals("comissionado") && commission.isEmpty()) EmployeeException.emptyEmployeeCommission();
        else if(!NumberFormat.isValueNumeric(remuneration)) EmployeeException.wrongRemunerationType();
        else if(!NumberFormat.isValueNumeric(commission)) EmployeeException.wrongCommissionType();

        double newRemuneration = NumberFormat.stringToDouble(remuneration);
        double newCommission = NumberFormat.stringToDouble(commission);

        if(Arrays.stream(typeOptions).noneMatch(item -> item.equals(type))) EmployeeException.invalidType();

        if(type.equals("horista") && newCommission != 0) EmployeeException.invalidEmployeeType();
        if(type.equals("assalariado") && newCommission != 0) EmployeeException.invalidEmployeeType();
        if(newCommission < 0) EmployeeException.negativeEmployeeCommission();
        if(newRemuneration < 0) EmployeeException.negativeEmployeeRemuneration();
    }

    public String createEmployee(String name, String address, String type, String remuneration, String commission) throws NoSuchFieldException, ClassCastException, IllegalArgumentException {
        validateEmployeeArguments(name, address, type, remuneration, commission);

        double newRemuneration = NumberFormat.stringToDouble(remuneration);
        double newCommission = NumberFormat.stringToDouble(commission);

        String newId = Integer.toString(employees.size() + 1);
        Employee employee = new Employee("id".concat(newId), name, address, type, newRemuneration, newCommission, false);
        employees.put(employee.getId(), employee);

        return employee.getId();
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee.getId());
    }

    private static String getEmployeeBankInfo(Employee employee, String property) throws NoSuchFieldException {
        EmployeeBank bankInfo = employee.getEmployeeBank();

        if(!employee.getFormOfPayment().equals("banco") || bankInfo == null) EmployeeException.withoutBankAccount();

        if(property.equals("banco")) return bankInfo.getBankName();
        if(property.equals("agencia")) return bankInfo.getBankBranch();
        else return bankInfo.getCurrentAccount();
    }

    private static String getEmployeeSyndicateInfo(Employee employee, String property, SyndicateController syndicateController) throws IllegalArgumentException, NoSuchFieldException, ClassNotFoundException {
        String employeeSyndicateId = employee.getLinkedSyndicateId();
        if(!employee.getUnionized()) EmployeeException.employeeIsNotUnionized();

        Syndicate syndicate = syndicateController.getSyndicateById(employeeSyndicateId);

        String value = NumberFormat.doubleToCommaFormat(syndicate.getEmployeeById(employee.getId()).getValue());

        return property.equals("idSindicato") ? employeeSyndicateId : value;
    }

    private static String getEmployeeCommission(Employee employee) throws IllegalArgumentException {
        if(!employee.getType().equals("comissionado")) EmployeeException.nonCommissioned();

        return NumberFormat.doubleToCommaFormat(employee.getCommission());
    }

    public String formatPaymentMethod(Employee employee) {
        if(employee.getFormOfPayment().equals("banco")) {
            return employee.getEmployeeBank().getBankName()+", "+"Ag. "+employee.getEmployeeBank().getBankBranch()+" CC "+employee.getEmployeeBank().getCurrentAccount();
        } else if(employee.getFormOfPayment().equals("emMaos")) {
            return "Em maos";
        } else {
            return "Correios, "+employee.getAddress();
        }
    }

    public String getEmployeeProperty(String employeeId, String property, SyndicateController syndicateController) throws NoSuchFieldException, ClassNotFoundException {
        Employee employee = this.getEmployeeById(employeeId);

        if(property.equals("nome")) return employee.getName();
        else if(property.equals("id")) return employee.getId();
        else if(property.equals("endereco")) return employee.getAddress();
        else if(property.equals("tipo")) return employee.getType();
        else if(property.equals("salario")) return NumberFormat.doubleToCommaFormat(employee.getRemuneration());
        else if(property.equals("sindicalizado")) return Boolean.toString(employee.getUnionized());
        else if (property.equals("metodoPagamento")) return employee.getFormOfPayment();
        else if(property.equals("banco") || property.equals("agencia") || property.equals("contaCorrente")) return getEmployeeBankInfo(employee, property);
        else if(property.equals("idSindicato") || property.equals("taxaSindical")) return getEmployeeSyndicateInfo(employee, property, syndicateController);
        else if(property.equals("comissao")) return getEmployeeCommission(employee);
        else EmployeeException.propertyNotExists();

        return employeeId;
    }

    public static Employee getEmployeeBySyndicate(String syndicateId) {
        for (Employee employee : employees.values()) {
            String linkedSyndicateId = employee.getLinkedSyndicateId();
            if (linkedSyndicateId != null && linkedSyndicateId.equals(syndicateId)) {
                return employee;
            }
        }

        return null;
    }

    public int getWorkedHours(Employee employee, LocalDate startDate, LocalDate finalDate) throws ClassNotFoundException {
        if(startDate.isAfter(finalDate)) DateException.invalidDateOrder();

        if(!employee.getType().equals("horista")) EmployeeException.nonHourly();

        List<Timestamp> employeeTimeStamp = employee.getTimestamp();

        if(employeeTimeStamp.isEmpty()) return 0;

        int totalHours = 0;

        for (Timestamp timestamp : employeeTimeStamp) {
            LocalDate date = timestamp.getDate();

            if(date.isAfter(startDate) || date.isEqual(startDate)) {
                if(date.isBefore(finalDate)) {
                    int validHour = (int) timestamp.getHours() > 8 ? 8 : (int) timestamp.getHours();

                    totalHours += validHour;
                }
            }
        }

        return totalHours;
    }

    public String getWorkedOvertime(Employee employee, LocalDate startDate, LocalDate finalDate) throws ClassNotFoundException {
        List<Timestamp> employeeTimeStampList = employee.getTimestamp();

        if(employeeTimeStampList == null || employeeTimeStampList.isEmpty()) return "0";

        double totalHours = 0;
        int workedDays = 0;

        for (Timestamp timestamp : employeeTimeStampList) {
            LocalDate date = timestamp.getDate();
            if(date.isAfter(startDate) || date.isEqual(startDate)) {
                if(date.isBefore(finalDate)) {
                    workedDays += 1;
                    totalHours += timestamp.getHours();
                }
            }
        }

        totalHours = totalHours >= (workedDays*8) ? totalHours - (8*workedDays) : 0;

        return totalHours > 0 ?
            Double.toString(totalHours).replace('.', ',').replace(",0", "")
            : "0";
    }

    public void toLaunchTheCard(Employee employee, LocalDate date, double hours) throws NoSuchFieldException, ClassNotFoundException {
        if(hours <= 0) EmployeeException.negativeEmployeeHours();

        if(!employee.getType().equals("horista")) EmployeeException.nonHourly();

        List<Timestamp> timestamps = employee.getTimestamp();

        String newId = employee.getId()+"_id_0";

        if(!timestamps.isEmpty()) {
            newId += timestamps.size();
        }

        employee.setLastPaymentDate(date);
        Timestamp newTimestamp = new Timestamp(newId, date, hours);
        employee.setTimestamp(newTimestamp);

        updateEmployeeList(employee);
    }

    public void updateEmployeeByType(Employee employee, String property, String value, double commission) throws NoSuchFieldException {
        if(property.equals("tipo")) {
            employee.setType(value);

            if(value.equals("comissionado")) employee.setCommission(commission);

            else if(value.equals("horista")) employee.setRemuneration(NumberFormat.doubleToInt(commission));
        } else {
            EmployeeException.propertyNotExists();
        }
    }

    public void updateEmployeeBankInfo(Employee employee, String property, String value, String bankName, String bankBranch, String currentAccount) throws NoSuchFieldException {
        if(property.equals("metodoPagamento") && value.equals("banco")) {
            if(bankName == null || bankName.isEmpty()) EmployeeException.isBankNameEmpty();
            if(bankBranch == null || bankBranch.isEmpty()) EmployeeException.isBankBranchEmpty();
            if(currentAccount == null || currentAccount.isEmpty()) EmployeeException.isCurrentAccountEmpty();

            employee.setFormOfPayment(value);
            employee.setEmployeeBank(bankName, bankBranch, currentAccount);
        }
    }

    private static void isInvalidTypeOption(String value) {
        if(Arrays.stream(typeOptions).noneMatch(item -> item.equals(value))) EmployeeException.invalidType();
    }

    private static void isInvalidFormOfPaymentOption(String value) {
        if(Arrays.stream(formOfPaymentOptions).noneMatch(item -> item.equals(value))) EmployeeException.invalidPaymentMethod();
    }

    public void updateEmployeeProperty(Employee employee, String property, String value, String syndicateId, String unionFee, SyndicateController syndicateController) throws ClassNotFoundException, NoSuchFieldException {
        if(property.equals("nome")) {
            if(value == null || value.isEmpty()) EmployeeException.emptyEmployeeName();
            employee.setName(value);
        } else if(property.equals("endereco")) {
            if(value == null || value.isEmpty()) EmployeeException.emptyEmployeeAddress();
            employee.setAddress(value);
        } else if(property.equals("tipo")) {
            isInvalidTypeOption(value);
            employee.setType(value);
        } else if(property.equals("metodoPagamento")) {
            isInvalidFormOfPaymentOption(value);
            employee.setFormOfPayment(value);
        } else if(property.equals("salario")) {
            if(value == null || value.isEmpty()) EmployeeException.emptyEmployeeRemuneration();
            else if(!NumberFormat.isValueNumeric(value)) EmployeeException.wrongRemunerationType();
            else if(NumberFormat.stringToDouble(value) <= 0) EmployeeException.negativeEmployeeRemuneration();

            employee.setRemuneration(Integer.parseInt(value));
        } else if(property.equals("comissao")) {
            if(!employee.getType().equals("comissionado")) EmployeeException.nonCommissioned();
            else if(value.isEmpty()) EmployeeException.emptyEmployeeCommission();
            else if(!NumberFormat.isValueNumeric(value)) EmployeeException.wrongCommissionType();
            else if(NumberFormat.stringToDouble(value) <= 0) EmployeeException.negativeEmployeeCommission();
            employee.setCommission(NumberFormat.stringToDouble(value));
        } else if(property.equals("sindicalizado")) {
            if(!value.equals("false") && !value.equals("true")) SyndicateException.invalidBooleanFormat();
            else if(syndicateId.isEmpty()) SyndicateException.syndicateIdNotExists();
            else if(unionFee.isEmpty()) SyndicateException.syndicateUnionFeeNotExists();
            else if(!NumberFormat.isValueNumeric(unionFee)) SyndicateException.notNumericUnionFee();
            else if(NumberFormat.stringToDouble(unionFee) <= 0) SyndicateException.negativeUnionFee();

            boolean isUnionized = Boolean.parseBoolean(value);
            employee.setUnionized(isUnionized);

            if(isUnionized) {
                Employee employeeWithSyndicate = EmployeeController.getEmployeeBySyndicate(syndicateId);
                Syndicate syndicate = syndicateController.getSyndicateById(syndicateId);

                if(employeeWithSyndicate != null && !employeeWithSyndicate.getId().isEmpty()) SyndicateException.employeeAlreadyExists();
                else if(syndicate == null) syndicate = new Syndicate(syndicateId);

                syndicateController.updateSyndicate(syndicate);

                UnionizedEmployee syndicateEmployee = syndicate.getEmployeeById(employee.getId());

                if(syndicateEmployee == null || syndicateEmployee.getId().isEmpty()) {
                    String newId = ""+(syndicate.getEmployees().isEmpty() ? 0 : syndicate.getEmployees().size());

                    syndicate.addNewEmployee(new UnionizedEmployee(
                        employee.getId(),
                        employee.getName(),
                        employee.getType(),
                        employee.getRemuneration(),
                        employee.getSales(),
                        "unionizedId_"+newId,
                        NumberFormat.stringToDouble(unionFee),
                        true
                    ));
                } else {
                    syndicateEmployee.setUnionFee(NumberFormat.stringToDouble(unionFee));
                }

                employee.setLinkedSyndicate(syndicate.getId());
                employee.setUnionFee(NumberFormat.stringToDouble(unionFee));
                updateEmployeeList(employee);
            }
        } else {
            EmployeeException.propertyNotExists();
        }
    }

    public String getServiceFees(Employee employee, LocalDate startDate, LocalDate finishDate, SyndicateController syndicateController) throws ClassNotFoundException, NoSuchFieldException {
        if(!employee.getUnionized()) EmployeeException.employeeIsNotUnionized();
        else if(startDate.isAfter(finishDate)) DateException.invalidDateOrder();
        else if(employee.getId().isEmpty()) SyndicateException.syndicateNotFound();

        String linkedSyndicateId = employee.getLinkedSyndicateId();

        if(linkedSyndicateId == null) return "0,00";

        Syndicate syndicate = syndicateController.getSyndicateById(linkedSyndicateId);

        double totalValue = 0;

        List<UnionFee> unionFeeList = syndicate.getUnionFeeList();

        if(unionFeeList.isEmpty()) return "0,00";

        for (UnionFee unionFee : unionFeeList) {
            LocalDate date = unionFee.getDate();

            if(date.isAfter(startDate) || date.isEqual(startDate)) {
                if(date.isBefore(finishDate)) {
                    totalValue += unionFee.getValue();
                }
            }
        }

        return NumberFormat.doubleToCommaFormat(totalValue);
    }

    public void saveEmployeesInDatabase(XMLEmployeeManager database) throws Exception {
        try {
            database.createAndSaveEmployeeDocument(employees);
        } catch(Exception e) {
            throw new Exception("Erro ao salvar EMPLOYEES_XML");
        }
    }

    public abstract String getEmployeeProperty(String employeeId, String property) throws Exception;
}
