package org.attias.open.interactive.simulation.deployer;

import org.attias.open.interactive.simulation.core.backend.config.ProjectConfiguration;
import org.attias.open.interactive.simulation.core.backend.engine.AppConfiguration;
import org.attias.open.interactive.simulation.core.utils.Version;
import org.attias.open.interactive.simulation.deployer.tasks.InitializeDeployerTask;
import org.attias.open.interactive.simulation.deployer.tasks.ValidateProjectTask;
import org.attias.open.interactive.simulation.deployer.utils.ExtensionUtils;
import org.attias.open.interactive.simulation.deployer.utils.GradleUtils;
import org.attias.open.interactive.simulation.deployer.utils.PluginUtils;
import org.attias.open.interactive.simulation.deployer.utils.TaskUtils;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimulationDeployerPlugin implements Plugin<Project> {
    private static final Logger log = LoggerFactory.getLogger(SimulationDeployerPlugin.class);

    @Override
    public void apply(Project target) {
        validateProjectCompatible(target);
        ExtensionUtils.createDefaultExtensionIfNotExists(target);
        addPluginTasks(target);
    }

    private void validateProjectCompatible(Project target) {
        // Assert Gradle version compatible
        GradleUtils.checkGradleVersionSupported(target.getGradle());
        // Assert Java version compatible
        String javaVersion = JavaVersion.current().toString();
        if (!new Version(javaVersion).isAtLeast(Constant.MIN_JAVA_VERSION)) {
            throw new OISException("Java version 15 or higher is required. (version = " + javaVersion + ")");
        }
    }

    public void addPluginTasks(Project target) {
        // General tasks
        TaskUtils.addInitProjectTask(target);
        TaskUtils.addCleanTask(target);
        TaskProvider<ValidateProjectTask> validationTask = TaskUtils.addValidationTask(target);
        TaskProvider<InitializeDeployerTask> initTask = TaskUtils.addInitializeDeployerTask(target, validationTask);
        // Running & Deploying tasks cross platforms
        try {
            ProjectConfiguration configuration = PluginUtils.getProjectConfiguration(target);
            if (configuration.publish.platforms.contains(AppConfiguration.AppType.Desktop)) {
                TaskUtils.addRunDesktopTask(target, initTask);
            }
            if (configuration.publish.platforms.contains(AppConfiguration.AppType.Android)) {
                TaskUtils.addRunAndroidTask(target, initTask);
            }
            TaskUtils.addDeployTask(target, initTask);
        } catch (Exception e) {
            log.error("Could not find valid {} file at {}\nHint: use '{}' task to generate a valid OIS project.", ProjectConfiguration.DEFAULT_FILE_NAME, PluginUtils.getProjectConfigurationPath(target), Constant.INIT_PROJECT_TASK_NAME);
            e.printStackTrace();
        }
    }
}
