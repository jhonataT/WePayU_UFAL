package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.models.*;
import br.ufal.ic.p2.wepayu.utils.DateFormat;
import br.ufal.ic.p2.wepayu.utils.XMLEmployeeManager;
import br.ufal.ic.p2.wepayu.utils.XMLSyndicateManager;

import java.time.LocalDate;
import java.util.*;

public class Facade {
    private Map<String, Employee> employees;
    private Map<String, Syndicate> syndicates;
    private final XMLEmployeeManager xmlDatabase;
    private final XMLSyndicateManager xmlSyndicateDb;
    private final String[] typeOptions = { "horista", "assalariado", "comissionado" };
    private final String[] formOfPaymentOptions = { "banco", "correios", "emMaos" };
    private final String[] employeeProperties = { "id", "nome", "endereco", "tipo", "salario", "sindicalizado", "comissao", "hours", "sales", "banco" };

    public Facade() throws Exception {
        this.xmlDatabase = new XMLEmployeeManager("employees");
        this.xmlSyndicateDb = new XMLSyndicateManager("syndicates");

        this.employees = this.xmlDatabase.readAndGetEmployeeFile();
        this.syndicates = this.xmlSyndicateDb.readAndGetSyndicateFile(this.employees);
    }

    public void zerarSistema() throws Exception {
        this.employees = new HashMap<>();
        this.syndicates = new HashMap<>();
    }

    private Employee getEmployeeById(String id) throws Exception {
        Employee employeeToReturn = this.employees.get(id);

        if(employeeToReturn == null)
            throw new Exception("Empregado nao existe.");

        return employeeToReturn;
    }

    private Employee getEmployeeBySyndicate(String syndicateId) {
        for (Employee employee : this.employees.values()) {
            String linkedSyndicateId = employee.getLinkedSyndicateId();
            if (linkedSyndicateId != null && linkedSyndicateId.equals(syndicateId)) {
                return employee;
            }
        }

        return null;
    }

    private List<Employee> getEmployeesByName(String employeeName) {
        List<Employee> employeesToReturn = new ArrayList<>();

        for (Employee employee : this.employees.values()) {
            if (employee.getName().equals(employeeName)) {
                employeesToReturn.add(employee);
            }
        }

        return employeesToReturn;
    }

    private Syndicate getSyndicateById(String syndicateId) {
        return this.syndicates.get(syndicateId);
    }

    private void updateEmployeeList(Employee employeeToUpdate) {

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
        // if(Arrays.stream(this.employeeProperties).noneMatch(item -> item.equals(property)))
           //  throw new Exception("Atributo nao existe.");

        Employee employee = this.getEmployeeById(employeeId);

        if(property.equals("nome")) {
            return employee.getName();
        } else if(property.equals("id")) {
            return employee.getId();
        } else if(property.equals("banco") || property.equals("agencia")  || property.equals("contaCorrente") ) {
            EmployeeBank bankInfo = employee.getEmployeeBank();
            if(!employee.getFormOfPayment().equals("banco") || bankInfo == null)
                throw new Exception("Empregado nao recebe em banco.");

            return property.equals("banco") ? bankInfo.getBankName()
                : property.equals("agencia") ?
                bankInfo.getBankBranch() :
                bankInfo.getCurrentAccount();
        } else if(property.equals("idSindicato") || property.equals("taxaSindical")) {
            String employeeSyndicateId = employee.getLinkedSyndicateId();
            Syndicate syndicate = getSyndicateById(employeeSyndicateId);

            if(!employee.getUnionized() || employeeSyndicateId == null) throw new Exception("Empregado nao eh sindicalizado.");

            String value = String.format("%.2f", syndicate.getEmployeeById(employee.getId()).getValue()).replace('.', ',');

            return property.equals("idSindicato") ? employeeSyndicateId : value;
        } else if(property.equals("endereco")) {
            return employee.getAddress();
        } else if(property.equals("tipo")) {
            return employee.getType();
        } else if(property.equals("salario")) {
            return String.format("%.2f", employee.getRemuneration()).replace('.', ',');
        } else if(property.equals("sindicalizado")) {
            return Boolean.toString(employee.getUnionized());
        } else if(property.equals("comissao")) {
            if(!employee.getType().equals("comissionado"))
                throw new Exception("Empregado nao eh comissionado.");
            return String.format("%.2f", employee.getCommission()).replace('.', ',');
        } else if (property.equals("metodoPagamento")) {
            return employee.getFormOfPayment();
        } else {
            throw new Exception("Atributo nao existe.");
        }
    }

    public String getEmpregadoPorNome(String employeeName, int index) throws Exception {
        List<Employee> filteredEmployees = getEmployeesByName(employeeName);

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

        this.employees.put(employee.getId(), employee);

        return employee.getId();
    }

