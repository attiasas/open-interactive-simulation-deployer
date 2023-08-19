package org.attias.open.interactive.simulation.deployer.tasks;

import org.attias.open.interactive.simulation.core.backend.engine.AppConfiguration;
import org.attias.open.interactive.simulation.deployer.backend.DeployerManager;
import org.attias.open.interactive.simulation.deployer.utils.ExtensionUtils;
import org.attias.open.interactive.simulation.deployer.utils.PluginUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class RunDesktopSimulationTask extends DefaultTask {
    private static final Logger log = LoggerFactory.getLogger(RunDesktopSimulationTask.class);
    public static final String NAME = "runDesktop";

    @TaskAction
    public void runSimulation() throws IOException {
        log.info("{} Getting project Jar file", getPath());
        Set<File> jarFiles = PluginUtils.getProjectJars(getProject());
        if (jarFiles.isEmpty()) {
            throw new GradleException("Can't find project jars to deploy into the runners");
        }
        for (File jar : jarFiles) {
            log.info("Using Jar: {}", jar.getAbsolutePath());
        }
        log.info("{} Running desktop simulation", getPath());
        DeployerManager.executeRunOnPlatform(getProject(), jarFiles, AppConfiguration.AppType.Desktop);
    }
}
