package dev.lydtech.component.framework.configuration;

import java.util.*;
import java.util.stream.Collectors;

import dev.lydtech.component.framework.management.AdditionalContainer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

@Slf4j
public final class TestcontainersConfiguration {

    /**
     * Container configuration.
     */

    /**
     * Label key for the Docker container housing the service.
     */
    public static final String CONTAINER_MAIN_LABEL_KEY = "dev.lydtech.main-container-label";

    /**
     * A unique Id to optionally apply to docker container names to distinguish the group of containers for each test run.
     */
    public static final String CONTAINER_GROUP_ID = RandomStringUtils.randomAlphanumeric(8).toLowerCase();


    private static final String DEFAULT_CONTAINERS_STAYUP = "false";
    private static final String DEFAULT_CONTAINER_APPEND_GROUP_ID = "false";

    /**
     * Service default configuration.
     */

    private static final String DEFAULT_SERVICE_NAME = "app";
    private static final String DEFAULT_SERVICE_INSTANCE_COUNT = "1";
    private static final String DEFAULT_SERVICE_PORT = "8080";
    private static final String DEFAULT_SERVICE_DEBUG_PORT = "5001";
    private static final String DEFAULT_SERVICE_STARTUP_TIMEOUT_SECONDS = "180";
    private static final String DEFAULT_SERVICE_CONFIG_FILES_SYSTEM_PROPERTY = "spring.config.additional-location";
    private static final String DEFAULT_SERVICE_APPLICATION_YML_PATH = "src/test/resources/application-component-test.yml";
    private static final String DEFAULT_SERVICE_STARTUP_HEALTH_ENDPOINT = "/actuator/health";
    private static final String DEFAULT_SERVICE_STARTUP_LOG_MESSAGE = null;
    private static final String DEFAULT_SERVICE_IMAGE_TAG = "latest";
    private static final String DEFAULT_SERVICE_CONTAINER_LOGGING_ENABLED = "false";
    private static final String DEFAULT_SERVICE_DEBUG_SUSPEND = "false";
    private static final String DEFAULT_SERVICE_APPLICATION_ARGS = null;

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
    private static final String DEFAULT_POSTGRES_SCHEMA_FILE_PATH = null;
    private static final String DEFAULT_POSTGRES_CONTAINER_LOGGING_ENABLED = "false";

    /**
     * MongoDB configuration.
     */

    private static final String DEFAULT_MONGODB_ENABLED = "false";
    private static final String DEFAULT_MONGODB_IMAGE_TAG = "7.0.2";
    private static final String DEFAULT_MONGODB_PORT = "27017";
    private static final String DEFAULT_MONGODB_CONTAINER_LOGGING_ENABLED = "false";

    /**
     * MariaDB configuration.
     */

    private static final String DEFAULT_MARIADB_ENABLED = "false";
    private static final String DEFAULT_MARIADB_IMAGE_TAG = "10.6";
    private static final String DEFAULT_MARIADB_HOST_NAME = "mariadb-host";
    private static final String DEFAULT_MARIADB_PORT = "3306";
    private static final String DEFAULT_MARIADB_DATABASE_NAME = "mariadb-db";
    private static final String DEFAULT_MARIADB_USERNAME = "user";
    private static final String DEFAULT_MARIADB_PASSWORD = "password";
    private static final String DEFAULT_MARIADB_CONTAINER_LOGGING_ENABLED = "false";


    /**
     * Kafka default configuration.
     */

    private static final String DEFAULT_KAFKA_ENABLED = "false";
    private static final String DEFAULT_KAFKA_NATIVE_ENABLED = "false";
    private static final String DEFAULT_KAFKA_BROKER_COUNT = "1";
    private static final String DEFAULT_KAFKA_CONFLUENT_IMAGE_TAG = "7.3.2";
    private static final String DEFAULT_KAFKA_APACHE_NATIVE_IMAGE_TAG = "3.8.0";
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

    private static final String DEFAULT_CONDUKTOR_IMAGE_TAG = "1.23.0";
    private static final String DEFAULT_CONDUKTOR_ENABLED = "false";
    private static final String DEFAULT_CONDUKTOR_CONTAINER_LOGGING_ENABLED = "false";
    private static final String DEFAULT_CONDUKTOR_PORT = "8088";
    public static final String DEFAULT_CONDUKTOR_POSTGRES_IMAGE_TAG = "postgres:14";
    public static final String DEFAULT_CONDUKTOR_POSTGRES_DB = "conduktor-console";
    public static final String DEFAULT_CONDUKTOR_POSTGRES_USER = "conduktor";
    public static final String DEFAULT_CONDUKTOR_POSTGRES_PASSWORD = "POSTGRES_PASSWORD";

