package org.attias.open.interactive.simulation.deployer;

import org.attias.open.interactive.simulation.core.backend.engine.AppConfiguration;
import org.attias.open.interactive.simulation.core.backend.utils.ProjectUtils;
import org.attias.open.interactive.simulation.core.utils.Version;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * All the constants in the plugin
 */
public class Constant {

    // The group of the tasks added by the plugin
    public static final String GROUP_NAME = ProjectUtils.OIS;

    // The plugin directory in the user home
    public static final Path HOME_PATH = Paths.get(System.getProperty("user.home"), ProjectUtils.OIS_DIRECTORY_NAME);
    // The directory that cache the runners
    public static final Path RUNNERS_PATH = HOME_PATH.resolve("runners");
    // The version of the runners compatible with the plugin version
    public static final String RUNNER_VERSION = "0.1";
    // Minimum Gradle version to run the plugin
    public static final Version MIN_GRADLE_VERSION = new Version("7.0.0");
    // The default name that will be when simulation.ois is created
    public static final String DEFAULT_PROJECT_TITLE = "OIS Simulation";
    public static final Set<AppConfiguration.AppType> APP_TYPES = new HashSet<>(List.of(AppConfiguration.AppType.Desktop));

    public static final String OIS_RUNNER_GIT_REPO = "https://github.com/attiasas/open-interactive-simulation-runner.git";
    public static final String EXTENSION_NAME = "oisDeployer";

    public static final String CLEAN_TASK_NAME = "cleanRunners";
    public static final String CLEAN_TASK_DESCRIPTION = "Clean all the cached items in OIS environment";

    public static final String INIT_PROJECT_TASK_NAME = "initializeProject";
    public static final String INIT_PROJECT_TASK_DESCRIPTION = "Initialize your project and generate all the needed file with default values to run OIS project";

    public static final String VALIDATE_TASK_NAME = "validateProject";
    public static final String VALIDATE_TASK_DESCRIPTION = "Validate that all the needed project configurations are valid";

    public static final String INIT_TASK_NAME = "initializeDeployer";
    public static final String INIT_TASK_DESCRIPTION = "Initialize the simulation deployer resources before running related tasks";

    public static final String RUN_DESKTOP_TASK_NAME = "runDesktop";
    public static final String RUN_DESKTOP_TASK_DESCRIPTION = "Runs the simulation using desktop";

    public static final String DEPLOY_TASK_NAME = "deployProject";
    public static final String DEPLOY_TASK_DESCRIPTION = "Deploy the project to production files for each platform";
}
