# RabbitMQ 

Enable the RabbitMQ messaging broker via the property `rabbitmq.enabled`.

## Related properties
| Property                           | Usage                                                      | Default                    |
|------------------------------------|------------------------------------------------------------|----------------------------|
| rabbitmq.enabled                   | Whether a Docker RabbitMQ container should be started.     | `false`                    |
| rabbitmq.image.tag                 | The image tag of the RabbitMQ Docker container to use.     | `3.7.25-management-alpine` |
| rabbitmq.username                  | The RabbitMQ username.                                     | `guest`                    |
| rabbitmq.password                  | The RabbitMQ password.                                     | `guest`                    |
| rabbitmq.container.logging.enabled | Whether to output the RabbitMQ Docker logs to the console. | `false`                    |

Override the main configuration in the application's `application-component-test.yml` file to connect to the Dockerised RabbitMQ, for example:

```
spring:
    rabbitmq:
        host: rabbitmq
        port: 5672
        username: user
        password: password
```

The connection port must be set to `5672`.  This is not configurable.

# RabbitMQ Client

The RabbitMQ Client enables the component test to send messages to RabbitMQ queues or to the exchange with a routing key, and likewise consume messages from queues that are emitted by the service under test.

For example, use the `RabbitMQClient` to send and receive messages as follows:
```
import dev.lydtech.component.framework.client.rabbitmq.RabbitMQClient;

// Send a message via a queue.
RabbitMQClient.getInstance().sendMessage("demo-inbound-queue", inboundMessage);

// Send a message via an exchange with routing key.
RabbitMQClient.getInstance().sendMessage("demo-exchange", "demo.inbound", inboundMessage);

// Consume a message from a queue (via polling).
String outboundMessage = RabbitMQClient.getInstance().consumeMessage("demo-outbound-queue", Duration.ofSeconds(5));
```
