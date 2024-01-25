package dev.lydtech.component.framework.configuration;

import java.util.*;
import java.util.stream.Collectors;

import dev.lydtech.component.framework.management.AdditionalContainer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class TestcontainersConfiguration {

    // Label key for the Docker container housing the service.
    public static final String CONTAINER_MAIN_LABEL_KEY = "dev.lydtech.main-container-label";

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
    private static final String DEFAULT_SERVICE_DEBUG_SUSPEND = "false";

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
    private static final String DEFAULT_POSTGRES_SCHEMA_NAME = "test";
    private static final String DEFAULT_POSTGRES_USERNAME = "user";
    private static final String DEFAULT_POSTGRES_PASSWORD = "password";
    private static final String DEFAULT_POSTGRES_CONTAINER_LOGGING_ENABLED = "false";

    /**
     * MongoDB configuration.
     */

    private static final String DEFAULT_MONGODB_ENABLED = "false";
    private static final String DEFAULT_MONGODB_IMAGE_TAG = "7.0.2";
    private static final String DEFAULT_MONGODB_PORT = "27017";
    private static final String DEFAULT_MONGODB_CONTAINER_LOGGING_ENABLED = "false";

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
    private static final String DEFAULT_KAFKA_SASL_PLAIN_ENABLED = "false";
    private static final String DEFAULT_KAFKA_SASL_PLAIN_USERNAME = "demo";
    private static final String DEFAULT_KAFKA_SASL_PLAIN_PASSWORD = "demo-password";

    /**
     * Debezium (Kafka Connect) default configuration.
     */

    private static final String DEFAULT_DEBEZIUM_ENABLED = "false";
    private static final String DEFAULT_DEBEZIUM_IMAGE_TAG = "2.4.0.Final";
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

    private static final String DEFAULT_CONDUKTOR_IMAGE_TAG = "1.15.0";
    private static final String DEFAULT_CONDUKTOR_ENABLED = "false";
    private static final String DEFAULT_CONDUKTOR_CONTAINER_LOGGING_ENABLED = "false";
    private static final String DEFAULT_CONDUKTOR_PORT = "8088";

    /**
     * Conduktor Gateway configuration.
     */

    private static final String DEFAULT_CONDUKTOR_GATEWAY_IMAGE_TAG = "2.1.5";

    private static final String DEFAULT_CONDUKTOR_GATEWAY_ENABLED = "false";
    private static final String DEFAULT_CONDUKTOR_GATEWAY_CONTAINER_LOGGING_ENABLED = "false";
    private static final String DEFAULT_CONDUKTOR_GATEWAY_PROXY_PORT = "6969";
    private static final String DEFAULT_CONDUKTOR_GATEWAY_HTTP_PORT = "8888";

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
     * Elasticsearch configuration.
     */

    private static final String DEFAULT_ELASTICSEARCH_ENABLED = "false";
    private static final String DEFAULT_ELASTICSEARCH_IMAGE_TAG = "8.10.4";
    private static final String DEFAULT_ELASTICSEARCH_PORT = "9200";
    private static final String DEFAULT_ELASTICSEARCH_PASSWORD = null;
    private static final String DEFAULT_ELASTICSEARCH_CLUSTER_NAME = "elasticsearch";
    private static final String DEFAULT_ELASTICSEARCH_DISCOVERY_TYPE = "single-node";
    private static final String DEFAULT_ELASTICSEARCH_CONTAINER_LOGGING_ENABLED = "false";

    /**
     * The runtime configuration.
     */

    public static String CONTAINER_NAME_PREFIX;
    public static String CONTAINER_MAIN_LABEL;

    public static String SERVICE_NAME;
    public static int SERVICE_INSTANCE_COUNT;
    public static int SERVICE_PORT;
    public static int SERVICE_DEBUG_PORT;
    public static boolean SERVICE_DEBUG_SUSPEND;
    public static Map<String, String> SERVICE_ENV_VARS;
    public static int SERVICE_STARTUP_TIMEOUT_SECONDS;
    public static String SERVICE_IMAGE_TAG;
    public static boolean SERVICE_CONTAINER_LOGGING_ENABLED;

    public static List<AdditionalContainer> ADDITIONAL_CONTAINERS;

    public static boolean POSTGRES_ENABLED;
    public static String POSTGRES_IMAGE_TAG;
    public static String POSTGRES_HOST_NAME;
    public static int POSTGRES_PORT;
    public static String POSTGRES_DATABASE_NAME;
    public static String POSTGRES_SCHEMA_NAME;
    public static String POSTGRES_USERNAME;
    public static String POSTGRES_PASSWORD;
    public static boolean POSTGRES_CONTAINER_LOGGING_ENABLED;

    public static boolean MONGODB_ENABLED;
    public static String MONGODB_IMAGE_TAG;
    public static Integer MONGODB_PORT;
    public static boolean MONGODB_CONTAINER_LOGGING_ENABLED;

    public static boolean KAFKA_ENABLED;
    public static int KAFKA_BROKER_COUNT;
    public static String KAFKA_CONFLUENT_IMAGE_TAG;
    public static int KAFKA_PORT;
    public static List<String> KAFKA_TOPICS;
    public static int KAFKA_TOPIC_PARTITION_COUNT;
    public static boolean KAFKA_CONTAINER_LOGGING_ENABLED;
    public static int KAFKA_TOPIC_REPLICATION_FACTOR;
    public static int KAFKA_MIN_INSYNC_REPLICAS;
    public static boolean KAFKA_SASL_PLAIN_ENABLED;
    public static String KAFKA_SASL_PLAIN_USERNAME;
    public static String KAFKA_SASL_PLAIN_PASSWORD;

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

    public static boolean CONDUKTOR_GATEWAY_ENABLED;
    public static String CONDUKTOR_GATEWAY_IMAGE_TAG;
    public static int CONDUKTOR_GATEWAY_PROXY_PORT;
    public static int CONDUKTOR_GATEWAY_HTTP_PORT;
    public static boolean CONDUKTOR_GATEWAY_CONTAINER_LOGGING_ENABLED;

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

    public static boolean ELASTICSEARCH_ENABLED;
    public static String ELASTICSEARCH_IMAGE_TAG;
    public static int ELASTICSEARCH_PORT;
    public static String ELASTICSEARCH_PASSWORD;
    public static String ELASTICSEARCH_CLUSTER_NAME;
    public static String ELASTICSEARCH_DISCOVERY_TYPE;
    public static boolean ELASTICSEARCH_CONTAINER_LOGGING_ENABLED;

    static {
        configure();
    }

    protected static void configure() {
        CONTAINER_NAME_PREFIX = System.getProperty("container.name.prefix", DEFAULT_CONTAINER_NAME_PREFIX);
        CONTAINER_MAIN_LABEL = System.getProperty("container.main.label", DEFAULT_CONTAINER_MAIN_LABEL);
        SERVICE_NAME = System.getProperty("service.name", DEFAULT_SERVICE_NAME);
        SERVICE_INSTANCE_COUNT = Integer.parseInt(System.getProperty("service.instance.count", DEFAULT_SERVICE_INSTANCE_COUNT));
        SERVICE_PORT = Integer.parseInt(System.getProperty("service.port", DEFAULT_SERVICE_PORT));
        SERVICE_DEBUG_SUSPEND = Boolean.parseBoolean(System.getProperty("service.debug.suspend", DEFAULT_SERVICE_DEBUG_SUSPEND));
        SERVICE_ENV_VARS = parseEnvVars(System.getProperty("service.envvars", null));
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
        POSTGRES_SCHEMA_NAME = System.getProperty("postgres.schema.name", DEFAULT_POSTGRES_SCHEMA_NAME);
        POSTGRES_USERNAME = System.getProperty("postgres.username", DEFAULT_POSTGRES_USERNAME);
        POSTGRES_PASSWORD = System.getProperty("postgres.password", DEFAULT_POSTGRES_PASSWORD);
        POSTGRES_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("postgres.container.logging.enabled", DEFAULT_POSTGRES_CONTAINER_LOGGING_ENABLED));

        MONGODB_ENABLED = Boolean.valueOf(System.getProperty("mongodb.enabled", DEFAULT_MONGODB_ENABLED));
        MONGODB_IMAGE_TAG = System.getProperty("mongodb.image.tag", DEFAULT_MONGODB_IMAGE_TAG);
        // Port cannot be overridden in the MongoDB Testcontainer.
        MONGODB_PORT = Integer.parseInt(DEFAULT_MONGODB_PORT);
        MONGODB_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("mongodb.container.logging.enabled", DEFAULT_MONGODB_CONTAINER_LOGGING_ENABLED));

        KAFKA_ENABLED = Boolean.valueOf(System.getProperty("kafka.enabled", DEFAULT_KAFKA_ENABLED));
        KAFKA_BROKER_COUNT = Integer.parseInt(System.getProperty("kafka.broker.count", DEFAULT_KAFKA_BROKER_COUNT));
        KAFKA_CONFLUENT_IMAGE_TAG = System.getProperty("kafka.confluent.image.tag", DEFAULT_KAFKA_CONFLUENT_IMAGE_TAG);
        KAFKA_PORT = Integer.parseInt(System.getProperty("kafka.port", DEFAULT_KAFKA_PORT));
        KAFKA_TOPICS = parseKafkaTopics();
        KAFKA_TOPIC_PARTITION_COUNT = Integer.parseInt(System.getProperty("kafka.topic.partition.count", DEFAULT_KAFKA_TOPIC_PARTITION_COUNT));
        KAFKA_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("kafka.container.logging.enabled", DEFAULT_KAFKA_CONTAINER_LOGGING_ENABLED));
        KAFKA_TOPIC_REPLICATION_FACTOR = Integer.parseInt(System.getProperty("kafka.topic.replication.factor", DEFAULT_KAFKA_TOPIC_REPLICATION_FACTOR));
        KAFKA_MIN_INSYNC_REPLICAS = Integer.parseInt(System.getProperty("kafka.min.insync.replicas", DEFAULT_KAFKA_MIN_INSYNC_REPLICAS));
        KAFKA_SASL_PLAIN_ENABLED = Boolean.valueOf(System.getProperty("kafka.sasl.plain.enabled", DEFAULT_KAFKA_SASL_PLAIN_ENABLED));
        KAFKA_SASL_PLAIN_USERNAME = System.getProperty("kafka.sasl.plain.username", DEFAULT_KAFKA_SASL_PLAIN_USERNAME);
        KAFKA_SASL_PLAIN_PASSWORD = System.getProperty("kafka.sasl.plain.password", DEFAULT_KAFKA_SASL_PLAIN_PASSWORD);

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

        CONDUKTOR_GATEWAY_ENABLED = Boolean.valueOf(System.getProperty("conduktor.gateway.enabled", DEFAULT_CONDUKTOR_GATEWAY_ENABLED));
        CONDUKTOR_GATEWAY_IMAGE_TAG = System.getProperty("conduktor.gateway.image.tag", DEFAULT_CONDUKTOR_GATEWAY_IMAGE_TAG);
        CONDUKTOR_GATEWAY_PROXY_PORT = Integer.parseInt(System.getProperty("conduktor.gateway.proxy.port", DEFAULT_CONDUKTOR_GATEWAY_PROXY_PORT));
        CONDUKTOR_GATEWAY_HTTP_PORT = Integer.parseInt(System.getProperty("conduktor.gateway.http.port", DEFAULT_CONDUKTOR_GATEWAY_HTTP_PORT));
        CONDUKTOR_GATEWAY_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("conduktor.gateway.container.logging.enabled", DEFAULT_CONDUKTOR_GATEWAY_CONTAINER_LOGGING_ENABLED));

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

        ELASTICSEARCH_ENABLED = Boolean.valueOf(System.getProperty("elasticsearch.enabled", DEFAULT_ELASTICSEARCH_ENABLED));
        ELASTICSEARCH_IMAGE_TAG = System.getProperty("elasticsearch.image.tag", DEFAULT_ELASTICSEARCH_IMAGE_TAG);
        // Port cannot be overridden in the Elasticsearch Testcontainer.
        ELASTICSEARCH_PORT = Integer.parseInt(DEFAULT_ELASTICSEARCH_PORT);
        ELASTICSEARCH_PASSWORD = System.getProperty("elasticsearch.password", DEFAULT_ELASTICSEARCH_PASSWORD);
        ELASTICSEARCH_CLUSTER_NAME = System.getProperty("elasticsearch.cluster.name", DEFAULT_ELASTICSEARCH_CLUSTER_NAME);
        ELASTICSEARCH_DISCOVERY_TYPE = System.getProperty("elasticsearch.discovery.type", DEFAULT_ELASTICSEARCH_DISCOVERY_TYPE);
        ELASTICSEARCH_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("elasticsearch.container.logging.enabled", DEFAULT_ELASTICSEARCH_CONTAINER_LOGGING_ENABLED));
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

    protected static Map<String, String> parseEnvVars(String input) {
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
                throw new IllegalArgumentException("invalid key/value pair string for service env vars");
            }
        }

        return resultMap;
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
        log.info("Testcontainers Configuration:");

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
            log.info("postgres.schema.name: " + POSTGRES_SCHEMA_NAME);
            log.info("postgres.username: " + POSTGRES_USERNAME);
            log.info("postgres.password: " + POSTGRES_PASSWORD);
            log.info("postgres.container.logging.enabled: " + POSTGRES_CONTAINER_LOGGING_ENABLED);
        }

        log.info("mongodb.enabled: " + MONGODB_ENABLED);
        if(MONGODB_ENABLED) {
            log.info("mongodb.image.tag: " + MONGODB_IMAGE_TAG);
            log.info("mongodb.port: " + MONGODB_PORT);
            log.info("mongodb.container.logging.enabled: " + MONGODB_CONTAINER_LOGGING_ENABLED);
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
            log.info("kafka.sasl.plain.enabled: " + KAFKA_SASL_PLAIN_ENABLED);
            if(KAFKA_SASL_PLAIN_ENABLED) {
                log.info("kafka.sasl.plain.username: " + KAFKA_SASL_PLAIN_USERNAME);
                log.info("kafka.sasl.plain.password: " + KAFKA_SASL_PLAIN_PASSWORD);
            }
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

        log.info("conduktor.gateway.enabled: " + CONDUKTOR_GATEWAY_ENABLED);
        if(CONDUKTOR_GATEWAY_ENABLED) {
            log.info("conduktor.gateway.image.tag: " + CONDUKTOR_GATEWAY_IMAGE_TAG);
            log.info("conduktor.gateway.proxy.port: " + CONDUKTOR_GATEWAY_PROXY_PORT);
            log.info("conduktor.gateway.http.port: " + CONDUKTOR_GATEWAY_HTTP_PORT);
            log.info("conduktor.gateway.container.logging.enabled: " + CONDUKTOR_GATEWAY_CONTAINER_LOGGING_ENABLED);
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

        log.info("elasticsearch.enabled: " + ELASTICSEARCH_ENABLED);
        if(ELASTICSEARCH_ENABLED) {
            log.info("elasticsearch.image.tag: " + ELASTICSEARCH_IMAGE_TAG);
            log.info("elasticsearch.port: " + ELASTICSEARCH_PORT);
            log.info("elasticsearch.password: " + ELASTICSEARCH_PASSWORD);
            log.info("elasticsearch.cluster.name: " + ELASTICSEARCH_CLUSTER_NAME);
            log.info("elasticsearch.discovery.type: " + ELASTICSEARCH_DISCOVERY_TYPE);
            log.info("elasticsearch.container.logging.enabled: " + ELASTICSEARCH_CONTAINER_LOGGING_ENABLED);
        }
    }
}
