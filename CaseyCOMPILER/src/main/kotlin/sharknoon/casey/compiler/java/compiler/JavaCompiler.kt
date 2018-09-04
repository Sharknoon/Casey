package sharknoon.casey.compiler.java.compiler

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
 * See the License for the specific languageString governing permissions and
 * limitations under the License.
 */

import sharknoon.casey.compiler.general.cli.CLIArgs
import java.io.IOException
import java.nio.file.*
import javax.tools.ToolProvider

private val javacPath: Path? =
        try {
            val jdkPath = checkJavaHomeProperty()
                    ?: checkJavaHomeEnvironmentVariable()
                    ?: checkProgramFilesEnvironmentVariable()
                    ?: checkPathEnvironmentVariable()
            if (jdkPath != null) {
                val javacPath = jdkPath.resolve("bin").resolve("javac.exe")
                if (Files.exists(javacPath)) {
                    javacPath
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            System.err.println("Could not find Java-Compiler $e")
            null
        }


fun compile(args: CLIArgs): Boolean {
    try {
        val classpath = args.basePath.toAbsolutePath().toString()
        val mainClassJavaFile = args.functionPath.toAbsolutePath().toString()
        return runOnJavaCompiler(classpath, mainClassJavaFile) == 0
    } catch (e: Exception) {
        System.err.println("Compiler error: $e")
        e.printStackTrace(System.err)
    }

    System.err.println("Could not find a Compiler, please install the Java-JDK 10 or newer")
    return false
}

private fun runOnJavaCompiler(classPath: String, mainClassJavaFile: String): Int {
    //First try, find javac and execute it
    if (javacPath != null) {
        try {
            val javacPath = javacPath.toAbsolutePath().toString()
            val builder = ProcessBuilder(javacPath, "-cp", classPath, mainClassJavaFile)
            val process = builder.start()
            process.waitFor()
            return process.exitValue()
        } catch (e: Exception) {
            System.err.println("Compiler error: $e")
        }

    }
    //Second try, use the integrated compiler tools
    val compiler = ToolProvider.getSystemJavaCompiler()
    return compiler?.run(null, null, null, "-cp", classPath, mainClassJavaFile) ?: -1
}

private fun checkJavaHomeProperty(): Path? {
    return checkJDKDirectory(System.getProperty("java.home"))
}

private fun checkJavaHomeEnvironmentVariable(): Path? {
    return checkJDKDirectory(System.getenv("JAVA_HOME"))
}

private fun checkProgramFilesEnvironmentVariable(): Path? {
    return checkJDKDirectories(System.getenv("ProgramW6432") + "\\Java")
}

private fun checkPathEnvironmentVariable(): Path? {
    val paths = System.getenv("Path") ?: return null
    return paths
            .split(';')
            .filter { checkJDKDirectoryName(it) }
            .map { Paths.get(it) }
            .mapNotNull { it.parent }
            .firstOrNull()
}

private fun checkJDKDirectory(jdkDirectory: String = ""): Path? {
    try {
        if (!jdkDirectory.isEmpty()) {
            val javaPath = Paths.get(jdkDirectory)
            return if (checkJDKDirectoryName(javaPath)) {
                javaPath
            } else {
                val parent = javaPath.parent
                checkJDKDirectories(parent)
            }
        }
    } catch (e: Exception) {
        System.err.println("Could not check JDKDirectory for the JDK $e")
    }

    return null
}

private fun checkJDKDirectories(parentJdkDirectory: String = ""): Path? {
    try {
        if (!parentJdkDirectory.isEmpty()) {
            val javaPath = Paths.get(parentJdkDirectory)
            return checkJDKDirectories(javaPath)
        }
    } catch (e: Exception) {
        System.err.println("Could not check JDKDirectory for the JDK $e")
    }

    return null
}

private fun checkJDKDirectories(parentJdkDirectory: Path?): Path? {
    return if (parentJdkDirectory == null) null else scanDirectoryContent(parentJdkDirectory)
}

private fun scanDirectoryContent(directoryToScan: Path): Path? {
    if (!Files.isDirectory(directoryToScan)) {
        return null
    }
    return try {
        Files.list(directoryToScan)
                .filter { checkJDKDirectoryName(it) }
                .findAny()
                .orElse(null)
    } catch (e: IOException) {
        System.err.println("Could not list files of: $directoryToScan")
        null
    }

}

private fun checkJDKDirectoryName(directoryToScan: Path): Boolean {
    if (!Files.isDirectory(directoryToScan)) {
        return false
    }
    val fileName = directoryToScan.fileName.toString()
    return checkJDKDirectoryName(fileName)
}

private fun checkJDKDirectoryName(fileNameToScan: String?): Boolean {
    return fileNameToScan != null && fileNameToScan.matches(".*jdk.*[1-9][0-9]{1,2}.*".toRegex())
}

