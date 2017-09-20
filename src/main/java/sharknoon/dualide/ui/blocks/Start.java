package sharknoon.dualide.ui.blocks;

import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.geometry.Side;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 *
 * @author Josua Frank
 */
public class Start extends Block {

    private static final double START_HIGHT = 100;
    private static final double START_WIDTH = 200;
    private static final Side[] SIDES = new Side[]{Side.BOTTOM};

    public Start(Consumer<MouseEvent> onMouseDragged, Consumer<Boolean> mouseOverShape) {
        super(onMouseDragged, mouseOverShape, createStartShapeSupplier(), SIDES);
    }

    private static Supplier<Shape> createStartShapeSupplier() {
        return () -> {
            Rectangle rectangle = new Rectangle(START_WIDTH, START_HIGHT);
            rectangle.setArcWidth(100);
            rectangle.setArcHeight(100);
            rectangle.setFill(Color.GREEN);
            return rectangle;
        };
    }

}
