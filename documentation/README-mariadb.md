# MariaDB Database

Enable the MariaDB database via the property `mariadb.enabled`.  The database is available on port `3306`.

## Related properties
| Property                                        | Usage                                                                                                                                                                                                                                                                                                                                                                           | Default                            |
|-------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| mariadb.enabled                                 | Whether a Docker MariaDB container should be started.                                                                                                                                                                                                                                                                                                                           | `false`                            |
| mariadb.image.tag                               | The image tag of the MariaDB Docker container to use.                                                                                                                                                                                                                                                                                                                           | `10.6`                             |
| mariadb.host.name                               | The name of the MariaDB host.                                                                                                                                                                                                                                                                                                                                                   | `mariadb-host`                     |
| mariadb.port                                    | The port of the MariaDB Docker container.                                                                                                                                                                                                                                                                                                                                       | `3306`                             |
| mariadb.database.name                           | The name of the MariaDB database.                                                                                                                                                                                                                                                                                                                                               | `mariadb-db`                       |
| mariadb.username                                | The MariaDB username.                                                                                                                                                                                                                                                                                                                                                           | `user`                             |
| mariadb.password                                | The MariaDB password.                                                                                                                                                                                                                                                                                                                                                           | `password`                         |
| mariadb.container.logging.enabled               | Whether to output the MariaDB Docker logs to the console.                                                                                                                                                                                                                                                                                                                       | `false`                            |


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
