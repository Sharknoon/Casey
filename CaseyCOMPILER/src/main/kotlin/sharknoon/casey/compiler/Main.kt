package sharknoon.casey.compiler

import sharknoon.casey.compiler.general.cli.*
import sharknoon.casey.compiler.general.parser.parseCasey
import sharknoon.casey.compiler.java.compiler.compile
import sharknoon.casey.compiler.java.generator.generate
import kotlin.system.exitProcess

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
fun main(args: Array<String>) {
    val status = go(args)
    exitProcess(status)
}

fun go(args: Array<String>): Int {
    val cliArgs = parseCommandLine(args)
    if (cliArgs === null) {
        System.err.println("[STAGE 1: COMMANDLINE-PARSING FAILED]")
        return 1
    }
    println("[STAGE 1: COMMANDLINE-PARSING COMPLETE]")
    //---------------------
    val item = parseCasey(cliArgs.caseyPath)
    if (item === null) {
        System.err.println("[STAGE 2: CASEY-PARSING FAILED]")
        return 2
    }
    println("[STAGE 2: CASEY-PARSING COMPLETE]")
    //---------------------
    var success = false
    when (cliArgs.language) {
        CLIArgs.Language.JAVA -> success = generate(cliArgs, item)
        //Maybe add more languages
    }
    if (success) {
        println("[STAGE 3: CODE-GENERATION COMPLETE]")
    } else {
        System.err.println("[STAGE 3: CODE-GENERATION FAILED]")
        return 3
    }
    //---------------------
    success = false
    when (cliArgs.language) {
        CLIArgs.Language.JAVA -> success = compile(cliArgs)
        //Maybe add more languages
    }
    if (success) {
        println("[STAGE 4: CODE-COMPILATION COMPLETE]")
    } else {
        System.err.println("[STAGE 4: CODE-COMPILATION FAILED]")
        return 4
    }
    return 0
}