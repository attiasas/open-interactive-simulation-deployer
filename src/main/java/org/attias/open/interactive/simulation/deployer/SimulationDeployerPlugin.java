package org.attias.open.interactive.simulation.deployer;

import org.attias.open.interactive.simulation.core.utils.IOUtils;
import org.attias.open.interactive.simulation.deployer.tasks.InitializeDeployerTask;
import org.attias.open.interactive.simulation.deployer.utils.ExtensionUtils;
import org.attias.open.interactive.simulation.deployer.utils.PluginUtils;
import org.attias.open.interactive.simulation.deployer.utils.TaskUtils;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SimulationDeployerPlugin implements Plugin<Project> {
    private static final Logger log = LoggerFactory.getLogger(SimulationDeployerPlugin.class);

    @Override
    public void apply(Project target) {
        if (!isProjectCompatible(target)) {
            throw new GradleException("Can't apply IOS Deployer Plugin on " + target.getPath());
        }
        ExtensionUtils.createDefaultExtensionIfNotExists(target);
        try {
            // Setup project configuration
            if (PluginUtils.createProjectConfigurationIfNotExists(target)) {
                log.info("Created {} file for this project", "");
            }

            log.debug("Project Configuration loaded:\n{}", IOUtils.toJson(PluginUtils.getProjectConfiguration(target)));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Add plugin tasks
        TaskProvider<InitializeDeployerTask> initTask = TaskUtils.addInitializeDeployerTask(target);
        TaskUtils.addRunDesktopTask(target, initTask);
    }

    public boolean isProjectCompatible(Project project) {
        // TODO: to eliminate the setup stage -> to work see artifactory gradle plugin Project<Setting>
//        project.getBuildscript().getRepositories().add(project.getBuildscript().getRepositories().mavenLocal());
//        project.getBuildscript().getRepositories().add(project.getBuildscript().getRepositories().mavenCentral());
//        project.getBuildscript().getRepositories().add(project.getBuildscript().getRepositories().gradlePluginPortal());
//        project.getBuildscript().getRepositories().add(project.getBuildscript().getRepositories().maven(repo -> {
//            repo.setUrl("https://oss.sonatype.org/content/repositories/snapshots/");
//        }));
//        project.getBuildscript().getRepositories().add(project.getBuildscript().getRepositories().google());

        // Must have gradle installed in PATH
        return true;
    }
}
