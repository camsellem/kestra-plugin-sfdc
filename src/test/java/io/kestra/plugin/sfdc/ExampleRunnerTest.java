package io.kestra.plugin.sfdc;

import io.kestra.core.junit.annotations.KestraTest;
import io.kestra.core.queues.QueueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.kestra.core.models.executions.Execution;
import io.kestra.core.repositories.LocalFlowRepositoryLoader;
import io.kestra.core.runners.RunnerUtils;
import io.kestra.core.runners.StandAloneRunner;

import jakarta.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

/**
 * This test will load all flow located in `src/test/resources/flows/`
 * and will run an in-memory runner to be able to test a full flow. There is also a
 * configuration file in `src/test/resources/application.yml` that is only for the full runner
 * test to configure in-memory runner.
 */
@KestraTest
class ExampleRunnerTest {
    @Inject
    protected StandAloneRunner runner;

    @Inject
    protected RunnerUtils runnerUtils;

    @Inject
    protected LocalFlowRepositoryLoader repositoryLoader;

    @BeforeEach
    protected void init() throws IOException, URISyntaxException {
        repositoryLoader.load(Objects.requireNonNull(ExampleRunnerTest.class.getClassLoader().getResource("flows")));
        this.runner.run();
    }

    @SuppressWarnings("unchecked")
    @Test
    void flow() throws TimeoutException, QueueException {
        Execution execution = runnerUtils.runOne(null, "io.kestra.templates", "example");

        assertThat(execution.getTaskRunList(), hasSize(3));
        assertThat(((Map<String, Object>)execution.getTaskRunList().get(2).getOutputs().get("child")).get("value"), is("task-id"));
    }
}
