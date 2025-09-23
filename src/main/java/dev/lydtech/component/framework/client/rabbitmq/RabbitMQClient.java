package dev.lydtech.component.framework.client.rabbitmq;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class RabbitMQClient {
    protected String brokerUrl;
    private static RabbitMQClient instance;

    private RabbitMQClient(){
        String rabbitMQHost = Optional.ofNullable(System.getProperty("docker.host"))
                .orElse("localhost");
        String rabbitMQPort = Optional.ofNullable(System.getProperty("rabbitmq.mapped.port"))
                .orElseThrow(() -> new RuntimeException("rabbitmq.mapped.port property not found"));
        brokerUrl = "amqp://" + rabbitMQHost + ":" + rabbitMQPort;
        log.info("RabbitMQ broker URL is: " + brokerUrl);
    }

    public synchronized static RabbitMQClient getInstance() {
        if(instance==null) {
            instance = new RabbitMQClient();
        }
        return instance;
    }

    protected String getBrokerUrl() {
        return brokerUrl;
    }

    /**
     * Send a message to the given queue.
     */
    public void sendMessage(String queueName, String message) throws Exception {
        try (Connection connection = new ConnectionFactory() {{ setUri(getBrokerUrl()); }}.newConnection();
             Channel channel = connection.createChannel()) {
            channel.basicPublish("", queueName, null, message.getBytes(StandardCharsets.UTF_8));
            log.info("Sent message to queue '{}'", queueName);
        }
    }

    /**
     * Send a message to the given exchange with the given routing key.
     */
    public void sendMessage(String exchange, String routingKey, String message) throws Exception {
        try (Connection connection = new ConnectionFactory() {{ setUri(getBrokerUrl()); }}.newConnection();
             Channel channel = connection.createChannel()) {
            channel.basicPublish(exchange, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
            log.info("Sent message to exchange '{}' with routing key '{}'", exchange, routingKey);
        }
    }

    /**
     * Consume a single message from the given queue via a poll for the given duration.  Returns null if not found after the timeout.
     */
    public String consumeMessage(String queueName, Duration timeout) throws Exception {
        long deadline = System.currentTimeMillis() + timeout.toMillis();
        try (Connection connection = new ConnectionFactory() {{ setUri(getBrokerUrl()); }}.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(queueName, false, false, false, null);

            while (System.currentTimeMillis() < deadline) {
                GetResponse response = channel.basicGet(queueName, true);
                if (response != null) {
                    return new String(response.getBody(), StandardCharsets.UTF_8);
                }
                Thread.sleep(50);
            }
            return null;
        }
    }

    /**
     * Consume the messages within the given timeout, up to the expected number.
     */
    public List<String> consumeMessages(String queueName, int expectedCount, Duration timeout) throws Exception {
        List<String> messages = new ArrayList<>();
        long deadline = System.currentTimeMillis() + timeout.toMillis();

        try (Connection connection = new ConnectionFactory() {{ setUri(getBrokerUrl()); }}.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(queueName, false, false, false, null);

            while (messages.size() < expectedCount && System.currentTimeMillis() < deadline) {
                GetResponse response = channel.basicGet(queueName, true);
                if (response != null) {
                    messages.add(new String(response.getBody(), StandardCharsets.UTF_8));
                } else {
                    Thread.sleep(50);
                }
            }
        }

        return messages;
    }
}
