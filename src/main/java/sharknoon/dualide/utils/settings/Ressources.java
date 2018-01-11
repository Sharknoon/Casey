/*
 * Copyright 2018 Shark Industries.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sharknoon.dualide.utils.settings;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Josua Frank frank.josua@gmail.com
 */
public class Ressources {

    //used to store e.g. IDE settings
    private static final Path PRIVATE_PATH;
    //used to store e.g. Projects of users
    private static final Path PUBLIC_PATH;
    private static final String PRIVATE_DIR = "user.dir";
    private static final String PUBLIC_DIR = "user.home";
    private static final String PRIVATE_DIR_NAME = ".dualide";
    private static final String PUBLIC_DIR_NAME = "DualIDE";

    static {
        Path privPath = null;
        Path pubPath = null;
        String privateDir = System.getProperty(PRIVATE_DIR);
        String publicDir = System.getProperty(PUBLIC_DIR);
        try {
            privPath = Paths.get(privateDir);
            privPath = privPath.resolve(PRIVATE_DIR_NAME);
            Files.createDirectories(privPath);
        } catch (IllegalArgumentException | FileSystemNotFoundException
                | SecurityException | UnsupportedOperationException | IOException ex) {
            System.err.println("Error during private folder generation \n" + ex);//Logger not yet initialized
        }
        try {
            pubPath = Paths.get(publicDir);
            pubPath = pubPath.resolve(PUBLIC_DIR_NAME);
            Files.createDirectories(pubPath);
        } catch (IllegalArgumentException | FileSystemNotFoundException
                | SecurityException | UnsupportedOperationException | IOException ex) {
            System.err.println("Error during public folder generation \n" + ex);//Logger not yet initialized
        }
        PRIVATE_PATH = privPath;
        PUBLIC_PATH = pubPath;
        if (isFirstStart()) {
            copyRessourcesToRessourceDir();
        }
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
        Optional<Path> file = lookupFile(fileName, privateFile);
        if (file.isPresent()) {
            try {
                if (!Files.exists(file.get())) {
                    return Optional.empty();
                }
            } catch (InvalidPathException ex) {
                Logger.error("File not found: " + fileName, ex);
            } catch (SecurityException ex) {
                Logger.error("Security error: " + fileName, ex);
            }
            return file;
        }
        return Optional.empty();
    }

    public static Optional<InputStream> getFileAsStream(String fileName, boolean privateFile) {
        return getFile(fileName, privateFile)
                .map(file -> {
                    try {
                        return Files.newInputStream(file);
                    } catch (IOException ex) {
                        Logger.warning("Could not open File as Stream: " + fileName, ex);
                        return null;
                    }
                });
    }

