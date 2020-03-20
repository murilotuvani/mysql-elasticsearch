package br.com.autogeral.elasticsearch.dbf2elasticsearch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 27/02/2020 23:46:26
 * @author murilotuvani
 */
public class ProdutoImagemDao extends Dao {

    private final Connection conn;

    public ProdutoImagemDao(Connection conn) {
        this.conn = conn;
    }

    public List<ProdutoImagem> listar(String where) throws SQLException {
        String query = "SELECT CODIGO_PRODUTO,LINK,OBJETIVO\n"
                     + "  FROM PRODUTOS_IMAGENS\n"
                     + where;
        System.out.println(query);

        List<ProdutoImagem> lista = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                lista.add(lerRegistro(rs));
            }
        }
        return lista;
    }

    private ProdutoImagem lerRegistro(ResultSet rs) throws SQLException {
        ProdutoImagem p = new ProdutoImagem();
        p.setProdutoCodigo(rs.getInt("CODIGO_PRODUTO"));
        p.setLink(rs.getString("LINK"));
        p.setObjetivo(rs.getString("OBJETIVO"));
        return p;
    }
}

