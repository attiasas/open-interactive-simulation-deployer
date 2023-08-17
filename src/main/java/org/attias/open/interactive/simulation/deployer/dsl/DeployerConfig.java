package org.attias.open.interactive.simulation.deployer.dsl;

public class DeployerConfig {

    private String version;
    private String title;

    private String dynamicClass;

    public String getDynamicClass() {
        return dynamicClass;
    }

    public void setDynamicClass(String dynamicClass) {
        this.dynamicClass = dynamicClass;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
