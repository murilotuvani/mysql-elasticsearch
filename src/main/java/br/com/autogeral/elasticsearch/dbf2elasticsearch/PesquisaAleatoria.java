package br.com.autogeral.elasticsearch.dbf2elasticsearch;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;

/**
 * 15/04/2020 21:51:23
 * @see https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-compound-queries.html#java-query-dsl-function-score-query
 * @author murilotuvani
 */
public class PesquisaAleatoria {

    private static final String INDEX = "itens";
    private static final String TYPE = "item";
    

    private static final RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                    new HttpHost("localhost", 9200, "http")
            ));

    public static void main(String args[]) {
        try {
            PesquisaAleatoria s = new PesquisaAleatoria();
            s.pesquisar();
            client.close();
        } catch (IOException ex) {
            Logger.getLogger(PesquisaPaginada.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void pesquisar() {
//        FilterFunctionBuilder[] functions = {
//            new FunctionScoreQueryBuilder.FilterFunctionBuilder(
//            randomFunction()),
//            new FunctionScoreQueryBuilder.FilterFunctionBuilder(
//            exponentialDecayFunction("age", 0L, 1L))
//        };
//        QueryBuilders.functionScoreQuery()
    }

}
