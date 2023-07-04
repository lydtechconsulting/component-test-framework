package dev.lydtech.component.framework.extension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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

import static dev.lydtech.component.framework.extension.TestContainersConfiguration.CONTAINER_MAIN_LABEL;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.CONTAINER_MAIN_LABEL_KEY;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.CONTAINER_NAME_PREFIX;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.DEBEZIUM_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.DEBEZIUM_PORT;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.KAFKA_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.KAFKA_PORT;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.KAFKA_SCHEMA_REGISTRY_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.KAFKA_SCHEMA_REGISTRY_PORT;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.LOCALSTACK_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.LOCALSTACK_PORT;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.POSTGRES_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.POSTGRES_PORT;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.SERVICE_PORT;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.WIREMOCK_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.WIREMOCK_PORT;
import static dev.lydtech.component.framework.resource.Resource.CONDUKTORGATEWAY;
import static dev.lydtech.component.framework.resource.Resource.DEBEZIUM;
import static dev.lydtech.component.framework.resource.Resource.KAFKA;
import static dev.lydtech.component.framework.resource.Resource.KAFKA_SCHEMA_REGISTRY;
import static dev.lydtech.component.framework.resource.Resource.LOCALSTACK;
import static dev.lydtech.component.framework.resource.Resource.POSTGRES;
import static dev.lydtech.component.framework.resource.Resource.SERVICE;
import static dev.lydtech.component.framework.resource.Resource.WIREMOCK;
import static java.util.Collections.singletonList;

@Slf4j
public final class DockerManager {

    protected static DockerClient getDockerClient() {
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
     * Skip TestContainers setup if:
     *
     * - Main container is running (it has the expected prefix and label) AND
     * - TestContainers container is not running
     */
    protected static boolean shouldPerformSetup(DockerClient dockerClient) {
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

    protected static void captureDockerContainerPorts(DockerClient dockerClient) {
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
        // Just need to map the port of one of the Kafka servers.
        findContainerAndMapPort(dockerClient, KAFKA.toString(), KAFKA_ENABLED, KAFKA_PORT);
        findContainerAndMapPort(dockerClient, DEBEZIUM.toString(), DEBEZIUM_ENABLED, DEBEZIUM_PORT);
        findContainerAndMapPort(dockerClient, KAFKA_SCHEMA_REGISTRY.toString().replace("_", "."), KAFKA_SCHEMA_REGISTRY_ENABLED, KAFKA_SCHEMA_REGISTRY_PORT);
        findContainerAndMapPort(dockerClient, WIREMOCK.toString(), WIREMOCK_ENABLED, WIREMOCK_PORT);
        findContainerAndMapPort(dockerClient, LOCALSTACK.toString(), LOCALSTACK_ENABLED, LOCALSTACK_PORT);
        findContainerAndMapPort(dockerClient, CONDUKTORGATEWAY.toString(), KAFKA_ENABLED/* TODO */, 8888);

        captureHost();

        log.info("Docker host and ports captured.");
    }

    /**
     * Attempts to map the ports for each resource, in case the component test is being run via IDE after containers
     * were left up.  Only throws an exception if the container is not found when it is flagged as enabled.
     */
    private static void findContainerAndMapPort(DockerClient dockerClient, String resourceName, boolean enabled, int port) {
        ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
        String containerName = CONTAINER_NAME_PREFIX + "-" + resourceName;
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
