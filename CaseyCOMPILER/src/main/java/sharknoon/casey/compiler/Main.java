package sharknoon.casey.compiler;
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

import sharknoon.casey.compiler.general.CLIParser;
import sharknoon.casey.compiler.general.CaseyParser;
import sharknoon.casey.compiler.java.compiler.JavaCompiler;
import sharknoon.casey.compiler.java.generator.JavaGenerator;

public class Main {
    
    public static void main(String[] args) {
        int success = go(args);
        System.exit(success);
    }
    
    public static int go(String[] args) {
        var cliArgs = CLIParser.parseCommandLine(args);
        if (!cliArgs.isPresent()) {
            System.err.println("[STAGE 1: COMMANDLINE-PARSING FAILED]");
            return 1;
        }
        System.out.println("[STAGE 1: COMMANDLINE-PARSING COMPLETE]");
        //---------------------
        var item = CaseyParser.parseCasey(cliArgs.get().getCaseyPath());
        if (!item.isPresent()) {
            System.err.println("[STAGE 2: CASEY-PARSING FAILED]");
            return 2;
        }
        System.out.println("[STAGE 2: CASEY-PARSING COMPLETE]");
        //---------------------
        boolean success = false;
        switch (cliArgs.get().getLanguage()) {
            case JAVA:
                success = JavaGenerator.generate(cliArgs.get(), item.get());
                break;
            //TODO maybe add more languages
        }
        if (success) {
            System.out.println("[STAGE 3: CODE-GENERATION COMPLETE]");
        } else {
            System.err.println("[STAGE 3: CODE-GENERATION FAILED]");
            return 3;
        }
        //---------------------
        success = false;
        switch (cliArgs.get().getLanguage()) {
            case JAVA:
                success = JavaCompiler.compile(cliArgs.get());
                break;
            //TODO maybe add more languages
        }
        if (success) {
            System.out.println("[STAGE 4: CODE-COMPILATION COMPLETE]");
        } else {
            System.err.println("[STAGE 4: CODE-COMPILATION FAILED]");
            return 4;
        }
        return 0;
    }
    
    
}
