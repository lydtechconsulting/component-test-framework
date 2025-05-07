# Wiremock

## Related properties
| Property                                        | Usage                                                                                                                                                                                                                                                                                                                                                                           | Default                            |
|-------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| wiremock.enabled                                | Whether a Docker Wiremock container should be started.                                                                                                                                                                                                                                                                                                                          | `false`                            |
| wiremock.image.tag                              | The image tag of the Wiremock Docker container to use.                                                                                                                                                                                                                                                                                                                          | `3.6.0`                            |
| wiremock.container.logging.enabled              | Whether to output the Wiremock Docker logs to the console.                                                                                                                                                                                                                                                                                                                      | `false`                            |
| wiremock.options                                | Optional CLI arguments to be passed to Wiremock.                                                                                                                                                                                                                                                                                                                                |                                    |


The Wiremock container requires a `health.json` file to be provided in the `src/test/resources/wiremock/` directory with the following contents:
```
{
  "request": {
    "method": "GET",
    "url": "/health"
  },
  "response": {
    "status": 204
  }
}
```

This is used by the component-test-framework to determine whether the Wiremock container has successfully started.

All other mapping files placed in this same directory will also be loaded.

In a multi module project the `src/test/resources/wiremock/` directory lives in the `component-test` module.

The Wiremock client provides various methods for querying the admin API.  The admin API it hooks into is available here:
https://wiremock.org/docs/api

An example:
```
import dev.lydtech.component.framework.client.wiremock.WiremockClient;

RequestCriteria request = RequestCriteria.builder()
        .method("GET")
        .url("/api/thirdparty/"+key)
        .build();
Response response = WiremockClient.getInstance().findMatchingRequests(request);
```

Other mapping files can be loaded by a component test with the following call:
```
WiremockClient.getInstance().postMappingFile("thirdParty/retry_behaviour_success.json");
```
This requires the corresponding mapping file to be located under `src/test/resources/`.  e.g. in this case:
```
src/test/resources/thirdParty/retry_behaviour_success.json
```

Command Line arguments can be passed to Wiremock using the `wiremock.options` property, for example the following will enable global response templating:
```
<properties>
    <wiremock.options>--global-response-templating</containers.stayup>
</properties>
```
