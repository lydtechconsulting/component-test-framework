package dev.lydtech.component.framework.client.opensearch;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.JsonpMapper;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.rest_client.RestClientTransport;

@Slf4j
public class OpensearchCtfClient {

    private static OpensearchCtfClient instance;
    private static String baseUrl;

    private OpensearchCtfClient() {
        String host = Optional.ofNullable(System.getProperty("docker.host"))
            .orElse("localhost");
        String port = Optional.ofNullable(System.getProperty("elasticsearch.mapped.port"))
            .orElseThrow(() -> new RuntimeException("opensearch.mapped.port property not found"));
        baseUrl = "http://" + host + ":" + port;
        log.info("Opensearch base URL is: " + baseUrl);
    }

    public synchronized static OpensearchCtfClient getInstance() {
        if(instance==null) {
            instance = new OpensearchCtfClient();
        }
        return instance;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public OpenSearchClient getOpensearchClient() {
        return getOpensearchClient(new JacksonJsonpMapper());
    }


    public OpenSearchClient getOpensearchClient(JsonpMapper mapper) {
        RestClient restClient = RestClient.builder(HttpHost.create(baseUrl)).build();
        OpenSearchTransport transport = new RestClientTransport(restClient, mapper);
        return new OpenSearchClient(transport);
    }

}