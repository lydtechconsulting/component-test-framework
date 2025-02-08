package dev.lydtech.component.framework.client.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AdditionalContainerClientTest {

    @BeforeEach
    void setUp() {
        System.setProperty("docker.host", "localhost");
        System.setProperty("additional.container.ct-external-service-simulator.mapped.port", "55600");
        System.setProperty("additional.container.ct-third-party-simulator.mapped.port", "55594");
        System.setProperty("additional.container.ct-some-other-service.mapped.port", "55777");
        AdditionalContainerClient.getInstance();
    }

    @AfterEach
    void tearDown() {
        System.clearProperty("docker.host");
        System.clearProperty("additional.container.ct-external-service-simulator.mapped.port");
        System.clearProperty("additional.container.ct-third-party-simulator.mapped.port");
        System.clearProperty("additional.container.ct-some-other-service.mapped.port");
    }

    @Test
    void testExactMatchBaseUrl() {
        String baseUrl = AdditionalContainerClient.getInstance().getBaseUrl("ct-third-party-simulator");
        assertThat(baseUrl, is(equalTo("http://localhost:55594")));
    }

    @Test
    void testPartialMatchBaseUrl() {
        String baseUrl = AdditionalContainerClient.getInstance().getBaseUrl("third-party-simulator");
        assertThat(baseUrl, is(equalTo("http://localhost:55594")));
    }

    @Test
    void testMultipleMatchesBaseUrlThrowsException() {
        Exception exception = assertThrows(RuntimeException.class,
                () -> AdditionalContainerClient.getInstance().getBaseUrl("simulator"));

        assertThat(exception.getMessage(), containsString("Multiple base URLs found for container matching"));
    }

    @Test
    void testNoMatchBaseUrlThrowsException() {
        Exception exception = assertThrows(RuntimeException.class,
                () -> AdditionalContainerClient.getInstance().getBaseUrl("non-existent-service"));

        assertThat(exception.getMessage(), is(equalTo("No base URL found for container matching: non-existent-service")));
    }

    @Test
    void testExactMatchRequestSpecification() {
        assertThat(AdditionalContainerClient.getInstance().getRequestSpecification("ct-third-party-simulator"), is(notNullValue()));
    }

    @Test
    void testPartialMatchRequestSpecification() {
        assertThat(AdditionalContainerClient.getInstance().getRequestSpecification("third-party-simulator"), is(notNullValue()));
    }

    @Test
    void testMultipleMatchesRequestSpecificationThrowsException() {
        Exception exception = assertThrows(RuntimeException.class,
                () -> AdditionalContainerClient.getInstance().getRequestSpecification("simulator"));

        assertThat(exception.getMessage(), containsString("Multiple request specifications found for container matching"));
    }

    @Test
    void testNoMatchRequestSpecificationThrowsException() {
        Exception exception = assertThrows(RuntimeException.class,
                () -> AdditionalContainerClient.getInstance().getRequestSpecification("non-existent-service"));

        assertThat(exception.getMessage(), is(equalTo("No request specification found for container matching: non-existent-service")));
    }
}
