package org.attias.open.interactive.simulation.deployer;

import org.attias.open.interactive.simulation.core.backend.engine.AppConfiguration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Constant {
    public static final String OIS = "ois";
    public static final String GROUP_NAME = OIS;
    public static final String FILE_SUFFIX = "." + OIS;
    public static final String PROJECT_CONFIG_FILE_NAME = "simulation" + FILE_SUFFIX;

    public static final Path HOME_PATH = Paths.get(System.getProperty("user.home"), FILE_SUFFIX);
    public static final Path RUNNERS_PATH = HOME_PATH.resolve("runners");
    public static final String DEFAULT_RUNNER_VERSION = "0.1";

    public static final String DEFAULT_PROJECT_TITLE = "OIS Simulation";
    public static final Set<AppConfiguration.AppType> APP_TYPES = new HashSet<>(List.of(AppConfiguration.AppType.Desktop));

    public static final String OIS_RUNNER_GIT_REPO = "https://github.com/attiasas/open-interactive-simulation-runner.git";
    public static final String EXTENSION_NAME = "oisDeployer";

    public static final String INIT_TASK_NAME = "initializeDeployer";
    public static final String INIT_TASK_DESCRIPTION = "Initialize the simulation deployer resources before running related tasks";

    public static final String RUN_DESKTOP_TASK_NAME = "runDesktop";
    public static final String RUN_DESKTOP_TASK_DESCRIPTION = "Runs the simulation using desktop";
}
