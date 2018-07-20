package sharknoon.casey.compiler.general;/*
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

import com.fasterxml.jackson.databind.ObjectMapper;
import sharknoon.casey.compiler.general.beans.Item;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CaseyParser {
    
    private static List<String> ERRORS = new ArrayList<>();
    
    public static Optional<Item> parseCasey(String path) {
        var newPath = getPath(path);
        if (!newPath.isPresent()) {
            return Optional.empty();
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            Item item = mapper.readValue(Files.newInputStream(newPath.get()), Item.class);
            if (ERRORS.isEmpty()) {
                System.out.println("success");
                return Optional.of(item);
            }
        } catch (Exception e) {
            onCaseyParseError("Could not parse item: " + e);
        }
        ERRORS.forEach(System.out::println);
        return Optional.empty();
    }
    
    private static Optional<Path> getPath(String path) {
        Path newPath = Paths.get(path);
        if (Files.exists(newPath)) {
            return Optional.of(newPath);
        }
        onCaseyParseError("Could not find file: " + path);
        return Optional.empty();
    }
    
    public static void onCaseyParseError(String message) {
        ERRORS.add(message);
    }
    
}
