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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Creates a folder for the package
 */
public class OnPackage {
    
    public static void accept(CLIArgs args, Path currentPath, Item item) {
        try {
            if (item == null) {
                System.err.println("The package itself is not specified: " + currentPath);
                return;
            }
            if (item.name == null) {
                System.err.println("The name of the package is not specified: " + currentPath);
            }
            Path packagePath = currentPath.resolve(item.name);
            Files.createDirectories(packagePath);
            if (item.comments != null && !item.comments.isEmpty()) {
                Files.write(
                        packagePath.resolve("comments.txt"),
                        Stream.of(item.comments)
                                .map(c -> c.split("\\r?\\n"))
                                .map(Arrays::stream)
                                .flatMap(s -> s)
                                .collect(Collectors.toList()),
                        StandardCharsets.UTF_8
                );
            }
            for (Item child : item.children) {
                Java.convert(args, packagePath, child);
            }
        } catch (IOException e) {
            System.err.println("Error during package folder creation on " + currentPath + ": " + e);
        }
    }
    
}