    private LocalDate dateVerify(String newDate, String type, boolean isStrict) throws Exception {
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
        LocalDate formattedStartDate = this.dateVerify(startDate, "start", false);
        LocalDate formattedFinishDate = this.dateVerify(finishDate, "finish", true);

        if(formattedStartDate.isAfter(formattedFinishDate)) {
            throw new Exception("Data inicial nao pode ser posterior aa data final.");
        }

        Employee filtredEmployee = getEmployeeById(employeeId);

        if(!filtredEmployee.getType().equals("horista")) {
            throw new Exception("Empregado nao eh horista.");
        }

        List<Timestamp> employeeTimeStamp = filtredEmployee.getTimestamp();

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
        LocalDate formattedStartDate = this.dateVerify(startDate, "start", true);
        LocalDate formattedFinishDate = this.dateVerify(finishDate, "finish", true);

        Employee filtredEmployee = getEmployeeById(employeeId);



        List<Timestamp> employeeTimeStampList = filtredEmployee.getTimestamp();

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

        LocalDate formattedDate = this.dateVerify(date, "", true);

        Employee filtredEmployee = getEmployeeById(employeeId);

        if(!filtredEmployee.getType().equals("horista"))
            throw new Exception("Empregado nao eh horista.");

        List<Timestamp> timestamps = filtredEmployee.getTimestamp();

        String newId = employeeId+"_id_0";

        if(!timestamps.isEmpty()) {
            newId += timestamps.size();
        }

        double formattedHours = Double.parseDouble(hours.replace(",", "."));

        if(formattedHours <= 0) {
            throw new Exception("Horas devem ser positivas.");
        }

        Timestamp newTimestamp = new Timestamp(newId, formattedDate, formattedHours);

        filtredEmployee.setTimestamp(newTimestamp);

        this.employees.put(employeeId, filtredEmployee);
    }

    public String criarEmpregado(String name, String address, String type, String remuneration) throws Exception {
        if(type.equals("comissionado")) throw new Exception("Tipo nao aplicavel.");

        return criarEmpregado(name, address, type, remuneration, "0");
    }

    public void lancaVenda(String employeeId, String date, String value) throws Exception {
        if(employeeId.isEmpty()) throw new Exception("Identificacao do empregado nao pode ser nula.");

        LocalDate formattedDate = this.dateVerify(date, "", true);

        Employee filteredEmployee = getEmployeeById(employeeId);

        if(!filteredEmployee.getType().equals("comissionado"))
            throw new Exception("Empregado nao eh comissionado.");

        List<Sale> sales = filteredEmployee.getSales();

        String newId = employeeId+"_saleId_0";

        if(!sales.isEmpty()) {
            newId += sales.size();
        }

        double formattedValue = Double.parseDouble(value.replace(",", "."));

        if(formattedValue <= 0)
            throw new Exception("Valor deve ser positivo.");

        Sale newSale = new Sale(newId, formattedDate, formattedValue);

        filteredEmployee.setSale(newSale);

        this.employees.put(employeeId, filteredEmployee);
    }

