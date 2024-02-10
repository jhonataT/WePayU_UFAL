package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.controllers.EmployeeController;
import br.ufal.ic.p2.wepayu.controllers.PayrollController;
import br.ufal.ic.p2.wepayu.controllers.SaleController;
import br.ufal.ic.p2.wepayu.controllers.SyndicateController;
import br.ufal.ic.p2.wepayu.exceptions.DateException;
import br.ufal.ic.p2.wepayu.exceptions.EmployeeException;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.utils.*;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class Facade {
    private final Map<String, TxtFileManager> payrollList;
    private final XMLEmployeeManager xmlDatabase;
    private final XMLSyndicateManager xmlSyndicateDb;

    public Facade() throws Exception {
        this.xmlDatabase = new XMLEmployeeManager("employees");
        this.xmlSyndicateDb = new XMLSyndicateManager("syndicates");

        EmployeeController.initializeEmployees(this.xmlDatabase);
        SyndicateController.initializeSyndicates(this.xmlSyndicateDb);

        this.payrollList = new HashMap<>();
    }

    public void zerarSistema() throws Exception {
        EmployeeController.resetEmployees();
        SyndicateController.resetSyndicates();
    }

    public String getAtributoEmpregado(String employeeId, String property) throws Exception  {
        return EmployeeController.getEmployeeProperty(employeeId, property);
    }

    public String getEmpregadoPorNome(String employeeName, int index) throws Exception {
        return EmployeeController.getEmployeesByName(employeeName, index);
    }

    public String criarEmpregado(String name, String address, String type, String remuneration, String commission) throws Exception {
        return EmployeeController.createEmployee(
            name,
            address,
            type,
            NumberFormat.replaceCommaToDot(remuneration),
            NumberFormat.replaceCommaToDot(commission)
        );
    }

    private LocalDate dateVerify(String newDate, String type, boolean isStrict) throws ClassCastException {
        LocalDate date = null;

        try {
            date = DateFormat.stringToDate(newDate, isStrict);
        } catch(Exception e) {
            if(type.equals("start")) DateException.invalidStartDate();
            else if(type.equals("finish")) DateException.invalidFinalDate();
            else DateException.invalidDate();
        }

        return date;
    }

    public int getHorasNormaisTrabalhadas(String employeeId, String startDate, String finishDate) throws Exception {
        return EmployeeController.getWorkedHours(
            EmployeeController.getEmployeeById(employeeId),
            this.dateVerify(startDate, "start", false),
            this.dateVerify(finishDate, "finish", true)
        );
    }

    public String getHorasExtrasTrabalhadas(String employeeId, String startDate, String finishDate) throws Exception {
        return EmployeeController.getWorkedOvertime(
            EmployeeController.getEmployeeById(employeeId),
            this.dateVerify(startDate, "start", true),
            this.dateVerify(finishDate, "finish", true)
        );
    }

    public void lancaCartao(String employeeId, String date, String hours) throws Exception {
        EmployeeController.toLaunchTheCard(
            EmployeeController.getEmployeeById(employeeId),
            this.dateVerify(date, "", true),
            NumberFormat.stringToDouble(hours)
        );
    }

    public String criarEmpregado(String name, String address, String type, String remuneration) throws Exception {
        if(type.equals("comissionado")) throw new Exception("Tipo nao aplicavel.");

        return criarEmpregado(name, address, type, remuneration, "0");
    }

    public void lancaVenda(String employeeId, String date, String value) throws Exception {
        SaleController.saleLauncher(
            EmployeeController.getEmployeeById(employeeId),
            this.dateVerify(date, "", true),
            NumberFormat.stringToDouble(value)
        );
    }

    public String getVendasRealizadas(String employeeId, String startDate, String finishDate) throws Exception {
        return SaleController.getSalesMade(
            EmployeeController.getEmployeeById(employeeId),
            this.dateVerify(startDate, "start", true),
            this.dateVerify(finishDate, "finish", true)
        );
    }

    public void alteraEmpregado(String employeeId, String property, String value, String commission) throws Exception {
       EmployeeController.updateEmployeeByType(
           EmployeeController.getEmployeeById(employeeId),
           property,
           value,
           NumberFormat.stringToDouble(commission)
       );
    }

    public void alteraEmpregado(String employeeId, String property, String value, String bankName, String bankBranch, String currentAccount) throws Exception {
       EmployeeController.updateEmployeeBankInfo(
           EmployeeController.getEmployeeById(employeeId),
           property,
           value,
           bankName,
           bankBranch,
           currentAccount
       );
    }

    public void alteraEmpregado(String employeeId, String property, String value, String syndicateId, String unionFee) throws Exception {
        EmployeeController.updateEmployeeProperty(
           EmployeeController.getEmployeeById(employeeId),
           property,
           value,
           syndicateId,
           unionFee
        );
    }

    public void lancaTaxaServico(String syndicateId, String date, String value) throws Exception {
        SyndicateController.launchServiceFee(
            SyndicateController.getSyndicateById(syndicateId),
            this.dateVerify(date, "", true),
            NumberFormat.stringToDouble(value)
        );
    }

    public String getTaxasServico(String employeeId, String startDate, String finishDate) throws Exception {
        return EmployeeController.getServiceFees(
            EmployeeController.getEmployeeById(employeeId),
            this.dateVerify(startDate, "start", true),
            this.dateVerify(finishDate, "finish", true)
        );
    }

    public void alteraEmpregado(String employeeId, String property, String value) throws Exception {
        if(property.equals("sindicalizado")) {
            Employee employee = EmployeeController.getEmployeeById(employeeId);

            if(!value.equals("false") && !value.equals("true"))
                EmployeeException.invalidBooleanFormat();

            employee.setUnionized(Boolean.parseBoolean(value));
            EmployeeController.updateEmployeeList(employee);
        } else {
            this.alteraEmpregado(employeeId, property, value, "", "0");
        }
    }

    public void removerEmpregado(String employeeId) throws Exception {
        EmployeeController.removeEmployee(EmployeeController.getEmployeeById(employeeId));
    }

    public String totalFolha(String date) throws Exception {
        LocalDate formattedFinishDate = this.dateVerify(date, "", true);

        double totalPayment = 0;

        for(Employee employee : EmployeeController.getEmployees().values()) {
            boolean isUnionized = employee.getUnionized();
            double discount = 0;

            if(isUnionized) {
                discount += (employee.getUnionFee() * (employee.getType().equals("horista") ? 7 : employee.getType().equals("comissionado") ? 15 : 30));
                String syndicateId = employee.getLinkedSyndicateId();

                Syndicate syndicate = SyndicateController.getSyndicateById(syndicateId);

                UnionizedEmployee unionizedEmployee = syndicate.getEmployeeById(employee.getId());
                discount += (unionizedEmployee.getValue()  * (employee.getType().equals("horista") ? 7 : employee.getType().equals("comissionado") ? 15 : 30));
            }

            PayrollEmployeeResponse payrollEmployeeResponse = null;

            if(employee.getType().equals("horista") && DateFormat.isFriday(formattedFinishDate)) {
                payrollEmployeeResponse = PayrollController.getHourlyPayrollDetails(employee, formattedFinishDate, discount);
            } else if(employee.getType().equals("comissionado") && DateFormat.isFriday(formattedFinishDate)) {
                payrollEmployeeResponse = PayrollController.getCommissionedPayrollDetails(employee, formattedFinishDate, discount);
            } else if(employee.getType().equals("assalariado") && DateFormat.isLastWorkingDayOfMonth(formattedFinishDate)) {
                payrollEmployeeResponse = PayrollController.getSalariedPayrollDetails(employee, formattedFinishDate, discount);
            }

            if(payrollEmployeeResponse != null) {
                totalPayment += payrollEmployeeResponse.getRemuneration() - payrollEmployeeResponse.getDiscounts();
            }
        }

        return String.format("%.2f", totalPayment).replace('.', ',');
    }

    public void rodaFolha(String date, String output) throws IOException, Exception {
        String totalPayment = totalFolha(date);

        TxtFileManager newTxt = new TxtFileManager(output);
        newTxt.addingContent(totalPayment);

        this.payrollList.put(output, newTxt);
    }

    public void equalFiles(String file1, String file2) throws IOException {
        System.out.println("\n\nfile1 -> " + file1);
        System.out.println("file2 -> " + file2 + "\n\n");

        TxtFileManager filteredFile = this.payrollList.get(file2);

        System.out.println("\n\nARQUIVO ENCONTRADO -> " + filteredFile);

        if(filteredFile != null) {
            String validatedPayroll = TxtFileManager.getContent(file1);
            String newPayroll = filteredFile.getContent();

            System.out.println("\n\nvalidatedPayroll == newPayroll -> " + validatedPayroll == newPayroll);
        }
    }

    public void encerrarSistema() throws Exception {
        SyndicateController.saveSyndicateInDatabase(this.xmlSyndicateDb);
        EmployeeController.saveEmployeesInDatabase(this.xmlDatabase);
    }
}
