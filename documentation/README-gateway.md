# Conduktor Gateway

Conduktor Gateway is a proxy that sits between an application and Kafka that facilitates chaos testing.  It achieves this by intercepting requests made by the application to Kafka and returning a percentage of these with errors, based on registering interceptors with the Gateway.

See https://www.conduktor.io/gateway/ for more.

## Related properties
| Property                                        | Usage                                                                                                                                                                                                                                                                                                                                                                           | Default                            |
|-------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| conduktor.gateway.enabled                       | Whether a Docker Conduktor Gateway container should be started.                                                                                                                                                                                                                                                                                                                 | `false`                            |
| conduktor.gateway.image.tag                     | The image tag of the Conduktor Platform Docker container to use.                                                                                                                                                                                                                                                                                                                | `2.1.5`                            |
| conduktor.gateway.proxy.port                    | The exposed port of the Conduktor Gateway container.  This port must be available locally.  The port is used to connect to the proxy rather than the Kafka instance directly.  e.g. `bootstrap-servers: conduktorgateway:6969`                                                                                                                                                  | `6969`                             |
| conduktor.gateway.http.port                     | The exposed port of the Conduktor Gateway container HTTP management API.  This port must be available locally.  The port is used to connect to the proxy rather than the Kafka instance directly.  e.g. `bootstrap-servers: conduktorgateway:6969`                                                                                                                              | `8888`                             |
| conduktor.gateway.container.logging.enabled     | Whether to output the Conduktor Gateway Docker logs to the console.                                                                                                                                                                                                                                                                                                             | `false`                            |


A utility client provides the ability to simulate a number of different errors.  For example, to simulate `INVALID_REQUIRED_ACKS` for 20% of producer requests:

```
import dev.lydtech.component.framework.client.conduktor.ConduktorGatewayClient;

conduktorGatewayClient.simulateBrokenBroker(20, BrokenBrokerErrorType.INVALID_REQUIRED_ACKS);
```
The following are the supported `BrokerBrokerErrorType` types for simulating a broken broker by the component test framework, and whether each results in a Kafka retryable exception that the producer should be able to retry:

| Error Type | Exception Thrown             | Exception Type |
|------------|------------------------------|----------------|
| NOT_ENOUGH_REPLICAS | NotEnoughReplicasException   | Retryable      |
| CORRUPT_MESSAGE | CorruptRecordException | Retryable      |
| INVALID_REQUIRED_ACKS | InvalidRequiredAcksException | Not retryable  |
| UNKNOWN_SERVER_ERROR | UnknownServerException | Not retryable  |

To simulate a partition leader election at the time the request is made for 20% of requests (resulting in the Kafka retryable exception `NotLeaderOrFollowerException`):

```
conduktorGatewayClient.simulateLeaderElection(20);
```

To simulate a slow broker, for 100% of requests, adding a latency of between 50 milliseconds and 150 milliseconds for each request:
```
conduktorGatewayClient.simulateSlowBroker(100, 50, 150)
```

To clear existing interceptors, call:

```
conduktorGatewayClient.reset();
```

When no interceptors are registered, the Gateway will pass requests through directly to Kafka.
