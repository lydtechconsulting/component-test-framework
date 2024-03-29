package dev.lydtech.component.framework.client.kafka;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.security.plain.PlainLoginModule;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.awaitility.Awaitility;

import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.KAFKA_SASL_PLAIN_ENABLED;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.KAFKA_SASL_PLAIN_PASSWORD;
import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.KAFKA_SASL_PLAIN_USERNAME;

@Slf4j
public final class KafkaClient {
    protected String brokerUrl;
    private static KafkaClient instance;
    private KafkaProducer defaultProducer;

    private KafkaClient(){
        String kafkaHost = Optional.ofNullable(System.getProperty("docker.host"))
                .orElse("localhost");
        String kafkaPort = Optional.ofNullable(System.getProperty("kafka.mapped.port"))
                .orElseThrow(() -> new RuntimeException("kafka.mapped.port property not found"));
        brokerUrl = "http://" + kafkaHost + ":" + kafkaPort;
        log.info("Kafka broker URL is: " + brokerUrl);
        defaultProducer = createProducer();
    }

    public synchronized static KafkaClient getInstance() {
        if(instance==null) {
            instance = new KafkaClient();
        }
        return instance;
    }

    protected String getBrokerUrl() {
        return brokerUrl;
    }

    /**
     * Create a standard Consumer.
     */
    public Consumer createConsumer(String groupId, String topic) {
        return createConsumer(groupId, topic, null);
    }

