package org.attias.open.interactive.simulation.deployer.utils;

import org.attias.open.interactive.simulation.core.backend.config.ProjectConfiguration;
import org.attias.open.interactive.simulation.core.backend.engine.AppConfiguration;
import org.attias.open.interactive.simulation.core.backend.utils.ProjectUtils;
import org.attias.open.interactive.simulation.core.utils.IOUtils;
import org.attias.open.interactive.simulation.deployer.OISException;
import org.attias.open.interactive.simulation.deployer.backend.IconManager;
import org.gradle.api.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class DeployUtils {
    private static final Logger log = LoggerFactory.getLogger(DeployUtils.class);

    public static ProjectConfiguration prepareRunnersForDeployment(Project project) throws IOException {
        String projectPath = project.getPath();
        log.info("{}: Validating project for deployment", projectPath);
        deployValidations(project);
        ProjectConfiguration configuration = PluginUtils.getProjectConfiguration(project);
        if (configuration.publish.platforms.contains(AppConfiguration.AppType.Android)) {
            prepareAndroidResourcesDirectory(project, configuration);
        }
        log.info("{}: Clean runner asset directory", projectPath);
        IOUtils.deleteDirectoryContent(PluginUtils.getTargetAssetsDirectory(project));
        log.info("{}: Generating runner asset directory for deployment", projectPath);
        generateTargetAssetsDirectory(project, configuration);

        return configuration;
    }

    private static void prepareAndroidResourcesDirectory(Project project, ProjectConfiguration configuration) throws IOException {
        String projectPath = project.getPath();
        String androidModuleName = getRunnerModuleName(AppConfiguration.AppType.Android);
        Path androidResourcesDir = PluginUtils.getRunnerDirectory(project).resolve(androidModuleName).resolve("res");
        // Android Resources preparations
        log.info("{}: Clean Android icons resources directory", projectPath);
        IconManager.cleanAndroidIconsResourceDir(androidResourcesDir);
        log.info("{}: Generating Android icons", project.getPath());
        IconManager.generateTargetAndroidIcons(configuration, androidResourcesDir);
    }

    public static void deployValidations(Project project) {
        if (PluginUtils.getProjectAssetsDirectory(project).resolve(ProjectUtils.OIS).toFile().exists()) {
            throw new OISException("Can't deploy a project with resource directory named '" + ProjectUtils.OIS + "'");
        }
    }

    /**
     * Runner Assets directory structure at deploy:
     * - TargetAssetsDir
     *  - project assets....
     *  - .ois
     *      - simulation.ois
     *      - icons
     *          - icon ... (windows/linux/mac for all dims)
     */
    public static void generateTargetAssetsDirectory(Project project, ProjectConfiguration configuration) throws IOException {
        Path targetAssetsDir = PluginUtils.getTargetAssetsDirectory(project);
        String projectPath = project.getPath();
        // Copy project resources to target
        log.info("{}: Copy project resources to target", projectPath);
        Path projectAssetsDir = PluginUtils.getProjectAssetsDirectory(project);
        IOUtils.copyDirectoryContent(projectAssetsDir, targetAssetsDir);
        // Generate OIS assets needed for deployments at target
        log.info("{}: Generate OIS assets needed for deployments at target", projectPath);
        Path oisAssetsDir = targetAssetsDir.resolve(ProjectUtils.OIS);
        IOUtils.createDirIfNotExists(oisAssetsDir,true);
        // Create project configurations
        log.info("{}: Transfer project configurations", projectPath);
        IOUtils.writeAsJsonFile(configuration, oisAssetsDir.resolve(ProjectConfiguration.DEFAULT_FILE_NAME));
        if (configuration.publish.platforms.contains(AppConfiguration.AppType.Desktop)) {
            // Create icons assets
            log.info("{}: Create icons assets", projectPath);
            Path iconsDir = oisAssetsDir.resolve("icons");
            IOUtils.createDirIfNotExists(iconsDir, true);
            IconManager.generateTargetAssetsIcons(configuration, iconsDir);
        }
    }

    public static String getRunnerModuleName(AppConfiguration.AppType platform) {
        switch (platform) {
            case Desktop -> {
                return "desktop-runner";
            }
            case Android -> {
                return "android-runner";
            }
            default -> throw new OISException("Unsupported platform type " + platform);
        }
    }

    public static String getDeployedZipFileName(AppConfiguration.AppType platform) {
        String zipName = platform.name();
        if (platform.equals(AppConfiguration.AppType.Desktop)) {
            zipName += "-" + ProjectUtils.getCurrentOS().name().toLowerCase();
        }
        return zipName + ".zip";
    }
}
