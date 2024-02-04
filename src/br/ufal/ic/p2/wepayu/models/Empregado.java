package br.ufal.ic.p2.wepayu.models;

public class Empregado {
    private String nome;
    private String endere�o;
    private String tipo;
    private int salario;

    public Empregado(String nome, String endere�o, String tipo, int salario) {
        this.nome = nome;
        this.endere�o = endere�o;
        this.tipo = tipo;
        this.salario = salario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndere�o() {
        return endere�o;
    }

    public void setEndere�o(String endere�o) {
        this.endere�o = endere�o;
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
