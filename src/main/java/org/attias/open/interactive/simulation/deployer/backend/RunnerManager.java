package org.attias.open.interactive.simulation.deployer.backend;

import org.attias.open.interactive.simulation.core.backend.engine.AppConfiguration;
import org.attias.open.interactive.simulation.core.backend.config.ProjectConfiguration;
import org.attias.open.interactive.simulation.core.utils.IOUtils;
import org.attias.open.interactive.simulation.deployer.Constant;
import org.attias.open.interactive.simulation.deployer.utils.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.gradle.api.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

/**
 * Connection from plugin to runners
 */
public class RunnerManager {
    private static final Logger log = LoggerFactory.getLogger(RunnerManager.class);

    public static boolean fetchRunner(Project project) throws GitAPIException {
        Path workingDir = PluginUtils.getRunnerDirectory(project);
        if (validateRunnerWorkingDir(workingDir)) {
            return false;
        }
        if (IOUtils.createDirIfNotExists(workingDir, true)) {
            log.debug("Created OIS runner directory at {}", workingDir);
        }
        log.info("Fetching OIS runners version {}", Constant.RUNNER_VERSION);
        GitUtils.cloneRepoByTag(Constant.OIS_RUNNER_GIT_REPO, Constant.RUNNER_VERSION, workingDir.toString());
        return true;
    }

    private static boolean validateRunnerWorkingDir(Path workingDirPath) {
        File dir = workingDirPath.toFile();
        return dir.exists() && dir.isDirectory() && dir.list().length > 0;
    }

    public static void executeRunOnPlatform(Project project, AppConfiguration.AppType platform) {
        try {
            GradleUtils.executeGradleCommand(
                    PluginUtils.getRunnerDirectory(project),
                    getRunningEnvVariables(project),
                    log,
                    GradleUtils.getRunningProjectGradleCommands(platform)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> getRunningEnvVariables(Project project) {
        Map<String, String> env = new HashMap<>();
        // Add Runners expected environment variables
        env.put(AppConfiguration.ENV_PROJECT_JAR, PluginUtils.getProjectJar(project).getAbsolutePath());
        env.put(AppConfiguration.ENV_PROJECT_ASSETS_DIR, PluginUtils.getProjectAssetsDirectory(project).toString());
        env.put(ProjectConfiguration.ENV_PROJECT_CONFIG_PATH, PluginUtils.getProjectConfigurationPath(project).toString());
        log.info("Running Env: {}", env);
        // Add other existing
        env.putAll(System.getenv());
        return env;
    }

    public static void deployProject(Project project, AppConfiguration.AppType platform) throws IOException {
        GradleUtils.executeGradleCommand(
                PluginUtils.getRunnerDirectory(project),
                getDeployingEnvVariables(project),
                log,
                GradleUtils.getDeployingProjectGradleCommands(platform)
        );
    }

    public static Map<String, String> getDeployingEnvVariables(Project project) throws IOException {
        Map<String, String> env = new HashMap<>();
        env.put(AppConfiguration.ENV_PROJECT_JAR, PluginUtils.getProjectJar(project).getAbsolutePath());
        env.put(AppConfiguration.ENV_PROJECT_NAME, PluginUtils.getProjectPublishName(project));
        log.info("Deploying Env: {}", env);
        // Add other existing
        env.putAll(System.getenv());
        return env;
    }
}
