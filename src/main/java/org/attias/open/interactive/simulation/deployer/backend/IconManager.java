package org.attias.open.interactive.simulation.deployer.backend;

import org.attias.open.interactive.simulation.core.backend.config.ProjectConfiguration;
import org.attias.open.interactive.simulation.core.backend.utils.ProjectUtils;
import org.attias.open.interactive.simulation.core.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

/**
 * Manage all the icons of the project for running and deploying
 */
public class IconManager {
    private static final Logger log = LoggerFactory.getLogger(IconManager.class);
    // Loader for icons from the plugin resource directory
    private static ClassLoader defaultIconsLoader = Thread.currentThread().getContextClassLoader();
    // All the needed icon extensions for desktop runner
    private static final Set<ProjectUtils.IconExtension> desktopExtensions = new HashSet<>(List.of(ProjectUtils.IconExtension.PNG, ProjectUtils.IconExtension.ICO, ProjectUtils.IconExtension.ICNS));
    // All the needed icons for android runner
    private static final Map<ProjectUtils.IconExtension,List<Map.Entry<Integer, String[]>>> androidIconLocations = getAndroidIconLocations();

    private static Map<ProjectUtils.IconExtension,List<Map.Entry<Integer, String[]>>> getAndroidIconLocations() {
        Map<ProjectUtils.IconExtension,List<Map.Entry<Integer, String[]>>> map = new Hashtable<>();
        List<Map.Entry<Integer, String[]>> pngMap = new ArrayList<>();
        pngMap.add(Map.entry(48, new String[]{"drawable-mdpi", "ic_launcher.png"}));
        pngMap.add(Map.entry(72, new String[]{"drawable-hdpi", "ic_launcher.png"}));
        pngMap.add(Map.entry(96, new String[]{"drawable-xhdpi", "ic_launcher.png"}));
        pngMap.add(Map.entry(144, new String[]{"drawable-xxhdpi", "ic_launcher.png"}));
        pngMap.add(Map.entry(192, new String[]{"drawable-xxxhdpi", "ic_launcher.png"}));
        map.put(ProjectUtils.IconExtension.PNG, pngMap);

        List<Map.Entry<Integer, String[]>> xmlMap = new ArrayList<>();
        xmlMap.add(Map.entry(108, new String[]{"drawable-anydpi-v26", "ic_launcher_foreground.xml"}));
        map.put(ProjectUtils.IconExtension.XML, xmlMap);
        return map;
    }

    public static void cleanAndroidIconsResourceDir(Path androidResourcesDir) {
        for (Map.Entry<Integer, String[]> iconDir : androidIconLocations.get(ProjectUtils.IconExtension.PNG)) {
            IOUtils.deleteDirectoryContent(androidResourcesDir.resolve(iconDir.getValue()[0]));
        }
        File xmlIcon = Paths.get(androidResourcesDir.toString(), androidIconLocations.get(ProjectUtils.IconExtension.XML).get(0).getValue()).toFile();
        if (xmlIcon.exists()) {
            xmlIcon.delete();
        }
    }

    public static void generateTargetAndroidIcons(ProjectConfiguration configuration, Path androidResourcesDir) throws IOException {
        Map<ProjectUtils.IconExtension,List<Map.Entry<Integer, String[]>>> neededIcons = getAndroidIconLocations();
        // Custom & Generate
        if (configuration.publish.iconsDir != null) {
            File customDir = Paths.get(configuration.publish.iconsDir).toFile();
            if (customDir.exists() && customDir.isDirectory()) {
                handleCustomAndroidIcons(configuration, androidResourcesDir, neededIcons);
            }
        }
        // Default
        for (Map.Entry<ProjectUtils.IconExtension,List<Map.Entry<Integer, String[]>>> neededExtensionIcons : neededIcons.entrySet()) {
            ProjectUtils.IconExtension extension = neededExtensionIcons.getKey();
            for (Map.Entry<Integer,String[]> neededIconInfo : neededExtensionIcons.getValue()) {
                Path target = Paths.get(androidResourcesDir.toString(), neededIconInfo.getValue());
                log.warn("Using default android {}x{} icon{}", neededIconInfo.getKey(), neededIconInfo.getKey(), extension.value);
                IOUtils.copyFile(defaultIconsLoader.getResourceAsStream("icons/" + neededIconInfo.getKey() + target.getFileName()), target, true);
            }
        }
    }

