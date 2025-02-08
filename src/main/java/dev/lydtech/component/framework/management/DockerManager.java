package dev.lydtech.component.framework.management;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerPort;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.DockerClientFactory;

import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.ADDITIONAL_CONTAINERS;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.CONDUKTOR_GATEWAY_ENABLED;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.CONDUKTOR_GATEWAY_HTTP_PORT;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.CONTAINER_APPEND_GROUP_ID;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.CONTAINER_GROUP_ID;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.CONTAINER_MAIN_LABEL;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.CONTAINER_MAIN_LABEL_KEY;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.CONTAINER_NAME_PREFIX;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.DEBEZIUM_ENABLED;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.DEBEZIUM_PORT;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.ELASTICSEARCH_ENABLED;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.ELASTICSEARCH_PORT;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.KAFKA_ENABLED;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.KAFKA_NATIVE_ENABLED;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.KAFKA_NATIVE_PORT;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.KAFKA_PORT;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.KAFKA_SCHEMA_REGISTRY_ENABLED;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.KAFKA_SCHEMA_REGISTRY_PORT;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.LOCALSTACK_ENABLED;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.LOCALSTACK_PORT;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.MONGODB_ENABLED;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.MONGODB_PORT;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.POSTGRES_ENABLED;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.POSTGRES_PORT;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.MARIADB_ENABLED;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.MARIADB_PORT;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.SERVICE_PORT;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.WIREMOCK_ENABLED;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.WIREMOCK_PORT;
import static dev.lydtech.component.framework.resource.Resource.CONDUKTORGATEWAY;
import static dev.lydtech.component.framework.resource.Resource.DEBEZIUM;
import static dev.lydtech.component.framework.resource.Resource.ELASTICSEARCH;
import static dev.lydtech.component.framework.resource.Resource.KAFKA;
import static dev.lydtech.component.framework.resource.Resource.KAFKA_SCHEMA_REGISTRY;
import static dev.lydtech.component.framework.resource.Resource.LOCALSTACK;
import static dev.lydtech.component.framework.resource.Resource.MONGODB;
import static dev.lydtech.component.framework.resource.Resource.POSTGRES;
import static dev.lydtech.component.framework.resource.Resource.MARIADB;
import static dev.lydtech.component.framework.resource.Resource.SERVICE;
import static dev.lydtech.component.framework.resource.Resource.WIREMOCK;
import static java.util.Collections.singletonList;

@Slf4j
public final class DockerManager {

    public static DockerClient getDockerClient() {
        log.info("Check if services are running");
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock")
                .build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();
        return DockerClientImpl.getInstance(config, httpClient);
    }

    /**
     * Skip Testcontainers setup if:
     *
     * - Main container is running (it has the expected prefix and label) AND
     * - Testcontainers container is not running.
     *
     * If the system parameter container.append.group.id is true, then Testcontainers set up is always performed, as
     * that property is used to enable a group of containers to be run concurrently for multiple component test runs
     * (and the group of container names include a unique id to identify the group).
     */
    public static boolean shouldPerformSetup(DockerClient dockerClient) {
        if(CONTAINER_APPEND_GROUP_ID) return true;

        List<Container> containers = dockerClient.listContainersCmd().exec();
        boolean mainContainerPresent = containers.stream()
                    .filter(container -> Arrays.stream(container.getNames()).anyMatch(name -> name.startsWith("/" + CONTAINER_NAME_PREFIX + "-")))
                    .anyMatch(container -> container.getLabels()
                            .entrySet()
                            .stream()
                            .anyMatch(entry ->
                                    entry.getKey().equals(CONTAINER_MAIN_LABEL_KEY) && entry.getValue().equals(CONTAINER_MAIN_LABEL)
                            ));
        boolean testContainersPresent = containers.stream().anyMatch(container -> Arrays.stream(container.getNames()).anyMatch(name -> name.startsWith("/testcontainers-ryuk")));

        log.info("Current container status: main service with prefix ({}) and label ({}) running: {}, testcontainers running: {}",  CONTAINER_NAME_PREFIX, CONTAINER_MAIN_LABEL, mainContainerPresent, testContainersPresent);

        return !(mainContainerPresent && !testContainersPresent);
    }

