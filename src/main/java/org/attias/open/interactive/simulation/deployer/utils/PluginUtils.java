package org.attias.open.interactive.simulation.deployer.utils;

import org.attias.open.interactive.simulation.core.backend.config.ProjectConfiguration;
import org.attias.open.interactive.simulation.core.utils.IOUtils;
import org.attias.open.interactive.simulation.deployer.SimulationDeployerExtension;
import org.gradle.api.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class PluginUtils {

    private static final Logger log = LoggerFactory.getLogger(PluginUtils.class);

    public static final Path HOME_PATH = Paths.get(System.getProperty("user.home"), ".ois");
    public static final Path RUNNERS_PATH = HOME_PATH.resolve("runners");
    public static final String PROJECT_CONFIG_FILE_NAME = "simulation.ois";

    public static boolean createPluginDirectoryIfNotExists() {
        return IOUtils.createDirIfNotExists(HOME_PATH,true);
    }

    public static boolean createRunnersDirectoryIfNotExists() {
        return IOUtils.createDirIfNotExists(RUNNERS_PATH,true);
    }

    public static Path getProjectConfigurationPath(Project project) {
        Path configFile = project.getProjectDir().toPath().resolve(PROJECT_CONFIG_FILE_NAME);
        // override config file location with extension value if exist
        SimulationDeployerExtension extension = ExtensionUtils.getExtensionWithDeployer(project);
        if (extension != null) {
            configFile = Paths.get(extension.getConfigPath());
        }
        return configFile;
    }

    public static boolean createProjectConfigurationIfNotExists(Project project) throws IOException {
        Path configPath = getProjectConfigurationPath(project);
        if (configPath.toFile().exists()) {
            return false;
        }
        log.info("Creating default project configurations at {}", configPath);
        ProjectConfiguration configuration = new ProjectConfiguration();
        configuration.initialState = "StateKey";
        configuration.states.put("StateKey", "com.example.IStateClassName");
        IOUtils.writeAsJsonFile(configuration, configPath);
        return true;
    }

    public static ProjectConfiguration getProjectConfiguration(Project project) throws IOException {
        return getProjectConfiguration(getProjectConfigurationPath(project));
    }

    public static ProjectConfiguration getProjectConfiguration(Path path) throws IOException {
        File file = path.toFile();
        if (file.isDirectory()) {
            return IOUtils.getObjFromJsonFile(path.resolve(PROJECT_CONFIG_FILE_NAME).toFile(), ProjectConfiguration.class);
        } else {
            return IOUtils.getObjFromJsonFile(file, ProjectConfiguration.class);
        }
    }

    public static Set<File> getProjectJars(Project project) {
        return project.getTasks().getByName("jar").getOutputs().getFiles().getFiles();
    }
}
