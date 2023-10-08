package dev.lydtech.component.framework.client.conduktor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BrokenBrokerErrorType {

    NOT_ENOUGH_REPLICAS("PRODUCE"), // Retryable
    INVALID_REQUIRED_ACKS("PRODUCE"), // Not retryable
    CORRUPT_MESSAGE("PRODUCE"), // Retryable
    UNKNOWN_SERVER_ERROR("FETCH"); // Not retryable

    private final String errorMapType;
}
