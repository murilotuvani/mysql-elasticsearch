package br.com.autogeral.elasticsearch.dbf2elasticsearch;

import java.math.BigDecimal;

/**
 * 05/04/2020 19:43:23
 * @author murilotuvani
 */
public class ProdutoEstoque {
    
    private transient int produtoCodigo;
    private int loja;
    private BigDecimal disponivel;

    public int getProdutoCodigo() {
        return produtoCodigo;
    }

    public void setProdutoCodigo(int produtoCodigo) {
        this.produtoCodigo = produtoCodigo;
    }

    public int getLoja() {
        return loja;
    }

    public void setLoja(int loja) {
        this.loja = loja;
    }

    public BigDecimal getDisponivel() {
        return disponivel;
    }

    public void setDisponivel(BigDecimal disponivel) {
        this.disponivel = disponivel;
    }
    
    

}
