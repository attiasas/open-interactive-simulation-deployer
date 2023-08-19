package org.attias.open.interactive.simulation.deployer.backend;

import org.attias.open.interactive.simulation.core.backend.engine.AppConfiguration;
import org.attias.open.interactive.simulation.core.backend.config.ProjectConfiguration;
import org.attias.open.interactive.simulation.core.utils.IOUtils;
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
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class DeployerManager {
    private static final Logger log = LoggerFactory.getLogger(DeployerManager.class);
    // version -> runner path
    private static Map<String, Path> runnersWorkingDir = new Hashtable<>();

    public static boolean fetchRunner(String version) throws GitAPIException {
        Path runnerWorkingDir = getRunnerWorkingDirPath(version);

        boolean fetch = IOUtils.createDirIfNotExists(runnerWorkingDir,true);
        runnersWorkingDir.put(version, runnerWorkingDir);
        if (fetch) {
//            log.info("Fetching OIS runners version {}", this.deployerConfig.getVersion());
//            GitUtils.cloneRepoByTag(GitUtils.OIS_RUNNER_GIT_REPO,this.deployerConfig.getVersion(),deployerDirectory.toString());
            runnersWorkingDir.put(version, runnerWorkingDir);
            throw new GradleException("Can't find validated runner in directory " + runnerWorkingDir);
        }
        return fetch;
    }

    public static void executeRunOnPlatform(Project project, Set<File> projectJars, AppConfiguration.AppType appType) throws IOException {
        ProjectConfiguration configuration = PluginUtils.getProjectConfiguration(PluginUtils.getProjectConfigurationPath(project));
        if (configuration == null) {
            throw new GradleException("Can't fetch project configurations " + project.getPath());
        }
        String[] gradleCommands = null;
        if (appType.equals(AppConfiguration.AppType.Desktop)) {
            gradleCommands = new String[]{"clean", "run"};
        }
        if (gradleCommands == null) {
            throw new GradleException("Unknown application type " + appType);
        }
        executeGradleCommand(configuration.runner.version, getRunnerEnvVariables(project, projectJars), gradleCommands);
    }

    public static Map<String, String> getRunnerEnvVariables(Project project, Set<File> projectJars) {
        Map<String, String> env = new HashMap<>();
        // Add Runners expected environment variables
        env.put(AppConfiguration.ENV_PROJECT_JAR, String.valueOf(new ArrayList<>(projectJars).get(0)));
        env.put(ProjectConfiguration.ENV_PROJECT_CONFIG_PATH, PluginUtils.getProjectConfigurationPath(project).toString());
        log.info("Runner Env: {}", env);
        // Add other existing
        env.putAll(System.getenv());
        return env;
    }

    private static void executeGradleCommand(String runnerVersion, Map<String, String> environmentVariables, String... gradleCommands) {
        Path workingDir = getValidatedRunnerWorkingDir(runnerVersion);
        workingDir = Paths.get("C:\\Users\\Assaf Attias\\code\\OpenInteractiveSimulation\\open-interactive-simulation-runner");
        if (workingDir == null) {
            throw new RuntimeException("Before executing gradle task on runner " + runnerVersion + " fetch it");
        }
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

    private static boolean validateRunnerWorkingDir(Path workingDir) {
        return workingDir.toFile().exists();
    }

    public static Path getRunnerWorkingDirPath(String version) {
        return PluginUtils.RUNNERS_PATH.resolve(version);
    }

    public static Path getValidatedRunnerWorkingDir(String version) {
        Path workingDir = getRunnerWorkingDirPath(version);
        if (!validateRunnerWorkingDir(workingDir)) {
            runnersWorkingDir.remove(version);
            return null;
        }
        runnersWorkingDir.put(version, workingDir);
        return workingDir;
    }
}
