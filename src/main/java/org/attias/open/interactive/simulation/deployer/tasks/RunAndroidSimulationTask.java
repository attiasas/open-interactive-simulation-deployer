package org.attias.open.interactive.simulation.deployer.tasks;

import org.attias.open.interactive.simulation.core.backend.engine.AppConfiguration;
import org.attias.open.interactive.simulation.deployer.backend.RunnerManager;
import org.attias.open.interactive.simulation.deployer.utils.DeployUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Map;

public class RunAndroidSimulationTask extends DefaultTask {

    private static final Logger log = LoggerFactory.getLogger(RunAndroidSimulationTask.class);

    @TaskAction
    public void runSimulation() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        // Running an app on android is similar to deploying the project.
        // We need to prepare everything so the runner can be used in the android device
        DeployUtils.prepareRunnersForDeployment(getProject());
        log.info("{} Running android simulation", getPath());
        Map<String, String> env = RunnerManager.getDeployingEnvVariables(getProject());
        env.put(AppConfiguration.ENV_DEBUG_MODE, "true");
        // We execute the gradle commands one by one to make sure the debug app installed before running
        RunnerManager.executeRunOnPlatform(getProject(), env, AppConfiguration.AppType.Android, true);
    }
}
