package sharknoon.dualide.ui.bodies;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.binding.ObjectExpression;
import sharknoon.dualide.logic.items.Class.ObjectType;
import sharknoon.dualide.logic.types.PrimitiveType.BooleanType;
import sharknoon.dualide.logic.types.PrimitiveType.NumberType;
import sharknoon.dualide.logic.types.PrimitiveType.TextType;
import sharknoon.dualide.logic.types.PrimitiveType.VoidType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.logic.types.Type.UndefinedType;

public class BodyUtils {
    
    private static int getWeight(Type type) {
        if (type instanceof VoidType) {
            return 0;
        } else if (type instanceof BooleanType) {
            return 1;
        } else if (type instanceof NumberType) {
            return 2;
        } else if (type instanceof ObjectType) {
            return 3;
        } else if (type instanceof TextType) {
            return 4;
        } else if (type instanceof UndefinedType) {
            return 5;
        }
        return -1;
    }
    
    public static DoubleBinding calculateBodyHeightMultiplierForHorizontalPadding(ObjectExpression<Type> parent, ObjectExpression<Type> child) {
        return Bindings.createDoubleBinding(() -> {
            int parentWeight = getWeight(parent.get());
            int childWeight = getWeight(child.get());
            int difference = childWeight - parentWeight;
            return difference*0.1;//0,1 because max offset is 1/2 of the height -> 1/2 / 5 = 0.1, 5 because 5 different types
        }, parent, child);
    }
    
    public static DoubleBinding calculateDistance(ObjectExpression<Type> parent, ObjectExpression<Type> child, DoubleExpression childheight) {
        DoubleBinding multiplier = calculateBodyHeightMultiplierForHorizontalPadding(parent, child);
        return childheight.multiply(multiplier);
    }
    
}
