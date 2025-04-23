package dev.lydtech.component.framework.extension;

import dev.lydtech.component.framework.management.TestcontainersManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

class ComponentTestExtensionTest {

    private ExtensionContext context;

    @BeforeEach
    void setUp() {
        context = mock(ExtensionContext.class);
    }

    @Test
    void shouldInitialiseContainersOnlyOnceAcrossMultipleInvocations() throws Exception {
        try (MockedStatic<TestcontainersManager> mgr = mockStatic(TestcontainersManager.class)) {
            ComponentTestExtension firstExt = new ComponentTestExtension();
            ComponentTestExtension secondExt = new ComponentTestExtension();

            // First invocation should initialise
            firstExt.beforeAll(context);
            // Second invocation should not initialise again
            secondExt.beforeAll(context);

            // Verify initialise() was called exactly once in total
            mgr.verify(() -> TestcontainersManager.initialise(), times(1));
        }
    }
}
