package org.attias.open.interactive.simulation.deployer.backend;

import org.attias.open.interactive.simulation.deployer.utils.IOUtils;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class Deployer {
    private static final Logger log = LoggerFactory.getLogger(Deployer.class);
    public final String version;
//    public final Path workspaceDirectory = Paths.get("C:\\Users\\Assaf Attias\\code\\test\\test-desktop");
    public final Path workspaceDirectory =   Paths.get("C:\\Users\\Assaf Attias\\code\\OpenInteractiveSimulation\\open-interactive-simulation-runner");
    public final Map<String, String> environmentVariables;

    public Deployer(String version, Path workspaceDirectory, Map<String, String> environmentVariables) {
        this.version = version;
//        this.workspaceDirectory = workspaceDirectory;
        this.environmentVariables = environmentVariables;
    }

    public void runDesktopSimulation() {
        executeGradleCommand("clean", "run");
    }

    public void executeGradleCommand(String... gradleCommands) {
        executeGradleCommand(this.environmentVariables, gradleCommands);
    }

    public void executeGradleCommand(Map<String, String> environmentVariables, String... gradleCommands) {
        try (ProjectConnection connection = GradleConnector.newConnector().forProjectDirectory(workspaceDirectory.toFile()).connect()){
            BuildLauncher launcher = connection.newBuild()
                    .forTasks(gradleCommands);
            // Set environment variables for the task execution
            launcher.setEnvironmentVariables(environmentVariables);
            // Redirect Gradle output to SLF4J logger
            launcher.setStandardOutput(IOUtils.getRedirectOutToLogInfo(log));
            launcher.setStandardError(IOUtils.getRedirectOutToLogErr(log));
            // Run
            launcher.run();
        }
    }
}
