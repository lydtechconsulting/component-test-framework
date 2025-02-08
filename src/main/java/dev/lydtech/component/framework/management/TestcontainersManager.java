package dev.lydtech.component.framework.management;

import java.time.Duration;
import java.util.*;
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
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.security.plain.PlainLoginModule;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.*;
import static dev.lydtech.component.framework.resource.Resource.AMBAR;
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
import static dev.lydtech.component.framework.resource.Resource.MARIADB;
import static dev.lydtech.component.framework.resource.Resource.WIREMOCK;

@Slf4j
public final class TestcontainersManager {

    private Network network;
    private List<GenericContainer> serviceContainers = new ArrayList<>(1);
    private List<GenericContainer> additionalContainers;
    private GenericContainer postgresContainer;
    private MariaDBContainer mariaDBContainer;
    private MongoDBContainer mongoDbContainer;
    private List<GenericContainer> kafkaContainers;
    private List<GenericContainer> kafkaNativeContainers;
    private GenericContainer zookeeperContainer;
    private DebeziumContainer debeziumContainer;
    private GenericContainer kafkaSchemaRegistryContainer;
    private GenericContainer wiremockContainer;
    private GenericContainer localstackContainer;
    private GenericContainer controlCenterContainer;
    private GenericContainer conduktorPostgresContainer;
    private GenericContainer conduktorContainer;
    private GenericContainer conduktorGatewayContainer;
    private GenericContainer elasticSearchContainer;
    private GenericContainer ambarContainer;

    private TestcontainersManager(){}

    public static void initialise() {
        TestcontainersManager manager = new TestcontainersManager();
        log.info("Creating testcontainers...");
        manager.createContainers();
        log.info("Starting testcontainers...");
        manager.startContainers();
        log.info("Started testcontainers.");
    }

