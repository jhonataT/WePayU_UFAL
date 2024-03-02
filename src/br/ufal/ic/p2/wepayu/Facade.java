package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.controllers.EmployeeController;
import br.ufal.ic.p2.wepayu.controllers.PayrollController;
import br.ufal.ic.p2.wepayu.controllers.SaleController;
import br.ufal.ic.p2.wepayu.controllers.SyndicateController;
import br.ufal.ic.p2.wepayu.exceptions.DateException;
import br.ufal.ic.p2.wepayu.exceptions.EmployeeException;
import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.modules.AppModule;
import br.ufal.ic.p2.wepayu.utils.*;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class Facade {
    private AppModule appModule = new AppModule();

    public Facade() throws Exception {}

    public void zerarSistema() throws Exception {
        this.appModule.resetSystem();
    }

    public String getAtributoEmpregado(String employeeId, String property) throws Exception  {
        return this.appModule.getEmployeeProperty(employeeId, property);
    }

    public String getEmpregadoPorNome(String employeeName, int index) throws Exception {
        return this.appModule.getEmployeesByName(employeeName, index);
    }

    public String criarEmpregado(String name, String address, String type, String remuneration, String commission) throws Exception {
        return this.appModule.createEmployee(name, address, type, remuneration, commission);
    }

    public int getHorasNormaisTrabalhadas(String employeeId, String startDate, String finishDate) throws Exception {
        return this.appModule.getWorkedHours(employeeId, startDate, finishDate);
    }

    public String getHorasExtrasTrabalhadas(String employeeId, String startDate, String finishDate) throws Exception {
        return this.appModule.getWorkedOvertime(employeeId, startDate, finishDate);
    }

    public void lancaCartao(String employeeId, String date, String hours) throws Exception {
        this.appModule.toLaunchTheCard(employeeId, date, hours);
    }

    public String criarEmpregado(String name, String address, String type, String remuneration) throws Exception {
        return this.appModule.createEmployee(name, address, type, remuneration);
    }

    public void lancaVenda(String employeeId, String date, String value) throws Exception {
        this.appModule.saleLauncher(employeeId, date, value);
    }

    public String getVendasRealizadas(String employeeId, String startDate, String finishDate) throws Exception {
        return this.appModule.getSalesMade(employeeId, startDate, finishDate);
    }

    public void alteraEmpregado(String employeeId, String property, String value, String commission) throws Exception {
        this.appModule.updateEmployeeByType(employeeId, property, value, commission);
    }

    public void alteraEmpregado(String employeeId, String property, String value, String bankName, String bankBranch, String currentAccount) throws Exception {
       this.appModule.updateEmployeeBankInfo(employeeId, property, value, bankName, bankBranch, currentAccount);
    }

    public void alteraEmpregado(String employeeId, String property, String value, String syndicateId, String unionFee) throws Exception {
        this.appModule.updateEmployeeProperty(employeeId, property, value, syndicateId, unionFee);
    }

    public void lancaTaxaServico(String syndicateId, String date, String value) throws Exception {
        this.appModule.launchServiceFee(syndicateId, date, value);
    }

    public String getTaxasServico(String employeeId, String startDate, String finishDate) throws Exception {
        return this.appModule.getServiceFees(employeeId, startDate, finishDate);
    }

    public void alteraEmpregado(String employeeId, String property, String value) throws Exception {
        this.appModule.updateEmployeeList(employeeId, property, value);
    }

    public void removerEmpregado(String employeeId) throws Exception {
        this.appModule.removeEmployee(employeeId);
    }

    public String totalFolha(String date) throws Exception {
        return this.appModule.runPayrollAndGetTotal(date);
    }

    public void rodaFolha(String date, String output) throws IOException, Exception {
        this.appModule.savePayrollFile(date, output);
    }

    public void equalFiles(String file1, String file2) throws IOException {

    }

    public void encerrarSistema() throws Exception {
        this.appModule.turnOffSystem();
    }
}
