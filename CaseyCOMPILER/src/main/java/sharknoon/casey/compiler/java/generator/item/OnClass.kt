package sharknoon.casey.compiler.java.generator.item

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
 * See the License for the specific languageString governing permissions and
 * limitations under the License.
 */

import com.squareup.javapoet.*
import sharknoon.casey.compiler.general.beans.*
import sharknoon.casey.compiler.general.beans.Item.ItemType
import java.nio.file.Path
import java.util.*
import javax.lang.model.element.Modifier.PUBLIC

fun acceptClass(args: CLIArgs, currentPath: Path, item: Item): Boolean {
    val itemName = item.name
    val variables = getVariables(args, item) ?: return false
    val functions = getFunctions(args, item) ?: return false

    try {
        val clazz = TypeSpec.classBuilder(itemName)
                .addModifiers(PUBLIC)
                .addJavadoc(getJavaDoc(args, item))
                .addFields(variables)
                .addMethods(functions)
                .build()
        val classFile = JavaFile.builder(pathToClassPath(currentPath), clazz)
                .build()
        classFile.writeTo(args.basePath)
    } catch (e: Exception) {
        System.err.println("Error during class creation in $currentPath: $e")
        return false
    }

    return true
}

private fun getFunctions(args: CLIArgs, item: Item): List<MethodSpec>? {
    val functions = mutableListOf<MethodSpec>()
    for (child in item.children) {
        if (ItemType.FUNCTION != child.item) {
            continue
        }
        val function = getFunction(args, child, false)
        if (function == null) {
            System.err.println("Could not create object function " + child.name + " in " + getFullName(item))
            return null
        }
        functions.add(function)
    }
    return functions
}

private fun getVariables(args: CLIArgs, item: Item): List<FieldSpec>? {
    val variables = ArrayList<FieldSpec>()
    for (child in item.children) {
        if (child.item == null) {
            System.err.println("Type of class item not specified: " + getFullName(child))
            return null
        }
        if (ItemType.VARIABLE != child.item) {
            continue
        }
        if (child.type == null) {
            System.err.println("Type of variable of class not specified: " + getFullName(child))
            return null
        }

        val typeNameString = child.type
        val typeName = getTypeName(typeNameString ?: "") ?: return null
        val fieldName = child.name

        try {
            val variable = FieldSpec.builder(typeName, fieldName)
                    .addJavadoc(getJavaDoc(args, child))
                    .addModifiers(PUBLIC)
                    .initializer(getFieldInitializer(typeName))
                    .build()
            variables.add(variable)
        } catch (e: Exception) {
            System.err.println("Could not write class for variable " + getFullName(child) + ": " + e)
            return null
        }

    }
    return variables
}


