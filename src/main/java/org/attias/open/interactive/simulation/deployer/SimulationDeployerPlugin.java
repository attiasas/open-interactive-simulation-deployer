package org.attias.open.interactive.simulation.deployer;

import org.attias.open.interactive.simulation.deployer.dsl.SimulationDeployerExtension;
import org.attias.open.interactive.simulation.deployer.tasks.InitializeDeployerTask;
import org.attias.open.interactive.simulation.deployer.utils.ExtensionUtils;
import org.attias.open.interactive.simulation.deployer.utils.PluginUtils;
import org.attias.open.interactive.simulation.deployer.utils.TaskUtils;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimulationDeployerPlugin implements Plugin<Project> {
    private static final Logger log = LoggerFactory.getLogger(SimulationDeployerPlugin.class);

    @Override
    public void apply(Project target) {
        if (!isProjectCompatible(target)) {
            throw new GradleException("Can't apply IOS Deployer Plugin on " + target.getPath());
        }
        ExtensionUtils.getPluginExtensionOrCreateDefault(target);
        TaskProvider<InitializeDeployerTask> initTask = TaskUtils.addInitializeDeployerTask(target);
        TaskUtils.addRunDesktopTask(target, initTask);
    }

    public boolean isProjectCompatible(Project project) {
        // not working
//        project.getBuildscript().getRepositories().add(project.getBuildscript().getRepositories().mavenLocal());
//        project.getBuildscript().getRepositories().add(project.getBuildscript().getRepositories().mavenCentral());
//        project.getBuildscript().getRepositories().add(project.getBuildscript().getRepositories().gradlePluginPortal());
//        project.getBuildscript().getRepositories().add(project.getBuildscript().getRepositories().maven(repo -> {
//            repo.setUrl("https://oss.sonatype.org/content/repositories/snapshots/");
//        }));
//        project.getBuildscript().getRepositories().add(project.getBuildscript().getRepositories().google());
        return true;
    }
}
