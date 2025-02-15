package dev.lydtech.component.framework.configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import dev.lydtech.component.framework.management.AdditionalContainer;
import org.junit.jupiter.api.Test;

import static dev.lydtech.component.framework.configuration.ConfigurationParser.parseAdditionalContainers;
import static dev.lydtech.component.framework.configuration.ConfigurationParser.parseKafkaTopics;
import static dev.lydtech.component.framework.configuration.ConfigurationParser.parseKvPairs;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigurationParserTest {

    @Test
    public void testParseKafkaTopics() {
        assertThat(parseKafkaTopics("topic-a,topic-b,topic-c"), equalTo(Arrays.asList("topic-a", "topic-b", "topic-c")));
    }

    @Test
    public void testParseAdditionalContainers() {
        List<AdditionalContainer> additionalContainers = parseAdditionalContainers("third-party-simulator,9002,5002,latest,false:external-service-simulator,9003,5003,6.5.4,true");
        assertThat(additionalContainers.size(), equalTo(2));
        assertThat(additionalContainers.get(0).getName(), equalTo("third-party-simulator"));
        assertThat(additionalContainers.get(0).getPort(), equalTo(9002));
        assertThat(additionalContainers.get(0).getDebugPort(), equalTo(5002));
        assertThat(additionalContainers.get(0).getImageTag(), equalTo("latest"));
        assertThat(additionalContainers.get(0).getAdditionalContainerLoggingEnabled(), equalTo(false));
        assertThat(additionalContainers.get(1).getName(), equalTo("external-service-simulator"));
        assertThat(additionalContainers.get(1).getPort(), equalTo(9003));
        assertThat(additionalContainers.get(1).getDebugPort(), equalTo(5003));
        assertThat(additionalContainers.get(1).getImageTag(), equalTo("6.5.4"));
        assertThat(additionalContainers.get(1).getAdditionalContainerLoggingEnabled(), equalTo(true));
    }

    @Test
    public void testParseAdditionalContainers_Invalid() {
        Exception exception = assertThrows(RuntimeException.class, () -> parseAdditionalContainers("third-party-simulator,9002"));
        assertThat(exception.getMessage(), equalTo("Invalid additional containers details: [third-party-simulator, 9002] -  expecting 5 args, found 2."));
    }

    @Test
    public void testParseEnvVars_Invalid() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> parseKvPairs("invalid"));
        assertThat(exception.getMessage(), equalTo("invalid key/value pair string for service property"));
    }

    @Test
    public void testParseEnvVars() {
        Map<String, String> envVarsMap = parseKvPairs("firstKey=firstVal,    secondKey   =    secondVal");
        assertThat(envVarsMap.size(), equalTo(2));
        assertThat(envVarsMap.get("firstKey"), equalTo("firstVal"));
        assertThat(envVarsMap.get("secondKey"), equalTo("secondVal"));
    }
}
