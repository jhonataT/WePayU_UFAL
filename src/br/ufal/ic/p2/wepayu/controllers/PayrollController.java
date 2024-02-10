package br.ufal.ic.p2.wepayu.controllers;

import br.ufal.ic.p2.wepayu.models.Employee;
import br.ufal.ic.p2.wepayu.models.PayrollEmployeeResponse;
import br.ufal.ic.p2.wepayu.models.Sale;
import br.ufal.ic.p2.wepayu.models.Timestamp;
import br.ufal.ic.p2.wepayu.utils.DateFormat;

import java.time.LocalDate;
import java.util.List;

public class PayrollController {
    public static PayrollEmployeeResponse getCommissionedPayrollDetails(Employee employee, LocalDate date, double discounts) {
        LocalDate lastPaymentDate = employee.getLastPayment();

        double totalHours = 0;
        double totalOvertime = 0;
        double totalPayment = 0;
        double currentValue = 0;
        double currentValueFromSales = 0;

        if(lastPaymentDate == null || DateFormat.getDifferenceInDays(date, lastPaymentDate) >= 15) {
            long dayToDecrease = lastPaymentDate == null ? 15 : DateFormat.getDifferenceInDays(date, lastPaymentDate);

            List<Sale> saleList = employee.getSales();
            LocalDate formattedStartDate = date.minusDays(dayToDecrease);

            currentValue += (employee.getRemuneration() * 12 / 52) - discounts;

            double commission = employee.getCommission();

            if(!saleList.isEmpty()) {
                for (Sale sale : saleList) {
                    LocalDate saleDate = sale.getDate();

                    if(formattedStartDate.isBefore(saleDate) || formattedStartDate.isEqual(saleDate)) {
                        if(date.isAfter(saleDate) || date.isEqual(saleDate)) {
                            currentValueFromSales += sale.getValue() * commission;
                        }
                    }
                }
            }

            currentValue += currentValueFromSales;

            totalPayment += (currentValue);

            employee.setLastPaymentDate(date);
            EmployeeController.updateEmployeeList(employee);
        }

        String paymentMethod = employee.getFormOfPayment();

        return new PayrollEmployeeResponse(
            employee,
            totalHours,
            totalOvertime,
            totalPayment,
            discounts,
            paymentMethod,
            currentValueFromSales
        );
    }

    public static PayrollEmployeeResponse getSalariedPayrollDetails(Employee employee, LocalDate date, double discounts) {
        LocalDate lastPaymentDate = employee.getLastPayment();

        double totalPayment = 0;

        if(lastPaymentDate == null || DateFormat.getDifferenceInDays(date, lastPaymentDate) >= 30) {
            totalPayment += employee.getRemuneration();

            employee.setLastPaymentDate(date);
            EmployeeController.updateEmployeeList(employee);
        }

        String paymentMethod = employee.getFormOfPayment();

        return new PayrollEmployeeResponse(
            employee,
            0,
            0,
            totalPayment,
            discounts,
            paymentMethod,
            0
        );
    }

    public static PayrollEmployeeResponse getHourlyPayrollDetails(Employee employee, LocalDate date, double discounts) {
        LocalDate lastPaymentDate = employee.getLastPayment();

        double totalHours = 0;
        double totalOvertime = 0;
        double totalPayment = 0;
        double currentValue = 0;

        if(lastPaymentDate == null || DateFormat.getDifferenceInDays(date, lastPaymentDate) >= 7) {
            long dayToDecrease = lastPaymentDate == null ? 7 : DateFormat.getDifferenceInDays(date, lastPaymentDate);

            List<Timestamp> timestamps = employee.getTimestamp();
            LocalDate formattedStartDate = date.minusDays(dayToDecrease);

            for (Timestamp timestamp : timestamps) {
                LocalDate timestampDate = timestamp.getDate();

                if(timestampDate.isBefore(date)) {
                    if(timestampDate.isAfter(formattedStartDate)) {
                        totalHours += timestamp.getHours() > 8 ? 8 : timestamp.getHours();
                        totalOvertime += timestamp.getHours() > 8 ? timestamp.getHours() - 8 : 0;
                    }
                }
            }

            if(totalOvertime > 0) {
                totalPayment += (totalOvertime * (employee.getRemuneration() * 1.5));
            }

            currentValue = (totalHours * employee.getRemuneration());

            if(currentValue < 0) currentValue = 0;

            totalPayment += (currentValue);

            employee.setLastPaymentDate(date);
            EmployeeController.updateEmployeeList(employee);
        }

        String paymentMethod = employee.getFormOfPayment();

        return new PayrollEmployeeResponse(
            employee,
            totalHours,
            totalOvertime,
            totalPayment,
            discounts,
            paymentMethod,
            0
        );
    }


}
