package org.attias.open.interactive.simulation.deployer.tasks;

import org.attias.open.interactive.simulation.core.backend.config.ProjectConfiguration;
import org.attias.open.interactive.simulation.core.backend.engine.AppConfiguration;
import org.attias.open.interactive.simulation.core.utils.IOUtils;
import org.attias.open.interactive.simulation.deployer.backend.RunnerManager;
import org.attias.open.interactive.simulation.deployer.utils.DeployUtils;
import org.attias.open.interactive.simulation.deployer.utils.GradleUtils;
import org.attias.open.interactive.simulation.deployer.utils.PluginUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

/**
 * Deploy project and generated production simulation to activate for each platform
 */
public class DeployProjectTask extends DefaultTask {

    private static final Logger log = LoggerFactory.getLogger(DeployProjectTask.class);

    @TaskAction
    public void deployProject() throws IOException {
        Project project = getProject();
        String projectPath = getPath();
        log.info("{}: Starting to deploy project to all configured platforms", projectPath);
        // Phase 1: Prepare for deployment
        ProjectConfiguration configuration = DeployUtils.prepareRunnerForDeployment(project);
        Path targetLibDirectory = PluginUtils.getProjectOISLibsPath(project);
        if (IOUtils.createDirIfNotExists(targetLibDirectory, true)) {
            log.info("{}: Created OIS directory for the deployed artifacts at {}", projectPath, targetLibDirectory);
        }
        // Phase 2: Deploy with runners to all platform
        Set<AppConfiguration.AppType> platforms = configuration.publish.platforms;
        for (AppConfiguration.AppType platform : platforms) {
            deployProjectWithRunner(platform);
        }
        log.info("{}: Deploying OIS project task ended successfully.", projectPath);
    }

    /**
     * 1. Deploy for platform
     * 2. Zip generated artifacts
     * 3. Copy to OIS lib directory
     * @param platform
     * @throws IOException
     */
    private void deployProjectWithRunner(AppConfiguration.AppType platform) throws IOException {
        log.info("{}: Deploying project to {} platform", getPath(), platform);
        Project project = getProject();
        RunnerManager.deployProject(project, platform);
        log.info("{}: Copy generated artifacts for {} platform", getPath(), platform);
        Path targetLibDir = PluginUtils.getProjectOISLibsPath(getProject());
        String runnerModuleName = DeployUtils.getRunnerModuleName(platform);
        Path runnerBuildDir = PluginUtils.getRunnerDirectory(getProject()).resolve(runnerModuleName).resolve("build");
        String zipName = DeployUtils.getDeployedZipFileName(platform);
        String publishName = PluginUtils.getProjectPublishName(project);
        // Zip artifacts of platform into single archive at target lib dir
        IOUtils.zipItems(targetLibDir.resolve(zipName), file -> {
            // Convert the runner module name to the project name for distribution
            String entryName = file.getName();
            if (entryName.contains(runnerModuleName)) {
                return entryName.replace(runnerModuleName, publishName);
            }
            return entryName;
        }, GradleUtils.getGeneratedArtifactsItems(runnerBuildDir, platform));
    }
}
