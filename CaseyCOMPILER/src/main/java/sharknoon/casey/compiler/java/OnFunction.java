package sharknoon.casey.compiler.java;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import sharknoon.casey.compiler.general.beans.CLIArgs;
import sharknoon.casey.compiler.general.beans.Item;

import javax.lang.model.element.Modifier;
import java.nio.file.Path;
import java.util.Optional;

public class OnFunction {
    
    
    public static void accept(CLIArgs args, Path currentPath, Item item) {
        Optional<MethodSpec> optionalFunction = ItemUtils.getFunction(args, item, true);
        if (!optionalFunction.isPresent()) {
            return;
        }
        String className = item.name;
        MethodSpec function = optionalFunction.get();
        
        try {
            TypeSpec clazz = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(function)
                    .build();
            JavaFile varFile = JavaFile.builder(ItemUtils.pathToClassPath(currentPath), clazz)
                    .build();
            varFile.writeTo(args.getBasePath());
        } catch (Exception e) {
            System.err.println("Could not create function " + ItemUtils.getFullName(item) + ": " + e);
        }
    }
    
}
