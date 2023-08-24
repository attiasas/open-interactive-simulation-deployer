package org.attias.open.interactive.simulation.deployer.tasks;

import org.attias.open.interactive.simulation.deployer.backend.RunnerManager;
import org.attias.open.interactive.simulation.deployer.utils.PluginUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class InitializeDeployerTask extends DefaultTask {

    private static final Logger log = LoggerFactory.getLogger(InitializeDeployerTask.class);

    @TaskAction
    public void initialize() throws IOException, GitAPIException {
        Project project = getProject();
        String projectPath = getPath();
        log.info("{}: Prepare and initialize run environment", projectPath);
        // Initialize plugin directory
        if (PluginUtils.createPluginDirectoryIfNotExists()){
            log.info("{}: Created plugin directory", projectPath);
        }
        if (PluginUtils.createRunnersDirectoryIfNotExists()) {
            log.info("{}: Creating runners directory", projectPath);
        }
        // Fetch runner if needed
        RunnerManager.fetchRunner(project);
    }
}
