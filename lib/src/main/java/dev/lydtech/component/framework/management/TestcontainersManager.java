package dev.lydtech.component.framework.management;

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
import dev.lydtech.component.framework.configuration.TestcontainersConfiguration;
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
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

import static dev.lydtech.component.framework.resource.Resource.CONDUKTOR;
import static dev.lydtech.component.framework.resource.Resource.CONDUKTORGATEWAY;
import static dev.lydtech.component.framework.resource.Resource.DEBEZIUM;
import static dev.lydtech.component.framework.resource.Resource.ELASTICSEARCH;
import static dev.lydtech.component.framework.resource.Resource.KAFKA;
import static dev.lydtech.component.framework.resource.Resource.KAFKA_CONTROL_CENTER;
import static dev.lydtech.component.framework.resource.Resource.KAFKA_SCHEMA_REGISTRY;
import static dev.lydtech.component.framework.resource.Resource.LOCALSTACK;
import static dev.lydtech.component.framework.resource.Resource.MONGODB;
import static dev.lydtech.component.framework.resource.Resource.POSTGRES;
import static dev.lydtech.component.framework.resource.Resource.WIREMOCK;

@Slf4j
public final class TestcontainersManager {

    private Network network;
    private List<GenericContainer> serviceContainers = new ArrayList<>(1);
    private List<GenericContainer> additionalContainers;
    private GenericContainer postgresContainer;
    private MongoDBContainer mongoDbContainer;
    private List<KafkaContainer> kafkaContainers;
    private GenericContainer zookeeperContainer;
    private DebeziumContainer debeziumContainer;
    private GenericContainer kafkaSchemaRegistryContainer;
    private GenericContainer wiremockContainer;
    private GenericContainer localstackContainer;
    private GenericContainer controlCenterContainer;
    private GenericContainer conduktorContainer;
    private GenericContainer conduktorGatewayContainer;
    private GenericContainer elasticSearchContainer;

    private TestcontainersManager(){}

    public static void initialise() {
        TestcontainersManager manager = new TestcontainersManager();
        log.info("Creating test containers...");
        manager.createContainers();
        log.info("Starting test containers...");
        manager.startContainers();
        log.info("Started test containers.");
    }

