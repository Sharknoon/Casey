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

import com.squareup.javapoet.TypeSpec;
import sharknoon.casey.compiler.general.CaseyParser;
import sharknoon.casey.compiler.general.beans.CLIArgs;
import sharknoon.casey.compiler.general.beans.Item;

import java.nio.file.Path;

public class OnClass {
    
    public static void accept(CLIArgs args, Path currentPath, Item item) {
        String itemName = item.name;
        String fullItemName = CaseyParser.ITEM_TO_NAME.get(item);
        if (itemName == null) {
            //TODO
            return;
        }
        if (fullItemName == null) {
            //TODO
            return;
        }
        
        //children name comments
        try {
            TypeSpec.classBuilder(itemName);
            
        } catch (Exception e) {
            System.err.println("Error during class creation in " + currentPath + ": " + e);
        }
    }
    
}
