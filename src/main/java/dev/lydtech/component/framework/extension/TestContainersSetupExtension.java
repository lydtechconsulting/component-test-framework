package dev.lydtech.component.framework.extension;

import com.github.dockerjava.api.DockerClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

@Slf4j
public final class TestContainersSetupExtension implements BeforeAllCallback {

    // Use a static boolean to track whether the containers are started so multiple test classes each with this extension
    // only perform one startup.
    private static boolean started = false;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        log.info("Test containers setup extension started.");
        if (!started) {
            started = true;
            DockerClient dockerClient = DockerManager.getDockerClient();
            if  (DockerManager.shouldPerformSetup(dockerClient)) {
                TestContainersManager.initialise();
            } else {
                log.info("Main service is running. Skipping TestContainers setup.");
            }
            DockerManager.captureDockerContainerPorts(dockerClient);
        }
        log.info("Test containers setup extension completed.");
    }
}
