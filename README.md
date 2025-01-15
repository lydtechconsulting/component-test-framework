# Component Test Framework

# Contents

- [Overview](README.md#overview)
- [Supported Resources](README.md#supported-resources)
- [Supported Versions](README.md#supported-versions)
- [Maven Dependency](README.md#maven-dependency)
- [Example Usage Projects](README.md#example-usage-projects)
- [Upgrading From Previous Versions](README.md#upgrading-from-previous-versions)
- [Configuration Options](README.md#configuration-options)
- [Using Maven](README.md#using-maven)
- [Using Gradle](README.md#using-gradle)
- [Writing Component Tests](README.md#writing-component-tests)
- [Service Under Test](README.md#service-under-test)
- [Running The Component Tests](README.md#running-the-component-tests)
- [Additional Containers](README.md#additional-containers)
- [Postgres Database](README.md#postgres-database)
- [MongoDB Database](README.md#mongodb-database)
- [MariaDB Database](README.md#mariadb-database)
- [Kafka](README.md#kafka)
- [Kafka Avro](README.md#kafka-avro)
- [Kafka Schema Registry](README.md#kafka-schema-registry)
- [Confluent Control Center](README.md#confluent-control-center)
- [Conduktor Platform](README.md#conduktor-platform)
- [Conduktor Gateway](README.md#conduktor-gateway)
- [Debezium](README.md#debezium)
- [Wiremock](README.md#wiremock)
- [Localstack (with DynamoDB)](README.md#localstack)
- [Elasticsearch](README.md#elasticsearch)
- [Ambar](README.md#ambar)
- [Docker Commands](README.md#docker-commands)

# Overview

This open-source library facilitates component testing of a Spring Boot application.

The service under test and its required dependent resources (such as a broker or database) are spun up in Docker containers.  The component test then interacts with the system treating it as a black box, to verify the behaviour.

To use, a JUnit test is simply annotated with a Component Test Framework annotation.  Tests can be run both locally by the developer/tester, and automated in the CI pipeline.

The framework uses the open-source [Testcontainers](https://www.testcontainers.org/) library to start and manage the Docker containers.

In the following example, the Component Test Framework spins up the system under test.  In this case it comprises of two instances of the Spring Boot application to test and a two node Kafka cluster with Zookeeper, each in their own Docker container.  The application has a REST endpoint and consumes messages from, and produces messages to, Kafka.  Confluent Control Center is also spun up in a Docker container, which monitors the application instances and Kafka broker nodes, allowing the tester to view metrics on the system under test.  This can be a helpful tool in debugging test issues.

![Component testing a Spring Boot application that integrates with Kafka](resources/ctf-kafka-example.png)

_Figure 1: Component testing a Spring Boot application that integrates with Kafka_

This test is available in the repository [here](https://github.com/lydtechconsulting/kafka-springboot-consume-produce).

In this second example, a Change Data Capture (CDC) flow is tested.  The component test spins up the Spring Boot application, MongoDB, Debezium (Kafka Connect), and Kafka in Docker containers using the Component Test Framework.  The test registers the Debezium connector with Kafka Connect, and the end to end CDC flow is tested.

![Component testing a CDC flow with Kafka Connect](resources/ctf-cdc-example.png)

_Figure 2: Component testing a CDC flow with Kafka Connect_

This test is available in the repository [here](https://github.com/lydtechconsulting/kafka-connect-debezium-mongodb).

[[Back To Top](README.md#component-test-framework)]

# Supported Resources:

- Configurable number of instances of the service under test.
- Additional containers (simulators/services)
- Kafka broker (standard or native build)
- Kafka Schema Registry
- Postgres database
- MongoDB database
- MariaDB database
- Debezium Kafka Connect
- Standalone wiremock
- Localstack (AWS components - e.g. DynamoDB)
- Confluent Control Center
- Conduktor Platform
- Conduktor Gateway
- Elasticsearch
- Ambar (Event sourcing)

[[Back To Top](README.md#component-test-framework)]

# Supported Versions

`component-test-framework` version `2.x` supports:
- Spring Boot 3.x
- Kafka Clients 3.x
- Java 17

`component-test-framework` version `1.x` supports:
- Spring Boot 2.x
- Kafka Clients 2.x
- Java 11

[[Back To Top](README.md#component-test-framework)]

# Maven Dependency

Add this library as a dependency to the pom of the service under test:
```
    <dependency>
        <groupId>dev.lydtech</groupId>
        <artifactId>component-test-framework</artifactId>
        <version>{version}</version>
        <scope>test</scope>
    </dependency>
```

[[Back To Top](README.md#component-test-framework)]

# Example Usage Projects

Example companion projects have been created to demonstrate usage of this framework.

The `component-test-framework` can be used within a single module project as per:

https://github.com/lydtechconsulting/ctf-example-service

This project demonstrates using:
- Kafka
- Postgres
- Debezium
- Wiremock

There is an advantage in separating its usage from the service under test to ensure that it does not utilise any of the service's classes, and to ensure there are no dependency clashes with the serivce's dependencies.  The following project demonstrates its usage in a multi module project:

https://github.com/lydtechconsulting/ctf-example-multi-module

This project demonstrates using:
- A child `component-test` module. 
- REST calls to the service under test.
- Multiple additional containers (simulators) - these also benefit from the multi module structure as each is defined in its own child module.
- Running component tests with gradle.

Other reference projects that utilise the framework for component testing the application:

https://github.com/lydtechconsulting/kafka-idempotent-consumer-dynamodb - includes Localstack with DynamoDB and uses multiple instances of the service under test.

https://github.com/lydtechconsulting/kafka-schema-registry-avro - a multi-module maven project demonstrating using the Confluent Schema Registry, and demonstrates using Confluent Control Center and Conduktor Platform with Confluent Schema Registry integration.

https://github.com/lydtechconsulting/kafka-metrics - demonstrates using multiple Kafka broker nodes, multiple instances of the service under test, topic replication and min-insync replicas, with Confluent Control Center and Conduktor Platform).

https://github.com/lydtechconsulting/kafka-springboot-consume-produce - demonstrates using Kafka (either standard or native build) to consume and produce events, and observe events using Confluent Control Center and Conduktor.

https://github.com/lydtechconsulting/kafka-streams - demonstrates using Kafka with the Kafka Streams API. 

https://github.com/lydtechconsulting/kafka-idempotent-consumer - uses multiple instances of the service under test.

https://github.com/lydtechconsulting/kafka-consumer-retry - uses multiple instances of the service under test.

https://github.com/lydtechconsulting/kafka-batch-consume - uses a custom Producer with additional configuration for batch send.

https://github.com/lydtechconsulting/kafka-chaos-testing - demonstrates using Conduktor Gateway for chaos testing the service under test.

https://github.com/lydtechconsulting/kafka-connect-debezium-postgres - demonstrates using Kafka Connect with Debezium for Change Data Capture with Postgres. 

https://github.com/lydtechconsulting/kafka-connect-debezium-mongodb - demonstrates using Kafka Connect with Debezium for Change Data Capture with MongoDB.

https://github.com/lydtechconsulting/springboot-rest - demonstrates hitting the service via a REST API to perform CRUD operations.

https://github.com/lydtechconsulting/springboot-postgres - demonstrates using Postgres as the database for reading and writing items.

https://github.com/lydtechconsulting/springboot-mongodb - demonstrates using MongoDB as the database for reading and writing items.

https://github.com/lydtechconsulting/springboot-elasticsearch - demonstrates reading and writing items to Elasticsearch.

https://github.com/lydtechconsulting/kafka-sasl-plain - demonstrates consumers and producers connecting to Kafka using the Simple Authentication and Security Layer (SASL) PLAIN protocol.

https://github.com/lydtechconsulting/micronaut-postgres-java - demonstrates a Micronaut application written in Java, built with Gradle, using Postgres as the database for reading and writing items.

https://github.com/lydtechconsulting/micronaut-postgres-kotlin - demonstrates a Micronaut application written in Kotlin, built with Gradle, using Postgres as the database for reading and writing items.

https://github.com/lydtechconsulting/micronaut-kafka-java - demonstrates a Micronaut application written in Java, built with Gradle, using Kafka as the messaging broker for sending and receiving items.

https://github.com/lydtechconsulting/micronaut-kafka-kotlin - demonstrates a Micronaut application written in Kotlin, built with Gradle, using Kafka as the messaging broker for sending and receiving items.

https://github.com/lydtechconsulting/flink-kafka-connector - demonstrates a Flink application using Kafka as the source and sink for streaming data.

https://github.com/lydtechconsulting/event-sourcing-ambar - demonstrates event sourcing with Ambar.

[[Back To Top](README.md#component-test-framework)]

# Upgrading From Previous Versions

## Upgrading To 3.x From 2.x

### Application Default Port

The default port that the Component Test Framework uses for the application under test has changed from `9001` to `8080`.

If using port `9001` for the application either specify this port in the configuration:

e.g. in the pom.xml add `<service.port>9001</service.port>`

Or change the application port to be `8080` in order to leave the Component Test Framework using the default port.

## Upgrading To 2.6.0 From Any Previous Version

### JUnit Extension Class
The JUnit extension class `dev.lydtech.component.framework.extension.TestContainersSetupExtension` has been deprecated and removed from version `3.6.0`.  Instead use `dev.lydtech.component.framework.extension.ComponentTestExtension`.  e.g. `@ExtendWith(ComponentTestExtension.class)`.

### Testcontainers Environment Variable
The Testcontainers environment variable `TESTCONTAINERS_RYUK_DISABLED` used for keeping containers up between test runs has changed to `TESTCONTAINERS_REUSE_ENABLE`.  In the maven pom component test profile, change to use this variable:
```
<environmentVariables>
    <TESTCONTAINERS_REUSE_ENABLE>${containers.stayup}</TESTCONTAINERS_REUSE_ENABLE>
</environmentVariables>
```
Similarly for gradle, change the environment set up to:
```
environment "TESTCONTAINERS_REUSE_ENABLE", System.getProperty('containers.stayup')
```

[[Back To Top](README.md#component-test-framework)]

# Configuration Options

| Property                                        | Usage                                                                                                                                                                                                                                                                                                                                                                           | Default                                   |
|-------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------|
| containers.stayup                               | Whether the Docker containers should remain up after a test run.  This should be used to set the environment variable `TESTCONTAINERS_REUSE_ENABLE`.  Cannot be set to `true` if `container.append.group.id` is also `true`.                                                                                                                                                    | `false`                                   |
| container.name.prefix                           | The Docker container prefix name to use.  A namespace for the component test containers.  Using different prefixes means that multiple component test runs can run concurrently without conflict.                                                                                                                                                                               | `ct`                                      |
| container.main.label                            | The Docker containers housing the service instances has this label applied.  This is used as part of the `containers.stayup` check, along with the `container.name.prefix`, to determine if the containers are already running.  It is recommended to leave this as the default value, so that subsequent test runs from an IDE do not need to set a system parameter override. | `main-container`                          | 
| container.append.group.id                       | Whether to enable grouping the set of containers in the component test via a unique identifier that is appended to the container name.  This helps identify the group of containers in the given test run.  If set to `true` it means that multiple component test runs can run concurrently without conflict, but cannot be used if `containers.stayup` is set to `true`.      | `false`                                   |
| service.name                                    | The name of the service, used in the service Docker container name.                                                                                                                                                                                                                                                                                                             | `app`                                     |
| service.instance.count                          | The number of instances of the service under test to start.                                                                                                                                                                                                                                                                                                                     | `1`                                       |
| service.image.tag                               | The tagged image of the service Docker container to use.                                                                                                                                                                                                                                                                                                                        | `latest`                                  |
| service.port                                    | The service port number.                                                                                                                                                                                                                                                                                                                                                        | `8080`                                    |
| service.debug.port                              | The port for remote debugging the service.                                                                                                                                                                                                                                                                                                                                      | `5001`                                    |
| service.debug.suspend                           | Use `suspend=y` for remote debugging params. Useful for diagnosing service startup issues.                                                                                                                                                                                                                                                                                      | `false`                                   |
| service.envvars                                 | A comma-separated list of key=value pairs to pass as environment variables for the service container, e.g. `ARG1=value1,ARG2=value2`.                                                                                                                                                                                                                                           |                                           |
| service.additional.filesystem.binds             | A comma-separated list of key=value pairs to use as additional filesystem binds for the service container, where `key=sourcePath` and `value=containerPath` e.g. `./src/test/resources/myDirectory=./myDirectory`.                                                                                                                                                              |                                           |
| service.config.files.system.property            | The name of the system property that denotes the location of additional properties files (as specified in `service.application.yml.path`) for the application.                                                                                                                                                                                                                  | `spring.config.additional-location`       |
| service.application.yml.path                    | Path to the application config file for property overrides (yml format for the service under test).                                                                                                                                                                                                                                                                             | `src/test/resources/application-component-test.yml` |
| service.application.args                        | Application args to pass to the service under test.  Expects the `Dockerfile` to include `APP_ARGS` as part of the `ENTRYPOINT`. e.g. `ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar ${APP_ARGS}"]`                                                                                                                                                                  |                                           |
| service.startup.health.endpoint                 | The log health endpoint to wait for on startup.  Defaults to the Spring Actuator health endpoint.  If `service.startup.log.message` is set then this setting is ignored.                                                                                                                                                                                                        | `/actuator/health`                        |
| service.startup.log.message                     | The log message to wait for on startup. If set, instead of waiting for the health endpoint to return healthy, the container is not considered started until this regex is present in its logs.  e.g. `.*Startup completed.*`                                                                                                                                                    |                                           |
| service.startup.timeout.seconds                 | The number of seconds to wait for the service to start before considered failed.                                                                                                                                                                                                                                                                                                | `180`                                     |
| service.container.logging.enabled               | Whether to output the service Docker logs to the console.                                                                                                                                                                                                                                                                                                                       | `false`                                   |
| additional.containers                           | Colon separated list of additional containers to spin up, such as simulators.  Each additional container entry requires a comma separated list of details:  name, port, debugPort, imageTag, containerLoggingEnabled.  Example is: `third-party-simulator,9002,5002,latest,false:external-service-simulator,9003,5003,latest,false`                                             |                                           |
| postgres.enabled                                | Whether a Docker Postgres container should be started.                                                                                                                                                                                                                                                                                                                          | `false`                                   |
| postgres.image.tag                              | The image tag of the Postgres Docker container to use.                                                                                                                                                                                                                                                                                                                          | `14-alpine`                               |
| postgres.host.name                              | The name of the Postgres host.                                                                                                                                                                                                                                                                                                                                                  | `postgres`                                |
| postgres.port                                   | The port of the Postgres Docker container.                                                                                                                                                                                                                                                                                                                                      | `5432`                                    |
| postgres.database.name                          | The name of the Postgres database.                                                                                                                                                                                                                                                                                                                                              | `postgres-db`                             |
| postgres.schema.name                            | The name of the Postgres schema.                                                                                                                                                                                                                                                                                                                                                | `test`                                    |
| postgres.username                               | The Postgres username.                                                                                                                                                                                                                                                                                                                                                          | `user`                                    |
| postgres.password                               | The Postgres password.                                                                                                                                                                                                                                                                                                                                                          | `password`                                |
| postgres.schema.file.path                       | The path to the file containing the schema initialisation SQL.  e.g. `schema.sql`                                                                                                                                                                                                                                                                            |                                           |
| postgres.container.logging.enabled              | Whether to output the Postgres Docker logs to the console.                                                                                                                                                                                                                                                                                                                      | `false`                                   |
| mongodb.enabled                                 | Whether a Docker MongoDB container should be started.                                                                                                                                                                                                                                                                                                                           | `false`                                   |
| mongodb.image.tag                               | The image tag of the MongoDB Docker container to use.                                                                                                                                                                                                                                                                                                                           | `7.0.2`                                   |
| mongodb.container.logging.enabled               | Whether to output the MongoDB Docker logs to the console.                                                                                                                                                                                                                                                                                                                       | `false`                                   |
| mariadb.enabled                                 | Whether a Docker MariaDB container should be started.                                                                                                                                                                                                                                                                                                                           | `false`                                   |
| mariadb.image.tag                               | The image tag of the MariaDB Docker container to use.                                                                                                                                                                                                                                                                                                                           | `10.6`                                    |
| mariadb.host.name                               | The name of the MariaDB host.                                                                                                                                                                                                                                                                                                                                                   | `mariadb-host`                            |
| mariadb.port                                    | The port of the MariaDB Docker container.                                                                                                                                                                                                                                                                                                                                       | `3306`                                    |
| mariadb.database.name                           | The name of the MariaDB database.                                                                                                                                                                                                                                                                                                                                               | `mariadb-db`                              |
| mariadb.username                                | The MariaDB username.                                                                                                                                                                                                                                                                                                                                                           | `user`                                    |
| mariadb.password                                | The MariaDB password.                                                                                                                                                                                                                                                                                                                                                           | `password`                                |
| mariadb.container.logging.enabled               | Whether to output the MariaDB Docker logs to the console.                                                                                                                                                                                                                                                                                                                       | `false`                                   |
| kafka.enabled                                   | Whether a Docker Kafka container (standard build) should be started.  Cannot be `true` if `kafka.native.enabled` is `true`.  Set the application's Kafka `bootstrap-servers` to `kafka:9092` in the `application-component-test.yml` to connect.                                                                                                                                | `false`                                   |
| kafka.native.enabled                            | Whether a Docker Kafka container (native build) should be started.  Cannot be `true` if `kafka.enabled` is `true`.  Set the application's Kafka `bootstrap-servers` to `kafka:9093` in the `application-component-test.yml` to connect.                                                                                                                                         | `false`                                   |
| kafka.broker.count                              | The number of Kafka broker nodes in the cluster.  Each broker node will start in its own Docker container.  The first instance will be `kafka`, then subsequent will have an instance suffix, e.g. `kafka-2`.  If multiple instances are started a Zookeeper Docker container is also started (rather than using the embedded Zookeeper).                                       | `1`                                       |
| kafka.confluent.image.tag                       | The image tag of the Confluent Kafka Docker container to use.                                                                                                                                                                                                                                                                                                                   | `7.3.2`                                   |
| kafka.topics                                    | Comma delimited list of topics to create.  Often topics are auto-created, but for Kafka Streams for example they must be created upfront.                                                                                                                                                                                                                                       |
| kafka.topic.partition.count                     | The number of partitions for topics that are created.                                                                                                                                                                                                                                                                                                                           | `5`                                       |
| kafka.topic.replication.factor                  | The replication factor to use for topics.  Must not be greater than the configured `kafka.broker.count`.                                                                                                                                                                                                                                                                        | `1`                                       |
| kafka.min.insync.replicas                       | The minimum in-sync number of replicas required for successful writes to topics.  Must not be greater than the configured `kafka.broker.count` nor the `kafka.topic.replication.factor`.                                                                                                                                                                                        | `1`                                       |
| kafka.sasl.plain.enabled                        | Whether Kafka SASL PLAIN is enabled.                                                                                                                                                                                                                                                                                                                                            | `false`                                   |
| kafka.sasl.plain.username                       | The Kafka SASL PLAIN username.  Must be set if `kafka.sasl.plain.enabled` is `true`.                                                                                                                                                                                                                                                                                            | `demo`                                    |
| kafka.sasl.plain.password                       | The Kafka SASL PLAIN password.  Must be set if `kafka.sasl.plain.enabled` is `true`.                                                                                                                                                                                                                                                                                            | `demo-password`                           |
| kafka.container.logging.enabled                 | Whether to output the Kafka Docker logs to the console.                                                                                                                                                                                                                                                                                                                         | `false`                                   |
| kafka.schema.registry.enabled                   | Whether a Docker Schema Registry container should be started.                                                                                                                                                                                                                                                                                                                   | `false`                                   |
| kafka.schema.registry.confluent.image.tag       | The image tag of the Kafka Confluent Schema Registry Docker container to use.  Recommendation is to keep this the same as `kafka.confluent.image.tag`.                                                                                                                                                                                                                          | `7.3.2`                                   |
| kafka.schema.registry.port                      | The port of the Kafka Schema Registry Docker container.                                                                                                                                                                                                                                                                                                                         | `8081`                                    |
| kafka.schema.registry.container.logging.enabled | Whether to output the Kafka Schema Registry Docker logs to the console.                                                                                                                                                                                                                                                                                                         | `false`                                   |
| kafka.control.center.enabled                    | Whether a Docker Confluent Control Center container should be started.                                                                                                                                                                                                                                                                                                          | `false`                                   |
| kafka.control.center.confluent.image.tag        | The image tag of the Kafka Confluent Control Center Docker container to use.  Recommendation is to keep this the same as `kafka.confluent.image.tag`.                                                                                                                                                                                                                           | `7.3.2`                                   |
| kafka.control.center.port                       | The exposed port of the Kafka Confluent Control Center Docker container.  This port must be available locally.  Navigate to this port on localhost to view the console.  e.g. localhost:9021                                                                                                                                                                                    | `9021`                                    |
| kafka.control.center.export.metrics.enabled     | Whether to export JMX metrics from the broker.  Also means if interceptors are added to consumers and producers that further metrics are exported.  Requires Confluent's community package kafka-clients and monitoring-interceptors libraries.                                                                                                                                 | `false`                                   |
| kafka.control.center.jmx.port                   | The port for accessing the exported JMX metrics.  The port must be available on the local machine.                                                                                                                                                                                                                                                                              | `9101`                                    |
| kafka.control.center.container.logging.enabled  | Whether to output the Kafka Control Center Docker logs to the console.                                                                                                                                                                                                                                                                                                          | `false`                                   |
| conduktor.enabled                               | Whether a Docker Conduktor Platform container should be started.                                                                                                                                                                                                                                                                                                                | `false`                                   |
| conduktor.image.tag                             | The image tag of the Conduktor Platform Docker container to use.                                                                                                                                                                                                                                                                                                                | `1.23.0`                                  |
| conduktor.license.key                           | License key for Conduktor Platform.  (Optional)                                                                                                                                                                                                                                                                                                                                 |                                           |
| conduktor.port                                  | The exposed port of the Conduktor Platform Docker container.  This port must be available locally.  Navigate to this port on localhost to view the console.  e.g. localhost:8088                                                                                                                                                                                                | `8088`                                    |
| conduktor.container.logging.enabled             | Whether to output the Conduktor Docker logs to the console.                                                                                                                                                                                                                                                                                                                     | `false`                                   |
| conduktor.gateway.enabled                       | Whether a Docker Conduktor Gateway container should be started.                                                                                                                                                                                                                                                                                                                 | `false`                                   |
| conduktor.gateway.image.tag                     | The image tag of the Conduktor Platform Docker container to use.                                                                                                                                                                                                                                                                                                                | `2.1.5`                                   |
| conduktor.gateway.proxy.port                    | The exposed port of the Conduktor Gateway container.  This port must be available locally.  The port is used to connect to the proxy rather than the Kafka instance directly.  e.g. `bootstrap-servers: conduktorgateway:6969`                                                                                                                                                  | `6969`                                    |
| conduktor.gateway.http.port                     | The exposed port of the Conduktor Gateway container HTTP management API.  This port must be available locally.  The port is used to connect to the proxy rather than the Kafka instance directly.  e.g. `bootstrap-servers: conduktorgateway:6969`                                                                                                                              | `8888`                                    |
| conduktor.gateway.container.logging.enabled     | Whether to output the Conduktor Gateway Docker logs to the console.                                                                                                                                                                                                                                                                                                             | `false`                                   |
| debezium.enabled                                | Whether a Docker Debezium (Kafka Connect) container should be started.  Requires `kafka.enabled` and `postgres.enabled` to be `true`.                                                                                                                                                                                                                                           | `false`                                   |
| debezium.image.tag                              | The image tag of the Debezium Docker container to use.                                                                                                                                                                                                                                                                                                                          | `2.4.0.Final`                             |
| debezium.port                                   | The port of the Debezium Docker container.                                                                                                                                                                                                                                                                                                                                      | `8083`                                    |
| debezium.container.logging.enabled              | Whether to output the Debezium Docker logs to the console.                                                                                                                                                                                                                                                                                                                      | `false`                                   |
| wiremock.enabled                                | Whether a Docker Wiremock container should be started.                                                                                                                                                                                                                                                                                                                          | `false`                                   |
| wiremock.image.tag                              | The image tag of the Wiremock Docker container to use.                                                                                                                                                                                                                                                                                                                          | `3.6.0`                                   |
| wiremock.container.logging.enabled              | Whether to output the Wiremock Docker logs to the console.                                                                                                                                                                                                                                                                                                                      | `false`                                   |
| wiremock.options                                | Optional CLI arguments to be passed to Wiremock.                                                                                                                                                                                                                                                                                                                                |                                           |
| localstack.enabled                              | Whether a Docker Localstack (AWS) container should be started.                                                                                                                                                                                                                                                                                                                  | `false`                                   |
| localstack.image.tag                            | The image tag of the Localstack Docker container to use.                                                                                                                                                                                                                                                                                                                        | `0.14.3`                                  |
| localstack.port                                 | The port of the Localstack Docker container.                                                                                                                                                                                                                                                                                                                                    | `4566`                                    |
| localstack.services                             | Comma delimited list of AWS services to start.                                                                                                                                                                                                                                                                                                                                  | `dynamodb`                                |
| localstack.region                               | The region to use.                                                                                                                                                                                                                                                                                                                                                              | `eu-west-2`                               |
| localstack.container.logging.enabled            | Whether to output the Localstack Docker logs to the console.                                                                                                                                                                                                                                                                                                                    | `false`                                   |
| localstack.init.file.path                       | A path to a script to initialise Localstack (e.g. create S3 buckets). This will be mounted in the `/docker-entrypoint-initaws.d` directory on the Localstack container.                                                                                                                                                                                                         | `null`                                    |
| elasticsearch.enabled                           | Whether a Docker Elasticsearch container should be started.                                                                                                                                                                                                                                                                                                                     | `false`                                   |
| elasticsearch.image.tag                         | The image tag of the Elasticsearch Docker container to use.                                                                                                                                                                                                                                                                                                                     | `8.10.4`                                  |
| elasticsearch.password                          | The Elasticsearch password to use.                                                                                                                                                                                                                                                                                                                                              |                                           |
| elasticsearch.cluster.name                      | The name of the Elasticsearch cluster.                                                                                                                                                                                                                                                                                                                                          | `elasticsearch`                           |
| elasticsearch.discovery.type                    | Whether to form a single node or multi node Elasticsearch cluster.                                                                                                                                                                                                                                                                                                              | `single-node`                             |
| elasticsearch.container.logging.enabled         | Whether to output the Elasticsearch Docker logs to the console.                                                                                                                                                                                                                                                                                                                 | `false`                                   |
| ambar.enabled                                   | Whether a Docker Ambar container should be started.                                                                                                                                                                                                                                                                                                                             | `false`                                   |
| ambar.image.tag                                 | The image tag of the Ambar Docker container to use.                                                                                                                                                                                                                                                                                                                             | `v1.8`                                    |
| ambar.config.file.path                          | The file path for the Ambar config.                                                                                                                                                                                                                                                                                                                                             | `src/test/resources/ambar-config.yaml`                                          |
| ambar.container.logging.enabled                 | Whether to output the Ambar Docker logs to the console.                                                                                                                                                                                                                                                                                                                         | `false`                                   |

The configuration is logged at test execution time at INFO level.  Enable in `logback-test.xml` with:
```
<logger name="dev.lydtech" level="INFO"/>
```

For choosing a value for the `kafka.confluent.image.tag` property, the Confluent Platform and Apache Kafka Compatibility matrix is available here:
https://docs.confluent.io/platform/current/installation/versions-interoperability.html

[[Back To Top](README.md#component-test-framework)]

# Using Maven

## Pom.xml Properties

To enable leaving the Docker containers running after a test run, in order to run tests again without re-starting the containers, the following property should be included in the `properties` section:
```
<properties>
    <containers.stayup>false</containers.stayup>
</properties>
```

The `containers.stayup` property is then used by the Maven Surefire Plugin in the `environmentVariables`.

The `http.client5.version` override may need to be included to ensure that the correct version of this lib is used for the Docker containers check.  This is not required in a multi module project.
```
<httpclient5.version>5.0.4</httpclient5.version>
```

## Maven Surefire Plugin

Add the Maven Surefire Plugin to the pom of the service under test with the following profile.  This example includes adding some property overrides:
```
<profiles>
    <profile>
        <id>component</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <configuration>
                        <includes>
                            <include>*CT.*</include>
                        </includes>
                        <environmentVariables>
                            <TESTCONTAINERS_REUSE_ENABLE>${containers.stayup}</TESTCONTAINERS_REUSE_ENABLE>
                        </environmentVariables>
                        <systemPropertyVariables>
                            <service.instance.count>3</service.instance.count>
                            <service.port>8080</service.port>
                            <service.debug.port>5001</service.debug.port>
                            <kafka.enabled>true</kafka.enabled>
                            <kafka.broker.count>3</kafka.broker.count>
                        </systemPropertyVariables>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

The property overrides are all optional.  There is no need to include them if the default values are required.  The above examples do not indicate defaults.

In a multi module maven project the surefire plugin should be added to the pom of the component test module.

[[Back To Top](README.md#component-test-framework)]

# Using Gradle

Add the following to the `build.gradle` test method:
```
test {
    systemProperties = System.properties
    environment "TESTCONTAINERS_REUSE_ENABLE", System.getProperty('containers.stayup')
    useJUnitPlatform()
}
```

Define any properties that need their default value to be overridden in the `gradle.properties` file.  For example:
```
systemProp.service.name=ctf-example-mm-service
systemProp.additional.containers=third-party-simulator,9002,5002,latest,false:external-service-simulator,9003,5003,latest,false
systemProp.containers.stayup=true
```

The `containers.stayup` property is added to the environment variables by the Gradle build.

[[Back To Top](README.md#component-test-framework)]

# Writing Component Tests

## Component Test Annotations

Annotate the JUnit test with the following extra annotation:

```
import dev.lydtech.component.framework.extension.ComponentTestExtension;

@ExtendWith(ComponentTestExtension.class)
public class EndToEndCT {
```

The `ComponentTestExtension` is the JUnit5 extension that enables hooking into a test execution run before the tests themselves run, so that the Dockerised containers can be started.

The component test class should be named with the suffix `CT`.  This ensures it is not run via the standard maven-surefire-plugin (if that is in use in the service pom.xml).  Instead it is only run with the `mvn` command when the profile `-Pcomponent` is included.

# Component Test Application Properties

By default the library expects an `application-component-test.yml` properties file in the `src/test/resources` directory for the service under test with the necessary property overrides.  e.g. to specify the Kafka bootstrap-services URL:
```
---
kafka:
    bootstrap-servers: kafka:9092
```

This default location can be overridden using the property `service.application.yml.path`.

By default, the system property used to specify the location of the additional properties file is the Spring Boot `spring.config.additional-location`.  This system property name can be overridden via `service.config.files.system.property`.  For example, for a Micronaut application, this should typically be set to `micronaut.config.files`.

[[Back To Top](README.md#component-test-framework)]

# Service Under Test

## Service Health

The service under test can expose its health endpoint for the test set up to know that the service has successfully started before the configurable `service.startup.timeout.seconds` has expired.  The endpoint to check can be overridden via the `service.startup.health.endpoint`.  It defaults to the Spring Actuator health endpoint:
```
/actuator/health
```
Include the Spring Boot Actuator module (along with the Spring Boot Starter Web module) in the service pom for this to auto-enable:
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>${spring.boot.version}</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
    <version>${spring.boot.version}</version>
</dependency>
```
For the default Micronaut health endpoint configure this to `/health`.  Ensure the application has the following dependency:

```
<dependency>
    <groupId>io.micronaut</groupId>
    <artifactId>micronaut-management</artifactId>
    <version>{micronaut-version}</version>
</dependency>
```

Alternatively the presence of a startup log message can be used to determine whether the service has successfully started, using the `service.startup.log.message` configuration.  Set this to the regex String to match on.  For example, set this property to `.*Startup completed.*` to match the following log message from a Micronaut application:
```
2024-02-27 14:05:46 INFO  i.m.r.Micronaut - Startup completed in 31ms. Server Running: http://d911af15be07:8080
```

If this property is set it will be used instead of the startup health endpoint property.

## Dockerising The Service

Build a Docker container with the service under test.  

e.g. use a `Dockerfile` with contents:
```
FROM openjdk:17.0.2-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]
```

Build the application jar:
```
mvn clean install
```

Build the Docker container:
```
docker build -t ct/my-service:latest .
```

Assumes `ct` is used as the container prefix for the component tests (which is the default but can be overridden).

The name (`my-service`) must match the `service.name` configuration.

### Passing Extra Args

To pass extra args to the service under test, update the `Dockerfile` entry point to include `$APP_ARGS` before Dockerising the service:
```
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar ${APP_ARGS}"]
```

Set the `service.application.args` with the extra arguments required.  e.g. `-Dservice.application.args=kafka:9092`.

These will then be passed to the main application class, e.g.
```
public static void main(String[] args) {
    if (args.length > 0) {
        String bootstrapServers = args[0];
```

## Discovering The Service URL

The `dev.lydtech.component.framework.client.service.ServiceClient` provides a static `getBaseUrl` method to get the base URL, enabling REST calls to be made.
e.g. if using RestAssured as the HTTP client in the test:
```
RestAssured.baseURI = ServiceClient.getInstance().getBaseUrl();
```

Querying using RestAssured:
```
RestAssured.get("/v1/my-service").then().assertThat().statusCode(202)
```

[[Back To Top](README.md#component-test-framework)]

# Running The Component Tests

## Maven

Run tests:
```
mvn test -Pcomponent
```

Run tests leaving containers up:
```
mvn test -Pcomponent -Dcontainers.stayup
```

Override any of the other configurable properties in the same way, specifying `-D` args.  e.g.
```
mvn test -Pcomponent -Dservice.instance.count=2 -Dkafka.enabled
```

## Gradle

Run tests:
```
./gradlew clean build
```

Run tests leaving the containers up:
```
./gradlew clean build -Dcontainers.stayup=false
```

Override any of the other configurable properties in the same way, specifying `-D` args.  e.g.
```
./gradlew clean build -Dservice.instance.count=2 -Dkafka.enabled
```

## Keeping Docker Containers Up Between Test Runs

A common usage of this framework is to keep the Docker containers running whilst developing and running the component tests.

As it can take some time to spin up multiple Docker containers for the different resources in use, skipping this step for each test run is a key advantage.

To achieve this, use the `containers.stayup` configuration property:
```
mvn test -Pcomponent -Dcontainers.stayup
```

With the containers running the component tests can for example be run from with the IDE (through the standard right-click Run/Debug test). 

Changes to system properties are only respected when containers are being brought up.  So if changes are required then the containers must be stopped and restarted.

To manually stop the containers, see the Docker commands section below.

The `containers.stayup` property drives the `TESTCONTAINERS_REUSE_ENABLE` environment property.  This is a Testcontainers library property it uses to determine whether it should automatically clean up the Docker containers at the end of the test run.

## Running Concurrent Component Test Runs

In may be required to run multiple component tests concurrently in the same environment.  This may happen in the CI pipeline for example, where perhaps the `main` branch is being built and tested at the same time as one or more `PR` branches.  To facilitate this, there are two alternative approaches that can be adopted.

### Container Name Prefix

Set the `container.name.prefix` to a unique value for the test run.  For example, two concurrent test runs could be executed by building the two containers, and running the component tests with the different prefixes:
```
docker build -t ct1/kafka-springboot-consume-produce:latest .
docker build -t ct2/kafka-springboot-consume-produce:latest .

mvn test -Pcomponent -Dcontainer.name.prefix=ct1
mvn test -Pcomponent -Dcontainer.name.prefix=ct2
```

The following screenshot shows two component test runs executing simultaneously, with prefixes `ct1` and `ct2`. Each group of containers is managed by a `testcontainers-ryuk` container.

![Concurrent component test runs using container.name.prefix](resources/ctf-container-name-prefix.png)

_Figure 3: Concurrent component test runs using container.name.prefix_

This has the advantage that one or both groups of containers could be left up between test runs and reused on a subsequent test execution, using the `containers.stayup` property.

### Container Append Group Id

Set the `container.append.group.id` property to `true`.  This appends an eight character unique Id to the names of all the containers spun up in the component test run.  This means that when another test run is started the container names do not conflict, and this subsequent test run can execute concurrently with the first.

Note that either the `containers.stayup` property or the `container.append.group.id` property can be set to `true` but not both.  This is because the configuration is allowing either the existing containers that are left up between test runs to be reused for the next test run, or for multiple groups of containers to be run concurrently.

The following screenshot shows three component test runs executing simultaneously.  Each group of containers is managed by a `testcontainers-ryuk` container.

![Concurrent component test runs using container.append.group.id](resources/ctf-container-append-group-id.png)

_Figure 4: Concurrent component test runs using container.append.group.id_

## Running Component Tests Within The IDE

Component tests can be run within the IDE as other tests are.  Typically a component test run is executed via the `mvn` command, and must be configured to leave the containers up.  This means that the configuration properties defined in the pom are used.

Tests can then be re-run via the usual IDE mechanism.  However for the framework to detect that the containers are up it must be passed the configured service name.

Therefore when using the Run/Debug test dialog in the IDE, include at least the `service.name` system parameter.  e.g.
```
-Dservice.name=ctf-example-service
```
If any other settings are required these should also be declared (if the containers have not already been left up from a previous run).  For example, enabling Kafka and configuring the topics in the Intellij dialog:

![Configuring parameters in the run / debug dialog](resources/ctf-run-debug-config.png)

_Figure 5: Configuring parameters in the run / debug dialog_

If the default service name (`app`) is used then this step can be skipped.

In order to run component tests from the IDE in the first place that will trigger the required Docker containers being started up then the Configuration dialog must be populated with the required System Properties in order to override the defaults.

e.g. add the following to the Configuration dialog to start up Postgres and Kafka:
```
-postgres.enabled=true -Dkafka.enabled=true
```

## Remote Debugging The Service

As the service is running in a Docker container, in order to debug a test run then remote debugging is required.

When configuring the service under test, a value for the `service.debug.port` property must be supplied.  This port is mapped to a random port when the Docker container is started (or multiple in the case where multiple instances of the service are configured to run).

Execute a test run, leaving the containers up, with `containers.stayup`.  Now remote debugging can be undertaken, setting breakpoints on the application code in the usual way.

The mapped debug port can be discovered by listing the Docker containers with `docker ps` and viewing the mapping.
```
CONTAINER ID   IMAGE                           COMMAND                   CREATED          STATUS          PORTS                                                        NAMES
19b474ec03e8   ct/ctf-example-service:latest   "sh -c 'java ${JAVA_…"    6 seconds ago    Up 5 seconds    0.0.0.0:57583->5001/tcp, 0.0.0.0:57584->8080/tcp             ct-ctf-example-service-1
```

As the configured debug port by default is `5001`, then the mapped port can be seen to be `57583`.

This port can then used in the IDE Remote JVM Debug Run/Debug Configurations dialog.  Use `Host: localhost` and `Port: 57583`, and start the debug.

Note that if the application code is changed then it must be rebuilt, and the service Docker container rebuilt and restarted.  This results in a different debug port being mapped.

[[Back To Top](README.md#component-test-framework)]

# Additional Containers

Any number of additional containers can be started as part of the test run, using the `additional.containers` parameter.  This enables spinning up of simulator services that take the place of real world third party services that the service under test calls.

For each additional container to start provide the name, port, debug port, image tag, and whether the Docker container logs should log to the container.

The additional service/simulator must have an `application.yml` with the required properties in its `src/main/resources` directory.  This will include the service port that is specified in the `additional.containers` property.

Within the component test directory/module declare a component test properties file for overriding the default properties.  This must live under a directory with the same name as the container (excluding the container prefix).  e.g. for a `third-party-simulator`, define:
```
src/test/resources/third-party-simulator/application-component-test.yml
```
A remote debugger can be attached to these containers as per the main service.

Additional containers work well in a multi module project.  They are co-located with the service under test, but defined in their own module for clear separation.  An example of using additional containers can be seen in the accompanying `ctf-example-multi-module` project:

https://github.com/lydtechconsulting/ctf-example-multi-module

[[Back To Top](README.md#component-test-framework)]

# Postgres Database

Enable the Postgres database via the property `postgres.enabled`.  The connection details can be configured including the host, database name, schema name, user and password.

A SQL file can be run when the database container is started for the initial database population.  Specify the path to the SQl file to use with the `postgres.schema.file.path` property.  For example, if the SQL file is located at `src/test/resources/schema.sql`, this will be placed in the classpath (in `target/test-classes/`), so set the property as:

`-Dpostgres.schema.file.path=schema.sql`

Override the main configuration in the application's `application-component-test.yml` file in order to connect to the Dockerised Postgres, for example:
```
spring:
    datasource:
        url: jdbc:postgresql://postgres:5432/postgres?currentSchema=dmeo
        username: postgres
        password: postgres
```

Use the `PostgresClient` utility class to get a `Connection` that can be used to run queries against the database:
```
import dev.lydtech.component.framework.client.database.PostgresClient;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

Connection dbConnection = PostgresClient.getInstance().getConnection();
```

Note that when leaving containers up between test runs, if the default Postgres properties are overridden, then the test needs to be passed these in order to create the connection.  e.g. set:
```
-Dpostgres.database.name=dbname2 -Dpostgres.schema.name=schema2 -Dpostgres.username=user2 -Dpostgres.password=password2
```

Alternatively use the method to get the `Connection` that taking these parameters:
```
Connection dbConnection = PostgresClient.getInstance().getConnection("dbname2", "schema2", "user2", "password2");
```

Close the connection at the end of the test:
```
PostgresClient.getInstance().close(dbConnection);
```

The DB URL with the host and port can be obtained with the following call:
```
String dbUrl = MongoDbClient.getInstance().getMongoClient().getDbHostAndPortUrl();
```
This has the mapped port for the Docker container enabling the test to connect to the database.  It could then for example be used to instantiate a `JdbcTemplate` if using Spring:
```
DriverManagerDataSource dataSource = new DriverManagerDataSource();
dataSource.setUrl(dbUrl);
dataSource.setUsername(username);
dataSource.setPassword(password);
JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
```

[[Back To Top](README.md#component-test-framework)]

# MongoDB Database

Enable the MongoDB database via the property `mongodb.enabled`.  The database is available on port `27017`.

Override the main configuration in the application's `application-component-test.yml` file to connect to the Dockerised MongoDB, for example:

```
spring:
  data:
    mongodb:
      database: demo
      port: 27017
      host: mongodb
```
The MongoDB Testcontainer creates a replica set name `docker-rs`. 

Use the `MongoDbClient` utility class to get a `MongoClient` that can be used to run queries against the database:
```
import dev.lydtech.component.framework.client.database.MongoDbClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

MongoClient mongoClient = MongoDbClient.getInstance().getMongoClient();
MongoCollection items = mongoClient.getDatabase("demo").getCollection("items");
FindIterable results = items.find(Filters.eq("name", request.getName()));
```

Close the connection at the end of the test:
```
MongoDbClient.getInstance().close(mongoClient);
```

Alternatively the MongoDB URL can be obtained with the following call:
```
String dbUrl = MongoDbClient.getInstance().getMongoClient().getDbUrl();
```

This has the mapped port for the Docker container enabling the test to connect to the database.  It could then for example be used to instantiate a `MongoTemplate` if using Spring:
```
ConnectionString connectionString = new ConnectionString(dbUrl);
MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
    .applyConnectionString(connectionString)
    .build();
MongoTemplate mongoTemplate = new MongoTemplate(MongoClients.create(mongoClientSettings), "demo");
```

# MariaDB Database

Enable the MariaDB database via the property `mariadb.enabled`.  The database is available on port `3306`.

Override the main configuration in the application's `application-component-test.yml` file to connect to the Dockerised MariaDB, for example:

```
spring:
  datasource:
    driver-class-name: org.mariadb.jdbc.Driver
    url: jdbc:mariadb://localhost:3306/database
    username: username
    password: password
```

Use the `MariaDbClient` utility class to get a `Connection` that can be used to run queries against the database:
```
import dev.lydtech.component.framework.client.database.MariaDbClient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

Connection connection = MariaDbClient.getInstance().getConnection();

try (PreparedStatement statement = connection.prepareStatement("SELECT version()")) {
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
        LOG.info("resultset: " + resultSet.getString(1));
    }
}
```

Close the connection at the end of the test:
```
MariaDbClient.getInstance().close(dbConnection);
```

The DB URL with the host and port can be obtained with the following call:
```
String dbUrl = MariaDbClient.getInstance().getMariaDbClient().getDbHostAndPortUrl();
```

[[Back To Top](README.md#component-test-framework)]

# Kafka
## Kafka Configuration

The Kafka messaging broker is enabled by either setting `kafka.enabled` to `true`, to start a standard (Confluent) Kafka broker, or by setting `kafka.native.enabled` to `true`, to start a native (Apache) Kafka broker.  But flags cannot be enabled at the same time.

When running a standard broker, set the application's Kafka `bootstrap-servers` to `kafka:9092` in the `application-component-test.yml` to connect.

When running a native broker, Set the application's Kafka `bootstrap-servers` to `kafka:9093` in the `application-component-test.yml` to connect.  The native build boasts very fast start up time, which will aid overall test time.

Both flavours of the broker support integrating with Schema Registry, Debezium, Conduktor Platform, Conduktor Gateway, and Confluent Control Center.  No additional configuration is required to those resources for integration, they work seamlessly whether the standard or native Kafka broker are enabled.

A configurable number of broker and topic configurations can be applied.  These include setting the number of broker nodes in the cluster (`kafka.broker.count`), the topic replication factor (`kafka.topic.replication.factor`), and the minimum number of brokers that must be in-sync to accept a producer write (`kafka.min.insync.replicas`).  Any topics that should be created upfront can be declared in a comma separated list (`kafka.topics`), and the default topic partition count can be configured (`kafka.topic.partition.count`).

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

[[Back To Top](README.md#component-test-framework)]

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

[[Back To Top](README.md#component-test-framework)]

# Kafka Schema Registry

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

[[Back To Top](README.md#component-test-framework)]

# Confluent Control Center

The Confluent Control Center is a web application that provides a user interface for inspecting the Kafka broker and topics.  Messages on the topics can be viewed, and if the Confluent Schema Registry is enabled the message schemas can be viewed.  Full broker and topic configuration is also available.

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

[[Back To Top](README.md#component-test-framework)]

# Conduktor Platform

The Conduktor Platform is a web application that provides a user interface for inspecting the Kafka broker and topics.  Messages on the topics can be viewed, and if the Confluent Schema Registry is enabled the message schemas can be viewed.  Full broker and topic configuration is also available.

The Platform offers other services such as the ability to create Test flows to send and receive messages from the Kafka broker.  These can be explored through the UI.

A license key can be provided via the `conduktor.license.key` configuration parameter to unlock more features and services.  See https://conduktor.io for more.

The web application is configurable via the `conduktor.port` configuration parameter, defaulting to `8088`.  The chosen port must be available on the local machine running the component tests.

Once the containers are running, navigate to:
```
http://localhost:8088
```

Log in with the following credentials:
```
username: admin@conduktor.io
password: admin
```

Launch the `Console` application in order to view the broker, topics, messages, and schema registry data. 

Note: Conduktor requires a Postgres Database instance. By choosing to enable Conduktor, the Component Test Framework will also 
start a Postgres container and configure Conduktor to use that for its persistence.

[[Back To Top](README.md#component-test-framework)]

# Conduktor Gateway

Conduktor Gateway is a proxy that sits between an application and Kafka that facilitates chaos testing.  It achieves this by intercepting requests made by the application to Kafka and returning a percentage of these with errors, based on registering interceptors with the Gateway.

See https://www.conduktor.io/gateway/ for more.

A utility client provides the ability to simulate a number of different errors.  For example, to simulate `INVALID_REQUIRED_ACKS` for 20% of producer requests:

```
import dev.lydtech.component.framework.client.conduktor.ConduktorGatewayClient;

conduktorGatewayClient.simulateBrokenBroker(20, BrokenBrokerErrorType.INVALID_REQUIRED_ACKS);
```
The following are the supported `BrokerBrokerErrorType` types for simulating a broken broker by the component test framework, and whether each results in a Kafka retryable exception that the producer should be able to retry:

| Error Type | Exception Thrown             | Exception Type |
|------------|------------------------------|----------------|
| NOT_ENOUGH_REPLICAS | NotEnoughReplicasException   | Retryable      |
| CORRUPT_MESSAGE | CorruptRecordException | Retryable      |
| INVALID_REQUIRED_ACKS | InvalidRequiredAcksException | Not retryable  |
| UNKNOWN_SERVER_ERROR | UnknownServerException | Not retryable  |

To simulate a partition leader election at the time the request is made for 20% of requests (resulting in the Kafka retryable exception `NotLeaderOrFollowerException`):

```
conduktorGatewayClient.simulateLeaderElection(20);
```

To simulate a slow broker, for 100% of requests, adding a latency of between 50 milliseconds and 150 milliseconds for each request:
```
conduktorGatewayClient.simulateSlowBroker(100, 50, 150)
```

To clear existing interceptors, call:

```
conduktorGatewayClient.reset();
```

When no interceptors are registered, the Gateway will pass requests through directly to Kafka.

[[Back To Top](README.md#component-test-framework)]

# Debezium

Debezium provides a Kafka Connect source connector, streaming events from the Database changelog to Kafka.

A utility client is provides the ability to create the connector, and subsequently delete it.  The connector should be defined in a `json` file and passed to the client in the component test to create.  It can then be deleted at the end of the test using the connector name.

```
import dev.lydtech.component.framework.client.debezium.DebeziumClient;

DebeziumClient.getInstance().createConnector("connector/outbox-connector.json");

DebeziumClient.getInstance().deleteConnector("outbox-connector");
```

See the `ctf-example-service` project for example usage.

[[Back To Top](README.md#component-test-framework)]

# Wiremock

The Wiremock container requires a `health.json` file to be provided in the `src/test/resources/wiremock/` directory with the following contents:
```
{
  "request": {
    "method": "GET",
    "url": "/health"
  },
  "response": {
    "status": 204
  }
}
```

This is used by the component-test-framework to determine whether the Wiremock container has successfully started.

All other mapping files placed in this same directory will also be loaded.

In a multi module project the `src/test/resources/wiremock/` directory lives in the `component-test` module.

The Wiremock client provides various methods for querying the admin API.  The admin API it hooks into is available here:
https://wiremock.org/docs/api

An example:
```
import dev.lydtech.component.framework.client.wiremock.WiremockClient;

RequestCriteria request = RequestCriteria.builder()
        .method("GET")
        .url("/api/thirdparty/"+key)
        .build();
Response response = WiremockClient.getInstance().findMatchingRequests(request);
```

Other mapping files can be loaded by a component test with the following call:
```
WiremockClient.getInstance().postMappingFile("thirdParty/retry_behaviour_success.json");
```
This requires the corresponding mapping file to be located under `src/test/resources/`.  e.g. in this case:
```
src/test/resources/thirdParty/retry_behaviour_success.json
```

Command Line arguments can be passed to Wiremock using the `wiremock.options` property, for example the following will enable global response templating:
```
<properties>
    <wiremock.options>--global-response-templating</containers.stayup>
</properties>
```

[[Back To Top](README.md#component-test-framework)]

# Localstack

## DynamoDB

The provided DynamoDB client provides a method to create a table based on a given entity, in the specified region.

e.g. to create a `ProcessedEvent` table:

```
@DynamoDBTable(tableName="ProcessedEvent")
public class ProcessedEvent {

    @DynamoDBHashKey(attributeName="Id")
    private String id;
[...]
```
The call to the client is:
```
import dev.lydtech.component.framework.client.localstack.DynamoDbClient;

DynamoDbClient.getInstance().createTable(ProcessedEvent.class, "eu-west-2");
```
This method is overloaded to also allow passing in the access key and secret key to use, and the read and write capacity units for the table.

[[Back To Top](README.md#component-test-framework)]

# Elasticsearch

Enable Elasticsearch via the property `elasticsearch.enabled`.  Elasticsearch is available on port `9200`.

The container base URL can be obtained using the `ElasticsearchClient`:

```
import dev.lydtech.component.framework.client.elastic.ElasticsearchCtfClient;

String baseUrl = ElasticsearchCtfClient.getInstance().getBaseUrl();
```

The `co.elastic.clients.elasticsearch.ElasticsearchClient` can be obtained which can then be used to query the dockerised Elasticsearch.  Note this method is overloaded, also taking a `JsonpMapper`.
```
ElasticsearchClient esClient = ElasticsearchCtfClient.getInstance().getElasticsearchClient();
GetResponse<Item> getResponse = esClient.get(s -> s
    .index("item")
    .id(location), Item.class);
```

[[Back To Top](README.md#component-test-framework)]

# Ambar

Ambar is a data streaming service using event sourcing.  For more on Ambar see [ambar.cloud](https://ambar.cloud/). 

Enable Ambar via the property `ambar.enabled`.

As of `v1.8` of Ambar, the database schema for the event store must be loaded when the Postgres instance is started.  Add a `schema.sql` file to `src/test/resources/` with the following contents:
```
CREATE TABLE public.event_store (
    id SERIAL PRIMARY KEY,
    event_id TEXT NOT NULL,
    event_name TEXT NOT NULL,
    aggregate_id TEXT NOT NULL,
    aggregate_version BIGINT NOT NULL,
    json_payload TEXT NOT NULL,
    json_metadata TEXT NOT NULL,
    recorded_on TEXT NOT NULL,
    causation_id TEXT NOT NULL,
    correlation_id TEXT NOT NULL
);
```

Add system property `postgres.schema.file.path` with value `schema.sql` to the component test run.  e.g. update the `pom.xml` with:
```
<postgres.schema.file.path>schema.sql</postgres.schema.file.path>
```

Also add the `ambar-config.yaml` that defines the config for the Ambar container to `src/test/resources/`.  See the [Ambar documentation](https://docs.ambar.cloud/) for more on this.

[[Back To Top](README.md#component-test-framework)]

# Docker Commands

## List Docker Containers

`docker ps`

## Inspecting Kafka Topics

View consumer groups:
```
docker exec -it ct-kafka  /bin/sh /usr/bin/kafka-consumer-groups --bootstrap-server localhost:9092 --list
```

Inspect consumer group:
```
docker exec -it ct-kafka  /bin/sh /usr/bin/kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group my-consumer-group
```

View topics:
```
docker exec -it ct-kafka  /bin/sh /usr/bin/kafka-topics --bootstrap-server localhost:9092 --list
```

Inspect topic:
```
docker exec -it ct-kafka  /bin/sh /usr/bin/kafka-topics --bootstrap-server localhost:9092 --describe --topic foo-topic
```

View messages on topic:
```
docker exec -it ct-kafka  /bin/sh /usr/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic foo-topic --from-beginning
```

View messages on __consumer_offsets topic:
```
docker exec -it ct-kafka  /bin/sh /usr/bin/kafka-console-consumer  --formatter "kafka.coordinator.group.GroupMetadataManager\$OffsetsMessageFormatter" --bootstrap-server localhost:9092 --topic __consumer_offsets --from-beginning
```

## Clean Up Commands

- Manual clean up (if left containers up):
```
docker rm -f $(docker ps -aq)
```

- Forceful clean up (if Docker problems):
```
docker network prune
```
```
docker system prune
```
e.g. for the following exception:
```
com.github.dockerjava.api.exception.NotFoundException: Status 404: {"message":"could not find an available, non-overlapping IPv4 address pool among the defaults to assign to the network"}
```

[[Back To Top](README.md#component-test-framework)]

## Versioning & Release

Every commit or merge to `main` will increment the version and release to Maven central.

- To increment the major version, start commit message with `major:` or `BREAKING CHANGE:`.
- To increment the minor version, start commit message with `feat:`.
- Otherwise the patch version is incremented.