    private void createContainers() {
        if (TestcontainersConfiguration.SERVICE_INSTANCE_COUNT < 1) {
            throw new RuntimeException("At least one service container should be started");
        }
        network = Network.newNetwork();
        if (TestcontainersConfiguration.POSTGRES_ENABLED) {
            postgresContainer = createPostgresContainer();
        }
        if (TestcontainersConfiguration.MONGODB_ENABLED) {
            mongoDbContainer = createMongoDBContainer();
        }
        if (TestcontainersConfiguration.KAFKA_ENABLED) {
            if(TestcontainersConfiguration.KAFKA_TOPIC_REPLICATION_FACTOR > TestcontainersConfiguration.KAFKA_BROKER_COUNT) {
                throw new RuntimeException("kafka.topic.replication.factor: "+ TestcontainersConfiguration.KAFKA_TOPIC_REPLICATION_FACTOR+" - must not be greater than kafka.broker.count: "+ TestcontainersConfiguration.KAFKA_BROKER_COUNT);
            }
            if(TestcontainersConfiguration.KAFKA_MIN_INSYNC_REPLICAS > TestcontainersConfiguration.KAFKA_BROKER_COUNT) {
                throw new RuntimeException("kafka.min.insync.replicas: "+ TestcontainersConfiguration.KAFKA_MIN_INSYNC_REPLICAS+" - must not be greater than kafka.broker.count: "+ TestcontainersConfiguration.KAFKA_BROKER_COUNT);
            }
            if(TestcontainersConfiguration.KAFKA_MIN_INSYNC_REPLICAS > TestcontainersConfiguration.KAFKA_TOPIC_REPLICATION_FACTOR) {
                throw new RuntimeException("kafka.min.insync.replicas: "+ TestcontainersConfiguration.KAFKA_MIN_INSYNC_REPLICAS+" - must not be greater than kafka.topic.replication.factor: "+ TestcontainersConfiguration.KAFKA_TOPIC_REPLICATION_FACTOR);
            }
            if(TestcontainersConfiguration.KAFKA_BROKER_COUNT>1) {
                // As there are multiple Kafka instances they need to use the same external Zookeeper.
                zookeeperContainer = createZookeeperContainer();
            }

            if(TestcontainersConfiguration.KAFKA_CONTROL_CENTER_ENABLED && TestcontainersConfiguration.KAFKA_CONTROL_CENTER_EXPORT_METRICS_ENABLED) {
                // To support exporting metrics for Confluent Control Center, use the Confluent cp-server container.
                kafkaContainers = IntStream.range(1, TestcontainersConfiguration.KAFKA_BROKER_COUNT +1)
                    .mapToObj(this::createKafkaServerContainer)
                    .collect(Collectors.toList());

            } else {
                kafkaContainers = IntStream.range(1, TestcontainersConfiguration.KAFKA_BROKER_COUNT +1)
                    .mapToObj(this::createKafkaContainer)
                    .collect(Collectors.toList());
            }
        }
        if (TestcontainersConfiguration.DEBEZIUM_ENABLED) {
            if(!TestcontainersConfiguration.KAFKA_ENABLED) {
                throw new RuntimeException("Kafka must be enabled in order to use Debezium.");
            }
            debeziumContainer = createDebeziumContainer();
        }
        if (TestcontainersConfiguration.KAFKA_SCHEMA_REGISTRY_ENABLED) {
            if(!TestcontainersConfiguration.KAFKA_ENABLED) {
                throw new RuntimeException("Kafka must be enabled in order to use Kafka schema registry.");
            }
            kafkaSchemaRegistryContainer = createKafkaSchemaRegistryContainer();
        }
        if (TestcontainersConfiguration.WIREMOCK_ENABLED) {
            wiremockContainer = createWiremockContainer();
        }
        if (TestcontainersConfiguration.LOCALSTACK_ENABLED) {
            localstackContainer = createLocalstackContainer();
        }
        if (TestcontainersConfiguration.KAFKA_CONTROL_CENTER_ENABLED) {
            if (!TestcontainersConfiguration.KAFKA_ENABLED) {
                throw new RuntimeException("Kafka must be enabled in order to use Control Center.");
            }
            controlCenterContainer = createControlCenterContainer();
        }
        if (TestcontainersConfiguration.CONDUKTOR_ENABLED) {
            if (!TestcontainersConfiguration.KAFKA_ENABLED) {
                throw new RuntimeException("Kafka must be enabled in order to use Conduktor.");
            }
            conduktorContainer = createConduktorContainer();
        }
        if (TestcontainersConfiguration.CONDUKTOR_GATEWAY_ENABLED) {
            if (!TestcontainersConfiguration.KAFKA_ENABLED) {
                throw new RuntimeException("Kafka must be enabled in order to use Conduktor Gateway.");
            }
            conduktorGatewayContainer = createConduktorGatewayContainer();
        }
        if (TestcontainersConfiguration.ELASTICSEARCH_ENABLED) {
            elasticSearchContainer = createElasticsearchContainer();
        }

        serviceContainers = IntStream.range(1, TestcontainersConfiguration.SERVICE_INSTANCE_COUNT + 1)
                .mapToObj(this::createServiceContainer)
                .collect(Collectors.toList());

        additionalContainers = TestcontainersConfiguration.ADDITIONAL_CONTAINERS.stream().map(additionalContainer -> createAdditionalContainer(
                additionalContainer.getName(),
                additionalContainer.getPort(),
                additionalContainer.getDebugPort(),
                additionalContainer.getImageTag(),
                additionalContainer.getAdditionalContainerLoggingEnabled()))
                .collect(Collectors.toList());
    }