    private void createContainers() {

        if(CONTAINERS_STAYUP && CONTAINER_APPEND_GROUP_ID) {
            throw new RuntimeException("Either configure containers to stayup or enable concurrent test runs.");
        }
        if (SERVICE_INSTANCE_COUNT < 1) {
            throw new RuntimeException("At least one service container should be started");
        }
        network = Network.newNetwork();
        if (POSTGRES_ENABLED) {
            postgresContainer = createPostgresContainer();
        }
        if (MONGODB_ENABLED) {
            mongoDbContainer = createMongoDBContainer();
        }
        if (MARIADB_ENABLED) {
            mariaDBContainer = createMariaDBContainer();
        }
        if (KAFKA_ENABLED && KAFKA_NATIVE_ENABLED) {
            throw new RuntimeException("Cannot enable both kafka and kafka native:  kafka.enabled: "+KAFKA_ENABLED+" - kafka.native.enabled: "+KAFKA_NATIVE_ENABLED);
        }
        if (KAFKA_ENABLED || KAFKA_NATIVE_ENABLED) {
            if(KAFKA_TOPIC_REPLICATION_FACTOR > KAFKA_BROKER_COUNT) {
                throw new RuntimeException("kafka.topic.replication.factor: "+KAFKA_TOPIC_REPLICATION_FACTOR+" - must not be greater than kafka.broker.count: "+KAFKA_BROKER_COUNT);
            }
            if(KAFKA_MIN_INSYNC_REPLICAS > KAFKA_BROKER_COUNT) {
                throw new RuntimeException("kafka.min.insync.replicas: "+KAFKA_MIN_INSYNC_REPLICAS+" - must not be greater than kafka.broker.count: "+KAFKA_BROKER_COUNT);
            }
            if(KAFKA_MIN_INSYNC_REPLICAS > KAFKA_TOPIC_REPLICATION_FACTOR) {
                throw new RuntimeException("kafka.min.insync.replicas: "+KAFKA_MIN_INSYNC_REPLICAS+" - must not be greater than kafka.topic.replication.factor: "+KAFKA_TOPIC_REPLICATION_FACTOR);
            }
            if(KAFKA_ENABLED && KAFKA_BROKER_COUNT>1) {
                // As there are multiple standard Kafka instances they need to use the same external Zookeeper.
                zookeeperContainer = createZookeeperContainer();
            }

            if(KAFKA_ENABLED && KAFKA_CONTROL_CENTER_ENABLED && KAFKA_CONTROL_CENTER_EXPORT_METRICS_ENABLED) {
                // To support exporting metrics for Confluent Control Center, use the Confluent cp-server container.
                kafkaContainers = IntStream.range(1, KAFKA_BROKER_COUNT +1)
                    .mapToObj(this::createKafkaServerContainer)
                    .collect(Collectors.toList());
            } else if(KAFKA_ENABLED) {
                kafkaContainers = IntStream.range(1, KAFKA_BROKER_COUNT +1)
                    .mapToObj(this::createKafkaContainer)
                    .collect(Collectors.toList());
            } else if(KAFKA_NATIVE_ENABLED){
                kafkaNativeContainers = IntStream.range(1, KAFKA_BROKER_COUNT +1)
                    .mapToObj(this::createNativeKafkaContainer)
                    .collect(Collectors.toList());
            }
        }
        if (DEBEZIUM_ENABLED) {
            if(!KAFKA_ENABLED && !KAFKA_NATIVE_ENABLED) {
                throw new RuntimeException("Kafka must be enabled in order to use Debezium.");
            }
            debeziumContainer = createDebeziumContainer();
        }
        if (KAFKA_SCHEMA_REGISTRY_ENABLED) {
            if(!KAFKA_ENABLED && !KAFKA_NATIVE_ENABLED) {
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
            if (!KAFKA_ENABLED && !KAFKA_NATIVE_ENABLED) {
                throw new RuntimeException("Kafka must be enabled in order to use Control Center.");
            }
            controlCenterContainer = createControlCenterContainer();
        }
        if (CONDUKTOR_ENABLED) {
            if (!KAFKA_ENABLED && !KAFKA_NATIVE_ENABLED) {
                throw new RuntimeException("Kafka must be enabled in order to use Conduktor.");
            }
            conduktorPostgresContainer = createConduktorPostgresContainer();
            conduktorContainer = createConduktorContainer();
        }
        if (CONDUKTOR_GATEWAY_ENABLED) {
            if (!KAFKA_ENABLED && !KAFKA_NATIVE_ENABLED) {
                throw new RuntimeException("Kafka must be enabled in order to use Conduktor Gateway.");
            }
            conduktorGatewayContainer = createConduktorGatewayContainer();
        }
        if (ELASTICSEARCH_ENABLED) {
            elasticSearchContainer = createElasticsearchContainer();
        }
        if(AMBAR_ENABLED) {
            ambarContainer = createAmbarContainer();
        }

        serviceContainers = IntStream.range(1, SERVICE_INSTANCE_COUNT + 1)
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
            if(MONGODB_ENABLED) {
                mongoDbContainer.start();
            }
            if(MARIADB_ENABLED) {
                mariaDBContainer.start();
            }
            if(KAFKA_ENABLED) {
                if(KAFKA_BROKER_COUNT>1) {
                    // As there are multiple Kafka instances they need to use the same external Zookeeper.
                    zookeeperContainer.start();
                }
                kafkaContainers.stream().forEach(container -> container.start());
                createTopics();
            } else if(KAFKA_NATIVE_ENABLED) {
                kafkaNativeContainers.stream().forEach(container -> container.start());
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
                conduktorPostgresContainer.start();
                conduktorContainer.start();
            }
            if(CONDUKTOR_GATEWAY_ENABLED) {
                conduktorGatewayContainer.start();
            }
            if(WIREMOCK_ENABLED) {
                wiremockContainer.start();
            }
            if(LOCALSTACK_ENABLED) {
                localstackContainer.start();
            }
            if(ELASTICSEARCH_ENABLED) {
                elasticSearchContainer.start();
            }
            if(AMBAR_ENABLED) {
                ambarContainer.start();
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
        String suspendFlag = SERVICE_DEBUG_SUSPEND ? "y" : "n";
        String javaOpts = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=" + suspendFlag + ",address=*:"+SERVICE_DEBUG_PORT+" -Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom -D"+SERVICE_CONFIG_FILES_SYSTEM_PROPERTY+"=file:/application.yml";
        SERVICE_ENV_VARS.put("JAVA_OPTS", javaOpts);

        GenericContainer container = new GenericContainer<>(CONTAINER_NAME_PREFIX+"/"+SERVICE_NAME+":" + SERVICE_IMAGE_TAG)
                .withEnv(SERVICE_ENV_VARS)
                .withLabel(CONTAINER_MAIN_LABEL_KEY, CONTAINER_MAIN_LABEL)
                .withFileSystemBind(SERVICE_APPLICATION_YML_PATH, "/application.yml", BindMode.READ_ONLY)
                .withExposedPorts(SERVICE_PORT, SERVICE_DEBUG_PORT)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withReuse(true)
                .withCreateContainerCmdModifier(cmd -> {
                    String containerCmdModifier = CONTAINER_APPEND_GROUP_ID ?CONTAINER_NAME_PREFIX + "-" + containerName + "-" + CONTAINER_GROUP_ID :CONTAINER_NAME_PREFIX + "-" + containerName;
                    cmd.withName(containerCmdModifier);
                });

        SERVICE_ADDITIONAL_FILESYSTEM_BINDS.keySet().forEach(source -> container.withFileSystemBind(source, SERVICE_ADDITIONAL_FILESYSTEM_BINDS.get(source), BindMode.READ_ONLY));
        if(SERVICE_APPLICATION_ARGS != null) {
            container.withEnv("APP_ARGS", SERVICE_APPLICATION_ARGS);
        }
        if (SERVICE_STARTUP_LOG_MESSAGE != null) {
            container.waitingFor(Wait.forLogMessage(SERVICE_STARTUP_LOG_MESSAGE, 1))
                    .withStartupTimeout(Duration.ofSeconds(SERVICE_STARTUP_TIMEOUT_SECONDS));
        } else {
            container.waitingFor(Wait.forHttp(SERVICE_STARTUP_HEALTH_ENDPOINT)
                    .forPort(SERVICE_PORT)
                    .forStatusCode(200)
                    .withStartupTimeout(Duration.ofSeconds(SERVICE_STARTUP_TIMEOUT_SECONDS)));
        }
        if(SERVICE_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private GenericContainer createAdditionalContainer(String name, Integer port, Integer debugPort, String imageTag, boolean containerLoggingEnabled) {
        String javaOpts = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:"+debugPort+" -Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom -Dspring.config.additional-location=file:/application.yml";

        GenericContainer container = new GenericContainer<>(CONTAINER_NAME_PREFIX+"/"+name+":" + imageTag)
                .withEnv("JAVA_OPTS", javaOpts)
                .withLabel("additional-container-label", "additional-container")
                .withFileSystemBind("./target/test-classes/"+name+"/application-component-test.yml", "/application.yml", BindMode.READ_ONLY)
                .withExposedPorts(port, debugPort)
                .withNetwork(network)
                .withNetworkAliases(name)
                .withCreateContainerCmdModifier(cmd -> {
                    String containerCmdModifier = CONTAINER_APPEND_GROUP_ID ?CONTAINER_NAME_PREFIX + "-" + name + "-" + CONTAINER_GROUP_ID :CONTAINER_NAME_PREFIX + "-" + name;
                    cmd.withName(containerCmdModifier);
                })
                .withReuse(true)
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

        // If Debezium is enabled, use the Debezium / Postgres container.  Without it Debezium fails due to the wal_level property (logical vs replica).
        DockerImageName dockerImageName = DEBEZIUM_ENABLED?DockerImageName.parse("debezium/postgres").asCompatibleSubstituteFor("postgres"):DockerImageName.parse("postgres");
        GenericContainer container = new PostgreSQLContainer<>(dockerImageName.withTag(POSTGRES_IMAGE_TAG))
                .withDatabaseName(POSTGRES_DATABASE_NAME)
                .withUsername(POSTGRES_USERNAME)
                .withPassword(POSTGRES_PASSWORD)
                .withNetwork(network)
                .withNetworkAliases(POSTGRES_HOST_NAME)
                .withCreateContainerCmdModifier(cmd -> {
                    String containerCmdModifier = CONTAINER_APPEND_GROUP_ID ?CONTAINER_NAME_PREFIX + "-" + containerName + "-" + CONTAINER_GROUP_ID :CONTAINER_NAME_PREFIX + "-" + containerName;
                    cmd.withName(containerCmdModifier);
                })
                .withReuse(true)
                .withExposedPorts(POSTGRES_PORT);
        if(POSTGRES_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        if(POSTGRES_SCHEMA_FILE_PATH != null) {
            ((PostgreSQLContainer)container).withInitScript(POSTGRES_SCHEMA_FILE_PATH);
        }
        return container;
    }


    private MongoDBContainer createMongoDBContainer() {
        String containerName = MONGODB.toString();
        MongoDBContainer container = new MongoDBContainer("mongo:" + MONGODB_IMAGE_TAG)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withReuse(true)
                .withCreateContainerCmdModifier(cmd -> {
                    String containerCmdModifier = CONTAINER_APPEND_GROUP_ID ?CONTAINER_NAME_PREFIX + "-" + containerName + "-" + CONTAINER_GROUP_ID :CONTAINER_NAME_PREFIX + "-" + containerName;
                    cmd.withName(containerCmdModifier);
                });
        if(MONGODB_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private MariaDBContainer createMariaDBContainer() {
        String containerName = MARIADB.toString();

        var container = new MariaDBContainer<>("mariadb:" + MARIADB_IMAGE_TAG)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withReuse(true)
                .withCreateContainerCmdModifier(cmd -> {
                    String containerCmdModifier = CONTAINER_APPEND_GROUP_ID ?CONTAINER_NAME_PREFIX + "-" + containerName + "-" + CONTAINER_GROUP_ID :CONTAINER_NAME_PREFIX + "-" + containerName;
                    cmd.withName(containerCmdModifier);
                });
        if(MARIADB_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    /**
     * Standard Kafka and Zookeeper.
     */
    private GenericContainer createKafkaContainer(int instance) {
        final String containerName = instance==1?KAFKA.toString():KAFKA+"-"+instance;
       GenericContainer container = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka").withTag(KAFKA_CONFLUENT_IMAGE_TAG))
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withReuse(true)
                .withCreateContainerCmdModifier(cmd -> {
                    String containerCmdModifier = CONTAINER_APPEND_GROUP_ID ?CONTAINER_NAME_PREFIX + "-" + containerName + "-" + CONTAINER_GROUP_ID :CONTAINER_NAME_PREFIX + "-" + containerName;
                    cmd.withName(containerCmdModifier);
                });
        container = configureCommonKafkaContainerEnv(container, instance);
        if(KAFKA_BROKER_COUNT>1) {
            // As there are multiple Kafka instances they need to use the same external Zookeeper.
            ((KafkaContainer)container).withExternalZookeeper("zookeeper:2181");
        }
        if(KAFKA_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    /**
     * Native Kafka.
     */
    private GenericContainer createNativeKafkaContainer(int instance) {
        final String containerName = instance==1?KAFKA.toString():KAFKA+"-"+instance;
        DockerImageName nativeImage = DockerImageName.parse("apache/kafka-native").asCompatibleSubstituteFor("apache/kafka");
        GenericContainer container = new org.testcontainers.kafka.KafkaContainer(nativeImage.withTag(KAFKA_APACHE_NATIVE_IMAGE_TAG))
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withReuse(true)
                .withCreateContainerCmdModifier(cmd -> {
                    String containerCmdModifier = CONTAINER_APPEND_GROUP_ID ?CONTAINER_NAME_PREFIX + "-" + containerName + "-" + CONTAINER_GROUP_ID :CONTAINER_NAME_PREFIX + "-" + containerName;
                    cmd.withName(containerCmdModifier);
                })
                .withEnv("KAFKA_PROCESS_ROLES", "broker,controller");
        container = configureCommonKafkaContainerEnv(container, instance);
        if(KAFKA_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    /**
     * The Confluent cp-server container, with additional support for metrics.
     *
     * The main project must depend on Confluent's kafka-clients community package (e.g. 7.3.2-ccs) and monitoring-interceptors (e.g. 7.3.2) libraries.
     */
    private GenericContainer createKafkaServerContainer(int instance) {
        final String containerName = instance==1?KAFKA.toString():KAFKA+"-"+instance;
        DockerImageName cpServerImage = DockerImageName.parse("confluentinc/cp-server").asCompatibleSubstituteFor("confluentinc/cp-kafka");
        GenericContainer container = new KafkaContainer(cpServerImage.withTag(KAFKA_CONFLUENT_IMAGE_TAG))
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withEnv("KAFKA_CONFLUENT_LICENSE_TOPIC_REPLICATION_FACTOR", "1")
                .withEnv("KAFKA_CONFLUENT_BALANCER_TOPIC_REPLICATION_FACTOR", "1")
                .withEnv("KAFKA_JMX_PORT", KAFKA_CONTROL_CENTER_JMX_PORT)
                .withEnv("KAFKA_JMX_HOSTNAME", "localhost")
                .withEnv("CONFLUENT_METRICS_REPORTER_BOOTSTRAP_SERVERS", containerName+":"+KAFKA_INTERNAL_PORT)
                .withEnv("CONFLUENT_METRICS_REPORTER_TOPIC_REPLICAS", "1")
                .withEnv("KAFKA_METRIC_REPORTERS", "io.confluent.metrics.reporter.ConfluentMetricsReporter")
                .withEnv("CONFLUENT_METRICS_ENABLE", "true")
                .withReuse(true)
                .withCreateContainerCmdModifier(cmd -> {
                    String containerCmdModifier = CONTAINER_APPEND_GROUP_ID ?CONTAINER_NAME_PREFIX + "-" + containerName + "-" + CONTAINER_GROUP_ID :CONTAINER_NAME_PREFIX + "-" + containerName;
                    cmd.withName(containerCmdModifier);
                });
                container.withEnv("KAFKA_METRIC_REPORTERS", "io.confluent.metrics.reporter.ConfluentMetricsReporter");
        container = configureCommonKafkaContainerEnv(container, instance);
        if(KAFKA_BROKER_COUNT>1) {
            // As there are multiple Kafka instances they need to use the same external Zookeeper.
            ((KafkaContainer)container).withExternalZookeeper("zookeeper:2181");
        }
        if(KAFKA_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    /**
     * Set up the Env that is the same for both the Confluent cp-kafka and cp-server container types.
     */
    private GenericContainer configureCommonKafkaContainerEnv(GenericContainer kafkaContainer, int instance) {
        if(KAFKA_SASL_PLAIN_ENABLED) {
            if(KAFKA_SASL_PLAIN_USERNAME == null || KAFKA_SASL_PLAIN_USERNAME.isBlank() ||
                    KAFKA_SASL_PLAIN_PASSWORD == null || KAFKA_SASL_PLAIN_PASSWORD.isBlank()) {
                throw new RuntimeException("kafka.sasl.plain.enabled is true so kafka.sasl.plain.username: "+KAFKA_SASL_PLAIN_USERNAME+" - and kafka.sasl.plain.password: "+KAFKA_SASL_PLAIN_PASSWORD+" - must both be set.");
            }
            String jaasConfig = String.format(
                "%s required username=\"%s\" password=\"%s\" user_%s=\"%s\";",
                PlainLoginModule.class.getName(),
                KAFKA_SASL_PLAIN_USERNAME,
                KAFKA_SASL_PLAIN_PASSWORD,
                KAFKA_SASL_PLAIN_USERNAME,
                KAFKA_SASL_PLAIN_PASSWORD
            );
            kafkaContainer.withEnv("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "PLAINTEXT:SASL_PLAINTEXT,BROKER:SASL_PLAINTEXT");
            kafkaContainer.withEnv("KAFKA_SASL_MECHANISM_INTER_BROKER_PROTOCOL", "PLAIN");
            kafkaContainer.withEnv("KAFKA_LISTENER_NAME_BROKER_SASL_ENABLED_MECHANISMS", "PLAIN");
            kafkaContainer.withEnv("KAFKA_LISTENER_NAME_BROKER_PLAIN_SASL_JAAS_CONFIG", jaasConfig);
            kafkaContainer.withEnv("KAFKA_LISTENER_NAME_PLAINTEXT_SASL_ENABLED_MECHANISMS", "PLAIN");
            kafkaContainer.withEnv("KAFKA_LISTENER_NAME_PLAINTEXT_PLAIN_SASL_JAAS_CONFIG", jaasConfig);
        }

        return kafkaContainer
                .withEnv("KAFKA_BROKER_ID", String.valueOf(instance))
                .withEnv("KAFKA_NODE_ID", String.valueOf(instance))
                .withEnv("KAFKA_NUM_PARTITIONS", String.valueOf(KAFKA_TOPIC_PARTITION_COUNT))
                .withEnv("KAFKA_DEFAULT_REPLICATION_FACTOR", String.valueOf(KAFKA_TOPIC_REPLICATION_FACTOR))
                .withEnv("KAFKA_MIN_INSYNC_REPLICAS", String.valueOf(KAFKA_MIN_INSYNC_REPLICAS))
                .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", String.valueOf(KAFKA_TOPIC_REPLICATION_FACTOR))
                .withEnv("KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS", "0")
                .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", String.valueOf(KAFKA_MIN_INSYNC_REPLICAS))
                .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", String.valueOf(KAFKA_TOPIC_REPLICATION_FACTOR));
    }


    private GenericContainer createZookeeperContainer() {
        return new GenericContainer<>("confluentinc/cp-zookeeper:4.0.0")
                .withNetwork(network)
                .withNetworkAliases("zookeeper")
                .withEnv("ZOOKEEPER_CLIENT_PORT", "2181")
                .withReuse(true)
                .withCreateContainerCmdModifier(cmd -> {
                    String containerCmdModifier = CONTAINER_APPEND_GROUP_ID ?CONTAINER_NAME_PREFIX + "-zookeeper-" + CONTAINER_GROUP_ID :CONTAINER_NAME_PREFIX + "-zookeeper";
                    cmd.withName(containerCmdModifier);
                });
    }

    private DebeziumContainer createDebeziumContainer() {
        String containerName = DEBEZIUM.toString();
        int kafkaInternalPort = KAFKA_ENABLED?KAFKA_INTERNAL_PORT:KAFKA_NATIVE_INTERNAL_PORT;
        DebeziumContainer container = new DebeziumContainer(DockerImageName.parse("debezium/connect").withTag(DEBEZIUM_IMAGE_TAG))
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withKafka(network, KAFKA+":"+kafkaInternalPort)
                .withExposedPorts(DEBEZIUM_PORT)
                .dependsOn(KAFKA_ENABLED?kafkaContainers.get(0):kafkaNativeContainers.get(0))
                .withReuse(true)
                .withCreateContainerCmdModifier(cmd -> {
                    String containerCmdModifier = CONTAINER_APPEND_GROUP_ID ?CONTAINER_NAME_PREFIX + "-" + containerName + "-" + CONTAINER_GROUP_ID :CONTAINER_NAME_PREFIX + "-" + containerName;
                    cmd.withName(containerCmdModifier);
                });
        if(DEBEZIUM_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private GenericContainer createKafkaSchemaRegistryContainer() {
        String containerName = KAFKA_SCHEMA_REGISTRY.toString().replace("_", "-");
        int kafkaInternalPort = KAFKA_ENABLED?KAFKA_INTERNAL_PORT:KAFKA_NATIVE_INTERNAL_PORT;
        GenericContainer container = new GenericContainer<>("confluentinc/cp-schema-registry:" + KAFKA_SCHEMA_REGISTRY_CONFLUENT_IMAGE_TAG)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCreateContainerCmdModifier(cmd -> {
                    String containerCmdModifier = CONTAINER_APPEND_GROUP_ID ?CONTAINER_NAME_PREFIX + "-" + containerName + "-" + CONTAINER_GROUP_ID :CONTAINER_NAME_PREFIX + "-" + containerName;
                    cmd.withName(containerCmdModifier);
                })
                .withExposedPorts(KAFKA_SCHEMA_REGISTRY_PORT)
                .withEnv("SCHEMA_REGISTRY_HOST_NAME", containerName)
                .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", KAFKA+":"+kafkaInternalPort)
                .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:"+KAFKA_SCHEMA_REGISTRY_PORT)
                .withReuse(true)
                .dependsOn(KAFKA_ENABLED?kafkaContainers.get(0):kafkaNativeContainers.get(0));
        if(KAFKA_SCHEMA_REGISTRY_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private GenericContainer createControlCenterContainer() {
        String containerName = KAFKA_CONTROL_CENTER.toString().replace("_", "-");
        String containerCmdModifier = CONTAINER_APPEND_GROUP_ID ?CONTAINER_NAME_PREFIX + "-" + containerName + "-" + CONTAINER_GROUP_ID :CONTAINER_NAME_PREFIX + "-" + containerName;
        Consumer<CreateContainerCmd> cmd = e -> {
            // Force host port to be KAFKA_CONTROL_CENTER_PORT.
            e.withHostConfig(e.getHostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(KAFKA_CONTROL_CENTER_PORT),
                            new ExposedPort(KAFKA_CONTROL_CENTER_PORT))))
                    .withName(containerCmdModifier);
        };

        int kafkaInternalPort = KAFKA_ENABLED?KAFKA_INTERNAL_PORT:KAFKA_NATIVE_INTERNAL_PORT;
        GenericContainer container = new GenericContainer<>("confluentinc/cp-enterprise-control-center:" + KAFKA_CONTROL_CENTER_CONFLUENT_IMAGE_TAG)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCreateContainerCmdModifier(cmd)
                .withEnv("CONTROL_CENTER_BOOTSTRAP_SERVERS", KAFKA+":"+kafkaInternalPort)
                .withEnv("CONTROL_CENTER_REPLICATION_FACTOR", "1")
                .withEnv("CONTROL_CENTER_INTERNAL_TOPICS_PARTITIONS", String.valueOf(KAFKA_TOPIC_PARTITION_COUNT))
                .withEnv("CONTROL_CENTER_MONITORING_INTERCEPTOR_TOPIC_PARTITIONS", String.valueOf(KAFKA_TOPIC_PARTITION_COUNT))
                .withEnv("CONFLUENT_METRICS_TOPIC_REPLICATION", "1")
                .withEnv("PORT", String.valueOf(KAFKA_CONTROL_CENTER_PORT))
                .withEnv("CONTROL_CENTER_REST_LISTENERS", "http://0.0.0.0:"+KAFKA_CONTROL_CENTER_PORT)
                .withExposedPorts(KAFKA_CONTROL_CENTER_PORT)
                .withReuse(true)
                .withStartupTimeout(Duration.ofMinutes(3));
        if(KAFKA_SCHEMA_REGISTRY_ENABLED) {
            container.withEnv("CONTROL_CENTER_SCHEMA_REGISTRY_URL", "http://"+KAFKA_SCHEMA_REGISTRY.toString().replace("_", "-")+":"+KAFKA_SCHEMA_REGISTRY_PORT);
        }
        if(KAFKA_CONTROL_CENTER_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private GenericContainer createConduktorPostgresContainer() {
        String containerName = CONDUKTOR + "-postgres";
        String containerCmdModifier =
                CONTAINER_APPEND_GROUP_ID ? CONTAINER_NAME_PREFIX + "-" + containerName + "-" + CONTAINER_GROUP_ID
                        : CONTAINER_NAME_PREFIX + "-" + containerName;
        Consumer<CreateContainerCmd> cmd = e -> {
            e.withName(containerCmdModifier);
        };

        return new GenericContainer<>(DEFAULT_CONDUKTOR_POSTGRES_IMAGE_TAG)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCreateContainerCmdModifier(cmd)
                .withEnv("POSTGRES_DB", DEFAULT_CONDUKTOR_POSTGRES_DB)
                .withEnv("POSTGRES_USER", DEFAULT_CONDUKTOR_POSTGRES_USER)
                .withEnv("POSTGRES_PASSWORD", DEFAULT_CONDUKTOR_POSTGRES_PASSWORD)
                .withEnv("POSTGRES_HOST_AUTH_METHOD", "scram-sha-256")
                .withReuse(true);
    }

    private GenericContainer createConduktorContainer() {
        String containerName = CONDUKTOR.toString();
        int containerExposedPort = 8080;
        String containerCmdModifier = CONTAINER_APPEND_GROUP_ID ?CONTAINER_NAME_PREFIX + "-" + containerName + "-" + CONTAINER_GROUP_ID :CONTAINER_NAME_PREFIX + "-" + containerName;
        Consumer<CreateContainerCmd> cmd = e -> {
            // Force host port to be CONDUKTOR_PORT.
            e.withHostConfig(e.getHostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(CONDUKTOR_PORT),
                            new ExposedPort(containerExposedPort))))
                    .withName(containerCmdModifier);
        };

        int kafkaInternalPort = KAFKA_ENABLED?KAFKA_INTERNAL_PORT:KAFKA_NATIVE_INTERNAL_PORT;
        GenericContainer container = new GenericContainer<>("conduktor/conduktor-platform:" + CONDUKTOR_IMAGE_TAG)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCreateContainerCmdModifier(cmd)
                .withEnv("CDK_ORGANIZATION_NAME", "component-test-framework")
                .withEnv("CDK_ADMIN_EMAIL", "admin@conduktor.io")
                .withEnv("CDK_ADMIN_PASSWORD", "admin")
                .withEnv("CDK_CLUSTERS_0_ID", "CTF")
                .withEnv("CDK_CLUSTERS_0_NAME", "Local Cluster")
                .withEnv("CDK_CLUSTERS_0_BOOTSTRAPSERVERS", KAFKA + ":" + kafkaInternalPort)
                .withEnv("CDK_DATABASE_URL", "postgresql://" + DEFAULT_CONDUKTOR_POSTGRES_USER + ":" + DEFAULT_CONDUKTOR_POSTGRES_PASSWORD + "@" + CONDUKTOR + "-postgres" + ":5432/" + DEFAULT_CONDUKTOR_POSTGRES_DB)
                .withReuse(true)
                .withExposedPorts(containerExposedPort);
        if (CONDUKTOR_LICENSE_KEY != null) {
            container.withEnv("LICENSE_KEY", CONDUKTOR_LICENSE_KEY);
        }
        if (KAFKA_SCHEMA_REGISTRY_ENABLED) {
            container.withEnv("CDK_CLUSTERS_0_SCHEMAREGISTRY_URL",
                    "http://" + KAFKA_SCHEMA_REGISTRY.toString().replace("_", "-") + ":" + KAFKA_SCHEMA_REGISTRY_PORT);
        }
        if (CONDUKTOR_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    /**
     * Conduktor Gateway.
     */
    private GenericContainer createConduktorGatewayContainer() {
        String containerName = CONDUKTORGATEWAY.toString();

        int kafkaInternalPort = KAFKA_ENABLED?KAFKA_INTERNAL_PORT:KAFKA_NATIVE_INTERNAL_PORT;
        GenericContainer container = new GenericContainer<>("conduktor/conduktor-gateway:" + CONDUKTOR_GATEWAY_IMAGE_TAG)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withCreateContainerCmdModifier(cmd -> {
                    String containerCmdModifier = CONTAINER_APPEND_GROUP_ID ?CONTAINER_NAME_PREFIX + "-" + containerName + "-" + CONTAINER_GROUP_ID :CONTAINER_NAME_PREFIX + "-" + containerName;
                    cmd.withName(containerCmdModifier);
                })
                .withEnv("KAFKA_BOOTSTRAP_SERVERS", KAFKA+":"+ kafkaInternalPort)
                .withEnv("GATEWAY_HOST", CONDUKTORGATEWAY.toString())
                .withEnv("GATEWAY_PORT_RANGE", CONDUKTOR_GATEWAY_PROXY_PORT+":"+CONDUKTOR_GATEWAY_PROXY_PORT)
                .withEnv("HTTP_PORT", String.valueOf(CONDUKTOR_GATEWAY_HTTP_PORT))
                .withEnv("FEATURE_FLAGS_SINGLE_TENANT", "true")
                .withEnv("AUTHENTICATION_AUTHENTICATOR_TYPE", "NONE")
                .withExposedPorts(CONDUKTOR_GATEWAY_PROXY_PORT, CONDUKTOR_GATEWAY_HTTP_PORT)
                .withReuse(true)
                .waitingFor(Wait.forListeningPort());
        if(CONDUKTOR_GATEWAY_CONTAINER_LOGGING_ENABLED) {
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
                    String containerCmdModifier = CONTAINER_APPEND_GROUP_ID ?CONTAINER_NAME_PREFIX + "-" + containerName + "-" + CONTAINER_GROUP_ID :CONTAINER_NAME_PREFIX + "-" + containerName;
                    cmd.withName(containerCmdModifier);
                })
                .withClasspathResourceMapping("/wiremock", "/home/wiremock/mappings", BindMode.READ_WRITE)
                .withExposedPorts(WIREMOCK_PORT)
                .withEnv("WIREMOCK_OPTIONS", WIREMOCK_OPTIONS)
                .withReuse(true)
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
                    String containerCmdModifier = CONTAINER_APPEND_GROUP_ID ?CONTAINER_NAME_PREFIX + "-" + containerName + "-" + CONTAINER_GROUP_ID :CONTAINER_NAME_PREFIX + "-" + containerName;
                    cmd.withName(containerCmdModifier);
                })
                .withEnv("SERVICES", LOCALSTACK_SERVICES)
                .withReuse(true)
                .withExposedPorts(LOCALSTACK_PORT);
        if(LOCALSTACK_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        if (LOCALSTACK_INIT_FILE_PATH != null) {
            container.withFileSystemBind(LOCALSTACK_INIT_FILE_PATH, "/docker-entrypoint-initaws.d/init.sh");
        }
        return container;
    }

    private GenericContainer createElasticsearchContainer() {
        String containerName = ELASTICSEARCH.toString();
        DockerImageName elasticsearchImage = DockerImageName.parse("elastic/elasticsearch").asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch");
        ElasticsearchContainer container = new ElasticsearchContainer(elasticsearchImage.withTag(ELASTICSEARCH_IMAGE_TAG))
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withEnv("cluster.name", ELASTICSEARCH_CLUSTER_NAME)
                .withEnv("discovery.type", ELASTICSEARCH_DISCOVERY_TYPE)
                .withEnv("xpack.security.enabled", "false")
                .withReuse(true)
                .withCreateContainerCmdModifier(cmd -> {
                    String containerCmdModifier = CONTAINER_APPEND_GROUP_ID ?CONTAINER_NAME_PREFIX + "-" + containerName + "-" + CONTAINER_GROUP_ID :CONTAINER_NAME_PREFIX + "-" + containerName;
                    cmd.withName(containerCmdModifier);
                });
        if(ELASTICSEARCH_PASSWORD != null && !ELASTICSEARCH_PASSWORD.isBlank()) {
            // This sets "xpack.security.enabled" to true
            container.withPassword(ELASTICSEARCH_PASSWORD);
        }
        if(ELASTICSEARCH_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private GenericContainer createAmbarContainer() {
        String containerName = AMBAR.toString();
        GenericContainer container = new GenericContainer<>("ambarltd/emulator:" + AMBAR_IMAGE_TAG)
                .withNetwork(network)
                .withNetworkAliases(containerName)
                .withFileSystemBind(AMBAR_CONFIG_FILE_PATH, "/opt/emulator/config/config.yaml", BindMode.READ_ONLY)
                .withCreateContainerCmdModifier(cmd -> {
                    String containerCmdModifier = CONTAINER_APPEND_GROUP_ID ?CONTAINER_NAME_PREFIX + "-" + containerName + "-" + CONTAINER_GROUP_ID :CONTAINER_NAME_PREFIX + "-" + containerName;
                    cmd.withName(containerCmdModifier);
                })
                .withReuse(true);
        if(AMBAR_CONTAINER_LOGGING_ENABLED) {
            container.withLogConsumer(getLogConsumer(containerName));
        }
        return container;
    }

    private void createTopics() {
        if(!KAFKA_TOPICS.isEmpty()) {

            String bootstrapServers;
            if(KAFKA_ENABLED) {
                bootstrapServers = ((KafkaContainer)kafkaContainers.get(0)).getBootstrapServers();
            } else {
                bootstrapServers = ((org.testcontainers.kafka.KafkaContainer)kafkaNativeContainers.get(0)).getBootstrapServers();
            }
            Properties properties = new Properties();
            properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            if(KAFKA_SASL_PLAIN_ENABLED) {
                properties.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
                properties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
                String jaasConfig = String.format(
                        "%s required username=\"%s\" password=\"%s\";",
                        PlainLoginModule.class.getName(),
                        KAFKA_SASL_PLAIN_USERNAME,
                        KAFKA_SASL_PLAIN_PASSWORD
                );
                properties.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);
            }
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
