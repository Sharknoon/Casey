package sharknoon.casey.compiler.java;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.CodeBlock.Builder;
import com.squareup.javapoet.TypeName;
import sharknoon.casey.compiler.general.beans.CLIArgs;
import sharknoon.casey.compiler.general.beans.Statement;
import sharknoon.casey.compiler.general.beans.Statement.StatementType;

import java.util.List;
import java.util.Optional;

public class OnStatement {
    
    public static CodeBlock accept(CLIArgs args, Statement statement) {
        if (statement == null) {
            System.err.println("Statement is null, null statements are not allowed!");
            return null;
        }
        if (statement.type == null) {
            System.err.println("Type of the statement is null");
            return null;
        }
        switch (statement.type) {
            //Values
            case NUMBER:
            case BOOLEAN:
            case TEXT:
            case OBJECT:
                return onValue(statement);
            //Operators
            case ADD:
                return onOperator(args, statement, " + ");
            case AND:
                return onOperator(args, statement, " && ");
            case CONCAT:
                return onOperator(args, statement, ") + String.valueOf(", "String.valueOf(", ")");
            case DIVIDE:
                return onOperator(args, statement, " / ");
            case EQUALS:
                if (statement.parameter.size() != 2) {
                    System.err.println("The Not Equals Operator needs to have exactly two parameters");
                    return null;
                }
                return onOperator(args, statement, ", ", "Objects.equals(", ")");
            case GREATEROREQUALTHAN:
            
            case GREATERTHAN:
            
            case LENGTH:
                if (statement.parameter.size() != 1) {
                    System.err.println("The Length Operator needs to have exactly one parameter");
                    return null;
                }
                return onOperator(args, statement, "", "", ".length()");
            case LESSOREQUALTHAN:
            
            case LESSTHAN:
            
            case MODULO:
                return onOperator(args, statement, " % ");
            case MULTIPLY:
                return onOperator(args, statement, " * ");
            case NOTEQUALS:
                if (statement.parameter.size() != 2) {
                    System.err.println("The Not Equals Operator needs to have exactly two operators");
                    return null;
                }
                return onOperator(args, statement, ", ", "!Objects.equals(", ")");
            case NOT:
                if (statement.parameter.size() != 1) {
                    System.err.println("The NOT Operator needs to have exactly one parameter");
                    return null;
                }
                return onOperator(args, statement, "", "!", "");
            case OR:
                return onOperator(args, statement, " || ");
            case SUBTRACT:
                return onOperator(args, statement, " - ");
        }
        return null;
    }
    
    private static CodeBlock onValue(Statement statement) {
        if (statement.value == null) {
            System.err.println("Value of statement " + statement + " is null");
            return null;
        }
        if (statement.type == StatementType.TEXT) {
            return CodeBlock.of("$S", statement.value);
        } else if (statement.type == StatementType.OBJECT) {
            Optional<TypeName> optionalTypeName = ItemUtils.getTypeName(String.valueOf(statement.value));
            if (!optionalTypeName.isPresent()) {
                System.err.println("Could not get the type name for this object statement " + statement);
                return null;
            }
            return CodeBlock.of("new $T()", optionalTypeName.get());
        } else {
            return CodeBlock.of("$L", statement.value);
        }
    }
    
    private static CodeBlock onOperator(CLIArgs args, Statement statement, String delimiter) {
        return onOperator(args, statement, delimiter, null, null);
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
    private static CodeBlock onOperator(CLIArgs args, Statement statement, String secondaryDelimiter, boolean primaryDelimiter) {
        List<Statement> parameters = statement.parameter;
        if (parameters == null) {
            System.err.println("Parameter of the statement " + statement + " is null");
            return null;
        }
        if (parameters.size() < 1) {
            System.err.println("Operators need at least one parameter " + statement);
            return null;
        }
        Builder builder = CodeBlock.builder();
        for (int i = 1; i < parameters.size(); i++) {
            CodeBlock firstParameter = accept(args, parameters.get(i - 1));
            CodeBlock secondParameter = accept(args, parameters.get(i));
            if (firstParameter)
                if (firstParameter != null && secondParameter != null) {
                    builder.add("(").add(prevParameter).add(" ").add(secondaryDelimiter).add(" ").add(firstParameter).add(")");
                } else {
                    System.err.println("Parameter " + i + " of " + statement + " is null");
                    return null;
                }
        }
        
    }
    
    private static CodeBlock onOperator(CLIArgs args, Statement statement, String delimiter, String prefix, String suffix) {
        List<Statement> parameters = statement.parameter;
        if (parameters == null) {
            System.err.println("Parameter of the statement " + statement + " is null");
            return null;
        }
        if (parameters.size() < 1) {
            System.err.println("Operators need at least one parameter " + statement);
            return null;
        }
        Builder builder = CodeBlock.builder().add("(");
        if (prefix != null) {
            builder.add(prefix);
        }
        CodeBlock firstParameter = accept(args, parameters.get(0));
        if (firstParameter != null) {
            builder.add(firstParameter);
        } else {
            System.err.println("Parameter 0 of " + statement + " is null");
            return null;
        }
        for (int i = 1; i < parameters.size(); i++) {
            CodeBlock parameter = accept(args, parameters.get(0));
            if (parameter != null) {
                builder.add(delimiter);
                builder.add(parameter);
            } else {
                System.err.println("Parameter " + i + " of " + statement + " is null");
                return null;
            }
        }
        if (suffix != null) {
            builder.add(suffix);
        }
        return builder.add(")").build();
    }
    
}
