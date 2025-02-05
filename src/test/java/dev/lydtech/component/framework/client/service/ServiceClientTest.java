package dev.lydtech.component.framework.client.service;

import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServiceClientTest {

    @BeforeEach
    void setUp() {
        System.setProperty("docker.host", "localhost");
        System.setProperty("service.mapped.port", "8080");
        ServiceClient.resetInstance();
    }

    @AfterEach
    void tearDown() {
        System.clearProperty("docker.host");
        System.clearProperty("service.mapped.port");
        ServiceClient.resetInstance();
    }

    @Test
    void testGetBaseUrl() {
        String expectedBaseUrl = "http://localhost:8080";
        String actualBaseUrl = ServiceClient.getInstance().getBaseUrl();

        assertThat(actualBaseUrl, equalTo(expectedBaseUrl));
    }

    @Test
    void testGetRequestSpecification() {
        RequestSpecification requestSpecification = ServiceClient.getInstance().getRequestSpecification();

        assertThat(requestSpecification, notNullValue());
    }

    @Test
    void testThrowsExceptionWhenServicePortNotSet() {
        System.clearProperty("service.mapped.port");

        Exception exception = assertThrows(RuntimeException.class, ServiceClient::getInstance);

        assertThat(exception.getMessage(), equalTo("service.mapped.port property not found"));
    }
}
