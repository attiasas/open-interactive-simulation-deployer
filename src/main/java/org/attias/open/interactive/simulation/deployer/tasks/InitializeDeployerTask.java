package org.attias.open.interactive.simulation.deployer.tasks;

import org.attias.open.interactive.simulation.core.backend.config.ProjectConfiguration;
import org.attias.open.interactive.simulation.core.utils.IOUtils;
import org.attias.open.interactive.simulation.deployer.SimulationDeployerExtension;
import org.attias.open.interactive.simulation.deployer.backend.DeployerManager;
import org.attias.open.interactive.simulation.deployer.utils.ExtensionUtils;
import org.attias.open.interactive.simulation.deployer.utils.PluginUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InitializeDeployerTask extends DefaultTask {

    private static final Logger log = LoggerFactory.getLogger(InitializeDeployerTask.class);

    public static final String NAME = "initializeDeployer";

    @TaskAction
    public void initialize() throws IOException, GitAPIException {
        Project project = getProject();
        String projectPath = getPath();
        log.info("{}: Prepare and initialize run environment", projectPath);
        // Initialize plugin directory
        if (PluginUtils.createPluginDirectoryIfNotExists()){
            log.info("{}: Created plugin directory", projectPath);
        }
        if (PluginUtils.createRunnersDirectoryIfNotExists()){
            log.info("{}: Created runners directory", projectPath);
        }
        // Get project configuration
        log.info("{}: Getting project configurations", projectPath);
        ProjectConfiguration configuration = PluginUtils.getProjectConfiguration(PluginUtils.getProjectConfigurationPath(project));
        // Validate runner is ready to be used
        log.debug("{}: Using runner {}", projectPath, configuration.runner.version);
        if (DeployerManager.fetchRunner(configuration.runner.version)) {
            log.info("{}: Runner {} created from git repository", projectPath, configuration.runner.version);
        }
        Path tempAssetsDirectory = Files.createTempDirectory("ois");
        log.info("{}: Preparing assets at {}", projectPath, tempAssetsDirectory);
        for (String assetDirectory : configuration.runner.assetsDirectories) {
            log.info("{} Copying assets from {}", projectPath, assetDirectory);
            IOUtils.copyDirectoryContent(Paths.get(assetDirectory), tempAssetsDirectory);
        }
        log.info("{}: Run environment is ready", projectPath);
    }
}
