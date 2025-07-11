# Changelog

## [3.10.0] - 2025-06-24
### Added
- Supports Opensearch.

## [3.9.0] - 2025-02-15
### Added
- Supports loading configuration overrides from a properties or YAML file.

## [3.8.0] - 2025-02-08
### Added
- Supports client calls to additional containers using AdditionalContainerClient.

## [3.7.0] - 2025-01-15
### Added
- Supports event sourcing with Ambar.

## [3.6.0] - 2024-09-12
### Added
- Supports passing service application arguments.
### Removed
- Deleted the deprecated TestContainersSetupExtension.

## [3.5.0] - 2024-08-15
### Added
- Supports Kafka native.
### Changed
- Update Conduktor Gateway version to 3.2.1
- Update Testcontainers version to 1.20.1

## [3.4.1] - 2024-06-24
### Changed
- Default Conduktor version increased.

## [3.3.0] - 2024-06-11
### Changed
- Update kafka-avro-serializer to 7.5.3.

## [3.2.0] - 2024-05-31
### Changed
- Support passing in WIREMOCK_OPTIONS environment variable.
- Update default Wiremock tag to 3.6.0.

## [3.1.0] - 2024-05-31
### Added
- Supports MariaDB.

## [3.0.0] - 2024-05-14
### Changed
- Default port for services changed from 9001 to 8080.

## [2.13.0] - 2024-03-17
### Added
- Support addition of container group id for concurrent component test runs.

## [2.12.0] - 2024-03-08
### Added
- Support localstack initialisation file.

## [2.11.0] - 2024-03-05
### Added
- Support configurable configuration files system property.

## [2.10.0] - 2024-02-27
### Added
- Support configurable service health endpoint for startup.

## [2.9.0] - 2024-01-29
### Added
- New service configuration parameters supported.
- 
## [2.8.0] - 2024-01-26
### Added
- New service configuration parameters supported.

## [2.7.0] - 2023-11-21
### Added
- Supports Kafka SASL plain.

## [2.6.0] - 2023-11-18
### Changed
- Updated Testcontainers version to 1.19.2.
- Environment variable TESTCONTAINERS_REUSE_ENABLE replaces TESTCONTAINERS_RYUK_DISABLED.
- ComponentTestExtension replaces the deprecated TestContainersSetupExtension.

## [2.5.1] - 2023-11-08
### Changed
- Support MongoDB replica sets.
- Updated default Debezium version to 2.4.0.Final.

## [2.5.0] - 2023-11-04
### Added
- Supports Elasticsearch.

## [2.4.1] - 2023-10-30
### Added
- PostgresClient for connecting to Postgres instance.
- MongoDbClient for connecting to MongoDB instance.

## [2.4.0] - 2023-10-28
### Added
- Supports MongoDB.

## [2.3.0] - 2023-10-11
### Added
- Supports Conduktor Gateway.

## [2.2.0] - 2023-06-16
### Changed
- Updated default Conduktor Platform version to 1.15.0.

## [2.1.0] - 2023-03-30
### Changed
- Updated default Debezium version to 2.2.
- Updated default Wiremock version to 2.35.0.

## [2.0.0] - 2023-03-17
### Changed
- Supports Spring Boot 3.x.
- Supports Kafka Clients 3.x.
- Requires Java 17 or above.

## [1.11.0] - 2022-11-19
### Added
- Supports multiple Kafka broker nodes.
- Supports configurable topic replication factor.
- Supports configurable min insync replicas.

## [1.10.0] - 2022-10-11
### Added
- Supports exporting Kafka broker, consumer and producer JMX metrics to Confluent Control Center.
- Supports creating a configurable test consumer.

## [1.9.0] - 2022-10-01
### Added
- Supports Conduktor Platform.

## [1.8.0] - 2022-09-13
### Added
- Supports monitoring Kafka in Confluent Control Center, including integration with Confluent Schema Registry.

## [1.7.1] - 2022-08-24
### Changed
- Schema Registry Client documentation.

## [1.7.0] - 2022-08-23
### Added
- Supports Kafka Schema Registry.
- Supports producing and consuming Avro messages to/from Kafka.

## [1.6.0] - 2022-07-16
### Added
- Distributed under the Apache License 2.0.

## [1.5.0] - 2022-07-05
### Added
- Supports asynchronous message send.

## [1.4.0] - 2022-06-02
### Added
- Supports remote host.

## [1.3.0] - 2022-05-28
### Added
- Supports additional containers.

## [1.2.0] - 2022-05-25
### Added
- Supports Localstack (AWS) - DynamoDB.

## [1.1.0] - 2022-05-24
### Added
- Supports Wiremock.

## [1.0.2] - 2022-05-24
### Changed
- Documentation.

## [1.0.1] - 2022-05-24
### Changed
- Documentation.

## [1.0.0] - 2022-05-18
### Added
- Component test JUnit5 extension annotation.
- Supports multiple instances of the service under test.
- Supports Kafka broker.
- Supports Postgres database.
- Supports Debezium (Kafka Connect).
- Supports keeping containers up.
