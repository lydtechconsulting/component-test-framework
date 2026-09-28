# Localstack

## Related properties
| Property                                        | Usage                                                                                                                                                                                                                                                                                                                                                                           | Default                            |
|-------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| localstack.enabled                              | Whether a Docker Localstack (AWS) container should be started.                                                                                                                                                                                                                                                                                                                  | `false`                            |
| localstack.image.tag                            | The image tag of the Localstack Docker container to use.                                                                                                                                                                                                                                                                                                                        | `0.14.3`                           |
| localstack.port                                 | The port of the Localstack Docker container.                                                                                                                                                                                                                                                                                                                                    | `4566`                             |
| localstack.services                             | Comma delimited list of AWS services to start.                                                                                                                                                                                                                                                                                                                                  | `dynamodb`                         |
| localstack.container.logging.enabled            | Whether to output the Localstack Docker logs to the console.                                                                                                                                                                                                                                                                                                                    | `false`                            |
| localstack.init.file.path                       | A path to a script to initialise Localstack (e.g. create S3 buckets). This is copied to the container as an executable `init.sh` in the directory given by `localstack.init.dir`, so it does not need the executable bit set on the host.                                                                                                                                       | `null`                             |
| localstack.init.dir                             | The directory on the Localstack container that `localstack.init.file.path` is mounted into. Localstack removed the default `/docker-entrypoint-initaws.d` hook directory in 2.0, so an init file used with a 2.0 or later `localstack.image.tag` must set this to `/etc/localstack/init/ready.d`.                                                                               | `/docker-entrypoint-initaws.d`     |

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
