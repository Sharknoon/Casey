package sharknoon.casey.compiler.java.generator.statement

import com.squareup.javapoet.CodeBlock
import sharknoon.casey.compiler.general.beans.*
import sharknoon.casey.compiler.general.beans.Item.ItemType
import sharknoon.casey.compiler.general.beans.Statement.StatementType
import sharknoon.casey.compiler.general.getItem
import sharknoon.casey.compiler.java.generator.item.*
import java.util.*

private const val EMPTY = ""
private const val AND = " && "

fun acceptStatement(args: CLIArgs, statement: Statement): CodeBlock? {
    if (statement.type == null) {
        System.err.println("Type of the statement is null")
        return null
    }

    when (statement.type) {
        //Values
        Statement.StatementType.NUMBER,
        Statement.StatementType.BOOLEAN,
        Statement.StatementType.TEXT,
        Statement.StatementType.OBJECT -> return onValue(statement)
        //Operators
        Statement.StatementType.ADD -> return onOperator(args, statement, " + ")
        Statement.StatementType.AND -> return onOperator(args, statement, AND)
        Statement.StatementType.CONCAT -> return onOperator(args, statement, ") + String.valueOf(", CodeBlock.of("\$T.valueOf(", String::class.java), CodeBlock.of(")"))
        Statement.StatementType.DIVIDE -> return onOperator(args, statement, " / ")
        Statement.StatementType.EQUALS -> {
            if (statement.parameter.size < 2) {
                System.err.println("The Not Equals Operator needs to have two or more parameters")
                return null
            }
            return onOperator(args, statement, ", ", AND, CodeBlock.of("\$T.equals(", Objects::class.java), CodeBlock.of(")"))
        }
        Statement.StatementType.GREATER_OR_EQUAL_THAN -> {
            if (statement.parameter.size < 2) {
                System.err.println("The Greater Or Equals Than Operator needs to have two or more parameters")
                return null
            }
            return onOperator(args, statement, " >= ", AND)
        }
        Statement.StatementType.GREATER_THAN -> {
            if (statement.parameter.size < 2) {
                System.err.println("The Greater Than Operator needs to have two or more parameters")
                return null
            }
            return onOperator(args, statement, " > ", AND)
        }
        Statement.StatementType.LENGTH -> {
            if (statement.parameter.size != 1) {
                System.err.println("The Length Operator needs to have exactly one parameter")
                return null
            }
            return onOperator(args, statement, EMPTY, null, CodeBlock.of(".length()"))
        }
        Statement.StatementType.LESS_OR_EQUAL_THAN -> {
            if (statement.parameter.size < 2) {
                System.err.println("The Less Or Equal Than Operator needs to have two or more parameters")
                return null
            }
            return onOperator(args, statement, " <= ", AND)
        }
        Statement.StatementType.LESS_THAN -> {
            if (statement.parameter.size < 2) {
                System.err.println("The Less Than Operator needs to have two or more parameters")
                return null
            }
            return onOperator(args, statement, " < ", AND)
        }
        Statement.StatementType.MODULO -> return onOperator(args, statement, " % ")
        Statement.StatementType.MULTIPLY -> return onOperator(args, statement, " * ")
        Statement.StatementType.NOT_EQUALS -> {
            if (statement.parameter.size < 2) {
                System.err.println("The Not Equals Operator needs to have two or more parameters")
                return null
            }
            return onOperator(args, statement, ", ", AND, CodeBlock.of("!\$T.equals(", Objects::class.java), CodeBlock.of(")"))
        }
        Statement.StatementType.NOT -> {
            if (statement.parameter.size != 1) {
                System.err.println("The NOT Operator needs to have exactly one parameter")
                return null
            }
            return onOperator(args, statement, EMPTY, CodeBlock.of("!"), null)
        }
        Statement.StatementType.OR -> return onOperator(args, statement, " || ")
        Statement.StatementType.SUBTRACT -> return onOperator(args, statement, " - ")
        //Calls
        Statement.StatementType.CALL -> return onCall(args, statement)
    }
    return null
}

/**
 * This creates a CodeBlock fo a simple value without brackets
 *
 * @param statement The value statement
 * @return The CodeBlock without brackets or null if the statement is null or has a wrong object type
 */
private fun onValue(statement: Statement): CodeBlock? {
    if (statement.value == null) {
        System.err.println("Value of statement $statement is null")
        return null
    }
    when {
        //Text
        statement.type === StatementType.TEXT -> return CodeBlock.of("\$S", statement.value!!)
        //Object
        statement.type === StatementType.OBJECT -> {
            val typeName = getTypeName(statement.value.toString())
            if (typeName == null) {
                System.err.println("Could not get the type name for this object statement $statement")
                return null
            }
            return CodeBlock.of("new \$T()", typeName)
        }
        //Boolean or Number
        else -> return CodeBlock.of("\$L", statement.value!!)
    }
}

/**
 * In special cases where e.g. a > b > c must be converted to (a > b) && (b > c)
 *
 * @param args               The args if additional info is needed
 * @param statement          The statement which parameter should be converted
 * @param secondaryDelimiter The secondary delimitier, in this example >
 * @param primaryDelimiter   The primary Delimiter, in this example &&
 * @return A CodeBlock representing this operator
 */
