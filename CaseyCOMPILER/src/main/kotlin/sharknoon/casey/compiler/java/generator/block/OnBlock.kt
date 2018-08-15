package sharknoon.casey.compiler.java.generator.block

import com.squareup.javapoet.*
import sharknoon.casey.compiler.general.*
import sharknoon.casey.compiler.general.beans.*
import sharknoon.casey.compiler.general.beans.Block.BlockType.*
import sharknoon.casey.compiler.general.beans.Block.ConnectionSide
import sharknoon.casey.compiler.java.generator.item.*
import sharknoon.casey.compiler.java.generator.statement.acceptStatement
import java.util.*

private val EMPTY_CODE_BLOCK = CodeBlock.builder().build()

fun acceptBlock(args: CLIArgs, block: Block): CodeBlock? {
    val builder = CodeBlock.builder()
    if (block.blocktype == null) {
        System.err.println("The Type of the block $block is null")
        return null
    }
    when (block.blocktype) {
        START -> {
            val startBlock = onStartBlock(args, block) ?: return null
            builder.add(startBlock)
        }
        END -> {
            val endBlock = onEndBlock(args, block) ?: return null
            builder.add(endBlock)
        }
        DECISION -> {
            val decisionBlock = onDecisionBlock(args, block) ?: return null
            builder.add(decisionBlock)
        }
        CALL -> {
            val callBlock = onCallBlock(args, block) ?: return null
            builder.add(callBlock)
        }
        ASSIGNMENT -> {
            val assignmentBlock = onAssignmentBlock(args, block) ?: return null
            builder.add(assignmentBlock)
        }
        INPUT -> {
            val inputBlock = onInputBlock(args, block) ?: return null
            builder.add(inputBlock)
        }
        OUTPUT -> {
            val outputBlock = onOutputBlock(args, block) ?: return null
            builder.add(outputBlock)
        }
    }
    return builder.build()
}

private fun onStartBlock(args: CLIArgs, block: Block): CodeBlock? {
    val nextBlock = getNextBlock(block)
    if (nextBlock == null) {
        System.err.println("The Start-Block $block has no next Block")
        return null
    }
    return acceptBlock(args, nextBlock)
}

private fun onEndBlock(args: CLIArgs, block: Block): CodeBlock? {
    val blockcontent = block.blockcontent
    if (blockcontent == null) {
        System.err.println("End Block has no Content")
        return null
    }
    val returnStatement = blockcontent.statement ?: return CodeBlock
            .builder()
            .addStatement("return")
            .build()
    val returnValue = acceptStatement(args, returnStatement)
    if (returnValue == null) {
        System.err.println("The return statement of block $block is null")
        return null
    }
    return CodeBlock
            .builder()
            .addStatement("return \$L", returnValue)
            .build()
}

private fun onDecisionBlock(args: CLIArgs, block: Block): CodeBlock? {
    val blockcontent = block.blockcontent
    if (blockcontent == null) {
        System.err.println("The Decisionblock $block has no condition statement")
        return null
    }
    val conditionStatement = blockcontent.statement
    if (conditionStatement == null) {
        System.err.println("The Decisionblock $block has no condition statement")
        return null
    }
    val condition = acceptStatement(args, conditionStatement)
    if (condition == null) {
        System.err.println("DecisionBlock $block has no condition")
        return null
    }
    val trueBlock = getTrueBlock(block)
    val falseBlock = getFalseBlock(block)
    if (trueBlock == null && falseBlock == null) {
        System.err.println("DecisionBlock $block has no next Blocks")
        return null
    }
    var whenTrue: CodeBlock = EMPTY_CODE_BLOCK
    if (trueBlock != null) {
        whenTrue = acceptBlock(args, trueBlock) ?: EMPTY_CODE_BLOCK
        if (whenTrue == null) {
            System.err.println("The Block for the True-Decision is null")
            return null
        }
    }
    var whenFalse: CodeBlock? = null
    if (falseBlock != null) {
        whenFalse = acceptBlock(args, falseBlock)
        if (whenFalse == null) {
            System.err.println("The Block for the False-Decision is null")
            return null
        }
    }

    return if (whenFalse != null)
        CodeBlock
                .builder()
                .beginControlFlow("if (\$L)", condition)
                .add(whenTrue)
                .nextControlFlow("else")
                .add(whenFalse)
                .endControlFlow()
                .build()
    else
        CodeBlock
                .builder()
                .beginControlFlow("if (\$L)", condition)
                .add(whenTrue)
                .endControlFlow()
                .build()
}

private fun onCallBlock(args: CLIArgs, block: Block): CodeBlock? {
    val blockcontent = block.blockcontent
    if (blockcontent == null) {
        System.err.println("The content of the Call-Block is not specified")
        return null
    }
    val callStatement = blockcontent.statement ?: return null
    val callCode = acceptStatement(args, callStatement)
    if (callCode == null) {
        System.err.println("The Statement for this Call-Block is null")
        return null
    }
    val nextBlock = getNextBlock(block)
    if (nextBlock == null) {
        System.err.println("The Call-Block $block has no next Block")
        return null
    }
    val nextCode = acceptBlock(args, nextBlock)
    if (nextCode == null) {
        System.err.println("The following Code for this Call-Block is null")
        return null
    }
    return CodeBlock
            .builder()
            .addStatement("\$L", callCode)
            .add(nextCode)
            .build()
}

