package org.attias.open.interactive.simulation.deployer.tasks;

import org.attias.open.interactive.simulation.core.backend.config.ProjectConfiguration;
import org.attias.open.interactive.simulation.deployer.OISException;
import org.attias.open.interactive.simulation.deployer.utils.PluginUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ValidateProjectTask extends DefaultTask {
    private static final Logger log = LoggerFactory.getLogger(ValidateProjectTask.class);
    @TaskAction
    public void validate() throws IOException {
        log.info("{}: Validate project configurations", getPath());
        Project project = getProject();
        ProjectConfiguration configuration = PluginUtils.getProjectConfiguration(project);
        if (configuration == null) {
            throw new OISException("You must create a 'simulation.ois' file at: " + project.getProjectDir().getAbsolutePath());
        }
        if (configuration.title == null || configuration.title.isBlank()) {
            throw new OISException("'name' can't be empty");
        }
        if (configuration.initialState == null || configuration.initialState.isBlank() || !configuration.states.containsKey(configuration.initialState)) {
            throw new OISException("You must set the initialState to a valid state key: " + configuration.states.keySet());
        }
        if (configuration.publish.platforms.isEmpty()) {
            throw new OISException("You must define at least one runner platform");
        }
    }

}
