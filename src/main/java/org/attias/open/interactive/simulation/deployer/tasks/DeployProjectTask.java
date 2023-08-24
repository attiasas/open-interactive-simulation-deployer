package org.attias.open.interactive.simulation.deployer.tasks;

import org.attias.open.interactive.simulation.core.backend.config.ProjectConfiguration;
import org.attias.open.interactive.simulation.core.backend.engine.AppConfiguration;
import org.attias.open.interactive.simulation.core.backend.utils.ProjectUtils;
import org.attias.open.interactive.simulation.core.utils.IOUtils;
import org.attias.open.interactive.simulation.deployer.OISException;
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
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

/**
 * Deploy project and generated production simulation to activate for each platform
 */
public class DeployProjectTask extends DefaultTask {

    private static final Logger log = LoggerFactory.getLogger(DeployProjectTask.class);

    @TaskAction
    public void deployProject() throws IOException {
        log.info("{}: Starting to deploy project to all configured platforms", getPath());
        // Phase 1: Prepare for deployment
        deployValidations();
        cleanTargetAssetsDirectory();
        ProjectConfiguration configuration = generateTargetAssetsDirectory();
        Path targetLibDirectory = PluginUtils.getProjectOISLibsPath(getProject());
        if (IOUtils.createDirIfNotExists(targetLibDirectory, true)) {
            log.info("{}: Created OIS directory for the deployed artifacts at {}", getPath(), targetLibDirectory);
        }
        // Phase 2: Deploy with runners to all platform
        Set<AppConfiguration.AppType> platforms = configuration.publish.platforms;
        for (AppConfiguration.AppType platform : platforms) {
            deployProjectWithRunner(platform);
        }
        log.info("{}: Deploying OIS project task ended successfully.", getPath());
    }

    private void deployValidations() {
        log.info("{}: Validating project for deployment", getPath());
        if (PluginUtils.getProjectAssetsDirectory(getProject()).resolve(ProjectUtils.OIS_DIRECTORY_NAME).toFile().exists()) {
            throw new OISException("Can't deploy a project with resource directory named '" + ProjectUtils.OIS_DIRECTORY_NAME + "'");
        }
    }

    private void cleanTargetAssetsDirectory() {
        log.info("{}: Clean runner asset directory", getPath());
        IOUtils.deleteDirectoryContent(PluginUtils.getTargetAssetsDirectory(getProject()));
    }

    /**
     * Runner Assets directory structure at deploy:
     * - TargetAssetsDir
     *  - project assets....
     *  - .ois    make sure no ois directory exists by project..
     *      - simulation.ois
     *      - icons
     *          - icon.ico (windows)
     *          - icon.png (linux)
     *          - icon.icns (mac)
     * @return configurations that was saved as 'simulation.ois'
     * @throws IOException
     */
    private ProjectConfiguration generateTargetAssetsDirectory() throws IOException {
        log.info("{}: Generating runner asset directory for deployment", getPath());
        Path targetAssetsDir = PluginUtils.getTargetAssetsDirectory(getProject());
        // Copy project resources to target
        log.info("{}: Copy project resources to target", getPath());
        Path projectAssetsDir = PluginUtils.getProjectAssetsDirectory(getProject());
        IOUtils.copyDirectoryContent(projectAssetsDir, targetAssetsDir);
        // Generate OIS assets needed for deployments at target
        log.info("{}: Generate OIS assets needed for deployments at target", getPath());
        Path oisAssetsDir = targetAssetsDir.resolve(ProjectUtils.OIS_DIRECTORY_NAME);
        IOUtils.createDirIfNotExists(oisAssetsDir,true);
        // Create project configurations
        log.info("{}: Create project configurations", getPath());
        ProjectConfiguration configuration = PluginUtils.getProjectConfiguration(getProject());
        IOUtils.writeAsJsonFile(configuration, oisAssetsDir.resolve(ProjectConfiguration.DEFAULT_FILE_NAME));
        // Create icons assets
        log.info("{}: Create icons assets", getPath());
        Path iconsDir = oisAssetsDir.resolve("icons");
        IOUtils.createDirIfNotExists(iconsDir, true);
        ClassLoader defaultIconsLoader = Thread.currentThread().getContextClassLoader();
        DeployUtils.copyIconsToRunner(configuration, iconsDir, defaultIconsLoader);

        return configuration;
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