    public String getVendasRealizadas(String employeeId, String startDate, String finishDate) throws Exception {
        LocalDate formattedStartDate = this.dateVerify(startDate, "start", true);
        LocalDate formattedFinishDate = this.dateVerify(finishDate, "finish", true);

        if(formattedStartDate.isAfter(formattedFinishDate)) {
            throw new Exception("Data inicial nao pode ser posterior aa data final.");
        }

        Employee filteredEmployee = getEmployeeById(employeeId);

        if(!filteredEmployee.getType().equals("comissionado"))
            throw new Exception("Empregado nao eh comissionado.");

        List<Sale> employeeSales = filteredEmployee.getSales();

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

    public void alteraEmpregado(String employeeId, String property, String value, String commission) throws Exception {
        if(employeeId.isEmpty()) throw new Exception("Identificacao do empregado nao pode ser nula.");

        Employee filteredEmployee = getEmployeeById(employeeId);

        if(property.equals("tipo")) {
            filteredEmployee.setType(value);

            if(value.equals("comissionado")) {
                double formattedValue = Double.parseDouble(commission.replace(',', '.'));
                filteredEmployee.setCommission(formattedValue);
            } else if(value.equals("horista")) {
                int formattedValue = Integer.parseInt(commission.replace(',', '.'));
                filteredEmployee.setRemuneration(formattedValue);
            }
        } else {
            throw new Exception("Atributo nao existe.");
        }
    }

    public void alteraEmpregado(String employeeId, String property, String value, String bankName, String bankBranch, String currentAccount) throws Exception {
        if(employeeId.isEmpty()) throw new Exception("Identificacao do empregado nao pode ser nula.");

        Employee filteredEmployee = getEmployeeById(employeeId);

        if(property.equals("metodoPagamento") && value.equals("banco")) {
            if(bankName == null || bankName.isEmpty())
                throw new Exception("Banco nao pode ser nulo.");

            if(bankBranch == null || bankBranch.isEmpty())
                throw new Exception("Agencia nao pode ser nulo.");

            if(currentAccount == null || currentAccount.isEmpty())
                throw new Exception("Conta corrente nao pode ser nulo.");

            filteredEmployee.setFormOfPayment(value);
            filteredEmployee.setEmployeeBank(bankName, bankBranch, currentAccount);
        }
    }

    public void alteraEmpregado(String employeeId, String property, String value, String syndicateId, String unionFee) throws Exception {
        if(employeeId.isEmpty()) throw new Exception("Identificacao do empregado nao pode ser nula.");

        Employee filteredEmployee = getEmployeeById(employeeId);

        if(property.equals("nome")) {
            if(value == null || value.isEmpty())
                throw new Exception("Nome nao pode ser nulo.");

            filteredEmployee.setName(value);
        } else if(property.equals("endereco")) {
            if(value == null || value.isEmpty())
                throw new Exception("Endereco nao pode ser nulo.");

            filteredEmployee.setAddress(value);
        } else if(property.equals("tipo")) {
            if(Arrays.stream(this.typeOptions).noneMatch(item -> item.equals(value)))
                throw new Exception("Tipo invalido.");

            filteredEmployee.setType(value);
        } else if(property.equals("metodoPagamento")) {
            if(Arrays.stream(this.formOfPaymentOptions).noneMatch(item -> item.equals(value)))
                throw new Exception("Metodo de pagamento invalido.");

            filteredEmployee.setFormOfPayment(value);
        } else if(property.equals("salario")) {
            if(value == null || value.isEmpty())
                throw new Exception("Salario nao pode ser nulo.");

            if(!this.isNumeric(value))
                throw new Exception("Salario deve ser numerico.");

            if(Integer.parseInt(value) <= 0)
                throw new Exception("Salario deve ser nao-negativo.");

            filteredEmployee.setRemuneration(Integer.parseInt(value));
        } else if(property.equals("comissao")) {
            if(!filteredEmployee.getType().equals("comissionado"))
                throw new Exception("Empregado nao eh comissionado.");

            if(value.isEmpty())
                throw new Exception("Comissao nao pode ser nula.");

            if(!this.isNumeric(value.replace(',', '.')))
                throw new Exception("Comissao deve ser numerica.");

            if(Double.parseDouble(value.replace(',', '.')) <= 0)
                throw new Exception("Comissao deve ser nao-negativa.");

            double formattedValue = Double.parseDouble(value.replace(',', '.'));
            filteredEmployee.setCommission(formattedValue);
        } else if(property.equals("sindicalizado")) {
            if(!value.equals("false") && !value.equals("true"))
                throw new Exception("Valor deve ser true ou false.");

            if(syndicateId.isEmpty())
                throw new Exception("Identificacao do sindicato nao pode ser nula.");

            if(unionFee.isEmpty())
                throw new Exception("Taxa sindical nao pode ser nula.");

            if(!this.isNumeric(unionFee.replace(',', '.')))
               throw new Exception("Taxa sindical deve ser numerica.");

            if(Double.parseDouble(unionFee.replace(",", ".")) <= 0)
              throw new Exception("Taxa sindical deve ser nao-negativa.");

            boolean isUnionized = Boolean.parseBoolean(value);

            filteredEmployee.setUnionized(isUnionized);

            if(isUnionized) {
                double formattedUnionFee = Double.parseDouble(unionFee.replace(',', '.'));

                Employee employeeWithSyndicate = getEmployeeBySyndicate(syndicateId);

                if(employeeWithSyndicate != null && !employeeWithSyndicate.getId().isEmpty())
                    throw new Exception("Ha outro empregado com esta identificacao de sindicato");

                Syndicate syndicate = getSyndicateById(syndicateId);

                if(syndicate == null) syndicate = new Syndicate(syndicateId);

                this.syndicates.put(syndicateId, syndicate);

                UnionizedEmployee syndicateEmployee = syndicate.getEmployeeById(employeeId);

                if(syndicateEmployee == null || syndicateEmployee.getId().isEmpty()) {
                    String newId = ""+(syndicate.getEmployees().isEmpty() ? 0 : syndicate.getEmployees().size());

                    syndicateEmployee = new UnionizedEmployee(
                        employeeId,
                        filteredEmployee.getName(),
                        filteredEmployee.getType(),
                        filteredEmployee.getRemuneration(),
                        filteredEmployee.getSales(),
                        "unionizedId_"+newId,
                            formattedUnionFee,
                        true
                    );

                    syndicate.addNewEmployee(syndicateEmployee);
                } else {
                    syndicateEmployee.setUnionFee(formattedUnionFee);
                }

                filteredEmployee.setLinkedSyndicate(syndicate.getId());
                filteredEmployee.setUnionFee(formattedUnionFee);

                this.employees.put(employeeId, filteredEmployee);
            }
        } else {
            throw new Exception("Atributo nao existe.");
        }
    }

    public void lancaTaxaServico(String syndicateId, String date, String value) throws Exception {
        if(syndicateId.isEmpty())
            throw new Exception("Identificacao do membro nao pode ser nula.");

        LocalDate formattedDate = this.dateVerify(date, "", true);
        double formattedValue = Double.parseDouble(value.replace(",", "."));

        if(formattedValue <= 0)
            throw new Exception("Valor deve ser positivo.");

        Syndicate syndicate = getSyndicateById(syndicateId);

        if(syndicate == null)
            throw new Exception("Membro nao existe.");

        String newId = "unionFee_id_" + (syndicate.getUnionFeeList() == null || syndicate.getUnionFeeList().isEmpty() ? 0 : syndicate.getUnionFeeList().size());

        UnionFee newUnionFee = new UnionFee(newId, formattedDate, formattedValue);
        syndicate.addNewUnionFee(newUnionFee);
    }

    public String getTaxasServico(String employeeId, String startDate, String finishDate) throws Exception {
        if(employeeId.isEmpty())
            throw new Exception("Identificacao do membro nao pode ser nula.");

        LocalDate formattedStartDate = this.dateVerify(startDate, "start", true);
        LocalDate formattedFinishDate = this.dateVerify(finishDate, "finish", true);

        if(formattedStartDate.isAfter(formattedFinishDate)) {
            throw new Exception("Data inicial nao pode ser posterior aa data final.");
        }

        Employee filteredEmployee = getEmployeeById(employeeId);

        if(filteredEmployee.getId().isEmpty())
            throw new Exception("Membro nao existe.");

        if(!filteredEmployee.getUnionized())
            throw new Exception("Empregado nao eh sindicalizado.");

        String linkedSyndicateId = filteredEmployee.getLinkedSyndicateId();

        if(linkedSyndicateId == null)
            return "0,00";

        Syndicate syndicate = getSyndicateById(linkedSyndicateId);

        if(syndicate.getId().isEmpty())
            throw new Exception("Membro nao existe.");

        double totalValue = 0;

        List<UnionFee> unionFeeList = syndicate.getUnionFeeList();

        if(unionFeeList.isEmpty())
            return "0,00";

        for (UnionFee unionFee : unionFeeList) {
            LocalDate date = unionFee.getDate();

            if(date.isAfter(formattedStartDate) || date.isEqual(formattedStartDate)) {
                if(date.isBefore(formattedFinishDate)) {
                    totalValue += unionFee.getValue();
                }
            }
        }

        return String.format("%.2f", totalValue).replace('.', ',');
    }

    public void alteraEmpregado(String employeeId, String property, String value) throws Exception {
        if(employeeId == null || employeeId.isEmpty())
            throw new Exception("Identificacao do empregado nao pode ser nula.");

        if(property.equals("sindicalizado")) {
            Employee filteredEmployee = getEmployeeById(employeeId);

            if(!value.equals("false") && !value.equals("true"))
                throw new Exception("Valor deve ser true ou false.");

            filteredEmployee.setUnionized(Boolean.parseBoolean(value));
        } else {
            this.alteraEmpregado(employeeId, property, value, "", "0");
        }
    }

    public void removerEmpregado(String employeeId) throws Exception {
        if(employeeId.isEmpty()) throw new Exception("Identificacao do empregado nao pode ser nula.");

        Employee employeeToRemove = getEmployeeById(employeeId);

        this.employees.remove(employeeId);
    }

    private boolean saveSyndicateInDatabase() throws Exception {
        try {
            this.xmlSyndicateDb.createAndSaveSyndicateDocument(this.syndicates);
            this.syndicates = this.xmlSyndicateDb.readAndGetSyndicateFile(this.employees);
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean saveEmployeeInDatabase() throws Exception {
        try {
            this.xmlDatabase.createAndSaveEmployeeDocument(this.employees);
            this.employees = this.xmlDatabase.readAndGetEmployeeFile();
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void encerrarSistema() throws Exception {
        this.saveSyndicateInDatabase();
        this.saveEmployeeInDatabase();

        // Salvar XML apenas aqui
    }
}
