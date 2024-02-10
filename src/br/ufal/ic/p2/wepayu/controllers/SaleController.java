package br.ufal.ic.p2.wepayu.controllers;

import br.ufal.ic.p2.wepayu.exceptions.DateException;
import br.ufal.ic.p2.wepayu.exceptions.EmployeeException;
import br.ufal.ic.p2.wepayu.models.Employee;
import br.ufal.ic.p2.wepayu.models.Sale;
import br.ufal.ic.p2.wepayu.utils.NumberFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class SaleController {
    private static Map<String, Employee> employees;
    private static final SaleController instance = new SaleController();

    private SaleController() {}

    public static SaleController getInstance() {
        return instance;
    }

    public void saleLauncher(Employee employee, LocalDate date, double value, EmployeeController employeeController) {
        if(!employee.getType().equals("comissionado"))
            EmployeeException.nonCommissioned();

        List<Sale> sales = employee.getSales();

        String newId = employee.getId()+"_saleId_0";

        if(!sales.isEmpty()) {
            newId += sales.size();
        }

        if(value <= 0)
            EmployeeException.negativeValue();

        Sale newSale = new Sale(newId, date, value);

        employee.setSale(newSale);

        employeeController.updateEmployeeList(employee);
    }

    public String getSalesMade(Employee employee, LocalDate startDate, LocalDate finishDate) {
        if(startDate.isAfter(finishDate)) DateException.invalidDateOrder();

        if(!employee.getType().equals("comissionado"))
           EmployeeException.nonCommissioned();

        List<Sale> employeeSales = employee.getSales();

        if(employeeSales.isEmpty()) return "0,00";

        double totalHours = 0;

        for (Sale sale : employeeSales) {
            LocalDate date = sale.getDate();

            if(date.isAfter(startDate) || date.isEqual(startDate)) {
                if(date.isBefore(finishDate)) {
                    totalHours += sale.getValue();
                }
            }
        }

        return NumberFormat.doubleToCommaFormat(totalHours);
    }
}