    private void startContainers() {
        try {
            if(TestcontainersConfiguration.POSTGRES_ENABLED) {
                postgresContainer.start();
            }
            if(TestcontainersConfiguration.MONGODB_ENABLED) {
                mongoDbContainer.start();
            }
            if(TestcontainersConfiguration.KAFKA_ENABLED) {
                if(TestcontainersConfiguration.KAFKA_BROKER_COUNT>1) {
                    // As there are multiple Kafka instances they need to use the same external Zookeeper.
                    zookeeperContainer.start();
                }
                kafkaContainers.stream().forEach(container -> container.start());
                createTopics();
            }
            if(TestcontainersConfiguration.DEBEZIUM_ENABLED) {
                debeziumContainer.start();
            }
            if(TestcontainersConfiguration.KAFKA_SCHEMA_REGISTRY_ENABLED) {
                kafkaSchemaRegistryContainer.start();
            }
            if(TestcontainersConfiguration.KAFKA_CONTROL_CENTER_ENABLED) {
                controlCenterContainer.start();
            }
            if(TestcontainersConfiguration.CONDUKTOR_ENABLED) {
                conduktorContainer.start();
            }
            if(TestcontainersConfiguration.CONDUKTOR_GATEWAY_ENABLED) {
                conduktorGatewayContainer.start();
            }
            if(TestcontainersConfiguration.WIREMOCK_ENABLED) {
                wiremockContainer.start();
            }
            if(TestcontainersConfiguration.LOCALSTACK_ENABLED) {
                localstackContainer.start();
            }
            if(TestcontainersConfiguration.ELASTICSEARCH_ENABLED) {
                elasticSearchContainer.start();
            }
            serviceContainers.stream().forEach(container -> container.start());
            additionalContainers.stream().forEach(container -> container.start());
        } catch (Exception e) {
            log.error("Component test containers failed to start", e);
            throw e;
        }
    }

