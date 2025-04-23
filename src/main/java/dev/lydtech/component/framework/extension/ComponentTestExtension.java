package dev.lydtech.component.framework.extension;

import dev.lydtech.component.framework.management.TestcontainersManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Junit5 Extension class to instantiate the component-test-framework.
 *
 * Example usage:
 *
 * {@literal @}ExtendWith(ComponentTestExtension.class)
 * {@literal @}ActiveProfiles("component-test")
 * public class EndToEndCT {
 */
@Slf4j
public final class ComponentTestExtension implements BeforeAllCallback {

    // Use a static boolean to track whether the containers are started so multiple test classes each with this extension
    // only perform one startup.
    protected static boolean started = false;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        log.info("Component test framework extension started.");
        if (!started) {
            TestcontainersManager.initialise();
            started = true;
        }
        log.info("Test containers setup extension completed.");
    }
}
