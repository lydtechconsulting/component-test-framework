# Debezium

Debezium provides a Kafka Connect source connector, streaming events from the Database changelog to Kafka.

## Related properties
| Property                                        | Usage                                                                                                                                                                                                                                                                                                                                                                           | Default                            |
|-------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| debezium.enabled                                | Whether a Docker Debezium (Kafka Connect) container should be started.  Requires `kafka.enabled` and `postgres.enabled` to be `true`.                                                                                                                                                                                                                                           | `false`                            |
| debezium.image.tag                              | The image tag of the Debezium Docker container to use.                                                                                                                                                                                                                                                                                                                          | `2.4.0.Final`                      |
| debezium.port                                   | The port of the Debezium Docker container.                                                                                                                                                                                                                                                                                                                                      | `8083`                             |
| debezium.container.logging.enabled              | Whether to output the Debezium Docker logs to the console.                                                                                                                                                                                                                                                                                                                      | `false`                            |


A utility client is provides the ability to create the connector, and subsequently delete it.  The connector should be defined in a `json` file and passed to the client in the component test to create.  It can then be deleted at the end of the test using the connector name.

```
import dev.lydtech.component.framework.client.debezium.DebeziumClient;

DebeziumClient.getInstance().createConnector("connector/outbox-connector.json");

DebeziumClient.getInstance().deleteConnector("outbox-connector");
```

See the `ctf-example-service` project for example usage.