    /**
     * Conduktor Gateway configuration.
     */

    private static final String DEFAULT_CONDUKTOR_GATEWAY_IMAGE_TAG = "3.2.1";

    private static final String DEFAULT_CONDUKTOR_GATEWAY_ENABLED = "false";
    private static final String DEFAULT_CONDUKTOR_GATEWAY_CONTAINER_LOGGING_ENABLED = "false";
    private static final String DEFAULT_CONDUKTOR_GATEWAY_PROXY_PORT = "6969";
    private static final String DEFAULT_CONDUKTOR_GATEWAY_HTTP_PORT = "8888";

    /**
     * Wiremock default configuration.
     */

    private static final String DEFAULT_WIREMOCK_ENABLED = "false";
    private static final String DEFAULT_WIREMOCK_IMAGE_TAG = "3.6.0";
    private static final String DEFAULT_WIREMOCK_PORT = "8080";
    private static final String DEFAULT_WIREMOCK_CONTAINER_LOGGING_ENABLED = "false";
    private static final String DEFAULT_WIREMOCK_OPTIONS = "";

    /**
     * Localstack (AWS) default configuration.
     */

    private static final String DEFAULT_LOCALSTACK_ENABLED = "false";
    private static final String DEFAULT_LOCALSTACK_IMAGE_TAG = "0.14.3";
    private static final String DEFAULT_LOCALSTACK_PORT = "4566";
    private static final String DEFAULT_LOCALSTACK_SERVICES = "dynamodb";
    private static final String DEFAULT_LOCALSTACK_CONTAINER_LOGGING_ENABLED = "false";
    private static final String DEFAULT_LOCALSTACK_INIT_FILE_PATH = null;

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
     * Ambar configuration.
     */

    private static final String DEFAULT_AMBAR_ENABLED = "false";
    private static final String DEFAULT_AMBAR_IMAGE_TAG = "v1.8";
    private static final String DEFAULT_AMBAR_CONTAINER_LOGGING_ENABLED = "false";
    private static final String DEFAULT_AMBAR_CONFIG_FILE_PATH = "src/test/resources/ambar-config.yaml";

    /**
     * The runtime configuration.
     */

    public static boolean CONTAINERS_STAYUP;
    public static String CONTAINER_NAME_PREFIX;
    public static String CONTAINER_MAIN_LABEL;
    public static boolean CONTAINER_APPEND_GROUP_ID;
    public static String SERVICE_NAME;
    public static int SERVICE_INSTANCE_COUNT;
    public static int SERVICE_PORT;
    public static int SERVICE_DEBUG_PORT;
    public static boolean SERVICE_DEBUG_SUSPEND;
    public static Map<String, String> SERVICE_ENV_VARS;
    public static Map<String, String> SERVICE_ADDITIONAL_FILESYSTEM_BINDS;
    public static String SERVICE_CONFIG_FILES_SYSTEM_PROPERTY;
    public static String SERVICE_APPLICATION_YML_PATH;
    public static int SERVICE_STARTUP_TIMEOUT_SECONDS;
    public static String SERVICE_STARTUP_HEALTH_ENDPOINT;
    public static String SERVICE_STARTUP_LOG_MESSAGE;
    public static String SERVICE_IMAGE_TAG;
    public static boolean SERVICE_CONTAINER_LOGGING_ENABLED;
    public static String SERVICE_APPLICATION_ARGS;

    public static List<AdditionalContainer> ADDITIONAL_CONTAINERS;

    public static boolean POSTGRES_ENABLED;
    public static String POSTGRES_IMAGE_TAG;
    public static String POSTGRES_HOST_NAME;
    public static int POSTGRES_PORT;
    public static String POSTGRES_DATABASE_NAME;
    public static String POSTGRES_SCHEMA_NAME;
    public static String POSTGRES_USERNAME;
    public static String POSTGRES_PASSWORD;
    public static String POSTGRES_SCHEMA_FILE_PATH;
    public static boolean POSTGRES_CONTAINER_LOGGING_ENABLED;

