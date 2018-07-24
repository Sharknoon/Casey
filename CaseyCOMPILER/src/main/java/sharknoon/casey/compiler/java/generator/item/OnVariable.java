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

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import sharknoon.casey.compiler.general.beans.CLIArgs;
import sharknoon.casey.compiler.general.beans.Item;

import javax.lang.model.element.Modifier;
import java.nio.file.Path;
import java.util.Optional;

public class OnVariable {
    
    
    public static boolean accept(CLIArgs args, Path currentPath, Item item) {
        if (item == null) {
            System.err.println("Variable itself not specified: " + currentPath);
            return false;
        }
        if (item.name == null) {
            System.err.println("Name of variable not specified: " + ItemUtils.getFullName(item));
            return false;
        }
        if (item.type == null) {
            System.err.println("Type of variable not specified: " + ItemUtils.getFullName(item));
            return false;
        }
    
        String typeNameString = item.type;
        Optional<TypeName> optionalTypeName = ItemUtils.getTypeName(typeNameString);
        if (!optionalTypeName.isPresent()) {
            return false;
        }
        TypeName typeName = optionalTypeName.get();
        String className = item.name;
    
        try {
            FieldSpec field = FieldSpec.builder(typeName, className)
                    .addJavadoc(ItemUtils.getJavaDoc(args, item))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .initializer(ItemUtils.getFieldInitializer(typeName))
                    .build();
            TypeSpec clazz = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .addField(field)
                    .build();
            JavaFile varFile = JavaFile.builder(ItemUtils.pathToClassPath(currentPath), clazz)
                    .build();
            varFile.writeTo(args.getBasePath());
        } catch (Exception e) {
            System.err.println("Could not write class for variable " + ItemUtils.getFullName(item) + ": " + e);
            return false;
        }
        return true;
    }
}
