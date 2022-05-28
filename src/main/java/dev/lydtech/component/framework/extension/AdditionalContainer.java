package dev.lydtech.component.framework.extension;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdditionalContainer {

    private String name;
    private Integer port;
    private Integer debugPort;
    private String imageTag;
    private Boolean additionalContainerLoggingEnabled;
}
