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
import java.nio.file.Path
import javax.lang.model.element.Modifier.*


fun acceptVariable(args: CLIArgs, currentPath: Path, item: Item): Boolean {
    if (item.type == null) {
        System.err.println("Type of variable not specified: " + getFullName(item))
        return false
    }

    val typeNameString = item.type
    val typeName = getTypeName(typeNameString ?: "") ?: return false
    val className = item.name

    try {
        val field = FieldSpec.builder(typeName, className)
                .addJavadoc(getJavaDoc(args, item))
                .addModifiers(PUBLIC, STATIC)
                .initializer(getFieldInitializer(typeName))
                .build()
        val clazz = TypeSpec.classBuilder(className)
                .addModifiers(PUBLIC)
                .addField(field)
                .build()
        val varFile = JavaFile.builder(pathToClassPath(currentPath), clazz)
                .build()
        varFile.writeTo(args.basePath)
    } catch (e: Exception) {
        System.err.println("Could not write class for variable " + getFullName(item) + ": " + e)
        return false
    }

    return true
}

