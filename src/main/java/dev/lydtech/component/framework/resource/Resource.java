package dev.lydtech.component.framework.resource;

public enum Resource {

    SERVICE,
    KAFKA,
    POSTGRES,
    DEBEZIUM,
    WIREMOCK,
    LOCALSTACK;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
