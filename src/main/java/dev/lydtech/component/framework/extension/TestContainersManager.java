package dev.lydtech.component.framework.extension;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.debezium.testing.testcontainers.DebeziumContainer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import static dev.lydtech.component.framework.extension.TestContainersConfiguration.ADDITIONAL_CONTAINERS;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.CONTAINER_MAIN_LABEL;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.CONTAINER_MAIN_LABEL_KEY;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.CONTAINER_NAME_PREFIX;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.DEBEZIUM_CONTAINER_LOGGING_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.DEBEZIUM_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.DEBEZIUM_IMAGE_TAG;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.DEBEZIUM_PORT;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.KAFKA_CONFLUENT_IMAGE_TAG;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.KAFKA_CONTAINER_LOGGING_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.KAFKA_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.KAFKA_SCHEMA_REGISTRY_CONTAINER_LOGGING_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.KAFKA_SCHEMA_REGISTRY_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.KAFKA_SCHEMA_REGISTRY_PORT;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.KAFKA_SCHEMA_REGISTRY_WIREMOCK_IMAGE_TAG;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.KAFKA_TOPICS;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.KAFKA_TOPIC_PARTITION_COUNT;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.LOCALSTACK_CONTAINER_LOGGING_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.LOCALSTACK_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.LOCALSTACK_IMAGE_TAG;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.LOCALSTACK_PORT;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.LOCALSTACK_SERVICES;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.POSTGRES_CONTAINER_LOGGING_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.POSTGRES_DATABASE_NAME;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.POSTGRES_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.POSTGRES_HOST_NAME;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.POSTGRES_IMAGE_TAG;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.POSTGRES_PASSWORD;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.POSTGRES_PORT;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.POSTGRES_USERNAME;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.SERVICE_CONTAINER_LOGGING_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.SERVICE_DEBUG_PORT;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.SERVICE_IMAGE_TAG;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.SERVICE_INSTANCE_COUNT;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.SERVICE_NAME;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.SERVICE_PORT;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.SERVICE_STARTUP_TIMEOUT_SECONDS;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.WIREMOCK_CONTAINER_LOGGING_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.WIREMOCK_ENABLED;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.WIREMOCK_IMAGE_TAG;
import static dev.lydtech.component.framework.extension.TestContainersConfiguration.WIREMOCK_PORT;
import static dev.lydtech.component.framework.resource.Resource.DEBEZIUM;
import static dev.lydtech.component.framework.resource.Resource.KAFKA;
import static dev.lydtech.component.framework.resource.Resource.KAFKA_SCHEMA_REGISTRY;
import static dev.lydtech.component.framework.resource.Resource.LOCALSTACK;
import static dev.lydtech.component.framework.resource.Resource.POSTGRES;
import static dev.lydtech.component.framework.resource.Resource.WIREMOCK;

@Slf4j
public final class TestContainersManager {

    private Network network;
    private List<GenericContainer> serviceContainers;
    private List<GenericContainer> additionalContainers;
    private GenericContainer postgresContainer;
    private KafkaContainer kafkaContainer;
    private DebeziumContainer debeziumContainer;
    private GenericContainer kafkaSchemaRegistryContainer;
    private GenericContainer wiremockContainer;
    private GenericContainer localstackContainer;

    private TestContainersManager(){}

    protected static void initialise() {
        TestContainersManager manager = new TestContainersManager();
        log.info("Creating test containers...");
        manager.createContainers();
        log.info("Starting test containers...");
        manager.startContainers();
        log.info("Started test containers.");
    }

