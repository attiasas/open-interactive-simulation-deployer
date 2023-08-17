package org.attias.open.interactive.simulation.deployer.dsl;

import groovy.lang.Closure;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.util.internal.ConfigureUtil;

public class SimulationDeployerExtension {
    private final Project project;

    private DeployerConfig deployerConfig;

    public SimulationDeployerExtension(Project project) {
        this.project = project;
    }

    public void deployer(Closure<DeployerConfig> closure) {
        deployer(ConfigureUtil.configureUsing(closure));
    }

    public void deployer(Action<DeployerConfig> publishAction) {
        deployerConfig = new DeployerConfig();
        publishAction.execute(deployerConfig);
    }

    public DeployerConfig getDeployerConfig() {
        return deployerConfig;
    }
}
