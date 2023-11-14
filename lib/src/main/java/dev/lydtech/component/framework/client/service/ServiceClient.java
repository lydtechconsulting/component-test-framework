package dev.lydtech.component.framework.client.service;

import java.util.Optional;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ServiceClient {

    private String baseUrl;
    private RequestSpecification requestSpec;
    private static ServiceClient instance;

    private ServiceClient(){
        String serviceHost = Optional.ofNullable(System.getProperty("docker.host"))
                .orElse("localhost");
        String servicePort = Optional.ofNullable(System.getProperty("service.mapped.port"))
                .orElseThrow(() -> new RuntimeException("service.mapped.port property not found"));
        baseUrl = "http://" + serviceHost + ":" + servicePort;
        log.info("Service base URL is: " + baseUrl);
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .build();
    }

    public synchronized static ServiceClient getInstance() {
        if(instance==null) {
            instance = new ServiceClient();
        }
        return instance;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public RequestSpecification getRequestSpecification() {
        return requestSpec;
    }
}
