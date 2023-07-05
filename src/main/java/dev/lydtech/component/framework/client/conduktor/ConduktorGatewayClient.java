package dev.lydtech.component.framework.client.conduktor;

import java.util.Arrays;
import java.util.Optional;

import dev.lydtech.component.framework.mapper.JsonMapper;
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
        String[] brokerErrorTypeStrings = Arrays.stream(brokerErrorTypes)
                .map(BrokerErrorType::name)
                .toArray(String[]::new);
        GatewayBrokenBrokerRequest gatewayBrokenBrokerRequest = GatewayBrokenBrokerRequest.builder()
                .config(GatewayBrokenBrokerRequest.Config.builder()
                        .duration(10000L)
                        .durationUnit("MILLISECONDS")
                        .quietPeriod(1000L)
                        .quietPeriodUnit("MILLISECONDS")
                        .minLatencyToAddInMilliseconds(6000L)
                        .maxLatencyToAddInMilliseconds(7000L)
                        .errors(brokerErrorTypeStrings)
                        .build())
                .direction("REQUEST")
                .apiKeys("PRODUCE")
                .build();
        String request = JsonMapper.writeToJson(gatewayBrokenBrokerRequest);
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
        log.info("Resetting Conduktor Gateway.");
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