    public static void handleCustomAndroidIcons(ProjectConfiguration configuration, Path androidResourcesDir, Map<ProjectUtils.IconExtension,List<Map.Entry<Integer, String[]>>> neededIcons) throws IOException {
        Path customDir = Paths.get(configuration.publish.iconsDir);
        Path biggestCustomPngIcon = null;
        int biggestPngDim = -1;
        // Custom
        for (Map.Entry<ProjectUtils.IconExtension,List<Map.Entry<Integer, String[]>>> neededExtensionIcons : neededIcons.entrySet()) {
            ProjectUtils.IconExtension extension = neededExtensionIcons.getKey();
            for (int i = 0; i < neededExtensionIcons.getValue().size(); i++) {
                Map.Entry<Integer,String[]> neededIconInfo = neededExtensionIcons.getValue().get(i);
                Path target = Paths.get(androidResourcesDir.toString(), neededIconInfo.getValue());
                Path copied = searchCustomIconAndCopy(customDir, extension, neededIconInfo.getKey(), target);
                if (copied != null) {
                    log.info("Using custom android icon{} for size {}x{} from {}", extension.value, neededIconInfo.getKey(), neededIconInfo.getKey(), copied);
                    neededExtensionIcons.getValue().remove(i);
                    i--;
                    if (ProjectUtils.IconExtension.PNG.equals(extension) && neededIconInfo.getKey() > biggestPngDim) {
                        biggestPngDim = neededIconInfo.getKey();
                        biggestCustomPngIcon = copied;
                    }
                }
            }
        }
        
        // Generate
        if (configuration.publish.generateMissingIcons != null && configuration.publish.generateMissingIcons) {
            if (biggestPngDim <= 0) {
                log.warn("Can't find any custom valid icon to generate missing icons in the given Icons directory {}", customDir);
                return;
            }
            List<Map.Entry<Integer, String[]>> neededPngIcons = neededIcons.get(ProjectUtils.IconExtension.PNG);
            for (int i = 0; i < neededPngIcons.size(); i++) {
                Map.Entry<Integer,String[]> neededIconInfo = neededPngIcons.get(i);
                Path target = Paths.get(androidResourcesDir.toString(), neededIconInfo.getValue());
                log.info("Generating custom android png icon to size {}x{}", neededIconInfo.getKey(), neededIconInfo.getKey());
                copyAndResizeIcon(biggestCustomPngIcon, target, neededIconInfo.getKey());
                neededPngIcons.remove(i);
                i--;
            }
        }
    }

    public static void generateTargetAssetsIcons(ProjectConfiguration configuration, Path iconsTargetDir) throws IOException {
        // Prepare needed combinations
        List<Map.Entry<Integer,ProjectUtils.IconExtension>> neededIcons = new ArrayList<>();
        for (ProjectUtils.IconExtension extension : desktopExtensions) {
            for (int dim : ProjectUtils.DESKTOP_ICON_SIZES) {
                neededIcons.add(Map.entry(dim,extension));
            }
        }
        // Custom & Generate
        if (configuration.publish.iconsDir != null) {
            File customDir = Paths.get(configuration.publish.iconsDir).toFile();
            if (customDir.exists() && customDir.isDirectory()) {
                handleCustomAssetsIcons(configuration, iconsTargetDir, neededIcons);
            }
        }
        // Default
        for (Map.Entry<Integer,ProjectUtils.IconExtension> neededIconInfo : neededIcons) {
            String fileName = ProjectUtils.getDesktopIconFileName(neededIconInfo.getValue(), neededIconInfo.getKey());
            log.warn("Using default {} as asset icon", fileName);
            IOUtils.copyFile(defaultIconsLoader.getResourceAsStream("icons/" + fileName), iconsTargetDir.resolve(fileName), true);
        }
    }

