# Kafka
## Kafka Configuration

The Kafka messaging broker is enabled by either setting `kafka.enabled` to `true`, to start a standard (Confluent) Kafka broker, or by setting `kafka.native.enabled` to `true`, to start a native (Apache) Kafka broker.  But flags cannot be enabled at the same time.

## Related properties
| Property                                        | Usage                                                                                                                                                                                                                                                                                                                                                                           | Default                            |
|-------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| kafka.enabled                                   | Whether a Docker Kafka container (standard build) should be started.  Cannot be `true` if `kafka.native.enabled` is `true`.  Set the application's Kafka `bootstrap-servers` to `kafka:9092` in the `application-component-test.yml` to connect.                                                                                                                                | `false`                            |
| kafka.native.enabled                            | Whether a Docker Kafka container (native build) should be started.  Cannot be `true` if `kafka.enabled` is `true`.  Set the application's Kafka `bootstrap-servers` to `kafka:9093` in the `application-component-test.yml` to connect.                                                                                                                                         | `false`                            |
| kafka.broker.count                              | The number of Kafka broker nodes in the cluster.  Each broker node will start in its own Docker container.  The first instance will be `kafka`, then subsequent will have an instance suffix, e.g. `kafka-2`.  If multiple instances are started a Zookeeper Docker container is also started (rather than using the embedded Zookeeper).                                       | `1`                                |
| kafka.confluent.image.tag                       | The image tag of the Confluent Kafka Docker container to use.                                                                                                                                                                                                                                                                                                                   | `7.3.2`                            |
| kafka.topics                                    | Comma delimited list of topics to create.  Often topics are auto-created, but for Kafka Streams for example they must be created upfront.                                                                                                                                                                                                                                       |
| kafka.topic.partition.count                     | The number of partitions for topics that are created.                                                                                                                                                                                                                                                                                                                           | `5`                                |
| kafka.topic.replication.factor                  | The replication factor to use for topics.  Must not be greater than the configured `kafka.broker.count`.                                                                                                                                                                                                                                                                        | `1`                                |
| kafka.min.insync.replicas                       | The minimum in-sync number of replicas required for successful writes to topics.  Must not be greater than the configured `kafka.broker.count` nor the `kafka.topic.replication.factor`.                                                                                                                                                                                        | `1`                                |
| kafka.sasl.plain.enabled                        | Whether Kafka SASL PLAIN is enabled.                                                                                                                                                                                                                                                                                                                                            | `false`                            |
| kafka.sasl.plain.username                       | The Kafka SASL PLAIN username.  Must be set if `kafka.sasl.plain.enabled` is `true`.                                                                                                                                                                                                                                                                                            | `demo`                             |
| kafka.sasl.plain.password                       | The Kafka SASL PLAIN password.  Must be set if `kafka.sasl.plain.enabled` is `true`.                                                                                                                                                                                                                                                                                            | `demo-password`                    |
| kafka.container.logging.enabled                 | Whether to output the Kafka Docker logs to the console.                                                                                                                                                                                                                                                                                                                         | `false`                            |


When running a standard broker, set the application's Kafka `bootstrap-servers` to `kafka:9092` in the `application-component-test.yml` to connect.

When running a native broker, Set the application's Kafka `bootstrap-servers` to `kafka:9093` in the `application-component-test.yml` to connect.  The native build boasts very fast start up time, which will aid overall test time.

Both flavours of the broker support integrating with Schema Registry, Debezium, Conduktor Platform, Conduktor Gateway, and Confluent Control Center.  No additional configuration is required to those resources for integration, they work seamlessly whether the standard or native Kafka broker are enabled.

A configurable number of broker and topic configurations can be applied.  These include setting the number of broker nodes in the cluster (`kafka.broker.count`), the topic replication factor (`kafka.topic.replication.factor`), and the minimum number of brokers that must be in-sync to accept a producer write (`kafka.min.insync.replicas`).  Any topics that should be created upfront can be declared in a comma separated list (`kafka.topics`), and the default topic partition count can be configured (`kafka.topic.partition.count`).

For choosing a value for the `kafka.confluent.image.tag` property, the Confluent Platform and Apache Kafka Compatibility matrix is available here:
https://docs.confluent.io/platform/current/installation/versions-interoperability.html

## Kafka Client

The Kafka Client enables the component test to send events to topics on the Kafka broker, and likewise consume events that are emitted by the service under test.  These are JSON events.

Create a consumer to poll for messages sent by the service under test:
```
import dev.lydtech.component.framework.client.kafka.KafkaClient;

Consumer fooConsumer = KafkaClient.createConsumer(GROUP_ID, FOO_TOPIC);
```

The Consumer can be configured for the test by passing in a Properties map of Consumer Config values to the createConsumer() method.
```
Properties additionalConfig = new Properties();
additionalConfig.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, "io.confluent.monitoring.clients.interceptor.MonitoringConsumerInterceptor");
Consumer fooConsumer = KafkaClient.createConsumer(GROUP_ID, FOO_TOPIC);
```

Alternatively use one of the overloaded `initConsumer` methods, which take an extra argument, `initialPollSeconds`.  In addition to creating the consumer it performs an initial long poll up to the given number of seconds.  This is useful in order to clear the topic of any messages that it would otherwise consume during the test.

```
Consumer fooConsumer = KafkaClient.initConsumer(GROUP_ID, FOO_TOPIC, 3);
Consumer fooConsumer = KafkaClient.intConsumer(GROUP_ID, FOO_TOPIC, additionalConfig, 3);
```

Send a message synchronously:
```
KafkaClient.sendMessage(FOO_TOPIC, key, payload, headers);
```

Send a message asynchronously:
```
KafkaClient.sendMessageAsync(FOO_TOPIC, key, payload, headers);
```

These methods use a default Producer.  The Producer to use can optionally be passed in to both methods as the first arg.  The Producer can be configured for the test by passing in a Properties map of Producer Config values to the createProducer() method.
```
public KafkaProducer<Long, String> createProducer(Properties additionalConfig) {
```

For example, to set a `linger.ms` value, define the following in the component test:
```
Properties additionalConfig = new Properties();
additionalConfig.put(ProducerConfig.LINGER_MS_CONFIG, 100);
additionalConfig.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, "io.confluent.monitoring.clients.interceptor.MonitoringProducerInterceptor");
KafkaProducer producer = KafkaClient.getInstance().createProducer(additionalConfig);
```

This can then be coupled with the `sendMessageAsync(..)` call to ensure several messages are sent as a batch.

Consume and assert a message:
```
KafkaClient.consumeAndAssert("TestName", fooConsumer, EXPECTED_COUNT_RECEIVED, FURTHER_POLLS_TO_PERFORM, AWAIT_AT_MOST_SECONDS);
```

## Kafka SASL PLAIN

The framework supports enforcing Simple Authentication and Security Layer (SASL) PLAIN.  To enable, set `kafka.sasl.plain.enabled` to `true`.  Supply the `kafka.sasl.plain.username` and `kafka.sasl.plain.password` required to connect to Kafka.

## Kafka JSON Marshalling

A JSON mapping utility is provided to allow marshalling of PoJOs to/from JSON Strings.  This is a convenient feature for preparing event payloads to be sent in the JSON format to Kafka and likewise converted to their PoJO version when consumed.
```
import dev.lydtech.component.framework.mapper.JsonMapper

public static <T> T readFromJson(String json, Class<T> clazz) throws MappingException

public static String writeToJson(Object obj) throws MappingException
```