    private GenericContainer createServiceContainer(int instance) {
        String containerName = TestcontainersConfiguration.SERVICE_NAME+"-"+instance;
        String javaOpts = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:"+ TestcontainersConfiguration.SERVICE_DEBUG_PORT+" -Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom -Dspring.config.additional-location=file:/application.yml";

        GenericContainer container = new GenericContainer<>(TestcontainersConfiguration.CONTAINER_NAME_PREFIX+"/"+ TestcontainersConfiguration.SERVICE_NAME+":" + TestcontainersConfiguration.SERVICE_IMAGE_TAG)
                .withEnv("JAVA_OPTS", javaOpts)
                .withLabel(TestcontainersConfiguration.CONTAINER_MAIN_LABEL_KEY, TestcontainersConfiguration.CONTAINER_MAIN_LABEL)
                .withFileSystemBind("./target/test-classes/application-component-test.yml", "/application.yml", BindMode.READ_ONLY)
                .withExposedPorts(TestcontainersConfiguration.SERVICE_PORT, TestcontainersConfiguration.SERVICE_DEBUG_PORT)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(TestcontainersConfiguration.CONTAINER_NAME_PREFIX+"-"+containerName);
                })
                .waitingFor(Wait.forHttp("/actuator/health")
                    .forPort(TestcontainersConfiguration.SERVICE_PORT)
                    .forStatusCode(200)
                    .withStartupTimeout(Duration.ofSeconds(TestcontainersConfiguration.SERVICE_STARTUP_TIMEOUT_SECONDS)));
        if(TestcontainersConfiguration.SERVICE_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private GenericContainer createAdditionalContainer(String name, Integer port, Integer debugPort, String imageTag, boolean containerLoggingEnabled) {
        String javaOpts = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:"+debugPort+" -Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom -Dspring.config.additional-location=file:/application.yml";

        GenericContainer container = new GenericContainer<>(TestcontainersConfiguration.CONTAINER_NAME_PREFIX+"/"+name+":" + imageTag)
                .withEnv("JAVA_OPTS", javaOpts)
                .withFileSystemBind("./target/test-classes/"+name+"/application-component-test.yml", "/application.yml", BindMode.READ_ONLY)
                .withExposedPorts(port, debugPort)
                .withNetwork(network)
                .withNetworkAliases(name)
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(TestcontainersConfiguration.CONTAINER_NAME_PREFIX+"-"+name);
                })
                .waitingFor(Wait.forHttp("/actuator/health")
                        .forPort(port)
                        .forStatusCode(200)
                        .withStartupTimeout(Duration.ofSeconds(TestcontainersConfiguration.SERVICE_STARTUP_TIMEOUT_SECONDS)));
        if(containerLoggingEnabled) {
            container.withLogConsumer(getLogConsumer(name));
        }
        return container;
    }

    private GenericContainer createPostgresContainer() {
        String containerName = POSTGRES.toString();

        // Use of the Debezium / Postgres container. This allows use of Debezium (if enabled).  Without it it fails due
        // to the wal_level property (logical vs replica).
        GenericContainer container = new PostgreSQLContainer<>(DockerImageName.parse("debezium/postgres").asCompatibleSubstituteFor("postgres").withTag(TestcontainersConfiguration.POSTGRES_IMAGE_TAG))
                .withDatabaseName(TestcontainersConfiguration.POSTGRES_DATABASE_NAME)
                .withUsername(TestcontainersConfiguration.POSTGRES_USERNAME)
                .withPassword(TestcontainersConfiguration.POSTGRES_PASSWORD)
                .withNetwork(network)
                .withNetworkAliases(TestcontainersConfiguration.POSTGRES_HOST_NAME)
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(TestcontainersConfiguration.CONTAINER_NAME_PREFIX+"-"+containerName);
                })
                .withExposedPorts(TestcontainersConfiguration.POSTGRES_PORT);
        if(TestcontainersConfiguration.POSTGRES_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }


    private MongoDBContainer createMongoDBContainer() {
        String containerName = MONGODB.toString();
        MongoDBContainer container = new MongoDBContainer("mongo:" + TestcontainersConfiguration.MONGODB_IMAGE_TAG)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCommand("--replSet", TestcontainersConfiguration.MONGODB_REPLICA_SET)
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(TestcontainersConfiguration.CONTAINER_NAME_PREFIX+"-"+containerName);
                });
        if(TestcontainersConfiguration.MONGODB_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    /**
     * Standard Kafka and Zookeeper.
     */
    private KafkaContainer createKafkaContainer(int instance) {
        final String containerName = instance==1?KAFKA.toString():KAFKA.toString()+"-"+instance;
        KafkaContainer container = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka").withTag(TestcontainersConfiguration.KAFKA_CONFLUENT_IMAGE_TAG))
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(TestcontainersConfiguration.CONTAINER_NAME_PREFIX+"-"+containerName);
                });
        container = configureCommonKafkaContainerEnv(container, instance);
        if(TestcontainersConfiguration.KAFKA_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    /**
     * The Confluent cp-server container, with additional support for metrics.
     *
     * The main project must depend on Confluent's kafka-clients community package (e.g. 7.3.2-ccs) and monitoring-interceptors (e.g. 7.3.2) libraries.
     */
    private KafkaContainer createKafkaServerContainer(int instance) {
        final String containerName = instance==1?KAFKA.toString():KAFKA.toString()+"-"+instance;
        DockerImageName cpServerImage = DockerImageName.parse("confluentinc/cp-server").asCompatibleSubstituteFor("confluentinc/cp-kafka");
        KafkaContainer container = new KafkaContainer(cpServerImage.withTag(TestcontainersConfiguration.KAFKA_CONFLUENT_IMAGE_TAG))
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withEnv("KAFKA_CONFLUENT_LICENSE_TOPIC_REPLICATION_FACTOR", "1")
                .withEnv("KAFKA_CONFLUENT_BALANCER_TOPIC_REPLICATION_FACTOR", "1")
                .withEnv("KAFKA_JMX_PORT", TestcontainersConfiguration.KAFKA_CONTROL_CENTER_JMX_PORT)
                .withEnv("KAFKA_JMX_HOSTNAME", "localhost")
                .withEnv("CONFLUENT_METRICS_REPORTER_BOOTSTRAP_SERVERS", containerName+":9092")
                .withEnv("CONFLUENT_METRICS_REPORTER_TOPIC_REPLICAS", "1")
                .withEnv("KAFKA_METRIC_REPORTERS", "io.confluent.metrics.reporter.ConfluentMetricsReporter")
                .withEnv("CONFLUENT_METRICS_ENABLE", "true")
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(TestcontainersConfiguration.CONTAINER_NAME_PREFIX+"-"+containerName);
                });
                container.withEnv("KAFKA_METRIC_REPORTERS", "io.confluent.metrics.reporter.ConfluentMetricsReporter");
        container = configureCommonKafkaContainerEnv(container, instance);
        if(TestcontainersConfiguration.KAFKA_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    /**
     * Set up the Env that is the same for both the Confluent cp-kafka and cp-server container types.
     */
    private KafkaContainer configureCommonKafkaContainerEnv(KafkaContainer kafkaContainer, int instance) {
        if(TestcontainersConfiguration.KAFKA_BROKER_COUNT>1) {
            // As there are multiple Kafka instances they need to use the same external Zookeeper.
            kafkaContainer.withExternalZookeeper("zookeeper:2181");
        }
        return kafkaContainer
                .withEnv("KAFKA_BROKER_ID", String.valueOf(instance))
                .withEnv("KAFKA_NUM_PARTITIONS", String.valueOf(TestcontainersConfiguration.KAFKA_TOPIC_PARTITION_COUNT))
                .withEnv("KAFKA_DEFAULT_REPLICATION_FACTOR", String.valueOf(TestcontainersConfiguration.KAFKA_TOPIC_REPLICATION_FACTOR))
                .withEnv("KAFKA_MIN_INSYNC_REPLICAS", String.valueOf(TestcontainersConfiguration.KAFKA_MIN_INSYNC_REPLICAS))
                .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", String.valueOf(TestcontainersConfiguration.KAFKA_TOPIC_REPLICATION_FACTOR))
                .withEnv("KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS", "0")
                .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", String.valueOf(TestcontainersConfiguration.KAFKA_MIN_INSYNC_REPLICAS))
                .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", String.valueOf(TestcontainersConfiguration.KAFKA_TOPIC_REPLICATION_FACTOR));
    }


    private GenericContainer createZookeeperContainer() {
        return new GenericContainer<>("confluentinc/cp-zookeeper:4.0.0")
                .withNetwork(network)
                .withNetworkAliases("zookeeper")
                .withEnv("ZOOKEEPER_CLIENT_PORT", "2181")
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(TestcontainersConfiguration.CONTAINER_NAME_PREFIX+"-zookeeper");
                });
    }

    private DebeziumContainer createDebeziumContainer() {
        String containerName = DEBEZIUM.toString();
        DebeziumContainer container = new DebeziumContainer(DockerImageName.parse("debezium/connect").withTag(TestcontainersConfiguration.DEBEZIUM_IMAGE_TAG))
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withKafka(kafkaContainers.get(0))
                .withExposedPorts(TestcontainersConfiguration.DEBEZIUM_PORT)
                .dependsOn(kafkaContainers.get(0))
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(TestcontainersConfiguration.CONTAINER_NAME_PREFIX+"-"+containerName);
                });
        if(TestcontainersConfiguration.DEBEZIUM_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private GenericContainer createKafkaSchemaRegistryContainer() {
        String containerName = KAFKA_SCHEMA_REGISTRY.toString().replace("_", "-");
        GenericContainer container = new GenericContainer<>("confluentinc/cp-schema-registry:" + TestcontainersConfiguration.KAFKA_SCHEMA_REGISTRY_CONFLUENT_IMAGE_TAG)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(TestcontainersConfiguration.CONTAINER_NAME_PREFIX+"-"+containerName);
                })
                .withExposedPorts(TestcontainersConfiguration.KAFKA_SCHEMA_REGISTRY_PORT)
                .withEnv("SCHEMA_REGISTRY_HOST_NAME", containerName)
                .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", KAFKA.toString()+":9092")
                .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:"+ TestcontainersConfiguration.KAFKA_SCHEMA_REGISTRY_PORT)
                .dependsOn(kafkaContainers.get(0));
        if(TestcontainersConfiguration.KAFKA_SCHEMA_REGISTRY_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private GenericContainer createControlCenterContainer() {
        String containerName = KAFKA_CONTROL_CENTER.toString().replace("_", "-");
        // Force host port to be KAFKA_CONTROL_CENTER_PORT.
        Consumer<CreateContainerCmd> cmd = e -> e.withHostConfig(e.getHostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(TestcontainersConfiguration.KAFKA_CONTROL_CENTER_PORT),
                new ExposedPort(TestcontainersConfiguration.KAFKA_CONTROL_CENTER_PORT))))
                .withName(TestcontainersConfiguration.CONTAINER_NAME_PREFIX+"-"+containerName);
        GenericContainer container = new GenericContainer<>("confluentinc/cp-enterprise-control-center:" + TestcontainersConfiguration.KAFKA_CONTROL_CENTER_CONFLUENT_IMAGE_TAG)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCreateContainerCmdModifier(cmd)
                .withEnv("CONTROL_CENTER_BOOTSTRAP_SERVERS", KAFKA.toString()+":9092")
                .withEnv("CONTROL_CENTER_REPLICATION_FACTOR", "1")
                .withEnv("CONTROL_CENTER_INTERNAL_TOPICS_PARTITIONS", String.valueOf(TestcontainersConfiguration.KAFKA_TOPIC_PARTITION_COUNT))
                .withEnv("CONTROL_CENTER_MONITORING_INTERCEPTOR_TOPIC_PARTITIONS", String.valueOf(TestcontainersConfiguration.KAFKA_TOPIC_PARTITION_COUNT))
                .withEnv("CONFLUENT_METRICS_TOPIC_REPLICATION", "1")
                .withEnv("PORT", String.valueOf(TestcontainersConfiguration.KAFKA_CONTROL_CENTER_PORT))
                .withEnv("CONTROL_CENTER_REST_LISTENERS", "http://0.0.0.0:"+ TestcontainersConfiguration.KAFKA_CONTROL_CENTER_PORT)
                .withExposedPorts(TestcontainersConfiguration.KAFKA_CONTROL_CENTER_PORT)
                .withStartupTimeout(Duration.ofMinutes(3));
        if(TestcontainersConfiguration.KAFKA_SCHEMA_REGISTRY_ENABLED) {
            container.withEnv("CONTROL_CENTER_SCHEMA_REGISTRY_URL", "http://"+KAFKA_SCHEMA_REGISTRY.toString().replace("_", "-")+":"+ TestcontainersConfiguration.KAFKA_SCHEMA_REGISTRY_PORT);
        }
        if(TestcontainersConfiguration.KAFKA_CONTROL_CENTER_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private GenericContainer createConduktorContainer() {
        String containerName = CONDUKTOR.toString();
        int containerExposedPort = 8080;
        // Force host port to be CONDUKTOR_PORT.
        Consumer<CreateContainerCmd> cmd = e -> e.withHostConfig(e.getHostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(TestcontainersConfiguration.CONDUKTOR_PORT),
                new ExposedPort(containerExposedPort))))
                .withName(TestcontainersConfiguration.CONTAINER_NAME_PREFIX+"-"+containerName);

        GenericContainer container = new GenericContainer<>("conduktor/conduktor-platform:" + TestcontainersConfiguration.CONDUKTOR_IMAGE_TAG)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCreateContainerCmdModifier(cmd)
                .withEnv("CDK_ORGANIZATION_NAME", "component-test-framework")
                .withEnv("CDK_ADMIN_EMAIL", "admin@conduktor.io")
                .withEnv("CDK_ADMIN_PASSWORD", "admin")
                .withEnv("CDK_CLUSTERS_0_ID", "CTF")
                .withEnv("CDK_CLUSTERS_0_NAME", "Local Cluster")
                .withEnv("CDK_CLUSTERS_0_BOOTSTRAPSERVERS", KAFKA.toString()+":9092")
                .withExposedPorts(containerExposedPort);
        if(TestcontainersConfiguration.CONDUKTOR_LICENSE_KEY != null) {
            container.withEnv("LICENSE_KEY", TestcontainersConfiguration.CONDUKTOR_LICENSE_KEY);
        }
        if(TestcontainersConfiguration.KAFKA_SCHEMA_REGISTRY_ENABLED) {
            container.withEnv("SCHEMA_REGISTRY_URL", "http://"+KAFKA_SCHEMA_REGISTRY.toString().replace("_", "-")+":"+ TestcontainersConfiguration.KAFKA_SCHEMA_REGISTRY_PORT);
        }
        if(TestcontainersConfiguration.CONDUKTOR_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    /**
     * Conduktor Gateway.
     */
    private GenericContainer createConduktorGatewayContainer() {
        String containerName = CONDUKTORGATEWAY.toString();

        GenericContainer container = new GenericContainer<>("conduktor/conduktor-gateway:" + TestcontainersConfiguration.CONDUKTOR_GATEWAY_IMAGE_TAG)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(TestcontainersConfiguration.CONTAINER_NAME_PREFIX+"-"+containerName);
                })
                .withEnv("KAFKA_BOOTSTRAP_SERVERS", KAFKA.toString()+":9092")
                .withEnv("GATEWAY_HOST", CONDUKTORGATEWAY.toString())
                .withEnv("GATEWAY_PORT_RANGE", TestcontainersConfiguration.CONDUKTOR_GATEWAY_PROXY_PORT+":"+ TestcontainersConfiguration.CONDUKTOR_GATEWAY_PROXY_PORT)
                .withEnv("HTTP_PORT", String.valueOf(TestcontainersConfiguration.CONDUKTOR_GATEWAY_HTTP_PORT))
                .withEnv("FEATURE_FLAGS_SINGLE_TENANT", "true")
                .withEnv("AUTHENTICATION_AUTHENTICATOR_TYPE", "NONE")
                .withExposedPorts(TestcontainersConfiguration.CONDUKTOR_GATEWAY_PROXY_PORT, TestcontainersConfiguration.CONDUKTOR_GATEWAY_HTTP_PORT)
                .waitingFor(Wait.forListeningPort());
        if(TestcontainersConfiguration.CONDUKTOR_GATEWAY_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }

        return container;
    }

    private GenericContainer createWiremockContainer() {
        String containerName = WIREMOCK.toString();
        GenericContainer container = new GenericContainer<>("wiremock/wiremock:" + TestcontainersConfiguration.WIREMOCK_IMAGE_TAG)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(TestcontainersConfiguration.CONTAINER_NAME_PREFIX+"-"+containerName);
                })
                .withClasspathResourceMapping("/wiremock", "/home/wiremock/mappings", BindMode.READ_WRITE)
                .withExposedPorts(TestcontainersConfiguration.WIREMOCK_PORT)
                .waitingFor(Wait.forHttp("/health").forStatusCode(204));
        if(TestcontainersConfiguration.WIREMOCK_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private GenericContainer createLocalstackContainer() {
        String containerName = LOCALSTACK.toString();
        GenericContainer container = new GenericContainer<>("localstack/localstack:" + TestcontainersConfiguration.LOCALSTACK_IMAGE_TAG)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(TestcontainersConfiguration.CONTAINER_NAME_PREFIX+"-"+containerName);
                })
                .withEnv("SERVICES", TestcontainersConfiguration.LOCALSTACK_SERVICES)
                .withExposedPorts(TestcontainersConfiguration.LOCALSTACK_PORT);
        if(TestcontainersConfiguration.LOCALSTACK_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private GenericContainer createElasticsearchContainer() {
        String containerName = ELASTICSEARCH.toString();
        DockerImageName elasticsearchImage = DockerImageName.parse("elastic/elasticsearch").asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch");
        ElasticsearchContainer container = new ElasticsearchContainer(elasticsearchImage.withTag(TestcontainersConfiguration.ELASTICSEARCH_IMAGE_TAG))
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withEnv("cluster.name", TestcontainersConfiguration.ELASTICSEARCH_CLUSTER_NAME)
                .withEnv("discovery.type", TestcontainersConfiguration.ELASTICSEARCH_DISCOVERY_TYPE)
                .withEnv("xpack.security.enabled", "false")
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName(TestcontainersConfiguration.CONTAINER_NAME_PREFIX+"-"+containerName);
                });
        if(TestcontainersConfiguration.ELASTICSEARCH_PASSWORD != null && !TestcontainersConfiguration.ELASTICSEARCH_PASSWORD.isBlank()) {
            // This sets "xpack.security.enabled" to true
            container.withPassword(TestcontainersConfiguration.ELASTICSEARCH_PASSWORD);
        }
        if(TestcontainersConfiguration.ELASTICSEARCH_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private void createTopics() {
        if(!TestcontainersConfiguration.KAFKA_TOPICS.isEmpty()) {
            Properties properties = new Properties();
            properties.put(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainers.get(0).getBootstrapServers()
            );
            Admin admin = Admin.create(properties);
            Collection<NewTopic> newTopics = new ArrayList<>(TestcontainersConfiguration.KAFKA_TOPICS.size());
            int partitions = TestcontainersConfiguration.KAFKA_TOPIC_PARTITION_COUNT;
            short replicationFactor = 1;
            for (String topicName : TestcontainersConfiguration.KAFKA_TOPICS) {
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
            log.info("Created topics: " + TestcontainersConfiguration.KAFKA_TOPICS);
        }
    }

    private Slf4jLogConsumer getLogConsumer(String containerName) {
        return new Slf4jLogConsumer(LoggerFactory.getLogger("container."+containerName))
                .withRemoveAnsiCodes(false);
    }
}
