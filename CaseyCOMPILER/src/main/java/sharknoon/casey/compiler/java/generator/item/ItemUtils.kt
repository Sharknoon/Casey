package sharknoon.casey.compiler.java.generator.item

import com.squareup.javapoet.*
import org.jsoup.Jsoup
import sharknoon.casey.compiler.general.*
import sharknoon.casey.compiler.general.beans.*
import sharknoon.casey.compiler.general.beans.Block.BlockType
import sharknoon.casey.compiler.general.beans.Item.ItemType
import sharknoon.casey.compiler.java.generator.block.acceptBlock
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.*
import java.util.function.Function
import javax.lang.model.element.Modifier.*

val STRING_TYPE_NAME: TypeName = ClassName.get("java.lang", "String")
private const val EMPTY = ""
private val FUNCTION_ALONE_MODIFIERS = listOf(PUBLIC, STATIC)
private val FUNCTION_IN_CLASS_MODIFIERS = listOf(PUBLIC)
private const val JAVADOC_START = "/**\n"
private const val JAVADOC_END = " */\n"
private const val JAVADOC_LINE_START = " * "
private const val JAVADOC_NEWLINE = "\n"

/**
 * Extracts the HTML of the item, checks for nulls and encapsulates the body from the comment
 *
 * @param args The CLIArgs to check if comments should be ignored
 * @param item The item which the comments should be extracted
 * @return The comments or a empty string of a error occurs
 */
internal fun getJavaDoc(args: CLIArgs, item: Item): String {
    if (args.ignoreComments) {
        return EMPTY
    }
    if (item.comments.isEmpty()) {
        return EMPTY
    }
    try {
        return Jsoup.parse(item.comments).body().html() + "\n"
    } catch (e: Exception) {
        println("Warning, could not load comments $e")
    }

    return EMPTY
}

private fun getJavaDocFormatted(htmlJavaDoc: String): String {
    if (htmlJavaDoc.isEmpty()) {
        return EMPTY
    }
    return htmlJavaDoc.lines()
            .joinToString(
                    prefix = JAVADOC_START,
                    separator = JAVADOC_NEWLINE,
                    postfix = JAVADOC_END)
            { JAVADOC_LINE_START + it }
}

/**
 * Converts a Path to a directory to a JavaGenerator-Classpath
 *
 * @param path The Path to be converted
 * @return The JavaGenerator-Classpath
 */
internal fun pathToClassPath(path: Path): String {
    val pathIterator = path.iterator()
    val result = StringBuilder()
    if (pathIterator.hasNext()) {
        result.append(pathIterator.next())
    } else {
        return EMPTY
    }
    while (pathIterator.hasNext()) {
        result.append('.').append(pathIterator.next())
    }
    return result.toString()
}

/**
 * Writes a "comments.txt" file with the comments of this item, if this item has comments and comments arent ignored
 *
 * @param args The CLIArgs to check if comments are ignored
 * @param item The Item which comments should be written, typically a project or a package, class, variables and
 * functions are writing the comments inside ítself
 * @param path The path to write the file into
 */
internal fun writeComments(args: CLIArgs, item: Item, path: Path) {
    if (args.ignoreComments) {
        return
    }
    try {
        item.comments
        if (!item.comments.isEmpty()) {
            Files.write(
                    path.resolve("comments.html"),
                    item.comments.lines(),
                    StandardCharsets.UTF_8
            )
        }
    } catch (e: IOException) {
        println("Could not write comments: $e")
    }

}

/**
 * Returns the full name of a item (e.g. Project.package.Clazz) or a empty String if this item is isnt registered
 *
 * @param item The item which full name should be returned
 * @return The full name or a empty String
 */
internal fun getFullName(item: Item): String {
    val name = getItemFullName(item)
    return name ?: EMPTY
}

/**
 * Checks weather this item was selected via CLI arguments as main method or not
 *
 * @param args The CLIArgs to get the main method
 * @param item The item to get its name
 * @return True, if this is the method to start with
 */
