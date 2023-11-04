package dev.lydtech.component.framework.client.elastic;

import java.util.Optional;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import io.restassured.builder.RequestSpecBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

@Slf4j
public class ElasticsearchCtfClient {

    private static ElasticsearchCtfClient instance;
    private static String baseUrl;

    private ElasticsearchCtfClient(){
        String host = Optional.ofNullable(System.getProperty("docker.host"))
                .orElse("localhost");
        String port = Optional.ofNullable(System.getProperty("elasticsearch.mapped.port"))
                .orElseThrow(() -> new RuntimeException("elasticsearch.mapped.port property not found"));
        baseUrl = "http://" + host + ":" + port;
        log.info("Elasticsearch base URL is: " + baseUrl);
    }

    public synchronized static ElasticsearchCtfClient getInstance() {
        if(instance==null) {
            instance = new ElasticsearchCtfClient();
        }
        return instance;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public ElasticsearchClient getElasticsearchClient() {
        return getElasticsearchClient(new JacksonJsonpMapper());
    }

    public ElasticsearchClient getElasticsearchClient(JsonpMapper mapper) {
        RestClient restClient = RestClient
                .builder(HttpHost.create(baseUrl))
                .build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, mapper);
        return new ElasticsearchClient(transport);
    }
}
