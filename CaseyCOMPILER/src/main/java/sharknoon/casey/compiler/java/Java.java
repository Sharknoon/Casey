package sharknoon.casey.compiler.java;/*
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

import java.nio.file.Path;
import java.nio.file.Paths;

public class Java {
    
    public static void convert(CLIArgs args, Item item) {
        convert(args, Paths.get(""), item);
    }
    
    public static void convert(CLIArgs args, Path currentPath, Item item) {
        switch (item.item) {
            case PROJECT:
                OnProject.accept(args, currentPath, item);
                break;
            case PACKAGE:
                OnPackage.accept(args, currentPath, item);
                break;
            case CLASS:
                OnClass.accept(args, currentPath, item);
                break;
            case FUNCTION:
                OnFunction.accept(args, currentPath, item);
                break;
            case VARIABLE:
                OnVariable.accept(args, currentPath, item);
                break;
            case PARAMETER:
                System.err.println("Parameters not allowed outside of functions");
                break;
        }
    }
    
}
