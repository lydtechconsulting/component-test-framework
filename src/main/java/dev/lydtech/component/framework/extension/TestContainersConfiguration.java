package dev.lydtech.component.framework.extension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    private static final String DEFAULT_KAFKA_CONFLUENT_IMAGE_TAG = "6.2.4";
    private static final String DEFAULT_KAFKA_PORT = "9093";
    private static final String DEFAULT_KAFKA_TOPIC_PARTITION_COUNT = "5";
    private static final String DEFAULT_KAFKA_CONTAINER_LOGGING_ENABLED = "false";

    /**
     * Debezium (Kafka Connect) default configuration.
     */

    private static final String DEFAULT_DEBEZIUM_ENABLED = "false";
    private static final String DEFAULT_DEBEZIUM_IMAGE_TAG = "1.7.0.Final";
    private static final String DEFAULT_DEBEZIUM_PORT = "8083";
    private static final String DEFAULT_DEBEZIUM_CONTAINER_LOGGING_ENABLED = "false";

    /**
     * Wiremock default configuration.
     */

    private static final String DEFAULT_WIREMOCK_ENABLED = "false";
    private static final String DEFAULT_WIREMOCK_IMAGE_TAG = "2.32.0";
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

    public static final String CONTAINER_NAME_PREFIX = System.getProperty("container.name.prefix", DEFAULT_CONTAINER_NAME_PREFIX);
    public static final String CONTAINER_MAIN_LABEL = System.getProperty("container.main.label", DEFAULT_CONTAINER_MAIN_LABEL);

    public static final String SERVICE_NAME = System.getProperty("service.name", DEFAULT_SERVICE_NAME);
    public static final int SERVICE_INSTANCE_COUNT = Integer.parseInt(System.getProperty("service.instance.count", DEFAULT_SERVICE_INSTANCE_COUNT));
    public static final int SERVICE_PORT = Integer.parseInt(System.getProperty("service.port", DEFAULT_SERVICE_PORT));
    public static final int SERVICE_DEBUG_PORT = Integer.parseInt(System.getProperty("service.debug.port", DEFAULT_SERVICE_DEBUG_PORT));
    public static final int SERVICE_STARTUP_TIMEOUT_SECONDS = Integer.parseInt(System.getProperty("service.startup.timeout.seconds", DEFAULT_SERVICE_STARTUP_TIMEOUT_SECONDS));
    public static final String SERVICE_IMAGE_TAG = System.getProperty("service.image.tag", DEFAULT_SERVICE_IMAGE_TAG);
    public static final boolean SERVICE_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("service.container.logging.enabled", DEFAULT_SERVICE_CONTAINER_LOGGING_ENABLED));

    public static final boolean POSTGRES_ENABLED = Boolean.valueOf(System.getProperty("postgres.enabled", DEFAULT_POSTGRES_ENABLED));
    public static final String POSTGRES_IMAGE_TAG = System.getProperty("postgres.image.tag", DEFAULT_POSTGRES_IMAGE_TAG);
    public static final String POSTGRES_HOST_NAME = System.getProperty("postgres.host.name", DEFAULT_POSTGRES_HOST_NAME);
    public static final int POSTGRES_PORT = Integer.parseInt(System.getProperty("postgres.port", DEFAULT_POSTGRES_PORT));
    public static final String POSTGRES_DATABASE_NAME = System.getProperty("postgres.database.name", DEFAULT_POSTGRES_DATABASE_NAME);
    public static final String POSTGRES_USERNAME = System.getProperty("postgres.username", DEFAULT_POSTGRES_USERNAME);
    public static final String POSTGRES_PASSWORD = System.getProperty("postgres.password", DEFAULT_POSTGRES_PASSWORD);
    public static final boolean POSTGRES_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("postgres.container.logging.enabled", DEFAULT_POSTGRES_CONTAINER_LOGGING_ENABLED));

    public static final boolean KAFKA_ENABLED = Boolean.valueOf(System.getProperty("kafka.enabled", DEFAULT_KAFKA_ENABLED));
    public static final String KAFKA_CONFLUENT_IMAGE_TAG = System.getProperty("kafka.confluent.image.tag", DEFAULT_KAFKA_CONFLUENT_IMAGE_TAG);
    public static final int KAFKA_PORT = Integer.parseInt(System.getProperty("kafka.port", DEFAULT_KAFKA_PORT));
    public static final List<String> KAFKA_TOPICS = parseKafkaTopics();
    public static final int KAFKA_TOPIC_PARTITION_COUNT = Integer.parseInt(System.getProperty("kafka.topic.partition.count", DEFAULT_KAFKA_TOPIC_PARTITION_COUNT));
    public static final boolean KAFKA_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("kafka.container.logging.enabled", DEFAULT_KAFKA_CONTAINER_LOGGING_ENABLED));

    public static final boolean DEBEZIUM_ENABLED = Boolean.valueOf(System.getProperty("debezium.enabled", DEFAULT_DEBEZIUM_ENABLED));
    public static final String DEBEZIUM_IMAGE_TAG = System.getProperty("debezium.image.tag", DEFAULT_DEBEZIUM_IMAGE_TAG);
    public static final int DEBEZIUM_PORT = Integer.parseInt(System.getProperty("debezium.port", DEFAULT_DEBEZIUM_PORT));
    public static final boolean DEBEZIUM_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("debezium.container.logging.enabled", DEFAULT_DEBEZIUM_CONTAINER_LOGGING_ENABLED));

    public static final boolean WIREMOCK_ENABLED = Boolean.valueOf(System.getProperty("wiremock.enabled", DEFAULT_WIREMOCK_ENABLED));
    public static final String WIREMOCK_IMAGE_TAG = System.getProperty("wiremock.image.tag", DEFAULT_WIREMOCK_IMAGE_TAG);
    public static final int WIREMOCK_PORT = Integer.parseInt(System.getProperty("wiremock.port", DEFAULT_WIREMOCK_PORT));
    public static final boolean WIREMOCK_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("wiremock.container.logging.enabled", DEFAULT_WIREMOCK_CONTAINER_LOGGING_ENABLED));

    public static final boolean LOCALSTACK_ENABLED = Boolean.valueOf(System.getProperty("localstack.enabled", DEFAULT_LOCALSTACK_ENABLED));
    public static final String LOCALSTACK_IMAGE_TAG = System.getProperty("localstack.image.tag", DEFAULT_LOCALSTACK_IMAGE_TAG);
    public static final int LOCALSTACK_PORT = Integer.parseInt(System.getProperty("localstack.port", DEFAULT_LOCALSTACK_PORT));
    public static final String LOCALSTACK_SERVICES = System.getProperty("localstack.services", DEFAULT_LOCALSTACK_SERVICES);
    public static final boolean LOCALSTACK_CONTAINER_LOGGING_ENABLED = Boolean.valueOf(System.getProperty("localstack.container.logging.enabled", DEFAULT_LOCALSTACK_CONTAINER_LOGGING_ENABLED));

    private static List<String> parseKafkaTopics() {
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
            log.info("kafka.port: " + KAFKA_PORT);
            log.info("kafka.topics: " + KAFKA_TOPICS);
            log.info("kafka.topic.partition.count: " + KAFKA_TOPIC_PARTITION_COUNT);
            log.info("kafka.container.logging.enabled: " + KAFKA_CONTAINER_LOGGING_ENABLED);
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
