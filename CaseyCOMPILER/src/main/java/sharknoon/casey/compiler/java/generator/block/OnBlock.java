package sharknoon.casey.compiler.java.generator.block;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.CodeBlock.Builder;
import com.squareup.javapoet.TypeName;
import javafx.geometry.Side;
import sharknoon.casey.compiler.general.CaseyParser;
import sharknoon.casey.compiler.general.beans.Block;
import sharknoon.casey.compiler.general.beans.Block.BlockContent;
import sharknoon.casey.compiler.general.beans.CLIArgs;
import sharknoon.casey.compiler.general.beans.Item;
import sharknoon.casey.compiler.general.beans.Statement;
import sharknoon.casey.compiler.java.generator.item.ItemUtils;
import sharknoon.casey.compiler.java.generator.statement.OnStatement;

import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class OnBlock {
    
    public static CodeBlock accept(CLIArgs args, Block block) {
        Builder builder = CodeBlock.builder();
        if (block == null) {
            System.err.println("The Block to be processed is null");
            return null;
        }
        if (block.blocktype == null) {
            System.err.println("The Type of the block " + block + " is null");
            return null;
        }
        switch (block.blocktype) {
            case START:
                CodeBlock startBlock = onStartBlock(args, block);
                if (startBlock == null) {
                    return null;
                }
                builder.add(startBlock);
                break;
            case END:
                CodeBlock endBlock = onEndBlock(args, block);
                if (endBlock == null) {
                    return null;
                }
                builder.add(endBlock);
                break;
            case DECISION:
                CodeBlock decisionBlock = onDecisionBlock(args, block);
                if (decisionBlock == null) {
                    return null;
                }
                builder.add(decisionBlock);
                break;
            case CALL:
                CodeBlock callBlock = onCallBlock(args, block);
                if (callBlock == null) {
                    return null;
                }
                builder.add(callBlock);
                break;
            case ASSIGNMENT:
                CodeBlock assignmentBlock = onAssignmentBlock(args, block);
                if (assignmentBlock == null) {
                    return null;
                }
                builder.add(assignmentBlock);
                break;
            case INPUT:
                CodeBlock inputBlock = onInputBlock(args, block);
                if (inputBlock == null) {
                    return null;
                }
                builder.add(inputBlock);
                break;
            case OUTPUT:
                CodeBlock outputBlock = onOutputBlock(args, block);
                if (outputBlock == null) {
                    return null;
                }
                builder.add(outputBlock);
                break;
        }
        return builder.build();
    }
    
    private static CodeBlock onStartBlock(CLIArgs args, Block block) {
        if (block == null) {
            System.err.println("The Start block is null");
            return null;
        }
        Block nextBlock = getNextBlock(block);
        if (nextBlock == null) {
            System.err.println("The Start-Block " + block + " has no next Block");
            return null;
        }
        return accept(args, nextBlock);
    }
    
    private static CodeBlock onEndBlock(CLIArgs args, Block block) {
        if (block == null) {
            System.err.println("The End block itself is not specified");
            return null;
        }
        BlockContent blockcontent = block.blockcontent;
        if (blockcontent == null) {
            System.err.println("End Block has no Content");
            return null;
        }
        Statement returnStatement = blockcontent.statement;
        if (returnStatement == null) {
            return CodeBlock
                    .builder()
                    .addStatement("return")
                    .build();
        }
        CodeBlock returnValue = OnStatement.accept(args, returnStatement);
        if (returnValue == null) {
            System.err.println("The return statement of block " + block + " is null");
            return null;
        }
        return CodeBlock
                .builder()
                .addStatement("return $L", returnValue)
                .build();
    }
    
    private static CodeBlock onDecisionBlock(CLIArgs args, Block block) {
        if (block == null) {
            System.err.println("The Decisionblock itself is not specified");
            return null;
        }
        BlockContent blockcontent = block.blockcontent;
        if (blockcontent == null) {
            System.err.println("The Decisionblock " + block + " has no condition statement");
            return null;
        }
        Statement conditionStatement = blockcontent.statement;
        if (conditionStatement == null) {
            System.err.println("The Decisionblock " + block + " has no condition statement");
            return null;
        }
        CodeBlock condition = OnStatement.accept(args, conditionStatement);
        if (condition == null) {
            System.err.println("DecisionBlock " + block + " has no condition");
            return null;
        }
        Block trueBlock = getTrueBlock(block);
        Block falseBlock = getFalseBlock(block);
        if (trueBlock == null && falseBlock == null) {
            System.err.println("DecisionBlock " + block + " has no next Blocks");
            return null;
        }
        CodeBlock whenTrue = null;
        if (trueBlock != null) {
            whenTrue = accept(args, trueBlock);
            if (whenTrue == null) {
                System.err.println("The Block for the True-Decision is null");
                return null;
            }
        }
        CodeBlock whenFalse = null;
        if (falseBlock != null) {
            whenFalse = accept(args, falseBlock);
            if (whenFalse == null) {
                System.err.println("The Block for the False-Decision is null");
                return null;
            }
        }
        
        return whenFalse != null ?
                CodeBlock
                        .builder()
                        .beginControlFlow("if ($L)", condition)
                        .add(whenTrue)
                        .nextControlFlow("else")
                        .add(whenFalse)
                        .endControlFlow()
                        .build()
                :
                CodeBlock
                        .builder()
                        .beginControlFlow("if ($L)", condition)
                        .add(whenTrue)
                        .endControlFlow()
                        .build()
                ;
    }
    
    private static CodeBlock onCallBlock(CLIArgs args, Block block) {
        if (block == null) {
            System.err.println("The Call-Block itself is not specified");
            return null;
        }
        BlockContent blockcontent = block.blockcontent;
        if (blockcontent == null) {
            System.err.println("The content of the Call-Block is not specified");
            return null;
        }
        Statement callStatement = blockcontent.statement;
        if (callStatement == null) {
            
            return null;
        }
        CodeBlock callCode = OnStatement.accept(args, callStatement);
        if (callCode == null) {
            System.err.println("The Statement for this Call-Block is null");
            return null;
        }
        Block nextBlock = getNextBlock(block);
        if (nextBlock == null) {
            System.err.println("The Call-Block " + block + " has no next Block");
            return null;
        }
        CodeBlock nextCode = accept(args, nextBlock);
        if (nextCode == null) {
            System.err.println("The following Code for this Call-Block is null");
            return null;
        }
        return CodeBlock
                .builder()
                .addStatement("$L", callCode)
                .add(nextCode)
                .build();
    }
    
    private static CodeBlock onAssignmentBlock(CLIArgs args, Block block) {
        if (block == null) {
            System.err.println("The Assignment-Block itself is not specified");
            return null;
        }
        BlockContent blockcontent = block.blockcontent;
        if (blockcontent == null) {
            System.err.println("The Content of this Assignment-Block is empty");
            return null;
        }
        String assignmentVariable = blockcontent.variable;
        if (assignmentVariable == null) {
            
            return null;
        }
        Statement assignmentStatement = blockcontent.statement;
        if (assignmentStatement == null) {
            
            return null;
        }
        Item variable = CaseyParser.NAME_TO_ITEM.get(assignmentVariable);
        if (variable == null) {
            System.err.println("The Variable of Block " + block + " is not a correct Type " + assignmentVariable);
            return null;
        }
        CodeBlock variableName = ItemUtils.getVariableName(variable);
        CodeBlock assignmentStatementCodeBlock = OnStatement.accept(args, assignmentStatement);
        if (assignmentStatementCodeBlock == null) {
            System.err.println("The Assignment of Block " + block + " is not a correct Statement");
            return null;
        }
        Block nextBlock = getNextBlock(block);
        if (nextBlock == null) {
            System.err.println("The Block " + block + " has no next Block");
            return null;
        }
        CodeBlock nextCode = accept(args, nextBlock);
        if (nextCode == null) {
            return null;
        }
        return CodeBlock
                .builder()
                .addStatement("$L = $L", variableName, assignmentStatementCodeBlock)
                .add(nextCode)
                .build();
    }
    
    
    private static CodeBlock onInputBlock(CLIArgs args, Block block) {
        if (block == null) {
            
            return null;
        }
        BlockContent blockcontent = block.blockcontent;
        if (blockcontent == null) {
            
            return null;
        }
        String inputVariable = blockcontent.variable;
        if (inputVariable == null) {
            
            return null;
        }
        Item variable = CaseyParser.NAME_TO_ITEM.get(inputVariable);
        Optional<TypeName> typeNameOptional = ItemUtils.getTypeName(variable.type);
        if (!typeNameOptional.isPresent()) {
            return null;
        }
        TypeName typeName = typeNameOptional.get();
        String methodName = "";
        String defaultValue;
        if (typeName == TypeName.DOUBLE) {
            methodName = "Double";
            defaultValue = "0.0";
        } else if (typeName == TypeName.BOOLEAN) {
            methodName = "Boolean";
            defaultValue = "false";
        } else if (typeName == ItemUtils.STRING_TYPE_NAME) {
            //Do nothing, method is .next();
            defaultValue = "\"\"";
        } else {
            System.err.println("Input type is not allowed, only Text, Boolean and Number is allowed");
            return null;
        }
        
        CodeBlock variableName = ItemUtils.getVariableName(variable);
        Block nextBlock = getNextBlock(block);
        if (nextBlock == null) {
            System.err.println("The Block " + block + " has no next Block");
            return null;
        }
        CodeBlock nextCode = accept(args, nextBlock);
        if (nextCode == null) {
            return null;
        }
        return CodeBlock
                .builder()
                .beginControlFlow("try")
                .addStatement("$T scanner = new $T($T.in)", Scanner.class, Scanner.class, System.class)
                .addStatement("$L = scanner.next" + methodName + "()", variableName)
                .nextControlFlow("catch ($T e)", Exception.class)
                .addStatement("$L = " + defaultValue, variableName)
                .addStatement("$T.err.println($S)", System.class, "Entered value not correct, using " + defaultValue + " instead")
                .endControlFlow()
                .add(nextCode)
                .build();
    }
    
    private static CodeBlock onOutputBlock(CLIArgs args, Block block) {
        if (block == null) {
            
            return null;
        }
        BlockContent blockcontent = block.blockcontent;
        if (blockcontent == null) {
            
            return null;
        }
        Statement outputStatement = blockcontent.statement;
        if (outputStatement == null) {
            
            return null;
        }
        CodeBlock outputCodeBlock = OnStatement.accept(args, outputStatement);
        if (outputCodeBlock == null) {
            System.err.println("The output value of the Block " + block + " is not a correct Statement");
            return null;
        }
        
        Block nextBlock = getNextBlock(block);
        if (nextBlock == null) {
            System.err.println("The Block " + block + " has no next Block");
            return null;
        }
        CodeBlock nextCode = accept(args, nextBlock);
        if (nextCode == null) {
            return null;
        }
        return CodeBlock
                .builder()
                .addStatement("$T.out.println($L)", System.class, outputCodeBlock)
                .add(nextCode)
                .build();
    }
    
    private static Block getNextBlock(Block block) {
        if (block.blockconnections == null || block.blockconnections.isEmpty()) {
            System.err.println("Could not get the connections for the block " + block);
            return null;
        }
        Map<UUID, Side> destination = block.blockconnections.values().iterator().next();
        if (destination == null || destination.isEmpty()) {
            System.err.println("The destinations of this block connections are null, cant get next block " + block);
            return null;
        }
        UUID next = destination.keySet().iterator().next();
        return CaseyParser.NAME_TO_BLOCK.get(next);
    }
    
    private static Block getTrueBlock(Block block) {
        return getDecisionConditionBlock(block, Side.RIGHT);
    }
    
    private static Block getDecisionConditionBlock(Block block, Side right) {
        if (block.blockconnections == null || block.blockconnections.isEmpty()) {
            System.err.println("Could not get the connections for the block " + block);
            return null;
        }
        Map<UUID, Side> destination = block.blockconnections.get(right);
        if (destination.isEmpty()) {
            System.err.println("The destinations of this block connections are null, cant get next block " + block);
            return null;
        }
        UUID next = destination.keySet().iterator().next();
        return CaseyParser.NAME_TO_BLOCK.get(next);
    }
    
    private static Block getFalseBlock(Block block) {
        return getDecisionConditionBlock(block, Side.LEFT);
    }
    
}
