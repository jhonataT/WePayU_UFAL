package br.ufal.ic.p2.wepayu.modules;

import br.ufal.ic.p2.wepayu.controllers.EmployeeController;
import br.ufal.ic.p2.wepayu.controllers.PayrollController;
import br.ufal.ic.p2.wepayu.controllers.SaleController;
import br.ufal.ic.p2.wepayu.controllers.SyndicateController;
import br.ufal.ic.p2.wepayu.exceptions.EmployeeException;
import br.ufal.ic.p2.wepayu.models.Employee;
import br.ufal.ic.p2.wepayu.models.PayrollEmployeeResponse;
import br.ufal.ic.p2.wepayu.utils.DateFormat;
import br.ufal.ic.p2.wepayu.utils.NumberFormat;
import br.ufal.ic.p2.wepayu.utils.XMLEmployeeManager;
import br.ufal.ic.p2.wepayu.utils.XMLSyndicateManager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class AppModule extends EmployeeController{
    private final XMLEmployeeManager xmlDatabase;
    private final XMLSyndicateManager xmlSyndicateDb;
    private final Map<LocalDate, Map<Employee, PayrollEmployeeResponse>> lastLocalPayroll;
    private final EmployeeController employeeController;
    private final SaleController saleController;
    private final SyndicateController syndicateController;
    private final PayrollController payrollController;

    public AppModule() throws Exception {
        super();
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

    public void resetSystem() throws Exception {
        this.employeeController.resetEmployees();
        this.syndicateController.resetSyndicates();
    }

    @Override()
    public String getEmployeeProperty(String employeeId, String property) throws Exception  {
        return this.employeeController.getEmployeeProperty(employeeId, property, this.syndicateController);
    }

    @Override()
    public String createEmployee(String name, String address, String type, String remuneration, String commission) throws NoSuchFieldException, ClassCastException, IllegalArgumentException {
        return this.employeeController.createEmployee(
            name,
            address,
            type,
            NumberFormat.replaceCommaToDot(remuneration),
            NumberFormat.replaceCommaToDot(commission)
        );
    }

    public int getWorkedHours(String employeeId, String startDate, String finishDate) throws Exception {
        return this.employeeController.getWorkedHours(
            this.employeeController.getEmployeeById(employeeId),
            DateFormat.dateVerify(startDate, "start", false),
            DateFormat.dateVerify(finishDate, "finish", true)
        );
    }

    public String getWorkedOvertime(String employeeId, String startDate, String finishDate) throws Exception {
        return this.employeeController.getWorkedOvertime(
            this.employeeController.getEmployeeById(employeeId),
            DateFormat.dateVerify(startDate, "start", true),
            DateFormat.dateVerify(finishDate, "finish", true)
        );
    }

    public void toLaunchTheCard(String employeeId, String date, String hours) throws Exception {
        this.employeeController.toLaunchTheCard(
            this.employeeController.getEmployeeById(employeeId),
            DateFormat.dateVerify(date, "", true),
            NumberFormat.stringToDouble(hours)
        );
    }

    public String createEmployee(String name, String address, String type, String remuneration) throws Exception {
        if(type.equals("comissionado")) throw new Exception("Tipo nao aplicavel.");

        return createEmployee(name, address, type, remuneration, "0");
    }

    public void saleLauncher(String employeeId, String date, String value) throws Exception {
        this.saleController.saleLauncher(
            this.employeeController.getEmployeeById(employeeId),
            DateFormat.dateVerify(date, "", true),
            NumberFormat.stringToDouble(value),
            this.employeeController
        );
    }

    public String getSalesMade(String employeeId, String startDate, String finishDate) throws Exception {
        return this.saleController.getSalesMade(
            this.employeeController.getEmployeeById(employeeId),
            DateFormat.dateVerify(startDate, "start", true),
            DateFormat.dateVerify(finishDate, "finish", true)
        );
    }

    public void updateEmployeeByType(String employeeId, String property, String value, String commission) throws Exception {
        this.employeeController.updateEmployeeByType(
            this.employeeController.getEmployeeById(employeeId),
            property,
            value,
            NumberFormat.stringToDouble(commission)
        );
    }

    public void updateEmployeeBankInfo(String employeeId, String property, String value, String bankName, String bankBranch, String currentAccount) throws Exception {
        this.employeeController.updateEmployeeBankInfo(
            this.employeeController.getEmployeeById(employeeId),
            property,
            value,
            bankName,
            bankBranch,
            currentAccount
        );
    }

    public void updateEmployeeProperty(String employeeId, String property, String value, String syndicateId, String unionFee) throws Exception {
        this.employeeController.updateEmployeeProperty(
            this.employeeController.getEmployeeById(employeeId),
            property,
            value,
            syndicateId,
            unionFee,
            this.syndicateController
        );
    }

    public void launchServiceFee(String syndicateId, String date, String value) throws Exception {
        this.syndicateController.launchServiceFee(
            this.syndicateController.getSyndicateById(syndicateId),
            DateFormat.dateVerify(date, "", true),
            NumberFormat.stringToDouble(value)
        );
    }

    public String getServiceFees(String employeeId, String startDate, String finishDate) throws Exception {
        return this.employeeController.getServiceFees(
            this.employeeController.getEmployeeById(employeeId),
            DateFormat.dateVerify(startDate, "start", true),
            DateFormat.dateVerify(finishDate, "finish", true),
            this.syndicateController
        );
    }

    public void updateEmployeeList(String employeeId, String property, String value) throws Exception {
        if(property.equals("sindicalizado")) {
            Employee employee = this.employeeController.getEmployeeById(employeeId);

            if(!value.equals("false") && !value.equals("true"))
                EmployeeException.invalidBooleanFormat();

            employee.setUnionized(Boolean.parseBoolean(value));
            this.employeeController.updateEmployeeList(employee);
        } else {
            this.updateEmployeeProperty(employeeId, property, value, "", "0");
        }
    }

    public void removeEmployee(String employeeId) throws Exception {
        this.employeeController.removeEmployee(this.employeeController.getEmployeeById(employeeId));
    }

    public String runPayrollAndGetTotal(String date) throws Exception {
        return this.payrollController.runPayrollAndGetTotal(
            DateFormat.dateVerify(date, "", true),
            this.lastLocalPayroll,
            this.employeeController,
            this.syndicateController
        );
    }

    public void savePayrollFile(String date, String output) throws IOException, Exception {
        this.payrollController.savePayrollFile(
            DateFormat.dateVerify(date, "", true),
            output,
            this.lastLocalPayroll,
            this.employeeController,
            this.syndicateController
        );
    }

    public void equalFiles(String file1, String file2) throws IOException {

    }

    public void turnOffSystem() throws Exception {
        this.syndicateController.saveSyndicateInDatabase(this.xmlSyndicateDb);
        this.employeeController.saveEmployeesInDatabase(this.xmlDatabase);
    }

}
