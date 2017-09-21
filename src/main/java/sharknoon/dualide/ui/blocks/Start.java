package sharknoon.dualide.ui.blocks;

import java.util.function.BiConsumer;
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

    public Start(BiConsumer<MouseEvent, Boolean> onMouseDragged, Consumer<Boolean> mouseOverShape) {
        super(onMouseDragged, mouseOverShape, createStartShapeSupplier(), SIDES);
    }

    private static Supplier<Shape> createStartShapeSupplier() {
        return () -> {
            Rectangle rectangle = new Rectangle(START_WIDTH, START_HIGHT);
            rectangle.setArcWidth(START_HIGHT < START_WIDTH ? START_HIGHT : START_WIDTH);
            rectangle.setArcHeight(START_HIGHT < START_WIDTH ? START_HIGHT : START_WIDTH);
            rectangle.setFill(Color.GREEN);
            return rectangle;
        };
    }

}
