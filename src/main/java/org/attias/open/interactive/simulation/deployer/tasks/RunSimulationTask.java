package org.attias.open.interactive.simulation.deployer.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunSimulationTask extends DefaultTask {
    private static final Logger log = LoggerFactory.getLogger(RunSimulationTask.class);
    public static final String NAME = "runSimulation";

    @TaskAction
    public void runSimulation() {
        log.info("hello world {}", getPath());
    }
}