    public static void captureDockerContainerPorts(DockerClient dockerClient) {
        log.info("Capturing Docker ports...");
        log.info("Container main label: "+CONTAINER_MAIN_LABEL);
        // To locate the service containers use the container prefix and main container label.  This decouples discovery
        // from the service name, so that subsequent runs do not need this overridden if changed each time.
        List<Container> serviceContainers = dockerClient.listContainersCmd()
                .withNameFilter(singletonList(CONTAINER_NAME_PREFIX + "-"))
                .withLabelFilter(Collections.singletonMap(CONTAINER_MAIN_LABEL_KEY, CONTAINER_MAIN_LABEL))
                .exec();
        if (serviceContainers.size() > 0) {
            mapPort(SERVICE.toString(), SERVICE_PORT, serviceContainers.get(0));
        } else {
            throw new RuntimeException("Service container not found");
        }
        findContainerAndMapPort(dockerClient, POSTGRES.toString(), POSTGRES_ENABLED, POSTGRES_PORT);
        findContainerAndMapPort(dockerClient, MONGODB.toString(), MONGODB_ENABLED, MONGODB_PORT);
        findContainerAndMapPort(dockerClient, MARIADB.toString(), MARIADB_ENABLED, MARIADB_PORT);
        // Just need to map the port of one of the Kafka servers, and either look up using the Kafka or the Kafka native port.
        if (KAFKA_ENABLED) {
            findContainerAndMapPort(dockerClient, KAFKA.toString(), KAFKA_ENABLED, KAFKA_PORT);
        } else {
            findContainerAndMapPort(dockerClient, KAFKA.toString(), KAFKA_NATIVE_ENABLED, KAFKA_NATIVE_PORT);
        }
        findContainerAndMapPort(dockerClient, DEBEZIUM.toString(), DEBEZIUM_ENABLED, DEBEZIUM_PORT);
        findContainerAndMapPort(dockerClient, KAFKA_SCHEMA_REGISTRY.toString().replace("_", "."), KAFKA_SCHEMA_REGISTRY_ENABLED, KAFKA_SCHEMA_REGISTRY_PORT);
        findContainerAndMapPort(dockerClient, WIREMOCK.toString(), WIREMOCK_ENABLED, WIREMOCK_PORT);
        findContainerAndMapPort(dockerClient, LOCALSTACK.toString(), LOCALSTACK_ENABLED, LOCALSTACK_PORT);
        findContainerAndMapPort(dockerClient, CONDUKTORGATEWAY.toString(), CONDUKTOR_GATEWAY_ENABLED, CONDUKTOR_GATEWAY_HTTP_PORT);
        findContainerAndMapPort(dockerClient, ELASTICSEARCH.toString(), ELASTICSEARCH_ENABLED, ELASTICSEARCH_PORT);
        mapAdditionalContainersPorts(dockerClient);

        captureHost();

        log.info("Docker host and ports captured.");
    }

    private static void mapAdditionalContainersPorts(DockerClient dockerClient) {
        List<Container> additionalContainers = dockerClient.listContainersCmd()
                .withNameFilter(singletonList(CONTAINER_NAME_PREFIX + "-"))
                .withLabelFilter(Collections.singletonMap("additional-container-label", "additional-container"))
                .exec();

        for (Container container : additionalContainers) {
            String containerName = Arrays.stream(container.getNames())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Container name not found"))
                    .replaceFirst("^/", "");

            Optional<AdditionalContainer> matchingAdditionalContainer = ADDITIONAL_CONTAINERS.stream()
                    .filter(ac -> (CONTAINER_NAME_PREFIX+"-"+ac.getName()).equals(containerName))
                    .findFirst();

            if (matchingAdditionalContainer.isPresent()) {
                AdditionalContainer additionalContainer = matchingAdditionalContainer.get();
                int privatePort = additionalContainer.getPort();
                mapPort("additional.container." + containerName, privatePort, container);
            } else {
                throw new RuntimeException("No matching additional container found for " + containerName);
            }
        }
    }

    /**
     * Attempts to map the ports for each resource, in case the component test is being run via IDE after containers
     * were left up.  Only throws an exception if the container is not found when it is flagged as enabled.
     */
    private static void findContainerAndMapPort(DockerClient dockerClient, String resourceName, boolean enabled, int port) {
        ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
        String containerName = CONTAINER_APPEND_GROUP_ID?CONTAINER_NAME_PREFIX + "-" + resourceName + "-" + CONTAINER_GROUP_ID :CONTAINER_NAME_PREFIX + "-" + resourceName;
        log.info("Discovering host and mapping port for container {}", containerName);
        List<Container> containers = listContainersCmd.withNameFilter(singletonList(containerName)).exec();
        if(containers.size()>1) {
            // The Name Filter is a pattern search, so need to check for exact name match as more than one found.
            containers = containers.stream()
                    .filter(container -> Arrays.stream(container.getNames()).anyMatch(n -> n.equals("/"+containerName)))
                    .collect(Collectors.toList());
        }
        if (containers.size() == 1) {
            mapPort(resourceName, port, containers.get(0));
        } else {
            if(enabled) {
                log.error(resourceName + " is enabled but single container is not found - containerName: {} - containers.size(): {} - port: {}", containerName, containers.size(), port);
                throw new RuntimeException(resourceName + " container is not found.");
            } else {
                log.info(resourceName + " container is not enabled");
            }
        }
    }

    private static void mapPort(String resourceName, int port, Container container) {
        Integer mappedPort = Arrays.stream(container.getPorts())
                .filter(x -> Objects.equals(x.getPrivatePort(), port))
                .map(ContainerPort::getPublicPort).findFirst()
                .orElseThrow(() -> new RuntimeException(resourceName + " port not found"));
        log.info(resourceName + " port " + port + " is mapped to " + mappedPort);
        System.setProperty(resourceName + ".mapped.port", mappedPort.toString());
    }

    private static void captureHost() {
        String host = DockerClientFactory.instance().dockerHostIpAddress();
        log.info("Docker host is: "+host);
        System.setProperty("docker.host", host);
    }
}
