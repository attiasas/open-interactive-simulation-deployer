package org.attias.open.interactive.simulation.deployer;

import org.gradle.api.Project;

public class SimulationDeployerExtension {
    private final Project project;
    private String configPath;

    public SimulationDeployerExtension(Project project) {
        this.project = project;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String projectConfigPath) {
        this.configPath = projectConfigPath;
    }
}
