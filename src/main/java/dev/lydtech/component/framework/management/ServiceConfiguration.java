package dev.lydtech.component.framework.management;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceConfiguration {
    private String name;
    private Boolean enabled;
    private Integer instanceCount;
    private String imageTag;
    private Integer port;
    private Integer debugPort;
    private Boolean debugSuspend;
    private Map<String, String> envvars;
    private Map<String, String> additionalFilesystemBinds;
    private String configFilesSystemProperty;
    private String applicationYmlPath;
    private String applicationArgs;
    private String startupHealthEndpoint;
    private String startupLogMessage;
    private Integer startupTimeoutSeconds;
    private Boolean containerLoggingEnabled;
}

