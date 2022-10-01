package dev.lydtech.component.framework.extension;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
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

import static dev.lydtech.component.framework.extension.TestContainersConfiguration.*;
import static dev.lydtech.component.framework.resource.Resource.*;

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
    private GenericContainer controlCenterContainer;
    private GenericContainer conduktorContainer;

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
        if (KAFKA_CONTROL_CENTER_ENABLED) {
            if (!KAFKA_ENABLED) {
                throw new RuntimeException("Kafka must be enabled in order to use Control Center.");
            }
            controlCenterContainer = createControlCenterContainer();
        }
        if (CONDUKTOR_ENABLED) {
            if (!KAFKA_ENABLED) {
                throw new RuntimeException("Kafka must be enabled in order to use Conduktor.");
            }
            conduktorContainer = createConduktorContainer();
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
            if(KAFKA_CONTROL_CENTER_ENABLED) {
                controlCenterContainer.start();
            }
            if(CONDUKTOR_ENABLED) {
                conduktorContainer.start();
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
        String containerName = KAFKA_SCHEMA_REGISTRY.toString().replace("_", "-");
        GenericContainer container = new GenericContainer<>("confluentinc/cp-schema-registry:" + KAFKA_SCHEMA_REGISTRY_CONFLUENT_IMAGE_TAG)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(CONTAINER_NAME_PREFIX+"-"+containerName);
                })
                .withExposedPorts(KAFKA_SCHEMA_REGISTRY_PORT)
                .withEnv("SCHEMA_REGISTRY_HOST_NAME", containerName)
                .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", KAFKA.toString()+":9092")
                .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:"+KAFKA_SCHEMA_REGISTRY_PORT)
                .dependsOn(kafkaContainer);
        if(KAFKA_SCHEMA_REGISTRY_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private GenericContainer createControlCenterContainer() {
        String containerName = KAFKA_CONTROL_CENTER.toString().replace("_", "-");
        GenericContainer container = new GenericContainer<>("confluentinc/cp-enterprise-control-center:" + KAFKA_CONTROL_CENTER_CONFLUENT_IMAGE_TAG)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(CONTAINER_NAME_PREFIX+"-"+containerName);
                })
                .withEnv("CONTROL_CENTER_BOOTSTRAP_SERVERS", KAFKA.toString()+":9092")
                .withEnv("CONTROL_CENTER_REPLICATION_FACTOR", "1")
                .withEnv("CONTROL_CENTER_INTERNAL_TOPICS_PARTITIONS", "1")
                .withEnv("CONTROL_CENTER_MONITORING_INTERCEPTOR_TOPIC_PARTITIONS", "1")
                .withExposedPorts(KAFKA_CONTROL_CENTER_PORT);
        if(KAFKA_SCHEMA_REGISTRY_ENABLED) {
            container.withEnv("CONTROL_CENTER_SCHEMA_REGISTRY_URL", "http://"+KAFKA_SCHEMA_REGISTRY.toString().replace("_", "-")+":"+KAFKA_SCHEMA_REGISTRY_PORT);
        }
        if(KAFKA_CONTROL_CENTER_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private GenericContainer createConduktorContainer() {
        String containerName = CONDUKTOR.toString();
        int containerExposedPort = 80;
        // Force host port to be CONDUKTOR_PORT.
        Consumer<CreateContainerCmd> cmd = e -> e.withHostConfig(e.getHostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(CONDUKTOR_PORT),
                new ExposedPort(containerExposedPort))))
                .withName(CONTAINER_NAME_PREFIX+"-"+containerName);

        GenericContainer container = new GenericContainer<>("conduktor/conduktor-platform:" + CONDUKTOR_IMAGE_TAG)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCreateContainerCmdModifier(cmd)
                .withEnv("KAFKA_BOOTSTRAP_SERVER", KAFKA.toString()+":9092")
                .withExposedPorts(containerExposedPort);
        if(CONDUKTOR_LICENSE_KEY != null) {
            container.withEnv("LICENSE_KEY", CONDUKTOR_LICENSE_KEY);
        }
        if(KAFKA_SCHEMA_REGISTRY_ENABLED) {
            container.withEnv("SCHEMA_REGISTRY_URL", "http://"+KAFKA_SCHEMA_REGISTRY.toString().replace("_", "-")+":"+KAFKA_SCHEMA_REGISTRY_PORT);
        }
        if(CONDUKTOR_CONTAINER_LOGGING_ENABLED) {
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
