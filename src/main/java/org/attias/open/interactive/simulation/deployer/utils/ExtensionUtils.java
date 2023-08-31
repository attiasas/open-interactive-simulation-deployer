package org.attias.open.interactive.simulation.deployer.utils;

import org.attias.open.interactive.simulation.deployer.Constant;
import org.attias.open.interactive.simulation.deployer.SimulationDeployerExtension;
import org.gradle.api.Project;

import java.io.File;
import java.nio.file.Path;

public class ExtensionUtils {

    public static SimulationDeployerExtension createDefaultExtensionIfNotExists(Project project) {
        SimulationDeployerExtension extension = getProjectExtension(project);
        if (extension == null) {
            extension = project.getExtensions().create(Constant.EXTENSION_NAME, SimulationDeployerExtension.class, project);
        }
        return extension;
    }

    public static SimulationDeployerExtension getProjectExtension(Project project) {
        while (project != null) {
            SimulationDeployerExtension extension = project.getExtensions().findByType(SimulationDeployerExtension.class);
            if (extension != null) {
                return extension;
            }
            project = project.getParent();
        }
        return null;
    }

    public static Path getOverrideConfigPath(Project project) {
        SimulationDeployerExtension extension = getProjectExtension(project);
        String configPath = extension.getConfigPath();
        if (configPath == null) {
            return null;
        }
        File config = new File(configPath);
        return config.exists() && config.isFile() ? config.toPath() : null;
    }

    public static Path getOverrideRunnerPath(Project project) {
        SimulationDeployerExtension extension = getProjectExtension(project);
        String runnerPath = extension.getRunnerPath();
        if (runnerPath == null) {
            return null;
        }
        File runnerDir = new File(runnerPath);
        return runnerDir.exists() && runnerDir.isDirectory() ? runnerDir.toPath() : null;
    }

    public static Path getOverrideAssetsPath(Project project) {
        SimulationDeployerExtension extension = getProjectExtension(project);
        String assetsPath = extension.getAssetsPath();
        if (assetsPath == null) {
            return null;
        }
        File assetsDir = new File(assetsPath);
        if (!assetsDir.exists() || !assetsDir.isDirectory()) {
            return null;
        }
        String[] content = assetsDir.list();
        return content != null && content.length > 0 ? assetsDir.toPath() : null;
    }

    public static Path getOverrideAndroidSdkPath(Project project) {
        SimulationDeployerExtension extension = getProjectExtension(project);
        String sdkPath = extension.getAndroidSdkPath();
        if (sdkPath == null) {
            return null;
        }
        File sdkDir = new File(sdkPath);
        if (!sdkDir.exists() || !sdkDir.isDirectory()) {
            return null;
        }
        String[] content = sdkDir.list();
        return content != null && content.length > 0 ? sdkDir.toPath() : null;
    }
}