private fun onOperator(args: CLIArgs,
                       statement: Statement,
                       secondaryDelimiter: String,
                       primaryDelimiter: String,
                       secondaryPrefix: CodeBlock? = null,
                       secondarySuffix: CodeBlock? = null): CodeBlock? {
    val builder = CodeBlock.builder()
    //Getting the parameters
    val parameters = statement.parameter
    if (parameters.isEmpty()) {
        System.err.println("Operators need at least one parameter $statement")
        return null
    }
    //Doesn't need this special case, avoiding unnecessary brackets
    if (parameters.size < 3) {
        return onOperator(args, statement, secondaryDelimiter, secondaryPrefix, secondarySuffix)
    }
    var firstParameter = acceptStatement(args, parameters[0])
    var secondParameter = acceptStatement(args, parameters[1])
    if (firstParameter == null) {
        System.err.println("Parameter 0 of $statement is null")
        return null
    }
    if (secondParameter == null) {
        System.err.println("Parameter 1 of $statement is null")
        return null
    }
    //Building the first (...)
    builder.add("((")
    if (secondaryPrefix != null) {
        builder.add("\$L", secondaryPrefix)
    }
    builder.add("\$L$secondaryDelimiter\$L", firstParameter, secondParameter)
    if (secondarySuffix != null) {
        builder.add("\$L", secondarySuffix)
    }
    builder.add(")")
    //Building the n more 'primaryDelimiter (...)'
    for (i in 2 until parameters.size) {
        firstParameter = acceptStatement(args, parameters[i - 1])
        secondParameter = acceptStatement(args, parameters[i])
        if (firstParameter == null) {
            System.err.println("Parameter " + (i - 1) + " of " + statement + " is null")
            return null
        }
        if (secondParameter == null) {
            System.err.println("Parameter $i of $statement is null")
            return null
        }
        builder.add("$primaryDelimiter(")
        if (secondaryPrefix != null) {
            builder.add("\$L", secondaryPrefix)
        }
        builder.add("\$L$secondaryDelimiter\$L", firstParameter, secondParameter)
        if (secondarySuffix != null) {
            builder.add("\$L", secondarySuffix)
        }
        builder.add(")")
    }
    return builder.add(")").build()
}

/**
 * Constructs a CodeBlock for a normal operator with a suffix and a prefix
 *
 * @param args      The args if additional info is needed
 * @param statement The operator to be converted
 * @param delimiter The delimiter e.g. ', ' for a equality check
 * @param prefix    A prefix e.g. 'Objects.equals(' for a equality check
 * @param suffix    A suffix e.g. ')' for a equality check
 * @return A CodeBlock for the operator
 */
private fun onOperator(args: CLIArgs, statement: Statement, delimiter: String, prefix: CodeBlock? = null, suffix: CodeBlock? = null): CodeBlock? {
    val parameters = statement.parameter
    if (parameters.isEmpty()) {
        System.err.println("Operators need at least one parameter $statement")
        return null
    }
    val builder = CodeBlock.builder().add("(")
    if (prefix != null) {
        builder.add(prefix)
    }
    val firstParameter = acceptStatement(args, parameters[0])
    if (firstParameter != null) {
        builder.add(firstParameter)
    } else {
        System.err.println("Parameter 0 of $statement is null")
        return null
    }
    for (i in 1 until parameters.size) {
        val parameter = acceptStatement(args, parameters[i])
        if (parameter != null) {
            builder.add("$delimiter\$L", parameter)
        } else {
            System.err.println("Parameter $i of $statement is null")
            return null
        }
    }
    if (suffix != null) {
        builder.add(suffix)
    }
    return builder.add(")").build()
}

private fun onCall(args: CLIArgs, statement: Statement?): CodeBlock? {
    if (statement == null) {
        System.err.println("Call-Statement is null")
        return null
    }
    val builder = CodeBlock.builder()
    var firstCall = true
    val calls = statement.calls
    for (call in calls) {
        if (call.type == null) {
            System.err.println("The Type of the call $call is null")
            return null
        }
        val item = getItem(call.type!!)
        if (item == null) {
            System.err.println("Type of the call " + call + " (" + call.type + ") does not exist")
            return null
        }
        if (firstCall) {
            firstCall = false
        } else {
            builder.add(".")
        }
        when {
            item.item === ItemType.VARIABLE -> builder.add(getVariableName(item))
            item.item === ItemType.PARAMETER -> builder.add(item.name)
            item.item === ItemType.FUNCTION -> {
                builder.add("\$L(", getFunctionName(item))
                val parameters = call.parameter
                for (i in parameters.indices) {
                    val parameter = parameters[i]
                    val parametersCodeBlock = acceptStatement(args, parameter)
                    if (parametersCodeBlock == null) {
                        System.err.println("Parameter $parameter is null")
                        return null
                    }
                    builder.add(parametersCodeBlock)
                    if (i < parameters.size - 1) {
                        builder.add(", ")
                    }
                }
                builder.add(")")
            }
            else -> {
                System.err.println("The call $call is not a function, a variable or a parameter")
                return null
            }
        }
    }
    return builder.build()
}