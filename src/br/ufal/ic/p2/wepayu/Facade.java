package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.utils.DateFormat;
import br.ufal.ic.p2.wepayu.utils.XMLManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Facade {
    private List<Employee> employees;
    private List<Syndicate> syndicates;
    private XMLManager xmlDatabase;
    private XMLManager xmlSalesDatabase;
    private String[] typeOptions = { "horista", "assalariado", "comissionado" };
    private String[] employeeProperties = { "id", "nome", "endereco", "tipo", "salario", "sindicalizado", "comissao", "hours", "sales" };

    public Facade() throws Exception {
        this.xmlDatabase = new XMLManager("employees");
        this.employees = this.xmlDatabase.readAndGetEmployeeFile();
        this.syndicates = new ArrayList<>();
    }

    public void zerarSistema() throws Exception {
        this.employees = new ArrayList<Employee>();

        this.xmlDatabase.createAndSaveEmployeeDocument(this.employees);
    }

    private List<Employee> getEmployeeById(String id) {
        return this.employees.stream().filter(item -> item.getId().equals(id)).toList();
    }

    private List<Syndicate> getSyndicateById(String id) {
        return this.syndicates.stream().filter(item -> item.getId().equals(id)).toList();
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public String getAtributoEmpregado(String employeeId, String property) throws Exception  {
        if(employeeId.isEmpty())
            throw new Exception("Identificacao do empregado nao pode ser nula.");
        if(Arrays.stream(this.employeeProperties).noneMatch(item -> item.equals(property)))
            throw new Exception("Atributo nao existe.");
        if(this.employees.isEmpty())
            throw new Exception("Empregado nao existe.");

        List<Employee> filteredEmployees = this.getEmployeeById(employeeId);

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

        List<Employee> employeeList = getEmployeeById(employeeId);

        if(employeeList.isEmpty()) {
            throw new Exception("Empregado nao existe.");
        }

        Employee employee = employeeList.get(0);

        if(!employee.getType().equals("horista")) {
            throw new Exception("Empregado nao eh horista.");
        }

        List<Timestamp> employeeTimeStamp = employee.getTimestamp();

        if(employeeTimeStamp.isEmpty()) return 0;

        int totalHours = 0;

        for (Timestamp timestamp : employeeTimeStamp) {
            LocalDate date = timestamp.getDate();

            if(date.isAfter(formattedStartDate) || date.isEqual(formattedStartDate)) {
                if(date.isBefore(formattedFinishDate)) {
                    int validHour = (int) timestamp.getHours() > 8 ? 8 : (int) timestamp.getHours();

                    totalHours += validHour;
                }
            }
        }

        return totalHours;
    }

    public String getHorasExtrasTrabalhadas(String employeeId, String startDate, String finishDate) throws Exception {
        LocalDate formattedStartDate = this.verifyDate(startDate, "start", true);
        LocalDate formattedFinishDate = this.verifyDate(finishDate, "finish", true);

        List<Employee> employeeList = getEmployeeById(employeeId);

        if(employeeList.isEmpty())
            throw new Exception("Empregado nao existe.");

        Employee employee = employeeList.get(0);

        List<Timestamp> employeeTimeStampList = employee.getTimestamp();

        if(employeeTimeStampList == null || employeeTimeStampList.isEmpty()) return "0";

        double totalHours = 0;
        int workedDays = 0;

        for (Timestamp timestamp : employeeTimeStampList) {
            LocalDate date = timestamp.getDate();

            if(date.isAfter(formattedStartDate) || date.isEqual(formattedStartDate)) {
                if(date.isBefore(formattedFinishDate)) {
                    workedDays += 1;
                    totalHours += timestamp.getHours();
                }
            }
        }

        totalHours = totalHours >= (workedDays*8) ? totalHours - (8*workedDays) : 0;

        return totalHours > 0 ?
            Double.toString(totalHours).replace('.', ',').replace(",0", "")
            : "0";
    }

    public void lancaCartao(String employeeId, String date, String hours) throws Exception {
        if(employeeId.isEmpty()) throw new Exception("Identificacao do empregado nao pode ser nula.");

        LocalDate formattedDate = this.verifyDate(date, "", true);

        List<Employee> employeeList = getEmployeeById(employeeId);

        if(employeeList.isEmpty()) {
            throw new Exception("Empregado nao existe.");
        }

        Employee employee = employeeList.get(0);

        if(!employee.getType().equals("horista")) {
            throw new Exception("Empregado nao eh horista.");
        }

        List<Timestamp> timestamps = employee.getTimestamp();

        String newId = employeeId+"_id_0";

        if(!timestamps.isEmpty()) {
            newId += timestamps.size();
        }

        double formattedHours = Double.parseDouble(hours.replace(",", "."));

        if(formattedHours <= 0) {
            throw new Exception("Horas devem ser positivas.");
        }

        Timestamp newTimestamp = new Timestamp(newId, formattedDate, formattedHours);

        employee.setTimestamp(newTimestamp);

        this.removerEmpregado(employeeId);
        this.employees.add(employee);
        saveEmployeeInDatabase();
    }

    public String criarEmpregado(String name, String address, String type, String remuneration) throws Exception {
        if(type.equals("comissionado")) throw new Exception("Tipo nao aplicavel.");

        return criarEmpregado(name, address, type, remuneration, "0");
    }

    public void lancaVenda(String employeeId, String date, String value) throws Exception {
        if(employeeId.isEmpty()) throw new Exception("Identificacao do empregado nao pode ser nula.");

        LocalDate formattedDate = this.verifyDate(date, "", true);

        List<Employee> employeeList = getEmployeeById(employeeId);

        if(employeeList.isEmpty()) {
            throw new Exception("Empregado nao existe.");
        }

        Employee employee = employeeList.get(0);

        if(!employee.getType().equals("comissionado")) {
            throw new Exception("Empregado nao eh comissionado.");
        }

        List<Sale> sales = employee.getSales();

        String newId = employeeId+"_saleId_0";

        if(!sales.isEmpty()) {
            newId += sales.size();
        }

        double formattedValue = Double.parseDouble(value.replace(",", "."));

        if(formattedValue <= 0) {
            throw new Exception("Valor deve ser positivo.");
        }

        Sale newSale = new Sale(newId, formattedDate, formattedValue);

        employee.setSale(newSale);

        this.removerEmpregado(employeeId);
        this.employees.add(employee);
        saveEmployeeInDatabase();
    }

    public String getVendasRealizadas(String employeeId, String startDate, String finishDate) throws Exception {
        LocalDate formattedStartDate = this.verifyDate(startDate, "start", true);
        LocalDate formattedFinishDate = this.verifyDate(finishDate, "finish", true);

        if(formattedStartDate.isAfter(formattedFinishDate)) {
            throw new Exception("Data inicial nao pode ser posterior aa data final.");
        }

        List<Employee> employeeList = getEmployeeById(employeeId);

        if(employeeList.isEmpty()) {
            throw new Exception("Empregado nao existe.");
        }

        Employee employee = employeeList.get(0);

        if(!employee.getType().equals("comissionado")) {
            throw new Exception("Empregado nao eh comissionado.");
        }

        List<Sale> employeeSales = employee.getSales();

        if(employeeSales.isEmpty()) return "0,00";

        double totalHours = 0;

        for (Sale sale : employeeSales) {
            LocalDate date = sale.getDate();

            if(date.isAfter(formattedStartDate) || date.isEqual(formattedStartDate)) {
                if(date.isBefore(formattedFinishDate)) {
                    totalHours += sale.getValue();
                }
            }
        }

        return String.format("%.2f", totalHours).replace('.', ',');
    }

    public void alteraEmpregado(String employeeId, String property, String value, String syndicateId, String unionFee) throws Exception {
        if(employeeId.isEmpty()) throw new Exception("Identificacao do empregado nao pode ser nula.");

        List<Employee> employeeList = getEmployeeById(employeeId);

        if(employeeList.isEmpty()) {
            throw new Exception("Empregado nao existe.");
        }

        Employee employee = employeeList.get(0);

        if(property.equals("sindicalizado")) {
            boolean isUnionized = Boolean.parseBoolean(value);

            employee.setUnionized(isUnionized);

            if(isUnionized) {
                double formattedunionFee = Double.parseDouble(unionFee.replace(',', '.'));

                List<Syndicate> filteredSyndicates = getSyndicateById(syndicateId);
                Syndicate syndicate;

                System.out.println("BEFORE CREATE -> "+filteredSyndicates.size());

                if(filteredSyndicates.isEmpty()) {
                    System.out.println("CREATE ");
                    syndicate = new Syndicate(syndicateId);
                } else {
                    syndicate = filteredSyndicates.get(0);
                }

                this.syndicates.add(syndicate);

                UnionizedEmployee syndicateEmployee = syndicate.getEmployeeById(employeeId);

                if(syndicateEmployee == null || syndicateEmployee.getId().isEmpty()) {
                    String newId = ""+(syndicate.getEmployees().isEmpty() ? 0 : syndicate.getEmployees().size());

                    syndicateEmployee = new UnionizedEmployee(
                        employeeId,
                        employee.getName(),
                        employee.getType(),
                        employee.getRemuneration(),
                        employee.getSales(),
                        "unionizedId_"+newId,
                        formattedunionFee
                    );

                    syndicate.addNewEmployee(syndicateEmployee);
                } else {
                    syndicateEmployee.setUnionFee(formattedunionFee);
                }

                employee.setLinkedSyndicate(syndicate);
                employee.setUnionFee(formattedunionFee);

                employee.setUnionized(Boolean.parseBoolean(value));
            }
        }
    }

    public void lancaTaxaServico(String syndicateId, String date, String value) throws Exception {
        LocalDate formattedDate = this.verifyDate(date, "", true);
        double formattedValue = Double.parseDouble(value.replace(",", "."));

        if(formattedValue <= 0)
            throw new Exception("Valor deve ser positivo.");

        List<Syndicate> filteredSyndicates = getSyndicateById(syndicateId);

        if(filteredSyndicates.isEmpty())
            throw new Exception("Membro nao existe.");

        Syndicate syndicate = filteredSyndicates.get(0);

        String newId = "unionFee_id_" + (syndicate.getUnionFeeList().isEmpty() ? 0 : syndicate.getUnionFeeList().size());

        UnionFee newUnionFee = new UnionFee(newId, formattedDate, formattedValue);
        syndicate.addNewUnionFee(newUnionFee);
    }

    public String getTaxasServico(String employeeId, String startDate, String finishDate) throws Exception {
        LocalDate formattedStartDate = this.verifyDate(startDate, "start", true);
        LocalDate formattedFinishDate = this.verifyDate(finishDate, "finish", true);

        List<Employee> filteredEmployees = getEmployeeById(employeeId);

        System.out.println("filteredEmployees SIZE -> "+filteredEmployees.size());

        if(filteredEmployees.isEmpty())
            throw new Exception("Membro nao existe.");

        // Create method to get Employees unionized;
        Employee employee = filteredEmployees.get(0);

        double totalValue = 0;

        // List<UnionFee> unionFeeList = employee.getUnionFeeList();

        /*for (UnionFee unionFee : unionFeeList) {
            LocalDate date = unionFee.getDate();

            if(date.isAfter(formattedStartDate) || date.isEqual(formattedStartDate)) {
                if(date.isBefore(formattedFinishDate)) {
                    totalValue += unionFee.getValue();
                }
            }
        }*/

        return String.format("%.2f", totalValue).replace('.', ',');
    }

    public void alteraEmpregado(String employeeId, String property, String value) throws Exception {
        this.alteraEmpregado(employeeId, property, value, "", "0");
    }

    public void removerEmpregado(String employeeId) throws Exception {
        if(employeeId.isEmpty()) throw new Exception("Identificacao do empregado nao pode ser nula.");

        List<Employee> employeeToRemove = this.employees.stream().filter(item -> item.getId().equals(employeeId)).toList();

        if(employeeToRemove.isEmpty()) throw new Exception("Empregado nao existe.");

        this.employees = this.employees.stream().filter(item -> !item.getId().equals(employeeId)).toList();

        saveEmployeeInDatabase();
    }

    public boolean saveEmployeeInDatabase() throws Exception {
        try {
            this.xmlDatabase.createAndSaveEmployeeDocument(this.employees);
            this.employees = this.xmlDatabase.readAndGetEmployeeFile();
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void encerrarSistema() {}
}
