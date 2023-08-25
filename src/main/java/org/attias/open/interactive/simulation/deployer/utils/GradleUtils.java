package org.attias.open.interactive.simulation.deployer.utils;

import org.attias.open.interactive.simulation.core.backend.engine.AppConfiguration;
import org.attias.open.interactive.simulation.core.utils.Version;
import org.attias.open.interactive.simulation.deployer.Constant;
import org.attias.open.interactive.simulation.deployer.OISException;
import org.gradle.api.GradleException;
import org.gradle.api.invocation.Gradle;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Map;

public class GradleUtils {

    public static void executeGradleCommand(Path workingDir, Map<String, String> environmentVariables, Logger log, String... gradleCommands) {
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

    public static String[] getRunningProjectGradleCommands(AppConfiguration.AppType platform) {
        String[] gradleCommands = null;
        if (platform.equals(AppConfiguration.AppType.Desktop)) {
            gradleCommands = new String[]{"clean", "run"};
        }
        if (gradleCommands == null) {
            throw new OISException("Unsupported platform type " + platform);
        }
        return gradleCommands;
    }

    public static String[] getDeployingProjectGradleCommands(AppConfiguration.AppType platform) {
        if (platform.equals(AppConfiguration.AppType.Desktop)) {
            return new String[]{"clean", "jpackageImage"};
        }
        throw new GradleException("Unsupported platform type " + platform);
    }

    public static Path[] getGeneratedArtifactsItems(Path buildPath, AppConfiguration.AppType platform) {
        if (platform.equals(AppConfiguration.AppType.Desktop)) {
            return new Path[]{
                    buildPath.resolve("jpackage").resolve("desktop-runner")
            };
        }
        throw new OISException("Unsupported platform type " + platform);
    }

    public static void checkGradleVersionSupported(Gradle gradle) throws GradleException {
        String gradleVersion = gradle.getGradleVersion();
        if (!new Version(gradleVersion).isAtLeast(Constant.MIN_GRADLE_VERSION)) {
            throw new OISException("Can't apply OIS deployer plugin on Gradle version " + gradleVersion + ". Minimum supported Gradle is " + Constant.MIN_GRADLE_VERSION);
        }
    }
}
