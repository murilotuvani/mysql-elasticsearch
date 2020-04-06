package br.com.autogeral.elasticsearch.dbf2elasticsearch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 05/04/2020 19:41:39
 *
 * @author murilotuvani
 */
public class ProdutoEstoqueDao {

    private final Connection conn;

    public ProdutoEstoqueDao(Connection conn) {
        this.conn = conn;
    }

    public List<ProdutoEstoque> listar(String where) throws SQLException {
        String query = "SELECT PRODUTO_CODIGO,LOJA,DISPONIVEL\n"
                + "  FROM PRODUTO_ESTOQUE\n"
                + where;
        System.out.println(query);

        List<ProdutoEstoque> lista = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                lista.add(lerRegistro(rs));
            }
        }
        return lista;
    }

    private ProdutoEstoque lerRegistro(ResultSet rs) throws SQLException {
        ProdutoEstoque p = new ProdutoEstoque();
        p.setProdutoCodigo(rs.getInt("PRODUTO_CODIGO"));
        p.setLoja(rs.getInt("LOJA"));
        p.setDisponivel(rs.getBigDecimal("DISPONIVEL"));
        return p;
    }

}
