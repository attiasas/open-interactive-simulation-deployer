package org.attias.open.interactive.simulation.deployer.utils;

import org.attias.open.interactive.simulation.deployer.tasks.InitializeDeployerTask;
import org.attias.open.interactive.simulation.deployer.tasks.RunDesktopSimulationTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.tasks.TaskProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskUtils {
    private static final Logger log = LoggerFactory.getLogger(TaskUtils.class);
    private static final String GROUP_NAME = "open-simulation";

    public static <T extends Task> TaskProvider<T> registerTaskInProject(String taskName, Class<T> taskClass, String taskDescription, Project project) {
        log.debug("Configure {} task for project", taskName, project.getPath());
        return project.getTasks().register(taskName, taskClass, task -> {
            task.setDescription(taskDescription);
            task.setGroup(GROUP_NAME);
        });
    }

    public static TaskProvider<InitializeDeployerTask> addInitializeDeployerTask(Project project) {
        try {
            return project.getTasks().named(RunDesktopSimulationTask.NAME, InitializeDeployerTask.class);
        } catch (UnknownTaskException e) {
            log.debug("Registering '{}' task to the project {}", RunDesktopSimulationTask.NAME, project.getPath());
        }
        TaskProvider<InitializeDeployerTask> task = registerTaskInProject(
                InitializeDeployerTask.NAME,
                InitializeDeployerTask.class,
                "Initialize the simulation deployer resources before running related tasks",
                project
        );
        task.configure(initializeDeployerTask -> initializeDeployerTask.dependsOn(project.getTasks().named("build")));
        return task;
    }

    public static void addRunDesktopTask(Project project, TaskProvider<InitializeDeployerTask> initTask) {
        try {
            project.getTasks().named(RunDesktopSimulationTask.NAME);
            return;
        } catch (UnknownTaskException e) {
            log.debug("Registering '{}' task to the project {}", RunDesktopSimulationTask.NAME, project.getPath());
        }
        registerTaskInProject(
                RunDesktopSimulationTask.NAME,
                RunDesktopSimulationTask.class,
                "Runs the simulation using desktop",
                project
        ).configure(runDesktopSimulationTask -> runDesktopSimulationTask.dependsOn(initTask));
    }
}
