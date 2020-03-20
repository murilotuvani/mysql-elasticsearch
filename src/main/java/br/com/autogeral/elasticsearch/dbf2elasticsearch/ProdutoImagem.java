package br.com.autogeral.elasticsearch.dbf2elasticsearch;

/**
 * 27/02/2020 23:40:29
 * @author murilotuvani
 */
public class ProdutoImagem {
    
    private transient int produtoCodigo;
    private String objetivo;
    private String link;

    public int getProdutoCodigo() {
        return produtoCodigo;
    }

    public void setProdutoCodigo(int produtoCodigo) {
        this.produtoCodigo = produtoCodigo;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }    

}
