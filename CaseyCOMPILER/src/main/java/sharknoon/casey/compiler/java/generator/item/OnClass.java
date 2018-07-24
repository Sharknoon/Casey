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

import com.squareup.javapoet.*;
import sharknoon.casey.compiler.general.beans.CLIArgs;
import sharknoon.casey.compiler.general.beans.Item;
import sharknoon.casey.compiler.general.beans.Item.ItemType;

import javax.lang.model.element.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OnClass {
    
    public static boolean accept(CLIArgs args, Path currentPath, Item item) {
        if (item == null) {
            System.err.println("Class itself not specified " + currentPath);
            return false;
        }
        if (item.name == null) {
            System.err.println("Name of class not specified " + currentPath);
            return false;
        }
        
        String itemName = item.name;
        List<FieldSpec> variables = getVariables(args, item);
        if (variables == null) {
            return false;
        }
        List<MethodSpec> functions = getFunctions(args, item);
        if (functions == null) {
            return false;
        }
        
        try {
            TypeSpec clazz = TypeSpec.classBuilder(itemName)
                    .addJavadoc(ItemUtils.getJavaDoc(args, item))
                    .addFields(variables)
                    .addMethods(functions)
                    .build();
            JavaFile classFile = JavaFile.builder(ItemUtils.pathToClassPath(currentPath), clazz)
                    .build();
            classFile.writeTo(args.getBasePath());
        } catch (Exception e) {
            System.err.println("Error during class creation in " + currentPath + ": " + e);
            return false;
        }
        return true;
    }
    
    private static List<MethodSpec> getFunctions(CLIArgs args, Item item) {
        List<MethodSpec> functions = new ArrayList<>();
        for (Item child : item.children) {
            if (!ItemType.FUNCTION.equals(child.item)) {
                continue;
            }
            Optional<MethodSpec> optionalFunction = ItemUtils.getFunction(args, child, false);
            if (!optionalFunction.isPresent()) {
                System.err.println("Could not create object function " + child.name + " in " + ItemUtils.getFullName(item));
                return null;
            }
            MethodSpec function = optionalFunction.get();
            functions.add(function);
        }
        return functions;
    }
    
    private static List<FieldSpec> getVariables(CLIArgs args, Item item) {
        List<FieldSpec> variables = new ArrayList<>();
        for (Item child : item.children) {
            if (child == null) {
                System.err.println("Variable of class itself not specified: " + ItemUtils.getFullName(item));
                return null;
            }
            if (child.item == null) {
                System.err.println("Type of class item not specified: " + ItemUtils.getFullName(child));
                return null;
            }
            if (!ItemType.VARIABLE.equals(child.item)) {
                continue;
            }
            if (child.name == null) {
                System.err.println("Name of variable of class not specified: " + ItemUtils.getFullName(child));
                return null;
            }
            if (child.type == null) {
                System.err.println("Type of variable of class not specified: " + ItemUtils.getFullName(child));
                return null;
            }
            
            String typeNameString = child.type;
            Optional<TypeName> optionalTypeName = ItemUtils.getTypeName(typeNameString);
            if (!optionalTypeName.isPresent()) {
                return null;
            }
            TypeName typeName = optionalTypeName.get();
            String className = child.name;
            
            try {
                FieldSpec variable = FieldSpec.builder(typeName, className)
                        .addJavadoc(ItemUtils.getJavaDoc(args, child))
                        .addModifiers(Modifier.PUBLIC)
                        .initializer(ItemUtils.getFieldInitializer(typeName))
                        .build();
                variables.add(variable);
            } catch (Exception e) {
                System.err.println("Could not write class for variable " + ItemUtils.getFullName(child) + ": " + e);
                return null;
            }
        }
        return variables;
    }
    
}