    private void createContainers() {
        if (SERVICE_INSTANCE_COUNT < 1) {
            throw new RuntimeException("At least one service container should be started");
        }
        network = Network.newNetwork();
        if (POSTGRES_ENABLED) {
            postgresContainer = createPostgresContainer();
        }
        if (KAFKA_ENABLED) {
            kafkaContainer = createKafkaContainer();
        }
        if (DEBEZIUM_ENABLED) {
            if(!KAFKA_ENABLED || !POSTGRES_ENABLED) {
                throw new RuntimeException("Kafka and Postgres must be enabled in order to use Debezium.");
            }
            debeziumContainer = createDebeziumContainer();
        }
        if (KAFKA_SCHEMA_REGISTRY_ENABLED) {
            if(!KAFKA_ENABLED) {
                throw new RuntimeException("Kafka must be enabled in order to use Kafka schema registry.");
            }
            kafkaSchemaRegistryContainer = createKafkaSchemaRegistryContainer();
        }
        if (WIREMOCK_ENABLED) {
            wiremockContainer = createWiremockContainer();
        }
        if (LOCALSTACK_ENABLED) {
            localstackContainer = createLocalstackContainer();
        }
        serviceContainers = IntStream.range(1, SERVICE_INSTANCE_COUNT+1)
            .mapToObj(this::createServiceContainer)
            .collect(Collectors.toList());

        additionalContainers = ADDITIONAL_CONTAINERS.stream().map(additionalContainer -> createAdditionalContainer(
                additionalContainer.getName(),
                additionalContainer.getPort(),
                additionalContainer.getDebugPort(),
                additionalContainer.getImageTag(),
                additionalContainer.getAdditionalContainerLoggingEnabled()))
            .collect(Collectors.toList());
    }

    private void startContainers() {
        try {
            if(POSTGRES_ENABLED) {
                postgresContainer.start();
            }
            if(KAFKA_ENABLED) {
                kafkaContainer.start();
                createTopics();
            }
            if(DEBEZIUM_ENABLED) {
                debeziumContainer.start();
            }
            if(KAFKA_SCHEMA_REGISTRY_ENABLED) {
                kafkaSchemaRegistryContainer.start();
            }
            if(WIREMOCK_ENABLED) {
                wiremockContainer.start();
            }
            if(LOCALSTACK_ENABLED) {
                localstackContainer.start();
            }
            serviceContainers.stream().forEach(container -> container.start());
            additionalContainers.stream().forEach(container -> container.start());
        } catch (Exception e) {
            log.error("Component test containers failed to start", e);
            throw e;
        }
    }

