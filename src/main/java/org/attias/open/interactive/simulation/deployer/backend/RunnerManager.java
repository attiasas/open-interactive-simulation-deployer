package org.attias.open.interactive.simulation.deployer.backend;

import org.attias.open.interactive.simulation.core.backend.engine.AppConfiguration;
import org.attias.open.interactive.simulation.core.backend.config.ProjectConfiguration;
import org.attias.open.interactive.simulation.core.utils.IOUtils;
import org.attias.open.interactive.simulation.core.utils.JsonUtils;
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

    public static Map<String, String> getRunningEnvVariables(Project project) {
        Map<String, String> env = new HashMap<>();
        // Add Runners expected environment variables
        env.put(AppConfiguration.ENV_DEBUG_MODE, "true");
        env.put(ProjectConfiguration.ENV_PROJECT_CONFIG_PATH, PluginUtils.getProjectConfigurationPath(project).toString());
        env.put(AppConfiguration.ENV_PROJECT_JAR, PluginUtils.getProjectJar(project).getAbsolutePath());
        env.put(AppConfiguration.ENV_PROJECT_ASSETS_DIR, PluginUtils.getProjectAssetsDirectory(project).toString());
        Path androidSdkPath = ExtensionUtils.getOverrideAndroidSdkPath(project);
        if (androidSdkPath != null) {
            env.put(AppConfiguration.ENV_ANDROID_SDK_PATH, androidSdkPath.toString());
        }
        return env;
    }

    public static Map<String, String> getDeployingEnvVariables(Project project) throws IOException {
        Map<String, String> env = new HashMap<>();
        env.put(AppConfiguration.ENV_PROJECT_NAME, PluginUtils.getProjectPublishName(project));
        env.put(AppConfiguration.ENV_PROJECT_GROUP, project.getGroup().toString());
        env.put(AppConfiguration.ENV_PROJECT_VERSION, project.getVersion().toString());
        env.put(AppConfiguration.ENV_PROJECT_VERSION_NUMBER, PluginUtils.getProjectPublishNumber(project));
        env.put(AppConfiguration.ENV_PROJECT_JAR, PluginUtils.getProjectJar(project).getAbsolutePath());
        return env;
    }

    public static void executeRunOnPlatform(Project project, Map<String, String> envVars, AppConfiguration.AppType platform, boolean oneByOne) throws IOException {
        log.info("Running Env:\n{}", JsonUtils.toJson(envVars));
        envVars.putAll(System.getenv());
        try {
            String[] commands = GradleUtils.getRunningProjectGradleCommands(platform);
            if (!oneByOne) {
                GradleUtils.executeGradleCommand(
                        PluginUtils.getRunnerDirectory(project),
                        envVars,
                        log,
                        commands
                );
                return;
            }
            for (String command : commands) {
                GradleUtils.executeGradleCommand(
                        PluginUtils.getRunnerDirectory(project),
                        envVars,
                        log,
                        command
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deployProject(Project project, AppConfiguration.AppType platform) throws IOException {
        Map<String, String> env = getDeployingEnvVariables(project);
        log.info("Deploying Env:\n{}", JsonUtils.toJson(env));
        // Add other existing
        env.putAll(System.getenv());
        GradleUtils.executeGradleCommand(
                PluginUtils.getRunnerDirectory(project),
                env,
                log,
                GradleUtils.getDeployingProjectGradleCommands(platform)
        );
    }
}
