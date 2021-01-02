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
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

/**
 * 27/02/2020 19:19:36
 *
 * @author murilotuvani
 */
public class Update {

    private static final String INDEX = "itens";

    private Connection conn = null;
    private final String host = "localhost";
    private final String port = "3309";
    private final String database = "autogeral";
    private final String user = "murilo.tuvani";
    private final String password = "@Aleggria7";
    private final RestHighLevelClient client;

    public Update() {
        boolean remote = Boolean.parseBoolean(System.getProperty("remote", "true"));
        
        if (remote) {
            final CredentialsProvider credentialsProvider
                    = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials("elastic", "kid30O2NShycZeci4jQH1xYm"));

            //9ae735505071462aa3c783169a4744ed.southamerica-east1.gcp.elastic-cloud.com:9243
            RestClientBuilder builder = RestClient.builder(
                    //new HttpHost("51b5229a8a15488cb513743b307925ef.southamerica-east1.gcp.elastic-cloud.com", 9243, "https"))
                    new HttpHost("146.148.50.104", 9243, "http"))
                    
                    .setHttpClientConfigCallback(new HttpClientConfigCallback() {
                        @Override
                        public HttpAsyncClientBuilder customizeHttpClient(
                                HttpAsyncClientBuilder httpClientBuilder) {
                            httpClientBuilder.disableAuthCaching();
                            return httpClientBuilder
                                    .setDefaultCredentialsProvider(credentialsProvider);
                        }
                    });
            client = new RestHighLevelClient(builder);
        } else {
            client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("localhost", 9200, "http")
                    //new HttpHost("localhost", 9201, "http")));
                    ));
        }
    }

    private Connection getCnnnection() throws SQLException {
        if (this.conn == null || this.conn.isClosed()) {
            this.conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", user, password);
        }
        return conn;
    }

    public static void main(String args[]) {
        try {
            //Class.forName("com.mysql.jdbc.Driver");
            Class.forName("com.mysql.cj.jdbc.Driver");
            
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
        int itensPorPagina = 5000;
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
            IndexRequest request = new IndexRequest(INDEX)
                    .index(INDEX).source(jsonString, XContentType.JSON)
                    .id(Integer.toString(p.getCodigo())).opType(DocWriteRequest.OpType.INDEX);
            bulkRequest.add(request);
        });
        try {
            BulkResponse result = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (result.hasFailures()) {
                System.out.println("Request failure : " + result.buildFailureMessage());
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private void enviarIndividualmente(Gson gson, List<Produto> lista) {
        lista.forEach(p -> {
            try {
                String jsonString = gson.toJson(p);
                IndexRequest request = new IndexRequest(INDEX)
                        .index(INDEX).source(jsonString, XContentType.JSON)
                        .id(Integer.toString(p.getCodigo())).opType(DocWriteRequest.OpType.INDEX);

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