package org.attias.open.interactive.simulation.deployer;

public class OISException extends RuntimeException {

    public OISException(String message) {
        super(message);
    }

    public OISException(String message, Throwable cause) {
        super(message, cause);
    }
}
