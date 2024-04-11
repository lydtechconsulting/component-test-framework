package dev.lydtech.component.framework.client.wiremock;

import java.io.InputStream;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lydtech.component.framework.mapper.JsonMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

import static org.hamcrest.Matchers.equalTo;

@Slf4j
public class WiremockClient {
    private ObjectMapper mapper;
    private RequestSpecification requestSpec;

    private static final String MAPPINGS_PATH = "/__admin/mappings";
    private static final String RESET_MAPPINGS_PATH = "/__admin/mappings/reset";
    private static final String DELETE_ALL_REQUESTS_PATH = "/__admin/requests";
    private static final String COUNT_REQUESTS_PATH = "/__admin/requests/count";
    private static final String FIND_REQUESTS_PATH = "/__admin/requests/find";

    private static WiremockClient instance;

    private WiremockClient(){
        String wiremockHost = Optional.ofNullable(System.getProperty("docker.host")).orElse("localhost");
        String wiremockPort = Optional.ofNullable(System.getProperty("wiremock.mapped.port"))
                .orElseThrow(() -> new RuntimeException("wiremock.mapped.port property not found"));
        String baseUrl = "http://" + wiremockHost + ":" + wiremockPort;
        log.info("Wiremock base URL is: " + baseUrl);
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .build();
        mapper = new ObjectMapper();
    }

    public synchronized static WiremockClient getInstance() {
        if(instance==null) {
            instance = new WiremockClient();
        }
        return instance;
    }

    public void resetMappings() {
        RestAssured.given()
                .spec(requestSpec)
                .post(RESET_MAPPINGS_PATH)
                .then()
                .assertThat()
                .statusCode(200);
    }

    public String postMappingFile(String jsonFile) {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(jsonFile)) {
            JsonNode jsonNode = mapper.readValue(in, JsonNode.class);
            String jsonString = mapper.writeValueAsString(jsonNode);
            return postMapping(jsonString);
        } catch (Exception e) {
            log.error("Failed to post wiremock mapping file: " + jsonFile, e);
            throw new RuntimeException(e);
        }
    }

    public String postMapping(String request) {
        log.debug("Posting wiremock mapping: "+request);
        return RestAssured.given()
                .spec(requestSpec)
                .body(request)
                .post(MAPPINGS_PATH)
                .then()
                .statusCode(201)
                .extract()
                .response()
                .body()
                .path("id");
    }

    public void deleteAllRequestsMappings() {
        RestAssured.given()
                .spec(requestSpec)
                .delete(DELETE_ALL_REQUESTS_PATH)
                .then()
                .assertThat()
                .statusCode(200);
    }

    public void countMatchingRequests(RequestCriteria request, Integer expectedCount) {
        countMatchingRequests(JsonMapper.writeToJson(request), expectedCount);
    }

    public void countMatchingRequests(String request, Integer expectedCount) {
        RestAssured.given()
            .spec(requestSpec)
            .body(request)
            .post(COUNT_REQUESTS_PATH)
            .then()
            .statusCode(200)
            .assertThat()
            .body("count", equalTo(expectedCount));
    }

    public Response findMatchingRequests(RequestCriteria request) {
        return findMatchingRequests(JsonMapper.writeToJson(request));
    }

    public Response findMatchingRequests(String request) {
        log.info("Finding matching requests for: {}", request);
        return RestAssured.given()
                .spec(requestSpec)
                .body(request)
                .post(FIND_REQUESTS_PATH)
                .then()
                .statusCode(200)
                .extract()
                .response();
    }
}