    public static boolean MONGODB_ENABLED;
    public static String MONGODB_IMAGE_TAG;
    public static Integer MONGODB_PORT;
    public static boolean MONGODB_CONTAINER_LOGGING_ENABLED;

    public static boolean MARIADB_ENABLED;
    public static String MARIADB_IMAGE_TAG;
    public static String MARIADB_HOST_NAME;
    public static int MARIADB_PORT;
    public static String MARIADB_DATABASE_NAME;
    public static String MARIADB_USERNAME;
    public static String MARIADB_PASSWORD;
    public static boolean MARIADB_CONTAINER_LOGGING_ENABLED;

    public static boolean KAFKA_ENABLED;
    public static boolean KAFKA_NATIVE_ENABLED;
    public static int KAFKA_BROKER_COUNT;
    public static String KAFKA_CONFLUENT_IMAGE_TAG;
    public static String KAFKA_APACHE_NATIVE_IMAGE_TAG;

    // These ports are hardcoded in org.testcontainers.containers.KafkaContainer (Standard) and org.testcontainers.kafka.KafkaContainer (Native) respectively.
    // PORT is the port for calls made from outside the Docker network (e.g. to 'localhost').
    // INTERNAL_PORT is the port for calls between services within the Docker network (e.g. to the 'kafka' container).
    public static int KAFKA_PORT = 9093;
    public static int KAFKA_INTERNAL_PORT = 9092;
    public static int KAFKA_NATIVE_PORT = 9092;
    public static int KAFKA_NATIVE_INTERNAL_PORT = 9093;

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
    public static String WIREMOCK_OPTIONS;

    public static boolean LOCALSTACK_ENABLED;
    public static String LOCALSTACK_IMAGE_TAG;
    public static int LOCALSTACK_PORT;
    public static String LOCALSTACK_SERVICES;
    public static boolean LOCALSTACK_CONTAINER_LOGGING_ENABLED;
    public static String LOCALSTACK_INIT_FILE_PATH;

    public static boolean ELASTICSEARCH_ENABLED;
    public static String ELASTICSEARCH_IMAGE_TAG;
    public static int ELASTICSEARCH_PORT;
    public static String ELASTICSEARCH_PASSWORD;
    public static String ELASTICSEARCH_CLUSTER_NAME;
    public static String ELASTICSEARCH_DISCOVERY_TYPE;
    public static boolean ELASTICSEARCH_CONTAINER_LOGGING_ENABLED;

    public static boolean AMBAR_ENABLED;
    public static String AMBAR_IMAGE_TAG;
    public static boolean AMBAR_CONTAINER_LOGGING_ENABLED;
    public static String AMBAR_CONFIG_FILE_PATH;

    static {
        configure();
    }

    protected static void configure() {

        CONTAINERS_STAYUP = Boolean.parseBoolean(System.getenv("TESTCONTAINERS_REUSE_ENABLE")!=null?System.getenv("TESTCONTAINERS_REUSE_ENABLE"):DEFAULT_CONTAINERS_STAYUP);
        CONTAINER_NAME_PREFIX = System.getProperty("container.name.prefix", DEFAULT_CONTAINER_NAME_PREFIX);
        CONTAINER_MAIN_LABEL = System.getProperty("container.main.label", DEFAULT_CONTAINER_MAIN_LABEL);
        CONTAINER_APPEND_GROUP_ID = Boolean.parseBoolean(System.getProperty("container.append.group.id", DEFAULT_CONTAINER_APPEND_GROUP_ID));

        SERVICE_NAME = System.getProperty("service.name", DEFAULT_SERVICE_NAME).toLowerCase();
        SERVICE_INSTANCE_COUNT = Integer.parseInt(System.getProperty("service.instance.count", DEFAULT_SERVICE_INSTANCE_COUNT));
        SERVICE_PORT = Integer.parseInt(System.getProperty("service.port", DEFAULT_SERVICE_PORT));
        SERVICE_DEBUG_SUSPEND = Boolean.parseBoolean(System.getProperty("service.debug.suspend", DEFAULT_SERVICE_DEBUG_SUSPEND));
        SERVICE_ENV_VARS = parseKvPairs(System.getProperty("service.envvars", null));
        SERVICE_ADDITIONAL_FILESYSTEM_BINDS = parseKvPairs(System.getProperty("service.additional.filesystem.binds", null));
        SERVICE_CONFIG_FILES_SYSTEM_PROPERTY = System.getProperty("service.config.files.system.property", DEFAULT_SERVICE_CONFIG_FILES_SYSTEM_PROPERTY);
        SERVICE_APPLICATION_YML_PATH = System.getProperty("service.application.yml.path", DEFAULT_SERVICE_APPLICATION_YML_PATH);
        SERVICE_DEBUG_PORT = Integer.parseInt(System.getProperty("service.debug.port", DEFAULT_SERVICE_DEBUG_PORT));
        SERVICE_STARTUP_TIMEOUT_SECONDS = Integer.parseInt(System.getProperty("service.startup.timeout.seconds", DEFAULT_SERVICE_STARTUP_TIMEOUT_SECONDS));
        SERVICE_STARTUP_HEALTH_ENDPOINT = System.getProperty("service.startup.health.endpoint", DEFAULT_SERVICE_STARTUP_HEALTH_ENDPOINT);
        SERVICE_STARTUP_LOG_MESSAGE = System.getProperty("service.startup.log.message", DEFAULT_SERVICE_STARTUP_LOG_MESSAGE);
        SERVICE_IMAGE_TAG = System.getProperty("service.image.tag", DEFAULT_SERVICE_IMAGE_TAG);
        SERVICE_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("service.container.logging.enabled", DEFAULT_SERVICE_CONTAINER_LOGGING_ENABLED));
        SERVICE_APPLICATION_ARGS = System.getProperty("service.application.args", DEFAULT_SERVICE_APPLICATION_ARGS);

