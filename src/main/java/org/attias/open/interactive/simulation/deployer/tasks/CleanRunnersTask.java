package org.attias.open.interactive.simulation.deployer.tasks;
;
import org.attias.open.interactive.simulation.core.utils.IOUtils;
import org.attias.open.interactive.simulation.deployer.Constant;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CleanRunnersTask extends DefaultTask {

    private static final Logger log = LoggerFactory.getLogger(DefaultTask.class);

    @TaskAction
    public void cleanCache() {
        log.info("{}: Clean OIS cache items", getPath());
        if (IOUtils.deleteDirectoryContent(Constant.RUNNERS_PATH)) {
            log.info("Deleted runners cache directory");
        }
    }
}
