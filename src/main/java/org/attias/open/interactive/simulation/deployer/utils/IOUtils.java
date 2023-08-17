package org.attias.open.interactive.simulation.deployer.utils;

import org.slf4j.Logger;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Path;

public class IOUtils {
    public static boolean createDirIfNotExists(Path dirPath, boolean failIfCantCreate) {
        File dir = dirPath.toFile();
        if (!dir.exists()) {
            boolean created = dir.mkdir();
            if (!created && failIfCantCreate) {
                throw new RuntimeException("Can't create directory at " + dirPath);
            }
            return created;
        }
        return false;
    }

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
