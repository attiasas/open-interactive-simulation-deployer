package org.attias.open.interactive.simulation.deployer.backend;

import org.attias.open.interactive.simulation.core.backend.engine.AppConfiguration;
import org.attias.open.interactive.simulation.core.backend.config.ProjectConfiguration;
import org.attias.open.interactive.simulation.core.utils.IOUtils;
import org.attias.open.interactive.simulation.deployer.Constant;
import org.attias.open.interactive.simulation.deployer.utils.ExtensionUtils;
import org.attias.open.interactive.simulation.deployer.utils.GitUtils;
import org.attias.open.interactive.simulation.deployer.utils.LogUtils;
import org.attias.open.interactive.simulation.deployer.utils.PluginUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class DeployerManager {
    private static final Logger log = LoggerFactory.getLogger(DeployerManager.class);

    public static boolean fetchRunner(String version) throws GitAPIException {
        if (getValidatedRunnerWorkingDir(version) != null) {
            return false;
        }
        Path workingDir = getRunnerWorkingDirPath(version);
        if (IOUtils.createDirIfNotExists(workingDir, true)) {
            log.debug("Created OIS runner directory at {}", workingDir);
        }
        log.info("Fetching OIS runners version {}", version);
        GitUtils.cloneRepoByTag(Constant.OIS_RUNNER_GIT_REPO, version, workingDir.toString());
        return true;
    }

    public static void executeRunOnPlatform(Project project, Set<File> projectJars, AppConfiguration.AppType appType) throws IOException {
        String[] gradleCommands = null;
        if (appType.equals(AppConfiguration.AppType.Desktop)) {
            gradleCommands = new String[]{"clean", "run"};
        }
        if (gradleCommands == null) {
            throw new GradleException("Unknown application type " + appType);
        }
        executeGradleCommand(
                getActualRunnerWorkingDir(project),
                getRunnerEnvVariables(project, projectJars),
                gradleCommands
        );
    }

    public static Path getValidatedRunnerWorkingDir(String version) {
        Path workingDir = getRunnerWorkingDirPath(version);
        return validateRunnerWorkingDir(workingDir) ? workingDir : null;
    }

    public static Map<String, String> getRunnerEnvVariables(Project project, Set<File> projectJars) {
        Map<String, String> env = new HashMap<>();
        // Add Runners expected environment variables
        env.put(AppConfiguration.ENV_PROJECT_JAR, String.valueOf(new ArrayList<>(projectJars).get(0)));

        Path assetsPath = ExtensionUtils.getOverrideAssetsPath(project);
        if (assetsPath != null) {
            env.put(AppConfiguration.ENV_PROJECT_ASSETS_DIR, assetsPath.toString());
        }
        assetsPath = PluginUtils.getProjectDefaultResourcesDirPath(project);
        if (assetsPath != null) {
            env.put(AppConfiguration.ENV_PROJECT_ASSETS_DIR, assetsPath.toString());
        }
        env.put(ProjectConfiguration.ENV_PROJECT_CONFIG_PATH, PluginUtils.getProjectConfigurationPath(project).toString());
        log.info("Runner Env: {}", env);
        // Add other existing
        env.putAll(System.getenv());
        return env;
    }

    private static void executeGradleCommand(Path workingDir, Map<String, String> environmentVariables, String... gradleCommands) {
        try (ProjectConnection connection = GradleConnector.newConnector().forProjectDirectory(workingDir.toFile()).connect()){
            BuildLauncher launcher = connection.newBuild()
                    .forTasks(gradleCommands);
            // Set environment variables for the task execution
            launcher.setEnvironmentVariables(environmentVariables);
            // Redirect Gradle output to SLF4J logger
            launcher.setStandardOutput(LogUtils.getRedirectOutToLogInfo(log));
            launcher.setStandardError(LogUtils.getRedirectOutToLogErr(log));
            // Run
            launcher.run();
        }
    }

    private static Path getActualRunnerWorkingDir(Project project) throws IOException {
        Path workingDir = ExtensionUtils.getOverrideRunnerPath(project);
        if (workingDir != null) {
            return workingDir;
        }
        ProjectConfiguration configuration = PluginUtils.getProjectConfiguration(project);
        if (configuration == null) {
            throw new GradleException("Can't fetch project configurations " + project.getPath());
        }
        workingDir = getValidatedRunnerWorkingDir(configuration.runner.version);
        if (workingDir == null) {
            throw new RuntimeException("Can't find valid runner working directory to run gradle task.");
        }
        return workingDir;
    }

    private static boolean validateRunnerWorkingDir(Path workingDirPath) {
        File dir = workingDirPath.toFile();
        return dir.exists() && dir.isDirectory() && dir.list().length > 0;
    }

    private static Path getRunnerWorkingDirPath(String version) {
        return Constant.RUNNERS_PATH.resolve(version);
    }
}
