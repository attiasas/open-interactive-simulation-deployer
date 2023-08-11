package org.attias.open.interactive.simulation.deployer.utils;

import org.attias.open.interactive.simulation.deployer.SimulationDeployerExtension;
import org.gradle.api.Project;

public class ExtensionUtils {
    public static SimulationDeployerExtension getPluginExtensionOrCreateDefault(Project project) {
        SimulationDeployerExtension extension = project.getExtensions().findByType(SimulationDeployerExtension.class);
        if (extension == null) {
            extension = project.getExtensions().create("simulationDeployer", SimulationDeployerExtension.class, project);
        }
        return extension;
    }
}
