package dev.lydtech.component.framework.client.debezium;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class DebeziumClient {

    private static DebeziumClient instance;
    private static final String CONNECTOR_PATH = "/connectors";
    private RequestSpecification requestSpec;

    private DebeziumClient(){
        String debeziumHost = Optional.ofNullable(System.getProperty("docker.host"))
                .orElse("localhost");
        String debeziumPort = Optional.ofNullable(System.getProperty("debezium.mapped.port"))
                .orElseThrow(() -> new RuntimeException("debezium.mapped.port property not found"));
        String baseUrl = "http://" + debeziumHost + ":" + debeziumPort;
        log.info("Debezium base URL is: " + baseUrl);
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .build();
    }

    public synchronized static DebeziumClient getInstance() {
        if(instance==null) {
            instance = new DebeziumClient();
        }
        return instance;
    }

    /**
     * Pass in file location relative to root of project.
     */
    public String createConnector(String jsonFile) {
        try(InputStream in = new FileInputStream(jsonFile)) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readValue(in, JsonNode.class);
            String jsonString = mapper.writeValueAsString(jsonNode);
            log.debug("Creating Kafka Connect with config: "+jsonString);
            return postRequest(jsonString, CONNECTOR_PATH, 201);
        } catch (Exception e) {
            // Note this returns a 409 conflict if the connector already exists.
            log.error("Failed to create Kafka Connect connector: " + jsonFile, e);
            throw new RuntimeException(e);
        }
    }

    public Integer deleteConnector(String connectorName) {
        try {
            return deleteRequest(CONNECTOR_PATH+"/"+connectorName);
        } catch (Exception e) {
            // Note this returns a 409 conflict if rebalancing at time of delete.
            log.error("Failed to delete Kafka Connect connector: " + connectorName, e);
            throw new RuntimeException(e);
        }
    }

    private String postRequest(String request, String path, Integer expectedResponse) {
        String id = RestAssured.given()
                .spec(requestSpec)
                .body(request)
                .contentType(ContentType.JSON)
                .post(path)
                .then()
                .statusCode(expectedResponse)
                .extract()
                .response()
                .body()
                .path("id");
        return id;
    }

    private Integer deleteRequest(String path) {
        return RestAssured.given()
                .spec(requestSpec)
                .delete(path)
                .getStatusCode();
    }
}
