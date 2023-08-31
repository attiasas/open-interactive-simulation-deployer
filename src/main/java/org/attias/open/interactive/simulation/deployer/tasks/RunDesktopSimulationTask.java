package org.attias.open.interactive.simulation.deployer.tasks;

import org.attias.open.interactive.simulation.core.backend.engine.AppConfiguration;
import org.attias.open.interactive.simulation.deployer.backend.RunnerManager;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RunDesktopSimulationTask extends DefaultTask {
    private static final Logger log = LoggerFactory.getLogger(RunDesktopSimulationTask.class);

    @TaskAction
    public void runSimulation() {
        log.info("{} Running desktop simulation", getPath());
        RunnerManager.executeRunOnPlatform(getProject(), RunnerManager.getRunningEnvVariables(getProject()), AppConfiguration.AppType.Desktop);
    }
}
