package org.attias.open.interactive.simulation.deployer.utils;

import org.attias.open.interactive.simulation.deployer.Constant;
import org.attias.open.interactive.simulation.deployer.tasks.*;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.tasks.TaskProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskUtils {
    private static final Logger log = LoggerFactory.getLogger(TaskUtils.class);

    public static <T extends Task> TaskProvider<T> registerTaskInProject(String taskName, Class<T> taskClass, String taskDescription, Project project) {
        log.debug("Configure {} task for project", taskName, project.getPath());
        return project.getTasks().register(taskName, taskClass, task -> {
            task.setDescription(taskDescription);
            task.setGroup(Constant.GROUP_NAME);
        });
    }

    public static void addCleanTask(Project project) {
        try {
            project.getTasks().named(Constant.CLEAN_TASK_NAME, CleanRunnersTask.class);
        } catch (UnknownTaskException e) {
            log.debug("Registering '{}' task to the project {}", Constant.CLEAN_TASK_NAME, project.getPath());
        }
        registerTaskInProject(
                Constant.CLEAN_TASK_NAME,
                CleanRunnersTask.class,
                Constant.CLEAN_TASK_DESCRIPTION,
                project
        );
    }

    public static void addInitProjectTask(Project project) {
        try {
            project.getTasks().named(Constant.INIT_PROJECT_TASK_NAME, InitializeProjectTask.class);
        } catch (UnknownTaskException e) {
            log.debug("Registering '{}' task to the project {}", Constant.INIT_PROJECT_TASK_NAME, project.getPath());
        }
        registerTaskInProject(
                Constant.INIT_PROJECT_TASK_NAME,
                InitializeProjectTask.class,
                Constant.INIT_PROJECT_TASK_DESCRIPTION,
                project
        );
    }

    public static TaskProvider<ValidateProjectTask> addValidationTask(Project project) {
        try {
            return project.getTasks().named(Constant.VALIDATE_TASK_NAME, ValidateProjectTask.class);
        } catch (UnknownTaskException e) {
            log.debug("Registering '{}' task to the project {}", Constant.VALIDATE_TASK_NAME, project.getPath());
        }
        return registerTaskInProject(
                Constant.VALIDATE_TASK_NAME,
                ValidateProjectTask.class,
                Constant.VALIDATE_TASK_DESCRIPTION,
                project
        );
    }

    public static TaskProvider<InitializeDeployerTask> addInitializeDeployerTask(Project project, TaskProvider<ValidateProjectTask> validationTask) {
        try {
            return project.getTasks().named(Constant.INIT_TASK_NAME, InitializeDeployerTask.class);
        } catch (UnknownTaskException e) {
            log.debug("Registering '{}' task to the project {}", Constant.INIT_TASK_NAME, project.getPath());
        }
        TaskProvider<InitializeDeployerTask> task = registerTaskInProject(
                Constant.INIT_TASK_NAME,
                InitializeDeployerTask.class,
                Constant.INIT_TASK_DESCRIPTION,
                project
        );
        task.configure(initializeDeployerTask -> {
            initializeDeployerTask.dependsOn(validationTask);
            initializeDeployerTask.dependsOn(project.getTasks().named("build"));
        });
        return task;
    }

    public static void addRunDesktopTask(Project project, TaskProvider<InitializeDeployerTask> initTask) {
        try {
            project.getTasks().named(Constant.RUN_DESKTOP_TASK_NAME);
            return;
        } catch (UnknownTaskException e) {
            log.debug("Registering '{}' task to the project {}", Constant.RUN_DESKTOP_TASK_NAME, project.getPath());
        }
        registerTaskInProject(
                Constant.RUN_DESKTOP_TASK_NAME,
                RunDesktopSimulationTask.class,
                Constant.RUN_DESKTOP_TASK_DESCRIPTION,
                project
        ).configure(runDesktopSimulationTask -> runDesktopSimulationTask.dependsOn(initTask));
    }

    public static void addDeployTask(Project project, TaskProvider<InitializeDeployerTask> initTask) {
        try {
            project.getTasks().named(Constant.DEPLOY_TASK_NAME);
            return;
        } catch (UnknownTaskException e) {
            log.debug("Registering '{}' task to the project {}", Constant.DEPLOY_TASK_NAME, project.getPath());
        }
        registerTaskInProject(
                Constant.DEPLOY_TASK_NAME,
                DeployProjectTask.class,
                Constant.DEPLOY_TASK_DESCRIPTION,
                project
        ).configure(deployProjectTask -> deployProjectTask.dependsOn(initTask));
    }
}
