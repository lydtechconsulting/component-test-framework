# Component Test Framework

A library allowing component testing of a Spring Boot application.

Dockerises the service under test and the required resources.

Currently supported resources:

- Configurable number of instances of the service under test.
- Kafka broker.

# Dependency

Add this library as a dependency to the pom of the service under test:
```
    <dependency>
        <groupId>dev.lydtech</groupId>
        <artifactId>component-test-framework</artifactId>
        <version>1.1.0</version>
        <scope>test</scope>
    </dependency>
```

# Example Usage

A companion service has been implemented to demonstrate usage of this framework.  It is available here:

https://github.com/lydtechconsulting/ctf-example-service

# Configuration

|Property|Usage|Default|
|---|---|---|
|containers.stayup|Whether the Docker containers should remain up after a test run.|false|
|container.name.prefix|The Docker container prefix name to use.  A namespace for the component test containers.|ct|
|container.main.label|The Docker containers housing the service instances has this label applied.  This is used as part of the `containers.stayup` check, along with the `container.name.prefix`, to determine if the containers are already running.  It is recommended to leave this as the default value, so that subsequent test runs from an IDE do not need to set a system parameter override.|main-container| 
|service.name|The name of the service, used in the service Docker container name.|app|
|service.instance.count|The number of instances of the service under test to start.|1|
|service.image.tag|The tagged image of the service Docker container to use.|latest|
|service.port|The service port number.|9001|
|service.debug.port|The port for remote debugging the service.|5001|
|service.startup.timeout.seconds|The number of seconds to wait for the service to start before considered failed.|180|
|service.container.logging.enabled|Whether to output the service Docker logs to the console.|false|
|postgres.enabled|Whether a Docker Postgres container should be started.|false|
|postgres.image.tag|The image tag of the Postgres Docker container to use.|14-alpine|
|postgres.host.name|The name of the Postgres host.|postgres|
|postgres.port|The port of the Postgres Docker container.|5432|
|postgres.database.name|The name of the Postgres database.|postgres-db|
|postgres.username|The Postgres username.|user|
|postgres.password|The Postgres password.|password|
|kafka.enabled|Whether a Docker Kafka container should be started.|false|
|kafka.confluent.image.tag|The image tag of the Confluent Kafka Docker container to use.|6.2.4|
|kafka.port|The port of the Kafka Docker container.|9093|
|kafka.topics|Comma delimited list of topics to create.  Often topics are auto-created, but for Kafka Streams for example they must be created upfront.|
|kafka.topic.partition.count|The number of partitions for topics that are created.|5|
|kafka.container.logging.enabled|Whether to output the Kafka Docker logs to the console.|false|
|debezium.enabled|Whether a Docker Debezium (Kafka Connect) container should be started.  Requires `kafka.enabled` and `postgres.enabled` to be true.|false|
|debezium.image.tag|The image tag of the Debezium Docker container to use.|1.7.0.Final|
|debezium.port|The port of the Debezium Docker container.|8083|
|debezium.container.logging.enabled|Whether to output the Debezium Docker logs to the console.|false|
|wiremock.enabled|Whether a Docker Wiremock container should be started.|false|
|wiremock.image.tag|The image tag of the Wiremock Docker container to use.|2.32.0|
|wiremock.port|The port of the Wiremock Docker container.|8080|
|wiremock.container.logging.enabled|Whether to output the Wiremock Docker logs to the console.|false|
|localstack.enabled|Whether a Docker Localstack (AWS) container should be started.|false|
|localstack.image.tag|The image tag of the Localstack Docker container to use.|0.14.3|
|localstack.port|The port of the Localstack Docker container.|4566|
|localstack.services|Comma delimited list of AWS services to start.|dynamodb|
|localstack.region|The region to use.|eu-west-2|
|localstack.container.logging.enabled|Whether to output the Localstack Docker logs to the console.|false|

The configuration is logged at test execution time at INFO level.  Enable in `logback-test.xml` with:
```
<logger name="dev.lydtech" level="INFO"/>
```

For choosing a value for the `kafka.confluent.image.tag` property, the Confluent Platform and Apache Kafka Compatibility matrix is available here:
https://docs.confluent.io/platform/current/installation/versions-interoperability.html

# Service Pom.xml

## Properties

To enable leaving the Docker containers running after a test run, in order to run tests again without re-starting the containers, the following property should be included in the `properties` section:
```
<properties>
    <containers.stayup>false</containers.stayup>
    <httpclient5.version>5.0.4</httpclient5.version>
</properties>
```

The `http.client5.version` override is to ensure that this version of the lib is used for the Docker containers check.

