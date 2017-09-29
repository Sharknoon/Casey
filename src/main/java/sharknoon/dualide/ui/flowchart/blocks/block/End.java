package sharknoon.dualide.ui.flowchart.blocks.block;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.geometry.Side;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import sharknoon.dualide.ui.flowchart.BlockEventHandler;
import sharknoon.dualide.ui.flowchart.Flowchart;
import sharknoon.dualide.ui.flowchart.blocks.Block;

/**
 *
 * @author Josua Frank
 */
public class End extends Block {

    private static final double END_HIGHT = 100;
    private static final double END_WIDTH = 200;
    private static final Side[] SIDES = new Side[]{Side.TOP};

    public End(Flowchart flowchart) {
        super(flowchart, createEndShapeSupplier(), SIDES);
    }

    private static Supplier<Shape> createEndShapeSupplier() {
        return () -> {
            Rectangle rectangle = new Rectangle(END_WIDTH, END_HIGHT);
            rectangle.setArcWidth(END_HIGHT < END_WIDTH ? END_HIGHT : END_WIDTH);
            rectangle.setArcHeight(END_HIGHT < END_WIDTH ? END_HIGHT : END_WIDTH);
            rectangle.setFill(Color.RED);
            return rectangle;
        };
    }

}
