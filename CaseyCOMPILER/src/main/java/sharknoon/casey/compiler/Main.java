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
import sharknoon.casey.compiler.java.Java;

public class Main {
    
    
    public static void main(String[] args) {
        var cliArgs = CLIParser.parseCommandLine(args);
        if (!cliArgs.isPresent()) {
            return;
        }
        var item = CaseyParser.parseCasey(cliArgs.get().getCaseyPath());
        if (!item.isPresent()) {
            return;
        }
        switch (cliArgs.get().getLanguage()) {
            case JAVA:
                Java.convert(cliArgs.get(), item.get());
                break;
            //TODO maybe add more languages
        }
    }
    
    
}
