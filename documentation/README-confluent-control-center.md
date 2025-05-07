# Confluent Control Center

The Confluent Control Center is a web application that provides a user interface for inspecting the Kafka broker and topics.  Messages on the topics can be viewed, and if the Confluent Schema Registry is enabled the message schemas can be viewed.  Full broker and topic configuration is also available.

## Related properties
| Property                                        | Usage                                                                                                                                                                                                                                                                                                                                                                           | Default                            |
|-------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| kafka.control.center.enabled                    | Whether a Docker Confluent Control Center container should be started.                                                                                                                                                                                                                                                                                                          | `false`                            |
| kafka.control.center.confluent.image.tag        | The image tag of the Kafka Confluent Control Center Docker container to use.  Recommendation is to keep this the same as `kafka.confluent.image.tag`.                                                                                                                                                                                                                           | `7.3.2`                            |
| kafka.control.center.port                       | The exposed port of the Kafka Confluent Control Center Docker container.  This port must be available locally.  Navigate to this port on localhost to view the console.  e.g. localhost:9021                                                                                                                                                                                    | `9021`                             |
| kafka.control.center.export.metrics.enabled     | Whether to export JMX metrics from the broker.  Also means if interceptors are added to consumers and producers that further metrics are exported.  Requires Confluent's community package kafka-clients and monitoring-interceptors libraries.                                                                                                                                 | `false`                            |
| kafka.control.center.jmx.port                   | The port for accessing the exported JMX metrics.  The port must be available on the local machine.                                                                                                                                                                                                                                                                              | `9101`                             |
| kafka.control.center.container.logging.enabled  | Whether to output the Kafka Control Center Docker logs to the console.                                                                                                                                                                                                                                                                                                          | `false`                            |


JMX metrics can be exported by enabling `kafka.control.center.export.metrics.enabled`.  This requires the application project to depend on Confluent library dependencies from the Confluent Maven repository.  The default `org.apache.kafka:kafka-clients` version must be excluded from other dependencies that bring it in.

```
<repositories>
    <repository>
        <id>confluent</id>
        <url>https://packages.confluent.io/maven/</url>
    </repository>
</repositories>
	
<dependencies>
    <dependency>
        <groupId>org.apache.kafka</groupId>
        <artifactId>kafka-clients</artifactId>
        <version>7.3.2-ccs</version>
    </dependency>
    <dependency>
        <groupId>io.confluent</groupId>
        <artifactId>monitoring-interceptors</artifactId>
        <version>7.3.2</version>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.kafka</groupId>
        <artifactId>spring-kafka</artifactId>
        <version>3.0.4</version>
        <exclusions>
            <exclusion>
                <groupId>org.apache.kafka</groupId>
                <artifactId>kafka-clients</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
```

With `kafka.control.center.export.metrics.enabled` set to `true` Confluent Monitoring Interceptors can to be used for Java producers and consumers.

For producers, set `interceptor.classes` to `io.confluent.monitoring.clients.interceptor.MonitoringProducerInterceptor`.
```
config.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, "io.confluent.monitoring.clients.interceptor.MonitoringProducerInterceptor");
```

For consumers, set `interceptor.classes` to `io.confluent.monitoring.clients.interceptor.MonitoringConsumerInterceptor`.
```
config.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, "io.confluent.monitoring.clients.interceptor.MonitoringConsumerInterceptor");
```

Interceptors can be added to component test consumers and producers too, by adding the additional config.  See the example on this above, in the `Kafka Client` section.

The web application port is configurable via the `kafka.control.center.port` configuration parameter, defaulting to `9021`.  The chosen port must be available on the local machine running the component tests.

Once the containers are running, navigate to:
```
http://localhost:9021
```
