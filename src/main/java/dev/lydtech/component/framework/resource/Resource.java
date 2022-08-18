package dev.lydtech.component.framework.resource;

public enum Resource {

    SERVICE,
    KAFKA,
    POSTGRES,
    DEBEZIUM,
    KAFKA_SCHEMA_REGISTRY,
    WIREMOCK,
    LOCALSTACK;

    @Override
    public String toString() {
        return super.toString().replace("_", "").toLowerCase();
    }
}
