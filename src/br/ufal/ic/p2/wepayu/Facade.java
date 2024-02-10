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
    private final XMLEmployeeManager xmlDatabase;
    private final XMLSyndicateManager xmlSyndicateDb;
    private Map<LocalDate, Map<Employee, PayrollEmployeeResponse>> lastLocalPayroll;
    private final EmployeeController employeeController;
    private final SaleController saleController;
    private final SyndicateController syndicateController;
    private final PayrollController payrollController;

    public Facade() throws Exception {
        this.lastLocalPayroll = new HashMap<>();

        this.xmlDatabase = new XMLEmployeeManager("employees");
        this.xmlSyndicateDb = new XMLSyndicateManager("syndicates");

        this.employeeController = EmployeeController.getInstance();
        this.employeeController.initializeEmployees(this.xmlDatabase);

        this.saleController = SaleController.getInstance();

        this.payrollController = PayrollController.getInstance();

        this.syndicateController = SyndicateController.getInstance();
        this.syndicateController.initializeSyndicates(this.xmlSyndicateDb, this.employeeController);
    }

    public void zerarSistema() throws Exception {
        this.employeeController.resetEmployees();
        this.syndicateController.resetSyndicates();
    }

    public String getAtributoEmpregado(String employeeId, String property) throws Exception  {
        return this.employeeController.getEmployeeProperty(employeeId, property, this.syndicateController);
    }

    public String getEmpregadoPorNome(String employeeName, int index) throws Exception {
        return this.employeeController.getEmployeesByName(employeeName, index);
    }

    public String criarEmpregado(String name, String address, String type, String remuneration, String commission) throws Exception {
        return this.employeeController.createEmployee(
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
        return this.employeeController.getWorkedHours(
            this.employeeController.getEmployeeById(employeeId),
            this.dateVerify(startDate, "start", false),
            this.dateVerify(finishDate, "finish", true)
        );
    }

    public String getHorasExtrasTrabalhadas(String employeeId, String startDate, String finishDate) throws Exception {
        return this.employeeController.getWorkedOvertime(
            this.employeeController.getEmployeeById(employeeId),
            this.dateVerify(startDate, "start", true),
            this.dateVerify(finishDate, "finish", true)
        );
    }

    public void lancaCartao(String employeeId, String date, String hours) throws Exception {
        this.employeeController.toLaunchTheCard(
            this.employeeController.getEmployeeById(employeeId),
            this.dateVerify(date, "", true),
            NumberFormat.stringToDouble(hours)
        );
    }

    public String criarEmpregado(String name, String address, String type, String remuneration) throws Exception {
        if(type.equals("comissionado")) throw new Exception("Tipo nao aplicavel.");

        return criarEmpregado(name, address, type, remuneration, "0");
    }

    public void lancaVenda(String employeeId, String date, String value) throws Exception {
        this.saleController.saleLauncher(
            this.employeeController.getEmployeeById(employeeId),
            this.dateVerify(date, "", true),
            NumberFormat.stringToDouble(value),
            this.employeeController
        );
    }

    public String getVendasRealizadas(String employeeId, String startDate, String finishDate) throws Exception {
        return this.saleController.getSalesMade(
            this.employeeController.getEmployeeById(employeeId),
            this.dateVerify(startDate, "start", true),
            this.dateVerify(finishDate, "finish", true)
        );
    }

    public void alteraEmpregado(String employeeId, String property, String value, String commission) throws Exception {
        this.employeeController.updateEmployeeByType(
           this.employeeController.getEmployeeById(employeeId),
           property,
           value,
           NumberFormat.stringToDouble(commission)
       );
    }

    public void alteraEmpregado(String employeeId, String property, String value, String bankName, String bankBranch, String currentAccount) throws Exception {
       this.employeeController.updateEmployeeBankInfo(
           this.employeeController.getEmployeeById(employeeId),
           property,
           value,
           bankName,
           bankBranch,
           currentAccount
       );
    }

    public void alteraEmpregado(String employeeId, String property, String value, String syndicateId, String unionFee) throws Exception {
        this.employeeController.updateEmployeeProperty(
           this.employeeController.getEmployeeById(employeeId),
           property,
           value,
           syndicateId,
           unionFee,
           this.syndicateController
        );
    }

    public void lancaTaxaServico(String syndicateId, String date, String value) throws Exception {
        this.syndicateController.launchServiceFee(
            this.syndicateController.getSyndicateById(syndicateId),
            this.dateVerify(date, "", true),
            NumberFormat.stringToDouble(value)
        );
    }

    public String getTaxasServico(String employeeId, String startDate, String finishDate) throws Exception {
        return this.employeeController.getServiceFees(
            this.employeeController.getEmployeeById(employeeId),
            this.dateVerify(startDate, "start", true),
            this.dateVerify(finishDate, "finish", true),
            this.syndicateController
        );
    }

    public void alteraEmpregado(String employeeId, String property, String value) throws Exception {
        if(property.equals("sindicalizado")) {
            Employee employee = this.employeeController.getEmployeeById(employeeId);

            if(!value.equals("false") && !value.equals("true"))
                EmployeeException.invalidBooleanFormat();

            employee.setUnionized(Boolean.parseBoolean(value));
            this.employeeController.updateEmployeeList(employee);
        } else {
            this.alteraEmpregado(employeeId, property, value, "", "0");
        }
    }

    public void removerEmpregado(String employeeId) throws Exception {
        this.employeeController.removeEmployee(this.employeeController.getEmployeeById(employeeId));
    }

    public String totalFolha(String date) throws Exception {
        return this.payrollController.runPayrollAndGetTotal(
            this.dateVerify(date, "", true),
            this.lastLocalPayroll,
            this.employeeController,
            this.syndicateController
        );
    }

    public void rodaFolha(String date, String output) throws IOException, Exception {
        this.payrollController.savePayrollFile(
            this.dateVerify(date, "", true),
            output,
            this.lastLocalPayroll,
            this.employeeController,
            this.syndicateController
        );
    }

    public void equalFiles(String file1, String file2) throws IOException {

    }

    public void encerrarSistema() throws Exception {
        this.syndicateController.saveSyndicateInDatabase(this.xmlSyndicateDb);
        this.employeeController.saveEmployeesInDatabase(this.xmlDatabase);
    }
}