The `containers.stayup` property is then used by the Maven Surefire Plugin in the `environmentVariables`.

## Maven Surefire Plugin

Add the Maven Surefire Plugin to the pom of the service under test with the following profile.

The following shows how to override the configurable properties:
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
                            <TESTCONTAINERS_RYUK_DISABLED>${containers.stayup}</TESTCONTAINERS_RYUK_DISABLED>
                        </environmentVariables>
                        <systemPropertyVariables>
                            <container.name.prefix>ct</container.name.prefix>
                            <container.main.label>main-container</container.main.label>
                            <service.name>${project.name}</service.name>
                            <service.instance.count>1</service.instance.count>
                            <service.image.tag>latest</service.image.tag>
                            <service.port>9001</service.port>
                            <service.debug.port>5001</service.debug.port>
                            <service.startup.timeout.seconds>180</service.startup.timeout.seconds>
                            <service.container.logging.enabled>false</service.container.logging.enabled>
                            <postgres.enabled>true</postgres.enabled>
                            <postgres.image.tag>14-alpine</postgres.image.tag>
                            <postgres.host.name>postgres</postgres.host.name>
                            <postgres.port>5432</postgres.port>
                            <postgres.database.name>postgres-db</postgres.database.name>
                            <postgres.username>user</postgres.username>
                            <postgres.password>password</postgres.password>
                            <postgres.container.logging.enabled>false</postgres.container.logging.enabled>
                            <kafka.enabled>true</kafka.enabled>
                            <kafka.confluent.image.tag>6.2.4</kafka.confluent.image.tag>
                            <kafka.port>9093</kafka.port>
                            <kafka.topics>inbound-foo-topic,outbound-bar-topic</kafka.topics>
                            <kafka.topic.partition.count>5</kafka.topic.partition.count>
                            <kafka.container.logging.enabled>false</kafka.container.logging.enabled>
                            <debezium.enabled>true</debezium.enabled>
                            <debezium.image.tag>1.7.0.Final</debezium.image.tag>
                            <debezium.port>8083</debezium.port>
                            <debezium.container.logging.enabled>false</debezium.container.logging.enabled>
                            <wiremock.enabled>true</wiremock.enabled>
                            <wiremock.image.tag>2.32.0</wiremock.image.tag>
                            <wiremock.port>8080</wiremock.port>
                            <wiremock.container.logging.enabled>false</wiremock.container.logging.enabled>
                            <localstack.enabled>true</localstack.enabled>
                            <localstack.image.tag>0.14.3</localstack.image.tag>
                            <localstack.port>4566</localstack.port>
                            <localstack.services>lambda,dynamodb,s3</localstack.services>
                            <localstack.region>eu-west-2</localstack.region>
                            <localstack.container.logging.enabled>false</localstack.container.logging.enabled>
                        </systemPropertyVariables>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

The property overrides are all optional.  There is no need to include them if the default values are required.  The above examples do not indicate defaults.

See `TestContainersConfiguration` for their usage.

# Component Test Annotations

Annotate the SpringBoot test with the following extra annotations:

```
import dev.lydtech.component.framework.extension.TestContainersSetupExtension;

@ExtendWith(TestContainersSetupExtension.class)
public class KafkaStreamsCT {
```

The `TestContainersSetupExtension` is the JUnit5 extension that enables hooking into a test execution run before the tests themselves run, so that the Dockerised containers can be started.

The component test class should be named with the suffix `CT`.  This ensures it is not run via the standard maven-surefire-plugin (if that is in use in the service pom.xml).  Instead it is only run with the `mvn` command when the profile `-Pcomponent` is included.

# Component Test Application Properties

The library expects an `application-component-test.yml` properties file in the `src/test/resources` directory for the service under test with the necessary property overrides.  e.g. to specify the Kafka bootstrap-services URL:
```
---
kafka:
    bootstrap-servers: kafka:9092
```

# Service Health

The service under test must expose its health endpoint for the test set up to know that the service has successfully started before the configurable `service.startup.timeout.seconds` has expired.
```
/actuator/health
```
Include the Spring Boot Actuator module in the service pom for this to auto-enable:
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
    <version>${spring.boot.version}</version>
