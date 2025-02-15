package dev.lydtech.component.framework.configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class ConfigurationKeys {

    // --- Container configuration keys ---
    public static final String CONTAINER_NAME_PREFIX_KEY = "container.name.prefix";
    public static final String CONTAINER_MAIN_LABEL_NAME_KEY = "container.main.label";
    public static final String CONTAINER_APPEND_GROUP_ID_KEY = "container.append.group.id";

    // --- Service configuration keys ---
    public static final String SERVICE_NAME_KEY = "service.name";
    public static final String SERVICE_INSTANCE_COUNT_KEY = "service.instance.count";
    public static final String SERVICE_PORT_KEY = "service.port";
    public static final String SERVICE_DEBUG_PORT_KEY = "service.debug.port";
    public static final String SERVICE_STARTUP_TIMEOUT_SECONDS_KEY = "service.startup.timeout.seconds";
    public static final String SERVICE_CONFIG_FILES_SYSTEM_PROPERTY_KEY = "service.config.files.system.property";
    public static final String SERVICE_APPLICATION_YML_PATH_KEY = "service.application.yml.path";
    public static final String SERVICE_STARTUP_HEALTH_ENDPOINT_KEY = "service.startup.health.endpoint";
    public static final String SERVICE_STARTUP_LOG_MESSAGE_KEY = "service.startup.log.message";
    public static final String SERVICE_IMAGE_TAG_KEY = "service.image.tag";
    public static final String SERVICE_CONTAINER_LOGGING_ENABLED_KEY = "service.container.logging.enabled";
    public static final String SERVICE_DEBUG_SUSPEND_KEY = "service.debug.suspend";
    public static final String SERVICE_APPLICATION_ARGS_KEY = "service.application.args";
    public static final String SERVICE_ENVVARS_KEY = "service.envvars";
    public static final String SERVICE_ADDITIONAL_FILESYSTEM_BINDS_KEY = "service.additional.filesystem.binds";

    // --- Additional containers configuration key ---
    public static final String ADDITIONAL_CONTAINERS_KEY = "additional.containers";

    // --- Postgres configuration keys ---
    public static final String POSTGRES_ENABLED_KEY = "postgres.enabled";
    public static final String POSTGRES_IMAGE_TAG_KEY = "postgres.image.tag";
    public static final String POSTGRES_HOST_NAME_KEY = "postgres.host.name";
    public static final String POSTGRES_PORT_KEY = "postgres.port";
    public static final String POSTGRES_DATABASE_NAME_KEY = "postgres.database.name";
    public static final String POSTGRES_SCHEMA_NAME_KEY = "postgres.schema.name";
    public static final String POSTGRES_USERNAME_KEY = "postgres.username";
    public static final String POSTGRES_PASSWORD_KEY = "postgres.password";
    public static final String POSTGRES_SCHEMA_FILE_PATH_KEY = "postgres.schema.file.path";
    public static final String POSTGRES_CONTAINER_LOGGING_ENABLED_KEY = "postgres.container.logging.enabled";

    // --- MongoDB configuration keys ---
    public static final String MONGODB_ENABLED_KEY = "mongodb.enabled";
    public static final String MONGODB_IMAGE_TAG_KEY = "mongodb.image.tag";
    public static final String MONGODB_CONTAINER_LOGGING_ENABLED_KEY = "mongodb.container.logging.enabled";

    // --- MariaDB configuration keys ---
    public static final String MARIADB_ENABLED_KEY = "mariadb.enabled";
    public static final String MARIADB_IMAGE_TAG_KEY = "mariadb.image.tag";
    public static final String MARIADB_HOST_NAME_KEY = "mariadb.host.name";
    public static final String MARIADB_PORT_KEY = "mariadb.port";
    public static final String MARIADB_DATABASE_NAME_KEY = "mariadb.database.name";
    public static final String MARIADB_USERNAME_KEY = "mariadb.username";
    public static final String MARIADB_PASSWORD_KEY = "mariadb.password";
    public static final String MARIADB_CONTAINER_LOGGING_ENABLED_KEY = "mariadb.container.logging.enabled";

    // --- Kafka configuration keys ---
    public static final String KAFKA_ENABLED_KEY = "kafka.enabled";
    public static final String KAFKA_NATIVE_ENABLED_KEY = "kafka.native.enabled";
    public static final String KAFKA_BROKER_COUNT_KEY = "kafka.broker.count";
    public static final String KAFKA_CONFLUENT_IMAGE_TAG_KEY = "kafka.confluent.image.tag";
    public static final String KAFKA_APACHE_NATIVE_IMAGE_TAG_KEY = "kafka.apache.native.image.tag";
    public static final String KAFKA_TOPICS_KEY = "kafka.topics";
    public static final String KAFKA_TOPIC_PARTITION_COUNT_KEY = "kafka.topic.partition.count";
    public static final String KAFKA_CONTAINER_LOGGING_ENABLED_KEY = "kafka.container.logging.enabled";
    public static final String KAFKA_TOPIC_REPLICATION_FACTOR_KEY = "kafka.topic.replication.factor";
    public static final String KAFKA_MIN_INSYNC_REPLICAS_KEY = "kafka.min.insync.replicas";
    public static final String KAFKA_SASL_PLAIN_ENABLED_KEY = "kafka.sasl.plain.enabled";
    public static final String KAFKA_SASL_PLAIN_USERNAME_KEY = "kafka.sasl.plain.username";
    public static final String KAFKA_SASL_PLAIN_PASSWORD_KEY = "kafka.sasl.plain.password";

    // --- Kafka Schema Registry configuration keys ---
    public static final String KAFKA_SCHEMA_REGISTRY_ENABLED_KEY = "kafka.schema.registry.enabled";
    public static final String KAFKA_SCHEMA_REGISTRY_CONFLUENT_IMAGE_TAG_KEY = "kafka.schema.registry.confluent.image.tag";
    public static final String KAFKA_SCHEMA_REGISTRY_PORT_KEY = "kafka.schema.registry.port";
    public static final String KAFKA_SCHEMA_REGISTRY_CONTAINER_LOGGING_ENABLED_KEY = "kafka.schema.registry.container.logging.enabled";

    // --- Kafka Control Center configuration keys ---
    public static final String KAFKA_CONTROL_CENTER_ENABLED_KEY = "kafka.control.center.enabled";
    public static final String KAFKA_CONTROL_CENTER_CONFLUENT_IMAGE_TAG_KEY = "kafka.control.center.confluent.image.tag";
    public static final String KAFKA_CONTROL_CENTER_EXPORT_METRICS_ENABLED_KEY = "kafka.control.center.export.metrics.enabled";
    public static final String KAFKA_CONTROL_CENTER_JMX_PORT_KEY = "kafka.control.center.jmx.port";
    public static final String KAFKA_CONTROL_CENTER_PORT_KEY = "kafka.control.center.port";
    public static final String KAFKA_CONTROL_CENTER_CONTAINER_LOGGING_ENABLED_KEY = "kafka.control.center.container.logging.enabled";

    // --- Conduktor configuration keys ---
    public static final String CONDUKTOR_ENABLED_KEY = "conduktor.enabled";
    public static final String CONDUKTOR_IMAGE_TAG_KEY = "conduktor.image.tag";
    public static final String CONDUKTOR_LICENSE_KEY_KEY = "conduktor.license.key";
    public static final String CONDUKTOR_PORT_KEY = "conduktor.port";
    public static final String CONDUKTOR_CONTAINER_LOGGING_ENABLED_KEY = "conduktor.container.logging.enabled";

    // --- Conduktor Gateway configuration keys ---
    public static final String CONDUKTOR_GATEWAY_ENABLED_KEY = "conduktor.gateway.enabled";
    public static final String CONDUKTOR_GATEWAY_IMAGE_TAG_KEY = "conduktor.gateway.image.tag";
    public static final String CONDUKTOR_GATEWAY_PROXY_PORT_KEY = "conduktor.gateway.proxy.port";
    public static final String CONDUKTOR_GATEWAY_HTTP_PORT_KEY = "conduktor.gateway.http.port";
    public static final String CONDUKTOR_GATEWAY_CONTAINER_LOGGING_ENABLED_KEY = "conduktor.gateway.container.logging.enabled";

    // --- Debezium configuration keys ---
    public static final String DEBEZIUM_ENABLED_KEY = "debezium.enabled";
    public static final String DEBEZIUM_IMAGE_TAG_KEY = "debezium.image.tag";
    public static final String DEBEZIUM_PORT_KEY = "debezium.port";
    public static final String DEBEZIUM_CONTAINER_LOGGING_ENABLED_KEY = "debezium.container.logging.enabled";

    // --- Wiremock configuration keys ---
    public static final String WIREMOCK_ENABLED_KEY = "wiremock.enabled";
    public static final String WIREMOCK_IMAGE_TAG_KEY = "wiremock.image.tag";
    public static final String WIREMOCK_PORT_KEY = "wiremock.port";
    public static final String WIREMOCK_CONTAINER_LOGGING_ENABLED_KEY = "wiremock.container.logging.enabled";
    public static final String WIREMOCK_OPTIONS_KEY = "wiremock.options";

    // --- Localstack configuration keys ---
    public static final String LOCALSTACK_ENABLED_KEY = "localstack.enabled";
    public static final String LOCALSTACK_IMAGE_TAG_KEY = "localstack.image.tag";
    public static final String LOCALSTACK_PORT_KEY = "localstack.port";
    public static final String LOCALSTACK_SERVICES_KEY = "localstack.services";
    public static final String LOCALSTACK_CONTAINER_LOGGING_ENABLED_KEY = "localstack.container.logging.enabled";
    public static final String LOCALSTACK_INIT_FILE_PATH_KEY = "localstack.init.file.path";

    // --- Elasticsearch configuration keys ---
    public static final String ELASTICSEARCH_ENABLED_KEY = "elasticsearch.enabled";
    public static final String ELASTICSEARCH_IMAGE_TAG_KEY = "elasticsearch.image.tag";
    public static final String ELASTICSEARCH_PASSWORD_KEY = "elasticsearch.password";
    public static final String ELASTICSEARCH_CLUSTER_NAME_KEY = "elasticsearch.cluster.name";
    public static final String ELASTICSEARCH_DISCOVERY_TYPE_KEY = "elasticsearch.discovery.type";
    public static final String ELASTICSEARCH_CONTAINER_LOGGING_ENABLED_KEY = "elasticsearch.container.logging.enabled";

    // --- Ambar configuration keys ---
    public static final String AMBAR_ENABLED_KEY = "ambar.enabled";
    public static final String AMBAR_IMAGE_TAG_KEY = "ambar.image.tag";
    public static final String AMBAR_CONTAINER_LOGGING_ENABLED_KEY = "ambar.container.logging.enabled";
    public static final String AMBAR_CONFIG_FILE_PATH_KEY = "ambar.config.file.path";

    // --- Provide a lookup Set for all keys ---
    protected static final Set<String> PROPERTY_KEYS;

    static {
        Set<String> keys = new HashSet<>(Arrays.asList(
                CONTAINER_NAME_PREFIX_KEY,
                CONTAINER_MAIN_LABEL_NAME_KEY,
                CONTAINER_APPEND_GROUP_ID_KEY,

                SERVICE_NAME_KEY,
                SERVICE_INSTANCE_COUNT_KEY,
                SERVICE_PORT_KEY,
                SERVICE_DEBUG_PORT_KEY,
                SERVICE_STARTUP_TIMEOUT_SECONDS_KEY,
                SERVICE_CONFIG_FILES_SYSTEM_PROPERTY_KEY,
                SERVICE_APPLICATION_YML_PATH_KEY,
                SERVICE_STARTUP_HEALTH_ENDPOINT_KEY,
                SERVICE_STARTUP_LOG_MESSAGE_KEY,
                SERVICE_IMAGE_TAG_KEY,
                SERVICE_CONTAINER_LOGGING_ENABLED_KEY,
                SERVICE_DEBUG_SUSPEND_KEY,
                SERVICE_APPLICATION_ARGS_KEY,
                SERVICE_ENVVARS_KEY,
                SERVICE_ADDITIONAL_FILESYSTEM_BINDS_KEY,

                ADDITIONAL_CONTAINERS_KEY,

                POSTGRES_ENABLED_KEY,
                POSTGRES_IMAGE_TAG_KEY,
                POSTGRES_HOST_NAME_KEY,
                POSTGRES_PORT_KEY,
                POSTGRES_DATABASE_NAME_KEY,
                POSTGRES_SCHEMA_NAME_KEY,
                POSTGRES_USERNAME_KEY,
                POSTGRES_PASSWORD_KEY,
                POSTGRES_SCHEMA_FILE_PATH_KEY,
                POSTGRES_CONTAINER_LOGGING_ENABLED_KEY,

                MONGODB_ENABLED_KEY,
                MONGODB_IMAGE_TAG_KEY,
                MONGODB_CONTAINER_LOGGING_ENABLED_KEY,

                MARIADB_ENABLED_KEY,
                MARIADB_IMAGE_TAG_KEY,
                MARIADB_HOST_NAME_KEY,
                MARIADB_PORT_KEY,
                MARIADB_DATABASE_NAME_KEY,
                MARIADB_USERNAME_KEY,
                MARIADB_PASSWORD_KEY,
                MARIADB_CONTAINER_LOGGING_ENABLED_KEY,

                KAFKA_ENABLED_KEY,
                KAFKA_NATIVE_ENABLED_KEY,
                KAFKA_BROKER_COUNT_KEY,
                KAFKA_CONFLUENT_IMAGE_TAG_KEY,
                KAFKA_APACHE_NATIVE_IMAGE_TAG_KEY,
                KAFKA_TOPICS_KEY,
                KAFKA_TOPIC_PARTITION_COUNT_KEY,
                KAFKA_CONTAINER_LOGGING_ENABLED_KEY,
                KAFKA_TOPIC_REPLICATION_FACTOR_KEY,
                KAFKA_MIN_INSYNC_REPLICAS_KEY,
                KAFKA_SASL_PLAIN_ENABLED_KEY,
                KAFKA_SASL_PLAIN_USERNAME_KEY,
                KAFKA_SASL_PLAIN_PASSWORD_KEY,

                KAFKA_SCHEMA_REGISTRY_ENABLED_KEY,
                KAFKA_SCHEMA_REGISTRY_CONFLUENT_IMAGE_TAG_KEY,
                KAFKA_SCHEMA_REGISTRY_PORT_KEY,
                KAFKA_SCHEMA_REGISTRY_CONTAINER_LOGGING_ENABLED_KEY,

                KAFKA_CONTROL_CENTER_ENABLED_KEY,
                KAFKA_CONTROL_CENTER_CONFLUENT_IMAGE_TAG_KEY,
                KAFKA_CONTROL_CENTER_EXPORT_METRICS_ENABLED_KEY,
                KAFKA_CONTROL_CENTER_JMX_PORT_KEY,
                KAFKA_CONTROL_CENTER_PORT_KEY,
                KAFKA_CONTROL_CENTER_CONTAINER_LOGGING_ENABLED_KEY,

                CONDUKTOR_ENABLED_KEY,
                CONDUKTOR_IMAGE_TAG_KEY,
                CONDUKTOR_LICENSE_KEY_KEY,
                CONDUKTOR_PORT_KEY,
                CONDUKTOR_CONTAINER_LOGGING_ENABLED_KEY,

                CONDUKTOR_GATEWAY_ENABLED_KEY,
                CONDUKTOR_GATEWAY_IMAGE_TAG_KEY,
                CONDUKTOR_GATEWAY_PROXY_PORT_KEY,
                CONDUKTOR_GATEWAY_HTTP_PORT_KEY,
                CONDUKTOR_GATEWAY_CONTAINER_LOGGING_ENABLED_KEY,

                DEBEZIUM_ENABLED_KEY,
                DEBEZIUM_IMAGE_TAG_KEY,
                DEBEZIUM_PORT_KEY,
                DEBEZIUM_CONTAINER_LOGGING_ENABLED_KEY,

                WIREMOCK_ENABLED_KEY,
                WIREMOCK_IMAGE_TAG_KEY,
                WIREMOCK_PORT_KEY,
                WIREMOCK_CONTAINER_LOGGING_ENABLED_KEY,
                WIREMOCK_OPTIONS_KEY,

                LOCALSTACK_ENABLED_KEY,
                LOCALSTACK_IMAGE_TAG_KEY,
                LOCALSTACK_PORT_KEY,
                LOCALSTACK_SERVICES_KEY,
                LOCALSTACK_CONTAINER_LOGGING_ENABLED_KEY,
                LOCALSTACK_INIT_FILE_PATH_KEY,

                ELASTICSEARCH_ENABLED_KEY,
                ELASTICSEARCH_IMAGE_TAG_KEY,
                ELASTICSEARCH_PASSWORD_KEY,
                ELASTICSEARCH_CLUSTER_NAME_KEY,
                ELASTICSEARCH_DISCOVERY_TYPE_KEY,
                ELASTICSEARCH_CONTAINER_LOGGING_ENABLED_KEY,

                AMBAR_ENABLED_KEY,
                AMBAR_IMAGE_TAG_KEY,
                AMBAR_CONTAINER_LOGGING_ENABLED_KEY,
                AMBAR_CONFIG_FILE_PATH_KEY
        ));
        PROPERTY_KEYS = Collections.unmodifiableSet(keys);
    }

    private ConfigurationKeys() {}
}
