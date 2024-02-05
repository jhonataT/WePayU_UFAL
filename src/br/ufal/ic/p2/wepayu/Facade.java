package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.models.EmployeeHistory;
import br.ufal.ic.p2.wepayu.models.Employee;
import br.ufal.ic.p2.wepayu.utils.DateFormat;
import br.ufal.ic.p2.wepayu.utils.XMLManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Facade {
    private List<Employee> employees;
    private List<EmployeeHistory> employeesHistory;
    private XMLManager xmlEmployeeDatabase;
    private XMLManager xmlHistoryDatabase;
    private String[] typeOptions = { "horista", "assalariado", "comissionado" };
    private String[] employeeProperties = { "id", "nome", "endereco", "tipo", "salario", "sindicalizado", "comissao" };


    public Facade() throws Exception {
        this.xmlEmployeeDatabase = new XMLManager("employee", "employees");
        this.xmlHistoryDatabase = new XMLManager("history", "employee_history");
        this.employees = this.xmlEmployeeDatabase.readAndGetEmployeeFile();
        this.employeesHistory = this.xmlHistoryDatabase.readAndGetHistoryFile();

        System.out.println("Employees number " + this.employees.size());
    }

    public void zerarSistema() throws Exception {
        this.employees = new ArrayList<Employee>();
        this.employeesHistory = new ArrayList<EmployeeHistory>();

        this.xmlEmployeeDatabase.createAndSaveEmployeeDocument(this.employees);
        this.xmlHistoryDatabase.createAndSaveHistoryDocument(this.employeesHistory);
    }

    private List<Employee> getEmployeeById(String id) {
        return this.employees.stream().filter(item -> item.getId().equals(id)).toList();
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public String getAtributoEmpregado(String id, String property) throws Exception  {
        if(id.isEmpty())
            throw new Exception("Identificacao do empregado nao pode ser nula.");
        if(Arrays.stream(this.employeeProperties).noneMatch(item -> item.equals(property)))
            throw new Exception("Atributo nao existe.");
        if(this.employees.isEmpty())
            throw new Exception("Empregado nao existe.");

        List<Employee> filteredEmployees = this.getEmployeeById(id);

        if(!filteredEmployees.isEmpty()) {
            Employee filteredEmployee = filteredEmployees.get(0);

            if(property.equals("nome"))
                return filteredEmployee.getName();
            if(property.equals("id"))
                return filteredEmployee.getId();
            else if(property.equals("endereco"))
                return filteredEmployee.getAddress();
            else if(property.equals("tipo"))
                return filteredEmployee.getType();
            else if(property.equals("salario"))
                return String.format("%.2f", filteredEmployee.getRemuneration()).replace('.', ',');
            else if(property.equals("sindicalizado"))
                return Boolean.toString(filteredEmployee.getUnionized());
            else if(property.equals("comissao"))
                return String.format("%.2f", filteredEmployee.getCommission()).replace('.', ',');
        }

        throw new Exception("Empregado nao existe.");
    }

    public String getEmpregadoPorNome(String employeeName, int index) throws Exception {
        List<Employee> filteredEmployees = this.employees.stream().filter(
            item -> item.getName().equals(employeeName)
        ).toList();

        if(!filteredEmployees.isEmpty() && index >= 0) {
            Employee employeeToReturn = filteredEmployees.get(index - 1);

            return employeeToReturn.getId();
        }

        throw new Exception("Nao ha empregado com esse nome.");
    }

    public String criarEmpregado(String name, String address, String type, String remuneration, String commission) throws Exception {
        String formattedRemuneration = remuneration.replace(',', '.');
        String formattedCommission = commission.replace(',', '.');

        if(name.isEmpty()) throw new Exception("Nome nao pode ser nulo.");
        if(address.isEmpty()) throw new Exception("Endereco nao pode ser nulo.");
        if(remuneration.isEmpty()) throw new Exception("Salario nao pode ser nulo.");
        if(type.equals("comissionado") && commission.isEmpty()) throw new Exception("Comissao nao pode ser nula.");
        if(!this.isNumeric(formattedRemuneration)) throw new Exception("Salario deve ser numerico.");
        if(!this.isNumeric(formattedCommission)) throw new Exception("Comissao deve ser numerica.");

        double newRemuneration = Double.parseDouble(formattedRemuneration);
        double newCommission = Double.parseDouble(formattedCommission);

        if(Arrays.stream(this.typeOptions).noneMatch(item -> item.equals(type))) throw new Exception("Tipo invalido.");

        if(type.equals("horista") && newCommission != 0) throw new Exception("Tipo nao aplicavel.");
        if(type.equals("assalariado") && newCommission != 0) throw new Exception("Tipo nao aplicavel.");
        if(newCommission < 0) throw new Exception("Comissao deve ser nao-negativa.");
        if(newRemuneration < 0) throw new Exception("Salario deve ser nao-negativo.");


        String newId = Integer.toString(this.employees.size() + 1);
        Employee employee = new Employee("id".concat(newId), name, address, type, newRemuneration, newCommission, false);

        this.employees.add(employee);
        saveEmployeeInDatabase();

        return employee.getId();
    }

    private List<EmployeeHistory> getEmployeeHistoryById(String employeeId) {
        return this.employeesHistory.stream().filter(item -> item.getEmployeeId().equals(employeeId)).toList();
    }

    private LocalDate verifyDate(String newDate, String type, boolean isStrict) throws Exception {
        LocalDate date;

        try {
            date = DateFormat.stringToDate(newDate, isStrict);
        } catch(Exception e) {
            if(type.equals("start")) {
                throw new Exception("Data inicial invalida.");
            } else if(type.equals("finish")) {
                throw new Exception("Data final invalida.");
            } else {
                throw new Exception("Data invalida.");
            }
        }

        return date;
    }

    public int getHorasNormaisTrabalhadas(String employeeId, String startDate, String finishDate) throws Exception {
        LocalDate formattedStartDate = this.verifyDate(startDate, "start", false);
        LocalDate formattedFinishDate = this.verifyDate(finishDate, "finish", true);

        if(formattedStartDate.isAfter(formattedFinishDate)) {
            throw new Exception("Data inicial nao pode ser posterior aa data final.");
        }

        String employeeType = getAtributoEmpregado(employeeId, "tipo");

        if(!employeeType.equals("horista")) {
            throw new Exception("Empregado nao eh horista.");
        }

        if(this.employeesHistory == null || this.employeesHistory.isEmpty()) return 0;

        List<EmployeeHistory> filteredHistory = getEmployeeHistoryById(employeeId);
        int totalHours = 0;

        if(!filteredHistory.isEmpty()) {
            for (EmployeeHistory employeeHistory : filteredHistory) {
                LocalDate date = employeeHistory.getDate();

                if(date.isAfter(formattedStartDate) || date.isEqual(formattedStartDate)) {
                    if(date.isBefore(formattedFinishDate)) {
                        int validHour = (int) employeeHistory.getHours() > 8 ? 8 : (int) employeeHistory.getHours();
                        totalHours += validHour;
                    }
                }
            }
        }

        return totalHours;
    }

    public String getHorasExtrasTrabalhadas(String employeeId, String startDate, String finishDate) throws Exception {
        LocalDate formattedStartDate = this.verifyDate(startDate, "start", false);
        LocalDate formattedFinishDate = this.verifyDate(finishDate, "finish", false);

        if(this.employeesHistory == null || this.employeesHistory.isEmpty()) return "0";

        List<EmployeeHistory> filteredHistory = getEmployeeHistoryById(employeeId);
        double totalHours = 0;
        int workedDays = 0;

        if(!filteredHistory.isEmpty()) {
            for (EmployeeHistory employeeHistory : filteredHistory) {
                LocalDate date = employeeHistory.getDate();

                if(date.isAfter(formattedStartDate) || date.isEqual(formattedStartDate)) {
                    if(date.isBefore(formattedFinishDate)) {
                        workedDays += 1;
                        totalHours += employeeHistory.getHours();
                    }
                }
            }
        }

        totalHours = totalHours >= (workedDays*8) ? totalHours - (8*workedDays) : 0;

        return totalHours > 0 ?
            Double.toString(totalHours).replace('.', ',').replace(",0", "")
            : "0";
    }

    public void lancaCartao(String employeeId, String date, String hours) throws Exception {
        String employeeType = getAtributoEmpregado(employeeId, "tipo");

        LocalDate formattedDate = this.verifyDate(date, "", true);

        if(!employeeType.equals("horista")) {
            throw new Exception("Empregado nao eh horista.");
        }

        String newId = "history_id_"+this.employeesHistory.size();

        double formattedHours = Double.parseDouble(hours.replace(",", "."));

        if(formattedHours <= 0) {
            throw new Exception("Horas devem ser positivas.");
        }

        EmployeeHistory newEmployeeHistory = new EmployeeHistory(newId, employeeId, formattedDate, formattedHours);

        this.employeesHistory.add(newEmployeeHistory);
        saveHistoryInDatabase();
    }

    public String criarEmpregado(String name, String address, String type, String remuneration) throws Exception {
        if(type.equals("comissionado")) throw new Exception("Tipo nao aplicavel.");

        return criarEmpregado(name, address, type, remuneration, "0");
    }

    public void removeEmpregado(String employeeId) throws Exception {
        this.employees = this.employees.stream().filter(item -> !item.getId().equals(employeeId)).toList();
        saveEmployeeInDatabase();
    }

    public void saveHistoryInDatabase() throws Exception {
        try {
            this.xmlHistoryDatabase.createAndSaveHistoryDocument(this.employeesHistory);
            this.employeesHistory = this.xmlHistoryDatabase.readAndGetHistoryFile();
        } catch(Exception error) {
            error.printStackTrace();
        }
    }

    public boolean saveEmployeeInDatabase() throws Exception {
        try {
            this.xmlEmployeeDatabase.createAndSaveEmployeeDocument(this.employees);
            this.employees = this.xmlEmployeeDatabase.readAndGetEmployeeFile();
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void encerrarSistema() {}

}
