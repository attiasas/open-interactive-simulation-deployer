package org.attias.open.interactive.simulation.deployer.backend;

import org.attias.open.interactive.simulation.core.engine.AppConfiguration;
import org.attias.open.interactive.simulation.deployer.dsl.DeployerConfig;
import org.attias.open.interactive.simulation.deployer.utils.PluginUtils;
import org.gradle.api.GradleException;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DeployerManager {
    private static final Logger log = LoggerFactory.getLogger(DeployerManager.class);

    private DeployerConfig deployerConfig;

    private Map<String, Deployer> deployerMap;

    public DeployerManager(DeployerConfig deployerConfig) {
        this.deployerConfig = deployerConfig;
//        this.deployerMap = new HashMap<>();
    }

    public void fetchDeployer() {
        Path deployerDirectory = PluginUtils.RUNNERS_PATH.resolve(this.deployerConfig.getVersion());
        if (!deployerDirectory.toFile().exists()) {
            throw new GradleException("Can't find runner directory " + deployerDirectory);
        }
    }

    public Deployer createDeployer(String version, File dynamicJar) {
        Path deployerDirectory = PluginUtils.RUNNERS_PATH.resolve(version);
        if (!deployerDirectory.toFile().exists()) {
            throw new GradleException("Can't find runner directory " + deployerDirectory);
        }
        if (!dynamicJar.exists()) {
            throw new GradleException("Can't find dynamic jar " + dynamicJar.getAbsolutePath());
        }
        return new Deployer(version, deployerDirectory, getDeployEnvVariables(this.deployerConfig, dynamicJar.getAbsolutePath()));
    }

    public Deployer getDeployer(File dynamicJar) {
        return createDeployer(this.deployerConfig.getVersion(), dynamicJar);
    }

    public Map<String, String> getDeployEnvVariables(DeployerConfig deployerConfig, String dynamicJarPath) {
        Map<String, String> env = new HashMap<>();
        env.put(AppConfiguration.ENV_PROJECT_JAR, dynamicJarPath);
        env.put(AppConfiguration.ENV_STATE, deployerConfig.getDynamicClass());

        env.put(AppConfiguration.ENV_TITLE, deployerConfig.getTitle());
        log.info("Env: {}", env);
        env.putAll(System.getenv());
        return env;
    }
}
