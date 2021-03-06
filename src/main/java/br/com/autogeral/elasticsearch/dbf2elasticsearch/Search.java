package br.com.autogeral.elasticsearch.dbf2elasticsearch;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

/**
 * 20/03/2020 00:14:47
 *
 * @author murilotuvani
 */
public class Search {

    private static final String INDEX = "itens";
    private static final String TYPE = "item";

    private final RestHighLevelClient client;

    public static void main(String args[]) {
        Search s = new Search();
        s.pesquisar();
    }

    public Search() {
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
                    
                    .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
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

    private void pesquisar() {
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            //sourceBuilder.query(QueryBuilders.termQuery("user", "kimchy"));
            sourceBuilder.query(QueryBuilders.termQuery("descricao", "Golf"));
            // posicao inicial que ira pesquisar
            sourceBuilder.from(0);
            // quantidadde de elementos a serem retornados pela pesquisa
            sourceBuilder.size(5);
            sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

            SearchRequest searchRequest = new SearchRequest();
            searchRequest.indices(INDEX);
            searchRequest.source(sourceBuilder);

            QueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("descricao", "Golf")
                    .fuzziness(Fuzziness.AUTO)
                    .prefixLength(3)
                    .maxExpansions(10);
            

//Para analisar as queries
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.profile(true);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            RestStatus status = searchResponse.status();
            System.out.println("Status         : " + status.name());
            TimeValue took = searchResponse.getTook();
            Boolean terminatedEarly = searchResponse.isTerminatedEarly();
            boolean timedOut = searchResponse.isTimedOut();

            SearchHits hits = searchResponse.getHits();
            long totalHits = hits.getTotalHits().value;
            float maxScore = hits.getMaxScore();
            System.out.println("Totais de hits : " + totalHits);
            System.out.println("Maior score    : " + maxScore);

            for (SearchHit hit : hits.getHits()) {
                System.out.println("hit type   : " + hit.getType());
                System.out.println("hit source : " + hit.getSourceAsString());
//                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
//                HighlightField highlight = highlightFields.get("title");
//                Text[] fragments = highlight.fragments();
//                String fragmentString = fragments[0].string();
            }

        } catch (IOException ex) {
            Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
