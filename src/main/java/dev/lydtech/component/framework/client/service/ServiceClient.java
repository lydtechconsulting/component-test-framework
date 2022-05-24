package dev.lydtech.component.framework.client.service;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ServiceClient {

    private static String baseUrl;
    private static ServiceClient instance;

    private ServiceClient(){
        String serviceHost = Optional.ofNullable(System.getProperty("service.host"))
                .orElse("localhost");
        String servicePort = Optional.ofNullable(System.getProperty("service.mapped.port"))
                .orElseThrow(() -> new RuntimeException("service.mapped.port property not found"));
        baseUrl = "http://" + serviceHost + ":" + servicePort;
        log.info("Service base URL is: " + baseUrl);
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
}
