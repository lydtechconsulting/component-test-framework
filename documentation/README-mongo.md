# MongoDB Database

Enable the MongoDB database via the property `mongodb.enabled`.  The database is available on port `27017`.

## Related properties
| Property                                        | Usage                                                                                                                                                                                                                                                                                                                                                                           | Default                            |
|-------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| mongodb.enabled                                 | Whether a Docker MongoDB container should be started.                                                                                                                                                                                                                                                                                                                           | `false`                            |
| mongodb.image.tag                               | The image tag of the MongoDB Docker container to use.                                                                                                                                                                                                                                                                                                                           | `7.0.2`                            |
| mongodb.container.logging.enabled               | Whether to output the MongoDB Docker logs to the console.                                                                                                                                                                                                                                                                                                                       | `false`                            |

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
