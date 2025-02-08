package dev.lydtech.component.framework.client.service;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public final class AdditionalContainerClient {

    private static AdditionalContainerClient instance;
    private final Map<String, String> baseUrls = new ConcurrentHashMap<>();
    private final Map<String, RequestSpecification> requestSpecs = new ConcurrentHashMap<>();

    private AdditionalContainerClient() {
        String additionalContainerHost = Optional.ofNullable(System.getProperty("docker.host"))
                .orElse("localhost");
        System.getProperties().stringPropertyNames().stream()
                .filter(key -> key.startsWith("additional.container."))
                .filter(key -> key.endsWith(".mapped.port"))
                .forEach(key -> {
                    String containerName = key.substring("additional.container.".length(), key.lastIndexOf(".mapped.port"));
                    String mappedPort = System.getProperty(key);

                    String baseUrl = "http://" + additionalContainerHost + ":" + mappedPort;
                    baseUrls.put(containerName, baseUrl);
                    requestSpecs.put(containerName, new RequestSpecBuilder().setBaseUri(baseUrl).build());

                    log.info("Additional container [{}] base URL is: {}", containerName, baseUrl);
                });

        if (baseUrls.isEmpty()) {
            throw new RuntimeException("No additional container mappings found in system properties.");
        }
    }

    public synchronized static AdditionalContainerClient getInstance() {
        if (instance == null) {
            instance = new AdditionalContainerClient();
        }
        return instance;
    }

    /**
     * Supports exact match e.g. 'ct-third-party-simulator' (that includes the prefix), and partial match e.g. 'ct-'third-party-simulator'
     * (without the prefix).
     */
    public String getBaseUrl(String containerName) {
        return Optional.ofNullable(baseUrls.get(containerName))
                .orElseGet(() -> findBaseUrlByPartialMatch(containerName));
    }

    /**
     * Supports exact match e.g. 'ct-third-party-simulator' (that includes the prefix), and partial match e.g. 'ct-'third-party-simulator'
     * (without the prefix).
     */
    public RequestSpecification getRequestSpecification(String containerName) {
        return Optional.ofNullable(requestSpecs.get(containerName))
                .orElseGet(() -> findRequestSpecificationByPartialMatch(containerName));
    }

    private String findBaseUrlByPartialMatch(String partialName) {
        List<String> matchingKeys = baseUrls.keySet().stream()
                .filter(name -> name.contains(partialName))
                .collect(Collectors.toList());

        if (matchingKeys.isEmpty()) {
            throw new RuntimeException("No base URL found for container matching: " + partialName);
        } else if (matchingKeys.size() > 1) {
            throw new RuntimeException("Multiple base URLs found for container matching: " + partialName + " -> " + matchingKeys);
        }

        return baseUrls.get(matchingKeys.get(0));
    }

    private RequestSpecification findRequestSpecificationByPartialMatch(String partialName) {
        List<String> matchingKeys = requestSpecs.keySet().stream()
                .filter(name -> name.contains(partialName))
                .collect(Collectors.toList());

        if (matchingKeys.isEmpty()) {
            throw new RuntimeException("No request specification found for container matching: " + partialName);
        } else if (matchingKeys.size() > 1) {
            throw new RuntimeException("Multiple request specifications found for container matching: " + partialName + " -> " + matchingKeys);
        }

        return requestSpecs.get(matchingKeys.get(0));
    }

}
