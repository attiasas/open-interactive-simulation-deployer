package org.attias.open.interactive.simulation.deployer;

import org.attias.open.interactive.simulation.deployer.utils.ExtensionUtils;
import org.attias.open.interactive.simulation.deployer.utils.TaskUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimulationDeployerPlugin implements Plugin<Project> {
    private static final Logger log = LoggerFactory.getLogger(SimulationDeployerPlugin.class);

    @Override
    public void apply(Project target) {
        SimulationDeployerExtension extension = ExtensionUtils.getPluginExtensionOrCreateDefault(target);
        TaskUtils.addRunSimulationTask(target);
    }
}
