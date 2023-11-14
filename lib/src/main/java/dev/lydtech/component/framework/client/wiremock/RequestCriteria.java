package dev.lydtech.component.framework.client.wiremock;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestCriteria {

    private String method;

    // Only one of the following may be set.
    private String urlPathMatching;
    private String urlPattern;
    private String url;
    private String urlPath;
    private String urlPathPattern;
}
