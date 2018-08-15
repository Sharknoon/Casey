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

import org.apache.commons.io.FileUtils
import sharknoon.casey.compiler.general.beans.*
import sharknoon.casey.compiler.general.beans.Item.ItemType
import sharknoon.casey.compiler.java.generator.generate
import java.nio.charset.StandardCharsets
import java.nio.file.*

/**
 * Creates a folder for the project
 */

fun acceptProject(args: CLIArgs, currentPath: Path, item: Item): Boolean {
    try {
        if (item.item !== ItemType.PROJECT) {
            System.err.println("The top item is not a project")
            return false
        }
        if (item.id == null) {
            System.err.println("The id of the project is not specified")
            return false
        }
        val projectFolder = args.basePath.resolve(Paths.get(item.name))
        if (Files.exists(projectFolder)) {
            FileUtils.forceDelete(projectFolder.toFile())
        }
        Files.createDirectories(projectFolder)
        Files.write(
                projectFolder.resolve("id.txt"),
                listOf(item.id.toString()),
                StandardCharsets.UTF_8
        )
        writeComments(args, item, projectFolder)
        for (child in item.children) {
            val success = generate(args, currentPath.resolve(Paths.get(item.name)), child)
            if (!success) {
                return false
            }
        }
    } catch (e: Exception) {
        System.err.println("Error during output directory cleanup: $e")
        return false
    }

    return true
}

