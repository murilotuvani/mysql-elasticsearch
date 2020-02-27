package br.com.autogeral.elasticsearch.dbf2elasticsearch;

/**
 * 27/02/2020 20:41:44
 *
 * @author murilotuvani
 */
public class Vendedor {

    private String nome;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Vendedor{" + "nome=" + nome + '}';
    }

}
