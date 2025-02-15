package dev.lydtech.component.framework.configuration;

import lombok.extern.slf4j.Slf4j;

import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.*;

@Slf4j
public class ConfigurationLogger {

    public static void log() {
        log.info("Testcontainers Configuration:");

        log.info("containers.stayup: " + CONTAINERS_STAYUP);
        log.info("container.name.prefix: " + CONTAINER_NAME_PREFIX);
        log.info("container.main.label: " + CONTAINER_MAIN_LABEL_NAME);
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
