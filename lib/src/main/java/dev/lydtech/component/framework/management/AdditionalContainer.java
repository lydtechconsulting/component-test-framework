package dev.lydtech.component.framework.management;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalContainer {

    private String name;
    private Integer port;
    private Integer debugPort;
    private String imageTag;
    private Boolean additionalContainerLoggingEnabled;
}
