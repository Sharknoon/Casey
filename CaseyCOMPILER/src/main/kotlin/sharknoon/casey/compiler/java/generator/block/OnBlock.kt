package sharknoon.casey.compiler.java.generator.block

import com.squareup.javapoet.*
import sharknoon.casey.compiler.general.*
import sharknoon.casey.compiler.general.cli.CLIArgs
import sharknoon.casey.compiler.general.parser.beans.Block
import sharknoon.casey.compiler.general.parser.beans.Block.BlockType.*
import sharknoon.casey.compiler.general.parser.getItem
import sharknoon.casey.compiler.java.generator.item.*
import sharknoon.casey.compiler.java.generator.statement.acceptStatement
import java.util.*

private val EMPTY_CODE_BLOCK = CodeBlock.builder().build()

fun acceptFunctionBlocks(args: CLIArgs, startBlock: Block): CodeBlock? {
    val builder = CodeBlock.builder()
    if (startBlock.blocktype != START) {
        System.err.println("The start of this function is not null ($startBlock)")
        return null
    }

    val skeleton = getSkeleton(startBlock) ?: return null
    for (subSkeleton in skeleton.SubSkeletons) {
        val codeBlock = when (subSkeleton) {
            is SingleBlock -> acceptSingleBlock(args, subSkeleton) ?: return null
            is Decision -> acceptDecisionSkeleton(args, subSkeleton) ?: return null
            is Loop -> acceptLoopSkeleton(args, subSkeleton) ?: return null
            else -> return null
        }
        builder.add(codeBlock)
    }

    return builder.build()
}

private fun acceptSingleBlock(args: CLIArgs, singleBlock: SingleBlock): CodeBlock? = onSingleBlock(args, singleBlock.block)

private fun acceptDecisionSkeleton(args: CLIArgs, decision: Decision): CodeBlock? = onDecisionBlock(args, decision.condition, decision.trueBlocks, decision.falseBlocks)

private fun acceptLoopSkeleton(args: CLIArgs, loop: Loop): CodeBlock? = onLoopBlock(args, loop.condition, loop.beforeCondition, loop.afterCondition)

private fun onSingleBlock(args: CLIArgs, block: Block): CodeBlock? {
    return when (block.blocktype) {
        START -> EMPTY_CODE_BLOCK
        END -> onEndBlock(args, block) ?: return null
        //DECISION -> onDecisionBlock(args, block) ?: return null
        CALL -> onCallBlock(args, block) ?: return null
        ASSIGNMENT -> onAssignmentBlock(args, block) ?: return null
        INPUT -> onInputBlock(block) ?: return null
        OUTPUT -> onOutputBlock(args, block) ?: return null
        else -> null
    }
}

private fun onDecisionBlock(args: CLIArgs, decisionBlock: Block, trueBlocks: List<Block>, falseBlocks: List<Block>): CodeBlock? {
    val blockcontent = decisionBlock.blockcontent
    if (blockcontent == null) {
        System.err.println("The Decisionblock $decisionBlock has no condition statement")
        return null
    }
    val conditionStatement = blockcontent.statement
    if (conditionStatement == null) {
        System.err.println("The Decisionblock $decisionBlock has no condition statement")
        return null
    }

    val conditionCodeBlock = acceptStatement(args, conditionStatement)
    if (conditionCodeBlock == null) {
        System.err.println("DecisionBlock $decisionBlock has no condition")
        return null
    }

    val trueCodeBlock = CodeBlock.builder()
    for (trueBlock in trueBlocks) {
        val block = onSingleBlock(args, trueBlock)
        if (block == null) {
            System.err.println("Error in the true-Condition of this condition-block $decisionBlock")
            return null
        }
        trueCodeBlock.add(block)
    }

    val falseCodeBlock = CodeBlock.builder()
    for (falseBlock in falseBlocks) {
        val block = onSingleBlock(args, falseBlock)
        if (block == null) {
            System.err.println("Error in the false-Condition of this condition-block $decisionBlock")
            return null
        }
        falseCodeBlock.add(block)
    }


    return if (!falseCodeBlock.isEmpty)
        CodeBlock
                .builder()
                .beginControlFlow("if (\$L)", conditionCodeBlock)
                .add(trueCodeBlock.build())
                .nextControlFlow("else")
                .add(falseCodeBlock.build())
                .endControlFlow()
                .build()
    else
        CodeBlock
                .builder()
                .beginControlFlow("if (\$L)", conditionCodeBlock)
                .add(trueCodeBlock.build())
                .endControlFlow()
                .build()
}

