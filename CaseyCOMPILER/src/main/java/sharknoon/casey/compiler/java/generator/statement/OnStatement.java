package sharknoon.casey.compiler.java.generator.statement;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.CodeBlock.Builder;
import com.squareup.javapoet.TypeName;
import sharknoon.casey.compiler.general.CaseyParser;
import sharknoon.casey.compiler.general.beans.CLIArgs;
import sharknoon.casey.compiler.general.beans.Item;
import sharknoon.casey.compiler.general.beans.Item.ItemType;
import sharknoon.casey.compiler.general.beans.Statement;
import sharknoon.casey.compiler.general.beans.Statement.Call;
import sharknoon.casey.compiler.general.beans.Statement.StatementType;
import sharknoon.casey.compiler.java.generator.item.ItemUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class OnStatement {
    private static final TypeName OBJECTS_TYPE_NAME = ClassName.get(Objects.class);
    
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
                return onOperator(args, statement, ") + String.valueOf(", CodeBlock.of("$T.valueOf(", String.class), CodeBlock.of(")"));
            case DIVIDE:
                return onOperator(args, statement, " / ");
            case EQUALS:
                if (statement.parameter.size() < 2) {
                    System.err.println("The Not Equals Operator needs to have two or more parameters");
                    return null;
                }
                return onOperator(args, statement, ", ", " && ", CodeBlock.of("$T.equals(", OBJECTS_TYPE_NAME), CodeBlock.of(")"));
            case GREATER_OR_EQUAL_THAN:
                if (statement.parameter.size() < 2) {
                    System.err.println("The Greater Or Equals Than Operator needs to have two or more parameters");
                    return null;
                }
                return onOperator(args, statement, " >= ", " && ");
            case GREATER_THAN:
                if (statement.parameter.size() < 2) {
                    System.err.println("The Greater Than Operator needs to have two or more parameters");
                    return null;
                }
                return onOperator(args, statement, " > ", " && ");
            case LENGTH:
                if (statement.parameter.size() != 1) {
                    System.err.println("The Length Operator needs to have exactly one parameter");
                    return null;
                }
                return onOperator(args, statement, "", null, CodeBlock.of(".length()"));
            case LESS_OR_EQUAL_THAN:
                if (statement.parameter.size() < 2) {
                    System.err.println("The Less Or Equal Than Operator needs to have two or more parameters");
                    return null;
                }
                return onOperator(args, statement, " <= ", " && ");
            case LESS_THAN:
                if (statement.parameter.size() < 2) {
                    System.err.println("The Less Than Operator needs to have two or more parameters");
                    return null;
                }
                return onOperator(args, statement, " < ", " && ");
            case MODULO:
                return onOperator(args, statement, " % ");
            case MULTIPLY:
                return onOperator(args, statement, " * ");
            case NOT_EQUALS:
                if (statement.parameter.size() < 2) {
                    System.err.println("The Not Equals Operator needs to have two or more parameters");
                    return null;
                }
                return onOperator(args, statement, ", ", " && ", CodeBlock.of("!$T.equals(", OBJECTS_TYPE_NAME), CodeBlock.of(")"));
            case NOT:
                if (statement.parameter.size() != 1) {
                    System.err.println("The NOT Operator needs to have exactly one parameter");
                    return null;
                }
                return onOperator(args, statement, "", CodeBlock.of("!"), null);
            case OR:
                return onOperator(args, statement, " || ");
            case SUBTRACT:
                return onOperator(args, statement, " - ");
            //Calls
            case CALL:
                return onCall(args, statement);
        }
        return null;
    }
    
    /**
     * This creates a CodeBlock fo a simple value without brackets
     *
     * @param statement The value statement
     * @return The CodeBlock without brackets or null if the statement is null or has a wrong object type
     */
    private static CodeBlock onValue(Statement statement) {
        if (statement.value == null) {
            System.err.println("Value of statement " + statement + " is null");
            return null;
        }
        //Text
        if (statement.type == StatementType.TEXT) {
            return CodeBlock.of("$S", statement.value);
            //Object
        } else if (statement.type == StatementType.OBJECT) {
            Optional<TypeName> optionalTypeName = ItemUtils.getTypeName(String.valueOf(statement.value));
            if (!optionalTypeName.isPresent()) {
                System.err.println("Could not get the type name for this object statement " + statement);
                return null;
            }
            return CodeBlock.of("new $T()", optionalTypeName.get());
            //Boolean or Number
        } else {
            return CodeBlock.of("$L", statement.value);
        }
    }
    
    /**
     * Returns a CodeBlock for this a standard operator like add or and
     *
     * @param args      The args if additional info is needed
     * @param statement The statement to be converted
     * @param delimiter The delimiter e.g. + in a addition
     * @return The newly created CodeBlock
     */
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
    private static CodeBlock onOperator(CLIArgs args,
                                        Statement statement,
                                        String secondaryDelimiter,
                                        String primaryDelimiter) {
        return onOperator(args, statement, secondaryDelimiter, primaryDelimiter, null, null);
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
    private static CodeBlock onOperator(CLIArgs args,
                                        Statement statement,
                                        String secondaryDelimiter,
                                        String primaryDelimiter,
                                        CodeBlock secondaryPrefix,
                                        CodeBlock secondarySuffix) {
        Builder builder = CodeBlock.builder();
        //Getting the parameters
        List<Statement> parameters = statement.parameter;
        if (parameters == null) {
            System.err.println("Parameter of the statement " + statement + " is null");
            return null;
        }
        if (parameters.size() < 1) {
            System.err.println("Operators need at least one parameter " + statement);
            return null;
        }
        //Doesn't need this special case, avoiding unnecessary brackets
        if (parameters.size() < 3) {
            return onOperator(args, statement, secondaryDelimiter, secondaryPrefix, secondarySuffix);
        }
        CodeBlock firstParameter = accept(args, parameters.get(0));
        CodeBlock secondParameter = accept(args, parameters.get(1));
        if (firstParameter == null) {
            System.err.println("Parameter 0 of " + statement + " is null");
            return null;
        }
        if (secondParameter == null) {
            System.err.println("Parameter 1 of " + statement + " is null");
            return null;
        }
        //Building the first (...)
        builder.add("((");
        if (secondaryPrefix != null) {
            builder.add("$L", secondaryPrefix);
        }
        builder.add("$L" + secondaryDelimiter + "$L", firstParameter, secondParameter);
        if (secondarySuffix != null) {
            builder.add("$L", secondarySuffix);
        }
        builder.add(")");
        //Building the n more 'primaryDelimiter (...)'
        for (int i = 2; i < parameters.size(); i++) {
            firstParameter = accept(args, parameters.get(i - 1));
            secondParameter = accept(args, parameters.get(i));
            if (firstParameter == null) {
                System.err.println("Parameter " + (i - 1) + " of " + statement + " is null");
                return null;
            }
            if (secondParameter == null) {
                System.err.println("Parameter " + i + " of " + statement + " is null");
                return null;
            }
            builder.add(primaryDelimiter + "(");
            if (secondaryPrefix != null) {
                builder.add("$L", secondaryPrefix);
            }
            builder.add("$L" + secondaryDelimiter + "$L", firstParameter, secondParameter);
            if (secondarySuffix != null) {
                builder.add("$L", secondarySuffix);
            }
            builder.add(")");
        }
        return builder.add(")").build();
    }
    
    /**
     * Constructs a CodeBlock for a normal operator with a suffix and a prefix
     *
     * @param args      The args if additional info is needed
     * @param statement The operator to be converted
     * @param delimiter The delimiter e.g. ', ' for a equality check
     * @param prefix    A prefix e.g. 'Objects.equals(' for a equality check
     * @param suffix    A suffic e.g. ')' for a equality check
     * @return A CodeBlock for the operator
     */
    private static CodeBlock onOperator(CLIArgs args, Statement statement, String delimiter, CodeBlock prefix, CodeBlock suffix) {
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
            CodeBlock parameter = accept(args, parameters.get(i));
            if (parameter != null) {
                builder.add(delimiter + "$L", parameter);
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
    
    private static CodeBlock onCall(CLIArgs args, Statement statement) {
        if (statement == null) {
            System.err.println("Call-Statement is null");
            return null;
        }
        if (statement.calls == null) {
            System.err.println("Calls of the Call Statement " + statement + " is  null");
            return null;
        }
        Builder builder = CodeBlock.builder();
        boolean firstCall = true;
        List<Call> calls = statement.calls;
        for (Call call : calls) {
            if (call == null) {
                System.err.println("A call of the statement " + statement + " is null");
                return null;
            }
            if (call.type == null) {
                System.err.println("The Type of the call " + call + " is null");
                return null;
            }
            Item item = CaseyParser.NAME_TO_ITEM.get(call.type);
            if (item == null) {
                System.err.println("Type of the call " + call + " (" + call.type + ") does not exist");
                return null;
            }
            if (firstCall) {
                firstCall = false;
            } else {
                builder.add(".");
            }
            if (item.item == ItemType.VARIABLE) {
                builder.add(ItemUtils.getVariableName(item));
            } else if (item.item == ItemType.PARAMETER) {
                builder.add(item.name);
            } else if (item.item == ItemType.FUNCTION) {
                builder.add("$L(", ItemUtils.getFunctionName(item));
                for (Statement parameter : call.parameter) {
                    CodeBlock parameterCodeBlock = accept(args, parameter);
                    if (parameterCodeBlock == null) {
                        System.err.println("Parameter " + parameter + " is null");
                        return null;
                    }
                    builder.add(parameterCodeBlock);
                }
                builder.add(")");
            } else {
                System.err.println("The call " + call + " is not a function, a variable or a parameter");
                return null;
            }
        }
        return builder.build();
    }
    
}
