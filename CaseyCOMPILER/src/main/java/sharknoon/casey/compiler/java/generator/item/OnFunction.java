package sharknoon.casey.compiler.java.generator.item;

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
    
    
    public static boolean accept(CLIArgs args, Path currentPath, Item item) {
        Optional<MethodSpec> optionalFunction = ItemUtils.getFunction(args, item, true);
        if (!optionalFunction.isPresent()) {
            System.err.println("Could not create static function " + ItemUtils.getFullName(item));
            return false;
        }
        String className = item.name;
        List<MethodSpec> methods = new ArrayList<>();
        MethodSpec function = optionalFunction.get();
        methods.add(function);
        
        if (ItemUtils.isMainMethod(args, item)) {
            Map<String, String> parameters = args.getParameters();
            List<String> paramterValuesInRightOrder = item.children.stream()
                    .filter(i -> i.item == ItemType.PARAMETER)
                    .map(n -> {
                        if (parameters.containsKey(n.name)) {
                            if ("TEXT".equals(n.type)) {
                                return "\"" + parameters.get(n.name) + "\"";
                            }
                            return parameters.get(n.name);
                        } else {
                            System.err.println("Function "
                                    + ItemUtils.getFullName(item)
                                    + " has wrong parameters actual: ("
                                    + String.join(", ", parameters.keySet())
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
                return false;
            }
            
            MethodSpec mainMethod = MethodSpec.methodBuilder("main")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(String[].class, "args")
                    .addStatement(className + "." + className + "(" + String.join(", ", paramterValuesInRightOrder) + ")")
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
            return false;
        }
        return true;
    }
    
}
