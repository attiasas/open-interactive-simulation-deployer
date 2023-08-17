package org.attias.open.interactive.simulation.deployer.utils;

import org.attias.open.interactive.simulation.deployer.dsl.DeployerConfig;
import org.attias.open.interactive.simulation.deployer.dsl.SimulationDeployerExtension;
import org.gradle.api.Project;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PluginUtils {

    public static final Path HOME_PATH = Paths.get(System.getProperty("user.home"), ".ios");
    public static final Path RUNNERS_PATH = HOME_PATH.resolve("runners");

    public static boolean createPluginDirectoryIfNotExists() {
        return IOUtils.createDirIfNotExists(HOME_PATH,true);
    }

    public static boolean createRunnersDirectoryIfNotExists() {
        return IOUtils.createDirIfNotExists(RUNNERS_PATH,true);
    }
}
