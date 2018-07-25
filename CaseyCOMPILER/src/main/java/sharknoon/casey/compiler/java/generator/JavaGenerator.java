package sharknoon.casey.compiler.java.generator;/*
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

import sharknoon.casey.compiler.general.beans.CLIArgs;
import sharknoon.casey.compiler.general.beans.Item;
import sharknoon.casey.compiler.java.generator.item.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaGenerator {
    
    public static boolean generate(CLIArgs args, Item item) {
        return generate(args, Paths.get(""), item);
    }
    
    public static boolean generate(CLIArgs args, Path currentPath, Item item) {
        switch (item.item) {
            case PROJECT:
                return OnProject.accept(args, currentPath, item);
            case PACKAGE:
                return OnPackage.accept(args, currentPath, item);
            case CLASS:
                return OnClass.accept(args, currentPath, item);
            case FUNCTION:
                return OnFunction.accept(args, currentPath, item);
            case VARIABLE:
                return OnVariable.accept(args, currentPath, item);
            case PARAMETER:
                System.err.println("Parameters not allowed outside of functions");
                return false;
        }
        return false;
    }
    
}
