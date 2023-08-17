package org.attias.open.interactive.simulation.deployer.tasks;

import org.attias.open.interactive.simulation.deployer.backend.Deployer;
import org.attias.open.interactive.simulation.deployer.backend.DeployerManager;
import org.attias.open.interactive.simulation.deployer.dsl.DeployerConfig;
import org.attias.open.interactive.simulation.deployer.dsl.SimulationDeployerExtension;
import org.attias.open.interactive.simulation.deployer.utils.ExtensionUtils;
import org.attias.open.interactive.simulation.deployer.utils.PluginUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitializeDeployerTask extends DefaultTask {

    private static final Logger log = LoggerFactory.getLogger(InitializeDeployerTask.class);

    public static final String NAME = "initializeDeployer";

    @TaskAction
    public void initialize() {
        log.info("{}: Prepare and initialize run environment", getPath());
        // Initialize plugin directory
        if (PluginUtils.createPluginDirectoryIfNotExists()){
            log.info("{}: Created plugin directory", getPath());
        }
        if (PluginUtils.createRunnersDirectoryIfNotExists()){
            log.info("{}: Created runners directory", getPath());
        }
        // Validate runner is ready to be used
        DeployerConfig deployerConfig = ExtensionUtils.getDeployerConfig(getProject());
        DeployerManager deployerManager = new DeployerManager(deployerConfig);
        deployerManager.fetchDeployer();
        log.info("{}: Runner environment {} is ready", getPath(), deployerConfig.getVersion());
    }
}
