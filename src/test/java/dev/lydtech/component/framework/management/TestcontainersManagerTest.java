package dev.lydtech.component.framework.management;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import dev.lydtech.component.framework.configuration.TestcontainersConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestcontainersManagerTest {

    @AfterEach
    public void tearDown() {
        System.clearProperty("container.parallel.startup.enabled");
        TestcontainersConfiguration.configure(new Properties());
    }

    private void configureParallelStartup(boolean enabled) {
        Properties properties = new Properties();
        properties.setProperty("container.parallel.startup.enabled", String.valueOf(enabled));
        TestcontainersConfiguration.configure(properties);
    }

    /**
     * The sequences must genuinely overlap, not merely all complete. Each one counts down and then waits
     * for the others, so this can only pass if they run at the same time - and cannot pass by accident on
     * a fast machine.
     */
    @Test
    public void testRunStartupSequences_parallel_sequencesOverlap() {
        configureParallelStartup(true);
        CountDownLatch latch = new CountDownLatch(3);
        AtomicInteger completed = new AtomicInteger();

        Runnable sequence = () -> {
            latch.countDown();
            try {
                if (!latch.await(10, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("sequences did not run concurrently");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
            completed.incrementAndGet();
        };

        TestcontainersManager.runStartupSequences(List.of(sequence, sequence, sequence));

        assertThat(completed.get(), is(equalTo(3)));
    }

    @Test
    public void testRunStartupSequences_sequential_runsInOrder() {
        configureParallelStartup(false);
        List<String> order = new CopyOnWriteArrayList<>();

        TestcontainersManager.runStartupSequences(List.of(
                () -> order.add("first"),
                () -> order.add("second"),
                () -> order.add("third")));

        assertThat(order, is(equalTo(List.of("first", "second", "third"))));
    }

    /**
     * The original failure must surface unchanged, so a container that fails to start reports the same
     * error whether or not startup is parallel.
     */
    @Test
    public void testRunStartupSequences_parallel_failurePropagatesOriginalException() {
        configureParallelStartup(true);
        IllegalStateException failure = new IllegalStateException("kafka failed to start");

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> TestcontainersManager.runStartupSequences(List.of(
                        () -> { },
                        () -> { throw failure; })));

        assertThat(thrown, is(equalTo(failure)));
    }

    @Test
    public void testRunStartupSequences_sequential_failurePropagatesOriginalException() {
        configureParallelStartup(false);
        IllegalStateException failure = new IllegalStateException("kafka failed to start");

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> TestcontainersManager.runStartupSequences(List.of(
                        () -> { },
                        () -> { throw failure; })));

        assertThat(thrown, is(equalTo(failure)));
    }

    /**
     * Every sequence is awaited even when one fails, so the others are not left starting containers in
     * the background while the error propagates.
     */
    @Test
    public void testRunStartupSequences_parallel_awaitsEverySequenceOnFailure() {
        configureParallelStartup(true);
        CountDownLatch released = new CountDownLatch(1);
        AtomicInteger finished = new AtomicInteger();

        assertThrows(IllegalStateException.class,
                () -> TestcontainersManager.runStartupSequences(List.of(
                        () -> {
                            try {
                                released.await(10, TimeUnit.SECONDS);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            finished.incrementAndGet();
                        },
                        () -> {
                            released.countDown();
                            throw new IllegalStateException("kafka failed to start");
                        })));

        assertThat(finished.get(), is(equalTo(1)));
    }

    @Test
    public void testRunStartupSequences_singleSequenceRunsWithoutAnExecutor() {
        configureParallelStartup(true);
        AtomicInteger completed = new AtomicInteger();

        TestcontainersManager.runStartupSequences(List.of(completed::incrementAndGet));

        assertThat(completed.get(), is(equalTo(1)));
    }
}
