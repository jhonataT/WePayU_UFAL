package br.ufal.ic.p2.wepayu.models;

public class Empregado {
    private String nome;
    private String enderešo;
    private String tipo;
    private int salario;

    public Empregado(String nome, String enderešo, String tipo, int salario) {
        this.nome = nome;
        this.enderešo = enderešo;
        this.tipo = tipo;
        this.salario = salario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEnderešo() {
        return enderešo;
    }

    public void setEnderešo(String enderešo) {
        this.enderešo = enderešo;
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
