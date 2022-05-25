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
            Integer mappedPort = getMappedPort("Service", SERVICE_PORT, serviceContainers);
            log.info("Service port " + SERVICE_PORT + " is mapped to " + mappedPort);
            System.setProperty("service.mapped.port", mappedPort.toString());
        } else {
            throw new RuntimeException("Service container not found");
        }

        String postgresContainerName = CONTAINER_NAME_PREFIX + "-postgres";
        mapPorts("Postgres", POSTGRES_ENABLED, listContainersCmd, postgresContainerName, POSTGRES_PORT, "postgres");

        String kafkaContainerName = CONTAINER_NAME_PREFIX + "-kafka";
        mapPorts("Kafka", KAFKA_ENABLED, listContainersCmd, kafkaContainerName, KAFKA_PORT, "kafka");

        String debeziumContainerName = CONTAINER_NAME_PREFIX + "-debezium";
        mapPorts("Debezium", DEBEZIUM_ENABLED, listContainersCmd, debeziumContainerName, DEBEZIUM_PORT, "debezium");

        String wiremockContainerName = CONTAINER_NAME_PREFIX + "-wiremock";
        mapPorts("Wiremock", WIREMOCK_ENABLED, listContainersCmd, wiremockContainerName, WIREMOCK_PORT, "wiremock");

        String localstackContainerName = CONTAINER_NAME_PREFIX + "-localstack";
        mapPorts("Localstack", LOCALSTACK_ENABLED, listContainersCmd, localstackContainerName, LOCALSTACK_PORT, "localstack");

        log.info("Docker ports captured.");
    }

    /**
     * Only caters for mapping ports when there is a single container for the resource.
     *
     * Attempts to map the ports for each resource, in case the component test is being run via IDE after containers
     * were left up.  Only throws an exception if the container is not found when it is flagged as enabled.
     */
    private static void mapPorts(String name, boolean enabled, ListContainersCmd listContainersCmd, String containerName, int port, String portParameterName) {
        List<Container> containers = listContainersCmd.withNameFilter(singletonList(containerName)).exec();
        if(containers.size()>1) {
            // The Name Filter is a pattern search, so need to check for exact name match as more than one found.
            containers = containers.stream()
                    .filter(container -> Arrays.stream(container.getNames()).anyMatch(n -> n.equals("/"+containerName)))
                    .collect(Collectors.toList());
        }
        if (containers.size() == 1) {
            Integer mappedPort = getMappedPort(name, port, containers);
            log.info(name + " port " + port + " is mapped to " + mappedPort);
            System.setProperty(portParameterName+".mapped.port", mappedPort.toString());
        } else {
            if(enabled) {
                log.error(name + " is enabled but single container is not found - containerName: {} - containers.size(): {} - port: {} - portParameterName: {}", containerName, containers.size(), port, portParameterName);
                throw new RuntimeException(name + " single container is not found.");
            } else {
                log.info(name + " container is not enabled");
            }
        }
    }

    private static Integer getMappedPort(String name, int port, List<Container> containers) {
        return Arrays.stream(containers.get(0).getPorts())
                .filter(x -> Objects.equals(x.getPrivatePort(), port))
                .map(ContainerPort::getPublicPort).findFirst()
                .orElseThrow(() -> new RuntimeException(name + " port not found"));
    }
}
