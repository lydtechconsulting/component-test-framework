package dev.lydtech.component.framework.configuration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

class ConfigurationLoaderPropertiesFileTest {

    @BeforeEach
    public void setUp() {
        System.clearProperty("component.test.configuration.filename");
    }

    @AfterEach
    public void tearDown() {
        System.clearProperty("component.test.configuration.filename");
    }

    /**
     * Asserts the configration in the component-test.properties file in src/test/resources is loaded and overrides the defaults.
     *
     * service.name and kafka.enabled are overridden, service.port is not overridden.
     */
    @Test
    public void testLoadConfiguration_DefaultPropertiesFile() {
        try (MockedStatic<ConfigurationLogger> configurationLoggerMock = mockStatic(ConfigurationLogger.class)) {
            ConfigurationLoader.loadConfiguration();

            assertThat(TestcontainersConfiguration.SERVICES, notNullValue());
            assertThat(TestcontainersConfiguration.SERVICES.size(), equalTo(1));
            assertThat(TestcontainersConfiguration.SERVICES.get(0).getName(), equalTo("demo"));
            assertThat(TestcontainersConfiguration.SERVICES.get(0).getPort(), equalTo(8080));
            assertThat(TestcontainersConfiguration.KAFKA_ENABLED, equalTo(true));
            configurationLoggerMock.verify(ConfigurationLogger::log, times(1));
        }
    }

    /**
     * Asserts the configration in the component-test-custom.yaml file in src/test/resources is loaded and overrides the defaults.
     *
     * service.name and kafka.enabled are overridden, service.port is not overridden.
     */
    @Test
    public void testLoadConfiguration_CustomYamlFile() {
        System.setProperty("component.test.configuration.filename", "component-test-custom.yaml");
        try (MockedStatic<ConfigurationLogger> configurationLoggerMock = mockStatic(ConfigurationLogger.class)) {
            ConfigurationLoader.loadConfiguration();

            assertThat(TestcontainersConfiguration.SERVICES, notNullValue());
            assertThat(TestcontainersConfiguration.SERVICES.size(), equalTo(1));
            assertThat(TestcontainersConfiguration.SERVICES.get(0).getName(), equalTo("demo"));
            assertThat(TestcontainersConfiguration.SERVICES.get(0).getPort(), equalTo(8080));
            assertThat(TestcontainersConfiguration.KAFKA_ENABLED, equalTo(true));
            configurationLoggerMock.verify(ConfigurationLogger::log, times(1));
        }
    }

    /**
     * Asserts the configration in the component-test-custom.yml file in src/test/resources is loaded and overrides the defaults.
     *
     * service.name and kafka.enabled are overridden, service.port is not overridden.
     */
    @Test
    public void testLoadConfiguration_CustomYmlFile() {
        System.setProperty("component.test.configuration.filename", "component-test-custom.yml");
        try (MockedStatic<ConfigurationLogger> configurationLoggerMock = mockStatic(ConfigurationLogger.class)) {
            ConfigurationLoader.loadConfiguration();

            assertThat(TestcontainersConfiguration.SERVICES, notNullValue());
            assertThat(TestcontainersConfiguration.SERVICES.size(), equalTo(1));
            assertThat(TestcontainersConfiguration.SERVICES.get(0).getName(), equalTo("demo"));
            assertThat(TestcontainersConfiguration.SERVICES.get(0).getPort(), equalTo(8080));
            assertThat(TestcontainersConfiguration.KAFKA_ENABLED, equalTo(true));
            configurationLoggerMock.verify(ConfigurationLogger::log, times(1));
        }
    }

    /**
     * Asserts that a provided properties file contains an unknown property key that an exception is thrown.
     */
    @Test
    public void testLoadConfiguration_InvalidKey_ThrowsException() {
        System.setProperty("component.test.configuration.filename", "component-test-invalid-key.properties");
        try (MockedStatic<ConfigurationLogger> configurationLoggerMock = mockStatic(ConfigurationLogger.class)) {
            RuntimeException thrown = assertThrows(RuntimeException.class, ConfigurationLoader::loadConfiguration);

            assertThat(thrown.getMessage(), equalTo("Unexpected property in configuration: some.unknown.key"));
            configurationLoggerMock.verify(ConfigurationLogger::log, never());
        }
    }
}