private fun onAssignmentBlock(args: CLIArgs, block: Block): CodeBlock? {
    val blockcontent = block.blockcontent
    if (blockcontent == null) {
        System.err.println("The Content of this Assignment-Block is empty")
        return null
    }
    val assignmentVariable = blockcontent.variable ?: return null
    val assignmentStatement = blockcontent.statement ?: return null
    val variable = getItem(assignmentVariable)
    if (variable == null) {
        System.err.println("The Variable of Block $block is not a correct Type $assignmentVariable")
        return null
    }
    var variableName = getVariableName(variable)
    val assignmentStatementCodeBlock = acceptStatement(args, assignmentStatement)
    if (assignmentStatementCodeBlock == null) {
        System.err.println("The Assignment of Block $block is not a correct Statement")
        return null
    }
    if (variableName.toString() == assignmentStatementCodeBlock.toString()) {
        variableName = CodeBlock.of("this.\$L", variableName)
    }
    val nextBlock = getNextBlock(block)
    if (nextBlock == null) {
        System.err.println("The Block $block has no next Block")
        return null
    }
    val nextCode = acceptBlock(args, nextBlock) ?: return null
    return CodeBlock
            .builder()
            .addStatement("\$L = \$L", variableName, assignmentStatementCodeBlock)
            .add(nextCode)
            .build()
}


private fun onInputBlock(args: CLIArgs, block: Block): CodeBlock? {
    val blockcontent = block.blockcontent ?: return null
    val inputVariable = blockcontent.variable ?: return null
    val variable = getItem(inputVariable) ?: return null
    val typeName = getTypeName(variable.type ?: "") ?: return null
    var methodName = ""
    val defaultValue: String
    when {
        typeName === TypeName.DOUBLE -> {
            methodName = "Double"
            defaultValue = "0.0"
        }
        typeName === TypeName.BOOLEAN -> {
            methodName = "Boolean"
            defaultValue = "false"
        }
        typeName === STRING_TYPE_NAME ->
            //Do nothing, method is .next();
            defaultValue = "\"\""
        else -> {
            System.err.println("Input type is not allowed, only Text, Boolean and Number is allowed")
            return null
        }
    }

    val variableName = getVariableName(variable)
    val nextBlock = getNextBlock(block)
    if (nextBlock == null) {
        System.err.println("The Block $block has no next Block")
        return null
    }
    val nextCode = acceptBlock(args, nextBlock) ?: return null
    return CodeBlock
            .builder()
            .beginControlFlow("try")
            .addStatement("\$T scanner = new \$T(\$T.in)", Scanner::class.java, Scanner::class.java, System::class.java)
            .addStatement("\$L = scanner.next$methodName()", variableName)
            .nextControlFlow("catch (\$T e)", Exception::class.java)
            .addStatement("\$L = $defaultValue", variableName)
            .addStatement("\$T.err.println(\$S)", System::class.java, "Entered value not correct, using $defaultValue instead")
            .endControlFlow()
            .add(nextCode)
            .build()
}

private fun onOutputBlock(args: CLIArgs, block: Block): CodeBlock? {
    val blockcontent = block.blockcontent ?: return null
    val outputStatement = blockcontent.statement ?: return null
    val outputCodeBlock = acceptStatement(args, outputStatement)
    if (outputCodeBlock == null) {
        System.err.println("The output value of the Block $block is not a correct Statement")
        return null
    }

    val nextBlock = getNextBlock(block)
    if (nextBlock == null) {
        System.err.println("The Block $block has no next Block")
        return null
    }
    val nextCode = acceptBlock(args, nextBlock) ?: return null
    return CodeBlock
            .builder()
            .addStatement("\$T.out.println(\$L)", System::class.java, outputCodeBlock)
            .add(nextCode)
            .build()
}

private fun getNextBlock(block: Block): Block? {
    block.blockconnections
    if (block.blockconnections.isEmpty()) {
        System.err.println("Could not get the connections for the block $block")
        return null
    }
    val destination = block.blockconnections.values.iterator().next()
    if (destination.isEmpty()) {
        System.err.println("The destinations of this block connections are null, cant get next block $block")
        return null
    }
    val next = destination.keys.iterator().next()
    return getBlock(next)
}

private fun getTrueBlock(block: Block): Block? {
    return getDecisionConditionBlock(block, ConnectionSide.RIGHT)
}

private fun getDecisionConditionBlock(block: Block, right: ConnectionSide): Block? {
    block.blockconnections
    if (block.blockconnections.isEmpty()) {
        System.err.println("Could not get the connections for the block $block")
        return null
    }
    val destination = block.blockconnections[right]
    if (destination == null || destination.isEmpty()) {
        System.err.println("The destinations of this block connections are null, cant get next block $block")
        return null
    }
    val next = destination.keys.iterator().next()
    return getBlock(next)
}

private fun getFalseBlock(block: Block): Block? {
    return getDecisionConditionBlock(block, ConnectionSide.LEFT)
}