internal fun isMainMethod(args: CLIArgs, item: Item): Boolean {
    return getFullName(item) == args.function
}

/**
 * Returns a TypeName for JavaPoet based on my primitive Strings or a empty Optional, if the class was not
 * registered
 *
 * @param typeNameString The full name of the item
 * @return The typename or a empty Optional
 */
fun getTypeName(typeNameString: String): TypeName? {
    var typeName: TypeName? = null
    var varType: VariableType? = null
    try {
        varType = VariableType.valueOf(typeNameString)
    } catch (e: IllegalArgumentException) {
        //Expected to happen when a type name (a.b.c) is supplied
    }

    //If is primitive or String
    when {
        varType != null -> when (varType) {
            VariableType.BOOLEAN -> typeName = TypeName.BOOLEAN
            VariableType.NUMBER -> typeName = TypeName.DOUBLE
            VariableType.TEXT -> typeName = STRING_TYPE_NAME
            VariableType.VOID -> typeName = TypeName.VOID
            else -> System.err.println("Could not determine Variable type")
        }
        //Is a class
        isValidItemName(typeNameString) -> {
            val lastPointIndex = typeNameString.lastIndexOf(".")
            typeName = ClassName.get(
                    typeNameString.substring(0, lastPointIndex),
                    typeNameString.substring(lastPointIndex + 1)
            )
        }
        else -> System.err.println("Could not determine type for $typeNameString")
    }
    return typeName
}

/**
 * Returns a CodeBlock for a name of a function. If the function is static,
 * it returns name.name and imports the class, otherwise returns name,
 * IMPORTANT, the brackets needed to be added seperatly
 */
fun getFunctionName(function: Item): CodeBlock? {
    return getVariableOrFunctionName(function, Function { isStaticFunction(it) })
}

/**
 * Returns a CodeBlock for a name of a variable. If the variable is static,
 * it returns name.name and imports the class, otherwise returns name
 */
fun getVariableName(variable: Item): CodeBlock? {
    return getVariableOrFunctionName(variable, Function { isStaticVariable(it) })
}

private fun getVariableOrFunctionName(item: Item, validItemFunction: Function<Item, Boolean>): CodeBlock? {
    if (validItemFunction.apply(item)) {
        val typeName = getTypeName(getFullName(item))
        if (typeName == null) {
            System.err.println("TypeName for " + item.item.toString().toLowerCase() + " " + getFullName(item) + " could not be determined")
            return null
        }
        return CodeBlock.of("\$T." + item.name, typeName)
    } else {
        return CodeBlock.of(item.name)
    }
}

/**
 * Initializes a field with a basic type to avoid null
 *
 * @param type The type be be initialized
 * @return The initializing Codeblock
 */
internal fun getFieldInitializer(type: TypeName): CodeBlock {
    if (TypeName.BOOLEAN == type) {
        return CodeBlock.of("false")
    }
    if (TypeName.DOUBLE == type) {
        return CodeBlock.of("0.0")
    }
    if (STRING_TYPE_NAME == type) {
        return CodeBlock.of("\"\"")
    }
    if (!type.isPrimitive) {
        return CodeBlock.of("new \$T()", type)
    }
    System.err.println("Could not determine initializer for field $type")
    return CodeBlock.builder().build()
}

/**
 * Returns a function for a item
 *
 * @param args         The args to specify, weather comments should be added or not
 * @param functionItem The item to be converted
 * @param isStatic     true if this item is not in a class, false oif it is in a package
 * @return A optional if methodspec
 */
