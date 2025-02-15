package dev.lydtech.component.framework.management;

import java.util.Collections;
import java.util.Properties;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.Container;
import dev.lydtech.component.framework.configuration.TestcontainersConfiguration;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DockerManagerTest {

    private AutoCloseable mocks;
    private DockerClient dockerClient;

    @BeforeEach
    public void setUp() {
        // Configure with the default properties.
        TestcontainersConfiguration.configure(new Properties());
        mocks = MockitoAnnotations.openMocks(this);
        dockerClient = Mockito.mock(DockerClient.class);
    }

    @AfterEach
    public void tearDown() throws Exception {
        mocks.close();
        dockerClient = null;
    }

    @Test
    public void testGetDockerClient() {
        DockerClient dockerClient = DockerManager.getDockerClient();

        MatcherAssert.assertThat(dockerClient, is(notNullValue()));
    }

    /**
     * If the main container is already running (the service under test), and the Testcontainers container is not present
     * (which if it was would be responsible for stopping the containers in the test), then do not perform setup.
     */
    @Test
    public void testShouldPerformSetup_WhenMainContainerIsRunning() {
        Container mainContainer = mock(Container.class);
        when(mainContainer.getNames()).thenReturn(new String[]{"/ct-docker-container-name"});
        when(mainContainer.getLabels()).thenReturn(Collections.singletonMap("dev.lydtech.main-container-label", "main-container"));

        ListContainersCmd listContainersCmdMock = mock(ListContainersCmd.class);
        when(dockerClient.listContainersCmd()).thenReturn(listContainersCmdMock);
        when(listContainersCmdMock.exec()).thenReturn(Collections.singletonList(mainContainer));

        boolean result = DockerManager.shouldPerformSetup(dockerClient);

        MatcherAssert.assertThat(result, is(false));
    }

    /**
     * If the main container is not running (the service under test), then perform setup, even if the Testcontainers
     * container is running.
     */
    @Test
    public void testShouldPerformSetup_WhenTestcontainersIsRunning() {
        Container testContainer = mock(Container.class);
        when(testContainer.getNames()).thenReturn(new String[]{"/testcontainers-ryuk"});

        ListContainersCmd listContainersCmdMock = mock(ListContainersCmd.class);
        when(dockerClient.listContainersCmd()).thenReturn(listContainersCmdMock);
        when(listContainersCmdMock.exec()).thenReturn(Collections.singletonList(testContainer));

        boolean result = DockerManager.shouldPerformSetup(dockerClient);

        MatcherAssert.assertThat(result, is(true));
    }

    /**
     * If no containers are running, then perform setup.
     */
    @Test
    public void testShouldPerformSetup_WhenNoContainerIsRunning() {
        Container testContainer = mock(Container.class);
        when(testContainer.getNames()).thenReturn(new String[]{""});

        ListContainersCmd listContainersCmdMock = mock(ListContainersCmd.class);
        when(dockerClient.listContainersCmd()).thenReturn(listContainersCmdMock);
        when(listContainersCmdMock.exec()).thenReturn(Collections.singletonList(testContainer));

        boolean result = DockerManager.shouldPerformSetup(dockerClient);

        MatcherAssert.assertThat(result, is(true));
    }
}
