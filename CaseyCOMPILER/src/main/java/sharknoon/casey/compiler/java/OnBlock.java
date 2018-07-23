package sharknoon.casey.compiler.java;

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

import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class OnBlock {
    
    static CodeBlock accept(CLIArgs args, Block block) {
        Builder builder = CodeBlock.builder();
        accept(args, block, builder);
        return builder.build();
    }
    
    private static void accept(CLIArgs args, Block block, Builder builder) {
        if (block == null) {
            
            return;
        }
        if (block.blocktype == null) {
            
            return;
        }
        switch (block.blocktype) {
            case START:
                accept(args, getNextBlock(block), builder);
                break;
            case END:
                builder.add(onEndBlock(args, block));
                break;
            case DECISION:
                builder.add(onDecisionBlock(args, block));
                break;
            case CALL:
                builder.add(onCallBlock(args, block));
                break;
            case ASSIGNMENT:
                builder.add(onAssignmentBlock(args, block));
                break;
            case INPUT:
                builder.add(onInputBlock(args, block));
                break;
            case OUTPUT:
                builder.add(onOutputBlock(args, block));
                break;
        }
    }
    
    private static CodeBlock onEndBlock(CLIArgs args, Block block) {
        if (block == null) {
            
            return null;
        }
        BlockContent blockcontent = block.blockcontent;
        if (blockcontent == null) {
            
            return null;
        }
        Statement returnStatement = blockcontent.statement;
        if (returnStatement == null) {
            
            return CodeBlock
                    .builder()
                    .addStatement("return")
                    .build();
        }
        return CodeBlock
                .builder()
                .addStatement("return $L", OnStatement.accept(args, returnStatement))
                .build();
    }
    
    private static CodeBlock onDecisionBlock(CLIArgs args, Block block) {
        if (block == null) {
            
            return null;
        }
        BlockContent blockcontent = block.blockcontent;
        if (blockcontent == null) {
            
            return null;
        }
        Statement decisionStatement = blockcontent.statement;
        if (decisionStatement == null) {
            
            return null;
        }
        return CodeBlock
                .builder()
                .beginControlFlow("if ($L)", OnStatement.accept(args, decisionStatement))
                .add(accept(args, getTrueBlock(block)))
                .nextControlFlow("else")
                .add(accept(args, getFalseBlock(block)))
                .endControlFlow()
                .build();
    }
    
    private static CodeBlock onCallBlock(CLIArgs args, Block block) {
        if (block == null) {
            
            return null;
        }
        BlockContent blockcontent = block.blockcontent;
        if (blockcontent == null) {
            
            return null;
        }
        Statement callStatement = blockcontent.statement;
        if (callStatement == null) {
            
            return null;
        }
        return CodeBlock
                .builder()
                .addStatement("$L", OnStatement.accept(args, callStatement))
                .add(accept(args, getNextBlock(block)))
                .build();
    }
    
    private static CodeBlock onAssignmentBlock(CLIArgs args, Block block) {
        if (block == null) {
            
            return null;
        }
        BlockContent blockcontent = block.blockcontent;
        if (blockcontent == null) {
            
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
        //TODO right variable name
        return CodeBlock
                .builder()
                .addStatement("$L = $L", variable.name, OnStatement.accept(args, assignmentStatement))
                .add(accept(args, getNextBlock(block)))
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
        String defaultValue = "";
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
        //TODO right variable name
        return CodeBlock
                .builder()
                .beginControlFlow("try")
                .addStatement("$T scanner = new $T($T.in)", Scanner.class, Scanner.class, System.class)
                .addStatement("$L = scanner.next" + methodName + "()", variable.name)
                .nextControlFlow("catch ($T e)", Exception.class)
                .addStatement("$L = " + defaultValue, variable.name)
                .addStatement("$T.err.println($S)", System.class, "Entered value not correct, using " + defaultValue + " instead")
                .endControlFlow()
                .add(accept(args, getNextBlock(block)))
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
        return CodeBlock
                .builder()
                .addStatement("$T.out.println($L)", System.class, OnStatement.accept(args, outputStatement))
                .add(accept(args, getNextBlock(block)))
                .build();
    }
    
    private static Block getNextBlock(Block block) {
        if (block.blockconnections == null || block.blockconnections.isEmpty()) {
            
            return null;
        }
        Map<UUID, Side> destination = block.blockconnections.values().iterator().next();
        if (destination == null || destination.isEmpty()) {
            
            return null;
        }
        UUID next = destination.keySet().iterator().next();
        return CaseyParser.NAME_TO_BLOCK.get(next);
    }
    
    private static Block getTrueBlock(Block block) {
        return getDecisionBlock(block, Side.RIGHT);
    }
    
    private static Block getDecisionBlock(Block block, Side right) {
        if (block.blockconnections == null || block.blockconnections.isEmpty()) {
            
            return null;
        }
        Map<UUID, Side> destination = block.blockconnections.get(right);
        if (destination.isEmpty()) {
            
            return null;
        }
        UUID next = destination.keySet().iterator().next();
        return CaseyParser.NAME_TO_BLOCK.get(next);
    }
    
    private static Block getFalseBlock(Block block) {
        return getDecisionBlock(block, Side.LEFT);
    }
    
}
