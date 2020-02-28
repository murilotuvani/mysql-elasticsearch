package br.com.autogeral.elasticsearch.dbf2elasticsearch;

import java.util.ArrayList;
import java.util.List;

/**
 * 27/02/2020 23:36:29
 *
 * @author murilotuvani
 */
public class Produto {

    private int codigo;
    private long codigoX;
    private String codigoSequencia;
    private String codigoFabricante;
    private String codigoOriginal;
    private String codigoBarras;
    private Integer etiquetaCodigo;
    private double disponivel;
    private double preco;
    private double pesoLiquido;
    private String descricao;
    private String descricaoSite;
    private String descricaoLonga;
    private String marca;
    private List<ProdutoImagem> imagens = new ArrayList<>();

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public long getCodigoX() {
        return codigoX;
    }

    public void setCodigoX(long codigoX) {
        this.codigoX = codigoX;
    }

    public String getCodigoSequencia() {
        return codigoSequencia;
    }

    public void setCodigoSequencia(String codigoSequencia) {
        this.codigoSequencia = codigoSequencia;
    }

    public String getCodigoFabricante() {
        return codigoFabricante;
    }

    public void setCodigoFabricante(String codigoFabricante) {
        this.codigoFabricante = codigoFabricante;
    }

    public String getCodigoOriginal() {
        return codigoOriginal;
    }

    public void setCodigoOriginal(String codigoOriginal) {
        this.codigoOriginal = codigoOriginal;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public Integer getEtiquetaCodigo() {
        return etiquetaCodigo;
    }

    public void setEtiquetaCodigo(Integer etiquetaCodigo) {
        this.etiquetaCodigo = etiquetaCodigo;
    }

    public double getDisponivel() {
        return disponivel;
    }

    public void setDisponivel(double disponivel) {
        this.disponivel = disponivel;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public double getPesoLiquido() {
        return pesoLiquido;
    }

    public void setPesoLiquido(double pesoLiquido) {
        this.pesoLiquido = pesoLiquido;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricaoSite() {
        return descricaoSite;
    }

    public void setDescricaoSite(String descricaoSite) {
        this.descricaoSite = descricaoSite;
    }

    public String getDescricaoLonga() {
        return descricaoLonga;
    }

    public void setDescricaoLonga(String descricaoLonga) {
        this.descricaoLonga = descricaoLonga;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public List<ProdutoImagem> getImagens() {
        return imagens;
    }

    public void setImagens(List<ProdutoImagem> imagens) {
        this.imagens = imagens;
    }

}
