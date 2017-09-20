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
public class End extends Block {

    private static final double END_HIGHT = 100;
    private static final double END_WIDTH = 200;
    private static final Side[] SIDES = new Side[]{Side.TOP};

    public End(Consumer<MouseEvent> onMouseDragged, Consumer<Boolean> mouseOverShape) {
        super(onMouseDragged, mouseOverShape, createEndShapeSupplier(), SIDES);
    }

    private static Supplier<Shape> createEndShapeSupplier() {
        return () -> {
            Rectangle rectangle = new Rectangle(END_WIDTH, END_HIGHT);
            rectangle.setArcWidth(100);
            rectangle.setArcHeight(100);
            rectangle.setFill(Color.RED);
            return rectangle;
        };
    }

}
