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

    public static void checkGradleVersionSupported(Gradle gradle) throws GradleException {
        String gradleVersion = gradle.getGradleVersion();
        if (!new Version(gradleVersion).isAtLeast(Constant.MIN_GRADLE_VERSION)) {
            throw new OISException("Can't apply OIS deployer plugin on Gradle version " + gradleVersion + ". Minimum supported Gradle is " + Constant.MIN_GRADLE_VERSION);
        }
    }

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
        switch (platform) {
            case Desktop -> {
                return new String[]{"clean", "run"};
            }
            case Android -> {
                return new String[]{"installDebug", "runAndroid"};
            }
            // HTML:
            // superDev -> intercept log:
            //01:10:32 INFO  Jetty 9.4.24.v20191120 started and listening on port 8080
            //01:10:32 INFO   runs at:
            //01:10:32 INFO    http://localhost:8080/
            default -> throw new OISException("Unsupported platform type " + platform);
        }
    }

    public static String[] getDeployingProjectGradleCommands(AppConfiguration.AppType platform) {
        switch (platform) {
            case Desktop -> {
                return new String[]{"clean", "jpackageImage"};
            }
            case Android -> {
                // will create an unsigned APK file in the android/build/outputs/apk folder. Before you can install or publish this APK, you must sign it. The APK build by the above command is already in release mode, you only need to follow the steps for keytool and jarsigner. You can install this APK file on any Android device that allows installation from unknown sources.
                return new String[]{"assembleRelease"};
            }
            // HTML:
            // dist
            // This will compile your app to Javascript and place the resulting Javascript, HTML and asset files in the html/build/dist/ folder. The contents of this folder have to be served up by a web server, e.g. Apache or Nginx. Just treat the contents like you’d treat any other static HTML/Javascript site. There is no Java or Java Applets involved!
            default -> throw new OISException("Unsupported platform type " + platform);
        }
    }

    public static Path[] getGeneratedArtifactsItems(Path buildPath, AppConfiguration.AppType platform) {
        switch (platform) {
            case Desktop -> {
                return new Path[]{
                        buildPath.resolve("jpackage").resolve("desktop-runner")
                };
            }
            case Android -> {
                // will create an unsigned APK file in the android/build/outputs/apk folder. Before you can install or publish this APK, you must sign it. The APK build by the above command is already in release mode, you only need to follow the steps for keytool and jarsigner. You can install this APK file on any Android device that allows installation from unknown sources.
                return new Path[]{
                        buildPath.resolve("outputs").resolve("apk").resolve("release")
                };
            }
            default -> throw new OISException("Unsupported platform type " + platform);
        }
    }
}
