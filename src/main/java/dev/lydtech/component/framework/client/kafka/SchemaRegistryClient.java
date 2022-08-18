package dev.lydtech.component.framework.client.kafka;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.confluent.kafka.schemaregistry.client.rest.entities.SchemaString;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

@Slf4j
public class SchemaRegistryClient {
    private RequestSpecification schemaRegistryRequestSpec;

    private static final String RESET_MAPPINGS_PATH = "/__admin/mappings/reset";

    private static SchemaRegistryClient instance;

    private String schemaRegistryBaseUrl;

    /**
     * Track the schema Id.
     */
    private static final AtomicInteger schemaId = new AtomicInteger(1);

    private SchemaRegistryClient() {
        String kafkaSchemaRegistryHost = Optional.ofNullable(System.getProperty("docker.host")).orElse("localhost");
        String kafkaSchemaRegistryPort = Optional.ofNullable(System.getProperty("kafkaschemaregistry.mapped.port"))
                .orElseThrow(() -> new RuntimeException("kafkaschemaregistry.port property not found"));
        this.schemaRegistryBaseUrl = "http://" + kafkaSchemaRegistryHost + ":" + kafkaSchemaRegistryPort;
        log.info("Kafka schema registry base URL is: " + schemaRegistryBaseUrl);
        schemaRegistryRequestSpec = new RequestSpecBuilder()
                .setBaseUri(schemaRegistryBaseUrl)
                .build();
        WireMock.configureFor(kafkaSchemaRegistryHost, Integer.parseInt(kafkaSchemaRegistryPort));
    }

    public synchronized static SchemaRegistryClient getInstance() {
        if(instance==null) {
            instance = new SchemaRegistryClient();
        }
        return instance;
    }

    /**
     * Reverts the initial schemaId to use to 1, and resets the wiremock mappings.
     */
    public void resetSchemaRegistry() {
        resetSchemaRegistrySchemaId();
        resetSchemaRegistryMappings();
    }

    /**
     * Reset the Schema Registry schemaId to use for when registering new schemas.
     */
    private void resetSchemaRegistrySchemaId() {
        schemaId.set(1);
    }

    /**
     * Reset the Schema Registry wiremock mappings.
     */
    private void resetSchemaRegistryMappings() {
        RestAssured.given()
                .spec(schemaRegistryRequestSpec)
                .post(RESET_MAPPINGS_PATH)
                .then()
                .assertThat()
                .statusCode(200);
    }

    protected String getSchemaRegistryBaseUrl() {
        return schemaRegistryBaseUrl;
    }

    /**
     * Register the schema derived from the avro generated class.
     *
     * To get the schema, call getClassSchema() on the avro generated class.
     *
     * e.g. SendPayment.getClassSchema().toString()
     *
     * The subject is calculated from the Class.  The hyphenated lower case String based on the avro generated class name,
     * with a "-value" suffix.
     * e.g. SendPayment is send-payment-value.
     *
     * @param clazz the class being registered
     * @param schema the schema JSON string
     * @return schemaId the schemaId used to register this Avro schema
     */
    public static int registerSchema(Class clazz, String schema) throws Exception {

        int schemaIdToUse = schemaId.getAndIncrement();
        String subject = getSubject(clazz);
        log.info("Registering schema for schemaId {} and subject {}", schemaIdToUse, subject);

        // Stub mapping for posting the Avro schema Id for this subject (based on the class).
        // e.g. POST /subjects/foo-completed-value?deleted=false
        stubFor(post(urlPathMatching("/subjects/"+subject))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody("{\"id\":"+schemaIdToUse+"}")));

        // Stub mapping for getting the registered schema for the given schema Id.
        // e.g. GET /schemas/ids/1?fetchMaxId=false
        final SchemaString schemaString = new SchemaString(schema);
        stubFor(get(urlPathMatching("/schemas/ids/"+schemaIdToUse))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(schemaString.toJson())));

        return schemaIdToUse;
    }

    /**
     * Example: SendPayment becomes send-payment-value.
     */
    private static String getSubject(Class clazz) {
        return clazz.getSimpleName().replaceAll("([^^])([A-Z][a-z])", "$1-$2").toLowerCase()+"-value";
    }
}