    public static Optional<Path> getDirectory(String directoryName, boolean privateDirectory) {
        Optional<Path> dir = lookupDirectory(directoryName, privateDirectory);
        if (dir.isPresent()) {
            try {
                if (!Files.isDirectory(dir.get())) {
                    return Optional.empty();
                }
            } catch (InvalidPathException ex) {
                Logger.error("Directory not found: " + directoryName, ex);
            } catch (SecurityException ex) {
                Logger.error("Security error: " + directoryName, ex);
            }
            return dir;
        }
        return Optional.empty();
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
        Optional<Path> fileSearch = lookupFile(path, privateFile);
        if (!fileSearch.isPresent()) {
            Path file = createFilePath(path, privateFile);
            try {
                Files.createDirectories(file.getParent());
                if (!Files.exists(file)) {
                    Files.createFile(file);
                }
                return true;
            } catch (UnsupportedOperationException | IOException | SecurityException ex) {
                Logger.error("Error creating File " + path, ex);
            }
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
    public static Path createAndGetFile(String path, boolean privateFile) {
        try {
            createFile(path, privateFile);
            return getFile(path, privateFile).orElse(null);
        } catch (InvalidPathException ex) {
            Logger.error("Could not create File " + path, ex);
        }
        return null;
    }

    public static InputStream createAndGetFileAsStream(String path, boolean privateField) {
        Path path2 = createAndGetFile(path, privateField);
        if (path2 == null) {
            return null;
        }
        try {
            return Files.newInputStream(path2);
        } catch (UnsupportedOperationException | IOException | SecurityException ex) {
            Logger.warning("Could not open or create File as Stream: " + path, ex);
        }
        return null;
    }

    /**
     * Deletes a File
     *
     * @param path The file to be deleted
     * @param privateFile
     * @return true if successful, false otherwise
     */
    public static boolean deleteFile(String path, boolean privateFile) {
        Optional<Path> file = lookupFile(path, privateFile);
        if (file.isPresent()) {
            try {
                Files.deleteIfExists(file.get());
                return true;
            } catch (IOException | SecurityException | InvalidPathException ex) {
                Logger.error("Could not delete file: " + path, ex);
            }
        }
        return false;
    }

    /**
     * Returns a list of all Files in the specific Directory
     *
     * @param path Directory of the files
     * @param privateDir
     * @return List of all files in the directory
     */
    public static List<Path> listFiles(String path, boolean privateDir) {
        Optional<Path> dir = lookupDirectory(path, privateDir);
        if (dir.isPresent()) {
            try {
                if (Files.isDirectory(dir.get())) {
                    Stream<Path> list = Files.list(dir.get());
                    return list.collect(Collectors.toList());
                }
            } catch (InvalidPathException | SecurityException | IOException ex) {
                Logger.error("Path not available: " + path, ex);
                return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }
    private static final Map<String, Path> FILE_CACHE = new HashMap<>();
    private static final Map<String, Path> DIR_CACHE = new HashMap<>();

    /**
     *
     * @param file can be a filename or a full path to the file e.g.
     * db/alioth.db
     * @return
     */
    private static Optional<Path> lookupFile(String file, boolean privateFile) {
        Path res = privateFile ? PRIVATE_PATH : PUBLIC_PATH;
        file = clean(file);
        if (FILE_CACHE.containsKey(file)) {
            return Optional.of(FILE_CACHE.get(file));
        }
        try {
            Path path = Paths.get(file);
            Path result = res.resolve(path);
            if (Files.exists(result)) {//Full Path is given
                FILE_CACHE.put(file, result);
                return Optional.of(result);
            } else {//Check in the caller name space directory
                result = res.resolve(getPackageNameOfCaller().replace(".", "/") + "/" + file);
                if (Files.exists(result)) {//Check the caller package
                    FILE_CACHE.put(file, result);
                    return Optional.of(result);
                } else {
                    //search for the file
                    Optional<Path> pathOptional = Files.find(res, 99, (path1, attrs) -> {
                        if (attrs.isRegularFile()) {
                            return path.getFileName().equals(path1.getFileName());
                        }
                        return false;
                    }).findFirst();
                    if (pathOptional.isPresent()) {
                        FILE_CACHE.put(file, pathOptional.get());
                        return pathOptional;
                    }
                }
            }
        } catch (InvalidPathException | IOException ex) {
        }
        return Optional.empty();
    }

    private static Path createFilePath(String fileName, boolean privateFile) {
        Path res = privateFile ? PRIVATE_PATH : PUBLIC_PATH;
        fileName = clean(fileName);
        if (fileName.contains("/")) {
            return res.resolve(Paths.get(fileName));
        } else {
            return res.resolve(getPackageNameOfCaller().replace(".", "/") + "/" + fileName);
        }
    }

    private static Optional<Path> lookupDirectory(String dir, boolean privateDir) {
        Path res = privateDir ? PRIVATE_PATH : PUBLIC_PATH;
        dir = clean(dir);
        if (DIR_CACHE.containsKey(dir)) {
            return Optional.of(DIR_CACHE.get(dir));
        }
        try {
            Path path = Paths.get(dir);
            Path result = res.resolve(path);
            if (Files.isDirectory(result)) {//Full Path is given
                DIR_CACHE.put(dir, result);
                return Optional.of(result);
            } else {//Check in the caller name space directory
                result = res.resolve(getPackageNameOfCaller().replace(".", "/") + "/" + dir);
                if (Files.isDirectory(result)) {//Check the caller package
                    DIR_CACHE.put(dir, result);
                    return Optional.of(result);
                } else {
                    //search for the file
                    Optional<Path> pathOptional = Files.find(res, 99, (path1, attrs) -> {
                        if (attrs.isDirectory()) {
                            return path.getFileName().equals(path1.getFileName());
                        }
                        return false;
                    }).findFirst();
                    if (pathOptional.isPresent()) {
                        DIR_CACHE.put(dir, pathOptional.get());
                        return pathOptional;
                    }
                }
            }
        } catch (InvalidPathException | IOException ex) {
        }
        return Optional.empty();
    }

    private static String clean(String fileOrDirectory) {
        fileOrDirectory = fileOrDirectory.replace("\\", "/");
        while (fileOrDirectory.startsWith("/")) {
            fileOrDirectory = fileOrDirectory.substring(1);
        }
        return fileOrDirectory;
    }

    private static boolean isFirstStart() {
        try {
            if (Files.list(PRIVATE_PATH).findAny().isPresent()) {
                return false;
            }
        } catch (IOException ex) {
            Logger.error("Could not determine, if this is the first start or not", ex);
        }
        return true;
    }

    /**
     * Use this method with caution, it will reset all the ressources like the
     * database or other files. The program is after this method call in the
     * factory state<p>
     * NOT WORKING YET DUE TO WINDOOFS IMPLEMENTATION OF FILE HANDLERS (cant
     * delete and recreate the same file)
     *
     * @param privateRes
     */
    public static void resetRessources(boolean privateRes) {
        deleteRessourcesDir(privateRes);
        if (privateRes) {
            copyRessourcesToRessourceDir();
        }
    }

    private static void deleteRessourcesDir(boolean privateRes) {
        Path toDelete = privateRes ? PRIVATE_PATH : PUBLIC_PATH;
        try {
            Files.walk(toDelete)
                    .sorted(Comparator.reverseOrder())
                    .filter(file -> !file.equals(toDelete))
                    .forEachOrdered(file -> {
                        try {
                            Files.deleteIfExists(file);
                        } catch (IOException ex) {
                            Logger.error("Could not delete file while resetting ressources", ex);
                        }
                    });
        } catch (IOException ex) {
            Logger.error("Could not delete Ressources directory", ex);
        }
    }

    /**
     * copying is only allowed to the private dir
     */
    private static void copyRessourcesToRessourceDir() {
        try {
            URL classesPath = Ressources.class.getProtectionDomain().getCodeSource().getLocation();
            final String rootPackage = Ressources.class.getPackage().getName().substring(0, Ressources.class.getName().indexOf("."));
            if (classesPath.getPath().endsWith("jar")) {//Compiled in a jar
                JarFile jar = new JarFile(new File(classesPath.toURI()));
                jar.stream()
                        .filter(entry -> !entry.getName().endsWith("class"))
                        .filter(entry -> !entry.isDirectory())
                        .filter(entry -> entry.getName().startsWith(rootPackage))
                        .forEach(entry -> {
                            InputStream inputStream = null;
                            try {
                                String name = entry.getName();
                                Path filePath = PRIVATE_PATH.resolve(name);
                                inputStream = jar.getInputStream(entry);
                                Files.createDirectories(filePath.getParent());
                                if (!Files.exists(filePath)) {
                                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                                }
                            } catch (InvalidPathException | IOException | IllegalStateException | UnsupportedOperationException ex) {
                                Logger.error("Could not copy ressources from jar to folder " + PRIVATE_PATH.toString(), ex);
                            } finally {
                                if (inputStream != null) {
                                    try {
                                        inputStream.close();
                                    } catch (IOException ex) {
                                    }
                                }
                            }
                        });
            } else {//Compiled in a folder
                Path classesFolder = Paths.get(classesPath.toURI());
                Files.walk(classesFolder)
                        .filter(entry -> !entry.getFileName().toString().endsWith("class"))
                        .filter(entry -> !Files.isDirectory(entry))
                        .filter(entry -> entry.getFileName().toString().startsWith(rootPackage))
                        .forEach(entry -> {
                            try {
                                String name = classesFolder.relativize(entry).toString();
                                Path filePath = PRIVATE_PATH.resolve(name);
                                Files.createDirectories(filePath.getParent());
                                if (!Files.exists(filePath)) {
                                    Files.copy(entry, filePath);
                                }
                            } catch (IOException ex) {
                                Logger.error("Could not copy ressources from the program folder to folder " + PRIVATE_PATH.toString(), ex);
                            }
                        });
            }
        } catch (SecurityException | IOException | NullPointerException | URISyntaxException ex) {
            Logger.error("Could not get the executing jar", ex);
        }
    }

    public static String getPackageNameOfCaller() {
        StackTraceElement[] stack = new Throwable().getStackTrace();
        for (StackTraceElement elem : stack) {
            try {
                if (!Ressources.class.isAssignableFrom(Class.forName(elem.getClassName()))) {
                    return elem.getClassName().substring(0, elem.getClassName().lastIndexOf("."));
                }
            } catch (ClassNotFoundException ex) {
            }
        }
        return stack.length > 0 ? stack[0].getClass().getPackage().getName() : "";
    }

}
