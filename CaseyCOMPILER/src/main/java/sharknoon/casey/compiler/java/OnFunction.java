package sharknoon.casey.compiler.java;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import sharknoon.casey.compiler.general.beans.CLIArgs;
import sharknoon.casey.compiler.general.beans.Item;
import sharknoon.casey.compiler.general.beans.Item.ItemType;

import javax.lang.model.element.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class OnFunction {
    
    
    public static void accept(CLIArgs args, Path currentPath, Item item) {
        Optional<MethodSpec> optionalFunction = ItemUtils.getFunction(args, item, true);
        if (!optionalFunction.isPresent()) {
            return;
        }
        String className = item.name;
        List<MethodSpec> methods = new ArrayList<>();
        MethodSpec function = optionalFunction.get();
        methods.add(function);
        boolean isMainMethod = false;
        MethodSpec mainMethod = null;
        if (ItemUtils.isMainMethod(args, item)) {
            isMainMethod = true;
            Map<String, String> parameters = args.getParameters();
            List<String> paramterValuesInRightOrder = item.children.stream()
                    .filter(i -> i.item == ItemType.PARAMETER)
                    .map(i -> {
                        String parameterName = i.name;
                        if (parameters.containsKey(parameterName)) {
                            return parameters.get(parameterName);
                        } else {
                            System.err.println("Function "
                                    + ItemUtils.getFullName(item)
                                    + " has wrong parameters actual: ("
                                    + parameters.keySet().stream().collect(Collectors.joining(", "))
                                    + ") expected: ("
                                    + item.children.stream()
                                    .filter(p -> p.item == ItemType.PARAMETER)
                                    .map(p -> p.name)
                                    .collect(Collectors.joining(", "))
                                    + ")"
                            );
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
        
            if (paramterValuesInRightOrder.contains(null)) {
                return;
            }
        
            mainMethod = MethodSpec.methodBuilder("main")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(String[].class, "args")
                    .addStatement(className + "." + className + "(" + paramterValuesInRightOrder.stream().collect(Collectors.joining(", ")) + ")")
                    .build();
            methods.add(mainMethod);
        }
        
        try {
            TypeSpec clazz = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethods(methods)
                    .build();
            JavaFile varFile = JavaFile.builder(ItemUtils.pathToClassPath(currentPath), clazz)
                    .build();
            varFile.writeTo(args.getBasePath());
        } catch (Exception e) {
            System.err.println("Could not create function " + ItemUtils.getFullName(item) + ": " + e);
        }
    }
    
}
