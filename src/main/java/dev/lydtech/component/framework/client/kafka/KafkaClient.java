package dev.lydtech.component.framework.client.kafka;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
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
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.awaitility.Awaitility;

@Slf4j
public final class KafkaClient {
    private static String brokerUrl;
    private static KafkaClient instance;

    private KafkaClient(){
        String kafkaHost = Optional.ofNullable(System.getProperty("kafka.host"))
                .orElse("localhost");
        String kafkaPort = Optional.ofNullable(System.getProperty("kafka.mapped.port"))
                .orElseThrow(() -> new RuntimeException("kafka.mapped.port property not found"));
        brokerUrl = "http://" + kafkaHost + ":" + kafkaPort;
        log.info("Kafka broker URL is: " + brokerUrl);
    }

    public synchronized static KafkaClient getInstance() {
        if(instance==null) {
            instance = new KafkaClient();
        }
        return instance;
    }

    public Consumer createConsumer(String groupId, String topic) {
        final Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId+"-"+topic);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        config.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, 1000);
        Consumer<Integer, String> consumer = new KafkaConsumer<>(config);
        consumer.subscribe(Collections.singletonList(topic));
        return consumer;
    }

    public Producer<Long, String> createProducer() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new KafkaProducer<>(config);
    }

    public void sendMessage(String topic, String key, String payload) throws Exception {
        sendMessage(topic, key, payload, null);
    }

    public void sendMessage(String topic, String key, String payload, Map<String, String> headers) throws Exception {
        final List<Header> recordHeaders = new ArrayList<>();
        if(headers!=null && headers.size()>0) {
            headers.forEach((headerKey, headerValue) -> recordHeaders.add(new RecordHeader(headerKey, headerValue != null ? headerValue.getBytes() : null)));
        }
        final ProducerRecord<Long, String> record = new ProducerRecord(topic, null, key, payload, recordHeaders);
        final RecordMetadata metadata = createProducer().send(record).get();
        log.debug(String.format("Sent record(key=%s value=%s) meta(topic=%s, partition=%d, offset=%d)",
                record.key(), record.value(), metadata.topic(), metadata.partition(), metadata.offset()));
    }

    /**
     * 1. Poll for messages on the application’s outbound topic.
     * 2. Assert the expected number are received.
     * 3. Performs the specified number of extra polls after the expected number received to ensure no further events.
     * 4. Returns the consumed events.
     */
    public List<ConsumerRecord<String, String>> consumeAndAssert(String testName, Consumer consumer, int expectedEventCount, int furtherPolls) throws Exception {
        AtomicInteger totalReceivedEvents = new AtomicInteger();
        AtomicInteger totalExtraPolls = new AtomicInteger(-1);
        AtomicInteger pollCount = new AtomicInteger();
        List<ConsumerRecord<String, String>> events = new ArrayList<>();

        Awaitility.await()
            .atMost(30, TimeUnit.SECONDS)
            .pollInterval(1, TimeUnit.SECONDS)
            .until(() -> {
                final ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(100));
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
