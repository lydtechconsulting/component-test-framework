# Localstack

## Related properties
| Property                                        | Usage                                                                                                                                                                                                                                                                                                                                                                           | Default                            |
|-------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| localstack.enabled                              | Whether a Docker Localstack (AWS) container should be started.                                                                                                                                                                                                                                                                                                                  | `false`                            |
| localstack.image.tag                            | The image tag of the Localstack Docker container to use.                                                                                                                                                                                                                                                                                                                        | `0.14.3`                           |
| localstack.port                                 | The port of the Localstack Docker container.                                                                                                                                                                                                                                                                                                                                    | `4566`                             |
| localstack.services                             | Comma delimited list of AWS services to start.                                                                                                                                                                                                                                                                                                                                  | `dynamodb`                         |
| localstack.region                               | The region to use.                                                                                                                                                                                                                                                                                                                                                              | `eu-west-2`                        |
| localstack.container.logging.enabled            | Whether to output the Localstack Docker logs to the console.                                                                                                                                                                                                                                                                                                                    | `false`                            |
| localstack.init.file.path                       | A path to a script to initialise Localstack (e.g. create S3 buckets). This will be mounted in the `/docker-entrypoint-initaws.d` directory on the Localstack container.                                                                                                                                                                                                         | `null`                             |

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
