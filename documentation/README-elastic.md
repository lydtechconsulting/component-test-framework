# Elasticsearch

Enable Elasticsearch via the property `elasticsearch.enabled`.  Elasticsearch is available on port `9200`.

## Related properties
| Property                                        | Usage                                                                                                                                                                                                                                                                                                                                                                           | Default                            |
|-------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| elasticsearch.enabled                           | Whether a Docker Elasticsearch container should be started.                                                                                                                                                                                                                                                                                                                     | `false`                            |
| elasticsearch.image.tag                         | The image tag of the Elasticsearch Docker container to use.                                                                                                                                                                                                                                                                                                                     | `8.10.4`                           |
| elasticsearch.password                          | The Elasticsearch password to use.                                                                                                                                                                                                                                                                                                                                              |                                    |
| elasticsearch.cluster.name                      | The name of the Elasticsearch cluster.                                                                                                                                                                                                                                                                                                                                          | `elasticsearch`                    |
| elasticsearch.discovery.type                    | Whether to form a single node or multi node Elasticsearch cluster.                                                                                                                                                                                                                                                                                                              | `single-node`                      |
| elasticsearch.container.logging.enabled         | Whether to output the Elasticsearch Docker logs to the console.                                                                                                                                                                                                                                                                                                                 | `false`                            |


The container base URL can be obtained using the `ElasticsearchClient`:

```
import dev.lydtech.component.framework.client.elastic.ElasticsearchCtfClient;

String baseUrl = ElasticsearchCtfClient.getInstance().getBaseUrl();
```

The `co.elastic.clients.elasticsearch.ElasticsearchClient` can be obtained which can then be used to query the dockerised Elasticsearch.  Note this method is overloaded, also taking a `JsonpMapper`.
```
ElasticsearchClient esClient = ElasticsearchCtfClient.getInstance().getElasticsearchClient();
GetResponse<Item> getResponse = esClient.get(s -> s
    .index("item")
    .id(location), Item.class);
```
