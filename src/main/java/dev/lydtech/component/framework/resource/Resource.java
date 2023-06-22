package dev.lydtech.component.framework.resource;

public enum Resource {

    SERVICE,
    KAFKA,
    POSTGRES,
    DEBEZIUM,
    KAFKA_SCHEMA_REGISTRY,
    KAFKA_CONTROL_CENTER,
    CONDUKTOR,
    CONDUKTOR_GATEWAY,
    WIREMOCK,
    LOCALSTACK;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
