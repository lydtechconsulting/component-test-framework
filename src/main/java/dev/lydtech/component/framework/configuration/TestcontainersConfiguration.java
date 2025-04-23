package dev.lydtech.component.framework.configuration;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import dev.lydtech.component.framework.management.AdditionalContainer;
import dev.lydtech.component.framework.management.ServiceConfiguration;
import org.apache.commons.lang3.RandomStringUtils;

public final class TestcontainersConfiguration {

    /**
     * The default configuration.
     */

    // --- Container configuration ---
    private static final String DEFAULT_CONTAINER_MAIN_LABEL_KEY = "dev.lydtech.main-container-label";
    private static final String DEFAULT_CONTAINER_GROUP_ID = RandomStringUtils.randomAlphanumeric(8).toLowerCase();
    private static final String DEFAULT_CONTAINER_NAME_PREFIX = "ct";
    private static final String DEFAULT_CONTAINER_MAIN_LABEL_NAME = "main-container";
    private static final String DEFAULT_CONTAINERS_STAYUP = "false";
    private static final String DEFAULT_CONTAINER_APPEND_GROUP_ID = "false";

    // --- Service default configuration ---
    protected static final String DEFAULT_SERVICE_NAME = "app";
    protected static final String DEFAULT_SERVICE_INSTANCE_COUNT = "1";
    protected static final String DEFAULT_SERVICE_PORT = "8080";
    protected static final String DEFAULT_SERVICE_DEBUG_PORT = "5001";
    protected static final String DEFAULT_SERVICE_STARTUP_TIMEOUT_SECONDS = "180";
    protected static final String DEFAULT_SERVICE_CONFIG_FILES_SYSTEM_PROPERTY = "spring.config.additional-location";
    protected static final String DEFAULT_SERVICE_APPLICATION_YML_PATH = "src/test/resources/application-component-test.yml";
    protected static final String DEFAULT_SERVICE_STARTUP_HEALTH_ENDPOINT = "/actuator/health";
    protected static final String DEFAULT_SERVICE_STARTUP_LOG_MESSAGE = null;
    protected static final String DEFAULT_SERVICE_IMAGE_TAG = "latest";
    protected static final String DEFAULT_SERVICE_CONTAINER_LOGGING_ENABLED = "false";
    protected static final String DEFAULT_SERVICE_DEBUG_SUSPEND = "false";
    protected static final String DEFAULT_SERVICE_APPLICATION_ARGS = null;
    protected static final String DEFAULT_SERVICE_ENVVARS = null;
    protected static final String DEFAULT_SERVICE_ADDITIONAL_FILESYSTEM_BINDS = null;

    // --- Additional containers configuration ---
    private static final String DEFAULT_ADDITIONAL_CONTAINERS = null;

    // --- Postgres configuration ---
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

    // --- MongoDB configuration ---
    private static final String DEFAULT_MONGODB_ENABLED = "false";
    private static final String DEFAULT_MONGODB_IMAGE_TAG = "7.0.2";
    private static final String DEFAULT_MONGODB_PORT = "27017";
    private static final String DEFAULT_MONGODB_CONTAINER_LOGGING_ENABLED = "false";

    // --- MariaDB configuration ---
    private static final String DEFAULT_MARIADB_ENABLED = "false";
    private static final String DEFAULT_MARIADB_IMAGE_TAG = "10.6";
    private static final String DEFAULT_MARIADB_HOST_NAME = "mariadb-host";
    private static final String DEFAULT_MARIADB_PORT = "3306";
    private static final String DEFAULT_MARIADB_DATABASE_NAME = "mariadb-db";
    private static final String DEFAULT_MARIADB_USERNAME = "user";
    private static final String DEFAULT_MARIADB_PASSWORD = "password";
    private static final String DEFAULT_MARIADB_CONTAINER_LOGGING_ENABLED = "false";

