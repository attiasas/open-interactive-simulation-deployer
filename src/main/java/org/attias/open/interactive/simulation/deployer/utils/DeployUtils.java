package org.attias.open.interactive.simulation.deployer.utils;

import org.attias.open.interactive.simulation.core.backend.config.ProjectConfiguration;
import org.attias.open.interactive.simulation.core.backend.engine.AppConfiguration;
import org.attias.open.interactive.simulation.core.backend.utils.ProjectUtils;
import org.attias.open.interactive.simulation.core.utils.IOUtils;
import org.attias.open.interactive.simulation.deployer.OISException;
import org.gradle.api.Project;
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

    public static ProjectConfiguration prepareRunnerForDeployment(Project project) throws IOException {
        String projectPath = project.getPath();
        log.info("{}: Validating project for deployment", projectPath);
        DeployUtils.deployValidations(project);
        log.info("{}: Clean runner asset directory", projectPath);
        DeployUtils.cleanTargetAssetsDirectory(project);
        log.info("{}: Generating runner asset directory for deployment", projectPath);
        return DeployUtils.generateTargetAssetsDirectory(project);
    }

    public static void deployValidations(Project project) {
        if (PluginUtils.getProjectAssetsDirectory(project).resolve(ProjectUtils.OIS).toFile().exists()) {
            throw new OISException("Can't deploy a project with resource directory named '" + ProjectUtils.OIS + "'");
        }
    }

    public static void cleanTargetAssetsDirectory(Project project) {
        IOUtils.deleteDirectoryContent(PluginUtils.getTargetAssetsDirectory(project));
    }

    /**
     * Runner Assets directory structure at deploy:
     * - TargetAssetsDir
     *  - project assets....
     *  - .ois
     *      - simulation.ois
     *      - icons
     *          - icon.ico (windows for all dims)
     *          - icon.png (windows/linux for all dims)
     *          - icon.icns (mac for all dims)
     * @return configurations that was saved as 'simulation.ois'
     * @throws IOException
     */
    public static ProjectConfiguration generateTargetAssetsDirectory(Project project) throws IOException {
        Path targetAssetsDir = PluginUtils.getTargetAssetsDirectory(project);
        String projectPath = project.getPath();
        // Copy project resources to target
        log.info("{}: Copy project resources to target", projectPath);
        Path projectAssetsDir = PluginUtils.getProjectAssetsDirectory(project);
        IOUtils.copyDirectoryContent(projectAssetsDir, targetAssetsDir);
        // Generate OIS assets needed for deployments at target
        log.info("{}: Generate OIS assets needed for deployments at target", projectPath);
        Path oisAssetsDir = targetAssetsDir.resolve(ProjectUtils.OIS);
        IOUtils.createDirIfNotExists(oisAssetsDir,true);
        // Create project configurations
        log.info("{}: Create project configurations", projectPath);
        ProjectConfiguration configuration = PluginUtils.getProjectConfiguration(project);
        IOUtils.writeAsJsonFile(configuration, oisAssetsDir.resolve(ProjectConfiguration.DEFAULT_FILE_NAME));
        // Create icons assets
        log.info("{}: Create icons assets", projectPath);
        Path iconsDir = oisAssetsDir.resolve("icons");
        IOUtils.createDirIfNotExists(iconsDir, true);
        ClassLoader defaultIconsLoader = Thread.currentThread().getContextClassLoader();
        DeployUtils.copyIconsToRunner(configuration, iconsDir, defaultIconsLoader);

        return configuration;
    }

    public static String getRunnerModuleName(AppConfiguration.AppType platform) {
        switch (platform) {
            case Desktop -> {
                return "desktop-runner";
            }
            case Android -> {
                return "android-runner";
            }
            default -> throw new OISException("Unsupported platform type " + platform);
        }
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
        // Custom
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