    private GenericContainer createServiceContainer(int instance) {
        String containerName = SERVICE_NAME+"-"+instance;
        String javaOpts = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:"+SERVICE_DEBUG_PORT+" -Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom -Dspring.config.additional-location=file:/application.yml";

        GenericContainer container = new GenericContainer<>(CONTAINER_NAME_PREFIX+"/"+SERVICE_NAME+":" + SERVICE_IMAGE_TAG)
                .withEnv("JAVA_OPTS", javaOpts)
                .withLabel(CONTAINER_MAIN_LABEL_KEY, CONTAINER_MAIN_LABEL)
                .withFileSystemBind("./target/test-classes/application-component-test.yml", "/application.yml", BindMode.READ_ONLY)
                .withExposedPorts(SERVICE_PORT, SERVICE_DEBUG_PORT)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(CONTAINER_NAME_PREFIX+"-"+containerName);
                })
                .waitingFor(Wait.forHttp("/actuator/health")
                    .forPort(SERVICE_PORT)
                    .forStatusCode(200)
                    .withStartupTimeout(Duration.ofSeconds(SERVICE_STARTUP_TIMEOUT_SECONDS)));
        if(SERVICE_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private GenericContainer createAdditionalContainer(String name, Integer port, Integer debugPort, String imageTag, boolean containerLoggingEnabled) {
        String javaOpts = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:"+debugPort+" -Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom -Dspring.config.additional-location=file:/application.yml";

        GenericContainer container = new GenericContainer<>(CONTAINER_NAME_PREFIX+"/"+name+":" + imageTag)
                .withEnv("JAVA_OPTS", javaOpts)
                .withFileSystemBind("./target/test-classes/"+name+"/application-component-test.yml", "/application.yml", BindMode.READ_ONLY)
                .withExposedPorts(port, debugPort)
                .withNetwork(network)
                .withNetworkAliases(name)
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(CONTAINER_NAME_PREFIX+"-"+name);
                })
                .waitingFor(Wait.forHttp("/actuator/health")
                        .forPort(port)
                        .forStatusCode(200)
                        .withStartupTimeout(Duration.ofSeconds(SERVICE_STARTUP_TIMEOUT_SECONDS)));
        if(containerLoggingEnabled) {
            container.withLogConsumer(getLogConsumer(name));
        }
        return container;
    }

    private GenericContainer createPostgresContainer() {
        String containerName = POSTGRES.toString();

        // Use of the Debezium / Postgres container. This allows use of Debezium (if enabled).  Without it it fails due
        // to the wal_level property (logical vs replica).
        GenericContainer container = new PostgreSQLContainer<>(DockerImageName.parse("debezium/postgres").asCompatibleSubstituteFor("postgres").withTag(POSTGRES_IMAGE_TAG))
                .withDatabaseName(POSTGRES_DATABASE_NAME)
                .withUsername(POSTGRES_USERNAME)
                .withPassword(POSTGRES_PASSWORD)
                .withNetwork(network)
                .withNetworkAliases(POSTGRES_HOST_NAME)
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(CONTAINER_NAME_PREFIX+"-"+containerName);
                })
                .withExposedPorts(POSTGRES_PORT);
        if(POSTGRES_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private KafkaContainer createKafkaContainer() {
        String containerName = KAFKA.toString();
        KafkaContainer container = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka").withTag(KAFKA_CONFLUENT_IMAGE_TAG))
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(CONTAINER_NAME_PREFIX+"-"+containerName);
                });
        if(KAFKA_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private DebeziumContainer createDebeziumContainer() {
        String containerName = DEBEZIUM.toString();
        DebeziumContainer container = new DebeziumContainer(DockerImageName.parse("debezium/connect").withTag(DEBEZIUM_IMAGE_TAG))
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withKafka(kafkaContainer)
                .withExposedPorts(DEBEZIUM_PORT)
                .dependsOn(kafkaContainer)
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(CONTAINER_NAME_PREFIX+"-"+containerName);
                });
        if(DEBEZIUM_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private GenericContainer createKafkaSchemaRegistryContainer() {
        String containerName = KAFKA_SCHEMA_REGISTRY.toString();
        GenericContainer container = new GenericContainer<>("wiremock/wiremock:" + KAFKA_SCHEMA_REGISTRY_WIREMOCK_IMAGE_TAG)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(CONTAINER_NAME_PREFIX+"-"+containerName);
                })
                .withClasspathResourceMapping("/schemaregistry", "/home/wiremock/mappings", BindMode.READ_WRITE)
                .withExposedPorts(KAFKA_SCHEMA_REGISTRY_PORT)
                .waitingFor(Wait.forHttp("/health").forStatusCode(204));
        if(KAFKA_SCHEMA_REGISTRY_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private GenericContainer createWiremockContainer() {
        String containerName = WIREMOCK.toString();
        GenericContainer container = new GenericContainer<>("wiremock/wiremock:" + WIREMOCK_IMAGE_TAG)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(CONTAINER_NAME_PREFIX+"-"+containerName);
                })
                .withClasspathResourceMapping("/wiremock", "/home/wiremock/mappings", BindMode.READ_WRITE)
                .withExposedPorts(WIREMOCK_PORT)
                .waitingFor(Wait.forHttp("/health").forStatusCode(204));
        if(WIREMOCK_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private GenericContainer createLocalstackContainer() {
        String containerName = LOCALSTACK.toString();
        GenericContainer container = new GenericContainer<>("localstack/localstack:" + LOCALSTACK_IMAGE_TAG)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(CONTAINER_NAME_PREFIX+"-"+containerName);
                })
                .withEnv("SERVICES", LOCALSTACK_SERVICES)
                .withExposedPorts(LOCALSTACK_PORT);
        if(LOCALSTACK_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private void createTopics() {
        if(!KAFKA_TOPICS.isEmpty()) {
            Properties properties = new Properties();
            properties.put(
                    AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers()
            );
            Admin admin = Admin.create(properties);
            Collection<NewTopic> newTopics = new ArrayList<>(KAFKA_TOPICS.size());
            int partitions = KAFKA_TOPIC_PARTITION_COUNT;
            short replicationFactor = 1;
            for (String topicName : KAFKA_TOPICS) {
                NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);
                newTopics.add(newTopic);
            }
            CreateTopicsResult result = admin.createTopics(newTopics);
            try {
                result.all().get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Topic creation failed.", e);
                throw new RuntimeException("Topic creation failed: " + e.getMessage(), e);
            }
            log.info("Created topics: " + KAFKA_TOPICS);
        }
    }

    private Slf4jLogConsumer getLogConsumer(String containerName) {
        return new Slf4jLogConsumer(LoggerFactory.getLogger("container."+containerName))
                .withRemoveAnsiCodes(false);
    }
}