private fun onLoopBlock(args: CLIArgs, decisionBlock: Block, beforeCondition: List<Block>, afterCondition: List<Block>): CodeBlock? {
    val blockcontent = decisionBlock.blockcontent
    if (blockcontent == null) {
        System.err.println("The Decisionblock $decisionBlock has no Decisionblock statement")
        return null
    }
    val conditionStatement = blockcontent.statement
    if (conditionStatement == null) {
        System.err.println("The Decisionblock $decisionBlock has no Decisionblock statement")
        return null
    }

    val conditionCodeBlock = acceptStatement(args, conditionStatement)
    if (conditionCodeBlock == null) {
        System.err.println("DecisionBlock $decisionBlock has no condition")
        return null
    }

    if (beforeCondition.isEmpty() && afterCondition.isEmpty()) {
        System.err.println("Both conditions of the loop with the Decisionblock $decisionBlock are empty")
        return null
    }

    val beforeCodeBlock = CodeBlock.builder()
    for (beforeBlock in beforeCondition) {
        val block = onSingleBlock(args, beforeBlock)
        if (block == null) {
            System.err.println("Error in the Loop before the Decisionblock $decisionBlock")
            return null
        }
        beforeCodeBlock.add(block)
    }

    val afterCodeBlock = CodeBlock.builder()
    for (afterBlock in afterCondition) {
        val block = onSingleBlock(args, afterBlock)
        if (block == null) {
            System.err.println("Error in the Loop after the Decisionblock $decisionBlock")
            return null
        }
        afterCodeBlock.add(block)
    }

    if (beforeCondition.isEmpty() && afterCondition.isNotEmpty()) {
        return CodeBlock
                .builder()
                .beginControlFlow("while (\$L)", conditionCodeBlock)
                .add(afterCodeBlock.build())
                .endControlFlow()
                .build()
    }

    if (beforeCondition.isNotEmpty() && afterCondition.isEmpty()) {
        return CodeBlock
                .builder()
                .beginControlFlow("do")
                .add(beforeCodeBlock.build())
                .endControlFlow("while (\$L)", conditionCodeBlock)
                .build()
    }

    return CodeBlock
            .builder()
            .add(beforeCodeBlock.build())
            .beginControlFlow("while (\$L)", conditionCodeBlock)
            .add(afterCodeBlock.build())
            .add(beforeCodeBlock.build())
            .endControlFlow()
            .build()
}

//private fun moveToNextBlock(args: CLIArgs, currentBlock: Block): CodeBlock? {
//    val nextBlock = getNextBlock(currentBlock)
//    if (nextBlock == null) {
//        System.err.println("The Block $currentBlock no next Block")
//        return null
//    }
//    return acceptFunctionBlocks(args, nextBlock)
//}
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

    return CodeBlock
            .builder()
            .addStatement("\$L", callCode)
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

    return CodeBlock
            .builder()
            .addStatement("\$L = \$L", variableName, assignmentStatementCodeBlock)
            .build()
}


private fun onInputBlock(block: Block): CodeBlock? {
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
    return CodeBlock
            .builder()
            .beginControlFlow("try")
            .addStatement("\$T scanner = new \$T(\$T.in)", Scanner::class.java, Scanner::class.java, System::class.java)
            .addStatement("\$L = scanner.next$methodName()", variableName)
            .nextControlFlow("catch (\$T e)", Exception::class.java)
            .addStatement("\$L = $defaultValue", variableName)
            .addStatement("\$T.err.println(\$S)", System::class.java, "Entered value not correct, using $defaultValue instead")
            .endControlFlow()
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

    return CodeBlock
            .builder()
            .addStatement("\$T.out.println(\$L)", System::class.java, outputCodeBlock)
            .build()
}

//private fun getNextBlock(block: Block): Block? {
//    block.blockconnections
//    if (block.blockconnections.isEmpty()) {
//        System.err.println("Could not get the connections for the block $block")
//        return null
//    }
//    val destination = block.blockconnections.values.iterator().next()
//    if (destination.isEmpty()) {
//        System.err.println("The destinations of this block connections are null, cant get next block $block")
//        return null
//    }
//    val next = destination.keys.iterator().next()
//    return getBlock(next)
//}
//
//private fun getTrueBlock(block: Block): Block? {
//    val trueblock = getDecisionConditionBlock(block, ConnectionSide.RIGHT)
//    if (trueblock == null) {
//        System.err.println("Could not get the required block for the true-condition of this condition-block $block")
//    }
//    return trueblock
//}
//
//private fun getFalseBlock(block: Block): Block? {
//    return getDecisionConditionBlock(block, ConnectionSide.LEFT)
//}
//
//private fun getDecisionConditionBlock(block: Block, side: ConnectionSide): Block? {
//    if (block.blockconnections.isEmpty()) {
//        //System.err.println("Could not get the connections for the block $block")
//        return null
//    }
//    val destination = block.blockconnections[side]
//    if (destination == null || destination.isEmpty()) {
//        //System.err.println("The destinations of this block connections are null, cant get next block $block")
//        return null
//    }
//    val next = destination.keys.iterator().next()
//    return getBlock(next)
//}




