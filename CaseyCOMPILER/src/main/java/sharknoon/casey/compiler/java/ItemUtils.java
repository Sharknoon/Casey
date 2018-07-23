package sharknoon.casey.compiler.java;

import com.google.common.collect.Streams;
import com.squareup.javapoet.*;
import com.squareup.javapoet.CodeBlock.Builder;
import org.apache.commons.lang3.EnumUtils;
import org.jsoup.Jsoup;
import sharknoon.casey.compiler.general.CaseyParser;
import sharknoon.casey.compiler.general.beans.Block;
import sharknoon.casey.compiler.general.beans.Block.BlockType;
import sharknoon.casey.compiler.general.beans.CLIArgs;
import sharknoon.casey.compiler.general.beans.Item;
import sharknoon.casey.compiler.general.beans.Item.ItemType;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemUtils {
    
    static final TypeName STRING_TYPE_NAME = ClassName.get("java.lang", "String");
    private static final String EMPTY = "";
    private static final List<Modifier> FUNCTION_ALONE_MODIFIERS = List.of(Modifier.PUBLIC, Modifier.STATIC);
    private static final List<Modifier> FUNCTION_IN_CLASS_MODIFIERS = List.of(Modifier.PUBLIC);
    
    /**
     * Extracts the HTML of the item, checks for nulls and encapsulates the body from the comment
     *
     * @param args The CLIArgs to check if comments should be ignored
     * @param item The item which the comments shold be extracted
     * @return The comments or a empty string of a error occurs
     */
    static String getJavaDoc(CLIArgs args, Item item) {
        if (args.getIgnoreComments()) {
            return EMPTY;
        }
        if (item == null) {
            return EMPTY;
        }
        if (item.comments == null || item.comments.isEmpty()) {
            return EMPTY;
        }
        try {
            return Jsoup.parse(item.comments).body().child(0).toString() + "\n";
        } catch (Exception e) {
            System.out.println("Warning, could not load comments " + e);
        }
        return EMPTY;
    }
    
    private static String getJavaDocFormatted(String htmlJavaDoc) {
        if (htmlJavaDoc == null || htmlJavaDoc.isEmpty()) {
            return EMPTY;
        }
        String formatted = Arrays.stream(htmlJavaDoc.split("\\r?\\n")).map(s -> " * " + s).collect(Collectors.joining("\n"));
        formatted = "/**\n" + formatted + "\n */\n";
        return formatted;
    }
    
    /**
     * Converts a Path to a directory to a Java-Classpath
     *
     * @param path The Path to be converted
     * @return The Java-Classpath
     */
    static String pathToClassPath(Path path) {
        return Streams.stream(path.iterator())
                .map(Path::toString)
                .collect(Collectors.joining("."));
    }
    
    /**
     * Writes a "comments.txt" file with the comments of this item, if this item has comments and comments arent ignored
     *
     * @param args The CLIArgs to check if comments are ignored
     * @param item The Item which comments should be written, typically a project or a package, class, variables and
     *             functions are writing the comments inside ítself
     * @param path The path to write the file into
     */
    static void writeComments(CLIArgs args, Item item, Path path) {
        if (args.getIgnoreComments()) {
            return;
        }
        try {
            if (item.comments != null && !item.comments.isEmpty()) {
                Files.write(
                        path.resolve("comments.html"),
                        Stream.of(item.comments)
                                .map(c -> c.split("\\r?\\n"))
                                .map(Arrays::stream)
                                .flatMap(s -> s)
                                .collect(Collectors.toList()),
                        StandardCharsets.UTF_8
                );
            }
        } catch (IOException e) {
            System.out.println("Could not write comments: " + e);
        }
    }
    
    /**
     * Returns the full name of a item (e.g. Project.package.Clazz) or a empty String if this item is isnt registered
     *
     * @param item The item which full name should be returned
     * @return The full name or a empty String
     */
    static String getFullName(Item item) {
        return CaseyParser.ITEM_TO_NAME.getOrDefault(item, "");
    }
    
    /**
     * Checks weather this item was selected via CLI arguments as main method or not
     *
     * @param args The CLIArgs to get the main method
     * @param item The item to get its name
     * @return True, if this is the method to start with
     */
    static boolean isMainMethod(CLIArgs args, Item item) {
        return getFullName(item).equals(args.getFunction());
    }
    
    /**
     * Returns a TypeName for JavaPoet based on my primitive Strings or a empty Optional, if the class was not
     * registered
     *
     * @param typeNameString The full name of the item
     * @return The typename or a empty Optional
     */
    static Optional<TypeName> getTypeName(String typeNameString) {
        TypeName typeName = null;
        //If is primitive or String
        if (EnumUtils.isValidEnum(VariableType.class, typeNameString)) {
            VariableType varType = VariableType.valueOf(typeNameString);
            switch (varType) {
                case BOOLEAN:
                    typeName = TypeName.BOOLEAN;
                    break;
                case NUMBER:
                    typeName = TypeName.DOUBLE;
                    break;
                case TEXT:
                    typeName = STRING_TYPE_NAME;
                    break;
                case VOID:
                    typeName = TypeName.VOID;
                    break;
                default:
                    System.err.println("Could not determine Variable type");
            }
            //Is a class
        } else if (CaseyParser.NAME_TO_ITEM.keySet().contains(typeNameString)) {
            int lastPointIndex = typeNameString.lastIndexOf(".");
            typeName = ClassName.get(
                    typeNameString.substring(0, lastPointIndex),
                    typeNameString.substring(lastPointIndex + 1, typeNameString.length())
            );
        } else {
            System.err.println("Could not determine type for " + typeNameString);
        }
        return Optional.ofNullable(typeName);
    }
    
    /**
     * Initializes a field with a basic type to avoid null
     *
     * @param type The type be be initialized
     * @return The initializing Codeblock
     */
    static CodeBlock getFieldInitializer(TypeName type) {
        if (TypeName.BOOLEAN.equals(type)) {
            return CodeBlock.of("false");
        }
        if (TypeName.DOUBLE.equals(type)) {
            return CodeBlock.of("0.0");
        }
        if (STRING_TYPE_NAME.equals(type)) {
            return CodeBlock.of("\"\"");
        }
        if (!type.isPrimitive()) {
            return CodeBlock.of("new $T()", type);
        }
        System.err.println("Could not determine initializer for field " + type);
        return CodeBlock.builder().build();
    }
    
    /**
     * Returns a function for a item
     *
     * @param args         The args to specify, weather comments should be added or not
     * @param functionItem The item to be converted
     * @param isStatic     true if this item is not in a class, false oif it is in a package
     * @return A optional if methodspec
     */
    static Optional<MethodSpec> getFunction(CLIArgs args, Item functionItem, boolean isStatic) {
        if (functionItem == null) {
            System.err.println("Function itself not specified");
            return Optional.empty();
        }
        if (functionItem.item == null) {
            System.err.println("Type of this function is not specified " + getFullName(functionItem));
            return Optional.empty();
        }
        if (!ItemType.FUNCTION.equals(functionItem.item)) {
            return Optional.empty();
        }
        if (functionItem.name == null) {
            System.err.println("Name of function not specified " + getFullName(functionItem));
            return Optional.empty();
        }
        if (functionItem.returntype == null) {
            System.err.println("Returntype of function not specified " + getFullName(functionItem));
            return Optional.empty();
        }
        
        List<Modifier> modifiers = isStatic ? FUNCTION_ALONE_MODIFIERS : FUNCTION_IN_CLASS_MODIFIERS;
        String returnTypeNameString = functionItem.returntype;
        Optional<TypeName> optionalReturnTypeName = ItemUtils.getTypeName(returnTypeNameString);
        if (!optionalReturnTypeName.isPresent()) {
            return Optional.empty();
        }
        TypeName returnTypeName = optionalReturnTypeName.get();
        String functionName = functionItem.name;
        List<ParameterSpec> parameters = ItemUtils.getParameters(functionItem);
        String parameterJavaDoc = getParameterJavaDoc(args, functionItem);
        List<CodeBlock> variables = getVariables(args, functionItem);
        Builder variablesAndBlocksBuilder = CodeBlock.builder();
        for (CodeBlock variable : variables) {
            variablesAndBlocksBuilder.add(variable);
        }
        for (Block block : functionItem.blocks) {
            if (block.blocktype == BlockType.START) {
                variablesAndBlocksBuilder.add(OnBlock.accept(args, block));
            }
        }
        CodeBlock variablesAndBlocks = variablesAndBlocksBuilder.build();
        
        try {
            MethodSpec function = MethodSpec.methodBuilder(functionName)
                    .addJavadoc(getJavaDoc(args, functionItem))
                    .addJavadoc(parameterJavaDoc)
                    .addModifiers(modifiers)
                    .addParameters(parameters)
                    .returns(returnTypeName)
                    .addCode(variablesAndBlocks)
                    .build();
            return Optional.of(function);
        } catch (Exception e) {
            System.err.println("Could not create function " + e);
        }
        return Optional.empty();
    }
    
    /**
     * Returns the parameter of a function as a List of parameterspec
     *
     * @param functionItem The item which parameters should be converted
     * @return The list of parameters
     */
    static List<ParameterSpec> getParameters(Item functionItem) {
        List<ParameterSpec> parameters = new ArrayList<>();
        for (Item child : functionItem.children) {
            if (child == null) {
                System.err.println("Parameter itself is not specified: " + ItemUtils.getFullName(functionItem));
                return parameters;
            }
            if (child.item == null) {
                System.err.println("Could not determine type of item " + ItemUtils.getFullName(child));
                return parameters;
            }
            if (!ItemType.PARAMETER.equals(child.item)) {
                continue;
            }
            if (child.name == null) {
                System.err.println("Could not determine name for parameter " + ItemUtils.getFullName(child));
                return parameters;
            }
            if (child.type == null) {
                System.err.println("Could not determine type for parameter " + ItemUtils.getFullName(child));
                return parameters;
            }
            Optional<TypeName> optionalParameterTypeName = ItemUtils.getTypeName(child.type);
            if (!optionalParameterTypeName.isPresent()) {
                return parameters;
            }
            TypeName parameterTypeName = optionalParameterTypeName.get();
            String parameterName = child.name;
            
            ParameterSpec parameter = ParameterSpec.builder(parameterTypeName, parameterName)
                    .build();
            parameters.add(parameter);
        }
        return parameters;
    }
    
    private static String getParameterJavaDoc(CLIArgs args, Item item) {
        if (args.getIgnoreComments()) {
            return EMPTY;
        }
        StringBuilder builder = new StringBuilder();
        for (Item child : item.children) {
            if (child == null
                    || child.item == null
                    || child.name == null
                    || child.comments == null
                    || child.comments.isEmpty()) {
                continue;
            }
            if (!ItemType.PARAMETER.equals(child.item)) {
                continue;
            }
            builder.append("@param ")
                    .append(child.name)
                    .append(" ")
                    .append(getJavaDoc(args, child));
        }
        return builder.toString();
    }
    
    static List<CodeBlock> getVariables(CLIArgs args, Item item) {
        List<CodeBlock> variables = new ArrayList<>();
        for (Item child : item.children) {
            if (child == null) {
                System.err.println("Variable itself not specified");
                return variables;
            }
            if (child.item == null) {
                System.err.println("Type of this variable is not specified " + getFullName(child));
                return variables;
            }
            if (!ItemType.VARIABLE.equals(child.item)) {
                return variables;
            }
            if (child.name == null) {
                System.err.println("Name of variable not specified: " + ItemUtils.getFullName(child));
                return variables;
            }
            if (child.type == null) {
                System.err.println("Type of variable not specified: " + ItemUtils.getFullName(child));
                return variables;
            }
            
            String typeNameString = child.type;
            Optional<TypeName> optionalTypeName = ItemUtils.getTypeName(typeNameString);
            if (!optionalTypeName.isPresent()) {
                return variables;
            }
            TypeName typeName = optionalTypeName.get();
            String className = child.name;
            
            try {
                CodeBlock variable = CodeBlock.builder()
                        .add(getJavaDocFormatted(getJavaDoc(args, child)))
                        .addStatement("$T $L = $L", typeName, className, getFieldInitializer(typeName))
                        .build();
                variables.add(variable);
            } catch (Exception e) {
                System.err.println("Could not create variable codeblock " + e);
            }
        }
        return variables;
    }
}
