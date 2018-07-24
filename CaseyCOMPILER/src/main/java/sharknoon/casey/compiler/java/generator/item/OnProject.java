package sharknoon.casey.compiler.java.generator.item;/*
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

import org.apache.commons.io.FileUtils;
import sharknoon.casey.compiler.general.beans.CLIArgs;
import sharknoon.casey.compiler.general.beans.Item;
import sharknoon.casey.compiler.general.beans.Item.ItemType;
import sharknoon.casey.compiler.java.generator.JavaGenerator;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Creates a folder for the project
 */
public class OnProject {
    
    public static boolean accept(CLIArgs args, Path currentPath, Item item) {
        try {
            if (item == null) {
                System.err.println("The project itself is not specified");
                return false;
            }
            if (item.item != ItemType.PROJECT) {
                System.err.println("The top item is not a project");
                return false;
            }
            if (item.name == null) {
                System.err.println("The name of the project is not specified");
                return false;
            }
            if (item.id == null) {
                System.err.println("The id of the project is not specified");
                return false;
            }
            Path projectFolder = args.getBasePath().resolve(Paths.get(item.name));
            if (Files.exists(projectFolder)) {
                FileUtils.forceDelete(projectFolder.toFile());
            }
            Files.createDirectories(projectFolder);
            Files.write(
                    projectFolder.resolve("id.txt"),
                    Stream.of(item.id).map(Object::toString).collect(Collectors.toList()),
                    StandardCharsets.UTF_8
            );
            ItemUtils.writeComments(args, item, projectFolder);
            for (Item child : item.children) {
                boolean success = JavaGenerator.generateJava(args, currentPath.resolve(Paths.get(item.name)), child);
                if (!success) {
                    return false;
                }
            }
        } catch (Exception e) {
            System.err.println("Error during output directory cleanup: " + e);
            return false;
        }
        return true;
    }
}
