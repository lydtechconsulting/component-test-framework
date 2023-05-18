package dev.lydtech.component.framework.extension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class TestContainersConfiguration {

    // Label key for the Docker container housing the service.
    protected static final String CONTAINER_MAIN_LABEL_KEY = "dev.lydtech.main-container-label";

    /**
     * Service default configuration.
     */

    private static final String DEFAULT_SERVICE_NAME = "app";
    private static final String DEFAULT_SERVICE_INSTANCE_COUNT = "1";
    private static final String DEFAULT_SERVICE_PORT = "9001";
    private static final String DEFAULT_SERVICE_DEBUG_PORT = "5001";
    private static final String DEFAULT_SERVICE_STARTUP_TIMEOUT_SECONDS = "180";
    private static final String DEFAULT_SERVICE_IMAGE_TAG = "latest";
    private static final String DEFAULT_SERVICE_CONTAINER_LOGGING_ENABLED = "false";

    /**
     * Container configuration.
     */

    private static final String DEFAULT_CONTAINER_NAME_PREFIX = "ct";
    private static final String DEFAULT_CONTAINER_MAIN_LABEL = "main-container";

    /**
     * Postgres configuration.
     */
    private static final String DEFAULT_POSTGRES_ENABLED = "false";
    private static final String DEFAULT_POSTGRES_IMAGE_TAG = "14-alpine";
    private static final String DEFAULT_POSTGRES_HOST_NAME = "postgres-host";
    private static final String DEFAULT_POSTGRES_PORT = "5432";
    private static final String DEFAULT_POSTGRES_DATABASE_NAME = "postgres-db";
    private static final String DEFAULT_POSTGRES_USERNAME = "user";
    private static final String DEFAULT_POSTGRES_PASSWORD = "password";
    private static final String DEFAULT_POSTGRES_CONTAINER_LOGGING_ENABLED = "false";

    /**
     * Kafka default configuration.
     */

    private static final String DEFAULT_KAFKA_ENABLED = "false";
    private static final String DEFAULT_KAFKA_BROKER_COUNT = "1";
    private static final String DEFAULT_KAFKA_CONFLUENT_IMAGE_TAG = "7.3.2";
    private static final String DEFAULT_KAFKA_PORT = "9093";
    private static final String DEFAULT_KAFKA_TOPIC_PARTITION_COUNT = "1";
    private static final String DEFAULT_KAFKA_CONTAINER_LOGGING_ENABLED = "false";
    private static final String DEFAULT_KAFKA_TOPIC_REPLICATION_FACTOR = "1";
    private static final String DEFAULT_KAFKA_MIN_INSYNC_REPLICAS = "1";

    /**
     * Debezium (Kafka Connect) default configuration.
     */

    private static final String DEFAULT_DEBEZIUM_ENABLED = "false";
    private static final String DEFAULT_DEBEZIUM_IMAGE_TAG = "2.2";
    private static final String DEFAULT_DEBEZIUM_PORT = "8083";
    private static final String DEFAULT_DEBEZIUM_CONTAINER_LOGGING_ENABLED = "false";

    /**
     * Kafka Confluent Schema Registry default configuration.
     */

    private static final String DEFAULT_KAFKA_SCHEMA_REGISTRY_ENABLED = "false";
    private static final String DEFAULT_KAFKA_SCHEMA_REGISTRY_CONFLUENT_IMAGE_TAG = "7.3.2";
    private static final String DEFAULT_KAFKA_SCHEMA_REGISTRY_PORT = "8081";
    private static final String DEFAULT_KAFKA_SCHEMA_REGISTRY_CONTAINER_LOGGING_ENABLED = "false";

    /**
     * Kafka Confluent Control Center default configuration.
     */

    private static final String DEFAULT_KAFKA_CONTROL_CENTER_ENABLED = "false";
    private static final String DEFAULT_KAFKA_CONTROL_CENTER_CONFLUENT_IMAGE_TAG = "7.3.2";
    private static final String DEFAULT_KAFKA_CONTROL_CENTER_PORT = "9021";
    private static final String DEFAULT_KAFKA_CONTROL_CENTER_JMX_PORT = "9101";
    private static final String DEFAULT_KAFKA_CONTROL_CENTER_EXPORT_METRICS_ENABLED = "false";
    private static final String DEFAULT_KAFKA_CONTROL_CENTER_CONTAINER_LOGGING_ENABLED = "false";

    /**
     * Conduktor configuration.
     */

    private static final String DEFAULT_CONDUKTOR_IMAGE_TAG = "1.4.0";
    private static final String DEFAULT_CONDUKTOR_ENABLED = "false";
    private static final String DEFAULT_CONDUKTOR_CONTAINER_LOGGING_ENABLED = "false";
    private static final String DEFAULT_CONDUKTOR_PORT = "8088";

    /**
     * Wiremock default configuration.
     */

    private static final String DEFAULT_WIREMOCK_ENABLED = "false";
    private static final String DEFAULT_WIREMOCK_IMAGE_TAG = "2.35.0";
    private static final String DEFAULT_WIREMOCK_PORT = "8080";
    private static final String DEFAULT_WIREMOCK_CONTAINER_LOGGING_ENABLED = "false";

    /**
     * Localstack (AWS) default configuration.
     */

    private static final String DEFAULT_LOCALSTACK_ENABLED = "false";
    private static final String DEFAULT_LOCALSTACK_IMAGE_TAG = "0.14.3";
    private static final String DEFAULT_LOCALSTACK_PORT = "4566";
    private static final String DEFAULT_LOCALSTACK_SERVICES = "dynamodb";
    private static final String DEFAULT_LOCALSTACK_CONTAINER_LOGGING_ENABLED = "false";

    /**
     * The runtime configuration.
     */

    public static String CONTAINER_NAME_PREFIX;
    public static String CONTAINER_MAIN_LABEL;

    public static String SERVICE_NAME;
    public static int SERVICE_INSTANCE_COUNT;
    public static int SERVICE_PORT;
    public static int SERVICE_DEBUG_PORT;
    public static int SERVICE_STARTUP_TIMEOUT_SECONDS;
    public static String SERVICE_IMAGE_TAG;
    public static boolean SERVICE_CONTAINER_LOGGING_ENABLED;

    public static List<AdditionalContainer> ADDITIONAL_CONTAINERS;

    public static boolean POSTGRES_ENABLED;
    public static String POSTGRES_IMAGE_TAG;
    public static String POSTGRES_HOST_NAME;
    public static int POSTGRES_PORT;
    public static String POSTGRES_DATABASE_NAME;
    public static String POSTGRES_USERNAME;
    public static String POSTGRES_PASSWORD;
    public static boolean POSTGRES_CONTAINER_LOGGING_ENABLED;

    public static boolean KAFKA_ENABLED;
    public static int KAFKA_BROKER_COUNT;
    public static String KAFKA_CONFLUENT_IMAGE_TAG;
    public static int KAFKA_PORT;
    public static List<String> KAFKA_TOPICS;
    public static int KAFKA_TOPIC_PARTITION_COUNT;
    public static boolean KAFKA_CONTAINER_LOGGING_ENABLED;
    public static int KAFKA_TOPIC_REPLICATION_FACTOR;
    public static int KAFKA_MIN_INSYNC_REPLICAS;

    public static boolean KAFKA_SCHEMA_REGISTRY_ENABLED;
    public static String KAFKA_SCHEMA_REGISTRY_CONFLUENT_IMAGE_TAG;
    public static int KAFKA_SCHEMA_REGISTRY_PORT;
    public static boolean KAFKA_SCHEMA_REGISTRY_CONTAINER_LOGGING_ENABLED;

    public static boolean KAFKA_CONTROL_CENTER_ENABLED;
    public static String KAFKA_CONTROL_CENTER_CONFLUENT_IMAGE_TAG;
    public static boolean KAFKA_CONTROL_CENTER_EXPORT_METRICS_ENABLED;
    public static String KAFKA_CONTROL_CENTER_JMX_PORT;
    public static int KAFKA_CONTROL_CENTER_PORT;
    public static boolean KAFKA_CONTROL_CENTER_CONTAINER_LOGGING_ENABLED;

    public static boolean CONDUKTOR_ENABLED;
    public static String CONDUKTOR_IMAGE_TAG;
    public static String CONDUKTOR_LICENSE_KEY;
    public static int CONDUKTOR_PORT;
    public static boolean CONDUKTOR_CONTAINER_LOGGING_ENABLED;

    public static boolean DEBEZIUM_ENABLED;
    public static String DEBEZIUM_IMAGE_TAG;
    public static int DEBEZIUM_PORT;
    public static boolean DEBEZIUM_CONTAINER_LOGGING_ENABLED;

    public static boolean WIREMOCK_ENABLED;
    public static String WIREMOCK_IMAGE_TAG;
    public static int WIREMOCK_PORT;
    public static boolean WIREMOCK_CONTAINER_LOGGING_ENABLED;

    public static boolean LOCALSTACK_ENABLED;
    public static String LOCALSTACK_IMAGE_TAG;
    public static int LOCALSTACK_PORT;
    public static String LOCALSTACK_SERVICES;
    public static boolean LOCALSTACK_CONTAINER_LOGGING_ENABLED;


    static {
        configure();
    }

    protected static void configure() {
        CONTAINER_NAME_PREFIX = System.getProperty("container.name.prefix", DEFAULT_CONTAINER_NAME_PREFIX);
        CONTAINER_MAIN_LABEL = System.getProperty("container.main.label", DEFAULT_CONTAINER_MAIN_LABEL);
        SERVICE_NAME = System.getProperty("service.name", DEFAULT_SERVICE_NAME);
        SERVICE_INSTANCE_COUNT = Integer.parseInt(System.getProperty("service.instance.count", DEFAULT_SERVICE_INSTANCE_COUNT));
        SERVICE_PORT = Integer.parseInt(System.getProperty("service.port", DEFAULT_SERVICE_PORT));
        SERVICE_DEBUG_PORT = Integer.parseInt(System.getProperty("service.debug.port", DEFAULT_SERVICE_DEBUG_PORT));
        SERVICE_STARTUP_TIMEOUT_SECONDS = Integer.parseInt(System.getProperty("service.startup.timeout.seconds", DEFAULT_SERVICE_STARTUP_TIMEOUT_SECONDS));
        SERVICE_IMAGE_TAG = System.getProperty("service.image.tag", DEFAULT_SERVICE_IMAGE_TAG);
        SERVICE_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("service.container.logging.enabled", DEFAULT_SERVICE_CONTAINER_LOGGING_ENABLED));

        ADDITIONAL_CONTAINERS = parseAdditionalContainers();

        POSTGRES_ENABLED = Boolean.valueOf(System.getProperty("postgres.enabled", DEFAULT_POSTGRES_ENABLED));
        POSTGRES_IMAGE_TAG = System.getProperty("postgres.image.tag", DEFAULT_POSTGRES_IMAGE_TAG);
        POSTGRES_HOST_NAME = System.getProperty("postgres.host.name", DEFAULT_POSTGRES_HOST_NAME);
        POSTGRES_PORT = Integer.parseInt(System.getProperty("postgres.port", DEFAULT_POSTGRES_PORT));
        POSTGRES_DATABASE_NAME = System.getProperty("postgres.database.name", DEFAULT_POSTGRES_DATABASE_NAME);
        POSTGRES_USERNAME = System.getProperty("postgres.username", DEFAULT_POSTGRES_USERNAME);
        POSTGRES_PASSWORD = System.getProperty("postgres.password", DEFAULT_POSTGRES_PASSWORD);
        POSTGRES_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("postgres.container.logging.enabled", DEFAULT_POSTGRES_CONTAINER_LOGGING_ENABLED));

        KAFKA_ENABLED = Boolean.valueOf(System.getProperty("kafka.enabled", DEFAULT_KAFKA_ENABLED));
        KAFKA_BROKER_COUNT = Integer.parseInt(System.getProperty("kafka.broker.count", DEFAULT_KAFKA_BROKER_COUNT));
        KAFKA_CONFLUENT_IMAGE_TAG = System.getProperty("kafka.confluent.image.tag", DEFAULT_KAFKA_CONFLUENT_IMAGE_TAG);
        KAFKA_PORT = Integer.parseInt(System.getProperty("kafka.port", DEFAULT_KAFKA_PORT));
        KAFKA_TOPICS = parseKafkaTopics();
        KAFKA_TOPIC_PARTITION_COUNT = Integer.parseInt(System.getProperty("kafka.topic.partition.count", DEFAULT_KAFKA_TOPIC_PARTITION_COUNT));
        KAFKA_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("kafka.container.logging.enabled", DEFAULT_KAFKA_CONTAINER_LOGGING_ENABLED));
        KAFKA_TOPIC_REPLICATION_FACTOR = Integer.parseInt(System.getProperty("kafka.topic.replication.factor", DEFAULT_KAFKA_TOPIC_REPLICATION_FACTOR));
        KAFKA_MIN_INSYNC_REPLICAS = Integer.parseInt(System.getProperty("kafka.min.insync.replicas", DEFAULT_KAFKA_MIN_INSYNC_REPLICAS));

        KAFKA_SCHEMA_REGISTRY_ENABLED = Boolean.valueOf(System.getProperty("kafka.schema.registry.enabled", DEFAULT_KAFKA_SCHEMA_REGISTRY_ENABLED));
        KAFKA_SCHEMA_REGISTRY_CONFLUENT_IMAGE_TAG = System.getProperty("kafka.schema.registry.confluent.image.tag", DEFAULT_KAFKA_SCHEMA_REGISTRY_CONFLUENT_IMAGE_TAG);
        KAFKA_SCHEMA_REGISTRY_PORT = Integer.parseInt(System.getProperty("kafka.schema.registry.port", DEFAULT_KAFKA_SCHEMA_REGISTRY_PORT));
        KAFKA_SCHEMA_REGISTRY_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("kafka.schema.registry.container.logging.enabled", DEFAULT_KAFKA_SCHEMA_REGISTRY_CONTAINER_LOGGING_ENABLED));

        KAFKA_CONTROL_CENTER_ENABLED = Boolean.valueOf(System.getProperty("kafka.control.center.enabled", DEFAULT_KAFKA_CONTROL_CENTER_ENABLED));
        KAFKA_CONTROL_CENTER_CONFLUENT_IMAGE_TAG = System.getProperty("kafka.control.center.confluent.image.tag", DEFAULT_KAFKA_CONTROL_CENTER_CONFLUENT_IMAGE_TAG);
        KAFKA_CONTROL_CENTER_EXPORT_METRICS_ENABLED = Boolean.valueOf(System.getProperty("kafka.control.center.export.metrics.enabled", DEFAULT_KAFKA_CONTROL_CENTER_EXPORT_METRICS_ENABLED));
        KAFKA_CONTROL_CENTER_JMX_PORT = System.getProperty("kafka.control.center.jmx.port", DEFAULT_KAFKA_CONTROL_CENTER_JMX_PORT);
        KAFKA_CONTROL_CENTER_PORT = Integer.parseInt(System.getProperty("kafka.control.center.port", DEFAULT_KAFKA_CONTROL_CENTER_PORT));
        KAFKA_CONTROL_CENTER_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("kafka.control.center.container.logging.enabled", DEFAULT_KAFKA_CONTROL_CENTER_CONTAINER_LOGGING_ENABLED));

        CONDUKTOR_ENABLED = Boolean.valueOf(System.getProperty("conduktor.enabled", DEFAULT_CONDUKTOR_ENABLED));
        CONDUKTOR_IMAGE_TAG = System.getProperty("conduktor.image.tag", DEFAULT_CONDUKTOR_IMAGE_TAG);
        CONDUKTOR_LICENSE_KEY = System.getProperty("conduktor.license.key");
        CONDUKTOR_PORT = Integer.parseInt(System.getProperty("conduktor.port", DEFAULT_CONDUKTOR_PORT));
        CONDUKTOR_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("conduktor.container.logging.enabled", DEFAULT_CONDUKTOR_CONTAINER_LOGGING_ENABLED));

        DEBEZIUM_ENABLED = Boolean.valueOf(System.getProperty("debezium.enabled", DEFAULT_DEBEZIUM_ENABLED));
        DEBEZIUM_IMAGE_TAG = System.getProperty("debezium.image.tag", DEFAULT_DEBEZIUM_IMAGE_TAG);
        DEBEZIUM_PORT = Integer.parseInt(System.getProperty("debezium.port", DEFAULT_DEBEZIUM_PORT));
        DEBEZIUM_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("debezium.container.logging.enabled", DEFAULT_DEBEZIUM_CONTAINER_LOGGING_ENABLED));

        WIREMOCK_ENABLED = Boolean.valueOf(System.getProperty("wiremock.enabled", DEFAULT_WIREMOCK_ENABLED));
        WIREMOCK_IMAGE_TAG = System.getProperty("wiremock.image.tag", DEFAULT_WIREMOCK_IMAGE_TAG);
        WIREMOCK_PORT = Integer.parseInt(System.getProperty("wiremock.port", DEFAULT_WIREMOCK_PORT));
        WIREMOCK_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("wiremock.container.logging.enabled", DEFAULT_WIREMOCK_CONTAINER_LOGGING_ENABLED));

        LOCALSTACK_ENABLED = Boolean.valueOf(System.getProperty("localstack.enabled", DEFAULT_LOCALSTACK_ENABLED));
        LOCALSTACK_IMAGE_TAG = System.getProperty("localstack.image.tag", DEFAULT_LOCALSTACK_IMAGE_TAG);
        LOCALSTACK_PORT = Integer.parseInt(System.getProperty("localstack.port", DEFAULT_LOCALSTACK_PORT));
        LOCALSTACK_SERVICES = System.getProperty("localstack.services", DEFAULT_LOCALSTACK_SERVICES);
        LOCALSTACK_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("localstack.container.logging.enabled", DEFAULT_LOCALSTACK_CONTAINER_LOGGING_ENABLED));
    }

    protected static List<AdditionalContainer> parseAdditionalContainers() {
        String additionalContainersPropertyValue = System.getProperty("additional.containers", null);
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

    protected static List<String> parseKafkaTopics() {
        String topicNamesPropertyValue = System.getProperty("kafka.topics", null);
        List<String> topics = Collections.EMPTY_LIST;
        if(topicNamesPropertyValue!=null) {
            topicNamesPropertyValue = topicNamesPropertyValue.replaceAll("\\s+","");
            if(topicNamesPropertyValue.length()>0) {
                topics = Arrays.asList(topicNamesPropertyValue.split(","));
            }
        }
        return topics;
    }

    static {
        log.info("TestContainers Configuration:");

        log.info("containers.stayup: " + System.getProperty("containers.stayup", Boolean.FALSE.toString()));
        log.info("container.name.prefix: " + CONTAINER_NAME_PREFIX);
        log.info("container.main.label: " + CONTAINER_MAIN_LABEL);

        log.info("service.name: " + SERVICE_NAME);
        log.info("service.instance.count: " + SERVICE_INSTANCE_COUNT);
        log.info("service.port: " + SERVICE_PORT);
        log.info("service.debug.port: " + SERVICE_DEBUG_PORT);
        log.info("service.startup.timeout.seconds: " + SERVICE_STARTUP_TIMEOUT_SECONDS);
        log.info("service.image.tag: " + SERVICE_IMAGE_TAG);
        log.info("service.container.logging.enabled: " + SERVICE_CONTAINER_LOGGING_ENABLED);

        log.info("additional.containers: "+System.getProperty("additional.containers", ""));

        log.info("postgres.enabled: " + POSTGRES_ENABLED);
        if(POSTGRES_ENABLED) {
            log.info("postgres.image.tag: " + POSTGRES_IMAGE_TAG);
            log.info("postgres.host.name: " + POSTGRES_HOST_NAME);
            log.info("postgres.port: " + POSTGRES_PORT);
            log.info("postgres.database.name: " + POSTGRES_DATABASE_NAME);
            log.info("postgres.username: " + POSTGRES_USERNAME);
            log.info("postgres.password: " + POSTGRES_PASSWORD);
            log.info("postgres.container.logging.enabled: " + KAFKA_CONTAINER_LOGGING_ENABLED);
        }

        log.info("kafka.enabled: " + KAFKA_ENABLED);
        if(KAFKA_ENABLED) {
            log.info("kafka.confluent.image.tag: " + KAFKA_CONFLUENT_IMAGE_TAG);
            log.info("kafka.broker.count: " + KAFKA_BROKER_COUNT);
            log.info("kafka.port: " + KAFKA_PORT);
            log.info("kafka.topics: " + KAFKA_TOPICS);
            log.info("kafka.topic.partition.count: " + KAFKA_TOPIC_PARTITION_COUNT);
            log.info("kafka.topic.replication.factor: " + KAFKA_TOPIC_REPLICATION_FACTOR);
            log.info("kafka.min.insync.replicas: " + KAFKA_MIN_INSYNC_REPLICAS);
            log.info("kafka.container.logging.enabled: " + KAFKA_CONTAINER_LOGGING_ENABLED);
        }

        log.info("kafka.schema.registry.enabled: " + KAFKA_SCHEMA_REGISTRY_ENABLED);
        if(KAFKA_SCHEMA_REGISTRY_ENABLED) {
            log.info("kafka.schema.registry.confluent.image.tag: " + KAFKA_SCHEMA_REGISTRY_CONFLUENT_IMAGE_TAG);
            log.info("kafka.schema.registry.port: " + KAFKA_SCHEMA_REGISTRY_PORT);
            log.info("kafka.schema.registry.container.logging.enabled: " + KAFKA_SCHEMA_REGISTRY_CONTAINER_LOGGING_ENABLED);
        }

        log.info("kafka.control.center.enabled: " + KAFKA_CONTROL_CENTER_ENABLED);
        if(KAFKA_CONTROL_CENTER_ENABLED) {
            log.info("kafka.control.center.confluent.image.tag: " + KAFKA_CONTROL_CENTER_CONFLUENT_IMAGE_TAG);
            log.info("kafka.control.center.port: " + KAFKA_CONTROL_CENTER_PORT);
            log.info("kafka.control.center.export.metrics.enabled: " + KAFKA_CONTROL_CENTER_EXPORT_METRICS_ENABLED);
            log.info("kafka.control.center.jmx.port: " + KAFKA_CONTROL_CENTER_JMX_PORT);
            if(KAFKA_CONTROL_CENTER_EXPORT_METRICS_ENABLED) {
                log.info("Kafka Control Center metrics require Confluent's community package kafka-clients and monitoring-interceptors libraries.");
            }
            log.info("kafka.control.center.container.logging.enabled: " + KAFKA_CONTROL_CENTER_CONTAINER_LOGGING_ENABLED);
        }

        log.info("conduktor.enabled: " + CONDUKTOR_ENABLED);
        if(CONDUKTOR_ENABLED) {
            log.info("conduktor.image.tag: " + CONDUKTOR_IMAGE_TAG);
            log.info("conduktor.license.key: " + CONDUKTOR_LICENSE_KEY);
            log.info("conduktor.port: " + CONDUKTOR_PORT);
            log.info("conduktor.container.logging.enabled: " + CONDUKTOR_CONTAINER_LOGGING_ENABLED);
        }

        log.info("debezium.enabled: " + DEBEZIUM_ENABLED);
        if(DEBEZIUM_ENABLED) {
            log.info("debezium.image.tag: " + DEBEZIUM_IMAGE_TAG);
            log.info("debezium.port: " + DEBEZIUM_PORT);
            log.info("debezium.container.logging.enabled: " + DEBEZIUM_CONTAINER_LOGGING_ENABLED);
        }

        log.info("wiremock.enabled: " + WIREMOCK_ENABLED);
        if(WIREMOCK_ENABLED) {
            log.info("wiremock.image.tag: " + WIREMOCK_IMAGE_TAG);
            log.info("wiremock.port: " + WIREMOCK_PORT);
            log.info("wiremock.container.logging.enabled: " + WIREMOCK_CONTAINER_LOGGING_ENABLED);
        }

        log.info("localstack.enabled: " + LOCALSTACK_ENABLED);
        if(LOCALSTACK_ENABLED) {
            log.info("localstack.image.tag: " + LOCALSTACK_IMAGE_TAG);
            log.info("localstack.port: " + LOCALSTACK_PORT);
            log.info("localstack.services: " + LOCALSTACK_SERVICES);
            log.info("localstack.container.logging.enabled: " + LOCALSTACK_CONTAINER_LOGGING_ENABLED);
        }
    }
}
