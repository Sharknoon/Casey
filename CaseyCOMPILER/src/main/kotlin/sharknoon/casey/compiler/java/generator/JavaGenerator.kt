package sharknoon.casey.compiler.java.generator

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
import sharknoon.casey.compiler.general.parser.beans.Item
import sharknoon.casey.compiler.general.parser.beans.Item.ItemType.*
import sharknoon.casey.compiler.java.generator.item.*
import java.nio.file.*

fun generate(args: CLIArgs, item: Item): Boolean {
    return generate(args, Paths.get(""), item)
}

fun generate(args: CLIArgs, currentPath: Path, item: Item): Boolean {
    return when (item.item) {
        PROJECT -> acceptProject(args, currentPath, item)
        PACKAGE -> acceptPackage(args, currentPath, item)
        CLASS -> acceptClass(args, currentPath, item)
        FUNCTION -> acceptFunction(args, currentPath, item)
        VARIABLE -> acceptVariable(args, currentPath, item)
        PARAMETER -> {
            System.err.println("Parameters not allowed outside of functions")
            false
        }
    }
}

