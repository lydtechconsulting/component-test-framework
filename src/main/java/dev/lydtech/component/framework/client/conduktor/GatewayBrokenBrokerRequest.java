package dev.lydtech.component.framework.client.conduktor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class GatewayBrokenBrokerRequest {

    private Config config;
    private String direction;
    private String apiKeys;

    @Builder
    @Data
    @AllArgsConstructor
    public static class Config {
        private Long duration;
        private String durationUnit;
        private Long quietPeriod;
        private String quietPeriodUnit;
        private Long minLatencyToAddInMilliseconds;
        private Long maxLatencyToAddInMilliseconds;
        private String[] errors;
    }
}
