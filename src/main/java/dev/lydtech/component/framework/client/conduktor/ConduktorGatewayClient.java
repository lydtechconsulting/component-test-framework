package dev.lydtech.component.framework.client.conduktor;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConduktorGatewayClient {
    private RequestSpecification requestSpec;
    private static ConduktorGatewayClient instance;


    private ConduktorGatewayClient(){
        String conduktorGatewayHost = Optional.ofNullable(System.getProperty("docker.host"))
                .orElse("localhost");
        String conduktorGatewayPort = Optional.ofNullable(System.getProperty("conduktorgateway.mapped.port"))
                .orElseThrow(() -> new RuntimeException("conduktorgateway.mapped.port property not found"));
        String baseUrl = "http://" + conduktorGatewayHost + ":" + conduktorGatewayPort;
        log.info("Conduktor Gateway base URL is: " + baseUrl);
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .build();
    }

    public synchronized static ConduktorGatewayClient getInstance() {
        if(instance==null) {
            instance = new ConduktorGatewayClient();
        }
        return instance;
    }

    public void injectChaos(BrokerErrorType... brokerErrorTypes) {
        String brokerErrorTypeJson = Arrays.stream(brokerErrorTypes)
                .map(BrokerErrorType::name)
                .collect(Collectors.joining("\", \""));
        String request = "{\"config\": {\"brokerIds\": [],\"duration\": 10000,\"durationUnit\": \"MILLISECONDS\",\"quietPeriod\": 1000,\"quietPeriodUnit\": \"MILLISECONDS\",\"minLatencyToAddInMilliseconds\": 6000,\"maxLatencyToAddInMilliseconds\": 7000,\"errors\": [\""+brokerErrorTypeJson+"\"]},\"direction\": \"REQUEST\",\"apiKeys\": \"PRODUCE\"}";

        log.info("Injecting chaos with request: "+request);
        RestAssured.given()
                .spec(requestSpec)
                .body(request)
                .auth()
                .basic("superUser", "superUser")
                .post("/tenant/passThroughTenant/feature/broken-broker")
                .then()
                .extract()
                .response()
                .then()
                .statusCode(200);
    }

    public void reset() {
        RestAssured.given()
                .spec(requestSpec)
                .auth()
                .basic("superUser", "superUser")
                .delete("/tenant/passThroughTenant/feature/broken-broker/apiKeys/PRODUCE/direction/REQUEST")
                .then()
                .extract()
                .response()
                .then()
                .statusCode(200);
    }
}
