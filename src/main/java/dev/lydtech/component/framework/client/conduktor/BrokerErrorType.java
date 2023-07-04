package dev.lydtech.component.framework.client.conduktor;

public enum BrokerErrorType {

    REQUEST_TIMED_OUT,
    BROKER_NOT_AVAILABLE,
    OFFSET_OUT_OF_RANGE,
    NOT_ENOUGH_REPLICAS,
    INVALID_REQUIRED_ACKS // Not retryable
}
