package br.com.autogeral.elasticsearch.dbf2elasticsearch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 27/02/2020 19:19:36
 *
 * @author murilotuvani
 */
public class Update {

    private Connection conn = null;
    private String host = "localhost";
    private String port = "3306";
    private String database = "autogeral";
    private String user = "root";
    private String password = "root";

    private Connection getCnnnection() throws SQLException {
        if (this.conn == null || this.conn.isClosed()) {
            this.conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
        }
        return conn;
    }

    public static void main(String args[]) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Update update = new Update();
            update.vendedores();

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void vendedores() throws SQLException {
        Statement stmt = getCnnnection().createStatement();
        ResultSet rs = stmt.executeQuery("select DESCRICAO from vendedores_dbf");
        List<Vendedor> list = new ArrayList<>();

        while (rs.next()) {
            Vendedor v = new Vendedor();
            String nome = rs.getString("DESCRICAO");
            v.setNome(nome);
            list.add(v);
        }

        list.forEach(v -> {
            System.out.println(v);
        });
    }

}
