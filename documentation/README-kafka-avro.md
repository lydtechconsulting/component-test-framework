# Kafka Avro

The Kafka Avro Client enables the component test to send events to topics on the Kafka broker, and likewise consume events that are emitted by the service under test.  These are Avro events, which are events with a rich data structure that is defined by a schema.

In order to pull in the required Kafka Avro dependency, add the following to the project's Maven `pom.xml`, or equivalent to the Gradle `build.gradle`:
```
<repositories>
    <repository>
        <id>confluent</id>
        <url>https://packages.confluent.io/maven/</url>
    </repository>
</repositories>
```
This client defers to the Kafka Client for sending and receiving messages, but uses the KafkaAvroSerializer and KafkaAvroDeserializer for the consumer and producer.

Create a consumer to poll for Avro messages sent by the service under test:

```
import dev.lydtech.component.framework.client.kafka.KafkaAvroClient;

Consumer fooConsumer = KafkaAvroClient.createConsumer(FOO_TOPIC);
```

Send a message (the payload is the Avro type):
```
KafkaAvroClient.sendMessage(FOO_TOPIC, key, payload, headers);
```

Consume and assert Avro messages (in this case a FooCompleted record):
```
List<ConsumerRecord<String, FooCompleted>> outboundEvents = KafkaAvroClient.getInstance().consumeAndAssert("TestName", fooConsumer, EXPECTED_COUNT_RECEIVED, FURTHER_POLLS_TO_PERFORM);
```