    // --- Kafka configuration ---
    private static final String DEFAULT_KAFKA_ENABLED = "false";
    private static final int DEFAULT_KAFKA_PORT = 9093;
    private static final int DEFAULT_KAFKA_INTERNAL_PORT = 9092;
    private static final String DEFAULT_KAFKA_NATIVE_ENABLED = "false";
    private static final int DEFAULT_KAFKA_NATIVE_PORT = 9092;
    private static final int DEFAULT_KAFKA_NATIVE_INTERNAL_PORT = 9093;
    private static final String DEFAULT_KAFKA_BROKER_COUNT = "1";
    private static final String DEFAULT_KAFKA_CONFLUENT_IMAGE_TAG = "7.3.2";
    private static final String DEFAULT_KAFKA_APACHE_NATIVE_IMAGE_TAG = "3.8.0";
    private static final String DEFAULT_KAFKA_TOPICS = null;
    private static final String DEFAULT_KAFKA_TOPIC_PARTITION_COUNT = "1";
    private static final String DEFAULT_KAFKA_CONTAINER_LOGGING_ENABLED = "false";
    private static final String DEFAULT_KAFKA_TOPIC_REPLICATION_FACTOR = "1";
    private static final String DEFAULT_KAFKA_MIN_INSYNC_REPLICAS = "1";
    private static final String DEFAULT_KAFKA_SASL_PLAIN_ENABLED = "false";
    private static final String DEFAULT_KAFKA_SASL_PLAIN_USERNAME = "demo";
    private static final String DEFAULT_KAFKA_SASL_PLAIN_PASSWORD = "demo-password";

    // --- Debezium (Kafka Connect) configuration ---
    private static final String DEFAULT_DEBEZIUM_ENABLED = "false";
    private static final String DEFAULT_DEBEZIUM_IMAGE_TAG = "2.4.0.Final";
    private static final String DEFAULT_DEBEZIUM_PORT = "8083";
    private static final String DEFAULT_DEBEZIUM_CONTAINER_LOGGING_ENABLED = "false";

    // --- Kafka Confluent Schema Registry configuration ---
    private static final String DEFAULT_KAFKA_SCHEMA_REGISTRY_ENABLED = "false";
    private static final String DEFAULT_KAFKA_SCHEMA_REGISTRY_CONFLUENT_IMAGE_TAG = "7.3.2";
    private static final String DEFAULT_KAFKA_SCHEMA_REGISTRY_PORT = "8081";
    private static final String DEFAULT_KAFKA_SCHEMA_REGISTRY_CONTAINER_LOGGING_ENABLED = "false";

    // --- Kafka Confluent Control Center configuration ---
    private static final String DEFAULT_KAFKA_CONTROL_CENTER_ENABLED = "false";
    private static final String DEFAULT_KAFKA_CONTROL_CENTER_CONFLUENT_IMAGE_TAG = "7.3.2";
    private static final String DEFAULT_KAFKA_CONTROL_CENTER_PORT = "9021";
    private static final String DEFAULT_KAFKA_CONTROL_CENTER_JMX_PORT = "9101";
    private static final String DEFAULT_KAFKA_CONTROL_CENTER_EXPORT_METRICS_ENABLED = "false";
    private static final String DEFAULT_KAFKA_CONTROL_CENTER_CONTAINER_LOGGING_ENABLED = "false";

    // --- Conduktor configuration ---
    private static final String DEFAULT_CONDUKTOR_ENABLED = "false";
    private static final String DEFAULT_CONDUKTOR_IMAGE_TAG = "1.23.0";
    private static final String DEFAULT_CONDUKTOR_LICENSE_KEY = null;
    private static final String DEFAULT_CONDUKTOR_CONTAINER_LOGGING_ENABLED = "false";
    private static final String DEFAULT_CONDUKTOR_PORT = "8088";
    public static final String DEFAULT_CONDUKTOR_POSTGRES_IMAGE_TAG = "postgres:14";
    public static final String DEFAULT_CONDUKTOR_POSTGRES_DB = "conduktor-console";
    public static final String DEFAULT_CONDUKTOR_POSTGRES_USER = "conduktor";
    public static final String DEFAULT_CONDUKTOR_POSTGRES_PASSWORD = "POSTGRES_PASSWORD";

    // --- Conduktor Gateway configuration ---
    private static final String DEFAULT_CONDUKTOR_GATEWAY_IMAGE_TAG = "3.2.1";
    private static final String DEFAULT_CONDUKTOR_GATEWAY_ENABLED = "false";
    private static final String DEFAULT_CONDUKTOR_GATEWAY_CONTAINER_LOGGING_ENABLED = "false";
    private static final String DEFAULT_CONDUKTOR_GATEWAY_PROXY_PORT = "6969";
    private static final String DEFAULT_CONDUKTOR_GATEWAY_HTTP_PORT = "8888";

