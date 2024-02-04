package br.ufal.ic.p2.wepayu.models;

public class Empregado {
    private String nome;
    private String endereço;
    private String tipo;
    private int salario;

    public Empregado(String nome, String endereço, String tipo, int salario) {
        this.nome = nome;
        this.endereço = endereço;
        this.tipo = tipo;
        this.salario = salario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereço() {
        return endereço;
    }

    public void setEndereço(String endereço) {
        this.endereço = endereço;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getSalario() {
        return salario;
    }

    public void setSalario(int salario) {
        this.salario = salario;
    }

}
