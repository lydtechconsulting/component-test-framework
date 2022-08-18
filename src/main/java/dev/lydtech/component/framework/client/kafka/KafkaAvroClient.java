package dev.lydtech.component.framework.client.kafka;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

@Slf4j
public class KafkaAvroClient {
    private static KafkaAvroClient instance;
    private KafkaProducer avroProducer;

    private KafkaAvroClient(){
        avroProducer = createProducer();
    }

    public synchronized static KafkaAvroClient getInstance() {
        if(instance==null) {
            instance = new KafkaAvroClient();
        }
        return instance;
    }

    /**
     * Create a standard Avro Producer.
     */
    public KafkaProducer<Long, String> createProducer() {
        return createProducer(null);
    }

    public Consumer createConsumer(String groupId, String topic) {
        final Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaClient.getInstance().getBrokerUrl());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId+"-"+topic);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        config.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, SchemaRegistryClient.getInstance().getSchemaRegistryBaseUrl());
        config.put(KafkaAvroDeserializerConfig.AUTO_REGISTER_SCHEMAS, false);
        config.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        config.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, 1000);
        Consumer<Integer, String> consumer = new KafkaConsumer<>(config);
        consumer.subscribe(Collections.singletonList(topic));
        return consumer;
    }

    /**
     * Defers to the method in KafkaClient.
     */
    public <T> List<ConsumerRecord<String, T>> consumeAndAssert(String testName, Consumer consumer, int expectedEventCount, int furtherPolls) throws Exception {
        return KafkaClient.getInstance().consumeAndAssert(testName, consumer, expectedEventCount, furtherPolls);
    }

    /**
     * Create a Producer with additional config.
     */
    public KafkaProducer<Long, String> createProducer(Properties additionalConfig) {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaClient.getInstance().getBrokerUrl());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        config.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, SchemaRegistryClient.getInstance().getSchemaRegistryBaseUrl());
        config.put(KafkaAvroSerializerConfig.AUTO_REGISTER_SCHEMAS, false);
        if(additionalConfig!=null && !additionalConfig.isEmpty()) {
            config.putAll(additionalConfig);
        }
        return new KafkaProducer<>(config);
    }

    /**
     * Send a message synchronously without headers and with the default Avro Producer.
     */
    public RecordMetadata sendMessage(String topic, String key, Object payload) throws Exception {
        return KafkaClient.getInstance().sendMessage(avroProducer, topic, key, payload, null);
    }

    /**
     * Send a message synchronously with the provided headers and with the default Avro Producer.
     */
    public RecordMetadata sendMessage(String topic, String key, Object payload, Map<String, String> headers) throws Exception {
        return KafkaClient.getInstance().sendMessage(avroProducer, topic, key, payload, headers);
    }

    /**
     * Send a message asynchronously without headers and with the default Avro Producer.
     */
    public Future<RecordMetadata> sendMessageAsync(String topic, String key, Object payload) {
        return KafkaClient.getInstance().sendMessageAsync(avroProducer, topic, key, payload, null);
    }

    /**
     * Send a message asynchronously with the provided headers and with the default Avro Producer.
     */
    public Future<RecordMetadata> sendMessageAsync(String topic, String key, Object payload, Map<String, String> headers) {
        return KafkaClient.getInstance().sendMessageAsync(avroProducer, topic, key, payload, headers);
    }
}
