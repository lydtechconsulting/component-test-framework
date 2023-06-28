package dev.lydtech.component.framework.client.conduktor;

import java.util.Optional;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
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

    public void postMapping() {
        String request = "{\"config\": {\"brokerIds\": [],\"duration\": 6000,\"durationUnit\": \"MILLISECONDS\",\"quietPeriod\": 20000,\"quietPeriodUnit\": \"MILLISECONDS\",\"minLatencyToAddInMilliseconds\": 6000,\"maxLatencyToAddInMilliseconds\": 7000,\"errors\": [\"REQUEST_TIMED_OUT\", \"BROKER_NOT_AVAILABLE\", \"OFFSET_OUT_OF_RANGE\", \"NOT_ENOUGH_REPLICAS\", \"INVALID_REQUIRED_ACKS\"]},\"direction\": \"REQUEST\",\"apiKeys\": \"PRODUCE\"}";
log.info("*************** RG: Posting Conduktor Gateway mapping: "+request);
        Response response = RestAssured.given()
                .spec(requestSpec)
                .body(request)
                .post("/tenant/passThroughTenant/feature/broken-broker")
                .then()
                .extract()
                .response();

        log.info("*************** RG: statuscode: "+response.statusCode());
        log.info("*************** RG: response: "+response.asPrettyString());
        log.info("*************** RG: response body: "+response.body().prettyPrint());


//                .then()
//                .statusCode(200);
    }

}
