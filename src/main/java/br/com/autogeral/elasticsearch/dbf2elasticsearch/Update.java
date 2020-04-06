package br.com.autogeral.elasticsearch.dbf2elasticsearch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

/**
 * 27/02/2020 19:19:36
 *
 * @author murilotuvani
 */
public class Update {

    private static final String INDEX = "itens";
    private static final String TYPE = "item";

    private Connection conn = null;
    private final String host = "localhost";
    private final String port = "3306";
    private final String database = "autogeral";
    private final String user = "root";
    private final String password = "root";
    private final RestHighLevelClient client;

    public Update() {
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")
                //new HttpHost("localhost", 9201, "http")));
                ));
    }

    private Connection getCnnnection() throws SQLException {
        if (this.conn == null || this.conn.isClosed()) {
            this.conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", user, password);
        }
        return conn;
    }

    public static void main(String args[]) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Update update = new Update();
            update.vendedores();
            update.produtos();
            update.close();

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void close() {
        try {
            this.client.close();
            this.conn.close();
        } catch (SQLException | IOException ex) {
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

    private void produtos() throws SQLException {
        ProdutoDao pd = new ProdutoDao(conn);
        long qtd = pd.quantidade();
        int itensPorPagina = 1000;
        int paginaAtual = 1;
        List<Produto> lista = null;
        do {
            lista = pd.listar(paginaAtual++, itensPorPagina);
            LongStream codigos = lista.stream().mapToInt(Produto::getCodigo).asLongStream();
            final StringBuilder sb = new StringBuilder();
            codigos.forEachOrdered(codigo -> {
                if (sb.length() == 0) {
                    sb.append(" WHERE CODIGO_PRODUTO IN (");
                } else {
                    sb.append(",");
                }
                sb.append(codigo);
            });

            final Map<Integer, Produto> mapaProdutosPorCodigo = lista.stream().collect(Collectors.toMap(Produto::getCodigo, Function.identity()));
            if (sb.length() > 0) {
                sb.append(")");
            }
            String where = sb.toString();
            adicionarImagens(lista, mapaProdutosPorCodigo, where);
            
            adicionarEstoques(lista, mapaProdutosPorCodigo, where.replace("CODIGO_PRODUTO", "PRODUTO_CODIGO"));
            if (lista != null && !lista.isEmpty()) {
                enviar(lista);
            }
        } while (lista != null && !lista.isEmpty());

    }

    private void enviar(List<Produto> lista) {
        final Gson gson = (new GsonBuilder()).setDateFormat("yyyy-MM-dd HH:mm:ss.SSS").setPrettyPrinting().create();
        if (true) {
            enviarBulk(gson, lista);
        } else {
            enviarIndividualmente(gson, lista);
        }
    }

    private void enviarBulk(Gson gson, List<Produto> lista) {
        BulkRequest bulkRequest = new BulkRequest();
        lista.forEach(p -> {
            String jsonString = gson.toJson(p);
            IndexRequest request = new IndexRequest(INDEX).type(TYPE)
                    .index(INDEX).source(jsonString, XContentType.JSON)
                    .id(Integer.toString(p.getCodigo())).opType(DocWriteRequest.OpType.CREATE);
            bulkRequest.add(request);
        });
        try {
            client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private void enviarIndividualmente(Gson gson, List<Produto> lista) {
        lista.forEach(p -> {
            try {
                String jsonString = gson.toJson(p);
                IndexRequest request = new IndexRequest(INDEX).type(TYPE)
                        .index(INDEX).source(jsonString, XContentType.JSON)
                        .id(Integer.toString(p.getCodigo())).opType(DocWriteRequest.OpType.CREATE);

                IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

                String index = indexResponse.getIndex();
                String id = indexResponse.getId();
                if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
                    System.out.println("Index " + index + " criado com id : " + id);
                } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                    System.out.println("Index " + index + " atualizado com id : " + id);
                }
                ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
                if (shardInfo.getTotal() != shardInfo.getSuccessful()) {

                }
                if (shardInfo.getFailed() > 0) {
                    for (ReplicationResponse.ShardInfo.Failure failure
                            : shardInfo.getFailures()) {
                        String reason = failure.reason();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        });
    }

    private void adicionarImagens(List<Produto> lista, Map<Integer, Produto> mapaProdutosPorCodigo, String where) throws SQLException {
        ProdutoImagemDao pid = new ProdutoImagemDao(getCnnnection());
        List<ProdutoImagem> imagens = pid.listar(where);
        Map<Integer, List<ProdutoImagem>> mapaImagensPorProduto = imagens.stream()
                .collect(Collectors.groupingBy(ProdutoImagem::getProdutoCodigo, Collectors.toList()));
        mapaImagensPorProduto.forEach((codigo, listaImagens) -> {
            if (mapaProdutosPorCodigo.containsKey(codigo)) {
                mapaProdutosPorCodigo.get(codigo).setImagens(listaImagens);
            }
        });
    }

    private void adicionarEstoques(List<Produto> lista, Map<Integer, Produto> mapaProdutosPorCodigo, String where) throws SQLException {
        ProdutoEstoqueDao pid = new ProdutoEstoqueDao(getCnnnection());
        List<ProdutoEstoque> imagens = pid.listar(where);
        Map<Integer, List<ProdutoEstoque>> mapaEstoquesPorProduto = imagens.stream()
                .collect(Collectors.groupingBy(ProdutoEstoque::getProdutoCodigo, Collectors.toList()));
        mapaEstoquesPorProduto.forEach((codigo, listaEstoques) -> {
            if (mapaProdutosPorCodigo.containsKey(codigo)) {
                mapaProdutosPorCodigo.get(codigo).setEstoques(listaEstoques);
            }
        });
    }
}