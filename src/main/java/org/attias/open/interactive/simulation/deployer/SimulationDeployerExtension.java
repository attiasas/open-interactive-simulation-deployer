package org.attias.open.interactive.simulation.deployer;

import org.gradle.api.Project;

public class SimulationDeployerExtension {
    private final Project project;
    private String configPath;
    private String runnerPath;
    private String assetsPath;
    private String androidSdkPath;

    public SimulationDeployerExtension(Project project) {
        this.project = project;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String projectConfigPath) {
        this.configPath = projectConfigPath;
    }

    public String getRunnerPath() {
        return runnerPath;
    }

    public void setRunnerPath(String runnerPath) {
        this.runnerPath = runnerPath;
    }

    public String getAssetsPath() {
        return assetsPath;
    }

    public void setAssetsPath(String assetsPath) {
        this.assetsPath = assetsPath;
    }

    public String getAndroidSdkPath() {
        return androidSdkPath;
    }

    public void setAndroidSdkPath(String androidSdkPath) {
        this.androidSdkPath = androidSdkPath;
    }
}