internal fun getFunction(args: CLIArgs, functionItem: Item, isStatic: Boolean): MethodSpec? {
    if (functionItem.item == null) {
        System.err.println("Type of this function is not specified " + getFullName(functionItem))
        return null
    }
    if (ItemType.FUNCTION != functionItem.item) {
        return null
    }
    if (functionItem.returntype == null) {
        System.err.println("Returntype of function not specified " + getFullName(functionItem))
        return null
    }

    val modifiers = if (isStatic) FUNCTION_ALONE_MODIFIERS else FUNCTION_IN_CLASS_MODIFIERS
    val returnTypeNameString = functionItem.returntype
    val returnTypeName = getTypeName(returnTypeNameString ?: EMPTY) ?: return null
    val functionName = functionItem.name
    val parameters = getParameters(functionItem)
    val parameterJavaDoc = getParameterJavaDoc(args, functionItem)
    val variables = getVariables(args, functionItem)
    val variablesAndBlocksBuilder = CodeBlock.builder()
    for (variable in variables) {
        variablesAndBlocksBuilder.add(variable)
    }
    for (block in functionItem.blocks) {
        if (block.blocktype === BlockType.START) {
            val codeBlock = acceptBlock(args, block)
            if (codeBlock == null) {
                System.err.println("Something in the function " + getFullName(functionItem) + " went wrong")
                return null
            }
            variablesAndBlocksBuilder.add(codeBlock)
        }
    }
    val variablesAndBlocks = variablesAndBlocksBuilder.build()

    try {
        return MethodSpec.methodBuilder(functionName)
                .addJavadoc(getJavaDoc(args, functionItem))
                .addJavadoc(parameterJavaDoc)
                .addModifiers(modifiers)
                .addParameters(parameters)
                .returns(returnTypeName)
                .addCode(variablesAndBlocks)
                .build()
    } catch (e: Exception) {
        System.err.println("Could not create function $e")
    }

    return null
}

/**
 * Returns the parameter of a function as a List of parameterspec
 *
 * @param functionItem The item which parameters should be converted
 * @return The list of parameters
 */
private fun getParameters(functionItem: Item): List<ParameterSpec> {
    val parameters = mutableListOf<ParameterSpec>()
    for (child in functionItem.children) {
        if (child.item == null) {
            System.err.println("Could not determine type of item " + getFullName(child))
            return parameters
        }
        if (ItemType.PARAMETER != child.item) {
            continue
        }
        if (child.type == null) {
            System.err.println("Could not determine type for parameter " + getFullName(child))
            return parameters
        }
        val parameterTypeName = getTypeName(child.type ?: EMPTY) ?: return listOf()
        val parameterName = child.name

        val parameter = ParameterSpec.builder(parameterTypeName, parameterName)
                .build()
        parameters.add(parameter)
    }
    return parameters
}

private fun getParameterJavaDoc(args: CLIArgs, item: Item): String {
    if (args.ignoreComments) {
        return EMPTY
    }
    val builder = StringBuilder()
    for (child in item.children) {
        if (child.item == null || child.comments.isEmpty()) {
            continue
        }
        if (ItemType.PARAMETER != child.item) {
            continue
        }
        builder.append("@param ")
                .append(child.name)
                .append(' ')
                .append(getJavaDoc(args, child))
    }
    return builder.toString()
}

private fun getVariables(args: CLIArgs, item: Item): List<CodeBlock> {
    val variables = mutableListOf<CodeBlock>()
    for (child in item.children) {
        if (child.item == null) {
            System.err.println("Type of this variable is not specified " + getFullName(child))
            return variables
        }
        if (ItemType.VARIABLE != child.item) {
            continue
        }
        if (child.type == null) {
            System.err.println("Type of variable not specified: " + getFullName(child))
            return variables
        }

        val typeNameString = child.type
        val typeName = getTypeName(typeNameString ?: EMPTY) ?: return listOf()
        val className = child.name

        try {
            val variable = CodeBlock.builder()
                    .add(getJavaDocFormatted(getJavaDoc(args, child)))
                    .addStatement("\$T \$L = \$L", typeName, className, getFieldInitializer(typeName))
                    .build()
            variables.add(variable)
        } catch (e: Exception) {
            System.err.println("Could not create variable codeblock $e")
        }

    }
    return variables
}
