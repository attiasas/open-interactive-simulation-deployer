package org.attias.open.interactive.simulation.deployer.tasks;

import org.attias.open.interactive.simulation.core.backend.engine.AppConfiguration;
import org.attias.open.interactive.simulation.deployer.backend.RunnerManager;
import org.attias.open.interactive.simulation.deployer.utils.DeployUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RunAndroidSimulationTask extends DefaultTask {

    private static final Logger log = LoggerFactory.getLogger(RunAndroidSimulationTask.class);

    @TaskAction
    public void runSimulation() throws IOException {
        // Running an app on android is similar to deploying the project.
        // We need to prepare everything so the runner can be used in the android device
        DeployUtils.prepareRunnersForDeployment(getProject());
        log.info("{} Running android simulation", getPath());
        RunnerManager.executeRunOnPlatform(getProject(), RunnerManager.getDeployingEnvVariables(getProject()), AppConfiguration.AppType.Android);
    }
}
