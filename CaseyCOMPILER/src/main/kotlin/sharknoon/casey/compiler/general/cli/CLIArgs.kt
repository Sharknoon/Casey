package sharknoon.casey.compiler.general.cli

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

import java.nio.file.*

data class CLIArgs(
        val function: String,
        private val caseyPathString: String,
        private val languageString: String,
        val parameters: Map<String, String>,
        val ignoreComments: Boolean) {


    /**
     * The path of the .casey file
     */
    val caseyPath: Path = Paths.get(caseyPathString)

    /**
     * The base path, the folder in which the .casey file is located
     *
     * @return
     */
    val basePath: Path = caseyPath.parent ?: Paths.get("")

    /**
     * The path to the main class .java file (base path + function path + '.java')
     *
     * @return
     */
    val functionPath: Path = basePath.resolve(function.replace('.', '/') + ".java")

    val language = Language.valueOf(languageString.toUpperCase())
    override fun toString(): String {
        return "CLIArgs(function='$function', " +
                "parameters=$parameters, " +
                "ignoreComments=$ignoreComments, " +
                "caseyPath=$caseyPath, " +
                "basePath=$basePath, " +
                "functionPath=$functionPath, " +
                "language=$language)"
    }

    enum class Language {
        JAVA
        //LUA maybe in the future
    }

}