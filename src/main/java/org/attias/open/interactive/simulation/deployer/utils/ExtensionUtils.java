package org.attias.open.interactive.simulation.deployer.utils;

import org.attias.open.interactive.simulation.deployer.dsl.DeployerConfig;
import org.attias.open.interactive.simulation.deployer.dsl.SimulationDeployerExtension;
import org.gradle.api.GradleException;
import org.gradle.api.Project;

public class ExtensionUtils {

    public static final String EXTENSION_NAME = "simulationDeployer";

    public static SimulationDeployerExtension getPluginExtensionOrCreateDefault(Project project) {
        SimulationDeployerExtension extension = project.getExtensions().findByType(SimulationDeployerExtension.class);
        if (extension == null) {
            extension = project.getExtensions().create(EXTENSION_NAME, SimulationDeployerExtension.class, project);
        }
        return extension;
    }

    public static SimulationDeployerExtension getExtensionWithDeployer(Project project) {
        while (project != null) {
            SimulationDeployerExtension extension = project.getExtensions().findByType(SimulationDeployerExtension.class);
            if (extension != null) {
                return extension;
            }
            project = project.getParent();
        }
        return null;
    }

    public static DeployerConfig getDeployerConfig(Project project) {
        SimulationDeployerExtension extension = getExtensionWithDeployer(project);
        if (extension != null) {
            DeployerConfig config = extension.getDeployerConfig();
            if (config.getTitle() != null && !config.getTitle().isBlank() && config.getDynamicClass() != null && !config.getDynamicClass().isBlank()) {
                return config;
            }
        }
        throw new GradleException("IOS Projects must define deployer config");

    }
}
