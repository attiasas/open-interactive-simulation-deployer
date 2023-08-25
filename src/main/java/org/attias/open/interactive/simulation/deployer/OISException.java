package org.attias.open.interactive.simulation.deployer;

import org.gradle.api.GradleException;

public class OISException extends GradleException {

    public OISException(String message) {
        super(message);
    }

    public OISException(String message, Throwable cause) {
        super(message, cause);
    }
}