        ADDITIONAL_CONTAINERS = parseAdditionalContainers();

        POSTGRES_ENABLED = Boolean.valueOf(System.getProperty("postgres.enabled", DEFAULT_POSTGRES_ENABLED));
        POSTGRES_IMAGE_TAG = System.getProperty("postgres.image.tag", DEFAULT_POSTGRES_IMAGE_TAG);
        POSTGRES_HOST_NAME = System.getProperty("postgres.host.name", DEFAULT_POSTGRES_HOST_NAME);
        POSTGRES_PORT = Integer.parseInt(System.getProperty("postgres.port", DEFAULT_POSTGRES_PORT));
        POSTGRES_DATABASE_NAME = System.getProperty("postgres.database.name", DEFAULT_POSTGRES_DATABASE_NAME);
        POSTGRES_SCHEMA_NAME = System.getProperty("postgres.schema.name", DEFAULT_POSTGRES_SCHEMA_NAME);
        POSTGRES_USERNAME = System.getProperty("postgres.username", DEFAULT_POSTGRES_USERNAME);
        POSTGRES_PASSWORD = System.getProperty("postgres.password", DEFAULT_POSTGRES_PASSWORD);
        POSTGRES_SCHEMA_FILE_PATH = System.getProperty("postgres.schema.file.path", DEFAULT_POSTGRES_SCHEMA_FILE_PATH);
        POSTGRES_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("postgres.container.logging.enabled", DEFAULT_POSTGRES_CONTAINER_LOGGING_ENABLED));

        MONGODB_ENABLED = Boolean.valueOf(System.getProperty("mongodb.enabled", DEFAULT_MONGODB_ENABLED));
        MONGODB_IMAGE_TAG = System.getProperty("mongodb.image.tag", DEFAULT_MONGODB_IMAGE_TAG);
        // Port cannot be overridden in the MongoDB Testcontainer.
        MONGODB_PORT = Integer.parseInt(DEFAULT_MONGODB_PORT);
        MONGODB_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("mongodb.container.logging.enabled", DEFAULT_MONGODB_CONTAINER_LOGGING_ENABLED));

        MARIADB_ENABLED = Boolean.valueOf(System.getProperty("mariadb.enabled", DEFAULT_MARIADB_ENABLED));
        MARIADB_IMAGE_TAG = System.getProperty("mariadb.image.tag", DEFAULT_MARIADB_IMAGE_TAG);
        MARIADB_HOST_NAME = System.getProperty("mariadb.host.name", DEFAULT_MARIADB_HOST_NAME);
        MARIADB_PORT = Integer.parseInt(System.getProperty("mariadb.port", DEFAULT_MARIADB_PORT));
        MARIADB_DATABASE_NAME = System.getProperty("mariadb.database.name", DEFAULT_MARIADB_DATABASE_NAME);
        MARIADB_USERNAME = System.getProperty("mariadb.username", DEFAULT_MARIADB_USERNAME);
        MARIADB_PASSWORD = System.getProperty("mariadb.password", DEFAULT_MARIADB_PASSWORD);
        MARIADB_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("mariadb.container.logging.enabled", DEFAULT_MARIADB_CONTAINER_LOGGING_ENABLED));

        KAFKA_ENABLED = Boolean.valueOf(System.getProperty("kafka.enabled", DEFAULT_KAFKA_ENABLED));
        KAFKA_NATIVE_ENABLED = Boolean.valueOf(System.getProperty("kafka.native.enabled", DEFAULT_KAFKA_NATIVE_ENABLED));
        KAFKA_BROKER_COUNT = Integer.parseInt(System.getProperty("kafka.broker.count", DEFAULT_KAFKA_BROKER_COUNT));
        KAFKA_CONFLUENT_IMAGE_TAG = System.getProperty("kafka.confluent.image.tag", DEFAULT_KAFKA_CONFLUENT_IMAGE_TAG);
        KAFKA_APACHE_NATIVE_IMAGE_TAG = System.getProperty("kafka.apache.native.image.tag", DEFAULT_KAFKA_APACHE_NATIVE_IMAGE_TAG);
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
        WIREMOCK_OPTIONS = System.getProperty("wiremock.options", DEFAULT_WIREMOCK_OPTIONS);

        LOCALSTACK_ENABLED = Boolean.valueOf(System.getProperty("localstack.enabled", DEFAULT_LOCALSTACK_ENABLED));
        LOCALSTACK_IMAGE_TAG = System.getProperty("localstack.image.tag", DEFAULT_LOCALSTACK_IMAGE_TAG);
        LOCALSTACK_PORT = Integer.parseInt(System.getProperty("localstack.port", DEFAULT_LOCALSTACK_PORT));
        LOCALSTACK_SERVICES = System.getProperty("localstack.services", DEFAULT_LOCALSTACK_SERVICES);
        LOCALSTACK_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("localstack.container.logging.enabled", DEFAULT_LOCALSTACK_CONTAINER_LOGGING_ENABLED));
        LOCALSTACK_INIT_FILE_PATH = System.getProperty("localstack.init.file.path", DEFAULT_LOCALSTACK_INIT_FILE_PATH);

        ELASTICSEARCH_ENABLED = Boolean.valueOf(System.getProperty("elasticsearch.enabled", DEFAULT_ELASTICSEARCH_ENABLED));
        ELASTICSEARCH_IMAGE_TAG = System.getProperty("elasticsearch.image.tag", DEFAULT_ELASTICSEARCH_IMAGE_TAG);
        // Port cannot be overridden in the Elasticsearch Testcontainer.
        ELASTICSEARCH_PORT = Integer.parseInt(DEFAULT_ELASTICSEARCH_PORT);
        ELASTICSEARCH_PASSWORD = System.getProperty("elasticsearch.password", DEFAULT_ELASTICSEARCH_PASSWORD);
        ELASTICSEARCH_CLUSTER_NAME = System.getProperty("elasticsearch.cluster.name", DEFAULT_ELASTICSEARCH_CLUSTER_NAME);
        ELASTICSEARCH_DISCOVERY_TYPE = System.getProperty("elasticsearch.discovery.type", DEFAULT_ELASTICSEARCH_DISCOVERY_TYPE);
        ELASTICSEARCH_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("elasticsearch.container.logging.enabled", DEFAULT_ELASTICSEARCH_CONTAINER_LOGGING_ENABLED));

        AMBAR_ENABLED = Boolean.valueOf(System.getProperty("ambar.enabled", DEFAULT_AMBAR_ENABLED));
        AMBAR_IMAGE_TAG = System.getProperty("ambar.image.tag", DEFAULT_AMBAR_IMAGE_TAG);
        AMBAR_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("ambar.container.logging.enabled", DEFAULT_AMBAR_CONTAINER_LOGGING_ENABLED));
        AMBAR_CONFIG_FILE_PATH = System.getProperty("ambar.config.file.path", DEFAULT_AMBAR_CONFIG_FILE_PATH);
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

    protected static Map<String, String> parseKvPairs(String input) {
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
                throw new IllegalArgumentException("invalid key/value pair string for service property");
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

        log.info("containers.stayup: " + CONTAINERS_STAYUP);
        log.info("container.name.prefix: " + CONTAINER_NAME_PREFIX);
        log.info("container.main.label: " + CONTAINER_MAIN_LABEL);
        log.info("container.append.group.id: " + CONTAINER_APPEND_GROUP_ID);
        if(CONTAINER_APPEND_GROUP_ID) {
            log.info("container group unique id: " + CONTAINER_GROUP_ID);
        }

        log.info("service.name: " + SERVICE_NAME);
        log.info("service.instance.count: " + SERVICE_INSTANCE_COUNT);
        log.info("service.port: " + SERVICE_PORT);
        log.info("service.debug.port: " + SERVICE_DEBUG_PORT);
        log.info("service.debug.suspend: " + SERVICE_DEBUG_SUSPEND);
        log.info("service.envvars: " + SERVICE_ENV_VARS);
        log.info("service.additional.filesystem.binds: " + SERVICE_ADDITIONAL_FILESYSTEM_BINDS);
        log.info("service.config.files.system.property: " + SERVICE_CONFIG_FILES_SYSTEM_PROPERTY);
        log.info("service.application.yml.path: " + SERVICE_APPLICATION_YML_PATH);
        log.info("service.startup.health.endpoint: " + SERVICE_STARTUP_HEALTH_ENDPOINT);
        String serviceStartupLogMessage = SERVICE_STARTUP_LOG_MESSAGE;
        if(SERVICE_STARTUP_LOG_MESSAGE == null) {
            serviceStartupLogMessage = "";
        }
        log.info("service.startup.log.message: " + serviceStartupLogMessage);
        String serviceApplicationArgs = SERVICE_APPLICATION_ARGS;
        if(SERVICE_APPLICATION_ARGS == null) {
            serviceApplicationArgs = "";
        }
        log.info("service.application.args: " + serviceApplicationArgs);
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
            String postgresSchemaFilePath = POSTGRES_SCHEMA_FILE_PATH;
            if(POSTGRES_SCHEMA_FILE_PATH == null) {
                postgresSchemaFilePath = "";
            }
            log.info("postgres.schema.file.path: " + postgresSchemaFilePath);
            log.info("postgres.container.logging.enabled: " + POSTGRES_CONTAINER_LOGGING_ENABLED);
        }

        log.info("mongodb.enabled: " + MONGODB_ENABLED);
        if(MONGODB_ENABLED) {
            log.info("mongodb.image.tag: " + MONGODB_IMAGE_TAG);
            log.info("mongodb.port: " + MONGODB_PORT);
            log.info("mongodb.container.logging.enabled: " + MONGODB_CONTAINER_LOGGING_ENABLED);
        }

        log.info("mariadb.enabled: " + MARIADB_ENABLED);
        if(MARIADB_ENABLED) {
            log.info("mariadb.image.tag: " + MARIADB_IMAGE_TAG);
            log.info("mariadb.host.name: " + MARIADB_HOST_NAME);
            log.info("mariadb.port: " + MARIADB_PORT);
            log.info("mariadb.database.name: " + MARIADB_DATABASE_NAME);
            log.info("mariadb.username: " + MARIADB_USERNAME);
            log.info("mariadb.password: " + MARIADB_PASSWORD);
            log.info("mariadb.container.logging.enabled: " + MARIADB_CONTAINER_LOGGING_ENABLED);
        }

        log.info("kafka.enabled: " + KAFKA_ENABLED);
        log.info("kafka.native.enabled: " + KAFKA_NATIVE_ENABLED);
        if(KAFKA_ENABLED) {
            log.info("kafka.confluent.image.tag: " + KAFKA_CONFLUENT_IMAGE_TAG);
        }
        if(KAFKA_NATIVE_ENABLED) {
            log.info("kafka.apache.native.image.tag: " + KAFKA_APACHE_NATIVE_IMAGE_TAG);
        }
        if(KAFKA_ENABLED || KAFKA_NATIVE_ENABLED) {
            log.info("kafka.broker.count: " + KAFKA_BROKER_COUNT);
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
            log.info("wiremock.options: " + WIREMOCK_OPTIONS);
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

        log.info("ambar.enabled: " + AMBAR_ENABLED);
        if(AMBAR_ENABLED) {
            log.info("ambar.image.tag: " + AMBAR_IMAGE_TAG);
            log.info("ambar.config.file.path: " + AMBAR_CONFIG_FILE_PATH);
            log.info("ambar.container.logging.enabled: " + AMBAR_CONTAINER_LOGGING_ENABLED);
        }
    }
}
