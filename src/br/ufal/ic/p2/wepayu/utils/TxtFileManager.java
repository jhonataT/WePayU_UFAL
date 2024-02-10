package br.ufal.ic.p2.wepayu.utils;

import br.ufal.ic.p2.wepayu.models.Employee;
import br.ufal.ic.p2.wepayu.models.PayrollEmployeeResponse;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class TxtFileManager {
    private String fileName;
    private FileWriter fileWriter;

    public TxtFileManager(String fileName) throws IOException {
        this.fileName = "src/br/ufal/ic/p2/wepayu/payroll/"+fileName;

        try {
            this.fileWriter = new FileWriter(this.fileName);
        }  catch(IOException e) {
            throw new IOException("Erro ao criar arquivo.");
        }
    }
    public void addingPayrollContent(LocalDate payrollDate, Map<Employee, PayrollEmployeeResponse> payrollList) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.fileName))) {
            bufferedWriter.write("FOLHA DE PAGAMENTO DO DIA "+payrollDate);
            bufferedWriter.newLine();
            bufferedWriter.write("====================================");
            bufferedWriter.newLine();
            bufferedWriter.newLine();

            writeFormattedContent(bufferedWriter, payrollList);

        } catch (IOException e) {
            throw new IOException("Erro ao escrever no arquivo: " + e.getMessage());
        }
    }

    private static void addingHourlyPayrollEmployees(BufferedWriter writer, List<PayrollEmployeeResponse> payrollList) throws IOException {
        writer.write("===============================================================================================================================");
        writer.newLine();
        writer.write("===================== HORISTAS ================================================================================================");
        writer.newLine();
        writer.write("===============================================================================================================================");
        writer.newLine();
        writer.write("Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo");
        writer.newLine();
        writer.write("==================================== ===== ===== ============= ========= =============== ======================================");
        writer.newLine();

        int totalHours = 0;
        int totalExtraHours = 0;
        double totalRemuneration = 0;
        double totalDiscounts = 0;

        for (PayrollEmployeeResponse currentPayroll : payrollList) {
            totalHours += currentPayroll.getHours();
            totalExtraHours += currentPayroll.getExtraHours();
            totalRemuneration += currentPayroll.getRemuneration();
            totalDiscounts += currentPayroll.getDiscounts();

            String currentRow = String.format("%-37s %5d %5d %13s %9s %15s %-40s",
                    currentPayroll.getEmployee().getName(),
                    currentPayroll.getHours(),
                    currentPayroll.getExtraHours(),
                    NumberFormat.doubleToCommaFormat(currentPayroll.getRemuneration()),
                    NumberFormat.doubleToCommaFormat(currentPayroll.getDiscounts()),
                    NumberFormat.doubleToCommaFormat(currentPayroll.getRemuneration() - currentPayroll.getDiscounts()),
                    currentPayroll.getPaymentMethod());

            writer.write(currentRow);
            writer.newLine();
        }

        String finalLine = String.format("TOTAL HORISTAS                        %5d %5d %13s %9s %15s",
                totalHours,
                totalExtraHours,
                NumberFormat.doubleToCommaFormat(totalRemuneration),
                NumberFormat.doubleToCommaFormat(totalDiscounts),
                NumberFormat.doubleToCommaFormat(totalRemuneration - totalDiscounts));

        writer.newLine();
        writer.write(finalLine);
        writer.newLine();
    }

    private static void addingWageEarnersPayrollEmployees(BufferedWriter writer, List<PayrollEmployeeResponse> payrollList) throws IOException {
        writer.newLine();
        writer.write("===============================================================================================================================");
        writer.newLine();
        writer.write("===================== ASSALARIADOS ============================================================================================");
        writer.newLine();
        writer.write("===============================================================================================================================");
        writer.newLine();
        writer.write("Nome                                             Salario Bruto Descontos Salario Liquido Metodo");
        writer.newLine();
        writer.write("==================================== ============= ========= =============== ======================================");

        double totalRemuneration = 0;
        double totalDiscounts = 0;

        for (PayrollEmployeeResponse currentPayroll : payrollList) {
            totalRemuneration += currentPayroll.getRemuneration();
            totalDiscounts += currentPayroll.getDiscounts();

            String currentRow = String.format("%-45s %13s %9s %15s %-40s",
                currentPayroll.getEmployee().getName(),
                NumberFormat.doubleToCommaFormat(currentPayroll.getRemuneration()),
                NumberFormat.doubleToCommaFormat(currentPayroll.getDiscounts()),
                NumberFormat.doubleToCommaFormat(currentPayroll.getRemuneration() - currentPayroll.getDiscounts()),
                currentPayroll.getPaymentMethod());

            writer.newLine();
            writer.write(currentRow);
        }

        String finalLine = String.format("TOTAL ASSALARIADOS                                       %13s %9s %15s",
            NumberFormat.doubleToCommaFormat(totalRemuneration),
            NumberFormat.doubleToCommaFormat(totalDiscounts),
            NumberFormat.doubleToCommaFormat(totalRemuneration - totalDiscounts));

        writer.newLine();
        writer.newLine();
        writer.write(finalLine);
        writer.newLine();
    }

    private static void addingCommissionedPayrollEmployees(BufferedWriter writer, List<PayrollEmployeeResponse> payrollList) throws IOException {
        writer.newLine();
        writer.write("===============================================================================================================================");
        writer.newLine();
        writer.write("===================== COMISSIONADOS ===========================================================================================");
        writer.newLine();
        writer.write("===============================================================================================================================");
        writer.newLine();
        writer.write("Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo");
        writer.newLine();
        writer.write("===================== ======== ======== ======== ============= ========= =============== ======================================");

        double totalRemuneration = 0;
        double totalDiscounts = 0;

        for (PayrollEmployeeResponse currentPayroll : payrollList) {
            totalRemuneration += currentPayroll.getRemuneration();
            totalDiscounts += currentPayroll.getDiscounts();

            String currentRow = String.format("%-25s %10s %10s %10s %13s %9s %15s %-40s",
                currentPayroll.getEmployee().getName(),
                NumberFormat.doubleToCommaFormat(currentPayroll.getFixedRemuneration()),
                NumberFormat.doubleToCommaFormat(currentPayroll.getTotalSales()),
                NumberFormat.doubleToCommaFormat(currentPayroll.getCommission()),
                NumberFormat.doubleToCommaFormat(currentPayroll.getRemuneration()),
                NumberFormat.doubleToCommaFormat(currentPayroll.getDiscounts()),
                NumberFormat.doubleToCommaFormat(currentPayroll.getRemuneration() - currentPayroll.getDiscounts()),
                currentPayroll.getPaymentMethod());

            writer.newLine();
            writer.write(currentRow);
        }

        String finalLine = String.format("TOTAL COMISSIONADOS      %10s %10s %10s %13s %9s %15s",
            NumberFormat.doubleToCommaFormat(totalRemuneration),
            "0,00", // Considerando que não há descontos específicos para comissionados no modelo
            "0,00", // Considerando que não há descontos específicos para comissionados no modelo
            NumberFormat.doubleToCommaFormat(totalRemuneration),
            NumberFormat.doubleToCommaFormat(totalDiscounts),
            NumberFormat.doubleToCommaFormat(totalRemuneration - totalDiscounts));

        writer.newLine();
        writer.newLine();
        writer.write(finalLine);
        writer.newLine();
    }


    private static void sortByEmployeeName(List<PayrollEmployeeResponse> currentList) {
        currentList.sort(Comparator.comparing(employeeResponse -> employeeResponse.getEmployee().getName()));
    }

    private static void writeFormattedContent(BufferedWriter writer, Map<Employee, PayrollEmployeeResponse> payrollList) throws IOException {
        List<PayrollEmployeeResponse> filteredHourlyList = new ArrayList<>();
        List<PayrollEmployeeResponse> filteredWageEarnersList = new ArrayList<>();
        List<PayrollEmployeeResponse> filteredCommissionedList = new ArrayList<>();

        for (PayrollEmployeeResponse currentEmployee : payrollList.values()) {
            if(currentEmployee.getEmployee().getType().equals("horista"))
                filteredHourlyList.add(currentEmployee);
            else if(currentEmployee.getEmployee().getType().equals("assalariado"))
                filteredWageEarnersList.add(currentEmployee);
            else if(currentEmployee.getEmployee().getType().equals("comissionado"))
                filteredCommissionedList.add(currentEmployee);
        }

        if(!filteredHourlyList.isEmpty()) sortByEmployeeName(filteredHourlyList);
        if(!filteredWageEarnersList.isEmpty()) sortByEmployeeName(filteredWageEarnersList);
        if(!filteredCommissionedList.isEmpty()) sortByEmployeeName(filteredCommissionedList);

        addingHourlyPayrollEmployees(writer, filteredHourlyList);
        addingWageEarnersPayrollEmployees(writer, filteredWageEarnersList);
        addingCommissionedPayrollEmployees(writer, filteredCommissionedList);
    }

    public String getContent() throws FileNotFoundException {
        String row;

        try {
            FileReader fileReader = new FileReader(this.fileName);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((row = bufferedReader.readLine()) != null) {
                System.out.println(row);
            }

            bufferedReader.close();
        } catch(FileNotFoundException e) {
            throw new FileNotFoundException("Erro ao ler o arquivo");
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo");
        }

        return row;
    }

    public static String getContent(String fileName) throws FileNotFoundException {
        String row;

        try {
            FileReader fileReader = new FileReader(fileName);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((row = bufferedReader.readLine()) != null) {
                System.out.println(row);
            }

            bufferedReader.close();
        } catch(FileNotFoundException e) {
            throw new FileNotFoundException("Erro ao ler o arquivo");
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo");
        }

        return row;
    }
}
