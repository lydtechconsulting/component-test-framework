package dev.lydtech.component.framework.client.localstack;

import java.util.Optional;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import lombok.extern.slf4j.Slf4j;

import static dev.lydtech.component.framework.configuration.TestcontainersConfiguration.LOCALSTACK_SERVICES;

@Slf4j
public class DynamoDbClient {

    private static DynamoDbClient instance;
    private String baseUrl;

    private DynamoDbClient(){
        String localstackHost = Optional.ofNullable(System.getProperty("docker.host"))
                .orElse("localhost");
        String localstackPort = Optional.ofNullable(System.getProperty("localstack.mapped.port"))
                .orElseThrow(() -> new RuntimeException("localstack.mapped.port property not found"));
        baseUrl = "http://" + localstackHost + ":" + localstackPort;
        log.info("DynamoDB base URL is: " + baseUrl);
    }

    public synchronized static DynamoDbClient getInstance() {
        if(LOCALSTACK_SERVICES == null || !LOCALSTACK_SERVICES.contains("dynamodb")) {
            throw new RuntimeException("'dynamodb' not specified in 'localstack.services'.  'localstack.services' is: "+LOCALSTACK_SERVICES);
        }
        if(instance==null) {
            instance = new DynamoDbClient();
        }
        return instance;
    }

    /**
     * Create a table in DynamoDB for the given class.
     *
     * Use default read/write capacity of 1.
     *
     * Use default AWS credentials "key" and "secret-key".
     */
    public void createTable(Class<?> clazz, String region) {
        createTable(clazz, region, "key", "secret-key", 1L, 1L);
    }

    /**
     * Create a table in DynamoDB for the given class.
     */
    public void createTable(Class<?> clazz, String region, String accessKey, String secretKey, Long readCapacityUnits, Long writeCapacityUnits) {
        try {
            AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(baseUrl, region))
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                    .build();
            DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
            CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(clazz);
            tableRequest.setProvisionedThroughput(new ProvisionedThroughput(readCapacityUnits, writeCapacityUnits));
            amazonDynamoDB.createTable(tableRequest);
        } catch (ResourceInUseException e) {
            log.info("Table already created: "+e.getMessage());
        }
    }
}
