package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.models.Employee;
import br.ufal.ic.p2.wepayu.utils.XMLManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Facade {

    private List<Employee> employees;
    private XMLManager xmlDatabase;

    private String[] typeOptions = { "horista", "assalariado", "comissionado" };
    private String[] employeeProperties = { "nome", "endereco", "tipo", "salario", "sindicalizado", "comissao" };

    public Facade() throws Exception {
        this.xmlDatabase = new XMLManager("employees");
        this.employees = this.xmlDatabase.readAndGetEmployeeFile();
    }

    public void zerarSistema() {
        this.employees = new ArrayList<>();
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
        Employee employee = new Employee(
            "id".concat(newId),
            name,
            address,
            type,
            newRemuneration,
            newCommission,
            false
        );

        this.employees.add(employee);
        saveEmployeeInDatabase();

        return employee.getId();
    }

    public String criarEmpregado(String name, String address, String type, String remuneration) throws Exception {
        if(type.equals("comissionado")) throw new Exception("Tipo nao aplicavel.");

        return criarEmpregado(name, address, type, remuneration, "0");
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
