package org.attias.open.interactive.simulation.deployer.tasks;

import org.attias.open.interactive.simulation.deployer.backend.DeployerManager;
import org.attias.open.interactive.simulation.deployer.utils.ExtensionUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class RunDesktopSimulationTask extends DefaultTask {
    private static final Logger log = LoggerFactory.getLogger(RunDesktopSimulationTask.class);
    public static final String NAME = "runDesktop";

    @TaskAction
    public void runSimulation() {
        log.info("{} Getting project Jar file", getPath());
        Set<File> jarFiles = getProject().getTasks().getByName("jar").getOutputs().getFiles().getFiles();
        for (File jar : jarFiles) {
            log.info("Using Jar: {}", jar.getAbsolutePath());
        }
        log.info("{} Running desktop simulation", getPath());
        DeployerManager deployerManager = new DeployerManager(ExtensionUtils.getDeployerConfig(getProject()));
        deployerManager.getDeployer(new ArrayList<>(jarFiles).get(0)).runDesktopSimulation();
    }
}
