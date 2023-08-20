package org.attias.open.interactive.simulation.deployer.utils;

import org.attias.open.interactive.simulation.core.backend.config.ProjectConfiguration;
import org.attias.open.interactive.simulation.core.utils.IOUtils;
import org.attias.open.interactive.simulation.deployer.Constant;
import org.gradle.api.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public class PluginUtils {
    private static final Logger log = LoggerFactory.getLogger(PluginUtils.class);

    public static boolean createPluginDirectoryIfNotExists() {
        return IOUtils.createDirIfNotExists(Constant.HOME_PATH,true);
    }

    public static boolean createRunnersDirectoryIfNotExists() {
        return IOUtils.createDirIfNotExists(Constant.RUNNERS_PATH,true);
    }

    public static Path getProjectConfigurationPath(Project project) {
        Path configFile = project.getProjectDir().toPath().resolve(Constant.PROJECT_CONFIG_FILE_NAME);
        // override config file location with extension value if exist
        Path override = ExtensionUtils.getOverrideConfigPath(project);
        if (override != null) {
            configFile = override;
        }
        return configFile;
    }

    public static boolean createProjectConfigurationIfNotExists(Project project) throws IOException {
        Path configPath = getProjectConfigurationPath(project);
        if (configPath.toFile().exists()) {
            return false;
        }
        log.info("Creating default project configurations at {}", configPath);
        IOUtils.writeAsJsonFile(createProjectConfiguration(), configPath);
        return true;
    }

    private static ProjectConfiguration createProjectConfiguration() {
        ProjectConfiguration configuration = new ProjectConfiguration();
        configuration.name = Constant.DEFAULT_PROJECT_TITLE;
        configuration.initialState = "StateKey";
        configuration.states.put("StateKey", "com.example.IStateClassName");
        configuration.runner.version = Constant.DEFAULT_RUNNER_VERSION;
        configuration.runner.types = Constant.APP_TYPES;
        return configuration;
    }

    public static ProjectConfiguration getProjectConfiguration(Project project) throws IOException {
        return getProjectConfiguration(getProjectConfigurationPath(project));
    }

    public static ProjectConfiguration getProjectConfiguration(Path path) throws IOException {
        File file = path.toFile();
        if (file.isDirectory()) {
            return IOUtils.getObjFromJsonFile(path.resolve(Constant.PROJECT_CONFIG_FILE_NAME).toFile(), ProjectConfiguration.class);
        } else {
            return IOUtils.getObjFromJsonFile(file, ProjectConfiguration.class);
        }
    }

    public static Set<File> getProjectJars(Project project) {
        return project.getTasks().getByName("jar").getOutputs().getFiles().getFiles();
    }

    public static Path getProjectDefaultResourcesDirPath(Project project) {
        File file = project.file("src/main/resources");
        if (file.exists() && file.isDirectory() && file.list().length > 0) {
            return file.toPath();
        }
        return null;
    }
}
