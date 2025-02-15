package dev.lydtech.component.framework.management;

import java.util.Collections;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.Container;
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
}
