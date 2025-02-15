package dev.lydtech.component.framework.extension;

import com.github.dockerjava.api.DockerClient;
import dev.lydtech.component.framework.management.DockerManager;
import dev.lydtech.component.framework.management.TestcontainersManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

class ComponentTestExtensionTest {

    private ComponentTestExtension extension;

    @BeforeEach
    public void setUp() {
        extension = new ComponentTestExtension();
    }

    @Test
    public void testBeforeAll_NotStarted() throws Exception {
        ComponentTestExtension.started = false;
        try (MockedStatic<DockerManager> dockerManagerMock = mockStatic(DockerManager.class);
             MockedStatic<TestcontainersManager> testcontainersManagerMock = mockStatic(TestcontainersManager.class)) {

            DockerClient mockDockerClient = mock(DockerClient.class);
            dockerManagerMock.when(DockerManager::getDockerClient).thenReturn(mockDockerClient);
            dockerManagerMock.when(() -> DockerManager.shouldPerformSetup(mockDockerClient)).thenReturn(true);
            ExtensionContext mockContext = mock(ExtensionContext.class);

            extension.beforeAll(mockContext);

            // Verify that the static methods were called as expected
            dockerManagerMock.verify(DockerManager::getDockerClient, times(1));
            dockerManagerMock.verify(() -> DockerManager.shouldPerformSetup(mockDockerClient), times(1));
            testcontainersManagerMock.verify(TestcontainersManager::initialise, times(1));
            dockerManagerMock.verify(() -> DockerManager.captureDockerContainerPorts(mockDockerClient), times(1));
        }
    }

    @Test
    public void testBeforeAll_AlreadyStarted() throws Exception {
        ComponentTestExtension.started = true;
        try (MockedStatic<DockerManager> dockerManagerMock = mockStatic(DockerManager.class);
             MockedStatic<TestcontainersManager> testcontainersManagerMock = mockStatic(TestcontainersManager.class)) {

            ExtensionContext mockContext = mock(ExtensionContext.class);

            extension.beforeAll(mockContext);

            dockerManagerMock.verifyNoInteractions();
            testcontainersManagerMock.verifyNoInteractions();
        }
    }
}