    // --- Wiremock configuration ---
    private static final String DEFAULT_WIREMOCK_ENABLED = "false";
    private static final String DEFAULT_WIREMOCK_IMAGE_TAG = "3.6.0";
    private static final String DEFAULT_WIREMOCK_PORT = "8080";
    private static final String DEFAULT_WIREMOCK_CONTAINER_LOGGING_ENABLED = "false";
    private static final String DEFAULT_WIREMOCK_OPTIONS = "";

    // --- Localstack (AWS) configuration ---
    private static final String DEFAULT_LOCALSTACK_ENABLED = "false";
    private static final String DEFAULT_LOCALSTACK_IMAGE_TAG = "0.14.3";
    private static final String DEFAULT_LOCALSTACK_PORT = "4566";
    private static final String DEFAULT_LOCALSTACK_SERVICES = "dynamodb";
    private static final String DEFAULT_LOCALSTACK_CONTAINER_LOGGING_ENABLED = "false";
    private static final String DEFAULT_LOCALSTACK_INIT_FILE_PATH = null;

    // --- Elasticsearch configuration ---
    private static final String DEFAULT_ELASTICSEARCH_ENABLED = "false";
    private static final String DEFAULT_ELASTICSEARCH_IMAGE_TAG = "8.10.4";
    private static final String DEFAULT_ELASTICSEARCH_PORT = "9200";
    private static final String DEFAULT_ELASTICSEARCH_PASSWORD = null;
    private static final String DEFAULT_ELASTICSEARCH_CLUSTER_NAME = "elasticsearch";
    private static final String DEFAULT_ELASTICSEARCH_DISCOVERY_TYPE = "single-node";
    private static final String DEFAULT_ELASTICSEARCH_CONTAINER_LOGGING_ENABLED = "false";

    // --- Ambar configuration ---
    private static final String DEFAULT_AMBAR_ENABLED = "false";
    private static final String DEFAULT_AMBAR_IMAGE_TAG = "v1.8";
    private static final String DEFAULT_AMBAR_CONTAINER_LOGGING_ENABLED = "false";
    private static final String DEFAULT_AMBAR_CONFIG_FILE_PATH = "src/test/resources/ambar-config.yaml";

    /**
     * The runtime configuration.
     */

    // --- Container configuration ---
    // Label key for the Docker container housing the service.
    public static String CONTAINER_MAIN_LABEL_KEY;
    // A unique Id to optionally apply to docker container names to distinguish the group of containers for each test run.
    public static String CONTAINER_GROUP_ID;
    public static boolean CONTAINERS_STAYUP;
    public static boolean CONTAINER_APPEND_GROUP_ID;
    public static String CONTAINER_NAME_PREFIX;
    public static String CONTAINER_MAIN_LABEL_NAME;

    // --- Service configuration ---
    public static List<ServiceConfiguration> SERVICES;

    // --- Additional containers ---
    public static List<AdditionalContainer> ADDITIONAL_CONTAINERS;

    // --- Postgres configuration ---
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

    // --- MongoDB configuration ---
    public static boolean MONGODB_ENABLED;
    public static String MONGODB_IMAGE_TAG;
    public static Integer MONGODB_PORT;
    public static boolean MONGODB_CONTAINER_LOGGING_ENABLED;

    // --- MariaDB configuration ---
    public static boolean MARIADB_ENABLED;
    public static String MARIADB_IMAGE_TAG;
    public static String MARIADB_HOST_NAME;
    public static int MARIADB_PORT;
    public static String MARIADB_DATABASE_NAME;
    public static String MARIADB_USERNAME;
    public static String MARIADB_PASSWORD;
    public static boolean MARIADB_CONTAINER_LOGGING_ENABLED;

    // --- Kafka configuration ---
    public static boolean KAFKA_ENABLED;
    public static boolean KAFKA_NATIVE_ENABLED;
    public static int KAFKA_BROKER_COUNT;
    public static String KAFKA_CONFLUENT_IMAGE_TAG;
    public static String KAFKA_APACHE_NATIVE_IMAGE_TAG;
    public static int KAFKA_PORT;
    public static int KAFKA_INTERNAL_PORT;
    public static int KAFKA_NATIVE_PORT;
    public static int KAFKA_NATIVE_INTERNAL_PORT;

    public static List<String> KAFKA_TOPICS;
    public static int KAFKA_TOPIC_PARTITION_COUNT;
    public static boolean KAFKA_CONTAINER_LOGGING_ENABLED;
    public static int KAFKA_TOPIC_REPLICATION_FACTOR;
    public static int KAFKA_MIN_INSYNC_REPLICAS;
    public static boolean KAFKA_SASL_PLAIN_ENABLED;
    public static String KAFKA_SASL_PLAIN_USERNAME;
    public static String KAFKA_SASL_PLAIN_PASSWORD;

