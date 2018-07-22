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

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Creates a folder for the package
 */
public class OnPackage {
    
    public static void accept(CLIArgs args, Path currentPath, Item item) {
        if (item == null) {
            System.err.println("The package itself is not specified: " + currentPath);
            return;
        }
        if (item.name == null) {
            System.err.println("The name of the package is not specified: " + currentPath);
        }
        Path relativePath = currentPath.resolve(item.name);
        Path fullPackagePath = args.getBasePath().resolve(relativePath);
        try {
            Files.createDirectories(fullPackagePath);
            ItemUtils.writeComments(args, item, fullPackagePath);
            for (Item child : item.children) {
                Java.convert(args, relativePath, child);
            }
        } catch (Exception e) {
            System.err.println("Error during package folder creation on " + currentPath + ": " + e);
        }
    }
    
}
