package dev.lydtech.component.framework.client.kafka;

import java.util.Optional;

import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaSchemaRegistryClient {

    private static KafkaSchemaRegistryClient instance;

    private String schemaRegistryBaseUrl;

    private SchemaRegistryClient client;

    private KafkaSchemaRegistryClient() {
        String kafkaSchemaRegistryHost = Optional.ofNullable(System.getProperty("docker.host")).orElse("localhost");
        String kafkaSchemaRegistryPort = Optional.ofNullable(System.getProperty("kafka.schema.registry.mapped.port"))
                .orElseThrow(() -> new RuntimeException("kafka.schema.registry.port property not found"));
        this.schemaRegistryBaseUrl = "http://" + kafkaSchemaRegistryHost + ":" + kafkaSchemaRegistryPort;
        log.info("Kafka schema registry base URL is: " + schemaRegistryBaseUrl);
        client = new CachedSchemaRegistryClient(schemaRegistryBaseUrl, 100);
    }

    public synchronized static KafkaSchemaRegistryClient getInstance() {
        if(instance==null) {
            instance = new KafkaSchemaRegistryClient();
        }
        return instance;
    }

    protected String getSchemaRegistryBaseUrl() {
        return schemaRegistryBaseUrl;
    }

    /**
     * Deletes registered schemas from the registry.
     */
    public void resetSchemaRegistry() throws Exception {
        client.reset();
        client.getAllSubjects().stream().forEach(subject -> {
            try {
                client.deleteSubject(subject);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Register the schema derived from the avro generated class.
     *
     * To get the schema, call getClassSchema() on the Avro generated class.
     *
     * e.g. SendPayment.getClassSchema().toString()
     *
     * The subject is calculated from the Class.  The hyphenated lower case String based on the avro generated class name,
     * with a "-value" suffix.
     * e.g. SendPayment is send-payment-value.
     *
     * @param subject the subject (topic name) of the schema being registered
     * @param schema the schema JSON string
     * @return schemaId the schemaId used to register this Avro schema
     */
    public int registerSchema(String subject, String schema) throws Exception {
        ParsedSchema avroSchema = new AvroSchema(schema);
        return client.register(subject+"-value", avroSchema);
    }
}
