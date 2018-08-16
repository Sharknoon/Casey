package sharknoon.casey.updater.cli

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

import org.apache.commons.cli.*


private var options: Options = initOptions()
private const val HEADER = "\n" +
        "-----------------------\n" +
        "-    CaseyUPDATER     -\n" +
        "-----------------------\n" +
        "\n"

fun parseCommandLine(args: Array<String>): CLIArgs? {
    val parser = DefaultParser()
    try {
        val cmd = parser.parse(options, args)
        if (cmd.options.isEmpty()) {
            throw Exception()
        }
        val check = cmd.getOptionValue("c")
        val update = cmd.getOptionValue("u")
        return CLIArgs(check, update)
    } catch (e: Exception) {
        println(e.message)
        val formatter = HelpFormatter()
        formatter.printHelp(
                100,
                "CaseyUPDATER",
                HEADER,
                options,
                "",
                true
        )
    }

    return null
}

private fun initOptions(): Options {
    val options = Options()

    val check = Option.builder("c")
            .longOpt("check")
            .hasArg()
            .argName("X.X")
            .desc("Checks for a newer version of Casey. The old version of casey to be checked against")
            .build()

    val update = Option.builder("u")
            .longOpt("update")
            .hasArg()
            .argName("../../Casey.jar")
            .desc("Updates Casey. The path to the Casey.jar to be replaced")
            .build()

    options.addOption(check)
    options.addOption(update)

    return options
}

