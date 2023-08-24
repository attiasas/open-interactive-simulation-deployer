package org.attias.open.interactive.simulation.deployer.utils;

import org.attias.open.interactive.simulation.core.backend.config.ProjectConfiguration;
import org.attias.open.interactive.simulation.core.backend.config.PublishConfiguration;
import org.attias.open.interactive.simulation.core.backend.engine.AppConfiguration;
import org.attias.open.interactive.simulation.core.backend.utils.ProjectUtils;
import org.attias.open.interactive.simulation.core.utils.IOUtils;
import org.attias.open.interactive.simulation.deployer.OISException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class DeployUtils {
    private static final Logger log = LoggerFactory.getLogger(DeployUtils.class);

    public static String getRunnerModuleName(AppConfiguration.AppType platform) {
        if (platform.equals(AppConfiguration.AppType.Desktop)) {
            return  "desktop-runner";
        }
        throw new OISException("Unsupported platform type " + platform);
    }

    public static String getOsName() {
        String name = System.getProperty("os.name");
        if (name.toLowerCase().contains("windows")) {
            return "windows";
        }
        return "";
    }

    public static String getDeployedZipFileName(AppConfiguration.AppType platform) {
        String zipName = platform.name();
        if (platform.equals(AppConfiguration.AppType.Desktop)) {
            zipName += "-" + getOsName();
        }
        return zipName + ".zip";
    }

    public static void copyIconsToRunner(ProjectConfiguration configuration, Path iconsDir, ClassLoader defaultIconLoader) throws IOException {
        int[] sizes = ProjectUtils.ICON_SIZES;
        String [] extensions = ProjectUtils.ICON_EXTENSIONS;
        Set<String> foundCombinations = new HashSet<>();

        if (configuration.publish.iconsDir != null) {
            Path iconsSrcDir = Paths.get(configuration.publish.iconsDir);
            if (iconsSrcDir.toFile().exists()) {
                copyCustomIcons(iconsSrcDir, iconsDir, extensions, sizes, foundCombinations);
            }
        }
        // Default
        for (String extension : extensions) {
            for (int size : sizes) {
                if (foundCombinations.contains(extension + size)) {
                    continue;
                }
                String defaultName = "icon" + size + extension;
                log.warn("Using default icon{} for size {}x{} from {}", extension, size, size, defaultName);
                IOUtils.copyFile(defaultIconLoader.getResourceAsStream("icons/" + defaultName), iconsDir.resolve(defaultName), true);
            }
        }
    }

    private static void copyCustomIcons(Path iconsSrcDir, Path iconsDir, String [] extensions, int[] sizes, Set<String> foundCombinations) throws IOException {
        // Custom
        File[] customIcons = iconsSrcDir.toFile().listFiles();
        for (File icon : customIcons) {
            String extension = getIconExtension(icon, extensions);
            if (extension == null || extension.isBlank()) {
                // Not an icon
                continue;
            }
            int dim = getIconDim(icon, sizes);
            if (dim <= 0) {
                // Not an icon
                continue;
            }
            String combination = extension + dim;
            if (foundCombinations.contains(combination)) {
                log.warn("skipping duplicated icon{} for size {}x{} at {}", extension, dim, dim, icon.getAbsolutePath());
                continue;
            }
            if (IOUtils.copyFile(Paths.get(icon.getAbsolutePath()), iconsDir.resolve("icon" + dim + extension), false)) {
                log.info("Using icon{} for size {}x{} from custom location {}", extension, dim, dim, icon.getAbsolutePath());
                foundCombinations.add(combination);
            }
        }
    }

    //
//    public static void copyIconsToRunner(PublishConfiguration.IconsConfigurations configurations, Path iconsDir, ClassLoader defaultIconLoader) throws IOException {
//        boolean windowsCopied = false;
//        boolean linuxCopied = false;
//        boolean macCopied = false;
//
//        // png: 128, 32, 16
//        // ico: 128
//        // icns: 128
//
//
////        Paths.get(configurations.pngIconPath).toFile().
//        // Override
//        if (configurations != null) {
//            if (configurations.icoIconPath != null && !configurations.icoIconPath.isBlank()) {
//                log.info("Copy ico icon from custom location {}", configurations.icoIconPath);
//                windowsCopied = IOUtils.copyFile(Paths.get(configurations.icoIconPath), iconsDir.resolve("icon.ico"), false);
//            }
//            if (configurations.pngIconPath != null && !configurations.pngIconPath.isBlank()) {
//                log.info("Copy png icon from custom location {}", configurations.pngIconPath);
//                linuxCopied = IOUtils.copyFile(Paths.get(configurations.pngIconPath), iconsDir.resolve("icon.png"), false);
//            }
//            if (configurations.icnsIconPath != null && !configurations.icnsIconPath.isBlank()) {
//                log.info("Copy icns icon from custom location {}", configurations.icnsIconPath);
//                macCopied = IOUtils.copyFile(Paths.get(configurations.icnsIconPath), iconsDir.resolve("icon.icns"), false);
//            }
//        }
//        // Defaults
//        if (!windowsCopied) {
//            IOUtils.copyFile(defaultIconLoader.getResourceAsStream("icon.ico"), iconsDir.resolve("icon.ico"), true);
//        }
//        if (!linuxCopied) {
//            IOUtils.copyFile(defaultIconLoader.getResourceAsStream("icon.png"), iconsDir.resolve("icon.png"), true);
//        }
//        if (!macCopied) {
//            IOUtils.copyFile(defaultIconLoader.getResourceAsStream("icon.icns"), iconsDir.resolve("icon.icns"), true);
//        }
//    }

    private static String getIconExtension(File potential, String[] validExtensions) {
        for (String valid : validExtensions) {
            if (potential.getName().endsWith(valid)) {
                return valid;
            }
        }
        return null;
    }

    private static int getIconDim(File potential, int[] validSizes) {
        try {
            BufferedImage image = ImageIO.read(potential);
            if (image == null) {
                // not an icon
                return -1;
            }
            int width = image.getWidth();
            int height = image.getHeight();
            if (width != height) {
                // dims not equal, not valid icon
                return -1;
            }
            for (int valid : validSizes) {
                if (valid == width) {
                    return width;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
