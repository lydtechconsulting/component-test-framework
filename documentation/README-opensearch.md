# Opensearch

Enable Opensearch via the property `opensearch.enabled`. Opensearch is available on port `9200`.

_note that `elasticsearch.enabled` must be `false` for the opensearch to container to start_

## Related properties

| Property                             | Usage                                                           | Default       |
|--------------------------------------|-----------------------------------------------------------------|---------------|
| opensearch.enabled                   | Whether a Docker Opensearch container should be started.        | `false`       |
| opensearch.image.tag                 | The image tag of the Opensearch Docker container to use.        | `2.15.0`      |
| opensearch.password                  | The Opensearch password to use.                                 |               |
| opensearch.cluster.name              | The name of the Opensearch cluster.                             | `opensearch`  |
| opensearch.discovery.type            | Whether to form a single node or multi node Opensearch cluster. | `single-node` |
| opensearch.container.logging.enabled | Whether to output the Opensearch Docker logs to the console.    | `false`       |

The container base URL can be obtained using the `OpensearchClient`:

```
import dev.lydtech.component.framework.client.open.OpensearchCtfClient;

String baseUrl = OpensearchCtfClient.getInstance().getBaseUrl();
```

The `org.opensearch.client.opensearch` can be obtained which can then be used to query the dockerised
Opensearch. Note this method is overloaded, also taking a `JsonpMapper`.

```
OpenSearchClient osClient = OpensearchCtfClient.getInstance().getOpensearchClient();
        GetResponse<Item> getResponse = osClient.get(s -> s
            .index("item")
            .id(location), Item.class);
```
