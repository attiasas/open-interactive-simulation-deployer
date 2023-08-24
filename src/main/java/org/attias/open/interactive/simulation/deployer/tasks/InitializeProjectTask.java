package org.attias.open.interactive.simulation.deployer.tasks;

import org.attias.open.interactive.simulation.core.backend.config.ProjectConfiguration;
import org.attias.open.interactive.simulation.deployer.utils.PluginUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class InitializeProjectTask extends DefaultTask {
    private static final Logger log = LoggerFactory.getLogger(InitializeProjectTask.class);
    @TaskAction
    public void initOisProjectStructure() throws IOException {
        log.info("Initialize OIS project structure and generated required items if not exists");
        // Setup project configuration
        if (PluginUtils.createProjectConfigurationIfNotExists(getProject())) {
            log.info("Created {} file for this project", ProjectConfiguration.DEFAULT_FILE_NAME);
        }
    }
}
