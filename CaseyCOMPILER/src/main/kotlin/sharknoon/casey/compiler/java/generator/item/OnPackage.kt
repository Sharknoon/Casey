package sharknoon.casey.compiler.java.generator.item

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


import sharknoon.casey.compiler.general.beans.*
import sharknoon.casey.compiler.java.generator.generate
import java.nio.file.*

/**
 * Creates a folder for the package
 */
fun acceptPackage(args: CLIArgs, currentPath: Path, item: Item): Boolean {
    try {
        val relativePath = currentPath.resolve(item.name)
        val fullPackagePath = args.basePath.resolve(relativePath)
        Files.createDirectories(fullPackagePath)
        writeComments(args, item, fullPackagePath)
        for (child in item.children) {
            val success = generate(args, relativePath, child)
            if (!success) {
                return false
            }
        }
        return true
    } catch (e: Exception) {
        System.err.println("Error during package folder creation on $currentPath: $e")
        e.printStackTrace()
        return false
    }
}

