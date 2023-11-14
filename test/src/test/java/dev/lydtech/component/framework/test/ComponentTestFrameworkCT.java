package dev.lydtech.component.framework.test;

import dev.lydtech.component.framework.client.service.ServiceClient;
import dev.lydtech.component.framework.extension.TestcontainersSetupExtension;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@ExtendWith(TestcontainersSetupExtension.class)
public class ComponentTestFrameworkCT {

    /**
     * Test that all the resources enabled in the pom.xml start up in containers, and that the service under test can be hit.
     */
    @Test
    public void testTestcontainersStartUp() {
        RestAssured.baseURI = ServiceClient.getInstance().getBaseUrl();

        get("/v1/version")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .assertThat()
                .body("version", equalTo("1.0"));
    }
}
