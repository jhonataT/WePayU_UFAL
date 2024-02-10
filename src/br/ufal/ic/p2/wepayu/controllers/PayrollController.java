package br.ufal.ic.p2.wepayu.controllers;

import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.utils.DateFormat;
import br.ufal.ic.p2.wepayu.utils.NumberFormat;
import br.ufal.ic.p2.wepayu.utils.TxtFileManager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayrollController {
    public static PayrollEmployeeResponse getCommissionedPayrollDetails(Employee employee, LocalDate date, double discounts) {
        LocalDate lastPaymentDate = employee.getLastPayment();

        double totalPayment = 0;
        double currentValue = 0;
        double currentValueFromSales = 0;

        long dayToDecrease = lastPaymentDate == null ? 15 : DateFormat.getDifferenceInDays(date, lastPaymentDate);

        List<Sale> saleList = employee.getSales();
        LocalDate formattedStartDate = date.minusDays(dayToDecrease);

        currentValue += (employee.getRemuneration() * 12 / 52);

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

        String paymentMethod = EmployeeController.formatPaymentMethod(employee);

        return new PayrollEmployeeResponse(
            employee,
            0,
            0,
            totalPayment,
            discounts,
            paymentMethod,
            currentValueFromSales,
            (employee.getRemuneration() * 12 / 52),
            employee.getCommission()
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

        String paymentMethod = EmployeeController.formatPaymentMethod(employee);

        return new PayrollEmployeeResponse(
            employee,
            0,
            0,
            totalPayment,
            discounts,
            paymentMethod,
            0,
            employee.getRemuneration(),
            0
        );
    }



    public static PayrollEmployeeResponse getHourlyPayrollDetails(Employee employee, LocalDate date, double discounts) {
        System.out.println("\n\nEMPLOYEE NAME -> " + employee.getName());
        System.out.println("CURRENT DATE -> " + date);
        System.out.println("LAST PAYMENT DATE -> " + employee.getLastPayment());
        System.out.println("DATE DIFFERENCE -> " + DateFormat.getDifferenceInDays(employee.getLastPayment(), date) + "\n\n");

        LocalDate lastPaymentDate = employee.getLastPayment();

        int totalHours = 0;
        int totalOvertime = 0;
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
                        totalHours += (int) timestamp.getHours() > 8 ? 8 : timestamp.getHours();
                        totalOvertime += (int) timestamp.getHours() > 8 ? timestamp.getHours() - 8 : 0;
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

        String paymentMethod = EmployeeController.formatPaymentMethod(employee);

        return new PayrollEmployeeResponse(
            employee,
            totalHours,
            totalOvertime,
            totalPayment,
            discounts,
            paymentMethod,
            0,
            employee.getRemuneration(),
            0
        );
    }

    public static String runPayrollAndGetTotal(LocalDate date, Map<LocalDate, Map<Employee, PayrollEmployeeResponse>> lastLocalPayroll) throws NoSuchFieldException, ClassNotFoundException {
        double totalPayment = 0;

        for(Employee employee : EmployeeController.getEmployees().values()) {

            boolean isUnionized = employee.getUnionized();
            double discount = 0;

            if(isUnionized) {
                discount += (employee.getUnionFee()) * (employee.getType().equals("horista") ? 7 : employee.getType().equals("comissionado") ? 14 : 30);

                String syndicateId = employee.getLinkedSyndicateId();

                Syndicate syndicate = SyndicateController.getSyndicateById(syndicateId);

                List<UnionizedEmployee> unionizedEmployees = syndicate.getEmployeesById(employee.getId());

                for(UnionizedEmployee unionizedEmployee : unionizedEmployees) {
                    discount += (unionizedEmployee.getValue() * (employee.getType().equals("horista") ? 7 : employee.getType().equals("comissionado") ? 14 : 30));
                }
            }

            PayrollEmployeeResponse payrollEmployeeResponse = null;
            LocalDate lastPaymentDate = employee.getLastPayment();

            if(employee.getType().equals("horista") && DateFormat.isFriday(date)) {
                payrollEmployeeResponse = PayrollController.getHourlyPayrollDetails(employee, date, discount);
            } else if(employee.getType().equals("comissionado") && DateFormat.isFriday(date) && DateFormat.getDifferenceInDays(date, lastPaymentDate) >= 15) {
                payrollEmployeeResponse = PayrollController.getCommissionedPayrollDetails(employee, date, discount);
            } else if(employee.getType().equals("assalariado") && DateFormat.isLastWorkingDayOfMonth(date)) {
                payrollEmployeeResponse = PayrollController.getSalariedPayrollDetails(employee, date, discount);
            }

            if(payrollEmployeeResponse != null) {
                Map<Employee, PayrollEmployeeResponse> currentList = lastLocalPayroll.get(date);

                if(currentList == null) {
                    currentList = new HashMap<>();
                }

                PayrollEmployeeResponse registerExists = currentList.get(payrollEmployeeResponse.getEmployee());

                if(registerExists == null && payrollEmployeeResponse.getRemuneration() >= 0) {
                    currentList.put(payrollEmployeeResponse.getEmployee(), payrollEmployeeResponse);
                    lastLocalPayroll.put(date, currentList);

                    totalPayment += payrollEmployeeResponse.getRemuneration();
                }
            }
        }

        return NumberFormat.doubleToCommaFormat(totalPayment);
    }

    public static void savePayrollFile(LocalDate date, String fileName, Map<LocalDate, Map<Employee, PayrollEmployeeResponse>> lastLocalPayroll) throws NoSuchFieldException, ClassNotFoundException, IOException {
        runPayrollAndGetTotal(date, lastLocalPayroll);

        TxtFileManager newTxt = new TxtFileManager(fileName);
        newTxt.addingPayrollContent(date, lastLocalPayroll.get(date));
    }

}
