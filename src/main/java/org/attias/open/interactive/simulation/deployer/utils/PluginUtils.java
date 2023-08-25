package org.attias.open.interactive.simulation.deployer.utils;

import org.attias.open.interactive.simulation.core.backend.config.ProjectConfiguration;
import org.attias.open.interactive.simulation.core.backend.utils.ProjectUtils;
import org.attias.open.interactive.simulation.core.utils.IOUtils;
import org.attias.open.interactive.simulation.deployer.Constant;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Set;

public class PluginUtils {
    private static final Logger log = LoggerFactory.getLogger(PluginUtils.class);

    public static boolean createPluginDirectoryIfNotExists() {
        return IOUtils.createDirIfNotExists(Constant.HOME_PATH,true);
    }

    public static boolean createRunnersDirectoryIfNotExists() {
        return IOUtils.createDirIfNotExists(Constant.RUNNERS_PATH, true);
    }

    public static Path getProjectConfigurationPath(Project project) {
        Path override = ExtensionUtils.getOverrideConfigPath(project);
        if (override != null) {
            return override;
        }
        return project.getProjectDir().toPath().resolve(ProjectConfiguration.DEFAULT_FILE_NAME);
    }

    public static Path getRunnerDirectory(Project project) {
        Path override = ExtensionUtils.getOverrideRunnerPath(project);
        if (override != null) {
            return override;
        }
        return Constant.RUNNERS_PATH.resolve(Constant.RUNNER_VERSION);
    }

    public static Path getTargetAssetsDirectory(Project project) {
        return getRunnerDirectory(project).resolve("assets");
    }

    public static Path getProjectAssetsDirectory(Project project) {
        Path override = ExtensionUtils.getOverrideAssetsPath(project);
        if (override != null) {
            return override;
        }
        return getProjectDefaultResourcesDirPath(project);
    }

    public static Path getProjectDefaultResourcesDirPath(Project project) {
        File file = project.file("src" + File.separator + "main" + File.separator + "resources");
        if (file.exists() && file.isDirectory() && file.list().length > 0) {
            return file.toPath();
        }
        return null;
    }

    public static Path getProjectOISLibsPath(Project project) {
        return project.getProjectDir().toPath().resolve("build").resolve(ProjectUtils.OIS);
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
        configuration.title = Constant.DEFAULT_PROJECT_TITLE;
        configuration.initialState = "StateKey";
        configuration.states.put("StateKey", "com.example.IStateClassName");;
        configuration.publish.platforms = Constant.APP_TYPES;
        return configuration;
    }

    public static ProjectConfiguration getProjectConfiguration(Project project) throws IOException {
        return ProjectConfiguration.get(getProjectConfigurationPath(project));
    }

    public static File getProjectJar(Project project) {
        log.info("{} Getting project Jar file", project.getPath());
        Set<File> jarFiles = project.getTasks().getByName("jar").getOutputs().getFiles().getFiles();
        if (jarFiles.isEmpty()) {
            throw new GradleException("Can't find project jars to deploy into the runners");
        }
        for (File jar : jarFiles) {
            log.info("Using Jar: {}", jar.getAbsolutePath());
        }
        return new ArrayList<>(jarFiles).get(0);
    }

    public static String getProjectPublishName(Project project) throws IOException {
        String name = getProjectConfiguration(project).publish.publishedName;
        if (name != null && !name.isBlank()) {
            return name;
        }
        return project.getName();
    }
}
