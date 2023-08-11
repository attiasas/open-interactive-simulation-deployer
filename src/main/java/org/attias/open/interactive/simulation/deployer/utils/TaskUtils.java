package org.attias.open.interactive.simulation.deployer.utils;

import org.attias.open.interactive.simulation.deployer.tasks.RunSimulationTask;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.tasks.TaskProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskUtils {
    private static final Logger log = LoggerFactory.getLogger(TaskUtils.class);

    public static <T extends Task> TaskProvider<T> registerTaskInProject(String taskName, Class<T> taskClass, String group, String taskDescription, Project project) {
        log.debug("Configure {} task for project", taskName, project.getPath());
        return project.getTasks().register(taskName, taskClass, task -> {
            task.setDescription(taskDescription);
            task.setGroup(group);
        });
    }

    public static void addRunSimulationTask(Project project) {
        try {
            project.getTasks().named(RunSimulationTask.NAME);
            return;
        } catch (UnknownTaskException e) {
            log.debug("Can't find '" + RunSimulationTask.NAME + "' task registered at the project", e);
        }
        registerTaskInProject(
                RunSimulationTask.NAME,
                RunSimulationTask.class,
                "open-simulation",
                "Runs the simulation using desktop",
                project
        );
    }
}
