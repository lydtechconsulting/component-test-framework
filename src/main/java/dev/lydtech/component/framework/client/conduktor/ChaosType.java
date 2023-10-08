package dev.lydtech.component.framework.client.conduktor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChaosType {

    BROKEN_BROKER("SimulateBrokenBrokersPlugin", "broken-broker"),
    LEADER_ELECTION("SimulateLeaderElectionsErrorsPlugin", "leader-election"),
    SLOW_BROKER("SimulateSlowBrokerPlugin", "slow-broker");

    private final String pluginClassName;
    private final String interceptorPath;
}
