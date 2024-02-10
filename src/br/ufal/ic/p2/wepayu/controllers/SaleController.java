package br.ufal.ic.p2.wepayu.controllers;

import br.ufal.ic.p2.wepayu.exceptions.DateException;
import br.ufal.ic.p2.wepayu.exceptions.EmployeeException;
import br.ufal.ic.p2.wepayu.models.Employee;
import br.ufal.ic.p2.wepayu.models.Sale;
import br.ufal.ic.p2.wepayu.utils.NumberFormat;

import java.time.LocalDate;
import java.util.List;

public class SaleController {

    public static void saleLauncher(Employee employee, LocalDate date, double value) {
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

        EmployeeController.updateEmployeeList(employee);
    }

    public static String getSalesMade(Employee employee, LocalDate startDate, LocalDate finishDate) {
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
