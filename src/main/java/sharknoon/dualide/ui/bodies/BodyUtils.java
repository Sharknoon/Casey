package sharknoon.dualide.ui.bodies;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import sharknoon.dualide.logic.items.Class.ObjectType;
import sharknoon.dualide.logic.types.PrimitiveType.BooleanType;
import sharknoon.dualide.logic.types.PrimitiveType.NumberType;
import sharknoon.dualide.logic.types.PrimitiveType.TextType;
import sharknoon.dualide.logic.types.PrimitiveType.VoidType;
import sharknoon.dualide.logic.types.Type;
import sharknoon.dualide.logic.types.Type.UndefinedType;

public class BodyUtils {
    
    static final Text helper;
    static final double DEFAULT_WRAPPING_WIDTH;
    static final double DEFAULT_LINE_SPACING;
    static final String DEFAULT_TEXT;
    static final TextBoundsType DEFAULT_BOUNDS_TYPE;
    
    static {
        helper = new Text();
        DEFAULT_WRAPPING_WIDTH = helper.getWrappingWidth();
        DEFAULT_LINE_SPACING = helper.getLineSpacing();
        DEFAULT_TEXT = helper.getText();
        DEFAULT_BOUNDS_TYPE = helper.getBoundsType();
    }
    
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
    
    public static DoubleBinding calculateBodyHeightMultiplierForHorizontalPadding(ObservableValue<Type> parent, ObservableValue<Type> child) {
        return Bindings.createDoubleBinding(() -> {
            int parentWeight = getWeight(parent.getValue());
            int childWeight = getWeight(child.getValue());
            if (parentWeight < 0) {//child is root
                parentWeight = childWeight;
            }
            int difference = childWeight - parentWeight;
            difference = difference < 0 ? 0 : difference;
            return (double) difference * 0.1;//0,1 because max offset is 1/2 of the height -> 1/2 / 5 = 0.1, 5 because 5 different types
        }, parent, child);
    }
    
    public static DoubleBinding calculateDistance(ObservableValue<Type> parent, ObservableValue<Type> child, ObservableDoubleValue childheight) {
        DoubleBinding multiplier = calculateBodyHeightMultiplierForHorizontalPadding(parent, child);
        return DoubleExpression.doubleExpression(childheight).multiply(multiplier);
    }
    
    public static ReadOnlyDoubleProperty minTextFieldWidthProperty(TextField tf) {
        ReadOnlyDoubleWrapper minTextFieldWidthProperty = new ReadOnlyDoubleWrapper();
        tf.textProperty().addListener((ov, prevText, currText) -> {
            // Do this in a Platform.runLater because of Textfield has no padding at first time and so on
            Platform.runLater(() -> {
                Text text = new Text(currText);
                text.setFont(tf.getFont()); // Set the same font, so the size is the same
                double width = text.getLayoutBounds().getWidth() // This big is the Text in the TextField
                        + tf.getPadding().getLeft() + tf.getPadding().getRight() // Add the padding of the TextField
                        + 2d; // Add some spacing
                minTextFieldWidthProperty.set(width); // Set the width
                tf.positionCaret(tf.getCaretPosition()); // If you remove this line, it flashes a little bit
            });
        });
        return minTextFieldWidthProperty.getReadOnlyProperty();
    }
    
    
}
