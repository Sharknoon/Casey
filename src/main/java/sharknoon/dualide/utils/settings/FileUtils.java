/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sharknoon.dualide.utils.settings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import sharknoon.dualide.Main;
import java.util.Iterator;

/**
 *
 * @author Josua Frank frank.josua@gmail.com
 */
public class FileUtils {

    private static Path ressourcePath;

    /**
     * Returns a Path to the requested File. The Files are contained in the
     * ressources folder in the .jar/.war or the plain File System .<br>
     * Divide the Directories with a '/'<br>
     * Dont start the file or directory witrh a '/'
     *
     * @param fileName Name of the requested file
     * @return Path to te requested file
     */
    public static Optional<Path> getFile(String fileName) {
        initRessourcePath();
        if (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }
        Path file;
        try {
            file = ressourcePath.resolve(fileName);
        } catch (Exception ex) {
            Logger.error("File not found: " + fileName, ex);
            return Optional.empty();
        }
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        return Optional.ofNullable(file);
    }

    /**
     * Creates a new File in the ressources Folder under the '/' divided
     * Directory
     *
     * @param path Directory where the new file is created
     */
    public static void createFile(String path) {
        initRessourcePath();
        path = clearPathString(path);
        try {
            Path fileToCreate = ressourcePath.resolve(path);
            Files.createDirectories(fileToCreate.getParent());
            Files.createFile(fileToCreate);
        } catch (InvalidPathException ex) {
            Logger.error("Path " + path + " is invalid", ex);
        } catch (IOException ex) {
            Logger.error("Could not create File: " + path, ex);
        }
    }

    /**
     * Returns the requested File and creates it, if it doesnt exist
     *
     * @param path Directory where the new file is created
     * @return The requested file
     */
    public static Optional<Path> createAndGetFile(String path) {
        initRessourcePath();
        path = clearPathString(path);
        try {
            Path file = ressourcePath.resolve(path);
            if (!Files.exists(file)) {
                createFile(path);
            }
            return getFile(path);
        } catch (InvalidPathException ex) {
            Logger.error("Could not create File " + ex);
        }
        return Optional.empty();
    }

    /**
     * Deletes a File
     *
     * @param path The file to be deleted
     * @return true if successful, false otherwise
     */
    public static boolean deleteFile(String path) {
        initRessourcePath();
        path = clearPathString(path);
        try {
            Path file = ressourcePath.resolve(path);
            Files.deleteIfExists(file);
            return true;
        } catch (IOException | InvalidPathException ex) {
            Logger.error("Could not delete file: " + path, ex);
            return false;
        }
    }

    private static void initRessourcePath() {
        if (ressourcePath == null) {
            try {
                String path = "/props/props.properties";
                URI uri = FileUtils.class.getResource(path).toURI();
                if (uri.getScheme().equals("jar")) {
                    FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                    ressourcePath = fileSystem.getPath(path);
                } else {
                    ressourcePath = Paths.get(uri);
                }
            } catch (URISyntaxException | IOException ex) {
                java.util.logging.Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static String clearPathString(String path) {
        path = path.replace("\\", "/");
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

    /**
     * Returns a list of all Files in the specific Directory
     *
     * @param path Directory of the files
     * @return List of all files in the directory
     */
    public static List<Path> listFiles(String path) {
        initRessourcePath();
        path = clearPathString(path);
        try {
            Path dir = ressourcePath.resolve(path);
            if (Files.isDirectory(dir)) {
                Stream<Path> list = Files.list(dir);
                return list.collect(Collectors.toList());
            }
        } catch (InvalidPathException | IOException ex) {
            Logger.error("Path not available: " + path, ex);
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }
}
