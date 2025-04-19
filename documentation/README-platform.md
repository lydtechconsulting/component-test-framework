# Conduktor Platform

The Conduktor Platform is a web application that provides a user interface for inspecting the Kafka broker and topics.  Messages on the topics can be viewed, and if the Confluent Schema Registry is enabled the message schemas can be viewed.  Full broker and topic configuration is also available.

## Related properties
| Property                                        | Usage                                                                                                                                                                                                                                                                                                                                                                           | Default                            |
|-------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| conduktor.enabled                               | Whether a Docker Conduktor Platform container should be started.                                                                                                                                                                                                                                                                                                                | `false`                            |
| conduktor.image.tag                             | The image tag of the Conduktor Platform Docker container to use.                                                                                                                                                                                                                                                                                                                | `1.23.0`                           |
| conduktor.license.key                           | License key for Conduktor Platform.  (Optional)                                                                                                                                                                                                                                                                                                                                 |                                    |
| conduktor.port                                  | The exposed port of the Conduktor Platform Docker container.  This port must be available locally.  Navigate to this port on localhost to view the console.  e.g. localhost:8088                                                                                                                                                                                                | `8088`                             |
| conduktor.container.logging.enabled             | Whether to output the Conduktor Docker logs to the console.                                                                                                                                                                                                                                                                                                                     | `false`                            |

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