    /**
     * Create a Consumer with additional config.
     */
    public Consumer createConsumer(String groupId, String topic, Properties additionalConfig) {
        final Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId+"-"+topic);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        config.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, 1000);

        if(KAFKA_SASL_PLAIN_ENABLED) {
            config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
            config.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
            config.put(SaslConfigs.SASL_JAAS_CONFIG, String.format(
                "%s required username=\"%s\" password=\"%s\";", PlainLoginModule.class.getName(), KAFKA_SASL_PLAIN_USERNAME, KAFKA_SASL_PLAIN_PASSWORD
            ));
        }

        if(additionalConfig!=null && !additionalConfig.isEmpty()) {
            config.putAll(additionalConfig);
        }
        Consumer consumer = new KafkaConsumer(config);
        consumer.subscribe(Collections.singletonList(topic));
        return consumer;
    }

    /**
     * Create a consumer and perform an initial long poll for the given number of seconds in order to clear the topic.
     */
    public Consumer initConsumer(String groupId, String topic, Long initialPollSeconds) {
        return initConsumer(groupId, topic, null, initialPollSeconds);
    }

    /**
     * Create a Consumer with additional config, and perform an initial long poll for the given number of seconds in order to
     * clear the topic.
     */
    public Consumer initConsumer(String groupId, String topic, Properties additionalConfig, Long initialPollSeconds) {
        Consumer consumer = createConsumer(groupId, topic, additionalConfig);
        consumer.poll(Duration.ofSeconds(initialPollSeconds));
        return consumer;
    }

    /**
     * Create a standard Producer.
     */
    public KafkaProducer<String, String> createProducer() {
        return createProducer(null);
    }

    /**
     * Create a Producer with additional config.
     */
    public KafkaProducer<String, String> createProducer(Properties additionalConfig) {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        if(KAFKA_SASL_PLAIN_ENABLED) {
            config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
            config.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
            config.put(SaslConfigs.SASL_JAAS_CONFIG, String.format(
                "%s required username=\"%s\" password=\"%s\";", PlainLoginModule.class.getName(), KAFKA_SASL_PLAIN_USERNAME, KAFKA_SASL_PLAIN_PASSWORD
            ));
        }

        if(additionalConfig!=null && !additionalConfig.isEmpty()) {
            config.putAll(additionalConfig);
        }
        return new KafkaProducer<>(config);
    }

    /**
     * Send a message synchronously without headers and with the default Producer.
     */
    public RecordMetadata sendMessage(String topic, String key, Object payload) throws Exception {
        return sendMessage(defaultProducer, topic, key, payload, null);
    }

    /**
     * Send a message synchronously without headers and with the provided Producer.
     */
    public RecordMetadata sendMessage(Producer producer, String topic, String key, Object payload) throws Exception {
        return sendMessage(producer, topic, key, payload, null);
    }

    /**
     * Send a message synchronously with the provided headers and with the default Producer.
     */
    public RecordMetadata sendMessage(String topic, String key, Object payload, Map<String, String> headers) throws Exception {
        return sendMessage(defaultProducer, topic, key, payload, headers);
    }

    /**
     * Send a message synchronously.  Awaits for the result of the send before returning.
     */
    public RecordMetadata sendMessage(Producer producer, String topic, String key, Object payload, Map<String, String> headers) throws Exception {
        final List<Header> recordHeaders = new ArrayList<>();
        if(headers!=null && headers.size()>0) {
            headers.forEach((headerKey, headerValue) -> recordHeaders.add(new RecordHeader(headerKey, headerValue != null ? headerValue.getBytes() : null)));
        }
        final ProducerRecord<Long, String> record = new ProducerRecord(topic, null, key, payload, recordHeaders);
        final RecordMetadata metadata = (RecordMetadata)producer.send(record).get();
        log.debug(String.format("Sent record(key=%s value=%s) meta(topic=%s, partition=%d, offset=%d)",
                record.key(), record.value(), metadata.topic(), metadata.partition(), metadata.offset()));
        return metadata;
    }

    /**
     * Send a message asynchronously without headers and with the default Producer.
     */
    public Future<RecordMetadata> sendMessageAsync(String topic, String key, Object payload) {
        return sendMessageAsync(defaultProducer, topic, key, payload, null);
    }

    /**
     * Send a message asynchronously without headers and with the provided Producer.
     */
    public Future<RecordMetadata> sendMessageAsync(Producer producer, String topic, String key, Object payload) {
        return sendMessageAsync(producer, topic, key, payload, null);
    }

    /**
     * Send a message asynchronously with the provided headers and with the default Producer.
     */
    public Future<RecordMetadata> sendMessageAsync(String topic, String key, Object payload, Map<String, String> headers) {
        return sendMessageAsync(defaultProducer, topic, key, payload, headers);
    }

    /**
     * Send a message asynchronously.  Allows for producing batches of messages, by tuning the producer linger.ms config.
     */
    public Future<RecordMetadata> sendMessageAsync(Producer producer, String topic, String key, Object payload, Map<String, String> headers) {
        final List<Header> recordHeaders = new ArrayList<>();
        if(headers!=null && headers.size()>0) {
            headers.forEach((headerKey, headerValue) -> recordHeaders.add(new RecordHeader(headerKey, headerValue != null ? headerValue.getBytes() : null)));
        }
        final ProducerRecord<Long, String> record = new ProducerRecord(topic, null, key, payload, recordHeaders);
        return producer.send(record);
    }

    public <T> List<ConsumerRecord<String, T>> consumeAndAssert(String testName, Consumer consumer, int expectedEventCount, int furtherPolls) throws Exception {
        int defaultAwaitAtMostSeconds = 60;
        return consumeAndAssert(testName, consumer, expectedEventCount, furtherPolls, defaultAwaitAtMostSeconds);
    }

    /**
     * 1. Poll for messages on the application’s outbound topic.
     * 2. Assert the expected number are received within the specified number of seconds.
     * 3. Performs the specified number of extra polls after the expected number received to ensure no further events.
     * 4. Returns the consumed events.
     */
    public <T> List<ConsumerRecord<String, T>> consumeAndAssert(String testName, Consumer consumer, int expectedEventCount, int furtherPolls, int awaitAtMostSeconds) throws Exception {
        AtomicInteger totalReceivedEvents = new AtomicInteger();
        AtomicInteger totalExtraPolls = new AtomicInteger(-1);
        AtomicInteger pollCount = new AtomicInteger();
        List<ConsumerRecord<String, T>> events = new ArrayList<>();

        Awaitility.await()
            .atMost(awaitAtMostSeconds, TimeUnit.SECONDS)
            .pollInterval(1, TimeUnit.SECONDS)
            .until(() -> {
                final ConsumerRecords<String, T> consumerRecords = consumer.poll(Duration.ofMillis(100));
                consumerRecords.forEach(record -> {
                    log.info(testName + " - received: " + record.value());
                    totalReceivedEvents.incrementAndGet();
                    events.add(record);
                });
                if(totalReceivedEvents.get() == expectedEventCount) {
                    // Track the extra polls, allowing for time to consume duplicates.
                    totalExtraPolls.incrementAndGet();
                }
                pollCount.getAndIncrement();
                log.info(testName + " - poll count: " + pollCount.get() + " - received count: " + totalReceivedEvents.get());
                return totalReceivedEvents.get() == expectedEventCount && totalExtraPolls.get() == furtherPolls;
            });
        return events;
    }
}
