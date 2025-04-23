package dev.lydtech.component.framework.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import dev.lydtech.component.framework.management.AdditionalContainer;
import dev.lydtech.component.framework.management.ServiceConfiguration;
import lombok.extern.slf4j.Slf4j;

import static dev.lydtech.component.framework.configuration.ConfigurationKeys.*;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.*;

@Slf4j
public class ConfigurationParser {

    /**
     * Parses service configurations from the provided properties.
     *
     * If a top-level "service.name" exists or no per-service keys exist, a single default service is assumed,
     * with system property overrides applied. Otherwise, properties of the form "service.<serviceName>.<property>"
     * are parsed and a configuration object is created for each service.
     *
     * @param properties loaded configuration properties
     * @return List of ServiceConfiguration objects
     */
    public static List<ServiceConfiguration> parseServices(Properties properties) {
        List<ServiceConfiguration> services = new ArrayList<>();

        // Determine if there are any per-service keys (of the form service.<serviceName>.<property>)
        boolean hasMultipleServiceKeys = properties.stringPropertyNames().stream()
                .filter(key -> key.startsWith("service."))
                .anyMatch(key -> !DEFAULT_SERVICE_KEYS.contains(key));

        // If a top-level default is provided OR no per-service keys exist, use the default service mode.
        if (properties.containsKey("service.name") || !hasMultipleServiceKeys) {
            String serviceName = properties.getProperty("service.name", DEFAULT_SERVICE_NAME).toLowerCase();
            ServiceConfiguration defaultService = ServiceConfiguration.builder()
                    .name(serviceName)
                    .enabled(true)
                    .instanceCount(Integer.valueOf(properties.getProperty(SERVICE_INSTANCE_COUNT_KEY, DEFAULT_SERVICE_INSTANCE_COUNT)))
                    .port(Integer.valueOf(properties.getProperty(SERVICE_PORT_KEY, DEFAULT_SERVICE_PORT)))
                    .debugPort(Integer.valueOf(properties.getProperty(SERVICE_DEBUG_PORT_KEY, DEFAULT_SERVICE_DEBUG_PORT)))
                    .startupTimeoutSeconds(Integer.valueOf(properties.getProperty(SERVICE_STARTUP_TIMEOUT_SECONDS_KEY, DEFAULT_SERVICE_STARTUP_TIMEOUT_SECONDS)))
                    .configFilesSystemProperty(properties.getProperty(SERVICE_CONFIG_FILES_SYSTEM_PROPERTY_KEY, DEFAULT_SERVICE_CONFIG_FILES_SYSTEM_PROPERTY))
                    .applicationYmlPath(properties.getProperty(SERVICE_APPLICATION_YML_PATH_KEY, DEFAULT_SERVICE_APPLICATION_YML_PATH))
                    .startupHealthEndpoint(properties.getProperty(SERVICE_STARTUP_HEALTH_ENDPOINT_KEY, DEFAULT_SERVICE_STARTUP_HEALTH_ENDPOINT))
                    .startupLogMessage(properties.getProperty(SERVICE_STARTUP_LOG_MESSAGE_KEY, DEFAULT_SERVICE_STARTUP_LOG_MESSAGE))
                    .imageTag(properties.getProperty(SERVICE_IMAGE_TAG_KEY, DEFAULT_SERVICE_IMAGE_TAG))
                    .containerLoggingEnabled(Boolean.valueOf(properties.getProperty(SERVICE_CONTAINER_LOGGING_ENABLED_KEY, DEFAULT_SERVICE_CONTAINER_LOGGING_ENABLED)))
                    .debugSuspend(Boolean.valueOf(properties.getProperty(SERVICE_DEBUG_SUSPEND_KEY, DEFAULT_SERVICE_DEBUG_SUSPEND)))
                    .applicationArgs(properties.getProperty(SERVICE_APPLICATION_ARGS_KEY, DEFAULT_SERVICE_APPLICATION_ARGS))
                    .envvars(parseKvPairs(properties.getProperty(SERVICE_ENVVARS_KEY, DEFAULT_SERVICE_ENVVARS)))
                    .additionalFilesystemBinds(parseKvPairs(properties.getProperty(SERVICE_ADDITIONAL_FILESYSTEM_BINDS_KEY, DEFAULT_SERVICE_ADDITIONAL_FILESYSTEM_BINDS)))
                    .build();
            services.add(defaultService);
            return services;
        }

        // Otherwise, we are in multiple service mode.
        // Build a map of serviceName -> (property name -> value)
        Map<String, Map<String, String>> servicePropsMap = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith("service.")) {
                String[] tokens = key.split("\\.");
                // Expect keys in the format: service.<serviceName>.<property>.*
                if (tokens.length < 3) continue;
                String serviceName = tokens[1];
                String propName = key.substring(("service." + serviceName + ".").length());
                servicePropsMap.computeIfAbsent(serviceName, k -> new HashMap<>())
                        .put(propName, properties.getProperty(key));
            }
        }

        // Create a ServiceConfiguration for each service found (even if enabled is false)
        for (Map.Entry<String, Map<String, String>> entry : servicePropsMap.entrySet()) {
            String serviceName = entry.getKey();
            Map<String, String> serviceProps = entry.getValue();

            ServiceConfiguration config = ServiceConfiguration.builder()
                    .name(serviceName) // serviceName was extracted from the key grouping
                    .enabled(Boolean.valueOf(serviceProps.getOrDefault("enabled", "false")))
                    .instanceCount(Integer.valueOf(serviceProps.getOrDefault("instance.count", DEFAULT_SERVICE_INSTANCE_COUNT)))
                    .imageTag(serviceProps.getOrDefault("image.tag", DEFAULT_SERVICE_IMAGE_TAG))
                    .port(Integer.valueOf(serviceProps.getOrDefault("port", DEFAULT_SERVICE_PORT)))
                    .debugPort(Integer.valueOf(serviceProps.getOrDefault("debug.port", DEFAULT_SERVICE_DEBUG_PORT)))
                    .startupTimeoutSeconds(Integer.valueOf(serviceProps.getOrDefault("startup.timeout.seconds", DEFAULT_SERVICE_STARTUP_TIMEOUT_SECONDS)))
                    .configFilesSystemProperty(serviceProps.getOrDefault("config.files.system.property", DEFAULT_SERVICE_CONFIG_FILES_SYSTEM_PROPERTY))
                    .applicationYmlPath(serviceProps.getOrDefault("application.yml.path", DEFAULT_SERVICE_APPLICATION_YML_PATH))
                    .startupHealthEndpoint(serviceProps.getOrDefault("startup.health.endpoint", DEFAULT_SERVICE_STARTUP_HEALTH_ENDPOINT))
                    .startupLogMessage(serviceProps.getOrDefault("startup.log.message", DEFAULT_SERVICE_STARTUP_LOG_MESSAGE))
                    .containerLoggingEnabled(Boolean.valueOf(serviceProps.getOrDefault("container.logging.enabled", DEFAULT_SERVICE_CONTAINER_LOGGING_ENABLED)))
                    .debugSuspend(Boolean.valueOf(serviceProps.getOrDefault("debug.suspend", DEFAULT_SERVICE_DEBUG_SUSPEND)))
                    .applicationArgs(serviceProps.getOrDefault("application.args", DEFAULT_SERVICE_APPLICATION_ARGS))
                    .envvars(parseKvPairs(serviceProps.getOrDefault("envvars", DEFAULT_SERVICE_ENVVARS)))
                    .additionalFilesystemBinds(parseKvPairs(serviceProps.getOrDefault("additional.filesystem.binds", DEFAULT_SERVICE_ADDITIONAL_FILESYSTEM_BINDS)))
                    .build();
            services.add(config);
        }
        return services;
    }

    protected static List<AdditionalContainer> parseAdditionalContainers(String additionalContainersPropertyValue) {
        log.debug("Parsing additional containers: {}", additionalContainersPropertyValue);
        List<AdditionalContainer> additionalContainers = Collections.EMPTY_LIST;
        if(additionalContainersPropertyValue!=null) {
            additionalContainersPropertyValue = additionalContainersPropertyValue.replaceAll("\\s+","");
            if(additionalContainersPropertyValue.length()>0) {
                List<String> containerDetailStrings = Arrays.asList(additionalContainersPropertyValue.split(":"));
                additionalContainers = containerDetailStrings.stream().map(containerDetail -> {
                    log.debug("Parsing individual additional container: {}", containerDetail);
                    List<String> parsedDetails = Arrays.asList(containerDetail.split(","));
                    if(parsedDetails.size()!=5) {
                        String message = "Invalid additional containers details: "+parsedDetails+" -  expecting 5 args, found "+parsedDetails.size()+".";
                        log.error(message);
                        throw new RuntimeException(message);
                    }
                    return AdditionalContainer.builder()
                            .name(parsedDetails.get(0))
                            .port(Integer.parseInt(parsedDetails.get(1)))
                            .debugPort(Integer.parseInt(parsedDetails.get(2)))
                            .imageTag(parsedDetails.get(3))
                            .additionalContainerLoggingEnabled(Boolean.valueOf(parsedDetails.get(4)))
                            .build();
                }).collect(Collectors.toList());
            }
        }
        return additionalContainers;
    }

    protected static Map<String, String> parseKvPairs(String input) {
        if (input == null) {
            return new HashMap<>();
        }

        Map<String, String> resultMap = new HashMap<>();

        String[] pairs = input.split(",");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");

            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                resultMap.put(key, value);
            } else {
                throw new IllegalArgumentException("invalid key/value pair string for service property");
            }
        }

        return resultMap;
    }

    protected static List<String> parseKafkaTopics(String topicNamesPropertyValue) {
        List<String> topics = Collections.EMPTY_LIST;
        if(topicNamesPropertyValue!=null) {
            topicNamesPropertyValue = topicNamesPropertyValue.replaceAll("\\s+","");
            if(topicNamesPropertyValue.length()>0) {
                topics = Arrays.asList(topicNamesPropertyValue.split(","));
            }
        }
        return topics;
    }
}
