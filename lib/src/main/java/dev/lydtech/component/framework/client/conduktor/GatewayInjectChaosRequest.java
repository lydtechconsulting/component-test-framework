package dev.lydtech.component.framework.client.conduktor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class GatewayInjectChaosRequest {

    private String name;
    private String pluginClass;
    private Integer priority;

    private Config config;

    @Builder
    @Data
    @AllArgsConstructor
    public static class Config {
        private Integer rateInPercent;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private ErrorMap errorMap;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer minLatencyMs;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer maxLatencyMs;
    }

    @Builder
    @Data
    @AllArgsConstructor
    public static class ErrorMap {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("FETCH")
        private String FETCH;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonProperty("PRODUCE")
        private String PRODUCE;
    }
}
