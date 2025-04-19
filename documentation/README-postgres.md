# Postgres Database

Enable the Postgres database via the property `postgres.enabled`.  The connection details can be configured including the host, database name, schema name, user and password.

## Related properties

| Property                                        | Usage                                                                                                                                                                                                                                                                                                                                                                           | Default                            |
|-------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| postgres.enabled                                | Whether a Docker Postgres container should be started.                                                                                                                                                                                                                                                                                                                          | `false`                            |
| postgres.image.tag                              | The image tag of the Postgres Docker container to use.                                                                                                                                                                                                                                                                                                                          | `14-alpine`                        |
| postgres.host.name                              | The name of the Postgres host.                                                                                                                                                                                                                                                                                                                                                  | `postgres`                         |
| postgres.port                                   | The port of the Postgres Docker container.                                                                                                                                                                                                                                                                                                                                      | `5432`                             |
| postgres.database.name                          | The name of the Postgres database.                                                                                                                                                                                                                                                                                                                                              | `postgres-db`                      |
| postgres.schema.name                            | The name of the Postgres schema.                                                                                                                                                                                                                                                                                                                                                | `test`                             |
| postgres.username                               | The Postgres username.                                                                                                                                                                                                                                                                                                                                                          | `user`                             |
| postgres.password                               | The Postgres password.                                                                                                                                                                                                                                                                                                                                                          | `password`                         |
| postgres.schema.file.path                       | The path to the file containing the schema initialisation SQL.  e.g. `schema.sql`                                                                                                                                                                                                                                                                                               |                                    |
| postgres.container.logging.enabled              | Whether to output the Postgres Docker logs to the console.                                                                                                                                                                                                                                                                                                                      | `false`                            |


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
