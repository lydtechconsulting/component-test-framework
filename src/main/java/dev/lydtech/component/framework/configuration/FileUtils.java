package dev.lydtech.component.framework.configuration;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

@Slf4j
public class FileUtils {

    /**
     * Loads a configuration file by name.
     * For .properties files, it uses Properties.load().
     * For YAML files, it flattens the YAML map into Properties.
     */
    protected static Properties loadConfigurationFile(String fileName) {
        try (InputStream stream = ConfigurationLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (stream != null) {
                Properties properties = new Properties();
                if (fileName.endsWith(".properties")) {
                    properties.load(stream);
                } else {
                    // Assume YAML file (.yml or .yaml)
                    Yaml yaml = new Yaml();
                    Map<String, Object> yamlConfig = yaml.load(stream);
                    properties = flattenYaml(yamlConfig, "");
                }
                return properties;
            }
        } catch (Exception e) {
            log.error("Error loading configuration file: {}", fileName, e);
            throw new RuntimeException("Failed to load configuration file: " + fileName, e);
        }
        return null;
    }

    /**
     * Converts a YAML Map into a Properties object.
     */
    private static Properties flattenYaml(Map<String, Object> yamlMap, String parentKey) {
        Properties properties = new Properties();
        for (Map.Entry<String, Object> entry : yamlMap.entrySet()) {
            String key = parentKey.isEmpty() ? entry.getKey() : parentKey + "." + entry.getKey();
            if (entry.getValue() instanceof Map) {
                properties.putAll(flattenYaml((Map<String, Object>) entry.getValue(), key));
            } else {
                properties.setProperty(key, entry.getValue() != null ? entry.getValue().toString() : "");
            }
        }
        return properties;
    }
}
