package dev.lydtech.component.framework.client.conduktor;

import java.util.EnumSet;
import java.util.Optional;

import dev.lydtech.component.framework.mapper.JsonMapper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import lombok.Builder;
import lombok.Getter;
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

    public void simulateBrokenBroker(int failureRatePercentage, BrokenBrokerErrorType brokenBrokerErrorType)  {
        GatewayInjectChaosRequest.ErrorMap.ErrorMapBuilder errorMapBuilder = GatewayInjectChaosRequest.ErrorMap.builder();
        if(brokenBrokerErrorType.getErrorMapType().equals("PRODUCE")) {
            errorMapBuilder.PRODUCE(brokenBrokerErrorType.name());
        } else {
            errorMapBuilder.FETCH(brokenBrokerErrorType.name());
        }

        injectChaos(ChaosType.BROKEN_BROKER, failureRatePercentage, errorMapBuilder.build(), null);
    }

    public void simulateLeaderElection(int failureRatePercentage)  {
        injectChaos(ChaosType.LEADER_ELECTION, failureRatePercentage, null, null);
    }

    public void simulateSlowBroker(int failureRatePercentage, int minLatencyMs, int maxLatencyMs)  {
        injectChaos(ChaosType.SLOW_BROKER, failureRatePercentage, null, Latency.builder().minLatencyMs(minLatencyMs).maxLatencyMs(maxLatencyMs).build());
    }

    @Getter
    @Builder
    private static class Latency {
        private int minLatencyMs;
        private int maxLatencyMs;
    }

    private void injectChaos(ChaosType chaosType, int failureRatePercentage, GatewayInjectChaosRequest.ErrorMap errorMap, Latency latency)  {
        GatewayInjectChaosRequest gatewayInjectChaosRequest = GatewayInjectChaosRequest.builder()
                .name(chaosType.name())
                .pluginClass("io.conduktor.gateway.interceptor.chaos."+chaosType.getPluginClassName())
                .priority(100)
                .config(GatewayInjectChaosRequest.Config.builder()
                        .rateInPercent(failureRatePercentage)
                        .errorMap(errorMap)
                        .minLatencyMs(latency!=null?latency.getMinLatencyMs():null)
                        .maxLatencyMs(latency!=null?latency.getMaxLatencyMs():null)
                        .build())
                .build();
        String request = JsonMapper.writeToJson(gatewayInjectChaosRequest);
        log.info("Injecting "+chaosType.name()+" chaos with request: "+request);

        RestAssured.given()
                .spec(requestSpec)
                .body(request)
                .contentType("application/json")
                .auth()
                .basic("admin", "conduktor")
                .post("/admin/interceptors/v1/vcluster/passthrough/interceptor/"+chaosType.getInterceptorPath())
                .then()
                .extract()
                .response()
                .then()
                .statusCode(201);
    }

    public void reset() {
        EnumSet.allOf(ChaosType.class)
                .forEach(chaosType -> reset(chaosType));
    }

    public int reset(ChaosType chaosType) {
        log.info("Resetting Conduktor Gateway for chaos type: "+chaosType.name());
        return RestAssured.given()
                .spec(requestSpec)
                .auth()
                .basic("admin", "conduktor")
                .delete("/admin/interceptors/v1/vcluster/passthrough/interceptor/"+chaosType.getInterceptorPath())
                .then()
                .extract()
                .response()
                .getStatusCode();
    }
}