    public static void handleCustomAssetsIcons(ProjectConfiguration configuration, Path iconsTargetDir, List<Map.Entry<Integer,ProjectUtils.IconExtension>> neededIcons) throws IOException {
        Path customDir = Paths.get(configuration.publish.iconsDir);
        Path biggestCustomPngIcon = null;
        int biggestPngDim = -1;
        // Custom
        for (int i = 0; i < neededIcons.size(); i++) {
            Map.Entry<Integer,ProjectUtils.IconExtension> neededIconInfo = neededIcons.get(i);
            String fileName = ProjectUtils.getDesktopIconFileName(neededIconInfo.getValue(), neededIconInfo.getKey());
            Path copied = searchCustomIconAndCopy(customDir, neededIconInfo.getValue(), neededIconInfo.getKey(), iconsTargetDir.resolve(fileName));
            if (copied != null) {
                log.info("Using custom assets png icon for size {}x{} from {}", neededIconInfo.getKey(), neededIconInfo.getKey(), copied);
                neededIcons.remove(i);
                i--;
                if (neededIconInfo.getKey() > biggestPngDim) {
                    biggestPngDim = neededIconInfo.getKey();
                    biggestCustomPngIcon = copied;
                }
            }
        }
        // Generate
        if (configuration.publish.generateMissingIcons != null && configuration.publish.generateMissingIcons) {
            if (biggestPngDim <= 0) {
                log.warn("Can't find any custom valid icon to generate missing icons in the given Icons directory {}", customDir);
                return;
            }
            for (int i = 0; i < neededIcons.size(); i++) {
                Map.Entry<Integer,ProjectUtils.IconExtension> neededIconInfo = neededIcons.get(i);
                if (!ProjectUtils.IconExtension.PNG.equals(neededIconInfo.getValue())) {
                    continue;
                }
                String fileName = ProjectUtils.getDesktopIconFileName(neededIconInfo.getValue(), neededIconInfo.getKey());
                log.info("Generating custom android png icon to size {}x{}", neededIconInfo.getKey(), neededIconInfo.getKey());
                copyAndResizeIcon(biggestCustomPngIcon, iconsTargetDir.resolve(fileName), neededIconInfo.getKey());
                neededIcons.remove(i);
                i--;
            }
        }
    }

    private static Path searchCustomIconAndCopy(Path iconsSrcDir, ProjectUtils.IconExtension neededExtension, int neededDim, Path target) throws IOException {
        File[] customIcons = iconsSrcDir.toFile().listFiles();
        if (customIcons == null) {
            return null;
        }
        for (File icon : customIcons) {
            if (neededExtension != getIconExtension(icon)) {
                continue;
            }
            if (!neededExtension.equals(ProjectUtils.IconExtension.XML) && neededDim != getImageDim(icon)) {
                continue;
            }
            if (IOUtils.copyFile(Paths.get(icon.getAbsolutePath()), target, false)) {
                return icon.toPath();
            }
        }
        return null;
    }

    private static ProjectUtils.IconExtension getIconExtension(File potential) {
        for (ProjectUtils.IconExtension extension : ProjectUtils.IconExtension.values()) {
            if (potential.getName().endsWith(extension.value)) {
                return extension;
            }
        }
        return null;
    }

    private static int getImageDim(File potential) {
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
            return width;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static void copyAndResizeIcon(Path source, Path destination, int dim) throws IOException {
        resizeImage(source, destination, dim, dim);
    }

    private static void resizeImage(Path inputImagePath, Path outputImagePath, int newWidth, int newHeight) throws IOException {
        BufferedImage inputImage = ImageIO.read(inputImagePath.toFile());

        BufferedImage outputImage = new BufferedImage(newWidth, newHeight, inputImage.getType());

        Graphics2D graphics2D = outputImage.createGraphics();
        graphics2D.drawImage(inputImage, 0, 0, newWidth, newHeight, null);
        graphics2D.dispose();

        String formatName = outputImagePath.toString().substring(outputImagePath.toString().lastIndexOf(".") + 1);

        ImageIO.write(outputImage, formatName, outputImagePath.toFile());
    }
}