    // --- Kafka Schema Registry configuration ---
    public static boolean KAFKA_SCHEMA_REGISTRY_ENABLED;
    public static String KAFKA_SCHEMA_REGISTRY_CONFLUENT_IMAGE_TAG;
    public static int KAFKA_SCHEMA_REGISTRY_PORT;
    public static boolean KAFKA_SCHEMA_REGISTRY_CONTAINER_LOGGING_ENABLED;

    // --- Kafka Control Center configuration ---
    public static boolean KAFKA_CONTROL_CENTER_ENABLED;
    public static String KAFKA_CONTROL_CENTER_CONFLUENT_IMAGE_TAG;
    public static boolean KAFKA_CONTROL_CENTER_EXPORT_METRICS_ENABLED;
    public static String KAFKA_CONTROL_CENTER_JMX_PORT;
    public static int KAFKA_CONTROL_CENTER_PORT;
    public static boolean KAFKA_CONTROL_CENTER_CONTAINER_LOGGING_ENABLED;

    // --- Conduktor configuration ---
    public static boolean CONDUKTOR_ENABLED;
    public static String CONDUKTOR_IMAGE_TAG;
    public static String CONDUKTOR_LICENSE_KEY;
    public static int CONDUKTOR_PORT;
    public static boolean CONDUKTOR_CONTAINER_LOGGING_ENABLED;
    public static String CONDUKTOR_POSTGRES_IMAGE_TAG;
    public static String CONDUKTOR_POSTGRES_DB;
    public static String CONDUKTOR_POSTGRES_USER;
    public static String CONDUKTOR_POSTGRES_PASSWORD;

    // --- Conduktor Gateway configuration ---
    public static boolean CONDUKTOR_GATEWAY_ENABLED;
    public static String CONDUKTOR_GATEWAY_IMAGE_TAG;
    public static int CONDUKTOR_GATEWAY_PROXY_PORT;
    public static int CONDUKTOR_GATEWAY_HTTP_PORT;
    public static boolean CONDUKTOR_GATEWAY_CONTAINER_LOGGING_ENABLED;

    // --- Debezium configuration ---
    public static boolean DEBEZIUM_ENABLED;
    public static String DEBEZIUM_IMAGE_TAG;
    public static int DEBEZIUM_PORT;
    public static boolean DEBEZIUM_CONTAINER_LOGGING_ENABLED;

    // --- Wiremock configuration ---
    public static boolean WIREMOCK_ENABLED;
    public static String WIREMOCK_IMAGE_TAG;
    public static int WIREMOCK_PORT;
    public static boolean WIREMOCK_CONTAINER_LOGGING_ENABLED;
    public static String WIREMOCK_OPTIONS;

    // --- Localstack configuration ---
    public static boolean LOCALSTACK_ENABLED;
    public static String LOCALSTACK_IMAGE_TAG;
    public static int LOCALSTACK_PORT;
    public static String LOCALSTACK_SERVICES;
    public static boolean LOCALSTACK_CONTAINER_LOGGING_ENABLED;
    public static String LOCALSTACK_INIT_FILE_PATH;

    // --- Elasticsearch configuration ---
    public static boolean ELASTICSEARCH_ENABLED;
    public static String ELASTICSEARCH_IMAGE_TAG;
    public static int ELASTICSEARCH_PORT;
    public static String ELASTICSEARCH_PASSWORD;
    public static String ELASTICSEARCH_CLUSTER_NAME;
    public static String ELASTICSEARCH_DISCOVERY_TYPE;
    public static boolean ELASTICSEARCH_CONTAINER_LOGGING_ENABLED;

    // --- Ambar configuration ---
    public static boolean AMBAR_ENABLED;
    public static String AMBAR_IMAGE_TAG;
    public static boolean AMBAR_CONTAINER_LOGGING_ENABLED;
    public static String AMBAR_CONFIG_FILE_PATH;

    // Private constructor to prevent instantiation.
    private TestcontainersConfiguration() {}