</dependency>
```

# Dockerising The Service

Build a Docker container with the service under test.  

e.g. use a `Dockerfile` with contents:
```
FROM openjdk:11.0.10-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]
```

Build the Spring Boot application jar:
```
mvn clean install
```

Build the Docker container:
```
docker build -t ct/my-service:latest .
```

Assumes `ct` is used as the container prefix for the component tests (which is the default but can be overridden).

The name (`my-service`) must match the `service.name` configuration.

# Running The Component Tests

Run tests:
```
mvn test -Pcomponent
```

Override any of the other configurable properties in the same way, specifying `-D` args.  e.g.
```
mvn test -Pcomponent -Dservice.instance.count=2 -Dkafka.container.logging.enabled
```

# Keeping Docker Containers Up Between Test Runs

A common usage of this framework is to keep the Docker containers running whilst developing and running the component tests.

As it can take some time to spin up multiple Docker containers for the different resources in use, skipping this step for each test run is a key advantage.

To achieve this, use the `containers.stayup` configuration property:
```
mvn test -Pcomponent -Dcontainers.stayup
```

With the containers running the component tests can for example be run from with the IDE (through the standard right-click Run/Debug test). 

Changes to system properties are only respected when containers are being brought up.  So if changes are required then the containers must be stopped and restarted.

To manually stop the containers, see the Docker commands section below.

The `containers.stayup` property drives the `TESTCONTAINERS_RYUK_DISABLED` environment property.  This is a TestContainers library property it uses to determine whether it should automatically clean up the Docker containers at the end of the test run.

# Running Component Tests Within The IDE

Component tests can be run within the IDE as other tests are.  Typically a component test run is executed via the `mvn` command, and must be configured to leave the containers up.  This means that the configuration properties defined in the pom are used.

Tests can then be re-run via the usual IDE mechanism.  However for the framework to detect that the containers are up it must be passed the configured service name.

Therefore when using the Run/Debug test dialog in the IDE, include the `service.name` system parameter.  e.g.
```
-Dservice.name=ctf-example-service
```
If the default service name (`app`) is used then this step can be skipped.

In order to run component tests from the IDE in the first place that will trigger the required Docker containers being started up then the Configuration dialog must be populated with the required System Properties in order to override the defaults.

e.g. add the following to the Configuration dialog to start up Postgres and Kafka:
```
-postgres.enabled=true -Dkafka.enabled=true
```

# Discovering the Service URL

The `dev.lydtech.component.framework.client.service.ServiceClient` provides a static `getBaseUrl` method to get the base URL, enabling REST calls to be made.
e.g. if using RestAssured as the HTTP client in the test:
```
RestAssured.baseURI = ServiceClient.getBaseUrl();
```

Querying using RestAssured:
```
RestAssured.get("/v1/my-service).then().assertThat().statusCode(202)
```

# Kafka Client

The Kafka Client enables the component test to send events to topics on the Kafka broker, and likewise consume events that are emitted by the service under test.

Create a consumer to poll for messages sent by the service under test:
```
import dev.lydtech.component.framework.client.kafka.KafkaClient;

Consumer fooConsumer = KafkaClient.createConsumer(FOO_TOPIC);
```

Send a message:
```
KafkaClient.sendMessage(FOO_TOPIC, key, payload);
```

Consume and assert a message:
```
KafkaClient.consumeAndAssert("TestName", fooConsumer, EXPECTED_COUNT_RECEIVED, FURTHER_POLLS_TO_PERFORM);
```

# Remote Debugging The Service

As the service is running in a Docker container, in order to debug a test run then remote debugging is required. 

When configuring the service under test, a value for the `service.debug.port` property must be supplied.  This port is mapped to a random port when the Docker container is started (or multiple in the case where multiple instances of the service are configured to run).

Execute a test run, leaving the containers up, with `containers.stayup`.  Now remote debugging can be undertaken, setting breakpoints on the application code in the usual way.

The mapped debug port can be discovered by listing the Docker containers with `docker ps` and viewing the mapping.
```
CONTAINER ID   IMAGE                           COMMAND                   CREATED          STATUS          PORTS                                                        NAMES
19b474ec03e8   ct/ctf-example-service:latest   "sh -c 'java ${JAVA_…"    6 seconds ago    Up 5 seconds    0.0.0.0:57583->5001/tcp, 0.0.0.0:57584->9001/tcp             ct-ctf-example-service-1
```

As the configured debug port by default is `5001`, then the mapped port can be seen to be `57583`.

This port can then used in the IDE Remote JVM Debug Run/Debug Configurations dialog.  Use `Host: localhost` and `Port: 57583`, and start the debug. 

Note that if the application code is changed then it must be rebuilt, and the service Docker container rebuilt and restarted.  This results in a different debug port being mapped.

# JSON Mapping

A JSON mapping utility is provided to allow marshalling of PoJOs to/from JSON Strings.  This is a convenient feature for preparing event payloads to be sent in the JSON format to Kafka.
```
dev.lydtech.component.framework.mapper
```

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
docker exec -it ct-kafka  /bin/sh /usr/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic foo-topic --from-beginning`
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
