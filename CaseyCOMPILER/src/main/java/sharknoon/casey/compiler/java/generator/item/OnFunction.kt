package sharknoon.casey.compiler.java.generator.item

import com.squareup.javapoet.*
import sharknoon.casey.compiler.general.beans.*
import sharknoon.casey.compiler.general.beans.Item.ItemType
import java.nio.file.Path
import java.util.stream.*
import javax.lang.model.element.Modifier.*


fun acceptFunction(args: CLIArgs, currentPath: Path, item: Item): Boolean {
    val function = getFunction(args, item, true)
    if (function == null) {
        System.err.println("Could not create static function " + getFullName(item))
        return false
    }
    val className = item.name
    val methods = mutableListOf<MethodSpec>()
    methods.add(function)

    if (isMainMethod(args, item)) {
        val parameters = args.parameters
        val parameterValuesInRightOrder = item.children.stream()
                .filter { i -> i.item === ItemType.PARAMETER }
                .map { n ->
                    if (parameters.containsKey(n.name)) {
                        if ("TEXT" == n.type) {
                            return@map "\"" + parameters[n.name] + "\""
                        }
                        return@map parameters[n.name]
                    } else {
                        System.err.println("Function "
                                + getFullName(item)
                                + " has wrong parameters actual: ("
                                + parameters.keys.joinToString(", ")
                                + ") expected: ("
                                + item.children.stream()
                                .filter { p -> p.item === ItemType.PARAMETER }
                                .map { p -> p.name }
                                .collect(Collectors.joining(", "))
                                + ")"
                        )
                        return@map null
                    }
                }
                .collect(Collectors.toList())

        if (parameterValuesInRightOrder.contains(null)) {
            return false
        }

        val mainMethod = MethodSpec.methodBuilder("main")
                .addModifiers(PUBLIC, STATIC)
                .addParameter(Array<String>::class.java, "args")
                .addStatement(className + "." + className + "(" + parameterValuesInRightOrder.joinToString(", ") + ")")
                .build()
        methods.add(mainMethod)
    }

    try {
        val clazz = TypeSpec.classBuilder(className)
                .addModifiers(PUBLIC)
                .addMethods(methods)
                .build()
        val varFile = JavaFile.builder(pathToClassPath(currentPath), clazz)
                .build()
        varFile.writeTo(args.basePath)
    } catch (e: Exception) {
        System.err.println("Could not create function " + getFullName(item) + ": " + e)
        return false
    }

    return true
}


