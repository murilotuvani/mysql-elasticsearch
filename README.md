# mysql-elasticsearch

Este projeto eh um ensaio para analisar se a ferramente ElasticSearch serve para a finalidade desejada que é a busca de produtos em um site de e-commerce.
No caso seguiremos com a versão 6.8 embora esta não seja a mais recente pelo fato de ser a versão suportada pelo SpringBoot Data.

[Tutorial básico do ElasticSearch] (https://www.elastic.co/guide/en/elasticsearch/reference/6.8/index.html)

[Tutorial sobre o uso do "Java High Level REST Client"] (https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high-compatibility.html)

[Uso de sinônimos no ElasticSearch] (https://www.elastic.co/pt/blog/boosting-the-power-of-elasticsearch-with-synonyms)

Comandos:

Informaçõs sobre os índices: http://localhost:9200/_cat/indices?v

Apenas os nomes dos índices: curl http://localhost:9200/_aliases?pretty=true


docker network create somenetwork
docker run -d --name elasticsearch -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:7.6.0


docker rm -f elasticsearch
docker imagens
docker rmi




docker run -d --name elasticsearch --net somenetwork -v C:\Users\Murilo\Documents\docker\elasticsearch\backup:/backup/ -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:6.8.1

docker rm -f elasticsearch
docker run -d --name elasticsearch --net somenetwork -v /Users/murilotuvani/docker/elasticsearch/backup:/backup/ -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:6.8.1


curl -X GET "localhost:9200/?pretty" -H 'Content-Type: application/json'

Mostra os índices
curl -X GET "localhost:9200/_cat/indices?pretty" -H 'Content-Type: application/json'

Detalha o índice "itens"
curl -X GET "localhost:9200/itens?pretty" -H 'Content-Type: application/json'

Pesquisa por arruela
curl -X GET "localhost:9200/itens/item/_search?q=arruela" -H 'Content-Type: application/json'

Pega o item com id=100
curl -X GET "localhost:9200/itens/item/100?pretty" -H 'Content-Type: application/json'


curl -H "Content-Type: application/json" http://localhost:9200/produtos/_analyze?text=Arruela

curl -X GET "localhost:9200/_analyze?pretty" -H 'Content-Type: application/json' -d '{
  "analyzer" : "standard",
  "text" : "Arruela"
}'

curl  -H 'Content-Type: application/json' -X GET  "localhost:9200/idx_produto/produto/_search?q=junta+gol"
curl  -H 'Content-Type: application/json' -X GET  "localhost:9200/idx_produto/produto/_search?q=junta+golf"