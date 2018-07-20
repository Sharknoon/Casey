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

import com.squareup.javapoet.*;
import org.apache.commons.lang3.EnumUtils;
import sharknoon.casey.compiler.general.beans.CLIArgs;
import sharknoon.casey.compiler.general.beans.Item;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class OnVariable {
    
    private static List<VariableType> variableTypes;
    
    public static void accept(CLIArgs args, Path currentPath, Item item) {
        if (item == null) {
            System.err.println("Variable itself not specified " + currentPath);
        }
        if (item.name == null) {
            System.err.println("Name of variable not specified " + currentPath);
        }
        if (item.type == null) {
            System.err.println("Type of variable not specified " + currentPath);
        }
        
        String className = item.name;
        if (variableTypes == null) {
            variableTypes = EnumUtils.getEnumList(VariableType.class);
        }
        if (EnumUtils.isValidEnum(VariableType.class, item.type)) {
            VariableType varType = VariableType.valueOf(item.type);
            TypeName typeName;
            switch (varType) {
                case BOOLEAN:
                    typeName = TypeName.BOOLEAN;
                    break;
                case NUMBER:
                    typeName = TypeName.DOUBLE;
                    break;
                case TEXT:
                    typeName = ClassName.bestGuess("java.lang.String");
                    break;
                default:
                    System.err.println("Could not determine Variable type");
                    typeName = null;
            }
            FieldSpec field = FieldSpec.builder(typeName, className)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .build();
            TypeSpec clazz = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .addField(field)
                    .build();
            JavaFile varFile = JavaFile.builder(currentPath.toString().replace("\\", "."), clazz)//TODO better solution
                    .build();
            try {
                varFile.writeTo(Paths.get(""));
            } catch (IOException e) {
                System.err.println("Could not write class for variable " + item + ": " + e);
            }
            
        }
    }
}