    /**
     * Update static configuration fields by merging the provided properties.
     * This should be called after all external overrides have been applied.
     *
     * @param properties merged properties containing defaults and overrides.
     */
    public static void configure(Properties properties) {

        // --- Container configuration ---
        CONTAINER_MAIN_LABEL_KEY = DEFAULT_CONTAINER_MAIN_LABEL_KEY;
        CONTAINER_GROUP_ID = DEFAULT_CONTAINER_GROUP_ID;
        CONTAINERS_STAYUP = Boolean.parseBoolean(System.getenv("TESTCONTAINERS_REUSE_ENABLE")!=null?System.getenv("TESTCONTAINERS_REUSE_ENABLE"):DEFAULT_CONTAINERS_STAYUP);
        CONTAINER_NAME_PREFIX = properties.getProperty("container.name.prefix", DEFAULT_CONTAINER_NAME_PREFIX);
        CONTAINER_MAIN_LABEL_NAME = properties.getProperty("container.main.label", DEFAULT_CONTAINER_MAIN_LABEL_NAME);
        CONTAINER_APPEND_GROUP_ID = Boolean.parseBoolean(properties.getProperty("container.append.group.id", DEFAULT_CONTAINER_APPEND_GROUP_ID));

        // --- Service configuration ---
        SERVICES = ConfigurationParser.parseServices(properties);

        // --- Additional containers ---
        ADDITIONAL_CONTAINERS = ConfigurationParser.parseAdditionalContainers(properties.getProperty("additional.containers", DEFAULT_ADDITIONAL_CONTAINERS));

        // --- Postgres configuration ---
        POSTGRES_ENABLED = Boolean.parseBoolean(properties.getProperty("postgres.enabled", DEFAULT_POSTGRES_ENABLED));
        POSTGRES_IMAGE_TAG = properties.getProperty("postgres.image.tag", DEFAULT_POSTGRES_IMAGE_TAG);
        POSTGRES_HOST_NAME = properties.getProperty("postgres.host.name", DEFAULT_POSTGRES_HOST_NAME);
        POSTGRES_PORT = Integer.parseInt(properties.getProperty("postgres.port", DEFAULT_POSTGRES_PORT));
        POSTGRES_DATABASE_NAME = properties.getProperty("postgres.database.name", DEFAULT_POSTGRES_DATABASE_NAME);
        POSTGRES_SCHEMA_NAME = properties.getProperty("postgres.schema.name", DEFAULT_POSTGRES_SCHEMA_NAME);
        POSTGRES_USERNAME = properties.getProperty("postgres.username", DEFAULT_POSTGRES_USERNAME);
        POSTGRES_PASSWORD = properties.getProperty("postgres.password", DEFAULT_POSTGRES_PASSWORD);
        POSTGRES_SCHEMA_FILE_PATH = properties.getProperty("postgres.schema.file.path", DEFAULT_POSTGRES_SCHEMA_FILE_PATH);
        POSTGRES_CONTAINER_LOGGING_ENABLED = Boolean.parseBoolean(properties.getProperty("postgres.container.logging.enabled", DEFAULT_POSTGRES_CONTAINER_LOGGING_ENABLED));

        // --- MongoDB configuration ---
        MONGODB_ENABLED = Boolean.parseBoolean(properties.getProperty("mongodb.enabled", DEFAULT_MONGODB_ENABLED));
        MONGODB_IMAGE_TAG = properties.getProperty("mongodb.image.tag", DEFAULT_MONGODB_IMAGE_TAG);
        // Port cannot be overridden in the MongoDB Testcontainer.
        MONGODB_PORT = Integer.parseInt(DEFAULT_MONGODB_PORT);
        MONGODB_CONTAINER_LOGGING_ENABLED = Boolean.parseBoolean(properties.getProperty("mongodb.container.logging.enabled",DEFAULT_MONGODB_CONTAINER_LOGGING_ENABLED));

        // --- MariaDB configuration ---
        MARIADB_ENABLED = Boolean.parseBoolean(properties.getProperty("mariadb.enabled", DEFAULT_MARIADB_ENABLED));
        MARIADB_IMAGE_TAG = properties.getProperty("mariadb.image.tag", DEFAULT_MARIADB_IMAGE_TAG);
        MARIADB_HOST_NAME = properties.getProperty("mariadb.host.name", DEFAULT_MARIADB_HOST_NAME);
        MARIADB_PORT = Integer.parseInt(properties.getProperty("mariadb.port", DEFAULT_MARIADB_PORT));
        MARIADB_DATABASE_NAME = properties.getProperty("mariadb.database.name", DEFAULT_MARIADB_DATABASE_NAME);
        MARIADB_USERNAME = properties.getProperty("mariadb.username", DEFAULT_MARIADB_USERNAME);
        MARIADB_PASSWORD = properties.getProperty("mariadb.password", DEFAULT_MARIADB_PASSWORD);
        MARIADB_CONTAINER_LOGGING_ENABLED = Boolean.parseBoolean(properties.getProperty("mariadb.container.logging.enabled", DEFAULT_MARIADB_CONTAINER_LOGGING_ENABLED));

        // --- Kafka configuration ---
        KAFKA_ENABLED = Boolean.valueOf(properties.getProperty("kafka.enabled", DEFAULT_KAFKA_ENABLED));
        KAFKA_NATIVE_ENABLED = Boolean.valueOf(properties.getProperty("kafka.native.enabled", DEFAULT_KAFKA_NATIVE_ENABLED));
        // These Kafka ports are hardcoded in org.testcontainers.containers.KafkaContainer (Standard) and org.testcontainers.kafka.KafkaContainer (Native) respectively.
        // PORT is the port for calls made from outside the Docker network (e.g. to 'localhost').
        // INTERNAL_PORT is the port for calls between services within the Docker network (e.g. to the 'kafka' container).
        KAFKA_PORT = DEFAULT_KAFKA_PORT;
        KAFKA_INTERNAL_PORT = DEFAULT_KAFKA_INTERNAL_PORT;
        KAFKA_NATIVE_PORT = DEFAULT_KAFKA_NATIVE_PORT;
        KAFKA_NATIVE_INTERNAL_PORT = DEFAULT_KAFKA_NATIVE_INTERNAL_PORT;
        KAFKA_BROKER_COUNT = Integer.parseInt(properties.getProperty("kafka.broker.count", DEFAULT_KAFKA_BROKER_COUNT));
        KAFKA_CONFLUENT_IMAGE_TAG = properties.getProperty("kafka.confluent.image.tag", DEFAULT_KAFKA_CONFLUENT_IMAGE_TAG);
        KAFKA_APACHE_NATIVE_IMAGE_TAG = properties.getProperty("kafka.apache.native.image.tag", DEFAULT_KAFKA_APACHE_NATIVE_IMAGE_TAG);
        KAFKA_TOPICS = ConfigurationParser.parseKafkaTopics(properties.getProperty("kafka.topics", DEFAULT_KAFKA_TOPICS));
        KAFKA_TOPIC_PARTITION_COUNT = Integer.parseInt(properties.getProperty("kafka.topic.partition.count", DEFAULT_KAFKA_TOPIC_PARTITION_COUNT));
        KAFKA_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(properties.getProperty("kafka.container.logging.enabled", DEFAULT_KAFKA_CONTAINER_LOGGING_ENABLED));
        KAFKA_TOPIC_REPLICATION_FACTOR = Integer.parseInt(properties.getProperty("kafka.topic.replication.factor", DEFAULT_KAFKA_TOPIC_REPLICATION_FACTOR));
        KAFKA_MIN_INSYNC_REPLICAS = Integer.parseInt(properties.getProperty("kafka.min.insync.replicas", DEFAULT_KAFKA_MIN_INSYNC_REPLICAS));
        KAFKA_SASL_PLAIN_ENABLED = Boolean.valueOf(properties.getProperty("kafka.sasl.plain.enabled", DEFAULT_KAFKA_SASL_PLAIN_ENABLED));
        KAFKA_SASL_PLAIN_USERNAME = properties.getProperty("kafka.sasl.plain.username", DEFAULT_KAFKA_SASL_PLAIN_USERNAME);
        KAFKA_SASL_PLAIN_PASSWORD = properties.getProperty("kafka.sasl.plain.password", DEFAULT_KAFKA_SASL_PLAIN_PASSWORD);

        // --- Kafka Schema Registry configuration ---
        KAFKA_SCHEMA_REGISTRY_ENABLED = Boolean.valueOf(properties.getProperty("kafka.schema.registry.enabled", DEFAULT_KAFKA_SCHEMA_REGISTRY_ENABLED));
        KAFKA_SCHEMA_REGISTRY_CONFLUENT_IMAGE_TAG = properties.getProperty("kafka.schema.registry.confluent.image.tag", DEFAULT_KAFKA_SCHEMA_REGISTRY_CONFLUENT_IMAGE_TAG);
        KAFKA_SCHEMA_REGISTRY_PORT = Integer.parseInt(properties.getProperty("kafka.schema.registry.port", DEFAULT_KAFKA_SCHEMA_REGISTRY_PORT));
        KAFKA_SCHEMA_REGISTRY_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(properties.getProperty("kafka.schema.registry.container.logging.enabled", DEFAULT_KAFKA_SCHEMA_REGISTRY_CONTAINER_LOGGING_ENABLED));

        // --- Kafka Control Center configuration ---
        KAFKA_CONTROL_CENTER_ENABLED = Boolean.valueOf(properties.getProperty("kafka.control.center.enabled", DEFAULT_KAFKA_CONTROL_CENTER_ENABLED));
        KAFKA_CONTROL_CENTER_CONFLUENT_IMAGE_TAG = properties.getProperty("kafka.control.center.confluent.image.tag", DEFAULT_KAFKA_CONTROL_CENTER_CONFLUENT_IMAGE_TAG);
        KAFKA_CONTROL_CENTER_EXPORT_METRICS_ENABLED = Boolean.valueOf(properties.getProperty("kafka.control.center.export.metrics.enabled", DEFAULT_KAFKA_CONTROL_CENTER_EXPORT_METRICS_ENABLED));
        KAFKA_CONTROL_CENTER_JMX_PORT = properties.getProperty("kafka.control.center.jmx.port", DEFAULT_KAFKA_CONTROL_CENTER_JMX_PORT);
        KAFKA_CONTROL_CENTER_PORT = Integer.parseInt(properties.getProperty("kafka.control.center.port", DEFAULT_KAFKA_CONTROL_CENTER_PORT));
        KAFKA_CONTROL_CENTER_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(properties.getProperty("kafka.control.center.container.logging.enabled", DEFAULT_KAFKA_CONTROL_CENTER_CONTAINER_LOGGING_ENABLED));

        // --- Conduktor configuration ---
        CONDUKTOR_ENABLED = Boolean.valueOf(properties.getProperty("conduktor.enabled", DEFAULT_CONDUKTOR_ENABLED));
        CONDUKTOR_IMAGE_TAG = properties.getProperty("conduktor.image.tag", DEFAULT_CONDUKTOR_IMAGE_TAG);
        CONDUKTOR_LICENSE_KEY = properties.getProperty("conduktor.license.key", DEFAULT_CONDUKTOR_LICENSE_KEY);
        CONDUKTOR_PORT = Integer.parseInt(properties.getProperty("conduktor.port", DEFAULT_CONDUKTOR_PORT));
        CONDUKTOR_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(properties.getProperty("conduktor.container.logging.enabled", DEFAULT_CONDUKTOR_CONTAINER_LOGGING_ENABLED));
        // Conduktor properties that are not overridable.
        CONDUKTOR_POSTGRES_IMAGE_TAG = DEFAULT_CONDUKTOR_POSTGRES_IMAGE_TAG;
        CONDUKTOR_POSTGRES_DB = DEFAULT_CONDUKTOR_POSTGRES_DB;
        CONDUKTOR_POSTGRES_USER = DEFAULT_CONDUKTOR_POSTGRES_USER;
        CONDUKTOR_POSTGRES_PASSWORD = DEFAULT_CONDUKTOR_POSTGRES_PASSWORD;

        // --- Conduktor Gateway configuration ---
        CONDUKTOR_GATEWAY_ENABLED = Boolean.valueOf(properties.getProperty("conduktor.gateway.enabled", DEFAULT_CONDUKTOR_GATEWAY_ENABLED));
        CONDUKTOR_GATEWAY_IMAGE_TAG = properties.getProperty("conduktor.gateway.image.tag", DEFAULT_CONDUKTOR_GATEWAY_IMAGE_TAG);
        CONDUKTOR_GATEWAY_PROXY_PORT = Integer.parseInt(properties.getProperty("conduktor.gateway.proxy.port", DEFAULT_CONDUKTOR_GATEWAY_PROXY_PORT));
        CONDUKTOR_GATEWAY_HTTP_PORT = Integer.parseInt(properties.getProperty("conduktor.gateway.http.port", DEFAULT_CONDUKTOR_GATEWAY_HTTP_PORT));
        CONDUKTOR_GATEWAY_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(properties.getProperty("conduktor.gateway.container.logging.enabled", DEFAULT_CONDUKTOR_GATEWAY_CONTAINER_LOGGING_ENABLED));

        // --- Debezium configuration ---
        DEBEZIUM_ENABLED = Boolean.valueOf(properties.getProperty("debezium.enabled", DEFAULT_DEBEZIUM_ENABLED));
        DEBEZIUM_IMAGE_TAG = properties.getProperty("debezium.image.tag", DEFAULT_DEBEZIUM_IMAGE_TAG);
        DEBEZIUM_PORT = Integer.parseInt(properties.getProperty("debezium.port", DEFAULT_DEBEZIUM_PORT));
        DEBEZIUM_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(properties.getProperty("debezium.container.logging.enabled", DEFAULT_DEBEZIUM_CONTAINER_LOGGING_ENABLED));

        // --- Wiremock configuration ---
        WIREMOCK_ENABLED = Boolean.valueOf(properties.getProperty("wiremock.enabled", DEFAULT_WIREMOCK_ENABLED));
        WIREMOCK_IMAGE_TAG = properties.getProperty("wiremock.image.tag", DEFAULT_WIREMOCK_IMAGE_TAG);
        WIREMOCK_PORT = Integer.parseInt(properties.getProperty("wiremock.port", DEFAULT_WIREMOCK_PORT));
        WIREMOCK_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(properties.getProperty("wiremock.container.logging.enabled", DEFAULT_WIREMOCK_CONTAINER_LOGGING_ENABLED));
        WIREMOCK_OPTIONS = properties.getProperty("wiremock.options", DEFAULT_WIREMOCK_OPTIONS);

        // --- Localstack configuration ---
        LOCALSTACK_ENABLED = Boolean.valueOf(properties.getProperty("localstack.enabled", DEFAULT_LOCALSTACK_ENABLED));
        LOCALSTACK_IMAGE_TAG = properties.getProperty("localstack.image.tag", DEFAULT_LOCALSTACK_IMAGE_TAG);
        LOCALSTACK_PORT = Integer.parseInt(properties.getProperty("localstack.port", DEFAULT_LOCALSTACK_PORT));
        LOCALSTACK_SERVICES = properties.getProperty("localstack.services", DEFAULT_LOCALSTACK_SERVICES);
        LOCALSTACK_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(properties.getProperty("localstack.container.logging.enabled", DEFAULT_LOCALSTACK_CONTAINER_LOGGING_ENABLED));
        LOCALSTACK_INIT_FILE_PATH = properties.getProperty("localstack.init.file.path", DEFAULT_LOCALSTACK_INIT_FILE_PATH);

        // --- Elasticsearch configuration ---
        ELASTICSEARCH_ENABLED = Boolean.valueOf(properties.getProperty("elasticsearch.enabled", DEFAULT_ELASTICSEARCH_ENABLED));
        ELASTICSEARCH_IMAGE_TAG = properties.getProperty("elasticsearch.image.tag", DEFAULT_ELASTICSEARCH_IMAGE_TAG);
        // Port cannot be overridden in the Elasticsearch Testcontainer.
        ELASTICSEARCH_PORT = Integer.parseInt(DEFAULT_ELASTICSEARCH_PORT);
        ELASTICSEARCH_PASSWORD = properties.getProperty("elasticsearch.password", DEFAULT_ELASTICSEARCH_PASSWORD);
        ELASTICSEARCH_CLUSTER_NAME = properties.getProperty("elasticsearch.cluster.name", DEFAULT_ELASTICSEARCH_CLUSTER_NAME);
        ELASTICSEARCH_DISCOVERY_TYPE = properties.getProperty("elasticsearch.discovery.type", DEFAULT_ELASTICSEARCH_DISCOVERY_TYPE);
        ELASTICSEARCH_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(properties.getProperty("elasticsearch.container.logging.enabled", DEFAULT_ELASTICSEARCH_CONTAINER_LOGGING_ENABLED));

        // --- Ambar configuration ---
        AMBAR_ENABLED = Boolean.valueOf(properties.getProperty("ambar.enabled", DEFAULT_AMBAR_ENABLED));
        AMBAR_IMAGE_TAG = properties.getProperty("ambar.image.tag", DEFAULT_AMBAR_IMAGE_TAG);
        AMBAR_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(properties.getProperty("ambar.container.logging.enabled", DEFAULT_AMBAR_CONTAINER_LOGGING_ENABLED));
        AMBAR_CONFIG_FILE_PATH = properties.getProperty("ambar.config.file.path", DEFAULT_AMBAR_CONFIG_FILE_PATH);
    }
}
