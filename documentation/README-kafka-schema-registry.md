# Kafka Schema Registry

## Related properties
| Property                                        | Usage                                                                                                                                                                                                                                                                                                                                                                           | Default                            |
|-------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| kafka.schema.registry.enabled                   | Whether a Docker Schema Registry container should be started.                                                                                                                                                                                                                                                                                                                   | `false`                            |
| kafka.schema.registry.confluent.image.tag       | The image tag of the Kafka Confluent Schema Registry Docker container to use.  Recommendation is to keep this the same as `kafka.confluent.image.tag`.                                                                                                                                                                                                                          | `7.3.2`                            |
| kafka.schema.registry.port                      | The port of the Kafka Schema Registry Docker container.                                                                                                                                                                                                                                                                                                                         | `8081`                             |
| kafka.schema.registry.container.logging.enabled | Whether to output the Kafka Schema Registry Docker logs to the console.                                                                                                                                                                                                                                                                                                         | `false`                            |

The Kafka Confluent Schema Registry is a registry for holding the schemas of the messages sent to Kafka topics.

The provided Kafka Schema Registry client enables the component test to interact with the Dockerised Confluent Schema Registry container in order to register the necessary schemas.

At runtime the Schema Registry is hit by the Kafka Avro serializer and deserializer in the Kafka Producers and Consumers in order for them to retrieve the required Avro schema for the given payload.

To this end the client provides a `registerSchema` method which takes the subject (which is the topic name) and the Avro schema associated with the messages on this topic.

```
import dev.lydtech.component.framework.client.kafka.KafkaSchemaRegistryClient;

KafkaSchemaRegistryClient.getInstance().registerSchema(topicName, FooCompleted.getClassSchema().toString());
```

The `getClassSchema()` method is a method generated on the Apache Avro generated class, and returns the schema String that is required for registering with the Schema Registry.

The KafkaSchemaRegistryClient also provides a reset schema registry method to allow the component test to clear and reset these schema mappings.

```
KafkaSchemaRegistryClient.getInstance().resetSchemaRegistry();
```

A recommended pattern is to call both the reset and the register methods in the test `@BeforeAll`.
