package org.attias.open.interactive.simulation.deployer.utils;

import org.slf4j.Logger;

import java.io.OutputStream;

public class LogUtils {
    public static OutputStream getRedirectOutToLogInfo(Logger log) {
        return new OutputStream() {
            private StringBuilder buffer = new StringBuilder();
            @Override
            public void write(int b) {
                if (b == '\n') {
                    log.info(buffer.toString());
                    buffer.setLength(0);
                } else {
                    buffer.append((char) b);
                }
            }
        };
    }

    public static OutputStream getRedirectOutToLogErr(Logger log) {
        return new OutputStream() {
            private StringBuilder buffer = new StringBuilder();
            @Override
            public void write(int b) {
                if (b == '\n') {
                    log.error(buffer.toString());
                    buffer.setLength(0);
                } else {
                    buffer.append((char) b);
                }
            }
        };
    }
}
