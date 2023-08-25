package org.attias.open.interactive.simulation.deployer;

import org.attias.open.interactive.simulation.core.backend.config.ProjectConfiguration;
import org.attias.open.interactive.simulation.core.backend.engine.AppConfiguration;
import org.attias.open.interactive.simulation.deployer.tasks.InitializeDeployerTask;
import org.attias.open.interactive.simulation.deployer.tasks.ValidateProjectTask;
import org.attias.open.interactive.simulation.deployer.utils.ExtensionUtils;
import org.attias.open.interactive.simulation.deployer.utils.GradleUtils;
import org.attias.open.interactive.simulation.deployer.utils.PluginUtils;
import org.attias.open.interactive.simulation.deployer.utils.TaskUtils;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimulationDeployerPlugin implements Plugin<Project> {
    private static final Logger log = LoggerFactory.getLogger(SimulationDeployerPlugin.class);

    @Override
    public void apply(Project target) {
        if (!isProjectCompatible(target)) {
            throw new GradleException("Can't apply IOS Deployer Plugin on " + target.getPath());
        }
        ExtensionUtils.createDefaultExtensionIfNotExists(target);
        // Add plugin tasks
        TaskUtils.addInitProjectTask(target);
        TaskUtils.addCleanTask(target);
        TaskProvider<ValidateProjectTask> validationTask = TaskUtils.addValidationTask(target);
        TaskProvider<InitializeDeployerTask> initTask = TaskUtils.addInitializeDeployerTask(target, validationTask);

        try {
            ProjectConfiguration configuration = PluginUtils.getProjectConfiguration(target);
            if (configuration.publish.platforms.contains(AppConfiguration.AppType.Desktop)) {
                TaskUtils.addRunDesktopTask(target, initTask);
            }

            TaskUtils.addDeployTask(target, initTask);
        } catch (Exception e) {
            log.error("Could not find valid {} file at {}\nHint: use '{}' task to generate a valid OIS project.", ProjectConfiguration.DEFAULT_FILE_NAME, PluginUtils.getProjectConfigurationPath(target), Constant.INIT_PROJECT_TASK_NAME);
            e.printStackTrace();
        }
    }

    public boolean isProjectCompatible(Project project) {
        // Assert Gradle version compatible
        GradleUtils.checkGradleVersionSupported(project.getGradle());
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
