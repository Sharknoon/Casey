/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharknoon.dualide.utils.settings;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Josua Frank frank.josua@gmail.com
 */
public class FileUtils {

    //used to store e.g. IDE settings
    private static Path privatePath;
    //used to store e.g. Projects of users
    private static Path publicPath;
    private static final String PRIVATE_DIR = "user.dir";
    private static final String PUBLIC_DIR = "user.home";
    private static final String PRIVATE_DIR_NAME = "settings";
    private static final String PUBLIC_DIR_NAME = "DualIDE";
    private static final String RESSOURCES_TO_BE_COPIED_NAME = "toBeCopied";

    public static void init() {
        if (privatePath == null || publicPath == null) {
            String privateDir = System.getProperty(PRIVATE_DIR);
            String publicDir = System.getProperty(PUBLIC_DIR);
            try {
                privatePath = Paths.get(privateDir);
                privatePath = privatePath.resolve(PRIVATE_DIR_NAME);
                Files.createDirectories(privatePath);
            } catch (IllegalArgumentException | FileSystemNotFoundException
                    | SecurityException | UnsupportedOperationException | IOException ex) {
                System.err.println("Error during private folder generation \n" + ex);//Logger not yet initialized
            }
            try {
                publicPath = Paths.get(publicDir);
                publicPath = publicPath.resolve(PUBLIC_DIR_NAME);
                Files.createDirectories(publicPath);
            } catch (IllegalArgumentException | FileSystemNotFoundException
                    | SecurityException | UnsupportedOperationException | IOException ex) {
                System.err.println("Error during public folder generation \n" + ex);//Logger not yet initialized
            }
        }
        copyRessourcesToPrivateDir();
    }

    /**
     * Returns a Path to the requested File. The Files are contained in the
     * ressources folder in the .jar/.war or the plain File System .<br>
     * Divide the Directories with a '/'<br>
     * Dont start the file or directory witrh a '/'
     *
     * @param fileName Name of the requested file
     * @param privateFile
     * @return Path to te requested file
     */
    public static Optional<Path> getFile(String fileName, boolean privateFile) {
        init();
        fileName = clearPathString(fileName);
        Path file = null;
        try {
            if (privateFile) {
                file = privatePath.resolve(fileName);
            } else {
                file = publicPath.resolve(fileName);
            }
            if (!Files.exists(file)) {
                return Optional.empty();
            }
        } catch (InvalidPathException ex) {
            Logger.error("File not found: " + fileName, ex);
        } catch (SecurityException ex) {
            Logger.error("Security error: " + fileName, ex);
        }
        return Optional.ofNullable(file);
    }

    /**
     * Creates a new File in the ressources Folder under the '/' divided
     * Directory, if it isnt already there, if it is, it does nothing
     *
     * @param path Directory where the new file is created
     * @param privateFile
     * @return
     */
    public static boolean createFile(String path, boolean privateFile) {
        init();
        path = clearPathString(path);
        try {
            Path fileToCreate;
            if (privateFile) {
                fileToCreate = privatePath.resolve(path);
            } else {
                fileToCreate = publicPath.resolve(path);
            }
            Files.createDirectories(fileToCreate.getParent());
            if (!Files.exists(fileToCreate)) {
                Files.createFile(fileToCreate);
            }
            return true;
        } catch (UnsupportedOperationException | IOException | SecurityException ex) {
            Logger.error("Error creating File " + path, ex);
        }
        return false;
    }

    /**
     * Returns the requested File and creates it, if it doesnt exist
     *
     * @param path Directory where the new file is created
     * @param privateFile
     * @return The requested file
     */
    public static Optional<Path> createAndGetFile(String path, boolean privateFile) {
        init();
        path = clearPathString(path);
        try {
            createFile(path, privateFile);
            return getFile(path, privateFile);
        } catch (InvalidPathException ex) {
            Logger.error("Could not create File " + path, ex);
        }
        return Optional.empty();
    }

    /**
     * Deletes a File
     *
     * @param path The file to be deleted
     * @param privateFile
     * @return true if successful, false otherwise
     */
    public static boolean deleteFile(String path, boolean privateFile) {
        init();
        path = clearPathString(path);
        try {
            Path file;
            if (privateFile) {
                file = privatePath.resolve(path);
            } else {
                file = publicPath.resolve(path);
            }
            Files.deleteIfExists(file);
            return true;
        } catch (IOException | SecurityException | InvalidPathException ex) {
            Logger.error("Could not delete file: " + path, ex);
            return false;
        }
    }

    /**
     * Returns a list of all Files in the specific Directory
     *
     * @param path Directory of the files
     * @param privateDir
     * @return List of all files in the directory
     */
    public static List<Path> listFiles(String path, boolean privateDir) {
        init();
        path = clearPathString(path);
        try {
            Path dir;
            if (privateDir) {
                dir = privatePath.resolve(path);
            } else {
                dir = publicPath.resolve(path);
            }
            if (Files.isDirectory(dir)) {
                Stream<Path> list = Files.list(dir);
                return list.collect(Collectors.toList());
            }
        } catch (InvalidPathException | SecurityException | IOException ex) {
            Logger.error("Path not available: " + path, ex);
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    private static String clearPathString(String path) {
        path = path.replace("\\", "/");
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

    private static void copyRessourcesToPrivateDir() {
        try {
            String jarPath = FileUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            JarFile jar = new JarFile(jarPath);
            jar.stream()
                    .filter(entry -> entry.getName().startsWith(RESSOURCES_TO_BE_COPIED_NAME))
                    .filter(entry -> !entry.isDirectory())
                    .forEach(entry -> {
                        try {
                            String nameWithoutFirstFolder = entry.getName().substring(RESSOURCES_TO_BE_COPIED_NAME.length() + 1);
                            Path filePath = privatePath.resolve(nameWithoutFirstFolder);
                            InputStream inputStream = jar.getInputStream(entry);
                            Files.createDirectories(filePath.getParent());
                            if (!Files.exists(filePath)) {
                                Files.copy(inputStream, filePath);
                            }
                        } catch (InvalidPathException | IOException | IllegalStateException | UnsupportedOperationException ex) {
                            Logger.error("Could not copy ressources from jar to folder " + privatePath.toString(), ex);
                        }
                    });
        } catch (SecurityException | IOException | NullPointerException ex) {
            Logger.error("Could not get the executing jar", ex);
        }
    }

}
