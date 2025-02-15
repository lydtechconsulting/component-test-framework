package dev.lydtech.component.framework.configuration;

import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

import static dev.lydtech.component.framework.configuration.FileUtils.loadConfigurationFile;

@Slf4j
public final class ConfigurationLoader {

    private static final String DEFAULT_PROPERTIES_FILE = "component-test.properties";
    private static final String DEFAULT_YAML_FILE = "component-test.yaml";
    private static final String DEFAULT_YML_FILE = "component-test.yml";
    private static final String COMPONENT_TEST_CONFIGURATION_OVERRIDE = "component.test.configuration.filename";


    /**
     * Loads the configuration from a properties file or YAML file, or falls back to use system properties.
     * Updates the static configuration fields in TestcontainersConfiguration.
     */
    public static void loadConfiguration() {

        // 1. Try to load a file specified by the system property.
        boolean fileLoaded = false;
        Properties overrideProperties = loadUserSpecifiedConfigurationFile();

        // 2. If none was specified or loaded, try the defaults.
        if (overrideProperties == null) {
            overrideProperties = loadDefaultConfigurationFile();
        }

        // 3. If any file was loaded, validate its keys.
        if (overrideProperties != null) {
            validateKeys(overrideProperties);
            fileLoaded = true;
            log.info("Loaded configuration from file.");
        } else {
            log.info("No configuration file loaded. Using system properties for allowed keys.");
            // 4. Use system properties if no file was loaded.
            overrideProperties = new Properties();
            for (String key : ConfigurationKeys.PROPERTY_KEYS) {
                String systemProperty = System.getProperty(key);
                if (systemProperty != null) {
                    overrideProperties.setProperty(key, systemProperty);
                }
            }
        }

        if (fileLoaded) {
            log.info("Configuration file loaded.  System properties will not override file values.");
        }

        // 5. Update the default static configuration fields with any overrides.
        TestcontainersConfiguration.configure(overrideProperties);

        // 6. Log the configuration.
        ConfigurationLogger.log();
    }

    /**
     * Helper: Loads a configuration file specified by the system property.
     * Throws a RuntimeException if the property is set but the file is missing or the extension is invalid.
     */
    private static Properties loadUserSpecifiedConfigurationFile() {
        String configurationFile = System.getProperty(COMPONENT_TEST_CONFIGURATION_OVERRIDE);
        if (configurationFile != null && !configurationFile.trim().isEmpty()) {
            // Check extension
            if (!(configurationFile.endsWith(".properties") || configurationFile.endsWith(".yml") || configurationFile.endsWith(".yaml"))) {
                throw new RuntimeException("Invalid file extension in system property " + COMPONENT_TEST_CONFIGURATION_OVERRIDE + ": " + configurationFile);
            }
            Properties properties = loadConfigurationFile(configurationFile);
            if (properties == null) {
                throw new RuntimeException("Specified configuration file '" + configurationFile + "' not found.");
            }
            log.info("Loaded configuration from system specified file: {}", configurationFile);
            return properties;
        }
        return null;
    }

    /**
     * Attempts to load the default configuration file.
     * First tries the default properties file, then the YAML files.
     */
    private static Properties loadDefaultConfigurationFile() {
        Properties properties = loadConfigurationFile(DEFAULT_PROPERTIES_FILE);
        if (properties != null) {
            log.info("Loaded properties from default file: {}", DEFAULT_PROPERTIES_FILE);
            return properties;
        }
        properties = loadConfigurationFile(DEFAULT_YAML_FILE);
        if (properties != null) {
            log.info("Loaded properties from default file: {}", DEFAULT_YAML_FILE);
            return properties;
        }
        properties = loadConfigurationFile(DEFAULT_YML_FILE);
        if (properties != null) {
            log.info("Loaded properties from default file: {}", DEFAULT_YML_FILE);
            return properties;
        }
        log.info("No default configuration file found.");
        return null;
    }

    /**
     * Validates that every key in the provided Properties exists in the allowed key set.
     */
    private static void validateKeys(Properties props) {
        for (String key : props.stringPropertyNames()) {
            if (!ConfigurationKeys.PROPERTY_KEYS.contains(key)) {
                throw new RuntimeException("Unexpected property in configuration: " + key);
            }
        }
    }
}
