package br.com.autogeral.elasticsearch.dbf2elasticsearch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 27/02/2020 23:43:24
 *
 * @author murilotuvani
 */
public class ProdutoDao extends Dao {

    private final Connection conn;

    public ProdutoDao(Connection conn) {
        this.conn = conn;
    }

    public long quantidade() throws SQLException {
        long quantidade = 0;
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("select count(1) from produtos_dbf;")) {
            if (rs.next()) {
                quantidade = rs.getLong(1);
            }
        }
        return quantidade;
    }

    long quantidadeAlteradaHoje() throws SQLException {
        long quantidade = 0;
        String query = "SELECT COUNT(A.CODIGO) \n"
                     + "  FROM PRODUTOS_DBF A JOIN PRODUTO_ESTOQUE_GLOBAL B ON A.CODIGO=B.PRODUTO_CODIGO\n"
                     + " WHERE (DATE(A.CADASTRO) = CURDATE()\n"
                     + "    OR  DATE(A.ALTERACAO)= CURDATE()\n"
                     + "    OR  DATE(B.CADASTRO) = CURDATE()\n"
                     + "    OR  DATE(B.ALTERADO) = CURDATE())\n"
                     + "   AND A.FINALIDADE_CODIGO=1";
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                quantidade = rs.getLong(1);
            }
        }
        return quantidade;
    }

    List<Produto> listarAlteradosHoje(int pagina, int registros) throws SQLException {
        int registroInicial = (pagina - 1) * registros;
        String query = "SELECT P.CODIGO,P.CODIGO_X,\n"
                + "       P.CODIGO_SEQUENCIA,P.CODIGO_FABRICANTE,\n"
                + "       P.CODIGO_ORIGINAL,P.CODIGO_BARRAS,\n"
                + "       P.DESCRICAO,P.MARCA,\n"
                + "       P.PRECO_PRAZO,P.CLASSE_FISCAL,P.EXCESSAO_IPI,\n"
                + "       P.CEST,P.CODIGO_ANP,\n"
                + "       P.PESO_LIQUIDO,\n"
                + "       P.LARGURA_PRODUTO,P.ALTURA_PRODUTO,P.PROFUNDIDADE_PRODUTO,\n"
                + "       P.LARGURA_EMBALAGEM,P.ALTURA_EMBALAGEM,P.PROFUNDIDADE_EMBALAGEM,\n"
                + "       P.DESCRICAO_SITE,P.DESCRICAO_LONGA,\n"
                + "       G.DISPONIVEL\n"
                + "  FROM produtos_dbf P JOIN PRODUTO_ESTOQUE_GLOBAL G ON P.CODIGO=G.PRODUTO_CODIGO\n"
                + " WHERE (DATE(P.CADASTRO) = CURDATE()\n"
                + "    OR  DATE(P.ALTERACAO)= CURDATE()\n"
                + "    OR  DATE(G.CADASTRO) = CURDATE()\n"
                + "    OR  DATE(G.ALTERADO) = CURDATE())\n"
                + "   AND P.FINALIDADE_CODIGO=1\n"
                + " LIMIT " + registroInicial + "," + registros;
        return listar(query);
    }


    public List<Produto> listar(int pagina, int registros) throws SQLException {
        int registroInicial = (pagina - 1) * registros;
        String query = "SELECT P.CODIGO,P.CODIGO_X,\n"
                + "       P.CODIGO_SEQUENCIA,P.CODIGO_FABRICANTE,\n"
                + "       P.CODIGO_ORIGINAL,P.CODIGO_BARRAS,\n"
                + "       P.DESCRICAO,P.MARCA,\n"
                + "       P.PRECO_PRAZO,P.CLASSE_FISCAL,P.EXCESSAO_IPI,\n"
                + "       P.CEST,P.CODIGO_ANP,\n"
                + "       P.PESO_LIQUIDO,\n"
                + "       P.LARGURA_PRODUTO,P.ALTURA_PRODUTO,P.PROFUNDIDADE_PRODUTO,\n"
                + "       P.LARGURA_EMBALAGEM,P.ALTURA_EMBALAGEM,P.PROFUNDIDADE_EMBALAGEM,\n"
                + "       P.DESCRICAO_SITE,P.DESCRICAO_LONGA,\n"
                + "       G.DISPONIVEL\n"
                + "  FROM produtos_dbf P JOIN PRODUTO_ESTOQUE_GLOBAL G ON P.CODIGO=G.PRODUTO_CODIGO\n"
                + " WHERE P.FINALIDADE_CODIGO=1\n"
                + " LIMIT " + registroInicial + "," + registros;
        return listar(query);
    }

    private List<Produto> listar(String query) throws SQLException {
        System.out.println(query);

        List<Produto> lista = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                lista.add(lerRegistro(rs));
            }
        }
        return lista;
    }


    private Produto lerRegistro(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setCodigo(rs.getInt("CODIGO"));
        p.setCodigoX(rs.getInt("CODIGO_X"));
        p.setCodigoSequencia(rs.getString("CODIGO_SEQUENCIA"));
        p.setCodigoFabricante(getStringOrNull(rs, "CODIGO_FABRICANTE"));
        p.setCodigoOriginal(getStringOrNull(rs, "CODIGO_ORIGINAL"));
        p.setCodigoBarras(getStringOrNull(rs, "CODIGO_BARRAS"));
//		p.setCodigoBarrasDun(getStringOrNull(rs, "CODIGO_BARRAS_DUN"));
        p.setDescricao(rs.getString("DESCRICAO"));
        p.setMarca(rs.getString("MARCA"));
        p.setPreco(rs.getDouble("PRECO_PRAZO"));
        //p.setClasseFiscal(rs.getString("CLASSE_FISCAL"));
        //p.setExcessaoIpi(getIntOrNull(rs, "EXCESSAO_IPI"));
        //p.setCest(getIntOrNull(rs, "CEST"));
        //p.setCodigoAnp(getIntOrNull(rs, "CODIGO_ANP"));
        p.setPesoLiquido(rs.getDouble("PESO_LIQUIDO"));
        p.setLarguraProduto(getDoubleOrNull(rs, "LARGURA_PRODUTO"));
        p.setAlturaProduto(getDoubleOrNull(rs, "ALTURA_PRODUTO"));
        p.setProfundidadeProduto(getDoubleOrNull(rs, "PROFUNDIDADE_PRODUTO"));
        p.setLarguraEmbalagem(getDoubleOrNull(rs, "LARGURA_EMBALAGEM"));
        p.setAlturaEmbalagem(getDoubleOrNull(rs, "ALTURA_EMBALAGEM"));
        p.setProfundidadeEmbalagem(getDoubleOrNull(rs, "PROFUNDIDADE_EMBALAGEM"));
        p.setDescricaoSite(getStringOrNull(rs, "DESCRICAO_SITE"));
        p.setDescricaoLonga(getStringOrNull(rs, "DESCRICAO_LONGA"));
        p.setDisponivel(rs.getDouble("DISPONIVEL"));

        return p;
    }

}
