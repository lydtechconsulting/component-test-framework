package dev.lydtech.component.framework.extension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerNetwork;
import com.github.dockerjava.api.model.ContainerNetworkSettings;
import com.github.dockerjava.api.model.ContainerPort;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.extern.slf4j.Slf4j;

import static dev.lydtech.component.framework.extension.TestContainersConfiguration.CONTAINER_MAIN_LABEL;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.CONTAINER_MAIN_LABEL_KEY;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.CONTAINER_NAME_PREFIX;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.DEBEZIUM_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.DEBEZIUM_PORT;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.KAFKA_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.KAFKA_PORT;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.LOCALSTACK_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.LOCALSTACK_PORT;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.POSTGRES_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.POSTGRES_PORT;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.SERVICE_PORT;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.WIREMOCK_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.WIREMOCK_PORT;
import static dev.lydtech.component.framework.resource.Resource.DEBEZIUM;
import static dev.lydtech.component.framework.resource.Resource.KAFKA;
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
                .filter(container -> Arrays.stream(container.getNames()).anyMatch(name -> name.startsWith("/"+CONTAINER_NAME_PREFIX+"-")))
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
        ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
        // To locate the service containers use the container prefix and main container label.  This decouples discovery
        // from the service name, so that subsequent runs do not need this overridden if changed each time.
        List<Container> serviceContainers = dockerClient.listContainersCmd()
                .withNameFilter(singletonList(CONTAINER_NAME_PREFIX+"-"))
                .withLabelFilter(Collections.singletonMap(CONTAINER_MAIN_LABEL_KEY, CONTAINER_MAIN_LABEL))
                .exec();
        if (serviceContainers.size() > 0) {
            mapPort(SERVICE.toString(), SERVICE_PORT, serviceContainers.get(0));
            discoverHost(SERVICE.toString(), serviceContainers.get(0));
        } else {
            throw new RuntimeException("Service container not found");
        }

        discoverHostAndMapPort(POSTGRES.toString(), POSTGRES_ENABLED, listContainersCmd, POSTGRES_PORT);
        discoverHostAndMapPort(KAFKA.toString(), KAFKA_ENABLED, listContainersCmd, KAFKA_PORT);
        discoverHostAndMapPort(DEBEZIUM.toString(), DEBEZIUM_ENABLED, listContainersCmd, DEBEZIUM_PORT);
        discoverHostAndMapPort(WIREMOCK.toString(), WIREMOCK_ENABLED, listContainersCmd, WIREMOCK_PORT);
        discoverHostAndMapPort(LOCALSTACK.toString(), LOCALSTACK_ENABLED, listContainersCmd, LOCALSTACK_PORT);

        log.info("Docker host and ports captured.");
    }

    /**
     * Dicovers the Docker container host for each resource.
     *
     * Attempts to map the ports for each resource, in case the component test is being run via IDE after containers
     * were left up.  Only throws an exception if the container is not found when it is flagged as enabled.
     */
    private static void discoverHostAndMapPort(String resourceName, boolean enabled, ListContainersCmd listContainersCmd, int port) {
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
            discoverHost(resourceName, containers.get(0));
        } else {
            if(enabled) {
                log.error(resourceName + " is enabled but single container is not found - containerName: {} - containers.size(): {} - port: {}", containerName, containers.size(), port);
                throw new RuntimeException(resourceName + " container is not found.");
            } else {
                log.info(resourceName + " container is not enabled");
            }
        }
    }

    private static void discoverHost(String resourceName, Container container) {
        ContainerNetworkSettings networkSettings = container.getNetworkSettings();
        if(networkSettings != null) {
            Map<String, ContainerNetwork> networks = networkSettings.getNetworks();
            if(networks != null && networks.values().size()>0) {
                String ipAddress = networks.values().iterator().next().getIpAddress();
                if(ipAddress != null && !ipAddress.isBlank()) {
                    log.info(resourceName + " container has IP address: "+ipAddress);
                    System.setProperty(resourceName +".host", ipAddress);
                } else {
                    log.warn(resourceName + " has no IP address set.");
                }
            } else {
                log.warn(resourceName + " has no network.");
            }
        } else {
            log.warn(resourceName + " has no network settings.");
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
}
