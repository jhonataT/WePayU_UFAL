package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.models.Empregado;

import java.util.ArrayList;
import java.util.List;

public class Facade {

    List<Empregado> empregados;

    public void zerarSistema() {
        this.empregados = new ArrayList<>();
    }

    public void getAtributoEmpregado(String emp, String atributo) throws Exception {
        throw new Exception("Empregado nao existe.");
    }

    public void criarEmpregado(String nome, String endereco, String tipo, int salario){
        Empregado empregado = new Empregado(nome, endereco, tipo, salario);
        this.empregados.add(empregado);
    }


}
