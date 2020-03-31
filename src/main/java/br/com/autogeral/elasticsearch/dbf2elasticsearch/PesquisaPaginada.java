package br.com.autogeral.elasticsearch.dbf2elasticsearch;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

/**
 * 20/03/2020 20:01:41
 *
 * @see https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high-search-scroll.html
 * @author murilo
 */
public class PesquisaPaginada {

    private static final String INDEX = "itens";
    private static final String TYPE = "item";

    private static final RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                    new HttpHost("localhost", 9200, "http")
            ));

    public static void main(String args[]) {
        try {
            PesquisaPaginada s = new PesquisaPaginada();
            s.pesquisar();
            client.close();
        } catch (IOException ex) {
            Logger.getLogger(PesquisaPaginada.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void pesquisar() {
        try {
            final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
            SearchRequest searchRequest = new SearchRequest(INDEX);
            searchRequest.scroll(scroll);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(matchQuery("descricao", "tampa do reservatório de oleo do gol g4"));
            // Limitando o numero de documentos retornados
            // @see https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high-search.html
            int documentos = 10;
            searchSourceBuilder.from(0);
            searchSourceBuilder.size(documentos);
            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            String scrollId = searchResponse.getScrollId();
            
            SearchHits hits = searchResponse.getHits();
            long totalHits = hits.getTotalHits();
            float maxScore = hits.getMaxScore();
            System.out.println("Totais de hits : " + totalHits);
            System.out.println("Maior score    : " + maxScore);
            
            SearchHit[] searchHits = hits.getHits();

            while (searchHits != null && searchHits.length > 0) {

                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(scroll);
                searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
                scrollId = searchResponse.getScrollId();
                //searchHits = searchResponse.getHits().getHits();
                for (SearchHit hit : searchResponse.getHits().getHits()) {
                    System.out.println("Type     : " + hit.getType() + "\t Scole : " + hit.getScore());
                    System.out.println("Document : " + hit.getSourceAsString());
                }

            }

            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollId);
            ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            boolean succeeded = clearScrollResponse.isSucceeded();
        } catch (IOException ex) {
            Logger.getLogger(PesquisaPaginada.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
