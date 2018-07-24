package sharknoon.casey.compiler.general;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import sharknoon.casey.compiler.general.beans.Block;
import sharknoon.casey.compiler.general.beans.Item;
import sharknoon.casey.compiler.general.beans.Item.ItemType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CaseyParser {
    
    public static Map<String, Item> NAME_TO_ITEM = new HashMap<>();
    public static Map<Item, String> ITEM_TO_NAME = new HashMap<>();
    public static Map<UUID, Block> NAME_TO_BLOCK = new HashMap<>();
    public static Map<Block, UUID> BLOCK_TO_NAME = new HashMap<>();
    public static List<Item> STATIC_VARIABLES = new ArrayList<>();
    public static List<Item> STATIC_FUNCTIONS = new ArrayList<>();
    
    public static Optional<Item> parseCasey(Path path) {
        if (!Files.exists(path)) {
            System.err.println("Could not find file: " + path);
            return Optional.empty();
        }
        var newItem = getItem(path);
        if (!newItem.isPresent()) {
            return Optional.empty();
        }
        buildNameDirectories(newItem.get(), null, "");
        return newItem;
    }
    
    private static Optional<Item> getItem(Path path) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Item item = mapper.readValue(Files.newInputStream(path), Item.class);
            return Optional.of(item);
        } catch (Exception e) {
            System.err.println("Could not parse item: " + e);
        }
        return Optional.empty();
    }
    
    private static void buildNameDirectories(Item i, Item p, String currentPath) {
        currentPath = currentPath.isEmpty() ? i.name : currentPath + "." + i.name;
        NAME_TO_ITEM.put(currentPath, i);
        ITEM_TO_NAME.put(i, currentPath);
        if (p != null && p.item == ItemType.PACKAGE) {
            if (i.item == ItemType.VARIABLE) {
                STATIC_VARIABLES.add(i);
            } else if (i.item == ItemType.FUNCTION) {
                STATIC_FUNCTIONS.add(i);
            }
        }
        if (i.children != null) {
            for (Item c : i.children) {
                buildNameDirectories(c, i, currentPath);
            }
        }
        if (i.blocks != null) {
            for (Block block : i.blocks) {
                NAME_TO_BLOCK.put(block.blockid, block);
                BLOCK_TO_NAME.put(block, block.blockid);
            }
        }
    }
    
}
