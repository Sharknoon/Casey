package sharknoon.casey.compiler.java.compiler;/*
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

import sharknoon.casey.compiler.general.beans.CLIArgs;

import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class JavaCompiler {
    
    public static boolean compile(CLIArgs args) {
        try {
            String classpath = args.getBasePath().toAbsolutePath().toString();
            String mainClassJavaFile = args.getFunctionPath().toAbsolutePath().toString();
            return runOnJavaCompiler(classpath, mainClassJavaFile) == 0;
        } catch (Exception e) {
            System.err.println("Compiler error: " + e);
        }
        System.err.println("Could not find a Compiler, please install the Java-JDK 10 or newer");
        return false;
    }
    
    private static int runOnJavaCompiler(String classPath, String mainClassJavaFile) {
        //First try, find javac and execute it
        Optional<Path> optionalJavacPath = getJavacPath();
        if (optionalJavacPath.isPresent()) {
            try {
                String javacPath = optionalJavacPath.get().toAbsolutePath().toString();
                ProcessBuilder builder = new ProcessBuilder(javacPath, "-cp", classPath, mainClassJavaFile);
                Process process = builder.start();
                process.waitFor();
                return process.exitValue();
            } catch (Exception e) {
                System.err.println("Compiler error: " + e);
            }
        }
        //Second try, use the integrated compiler tools
        javax.tools.JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler != null) {
            return compiler.run(null, null, null, "-cp", classPath, mainClassJavaFile);
        }
        return -1;
    }
    
    
    public static Optional<Path> getJavacPath() {
        try {
            Optional<Path> jdkPath = checkJavaHomeProperty()
                    .or(JavaCompiler::checkJavaHomeEnvironmentVariable)
                    .or(JavaCompiler::checkProgramFilesEnvironmentVariable)
                    .or(JavaCompiler::checkPathEnvironmentVariable);
            if (jdkPath.isPresent()) {
                Path javacPath = jdkPath.get().resolve("bin").resolve("javac");
                if (Files.isRegularFile(javacPath)) {
                    return Optional.of(javacPath);
                }
            }
        } catch (Exception e) {
            System.err.println("Could not find Java-Compiler " + e);
            return Optional.empty();
        }
        return Optional.empty();
    }
    
    private static Optional<Path> checkJavaHomeProperty() {
        return checkJDKDirectory(System.getProperty("java.home"));
    }
    
    private static Optional<Path> checkJavaHomeEnvironmentVariable() {
        return checkJDKDirectory(System.getenv("JAVA_HOME"));
    }
    
    private static Optional<Path> checkProgramFilesEnvironmentVariable() {
        return checkJDKDirectories(System.getenv("ProgramW6432") + "\\Java");
    }
    
    private static Optional<Path> checkPathEnvironmentVariable() {
        String paths = System.getenv("Path");
        if (paths == null) {
            return Optional.empty();
        }
        return Arrays.stream(paths.split(";"))
                .filter(JavaCompiler::scanFile)
                .map(Paths::get)
                .map(Path::getParent)
                .filter(Objects::nonNull)
                .findAny();
    }
    
    private static Optional<Path> checkJDKDirectory(String jdkDirectory) {
        try {
            if (jdkDirectory != null && !jdkDirectory.isEmpty()) {
                Path javaPath = Paths.get(jdkDirectory);
                if (scanFile(javaPath)) {
                    return Optional.of(javaPath);
                } else {
                    Path parent = javaPath.getParent();
                    return checkJDKDirectories(parent);
                }
            }
        } catch (Exception e) {
            System.err.println("Could not checkJDKDirectory for the JDK " + e);
        }
        return Optional.empty();
    }
    
    private static Optional<Path> checkJDKDirectories(String parentJdkDirectory) {
        try {
            if (parentJdkDirectory != null && !parentJdkDirectory.isEmpty()) {
                Path javaPath = Paths.get(parentJdkDirectory);
                return checkJDKDirectories(javaPath);
            }
        } catch (Exception e) {
            System.err.println("Could not checkJDKDirectory for the JDK " + e);
        }
        return Optional.empty();
    }
    
    private static Optional<Path> checkJDKDirectories(Path parentJdkDirectory) {
        if (parentJdkDirectory == null) {
            return Optional.empty();
        }
        return scanDirectory(parentJdkDirectory);
    }
    
    private static Optional<Path> scanDirectory(Path directoryToScan) {
        if (!Files.isDirectory(directoryToScan)) {
            return Optional.empty();
        }
        try {
            return Files.list(directoryToScan)
                    .filter(JavaCompiler::scanFile)
                    .findAny();
        } catch (IOException e) {
            System.err.println("Could not list files of: " + directoryToScan);
            return Optional.empty();
        }
    }
    
    private static boolean scanFile(Path fileToScan) {
        if (!Files.isRegularFile(fileToScan)) {
            return false;
        }
        String fileName = fileToScan.getFileName().toString();
        return scanFile(fileName);
    }
    
    private static boolean scanFile(String fileNameToScan) {
        return fileNameToScan != null && fileNameToScan.matches(".*jdk.*[1-9][0-9]{1,2}.*");
    }
    
}
