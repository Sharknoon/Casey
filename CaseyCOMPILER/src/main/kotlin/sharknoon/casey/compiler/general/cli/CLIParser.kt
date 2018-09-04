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

import org.apache.commons.cli.*

private var options = initOptions()

fun parseCommandLine(args: Array<String>): CLIArgs? {
    val parser = DefaultParser()
    try {
        val cmd = parser.parse(options, args)
        val path = cmd.getOptionValue("p")
        val function = cmd.getOptionValue("f")
        val language = cmd.getOptionValue("l")
        val parameter = cmd.getOptionValues("pa")?.toList() ?: listOf()
        val parameterMap = parameter.zipWithNext().toMap()
        val ignoreComments = cmd.hasOption("i")
        return CLIArgs(function, path, language, parameterMap, ignoreComments)
    } catch (e: Exception) {
        println(e.message)
        val formatter = HelpFormatter()
        val header = "\n" +
                "-----------------------\n" +
                "-    CaseyCOMPILER    -\n" +
                "-----------------------\n" +
                "\n"
        formatter.printHelp(
                100,
                "CaseyCOMPILER",
                header,
                options,
                "",
                true
        )
    }

    return null
}


private fun initOptions(): Options {
    val options = Options()

    val path = Option.builder("p")
            .longOpt("path")
            .hasArg()
            .argName("../../xyz.casey")
            .required()
            .desc("The path to the .casey file to be compiled")
            .build()

    val function = Option.builder("f")
            .longOpt("function")
            .hasArg()
            .argName("ABCProject.defpackage.XYZFunction")
            .required()
            .desc("The main function to be started with")
            .build()

    val language = Option.builder("l")
            .longOpt("languageString")
            .hasArg()
            .argName("name")
            .required()
            .desc("The languageString this project should be compiled to (" +
                    CLIArgs.Language.values().joinToString()
            )
            .build()

    val parameters = Option.builder("pa")
            .longOpt("parameter")
            .hasArgs()
            .argName("parameter=value")
            .valueSeparator()
            .desc("The parameters of the main function, if the function has some")
            .build()

    val ignoreComments = Option.builder("i")
            .longOpt("ignorecomments")
            .desc("Ignores the comments to improve parsing speed")
            .build()

    options.addOption(path)
    options.addOption(function)
    options.addOption(language)
    options.addOption(parameters)
    options.addOption(ignoreComments)

    return options
}